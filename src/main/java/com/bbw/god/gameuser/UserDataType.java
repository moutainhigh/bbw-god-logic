package com.bbw.god.gameuser;

import com.bbw.exception.CoderException;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.UserGodBlessRecord;
import com.bbw.god.activity.holiday.UserYouHun;
import com.bbw.god.activity.processor.cardboost.UserBoostCards;
import com.bbw.god.activity.processor.rechargesign.UserRechargeSignRecord;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.UserCitySetting;
import com.bbw.god.city.chengc.difficulty.UserAttackDifficulty;
import com.bbw.god.city.entity.UserTYFCell;
import com.bbw.god.city.entity.UserTYFTurn;
import com.bbw.god.city.exp.UserCityExpRecords;
import com.bbw.god.city.miaoy.hexagram.UserHexagram;
import com.bbw.god.city.miaoy.hexagram.UserHexagramBuff;
import com.bbw.god.city.mixd.nightmare.UserNightmareMiXian;
import com.bbw.god.city.mixd.nightmare.UserNightmareMiXianEnemy;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.UserNvWaPriceModel;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.UserNvWaTradeRecord;
import com.bbw.god.city.taiyf.UserTyfFillRecord;
import com.bbw.god.city.taiyf.mytaiyf.UserMYTyfFillRecord;
import com.bbw.god.city.taiyf.mytaiyf.UserMYTyfRoundSpecial;
import com.bbw.god.city.yed.UserAdventure;
import com.bbw.god.city.yeg.UserYeGElite;
import com.bbw.god.city.yeg.xiongshou.UserXionShouAward;
import com.bbw.god.game.chanjie.ChanjieUserInfo;
import com.bbw.god.game.combat.video.UserCombatVideo;
import com.bbw.god.game.dfdj.fight.DfdjFighter;
import com.bbw.god.game.sxdh.SxdhFighter;
import com.bbw.god.game.sxdh.SxdhShopRecord;
import com.bbw.god.game.transmigration.entity.UserTransmigration;
import com.bbw.god.game.transmigration.entity.UserTransmigrationCity;
import com.bbw.god.game.transmigration.entity.UserTransmigrationRecord;
import com.bbw.god.game.wanxianzhen.UserWanXian;
import com.bbw.god.game.zxz.entity.*;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsCardGroupInfo;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsInfo;
import com.bbw.god.gameuser.achievement.UserAchievement;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.UserAwardedAchievements;
import com.bbw.god.gameuser.achievement.UserRecentAchievements;
import com.bbw.god.gameuser.bag.UserBagBuyRecord;
import com.bbw.god.gameuser.biyoupalace.UserBYPalace;
import com.bbw.god.gameuser.biyoupalace.UserBYPalaceLockSkill;
import com.bbw.god.gameuser.buddy.AskBuddy;
import com.bbw.god.gameuser.buddy.FriendBuddy;
import com.bbw.god.gameuser.businessgang.digfortreasure.UserDigTreasure;
import com.bbw.god.gameuser.businessgang.digfortreasure.UserDigTreasurePos;
import com.bbw.god.gameuser.businessgang.luckybeast.UserLuckyBeast;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangInfo;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangTaskInfo;
import com.bbw.god.gameuser.card.*;
import com.bbw.god.gameuser.card.equipment.data.UserCardXianJue;
import com.bbw.god.gameuser.card.equipment.data.UserCardZhiBao;
import com.bbw.god.gameuser.card.juling.UserJuLJInfo;
import com.bbw.god.gameuser.chamberofcommerce.UserCocExpTaskInfo;
import com.bbw.god.gameuser.chamberofcommerce.UserCocInfo;
import com.bbw.god.gameuser.chamberofcommerce.UserCocTaskInfo;
import com.bbw.god.gameuser.dice.UserDiceInfo;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.guide.UserNewerGuide;
import com.bbw.god.gameuser.helpabout.UserHelpAbout;
import com.bbw.god.gameuser.kunls.data.UserInfusionInfo;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.beast.UserLeaderBeasts;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquipment;
import com.bbw.god.gameuser.leadercard.fashion.UserLeaderFashion;
import com.bbw.god.gameuser.leadercard.skil.UserLeaderCardSkillTree;
import com.bbw.god.gameuser.leadercard.skil.UserLeaderCardSkillTreeDetail;
import com.bbw.god.gameuser.limit.UserLimit;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.gameuser.nightmarenvwam.godsaltar.UserGodsAltarInfo;
import com.bbw.god.gameuser.nightmarenvwam.pinchpeople.UserPinchPeopleInfo;
import com.bbw.god.gameuser.pay.UserPayInfo;
import com.bbw.god.gameuser.pay.UserReceipt;
import com.bbw.god.gameuser.res.dice.UserDiceCapacity;
import com.bbw.god.gameuser.special.UserPocket;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.UserSpecialSaleRecord;
import com.bbw.god.gameuser.special.UserSpecialSetting;
import com.bbw.god.gameuser.task.businessgang.UserBusinessGangSpecialtyShippingTask;
import com.bbw.god.gameuser.task.businessgang.UserBusinessGangWeeklyTask;
import com.bbw.god.gameuser.task.daily.UserDailyTask;
import com.bbw.god.gameuser.task.daily.UserDailyTaskInfo;
import com.bbw.god.gameuser.task.dfdjchallenge.UserDfdjSeasonTask;
import com.bbw.god.gameuser.task.fshelper.FsHepler;
import com.bbw.god.gameuser.task.godtraining.UserGodTrainingTask;
import com.bbw.god.gameuser.task.grow.UserGrowTask;
import com.bbw.god.gameuser.task.main.UserMainTask;
import com.bbw.god.gameuser.task.sxdhchallenge.UserSxdhSeasonTask;
import com.bbw.god.gameuser.task.timelimit.UserCardVigor;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.gameuser.task.timelimit.cunz.UserCunzTasksInfo;
import com.bbw.god.gameuser.treasure.UserSymbolInfo;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureEffect;
import com.bbw.god.gameuser.treasure.UserTreasureRecord;
import com.bbw.god.gameuser.treasure.xianjiabox.UserXianJiaBox;
import com.bbw.god.gameuser.treasure.xianrenbox.UserXianRenBox;
import com.bbw.god.gameuser.unique.UserMonster;
import com.bbw.god.gameuser.unique.UserZxz;
import com.bbw.god.gameuser.yaozu.UserYaoZuInfo;
import com.bbw.god.gameuser.yuxg.*;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.mall.UserMallRefreshRecord;
import com.bbw.god.mall.cardshop.UserCardPool;
import com.bbw.god.mall.snatchtreasure.UserSnatchTreasure;
import com.bbw.god.mall.snatchtreasure.UserSnatchTreasureBox;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import com.bbw.god.notify.push.UserPush;
import com.bbw.god.notify.rednotice.UserAttackCityRedNotice;
import com.bbw.god.rechargeactivities.processor.timelimit.UserLimitBagCondition;
import com.bbw.god.rechargeactivities.wartoken.UserWarToken;
import com.bbw.god.rechargeactivities.wartoken.UserWarTokenTask;
import com.bbw.god.road.UserCrossingRecord;
import com.bbw.god.server.guild.UserGuild;
import com.bbw.god.server.guild.UserGuildTaskInfo;
import com.bbw.god.server.maou.alonemaou.UserAloneMaouData;
import com.bbw.god.server.maou.bossmaou.UserBossMaouData;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 玩家资源类型
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-07 15:04
 */
