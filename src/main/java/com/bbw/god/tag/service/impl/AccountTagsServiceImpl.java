package com.bbw.god.tag.service.impl;

import com.bbw.cache.LocalCache;
import com.bbw.common.DateUtil;
import com.bbw.god.db.entity.InsAccountTagsEntity;
import com.bbw.god.db.service.InsAccountTagsService;
import com.bbw.god.tag.service.AccountTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author: suchaobin
 * @createTime: 2019-11-04 10:19
 **/
@Service
public class AccountTagsServiceImpl implements AccountTagsService {
    private static final String CACHE_TYPE = "AccountTagsServiceMap";

    @Autowired
    private InsAccountTagsService insAccountTagsService;

    @Override
    public void addTag(String account, String tag) {
        if (!exists(account, tag)) {
            Date overDate = DateUtil.addYears(DateUtil.now(), 100);
            saveData(account, tag, overDate);
        }
    }

    @Override
    public void addTag(String account, String tag, Date overDate) {
        if (!exists(account, tag)) {
            saveData(account, tag, overDate);
        }
    }

    @Override
    public void addTagToday(String account, String tag) {
        if (!exists(account, tag)) {
            Date overDate = DateUtil.toDate(DateUtil.now(), "23:59:59");
            saveData(account, tag, overDate);
        }
    }

    @Override
    public void addTagOneWeek(String account, String tag) {
        if (!exists(account, tag)) {
            Date overDate = DateUtil.addDays(DateUtil.now(), 7);
            saveData(account, tag, overDate);
        }
    }

    /**
      * 将数据保存到数据库和缓存中，并设置对应的过期时间
      * @param account 玩家账号
      * @param tag 标签
      * @param overDate 数据库保存的标签过期时间
      * @return: void
      **/
    private void saveData(String account, String tag, Date overDate) {
        InsAccountTagsEntity entity = InsAccountTagsEntity.getInstance(account, tag, DateUtil.toDateTimeLong(overDate));
        long secondsBetween = DateUtil.getSecondsBetween(DateUtil.now(), DateUtil.fromDateLong(entity.getOverTime()));
        if (secondsBetween >= LocalCache.ONE_WEEK) {
            //剩余有效时间超过一周的，缓存保存一周，未超过一周的，缓存保存到对象失效
            LocalCache.getInstance().put(CACHE_TYPE, entity.getId(), entity, LocalCache.ONE_WEEK);
        } else {
            LocalCache.getInstance().put(CACHE_TYPE, entity.getId(), entity, secondsBetween);
        }
        insAccountTagsService.insert(entity);
    }

    @Override
    public boolean exists(String account, String tag) {
        InsAccountTagsEntity entity;
        String id = account + "#" + tag;
        boolean b = LocalCache.getInstance().containsKey(CACHE_TYPE, id);
        //判断缓存中是否存在
        if (b) {
            entity = LocalCache.getInstance().get(CACHE_TYPE, id);
            long secondsBetween = DateUtil.getSecondsBetween(DateUtil.now(), DateUtil.fromDateLong(entity.getOverTime()));
            //判断对象是否失效，未失效才返回true
            return secondsBetween > 0;
        }
        entity = insAccountTagsService.selectById(id);
        if (entity == null) {
            return false;
        }

        long secondsBetween = DateUtil.getSecondsBetween(DateUtil.now(), DateUtil.fromDateLong(entity.getOverTime()));
        //判断数据库的对象是否失效
        if (secondsBetween <= 0) {
            insAccountTagsService.deleteById(id);
            return false;
        }

        if (secondsBetween >= LocalCache.ONE_WEEK) {
            //剩余有效时间超过一周的，缓存保存一周，未超过一周的，缓存保存到对象失效
            LocalCache.getInstance().put(CACHE_TYPE, id, entity, LocalCache.ONE_WEEK);
        } else {
            LocalCache.getInstance().put(CACHE_TYPE, id, entity, secondsBetween);
        }
        return true;
    }
}
