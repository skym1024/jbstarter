package com.cclotus.cms.common.service

import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.service.IService
import com.cclotus.cms.common.domain.CmsPost
import com.cclotus.cms.common.vo.CmsPostFormReq
import com.cclotus.cms.common.vo.CmsPostFormRes

public interface ICmsPostService : IService<CmsPost> {
    /**
     * 查询内容列表
     * @param termAlias 分类别名
     * @param deep      是否显示栏目下子栏目的文章，默认不查询子栏目下的文章
     * @param post      过滤条件
     * @param pageParam 分页条件
     */
    fun list(
        termAlias: String?,
        deep: Boolean,
        post: CmsPost,
        pageNo: Int,
        pageSize: Int,
        paging: Boolean?,
        content: Boolean,
    ): IPage<CmsPostFormRes>


    /**
     * 根据文章别名或Id获取文章详情
     */
    fun detail(contentType: String, post: String): CmsPostFormRes

    /**
     * 根据文章ID获取文章详情
     */
    fun getPostInfoEx(
        catalogAlias: String?,
        postType: String,
        postId: Int?,
        postAlias: String?,
    ): CmsPostFormRes

    /**
     * 新增发布内容
     * @param post 发布内容
     * @param poster 发布者
     * @return 影响数据条数
     */
    fun create(post: CmsPostFormReq, poster: String): Int

    /**
     * 更新发布内容
     * @param post 待修改发布内容
     * @return 影响数据条数
     */
    fun update(post: CmsPostFormReq, poster: String): Int

    /**
     * 根据IDs删除发布内容
     */
    fun deleteByIds(postIds: Array<Int>): Int
}
