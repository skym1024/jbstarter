package com.cclotus.cms.common.vo

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * 文件信息
 *
 */
class FileInfo {

    /**
     * 当前路径
     */
    var path: String? = null

    /**
     * 文件名
     */
    var name: String? = null

    /**
     * 是否为目录
     */
    @JsonProperty("isDirectory")
    var isDirectory: Boolean? = null

    /**
     * 文件url
     */
    var url: String? = null

    /**
     * 文件大小，单位KB
     */
    var size: Double? = null

    /**
     * 文件夹是否为空
     */
    @JsonProperty("isEmpty")
    var isEmpty: Boolean? = null

    /**
     * 文件最后修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var updateTime: LocalDateTime? = null
}
