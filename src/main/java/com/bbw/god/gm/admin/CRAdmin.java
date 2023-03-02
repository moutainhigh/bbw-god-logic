package com.bbw.god.gm.admin;

/**
 * 管理端请求接口
 *
 * @author lzc
 * @date 2021年03月17日 下午15:39:26
 */
public class CRAdmin {

    public static class Cfg {
        /** 获取配置实体列表 */
        public static final String GET_CONFIG = "getConfig";
    }

    /**
     * 玩家数据相关的操作
     */
    public static class UserGm {
        /** 修复用户组 */
        public static final String FIX_USER_GROUP = "fixUserGroup";
        /** 限制登录 */
        public static final String LIMIT_LOGIN = "limitLogin";
        /** 限制发言 */
        public static final String LIMIT_TALKING = "limitTalking";
        /** 更新玩家到特定等级 */
        public static final String UPDATE_TO_LEVEL = "updateToLevel";
        /** 更新到某个等级的前一个等级（exp-1）*/
        public static final String UPDATE_TO_PRE_LEVEL = "updateToPreLevel";
        /** 设置新手引导的状态 */
        public static final String SET_GUIDE_STATUS = "setGuideStatus";
        /** 设置性别 */
        public static final String SET_SEX = "setSex";
    }

    /**
     * 玩家资源相关的操作
     */
    public static class UserRes {
        /** 加铜钱 */
        public static final String ADD_COPPER = "addCopper";
        /** 加元宝 */
        public static final String ADD_GOLD = "addGold";
        /** 加体力 */
        public static final String ADD_DICE = "addDice";
        /** 加元素 */
        public static final String ADD_ELE = "addEle";
        /** 添加特产 */
        public static final String ADD_SPECIAL = "addSpecial";
        /** 添加灵石 */
        public static final String ADD_LING_SHI = "addLingShi";
    }

    /**
     * 玩家数据相关的操作
     */
    public static class UserData {
        /** 添加月卡的天数 */
        public static final String ADD_YK_END_TIME = "addYKEndTime";
        /** 添加季卡的天数 */
        public static final String ADD_JK_END_TIME = "addJKEndTime";
        /** 添加速战卡 */
        public static final String ADD_SZK = "addSZK";
        /** 添加法宝 */
        public static final String ADD_TREASURES = "addTreasures";
        /** 模糊添加法宝 */
        public static final String ADD_LIKE_TREASURES = "addLikeTreasures";
        /** 邮件添加法宝 */
        public static final String ADD_TREASURES_BY_MAIL = "addTreasuresByMail";
        /** 加封地 */
        public static final String ADD_CITIES = "addCities";
    }

    /**
     * 玩家卡牌相关的操作
     */
    public static class UserCard {
        /** 重置卡牌技能 */
        public static final String RESET_CARD_SKILL = "resetCardSkill";
        /** 加卡牌 */
        public static final String ADD_CARDS = "addCards";
        /** 删除卡牌 */
        public static final String DEL_CARDS = "delCards";
        /** 删除卡牌 */
        public static final String DEL_CARDS_OF_UIDS = "delCardsOfUids";
        /** 调整卡牌等级 */
        public static final String UPDATE_CARD_TO_LEVEL = "updateCardToLevel";
        /** 调整卡牌阶数 */
        public static final String UPDATE_CARD_TO_HIERARCHY = "updateCardToHierarchy";
    }

    /**
     * 玩家任务相关的操作
     */
    public static class UserTask {
        /** 跳过新手进阶任务 */
        public static final String SET_PASS_GROW_TASKS = "setPassGrowTasks";
        /** 将新手进阶任务调整为 已完成未领取 状态 */
        public static final String SET_GROW_TASK_STATUS = "setGrowTaskStatus";
        /** 调整指定的新手进阶任务状态 */
        public static final String SET_GROW_TASK_STATUS_OF_INDEX = "setGrowTaskStatusOfIndex";
        /** 重置每日任务 */
        public static final String RESET_DAILY_TASKS = "resetDailyTasks";
    }
}