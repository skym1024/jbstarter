package com.cclotus.cms.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import lombok.extern.slf4j.Slf4j
import org.jeecg.common.api.vo.Result
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Slf4j
@Api(tags = ["新建-园区"])
@RestController
@RequestMapping("/hello")
class HelloController {
    @ApiOperation(value = "hello", notes = "hello")
    @GetMapping(value = ["/"])
    fun hello(): Result<String> {
        val result = Result<String>()
        result.result = "hello skym!"
        result.isSuccess = true
        return result
    }
}