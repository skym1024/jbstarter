package com.cclotus.cms.common.service

import com.baomidou.mybatisplus.extension.service.IService
import com.cclotus.cms.common.domain.CmsPostMeta

public interface ICmsPostMetaService : IService<CmsPostMeta> {
    /**
     * 根据内容ID列表返回扩展属性列表
     * @param postIds 内容ID列表
     * @return 指定内容ID的扩展属性列表
     */
    fun selectListByPostIds(postIds: List<Int>): List<CmsPostMeta>
}
