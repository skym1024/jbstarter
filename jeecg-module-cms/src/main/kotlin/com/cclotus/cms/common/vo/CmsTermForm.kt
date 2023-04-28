package com.cclotus.cms.common.vo

/**
 * CMS分类
 */
open class CmsTermForm {

    var termId: Int? = null

    var parentId: Int? = null

    var ancestors: String? = null

    var name: String? = null

    var alias: String? = null

    var description: String? = null

    var image: String? = null

    var taxonomy: String? = null

    var type: String? = null

    var number: Int? = null

    var objectIds: List<Int>? = null

    var meta: Map<String, String?>? = mutableMapOf()
}
