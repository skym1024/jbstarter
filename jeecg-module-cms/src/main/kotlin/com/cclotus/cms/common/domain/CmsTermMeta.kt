package com.cclotus.cms.common.domain

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import javax.validation.constraints.NotNull

/**
 * 分类扩展属性表
 */
@TableName("cms_term_meta")
class CmsTermMeta {

    /**
     * 自增Id
     */
    @TableId(value = "meta_id", type = IdType.AUTO)
    var metaId: Int? = null

    /**
     * 分类Id
     */
    @TableField(value = "term_id")
    @NotNull(message = "分类Id不可为空")
    var termId: Int? = null

    /**
     * 扩展属性键
     */
    @TableField(value = "meta_key")
    @NotNull(message = "分类的扩展属性键不可空")
    var metaKey: String? = null

    /**
     * 扩展属性值
     */
    @TableField(value = "meta_value")
    var metaValue: String? = null
}
