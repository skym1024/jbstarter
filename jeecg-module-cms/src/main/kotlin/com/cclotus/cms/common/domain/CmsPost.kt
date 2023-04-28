package com.cclotus.cms.common.domain

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/**
 * 内容数据
 */
@TableName("cms_post")
class CmsPost : CMSBaseEntity() {

    /**
     * 内容ID
     */
    @TableId(value = "post_id", type = IdType.AUTO)
    var postId: Int? = null

    /**
     * 内容标题
     */
    @TableField("title")
    var title: String? = null

    /**
     * 内容首图
     */
    @TableField("image")
    var image: String? = null

    /**
     * 内容摘要
     */
    @TableField("summary")
    var summary: String? = null

    /**
     * 内容正文类型
     */
    @TableField("content_type")
    var contentType: String? = null

    /**
     * 内容正文
     */
    @TableField("content")
    var content: String? = null

    /**
     * 发布状态(0:草稿 1: 发布)
     */
    @TableField("status")
    var status: String? = null

    /**
     * 置顶状态(0:否 非0: 是)
     */
    @TableField("top_status")
    var topStatus: Int? = null

    /**
     * 显示隐藏(0: 隐藏 1: 显示)
     */
    @TableField("visible")
    var visible: String? = null

    /**
     * 页面别名（只有页面类型内容此属性才生效）
     */
    @TableField("alias")
    var alias: String? = null

    /**
     * 文章所属分类
     */
    @TableField(exist = false)
    var catalogs: List<CmsTerm> = mutableListOf()

    /**
     * 文章扩展属性列表
     */
    @TableField(exist = false)
    var meta: List<CmsPostMeta> = mutableListOf()
}
