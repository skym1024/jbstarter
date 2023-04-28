package com.cclotus.cms.common.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.cclotus.cms.common.domain.CmsTerm
import org.apache.ibatis.annotations.Param

interface CmsTermMapper : BaseMapper<CmsTerm> {
    fun selectOneWithMetas(@Param("taxonomy") taxonomy: String,
                           @Param("termId") termId: Int?,
                           @Param("alias") alias: String?): CmsTerm?

    fun selectChildrenById(termId: Int): List<CmsTerm>
}
