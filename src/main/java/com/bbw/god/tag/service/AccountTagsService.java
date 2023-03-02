package com.bbw.god.tag.service;

import java.util.Date;

/**
 * @author: suchaobin
 * @createTime: 2019-11-04 10:19
 **/
public interface AccountTagsService {
    /**
     * 判断玩家是否拥有该标签
     *
     * @param account 玩家账号
     * @param tag     标签
     * @return: boolean
     * @see com.bbw.god.tag.TagName
     **/
    public boolean exists(String account, String tag);

    /**
     * 添加标签,永不过期
     *
     * @param account 玩家账号
     * @param tag     标签
     * @see com.bbw.god.tag.TagName
     */
    public void addTag(String account, String tag);

    /**
     * 添加标签，指定日期时失效
     *
     * @param account  玩家账号
     * @param tag      标签
     * @param overDate 过期时间
     * @return: void
     * @see com.bbw.god.tag.TagName
     **/
    public void addTag(String account, String tag, Date overDate);

    /**
     * 添加标签，今天结束时失效
     *
     * @param account 玩家账号
     * @param tag     标签
     * @return: void
     * @author suchaobin
     * @see com.bbw.god.tag.TagName
     **/
    public void addTagToday(String account, String tag);

    /**
     * 添加标签，一周后失效
     *
     * @param account 玩家账号
     * @param tag     标签
     * @return: void
     * @see com.bbw.god.tag.TagName
     **/
    public void addTagOneWeek(String account, String tag);

}
