package com.cclotus.cms.common.domain

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/**
 * 内容与栏目关联关系
 */
@TableName("cms_term_relationships")
class CmsTermRelationships {

    /**
     * 自增Id
     */
    @TableId(value = "rela_id", type = IdType.AUTO)
    var relaId: Int? = null

    /**
     * 栏目Id
     */
    @TableField(value = "term_id")
    var termId: Int? = null

    /**
     * 关联对象Id (关联对象：term, post, link)
     */
    @TableField("object_id")
    var objectId: Int? = null
}
