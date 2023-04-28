package com.cclotus.cms.common.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.cclotus.cms.common.domain.CmsPost
import com.cclotus.cms.common.domain.CmsPostMeta
import com.cclotus.cms.common.domain.CmsTermRelationships
import com.cclotus.cms.common.mapper.CmsPostMapper
import com.cclotus.cms.common.mapper.CmsPostMetaMapper
import com.cclotus.cms.common.mapper.CmsTermRelationshipsMapper
import com.cclotus.cms.common.util.EntityPathVariable
import com.cclotus.cms.common.vo.CmsPostFormReq
import com.cclotus.cms.common.vo.CmsPostFormRes
import com.cclotus.cms.common.vo.CmsTermForm
import org.jeecg.common.exception.ServiceException
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CmsPostService : ServiceImpl<CmsPostMapper, CmsPost>(), ICmsPostService {

    @Autowired
    lateinit var cmsTermService: CmsTermService

    @Autowired
    lateinit var cmsPostMapper: CmsPostMapper

    @Autowired
    lateinit var cmsTermRelationshipsMapper: CmsTermRelationshipsMapper

    @Autowired
    lateinit var cmsPostMetaMapper: CmsPostMetaMapper

    @Autowired
    lateinit var cmsPostMetaService: CmsPostMetaService

    /**
     * 查询内容列表
     * @param termAlias 分类别名
     * @param deep      是否显示栏目下子栏目的文章，默认不查询子栏目下的文章
     * @param post      过滤条件
     * @param pageParam 分页条件
     */
    override fun list(
        termAlias: String?,
        deep: Boolean,
        post: CmsPost,
        pageNo: Int,
        pageSize: Int,
        paging: Boolean?,
        content: Boolean,
    ): IPage<CmsPostFormRes> {

        // 如果存在termAlias过滤条件，先查询与该termAlias关联的objectIds
        val objectIds = termAlias?.takeIf { it.isNotBlank() }?.let {
            cmsTermService.getObjectIdsByTermAlias(it, deep).also { ids ->
                if (ids.isEmpty()) {
                    return Page<CmsPostFormRes>(0, 0, 0).apply { records = null }
                }
            }
        }

        // 查询post主表，并处理分页
        val queryWrapper = QueryWrapper<CmsPost>()
        if (!content) {
            // content为false时，不返回content字段，避免返回的数据量过大
            queryWrapper.select(CmsPost::class.java) { info -> !info.column.equals("content") }
        }

        if (!objectIds.isNullOrEmpty()) {
            // 设置termAlias过滤条件时，只返回与alias关联的objects
            queryWrapper.`in`("post_id", objectIds)
        }

        queryWrapper.eq(!post.contentType.isNullOrBlank(), "content_type", post.contentType)
            .like(!post.title.isNullOrBlank(), "title", post.title)
            .like(!post.summary.isNullOrBlank(), "summary", post.summary)
            .eq(!post.status.isNullOrBlank(), "status", post.status)
            .eq(post.topStatus != null, "top_status", post.topStatus)
            .eq(!post.visible.isNullOrBlank(), "visible", post.visible)
            .orderByDesc("top_status", "create_time")

        val newPageSize = if (paging == false) -1L  else pageSize
        val results = page(Page(pageNo.toLong(), newPageSize.toLong()), queryWrapper)
            ?: return Page<CmsPostFormRes>(0, 0, 0).apply { records = null }

        val posts = results.records

        // 查询meta从表，并按postId分组
        val postIds = posts.map { it.postId!! }.distinct()
        val metas = cmsPostMetaService.selectListByPostIds(postIds).groupBy { it.postId!! }

        // 将meta从表数据关联到post主表
        posts.forEach { it.meta = metas[it.postId] ?: emptyList() }

        // 构建分页查询结果，转换post并替换rows
        return Page<CmsPostFormRes>(results.current, results.size, results.total).apply {
            this.records = posts.map { it.toCmsPostFormRes() }
        }
    }

    fun CmsPost.toCmsPostFormRes(): CmsPostFormRes {
        return CmsPostFormRes().apply {
            BeanUtils.copyProperties(this@toCmsPostFormRes, this)
            this.meta = this@toCmsPostFormRes.meta.associate { it.metaKey!! to it.metaValue }
        }
    }

    /**
     * 根据文章别名或Id获取文章详情
     */
    override fun detail(contentType: String, post: String): CmsPostFormRes {
        val cmsPost = EntityPathVariable.parse(post).let {
            if (it.id != null) {
                cmsPostMapper.selectPostByPostId(contentType, it.id) ?: throw ServiceException(
                    "指定ID文章信息不存在",
                    500
                )
            } else if (it.alias != null) {
                cmsPostMapper.selectPostByAlias(contentType, it.alias)
                    ?: throw ServiceException("指定别名的文章信息不存在", 500)
            } else {
                throw ServiceException("指定的文章信息不存在", 500)
            }
        }

        return cmsPost.toCmsPostFormRes()
    }

    /**
     * 根据文章ID获取文章详情
     */
    override fun getPostInfoEx(
        catalogAlias: String?,
        postType: String,
        postId: Int?,
        postAlias: String?,
    ): CmsPostFormRes {
        if (postId == null && postAlias == null) {
            throw ServiceException("文章ID和别名不可同时为空", 500)
        }

        val cmsPost = cmsPostMapper.selectPostEx(catalogAlias, postType, postId, postAlias)
            ?: throw ServiceException("指定的文章信息不存在", 500)

        return cmsPost.toCmsPostFormRes()
    }

    /**
     * 新增发布内容
     * @param post 发布内容
     * @param poster 发布者
     * @return 影响数据条数
     */
    @Transactional
    override fun create(post: CmsPostFormReq, poster: String): Int {

        post.alias?.let {
            if (it.isNotBlank()) {
                val queryWrapper = QueryWrapper<CmsPost>()
                    .eq("alias", it)
                if (cmsPostMapper.selectCount(queryWrapper) > 0)
                    throw ServiceException("页面别名已存在", 500)
            }
        }

        // 插入发布内容
        val cmsPost = CmsPost().apply {
            BeanUtils.copyProperties(post, this)
            this.contentType = this.contentType ?: "article"
            this.status = this.status ?: "0"
            this.topStatus = this.topStatus ?: 0
            this.visible = this.visible ?: "1"
            this.createBy = poster
        }.also {
            if (cmsPostMapper.insert(it) != 1) {
                throw ServiceException("插入发布内容失败", 500)
            }
        }

        // 插入发布内容与栏目的关联关系
        post.catalogIds?.forEach { catalogId ->
            CmsTermRelationships().apply {
                this.objectId = cmsPost.postId
                this.termId = catalogId
            }.also {
                if (cmsTermRelationshipsMapper.insert(it) != 1) {
                    throw ServiceException("插入发布内容与栏目的关联关系失败", 500)
                }
            }
        }

        // 插入发布内容的扩展属性键值列表
        post.meta?.forEach { meta ->
            CmsPostMeta().apply {
                this.postId = cmsPost.postId
                this.metaKey = meta.key
                this.metaValue = meta.value
            }.also {
                if (cmsPostMetaMapper.insert(it) != 1) {
                    throw ServiceException("插入发布内容的扩展属性列表失败", 500)
                }
            }
        }

        return 1
    }

    /**
     * 更新发布内容
     * @param post 待修改发布内容
     * @return 影响数据条数
     */
    @Transactional
    override fun update(post: CmsPostFormReq, poster: String): Int {
        val postId = requireNotNull(post.postId) { "内容Id不可为空" }

        // 获取待更新发布内容
        val entity = cmsPostMapper.selectById(postId) ?: throw ServiceException("文章不存在", 500)

        post.alias?.let {
            if (it.isNotBlank()) {
                val queryWrapper = QueryWrapper<CmsPost>()
                    .eq("alias", it)
                    .ne("post_id", entity.postId)
                if (cmsPostMapper.selectCount(queryWrapper) > 0)
                    throw ServiceException("页面别名已存在", 500)
            }
        }

        // 更新发布内容与栏目类别的关联关系
        post.catalogIds?.let {
            updatePostCatalogsRelations(postId, it)
        }

        // 更新发布内容的扩展属性键值列表
        post.meta?.let {
            updatePostMetas(postId, it)
        }

        // 更新发布内容
        entity.apply {
            BeanUtils.copyProperties(post, this)
            this.updateBy = poster
        }.also {
            cmsPostMapper.updateById(it)
        }

        return 1
    }

    /**
     * 根据IDs删除发布内容
     */
    @Transactional
    override fun deleteByIds(postIds: Array<Int>): Int {
        if (postIds.isEmpty())
            return 0

        cmsPostMetaMapper.delete(QueryWrapper<CmsPostMeta>().`in`("post_id", postIds.toList()))

        cmsTermRelationshipsMapper.deleteObjectIds(postIds.toList())

        return cmsPostMapper.deleteBatchIds(postIds.toList())
    }

    /**
     * 更新发布内容的扩展属性键值列表
     */
    private fun updatePostMetas(postId: Int, metas: Map<String, String>) {
        // 删除旧的属性
        cmsPostMetaMapper.delete(QueryWrapper<CmsPostMeta>().eq("post_id", postId))

        // 插入新的属性
        metas.forEach { meta ->
            CmsPostMeta().apply {
                this.postId = postId
                this.metaKey = meta.key
                this.metaValue = meta.value
            }.also {
                if (cmsPostMetaMapper.insert(it) != 1) {
                    throw ServiceException("插入发布内容的扩展属性列表失败", 500)
                }
            }
        }
    }

    /**
     * 更新发布内容与栏目类别的关联关系
     */
    private fun updatePostCatalogsRelations(postId: Int, catalogIds: List<Int>) {

        // 获取关联的栏目类型
        val rela = cmsTermRelationshipsMapper.selectCatalogRelationsByPostId(postId)
        val relaCatalogIds = rela.map { r -> r.termId }.toList()

        // 计算待删除栏目类别列表，和待插入栏目类别列表
        val removeIds = relaCatalogIds.minus(catalogIds.toSet())
        val addIds = catalogIds.minus(relaCatalogIds.toSet())

        // 删除旧的关联关系
        rela.forEach {
            if (removeIds.contains(it.termId)) {
                cmsTermRelationshipsMapper.deleteById(it.relaId)
            }
        }

        // 插入新的关联关系
        //  TODO: 需要检查新增的栏目类别是否合法
        catalogIds.forEach {
            if (addIds.contains(it)) {
                CmsTermRelationships().apply {
                    this.objectId = postId
                    this.termId = it
                }.also { r ->
                    cmsTermRelationshipsMapper.insert(r)
                }
            }
        }
    }
}
