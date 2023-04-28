package com.cclotus.cms.admin.controller

import com.cclotus.cms.common.domain.CmsPost
import com.cclotus.cms.common.service.CmsPostService
import com.cclotus.cms.common.service.ICmsPostService
import com.cclotus.cms.common.vo.CmsPostFormReq
import org.apache.shiro.SecurityUtils
import org.jeecg.common.api.vo.Result
import org.jeecg.common.aspect.annotation.AutoLog
import org.jeecg.common.system.vo.LoginUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * CmsPostController
 *
 * @author ruoyi
 * @date 2023-03-07
 */
@RestController
@RequestMapping("/cms")
class CmsPostController {
    @Autowired
    lateinit var cmsPostService: ICmsPostService

    /**
     * 查询发布内容列表
     *
     * @param contentType  内容数据类型
     * @param termAlias 分类别名
     * @param paging    是否分页，默认分页
     * @param deep      是否显示栏目下子栏目的文章
     * @param post      过滤条件
     */
    @GetMapping(path = ["/{contentType:article|page|video|picture}/{termAlias}/list", "/{contentType:article|page|video|picture}/list"])
    fun list(
        @PathVariable("contentType") contentType: String?,
        @PathVariable(name = "termAlias", required = false) termAlias: String?,
        @RequestParam(name = "pageNo", defaultValue = "1") pageNo: Int,
        @RequestParam(name = "pageSize", defaultValue = "10") pageSize: Int,
        paging: Boolean?,
        deep: Boolean?,
        post: CmsPost,
        content: Boolean?
    ):  Result<*> {
        var deep = deep
        var content = content
        if (deep == null) deep = false
        if (content == null) content = false
        post.contentType = contentType
        return Result.OK(cmsPostService.list(termAlias, deep, post, pageNo, pageSize, paging, content))
    }

    /**
     * 根据页面别名获取页面详情
     *
     * @param contentType 页面别名
     * @return 页面详情信息
     */
    @GetMapping("/{contentType:article|page|video|picture}/{post}")
    fun detail(
        @PathVariable("contentType") contentType: String,
        @PathVariable("post") post: String
    ): Result<*> {
        return Result.OK(cmsPostService.detail(contentType, post))
    }

    /**
     * 新增发布内容
     */
    @AutoLog(value = "发布内容管理", operateType = 2)
    @PostMapping("/{contentType:article|page|video|picture}")
    fun add(
        @PathVariable("contentType") contentType: String,
        @Validated @RequestBody cmsPostFormReq: CmsPostFormReq
    ): Result<*>  {
        //获取登录用户信息
        val sysUser = SecurityUtils.getSubject().principal as LoginUser

        cmsPostFormReq.contentType = contentType
        cmsPostService.create(cmsPostFormReq, sysUser.username)

        return Result.ok<Any>("添加成功!")
    }

    /**
     * 修改发布内容
     */
    @AutoLog(value = "发布内容管理", operateType = 3)
    @PutMapping("/{contentType:article|page|video|picture}")
    fun edit(
        @PathVariable("contentType") contentType: String,
        @Validated @RequestBody cmsPostFormReq: CmsPostFormReq?
    ): Result<*> {
        val sysUser = SecurityUtils.getSubject().principal as LoginUser
        cmsPostService.update(cmsPostFormReq !!, sysUser.username)
        return Result.ok<Any>("修改成功!")
    }

    /**
     * 删除发布内容
     *
     * @param contentType    内容类型
     * @param postIds 内容ID列表
     */
    @AutoLog(value = "发布内容管理", operateType = 4)
    @DeleteMapping("/{contentType:article|page|video|picture}/{postIds}")
    fun remove(
        @PathVariable("contentType") contentType: String,
        @PathVariable("postIds") postIds: Array<Int>
    ): Result<*> {
        cmsPostService.deleteByIds(postIds)
        return Result.ok<Any>("删除成功!");
    }
}
