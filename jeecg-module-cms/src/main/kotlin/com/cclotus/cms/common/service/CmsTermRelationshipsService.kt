package com.cclotus.cms.common.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.cclotus.cms.common.domain.CmsPost
import com.cclotus.cms.common.domain.CmsTerm
import com.cclotus.cms.common.domain.CmsTermRelationships
import com.cclotus.cms.common.mapper.CmsPostMapper
import com.cclotus.cms.common.mapper.CmsTermMapper
import com.cclotus.cms.common.mapper.CmsTermRelationshipsMapper
import org.jeecg.common.exception.ServiceException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CmsTermRelationshipsService {

    @Autowired
    lateinit var cmsTermMapper: CmsTermMapper

    @Autowired
    lateinit var cmsPostMapper: CmsPostMapper

    @Autowired
    lateinit var cmsTermRelationshipsMapper: CmsTermRelationshipsMapper

    /**
     *  为内容绑定一组分类，如：一篇文章添加多个标签或关联某个栏目
     */
    @Transactional
    fun bindPostToTerms(postId: Int, termIds: Array<Int>):Int {
        if(!cmsPostMapper.exists(QueryWrapper<CmsPost>().eq("post_id", postId)))
            throw ServiceException("内容Id不存在", 500)

        termIds.forEach {
            cmsTermMapper.selectOne(QueryWrapper<CmsTerm>().eq("term_id", it))
                ?: throw ServiceException("分类Id不存在", 500)

            CmsTermRelationships().apply {
                this.objectId = postId
                this.termId = it
            }.also { r ->
                if (cmsTermRelationshipsMapper.insert(r) != 1) {
                    throw ServiceException("插入内容与分类的关系失败", 500)
                }
            }
        }
        return termIds.size
    }

    /**
     *  为分类绑定一组对象，如：一个栏目或标签下添加多篇文章
     *
     */
    @Transactional
    fun bindTermToObjects(termId: Int, objectIds:Array<Int>):Int {
        // 检查分类是否存在
        val term = cmsTermMapper.selectOne(QueryWrapper<CmsTerm>().eq("term_id", termId))

        objectIds.forEach {
            // 检查对象是否存在
            if(!isObjectExist(term.type!!, it))
                throw ServiceException("指定的对象不存在", 500)

            CmsTermRelationships().apply {
                this.objectId = it
                this.termId = termId
            }.also { r ->
                if (cmsTermRelationshipsMapper.insert(r) != 1) {
                    throw ServiceException("插入分类与关联对象的关系失败", 500)
                }
            }
        }

        return objectIds.size
    }

    private fun isObjectExist(dataType: String, objectId: Int): Boolean {
        return when(getObjectType(dataType)){
            "term" -> cmsTermMapper.exists(QueryWrapper<CmsTerm>().eq("term_id", objectId))
            "post" -> cmsPostMapper.exists(QueryWrapper<CmsPost>().eq("post_id", objectId))
            else -> throw ServiceException("未知的分类数据类型", 500)
        }
    }

    fun getObjectType(dataType: String): String{
        // TODO: 建立对应的字典项, 构建数据类型集合
        return when(dataType){
            in setOf("term", "child", "wxAppMenu", "menu", "catalog", "tag") -> "term"
            in setOf("post", "article", "page", "picture", "video") -> "post"
            in setOf("link", "external", "route", "appId") -> "link"
            else -> "unknown"
        }
    }

    /**
     *  从关联关系中，删除与termId关联的一组objectIds
     */
    @Transactional
    fun deleteObjectIds(termId: Int, objectIds: Array<Int>): Int {
        return cmsTermRelationshipsMapper.deleteObjectIdsByTermId(termId, objectIds.toList())
    }

    /**
     *  从关联关系中，删除与objectId关联的一组termIds
     */
    @Transactional
    fun deleteTermIds(objectId: Int, termIds: Array<Int>): Int {
        return cmsTermRelationshipsMapper.deleteTermIdsByObjectId(objectId, termIds.toList())
    }
}
