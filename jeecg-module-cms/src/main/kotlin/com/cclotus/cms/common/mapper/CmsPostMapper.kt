package com.cclotus.cms.common.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.cclotus.cms.common.domain.CmsPost
import org.apache.ibatis.annotations.Param

interface CmsPostMapper : BaseMapper<CmsPost> {

    /**
     * 根据内容ID查询内容详情
     * @param contentType 内容类型
     * @param postId 内容ID
     */
    fun selectPostByPostId(@Param("contentType") contentType: String,
                           @Param("postId") postId: Int): CmsPost?

    /**
     * 根据页面别名查询页面内容
     *  @param contentType 内容类型
     * @param alias 内容别名
     */
    fun selectPostByAlias(@Param("contentType") contentType: String,
                          @Param("alias") alias: String): CmsPost

    /**
     * 根据页面类型、别名以及内容Id或别名查询页面内容
     */
    fun selectPostEx(@Param("catalogAlias") catalogAlias:String?,
                     @Param("postType") postType: String,
                     @Param("postId") postId: Int?,
                     @Param("postAlias") postAlias: String?): CmsPost
}
