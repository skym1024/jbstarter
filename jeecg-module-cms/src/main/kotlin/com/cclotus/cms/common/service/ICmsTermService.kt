package com.cclotus.cms.common.service

import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.service.IService
import com.cclotus.cms.common.domain.CmsTerm
import com.cclotus.cms.common.vo.CmsTermForm
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

public interface ICmsTermService : IService<CmsTerm> {

    /**
     * 获取分类列表
     */
    fun list(term: CmsTerm, pageNo: Int, pageSize: Int, paging: Boolean?): IPage<CmsTermForm>

    /**
     * 根据分类别名或Id获取分类详情
     */
    fun detail(taxonomy: String, term: String): CmsTermForm

    /**
     * 添加分类
     * @param term 分类信息
     * @return 影响数据条数
     */
    @Transactional
    fun create(term: CmsTermForm): Int

    /**
     * 更新分类
     * @param term 分类信息
     * @return 影响数据条数
     */
    @Transactional
    fun update(term: CmsTermForm): Int

    /**
     * 根据IDs删除分类
     */
    @Transactional
    fun deleteByIds(termIds: Array<Int>): Int

    /**
     * 根据别名获取分类列表
     * @param alias 分类别名
     * @param deep 是否查询子分类
     */
    fun getTermsByAlias(alias: String, deep: Boolean): List<CmsTerm>

    /**
     * 根据别名获取与分类相关的对象Ids列表
     * @param alias 分类别名
     * @param deep 是否查询子分类
     */
    fun getObjectIdsByTermAlias(alias: String, deep: Boolean): List<Int>
}
