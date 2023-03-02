package com.bbw.god.tag.service;

import java.util.Date;

/**
 * @author: suchaobin
 * @createTime: 2019-10-30 15:21
 **/
public interface UserTagsService {
    /**
     * 添加标签,永不过期
     *
     * @param uid 玩家id
     * @param tag 标签
     * @see com.bbw.god.tag.TagName
     */
    public void addTag(Long uid, String tag);

    /**
     * 添加标签，指定日期时失效
     *
     * @param uid      玩家id
     * @param tag      标签
     * @param overDate 过期时间
     * @return: void
     * @see com.bbw.god.tag.TagName
     **/
    public void addTag(Long uid, String tag, Date overDate);

    /**
     * 添加标签，今天结束时失效
     *
     * @param uid 玩家id
     * @param tag 标签
     * @return: void
     * @author suchaobin
     * @see com.bbw.god.tag.TagName
     **/
    public void addTagToday(Long uid, String tag);

    /**
     * 添加标签，一周后失效
     *
     * @param uid 玩家id
     * @param tag 标签
     * @return: void
     * @see com.bbw.god.tag.TagName
     **/
    public void addTagOneWeek(Long uid, String tag);

    /**
     * 判断玩家是否拥有该标签
     *
     * @param uid 玩家id
     * @param tag 标签
     * @return: boolean
     * @see com.bbw.god.tag.TagName
     **/
    public boolean exists(Long uid, String tag);


    /**
     * 玩家使用最新的apk包首次登陆时赠送礼物
     *
     * @param uid 玩家id
     **/
    public void useLatestApkFirstLoginAwards(Long uid);
}