@Getter
@AllArgsConstructor
public enum UserDataType {
    ACTIVITY("activity", UserActivity.class),
    RECHARGE_SIGN("rechargeSign", UserRechargeSignRecord.class),
    BOOST_CARDS("boostCards", UserBoostCards.class),
    NEWER_GUIDE("newerGuide", UserNewerGuide.class),
    CARD("card", UserCard.class), // 卡牌
    CARD_GROUP("cardGroup", UserCardGroup.class), // 卡牌编组
    CARD_RANDOM("cardRandom", UserCardRandom.class), // 保底策略的卡牌随机记录
    SPECIAL("special", UserSpecial.class), // 特产
    SPECIAL_SETTING("specialSetting", UserSpecialSetting.class),// 玩家特产设置
    SPECIAL_SALE_RECORD("special_sale_record", UserSpecialSaleRecord.class),// 特产交易记录
    POCKET("pocket", UserPocket.class), //贴身口袋
    CITY("city", UserCity.class), // 城池
    NIGHT_MARE_CITY("nightMareCity", UserNightmareCity.class), // 梦魇城池
    CITY_SETTING("citySetting", UserCitySetting.class), // 城池设置
    CITY_EXP_RECORDS("cityExpRecords", UserCityExpRecords.class), // 城池体验
    TREASURE("treasure", UserTreasure.class), // 法宝
    TREASURE_EFFECT("treasureEffect", UserTreasureEffect.class), // 玩家元素馆道具效果记录
    TREASURE_RECORD("treasureRecord", UserTreasureRecord.class), // 玩家道具使用记录
    MALL_RECORD("mallRecord", UserMallRecord.class), // 商城记录
    MALL_SM_REFRESH("mallSMRefresh", UserMallRefreshRecord.class), // 商城神秘刷新记录
    CARD_POOL("cardPool", UserCardPool.class), // 玩家卡池记录
    GOD("god", UserGod.class), // 神仙
    CROSSING_RECORD("crossingRecord", UserCrossingRecord.class),//玩家经过路口的记录
    @Deprecated
    DAILY_TASK("taskDaily", UserDailyTask.class), // 每日任务
    DAILY_TASK_INFO("taskDailyInfo", UserDailyTaskInfo.class),// 每日任务信息
    MAIN_TASK("taskMain", UserMainTask.class), // 主线任务
    GROW_TASK("taskGrow", UserGrowTask.class), // 新手和进阶任务
    GOD_TRAINING_TASK("taskGodTraining", UserGodTrainingTask.class), // 上仙试炼任务
    SXDH_SEASON_TASK("taskSxdhSeason", UserSxdhSeasonTask.class), // 神仙大会赛季挑战
    DFDJ_SEASON_TASK("taskDfdjSeason", UserDfdjSeasonTask.class), // 巅峰对决赛季挑战
    ALONE_MAOU_DATA("maouAloneData", UserAloneMaouData.class), // 玩家独占魔王数据
    BOSS_MAOU_DATA("maouBossData", UserBossMaouData.class), // 玩家魔王降临数据

