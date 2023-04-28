package com.cclotus.cms.common.vo

import com.cclotus.cms.common.domain.CmsTerm
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

/**
 * 文章信息
 */
class CmsPostFormRes : CmsPostFormBase() {

    var createBy: String? = null

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var createTime: LocalDateTime? = null

    var updateBy: String? = null

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var updateTime: LocalDateTime? = null

    var remark: String? = null

    var catalogs: List<CmsTerm> = mutableListOf()

    var meta: Map<String, String?> = mutableMapOf()
}
