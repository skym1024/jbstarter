package com.cclotus.cms.common.vo

/**
 * 文章信息
 */
class CmsPostFormReq : CmsPostFormBase() {

    var catalogIds: List<Int>? = null

    var meta: Map<String, String>? = null
}
