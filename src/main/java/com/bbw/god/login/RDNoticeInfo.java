package com.bbw.god.login;

import com.bbw.god.city.miaoy.hexagram.RDHexagram;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDNoticeInfo extends RDLogin implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer gold;// 元宝
    private Integer dice;// 体力
    /** 钻石 */
    private Integer diamond = null;
    //    private Integer unReadNum;// 邮件未读数量
    //    private Integer isMaouComing;// 魔王是否降临   下个版本可删除
//    private Integer activityNum;// 可领取的福利数
//    private Integer chargeActivityNum;// 充值奖励领取状态
//    private Integer firstRechargeActivityNum;//首冲入口的活动的奖励领取情况  下个版本可删除
//    private Integer isShowFirstRechargeIcon;//是否显示首冲的入口图标   下个版本可删除
//    private Integer holidayActivityNum;// 节日活动的奖励领取情况
//    private Integer isShowHolidayIcon;// 是否显示节日活动的入口图标
    //    private Integer isRechargeValid = 1;// 充值活动是否显示
//    private Integer isRankValid = 1;// 冲榜奖励是否显示
    //    private Integer getCopper;// 俸禄   下个版本可删除
//    private Integer friendsRequestNum;// 好友申请数              下个版本可删除
    private List<String> broadcast = new ArrayList<>();// 广播
    //    private Integer dailyTaskStatus;// 每日任务状态
//    private Integer mainTaskStatus;// 主线任务状态
    //    private Integer monsterQuantity;// 友怪数       下个版本可删除
    private Integer diceBuyTimes;// 体力购买次数
    private Integer dfzUseTimes;// 定风珠使用次数
    private Integer ykRemainDays;// 月卡剩余天数
    private Integer jkRemainDays;// 季卡剩余天数
    private Integer ableEndFight;// 是否可以跳过战斗
    private Integer endFightLevel;// 速战卡解锁等级
    private Integer remainChallengeLayers = 0;//梦魇迷仙洞剩余挑战层数
    //    private Integer isGuessCard;// 点将台是否开启  下个版本可删除
//    private List<Integer> activityMallBags = new ArrayList<>();// 活动礼包   下个版本可删除
    //    private Integer firstBought;// 是否充值过
//    private Integer cocTaskStatus;// 商会任务状态
//    private Integer guildTaskStatus;// 行会任务状态
//    private Integer tianlingBagStatus;// 天灵礼包 下个版本可删除
    private Integer userLimitType;// 用户行为限制状态,10为封号，20为禁言

//    private List<ActivityShow> openActivityTypes = null;// 该数组为右侧活动图标显示   下个版本可删除

    // 新手进阶任务
//    private Integer growTaskStatus;// 当前新手进阶任务的状态
//    private Integer curGrowTaskType;//当前是新手还是进阶任务
//    private Integer isPassGrowTask;//是否通过新手引导任务
    private List<String> redNotices = new ArrayList<>();

    private List<ActivityShow> dynamicMenuTypes = null;// 动态菜单图标
    private Integer taiYiProgress = 0;//太一 进度
    private Integer nightmareTaiYiProgress = 0;//梦魇太一 进度
    private Integer nvWaProgress = 0;//女娲进度
    private String youHun = null;//野怪位置 游魂标识 含有多个时用N间隔
    private Integer totalRecharge = null;// 累充金额

    private Boolean isJoinQuestionnaire = null;// 是否参与了问卷调查

    private RDHexagram.BuffInfo currentHexagram = null;

    private Long boostRemainTime = -1L;

    public void addRedNotices(List<String> notices) {
        this.redNotices.addAll(notices);
    }

    public void addOpenMenu(int type, int num) {
        if (this.dynamicMenuTypes == null) {
            this.dynamicMenuTypes = new ArrayList<ActivityShow>();
        }
        this.dynamicMenuTypes.add(ActivityShow.instance(type, num));
    }

    public void addOpenMenu(int type) {
        if (this.dynamicMenuTypes == null) {
            this.dynamicMenuTypes = new ArrayList<ActivityShow>();
        }
        this.dynamicMenuTypes.add(ActivityShow.instance(type, 0));
    }

    public void addOpenMenu(ActivityShow icon) {
        if (this.dynamicMenuTypes == null) {
            this.dynamicMenuTypes = new ArrayList<ActivityShow>();
        }
        this.dynamicMenuTypes.add(icon);
    }

    @Data
    public static class ActivityShow implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer showType = null;// 显示的类型，活动一般传递活动的父类型，如70为归回活动图标
        private Integer awardNum = 0;// 小红点=》 大于0表示有领取的奖励，一般传递具体可领取的数量
        private Long nextTime;

        public static ActivityShow instance(int showType, int awardNum) {
            ActivityShow show = new ActivityShow();
            show.setAwardNum(awardNum);
            show.setShowType(showType);
            return show;
        }

        public static ActivityShow instance(int showType, int awardNum,long nextTime) {
            ActivityShow show = instance(showType,awardNum);
            show.setNextTime(nextTime);
            return show;
        }
    }
}