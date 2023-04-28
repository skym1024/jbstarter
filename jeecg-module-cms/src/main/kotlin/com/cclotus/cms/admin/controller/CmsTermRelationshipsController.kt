package com.cclotus.cms.admin.controller

import com.cclotus.cms.common.service.CmsTermRelationshipsService
import org.jeecg.common.api.vo.Result
import org.jeecg.common.aspect.annotation.AutoLog
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * CmsTermController
 *
 * @author ruoyi
 * @date 2023-03-07
 */
@RestController
@RequestMapping("/cms/relation")
class CmsTermRelationshipsController {
    @Autowired
    lateinit var cmsTermRelationshipsService: CmsTermRelationshipsService

    /**
     * 为内容绑定一组分类，如：一篇文章添加多个标签
     */
    @AutoLog(value = "关联关系管理", operateType = 2)
    @PostMapping("/p/{postId}/t/{termIds}")
    fun bindObjectToTerms(
        @PathVariable("postId") postId: Int,
        @PathVariable("termIds") termIds: Array<Int>
    ): Result<*> {
        cmsTermRelationshipsService.bindPostToTerms(postId , termIds)
        return Result.ok<Any>("添加成功!")
    }

    /**
     * 为分类绑定一组对象，如：一个栏目下添加多篇文章
     */
    @AutoLog(value = "关联关系管理", operateType = 2)
    @PostMapping("/t/{termId}/o/{objectIds}")
    fun bindTermToObjects(
        @PathVariable("termId") termId: Int,
        @PathVariable("objectIds") objectIds: Array<Int>
    ): Result<*> {
        cmsTermRelationshipsService.bindTermToObjects(termId , objectIds)
        return Result.ok<Any>("添加成功!")
    }

    /**
     * 从关联关系中，删除与termId关联的一组objectIds
     */
    @AutoLog(value = "关联关系管理", operateType = 4)
    @DeleteMapping("/t/{termId}/o/{objectIds}")
    fun removeObjects(
        @PathVariable("termId") termId: Int,
        @PathVariable("objectIds") objectIds: Array<Int>
    ):  Result<*>  {
        cmsTermRelationshipsService.deleteObjectIds(termId , objectIds)
        return Result.ok<Any>("删除成功!");
    }

    /**
     * 从关联关系中，删除与objectId关联的一组termIds
     */
    @AutoLog(value = "关联关系管理", operateType = 4)
    @DeleteMapping("/o/{objectId}/t/{termIds}")
    fun removeTerms(
        @PathVariable("objectId") objectId: Int,
        @PathVariable("termIds") termIds: Array<Int>
    ): Result<*> {
        cmsTermRelationshipsService.deleteTermIds(objectId , termIds)
        return Result.ok<Any>("删除成功!");
    }
}