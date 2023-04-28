package com.cclotus.cms.common.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.cclotus.cms.common.domain.CmsPostMeta
import com.cclotus.cms.common.mapper.CmsPostMetaMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CmsPostMetaService : ServiceImpl<CmsPostMetaMapper, CmsPostMeta>(), ICmsPostMetaService {

    @Autowired
    lateinit var cmsPostMetaMapper: CmsPostMetaMapper

    /**
     * 根据内容ID列表返回扩展属性列表
     * @param postIds 内容ID列表
     * @return 指定内容ID的扩展属性列表
     */
    override fun selectListByPostIds(postIds: List<Int>): List<CmsPostMeta> {
        if (postIds.isEmpty())
            return emptyList()

        return QueryWrapper<CmsPostMeta>()
            .`in`("post_id", postIds)
            .let {
                cmsPostMetaMapper.selectList(it)
            }
    }
}
