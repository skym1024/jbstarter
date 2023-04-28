package com.cclotus.cms.common.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.cclotus.cms.common.domain.CmsTerm
import com.cclotus.cms.common.domain.CmsTermMeta
import com.cclotus.cms.common.domain.CmsTermRelationships
import com.cclotus.cms.common.mapper.CmsTermMapper
import com.cclotus.cms.common.mapper.CmsTermMetaMapper
import com.cclotus.cms.common.mapper.CmsTermRelationshipsMapper
import com.cclotus.cms.common.util.EntityPathVariable
import com.cclotus.cms.common.vo.CmsTermForm
import org.jeecg.common.exception.ServiceException
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CmsTermService : ServiceImpl<CmsTermMapper, CmsTerm>(), ICmsTermService {

    @Autowired
    lateinit var cmsTermMapper: CmsTermMapper

    @Autowired
    lateinit var cmsTermRelationshipsMapper: CmsTermRelationshipsMapper

    @Autowired
    lateinit var cmsTermMetaMapper: CmsTermMetaMapper

    @Autowired
    lateinit var cmsTermMetaService: CmsTermMetaService

    /**
     * 获取分类列表
     */
    override fun list(term: CmsTerm, pageNo: Int, pageSize: Int, paging: Boolean?): IPage<CmsTermForm> {
        // 查询term主表，并处理分页
        val queryWrapper = QueryWrapper<CmsTerm>()
            .eq(!term.taxonomy.isNullOrBlank(), "taxonomy", term.taxonomy)
            .eq(!term.type.isNullOrBlank(), "type", term.type)
            .like(!term.name.isNullOrBlank(), "name", term.name)
            .eq(!term.alias.isNullOrEmpty(), "alias", term.alias)
            .eq(term.parentId != null, "parent_id", term.parentId)
            .orderByAsc("number")

        val newPageSize = if (paging == false) -1L  else pageSize

        val results = page(Page(pageNo.toLong(), newPageSize.toLong()), queryWrapper)
            ?: return Page<CmsTermForm>(0, 0, 0).apply { records = null }

        val terms = results.records

        // 查询meta从表，并按termId分组
        val termIds = terms.map { it.termId!! }.distinct().toList()
        val metas = cmsTermMetaService.selectListByTermIds(termIds).groupBy { it.termId!! }

        // 将meta从表数据关联到post主表
        terms.forEach { it.meta = metas[it.termId] ?: emptyList() }

        // 构建分页查询结果，转换post并替换rows
        return Page<CmsTermForm>(results.current, results.size, results.total).apply {
            this.records = terms.map { it.toCmsTermForm() }
        }
    }

    private fun CmsTerm.toCmsTermForm(): CmsTermForm {
        return CmsTermForm().apply {
            BeanUtils.copyProperties(this@toCmsTermForm, this)
            this.meta = this@toCmsTermForm.meta.associate { it.metaKey!! to it.metaValue!! }
        }
    }

    /**
     * 根据分类别名或Id获取分类详情
     */
    override fun detail(taxonomy: String, term: String): CmsTermForm {
        val (id, alias) = EntityPathVariable.parse(term)
        return cmsTermMapper.selectOneWithMetas(taxonomy, id, alias)?.toCmsTermForm()
                ?: throw ServiceException("分类不存在", 500)
    }

    /**
     * 添加分类
     * @param term 分类信息
     * @return 影响数据条数
     */
    @Transactional
    override fun create(term: CmsTermForm): Int {
        // 如果未设置 parentId，使用默认值 0 表示顶层栏目
        term.parentId = term.parentId ?: 0

        // 检查分类别名是否冲突
        val queryWrapper = QueryWrapper<CmsTerm>().eq("alias", term.alias!!)
        if (cmsTermMapper.selectCount(queryWrapper) > 0)
            throw ServiceException("分类别名已存在", 500)

        // 对于子分类
        if (term.parentId != 0) {
            // 检查父级分类是否存在
            val parentTerm = cmsTermMapper.selectById(term.parentId)
                ?: throw ServiceException("父级分类不存在", 500)

            // 默认子分类自动继承父分类类别
            term.type = term.type ?: parentTerm.type

            // 设置子分类的组级列表
            term.ancestors = parentTerm.ancestors + "," + term.parentId
        }else{
            term.ancestors = "0"
        }

        // 插入分类
        val entity = CmsTerm().apply {
            BeanUtils.copyProperties(term, this)
            this.number = this.number ?: 0
        }.also {
            if (cmsTermMapper.insert(it) != 1) {
                throw ServiceException("插入分类失败", 500)
            }
        }

        // 插入分类与对象的关联关系
        term.objectIds?.forEach { objectId ->
            CmsTermRelationships().apply {
                this.termId = entity.termId
                this.objectId = objectId
            }.also {
                if (cmsTermRelationshipsMapper.insert(it) != 1) {
                    throw ServiceException("插入分类与对象的关联关系失败", 500)
                }
            }
        }

        // 插入发布内容的扩展属性键值列表
        term.meta?.forEach { meta ->
            CmsTermMeta().apply {
                this.termId = entity.termId
                this.metaKey = meta.key
                this.metaValue = meta.value
            }.also {
                if (cmsTermMetaMapper.insert(it) != 1) {
                    throw ServiceException("插入分类的扩展属性列表失败", 500)
                }
            }
        }

        return 1
    }

    /**
     * 更新分类
     * @param term 分类信息
     * @return 影响数据条数
     */
    @Transactional
    override fun update(term: CmsTermForm): Int {
        val termId = requireNotNull(term.termId) { "分类Id不可为空" }

        /* 获取待更新分类 */
        val entity = cmsTermMapper.selectById(termId) ?: throw ServiceException("分类不存在", 500)

        /* 检查别名是否已冲突 */
        if (!term.alias.isNullOrBlank() && !entity.alias.equals(term.alias)) {
            val queryWrapper = QueryWrapper<CmsTerm>()
                .eq("alias", term.alias)
            if (cmsTermMapper.selectCount(queryWrapper) > 0) {
                throw ServiceException("分类别名已存在", 500)
            }
        }

        // 更新分类的扩展属性键值列表
        term.meta?.let {
            updateMetas(termId, it)
        }

        /**
         *   支持修改分类名称、别名、图标、描述、排序
         *   不支持修改分类系统、父级分类、分类的数据类型
         */
        return entity.apply {
            BeanUtils.copyProperties(term,
                entity,
                "term_id",
                "parent_id",
                "ancestors",
                "taxonomy",
                "type")
        }.let {
            cmsTermMapper.updateById(it)
        }
    }

    /**
     * 根据IDs删除分类
     */
    @Transactional
    override fun deleteByIds(termIds: Array<Int>): Int {
        if (termIds.isEmpty()) return 1

        QueryWrapper<CmsTerm>().`in`("parent_id", termIds.toList()).let {
            if(cmsTermMapper.exists(it))
                throw ServiceException("不能删除分类：分类下有子分类", 500)
        }

        QueryWrapper<CmsTermRelationships>().`in`("term_id", termIds.toList()).let {
            if(cmsTermRelationshipsMapper.exists(it))
                throw ServiceException("不能删除分类：分类下有关联的发布内容", 500)
        }

        // 删除分类的扩展属性
        QueryWrapper<CmsTermMeta>().`in`("term_id", termIds.toList()).let {
            cmsTermMetaMapper.delete(it)
        }

        return cmsTermMapper.deleteBatchIds(termIds.toList())
    }

    /**
     * 更新发布内容的扩展属性键值列表
     */
    private fun updateMetas(termId: Int, metas: Map<String, String?>) {
        // 删除旧的属性
        cmsTermMetaMapper.delete(QueryWrapper<CmsTermMeta>().eq("term_id", termId))

        // 插入新的属性
        metas.forEach { meta ->
            CmsTermMeta().apply {
                this.termId = termId
                this.metaKey = meta.key
                this.metaValue = meta.value
            }.also {
                if (cmsTermMetaMapper.insert(it) != 1) {
                    throw ServiceException("插入分类的扩展属性列表失败", 500)
                }
            }
        }
    }

    /**
     * 根据别名获取分类列表
     * @param alias 分类别名
     * @param deep 是否查询子分类
     */
    override fun getTermsByAlias(alias: String, deep: Boolean): List<CmsTerm> {
        val terms = mutableListOf<CmsTerm>()

        val term = cmsTermMapper.selectOne(QueryWrapper<CmsTerm>().eq("alias", alias))
            ?: throw ServiceException("分类别名不存在", 500)

        terms.add(term)

        if (deep) {
            terms.addAll(cmsTermMapper.selectChildrenById(term.termId!!))
        }

        return terms
    }

    /**
     * 根据别名获取与分类相关的对象Ids列表
     * @param alias 分类别名
     * @param deep 是否查询子分类
     */
    override fun getObjectIdsByTermAlias(alias: String, deep: Boolean): List<Int> {
        val termIds = mutableListOf<Int>()

        val term = cmsTermMapper.selectOne(QueryWrapper<CmsTerm>().eq("alias", alias))
            ?: throw ServiceException("分类别名不存在", 500)

        termIds.add(term.termId!!)

        if (deep) {
            termIds.addAll(cmsTermMapper.selectChildrenById(term.termId!!).mapNotNull { it.termId })
        }

        return cmsTermRelationshipsMapper.selectList(
            QueryWrapper<CmsTermRelationships>()
                .`in`("term_id", termIds)
                .groupBy("object_id")
                .having("count(*) >= ${termIds.size}")
                .select("object_id")
        ).mapNotNull { it.objectId }.distinct().toList()
    }
}
