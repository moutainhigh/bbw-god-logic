package com.bbw.god.tag.service.impl;

import com.bbw.cache.LocalCache;
import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.god.db.entity.InsUserTagsEntity;
import com.bbw.god.db.service.InsUserTagsService;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.tag.TagName;
import com.bbw.god.tag.service.UserTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author: suchaobin
 * @createTime: 2019-10-30 15:22
 **/
@Service
public class UserTagsServiceImpl implements UserTagsService {
    private static final String CACHE_TYPE = "UserTagsServiceMap";

    @Autowired
    private InsUserTagsService insUserTagsService;

    @Autowired
    private MailService mailService;

    @Override
    public void addTag(Long uid, String tag) {
        if (!exists(uid, tag)) {
            InsUserTagsEntity entity = InsUserTagsEntity.getInstance(uid, tag);
            LocalCache.getInstance().put(CACHE_TYPE, entity.getId(), entity, LocalCache.ONE_WEEK);
            insUserTagsService.insert(entity);
        }
    }

    @Override
    public void addTag(Long uid, String tag, Date overDate) {
        if (!exists(uid, tag)) {
            InsUserTagsEntity entity = InsUserTagsEntity.getInstance(uid, tag, DateUtil.toDateTimeLong(overDate));
            long secondsBetween = DateUtil.getSecondsBetween(DateUtil.now(), overDate);
            LocalCache.getInstance().put(CACHE_TYPE, entity.getId(), entity, secondsBetween);
            insUserTagsService.insert(entity);
        }
    }

    @Override
    public void addTagToday(Long uid, String tag) {
        if (!exists(uid, tag)) {
            Date overDate = DateUtil.toDate(DateUtil.now(), "23:59:59");
            InsUserTagsEntity entity = InsUserTagsEntity.getInstance(uid, tag, DateUtil.toDateTimeLong(overDate));
            long secondsBetween = DateUtil.getSecondsBetween(DateUtil.now(), overDate);
            LocalCache.getInstance().put(CACHE_TYPE, entity.getId(), entity, secondsBetween);
            insUserTagsService.insert(entity);
        }
    }

    @Override
    public void addTagOneWeek(Long uid, String tag) {
        if (!exists(uid, tag)) {
            Date overDate = DateUtil.addDays(DateUtil.now(), 7);
            InsUserTagsEntity entity = InsUserTagsEntity.getInstance(uid, tag, DateUtil.toDateTimeLong(overDate));
            LocalCache.getInstance().put(CACHE_TYPE, entity.getId(), entity, LocalCache.ONE_WEEK);
            insUserTagsService.insert(entity);
        }
    }

    @Override
    public boolean exists(Long uid, String tag) {
        InsUserTagsEntity entity;
        String id = uid + "#" + tag;
        boolean b = LocalCache.getInstance().containsKey(CACHE_TYPE, id);
        //判断缓存中是否存在
        if (b) {
            entity = LocalCache.getInstance().get(CACHE_TYPE, id);
            long secondsBetween = DateUtil.getSecondsBetween(DateUtil.now(), DateUtil.fromDateLong(entity.getOverTime()));
            //判断对象是否失效，未失效才返回true
            return secondsBetween > 0;
        }
        entity = insUserTagsService.selectById(id);
        if (entity == null) {
            insUserTagsService.deleteById(id);
            return false;
        } else {
            long secondsBetween = DateUtil.getSecondsBetween(DateUtil.now(), DateUtil.fromDateLong(entity.getOverTime()));
            //判断数据库的对象是否失效
            if (secondsBetween > 0) {
                if (secondsBetween >= LocalCache.ONE_WEEK) {
                    //剩余有效时间超过一周的，缓存保存一周，未超过一周的，缓存保存到对象失效
                    LocalCache.getInstance().put(CACHE_TYPE, id, entity, LocalCache.ONE_WEEK);
                } else {
                    LocalCache.getInstance().put(CACHE_TYPE, id, entity, secondsBetween);
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public void useLatestApkFirstLoginAwards(Long uid) {
        if (!exists(uid, TagName.LATEST_APK_VERSION)) {
            long overTime = DateUtil.toDateTimeLong(DateUtil.addDays(DateUtil.now(), 10));
            InsUserTagsEntity entity = InsUserTagsEntity.getInstance(uid, TagName.LATEST_APK_VERSION, overTime);
            sendAwardMail(uid);
            entity.setFirstLogin(false);
            long secondsBetween = DateUtil.getSecondsBetween(DateUtil.now(), DateUtil.addDays(DateUtil.now(), 10));
            LocalCache.getInstance().put(CACHE_TYPE, entity.getId(), entity, secondsBetween);
            insUserTagsService.insert(entity);
        } else {
            InsUserTagsEntity entity = LocalCache.getInstance().get(CACHE_TYPE, uid + "#" + TagName.LATEST_APK_VERSION);
            if (entity.isFirstLogin()) {
                sendAwardMail(uid);
                entity.setFirstLogin(false);
            }
        }
    }

    private void sendAwardMail(Long uid) {
        String title = LM.I.getMsgByUid(uid,"mail.use.latestApk.firstLogin.award.title");
        String content = LM.I.getMsgByUid(uid,"mail.use.latestApk.firstLogin.award.content");
        String award = "[{item:" + AwardEnum.FB.getValue() + ",awardId:" + TreasureEnum.XZY.getValue() + ",num:1}]";
        mailService.sendAwardMail(title, content, uid, award);
    }
}
