package com.cclotus.cms.common.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.cclotus.cms.common.domain.CmsPost
import com.cclotus.cms.common.domain.CmsTerm
import com.cclotus.cms.common.domain.CmsTermRelationships
import org.apache.ibatis.annotations.Param

interface CmsTermRelationshipsMapper : BaseMapper<CmsTermRelationships> {

    fun selectCatalogRelationsByPostId(postId: Int): List<CmsTermRelationships>

    fun selectCatalogTermsByPostId(postId: Int): List<CmsTerm>

    fun getBindingTermsByTermId(termId: Int):  List<CmsTerm>

    fun getBindingPostsByTermId(termId: Int):  List<CmsPost>

    fun deleteObjectIds(@Param("objectIds") objectIds: List<Int>): Int

    fun deleteObjectIdsByTermId(@Param("termId") termId: Int,
                                @Param("objectIds") objectIds: List<Int>): Int

    fun deleteTermIdsByObjectId(@Param("objectId") objectId: Int,
                                @Param("termIds") termIds: List<Int>): Int
}