    ACHIEVEMENT("achievement", UserAchievement.class), // 玩家成就
    ACHIEVEMENT_AWARDED("achievement_awarded", UserAwardedAchievements.class), // 玩家已领取的成就
    ACHIEVEMENT_RECENT("achievement_recent", UserRecentAchievements.class),// 玩家最近完成的成就

    SYMBOL_INFO("symbol_info", UserSymbolInfo.class),// 玩家符箓
    JLJ("jlj", UserJuLJInfo.class),// 玩家符箓

    ZXZ("zxz", UserZxz.class), // 诛仙阵
    // MAOU_RECORD("maourecord", MaouAttackDetail.class), // 打魔王记录
    /** 诛仙阵:玩家区域卡组 */
    USER_ZXZ_CARD_GROUP("userZxzCardGroup", UserZxzCardGroupInfo.class),
    /** 诛仙阵：玩家难度数据 */
    USER_ZXZ("userZxz", UserZxzInfo.class),
    /** 诛仙阵：玩家区域数据*/
    USER_ZXZ_REGION("userZxzRegion", UserZxzRegionInfo.class),
    USER_ENTRY("userEntry", UserEntryInfo.class),
    /** 诛仙阵：玩家区域通关卡组 */
    USER_PASS_CARD_GROUP("userPassCardGroup", UserPassRegionCardGroupInfo.class),
    /** 诛仙阵：四圣挑战 */
    USER_ZXZ_FOUR_SAINTS("userZxzFourSaintsInfo",UserZxzFourSaintsInfo.class),
    /** 诛仙阵：四圣挑战卡组*/
    USER_ZXZ_FOUR_SAINTS_CARD_GROUP("userZxzFourSaintsCardGroup", UserZxzFourSaintsCardGroupInfo.class),
    MAIL("mail", UserMail.class), // 玩家邮件
    MONSTER_HELP("monster", UserMonster.class), // 好友打怪记录

    TYF_CELL("tyf", UserTYFCell.class), // OLD太一府
    TYF_TURN("tyfTurn", UserTYFTurn.class), // OLD太一府 - 填充特产轮次
    TYF_FILL_RECORD("tyfFillRecord", UserTyfFillRecord.class), // 太一府捐献记录
    MYTYF_FILL_RECORD("mytyfFillRecord", UserMYTyfFillRecord.class), // 梦魇太一府捐献记录
    MYTYF_ROUND_SPECIAL("mytyfRoundSpecial", UserMYTyfRoundSpecial.class), // 梦魇太一府本轮特产

