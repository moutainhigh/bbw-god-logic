package com.bbw.god.game.zxz.service;

import com.bbw.common.DateUtil;
import org.junit.Assert;
import org.junit.Test;

public class ZxzRefreshServiceTest {

    @Test
    public void isToRefresh() {
        //2022-09-30 10:00:01
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(10, DateUtil.fromDateTimeString("2022-09-28 00:00:01")));
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(10, DateUtil.fromDateTimeString("2022-09-29 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(10, DateUtil.fromDateTimeString("2022-09-30 00:00:01")));
//
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(20, DateUtil.fromDateTimeString("2022-09-28 00:00:01")));
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(20, DateUtil.fromDateTimeString("2022-09-29 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(20, DateUtil.fromDateTimeString("2022-09-30 00:00:01")));

        //2022-09-26 10:00:01
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(30, DateUtil.fromDateTimeString("2022-09-21 00:00:01")));
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(30, DateUtil.fromDateTimeString("2022-09-25 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(30, DateUtil.fromDateTimeString("2022-09-26 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(30, DateUtil.fromDateTimeString("2022-09-27 00:00:01")));
//
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(40, DateUtil.fromDateTimeString("2022-09-21 00:00:01")));
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(40, DateUtil.fromDateTimeString("2022-09-25 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(40, DateUtil.fromDateTimeString("2022-09-26 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(40, DateUtil.fromDateTimeString("2022-09-27 00:00:01")));

        //2022-09-29 10:00:01
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(30, DateUtil.fromDateTimeString("2022-09-21 00:00:01")));
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(30, DateUtil.fromDateTimeString("2022-09-25 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(30, DateUtil.fromDateTimeString("2022-09-26 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(30, DateUtil.fromDateTimeString("2022-09-27 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(30, DateUtil.fromDateTimeString("2022-09-28 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(30, DateUtil.fromDateTimeString("2022-09-29 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(30, DateUtil.fromDateTimeString("2022-09-29 13:00:01")));
//
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(40, DateUtil.fromDateTimeString("2022-09-21 00:00:01")));
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(40, DateUtil.fromDateTimeString("2022-09-25 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(40, DateUtil.fromDateTimeString("2022-09-26 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(40, DateUtil.fromDateTimeString("2022-09-27 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(40, DateUtil.fromDateTimeString("2022-09-28 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(40, DateUtil.fromDateTimeString("2022-09-29 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(40, DateUtil.fromDateTimeString("2022-09-29 13:00:01")));

        //2022-09-29 14:00:01
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(30, DateUtil.fromDateTimeString("2022-09-21 00:00:01")));
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(30, DateUtil.fromDateTimeString("2022-09-25 00:00:01")));
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(30, DateUtil.fromDateTimeString("2022-09-26 00:00:01")));
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(30, DateUtil.fromDateTimeString("2022-09-28 00:00:01")));
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(30, DateUtil.fromDateTimeString("2022-09-29 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(30, DateUtil.fromDateTimeString("2022-09-29 13:00:01")));
//
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(40, DateUtil.fromDateTimeString("2022-09-21 00:00:01")));
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(40, DateUtil.fromDateTimeString("2022-09-25 00:00:01")));
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(40, DateUtil.fromDateTimeString("2022-09-26 00:00:01")));
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(40, DateUtil.fromDateTimeString("2022-09-28 00:00:01")));
//        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(40, DateUtil.fromDateTimeString("2022-09-29 00:00:01")));
//        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(40, DateUtil.fromDateTimeString("2022-09-29 13:00:01")));

        //2022-09-29 14:00:01
        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(50, DateUtil.fromDateTimeString("2022-09-21 00:00:01")));
        Assert.assertEquals(true, ZxzRefreshService.isToRefresh(50, DateUtil.fromDateTimeString("2022-09-25 00:00:01")));
        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(50, DateUtil.fromDateTimeString("2022-09-26 00:00:01")));
        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(50, DateUtil.fromDateTimeString("2022-09-28 00:00:01")));
        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(50, DateUtil.fromDateTimeString("2022-09-29 00:00:01")));
        Assert.assertEquals(false, ZxzRefreshService.isToRefresh(50, DateUtil.fromDateTimeString("2022-09-29 13:00:01")));
    }
}