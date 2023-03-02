package com.bbw.god.activity.rd;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.holiday.processor.holidaychinesezodiaccollision.RdChineseZodiacConllision;
import com.bbw.god.activity.holiday.processor.holidaychristmaswish.RdChristmasWishs;
import com.bbw.god.activity.holiday.processor.holidaythankflowerlanguage.RdFlowerpotInfos;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.gameuser.task.RDTaskList;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.rd.item.RDItems;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 活动
 *
 * @author suhq
 * @date 2019年3月3日 下午11:24:29
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDActivityList extends RDItems<RDActivityItem> implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer curType = null;
    private Integer totalProgress;
    private Long remainTime;
    private Integer isTodayOpened = 0;// 今日是否开启
    private RDMallList rdMallList = null;// 活动期间可兑换的商品集合

    private List<Integer> goodEvents = null;//好事集合
    private List<Integer> badEvents = null;//禁忌集合
    private String dateInfo = null;//时间信息

    /** 充值签到专用 */
    private Integer rechargeId = 0;
    private List<RDAward> awardeds;
    /** 每日摇一摇福利id */
    private int dailyShakeWelfareId = 0;
    /** 盘子上食物信息 */
    private List<Integer> plateFoodInfos;
    /** 离线收益 */
    private Integer offlineRevenue;
    /** 花盆信息 */
    private RdFlowerpotInfos rdFlowerpotInfos;
    /** 活动期间可完成的任务集合 */
    private RDTaskList rdTaskList;
    /** 圣诞心愿 */
    private RdChristmasWishs christmasWishs;
    /** 生肖对碰 */
    private RdChineseZodiacConllision chineseZodiacConllision;

    /**
     * 更新今天是否开放功能
     *
     * @param beginTime
     */
    public void upDateIsTodayOpened(Date beginTime, int openDays) {
        Date now = DateUtil.now();
        int daysBetween = DateUtil.getDaysBetween(beginTime, now) + 1;
        if (daysBetween < openDays) {
            this.isTodayOpened = 1;
        }

    }
}