    LIMIT("limit", UserLimit.class), // 玩家行为
    LOGIN_INFO("login", UserLoginInfo.class), // 用户登录信息
    AskBuddy("buddyAsk", AskBuddy.class), // 好友添加请求信息
    FriendBuddy("buddyFriend", FriendBuddy.class), // 好友对象信息
    RECEIPT("receipt", UserReceipt.class), // 玩家充值信息

    HELP_ABOUT("helpAbout", UserHelpAbout.class), // 玩家文字帮助阅读奖励

    Chamber_Of_Commerce_User_Info("cocUserInfo", UserCocInfo.class), // 商会玩家信息
    Chamber_Of_Commerce_User_Task_Info("cocUserTaskInfo", UserCocTaskInfo.class), // 商会任务信息
    Chamber_Of_Commerce_User_ExpTask_Info("cocUserExpTaskInfo", UserCocExpTaskInfo.class), // 商会历练信息

    ChanjieUserInfo("chanjieUserInfo", ChanjieUserInfo.class), // 阐截斗法用户信息
    Guild_User_Info("guildUserInfo", UserGuild.class), // 行会信息
    Guild_User_TaskInfo("guildUserTaskInfo", UserGuildTaskInfo.class), // 行会任务信息
    SXDH_FIGHTER("sxdhFighter", SxdhFighter.class), // 神仙大会玩家信息
    DFDJ_FIGHTER("dfdjFighter", DfdjFighter.class),// 巅峰对决玩家信息
    SXDH_SHOP_RECORD("sxdhShopRecord", SxdhShopRecord.class), // 神仙大会商店购买记录
    CombatVideo("CombatVideo", UserCombatVideo.class),
    BI_YOU_PALACE("byPalace", UserBYPalace.class),// 碧游宫
    BI_YOU_PALACE_LOCK_SKILL("byPalaceLockSkill", UserBYPalaceLockSkill.class),
    USER_HEXAGRAM("hexagram", UserHexagram.class),//文王64卦
    USER_PUSH("push", UserPush.class),//玩家推送功能
    USER_SHOW_CARDS("userShowCards", UserShowCard.class),//玩家展示卡信息
    FS_HEPLER("fsHepler", FsHepler.class), // 封神助手
    YeG_ELITE("yeGuaiElite", UserYeGElite.class), // 精英野怪
    Xiong_Shou_Award("xiongShouAward", UserXionShouAward.class),
    USER_COLLECT_CARD_GROUP("collectCardGroup", UserCollectCardGroup.class),
    USER_ACHIEVEMENT_INFO("achievementInfo", UserAchievementInfo.class),
    USER_WAN_XIAN("wanxian", UserWanXian.class),
    USER_ADVENTURE("adventure", UserAdventure.class), // 奇遇
    USER_SNATCH_TREASURE("snatchTreasure", UserSnatchTreasure.class),// 夺宝
    USER_SNATCH_TREASURE_BOX("snatchTreasureBox", UserSnatchTreasureBox.class),// 夺宝开箱
    USER_XIANREN_BOX("xianRenBox", UserXianRenBox.class),//仙人遗落的袋子
    USER_YOU_HUN("youHun", UserYouHun.class),//玩家游魂信息
    USER_GOD_BLESS_RECORD("godBlessRecord", UserGodBlessRecord.class),// 玩家上仙祝福记录
    USER_ATTACK_CITY_RED_NOTICE("userAttackCityRedNotice", UserAttackCityRedNotice.class),// 玩家攻城红点（三倍返利）
    USER_ATTACK_DIFFICULTY("attackDifficulty", UserAttackDifficulty.class),//攻城难度
    USER_DICE_CAPACITY("UserDiceCapacity", UserDiceCapacity.class),//体力存储罐
    USER_LEADER_CARD("UserLeaderCard", UserLeaderCard.class),//主角卡
    USER_LEADER_BEASTS("leaderBeasts", UserLeaderBeasts.class),//装配的神兽
    USER_LEADER_EQUIPMENT("leaderEquipment", UserLeaderEquipment.class),//装配的装备
    USER_LEADER_FASHION("leaderFashion", UserLeaderFashion.class),//激活的时装
    USER_LEADER_CARD_SKILL_TREE("UserLeaderCardSkillTree", UserLeaderCardSkillTree.class),//技能树
    USER_LEADER_CARD_SKILL_TREE_DETAIL("UserLeaderCardSkillTreeDetail", UserLeaderCardSkillTreeDetail.class),
    USER_HEXAGRAM_BUFF("HexagramBuff", UserHexagramBuff.class),
    USER_BAG_BUY_RECORD("userBagBuyRecord", UserBagBuyRecord.class),// 玩家背包购买记录
    USER_DICE_INFO("diceInfo", UserDiceInfo.class),// 玩家体力信息
    USER_PAY_INFO("payInfo", UserPayInfo.class),// 玩家充值附加信息
    USER_NIGHTMARE_MIXIAN("nightmareMixian", UserNightmareMiXian.class),//梦魇迷仙洞
    USER_NIGHTMARE_MIXIAN_ENEMY("nightmareMixianEnemy", UserNightmareMiXianEnemy.class),//梦魇迷仙洞
    USER_XIAN_JIA_BOX("xianJiaBox", UserXianJiaBox.class),//仙家宝库
    USER_WAR_TOKEN("warToken", UserWarToken.class),//战令
    USER_WAR_TOKEN_TASK("warTokenTask", UserWarTokenTask.class),//战令任务
    USER_LIMIT_BAG_CONDITION("limitBagCondition", UserLimitBagCondition.class),
    USER_CARD_VIGOR("cardVigor", UserCardVigor.class),
    USER_CUNZ_TASKS_INFO("cunZTaskInfo", UserCunzTasksInfo.class),
    USER_TIME_LIMIT_TASK("taskTimeLimit", UserTimeLimitTask.class),
    USER_YAOZU_INFO("yaozuInfo", UserYaoZuInfo.class),
    USER_TRANSMIGRATION("transmigration", UserTransmigration.class),
    USER_TRANSMIGRATION_CITY("transmigrationCity", UserTransmigrationCity.class),
    USER_TRANSMIGRATION_RECORD("transmigrationRecord", UserTransmigrationRecord.class),
    USER_YUXG("yuxg", UserYuXG.class),
    USER_YUXG_FUTU("fuTu", UserFuTu.class),
    USER_YUXG_FUCE("fuCe", UserFuCe.class),
    USER_YUXG_FUCE_UPSETTING("fuTuUpSetting", UserFuTuUpSetting.class),
    USER_YUXG_FUTU_INFO("fuTuInfo", UserFuTuInfo.class),
    USER_BUSINESS_GANG_INFO("businessInfo", UserBusinessGangInfo.class),
    USER_BUSINESS_GANG_TASK("businessTask", UserBusinessGangTaskInfo.class),
    USER_BUSINESS_GANG_SPECIALTY_SHIPPING_TASK("specialtyShippingTask", UserBusinessGangSpecialtyShippingTask.class),
    USER_BUSINESS_GANG_WEEKLY_TASK("weeklyTask", UserBusinessGangWeeklyTask.class),
    USER_LUCKY_BEAST("luckyBeast", UserLuckyBeast.class),
    USER_DIG_TREASURE("digTreasure", UserDigTreasure.class),
    USER_DIG_TREASURE_POS("digTreasurePos", UserDigTreasurePos.class),
    USER_KNEAD_SOIL_INFO("pinchPeopleInfo", UserPinchPeopleInfo.class),
    USER_NV_WA_TRADE_RECORD("nvWaTradeRecord", UserNvWaTradeRecord.class),
    USER_GODS_ALTAR("godsAltar", UserGodsAltarInfo.class),
    USER_NV_WA_PRICE_MODEL("nvWaPriceModel", UserNvWaPriceModel.class),
    USER_INFUSION_INFO("infusionInfo", UserInfusionInfo.class),
    USER_CARD_XIAN_JUE("cardXianJue", UserCardXianJue.class),//装配的仙诀
    USER_CARD_ZHI_BAO("cardZhiBao", UserCardZhiBao.class),//装配的至宝
    USER_YUXG_PRAY_SETTING("YuXGPraySetting", UserYuXGPraySetting.class),// 玩家玉虚宫设置


    ;
    private final String redisKey;

    private final Class<? extends UserData> entityClass;

    public static UserDataType fromRedisKey(String key) {
        for (UserDataType item : values()) {
            if (item.getRedisKey().equals(key)) {
                return item;
            }
        }
        throw CoderException.fatal("没有键值为[" + key + "]的数据类型！");
    }

    /**
     * 根据类对象，获取数据类型
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T extends UserData> UserDataType fromClass(Class<T> clazz) {
        for (UserDataType item : values()) {
            if (item.getEntityClass().equals(clazz)) {
                return item;
            }
        }
        throw CoderException.fatal("没有class为[" + clazz + "]的类型！");
    }
}
