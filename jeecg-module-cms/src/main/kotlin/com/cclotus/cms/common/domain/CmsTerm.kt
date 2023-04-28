package com.cclotus.cms.common.domain

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import javax.validation.constraints.NotBlank

/**
 * CMS分类用语
 */
@TableName("cms_term")
class CmsTerm {

    /**
     * 分类用语ID
     */
    @TableId(value = "term_id", type = IdType.AUTO)
    var termId: Int? = null

    /**
     * 上级Id
     */
    @TableField("parent_id")
    var parentId: Int? = null

    /**
     * 祖级列表
     */
    @TableField("ancestors")
    var ancestors: String? = null

    /**
     * 分类名称
     */
    @TableField("name")
    @NotBlank(message = "分类名称不可为空")
    var name: String? = null

    /**
     * 分类别名
     */
    @TableField("alias")
    @NotBlank(message = "分类别名不可为空")
    var alias: String? = null

    /**
     * 简要描述
     */
    @TableField("description")
    var description: String? = null

    /**
     * 分类图标
     */
    @TableField("image")
    var image: String? = null

    /**
     * 分类系统
     */
    @TableField("taxonomy")
    @NotBlank(message = "分类系统不可为空")
    var taxonomy: String? = null

    /**
     * 分类关联的数据类型
     */
    @TableField("type")
    @NotBlank(message = "分类关联的数据类型不可为空")
    var type: String? = null

    /**
     * 分类序号
     */
    @TableField("number")
    var number: Int? = null

    /**
     * 扩展属性列表
     */
    @TableField(exist = false)
    var meta: List<CmsTermMeta> = mutableListOf()
}