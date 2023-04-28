package com.cclotus.cms.common.domain

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import javax.validation.constraints.NotNull

/**
 * 内容与扩展属性关联关系
 */
@TableName("cms_post_meta")
class CmsPostMeta {

    /**
     * 自增Id
     */
    @TableId(value = "meta_id", type = IdType.AUTO)
    var metaId: Int? = null

    /**
     * 内容Id
     */
    @TableField(value = "post_id")
    @NotNull(message = "内容Id不可为空")
    var postId: Int? = null

    /**
     * 扩展属性键
     */
    @TableField(value = "meta_key")
    @NotNull(message = "内容的扩展属性键不可空")
    var metaKey: String? = null

    /**
     * 扩展属性值
     */
    @TableField(value = "meta_value")
    var metaValue: String? = null
}
