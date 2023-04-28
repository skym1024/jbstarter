package com.cclotus.cms.common.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.cclotus.cms.common.domain.CmsTermMeta
import com.cclotus.cms.common.mapper.CmsTermMetaMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CmsTermMetaService {

    @Autowired
    lateinit var cmsTermMetaMapper: CmsTermMetaMapper

    /**
     * 根据分类ID列表返回扩展属性列表
     * @param termIds 内容ID列表
     * @return 指定termID的扩展属性列表
     */
    fun selectListByTermIds(termIds: List<Int>): List<CmsTermMeta> {
        if (termIds.isEmpty())
            return mutableListOf()

        return QueryWrapper<CmsTermMeta>()
            .`in`("term_id", termIds)
            .let {
                cmsTermMetaMapper.selectList(it)
            }
    }
}
