package com.cclotus.cms.common.domain

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.TableField
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

abstract class CMSBaseEntity {

    /**
     * 创建人
     */
    @TableField("create_by")
    var createBy: String? = null

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    /**
     * 修改人
     */
    @TableField("update_by")
    var updateBy: String? = null

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    var updateTime: LocalDateTime? = null

    /**
     * 备注
     */
    @TableField("remark")
    var remark: String? = null

    /**
     * 请求参数
     */
    @TableField(exist = false)
    var params: MutableMap<String, Any> = mutableMapOf()
}
