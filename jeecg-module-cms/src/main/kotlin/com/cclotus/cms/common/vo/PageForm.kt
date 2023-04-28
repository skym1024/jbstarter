package com.cclotus.cms.common.vo

import javax.validation.constraints.NotBlank


class PageForm {

    /**
     * 页面标题
     */
    @NotBlank(message = "页面标题不可为空")
    var pageTitle: String? = null

    /**
     * 页面内容
     */
    var content: String? = null
}
