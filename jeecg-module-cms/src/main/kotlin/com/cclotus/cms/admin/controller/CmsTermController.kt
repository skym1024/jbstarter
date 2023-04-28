package com.cclotus.cms.admin.controller

import com.cclotus.cms.common.domain.CmsTerm
import com.cclotus.cms.common.service.CmsTermService
import com.cclotus.cms.common.service.ICmsTermService
import com.cclotus.cms.common.vo.CmsTermForm
import io.swagger.annotations.Api
import lombok.extern.slf4j.Slf4j
import org.jeecg.common.api.vo.Result
import org.jeecg.common.aspect.annotation.AutoLog
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * CmsTermController
 *
 * @author ruoyi
 * @date 2023-03-07
 */
@Slf4j
@Api(tags = ["分类管理"])
@RestController
@RequestMapping("/cms")
class CmsTermController {
    @Autowired
    lateinit var cmsTermService: ICmsTermService

    /**
     * 查询分类列表
     */
    @GetMapping("/{taxonomy:wxAppMenu|menu|catalog|tag}/list")
    fun list(
        @PathVariable("taxonomy") taxonomy: String,
        @RequestParam(name = "pageNo", defaultValue = "1") pageNo: Int,
        @RequestParam(name = "pageSize", defaultValue = "10") pageSize: Int,
        cmsTerm: CmsTerm,
        paging: Boolean?
    ): Result<*> {
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
    @GetMapping("/{taxonomy:wxAppMenu|menu|catalog|tag}/{term}")
    fun detail(
        @PathVariable("taxonomy") taxonomy: String,
        @PathVariable("term") term: String
    ): Result<*> {
        return Result.OK(cmsTermService.detail(taxonomy, term));
    }

    /**
     * 新增分类
     */
    @AutoLog(value = "分类管理", operateType = 2)
    @PostMapping("/{taxonomy:wxAppMenu|menu|catalog|tag}")
    fun add(
        @PathVariable("taxonomy") taxonomy: String,
        @Validated @RequestBody cmsTerm: CmsTermForm
    ): Result<*> {
        cmsTerm.taxonomy = taxonomy
        cmsTermService.create(cmsTerm)
        return Result.ok<Any>("添加成功!")
    }

    /**
     * 修改分类
     */
    @AutoLog(value = "分类管理", operateType = 3)
    @PutMapping("/{taxonomy:wxAppMenu|menu|catalog|tag}")
    fun edit(
        @PathVariable("taxonomy") taxonomy: String,
        @RequestBody cmsTerm: CmsTermForm
    ): Result<*> {
        cmsTerm.taxonomy = taxonomy
        cmsTermService.update(cmsTerm)
        return Result.ok<Any>("修改成功!")
    }

    /**
     * 删除分类
     */
    @AutoLog(value = "分类管理", operateType = 4)
    @DeleteMapping("/{taxonomy:wxAppMenu|menu|catalog|tag}/{termIds}")
    fun remove(
        @PathVariable("taxonomy") taxonomy: String,
        @PathVariable termIds: Array<Int>
    ): Result<*> {
        cmsTermService.deleteByIds(termIds)
        return Result.ok<Any>("删除成功!");
    }
}