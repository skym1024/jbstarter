package com.cclotus.cms.portal.controller

import com.cclotus.cms.common.domain.CmsPost
import com.cclotus.cms.common.domain.CmsTerm
import com.cclotus.cms.common.service.CmsPostService
import com.cclotus.cms.common.service.CmsTermService
import org.jeecg.common.api.vo.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cms")
class CmsWxController {

    @Autowired
    lateinit var cmsTermService: CmsTermService

    @Autowired
    lateinit var cmsPostService: CmsPostService

    /**
     * 查询菜单列表
     *
     * @param taxonomy 分类系统
     * @param paging    是否分页，默认分页
     * @param cmsTerm   过滤条件
     */
    @GetMapping("/{taxonomy:wxAppMenu|catalog|tag}/list")
    fun listMenu(@PathVariable("taxonomy") taxonomy: String,
                 @RequestParam(name = "pageNo", defaultValue = "1") pageNo: Int,
                 @RequestParam(name = "pageSize", defaultValue = "10") pageSize: Int,
                 cmsTerm: CmsTerm,
                 paging: Boolean
    ):  Result<*> {
        cmsTerm.taxonomy = taxonomy
        return Result.OK(cmsTermService.list(cmsTerm, pageNo, pageSize, paging))
    }

    /**
     * 根据分类别名或Id获取分类详情
     *
     * @param taxonomy 分类系统
     * @param term 分类标识
     * @return 分类详细信息
     */
    @GetMapping("/{taxonomy:wxAppMenu|catalog|tag}/{term}")
    fun detail(@PathVariable("taxonomy") taxonomy: String,
               @PathVariable("term") term: String): Result<*> {
        return Result.OK(cmsTermService.detail(taxonomy, term))
    }

    /**
     * 查询发布内容列表
     *
     * @param contentType 内容类型
     * @param termAlias 栏目别名
     * @param paging    是否分页，默认分页
     * @param deep      是否显示栏目下子栏目的文章
     * @param post      过滤条件
     */
    @GetMapping(path = ["/{contentType:article|page|video|picture}/{termAlias}/list", "/{contentType:article|page|video|picture}/list"])
    fun listPost(
        @PathVariable("contentType") contentType: String,
        @PathVariable(name = "termAlias", required = false) termAlias: String?,
        @RequestParam(name = "pageNo", defaultValue = "1") pageNo: Int,
        @RequestParam(name = "pageSize", defaultValue = "10") pageSize: Int,
        paging: Boolean?,
        deep: Boolean?,
        post: CmsPost,
        content: Boolean?
    ):  Result<*> {
        post.contentType = contentType
        return Result.OK(cmsPostService.list(termAlias, deep ?: false, post, pageNo, pageSize, paging, content ?: false))
    }

    /**
     * 获取文章详情信息
     *
     * @return
     */
    @GetMapping("/{contentType:article|page|video|picture}/{post}")
    fun detailPost(@PathVariable("contentType") contentType: String,
                   @PathVariable("post") post: String): Result<*> {
        return Result.OK(cmsPostService.detail(contentType, post))
    }
}
