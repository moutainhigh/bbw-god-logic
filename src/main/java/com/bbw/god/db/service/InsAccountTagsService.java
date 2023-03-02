package com.bbw.god.db.service;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.db.entity.InsAccountTagsEntity;

import java.util.List;

/**
 * @author: suchaobin
 * @createTime: 2019-11-04 10:19
 **/
public interface InsAccountTagsService extends IService<InsAccountTagsEntity> {
    /**
      * 根据玩家账号获取拥有的所有账号标签
      * @param account 玩家账号
      * @return: java.util.List<java.lang.String>
      **/
    List<String> getAllTagsByAccount(String account);
}
