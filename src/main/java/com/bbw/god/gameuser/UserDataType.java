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
 * ??????????????????
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
    CARD("card", UserCard.class), // ??????
    CARD_GROUP("cardGroup", UserCardGroup.class), // ????????????
    CARD_RANDOM("cardRandom", UserCardRandom.class), // ?????????????????????????????????
    SPECIAL("special", UserSpecial.class), // ??????
    SPECIAL_SETTING("specialSetting", UserSpecialSetting.class),// ??????????????????
    SPECIAL_SALE_RECORD("special_sale_record", UserSpecialSaleRecord.class),// ??????????????????
    POCKET("pocket", UserPocket.class), //????????????
    CITY("city", UserCity.class), // ??????
    NIGHT_MARE_CITY("nightMareCity", UserNightmareCity.class), // ????????????
    CITY_SETTING("citySetting", UserCitySetting.class), // ????????????
    CITY_EXP_RECORDS("cityExpRecords", UserCityExpRecords.class), // ????????????
    TREASURE("treasure", UserTreasure.class), // ??????
    TREASURE_EFFECT("treasureEffect", UserTreasureEffect.class), // ?????????????????????????????????
    TREASURE_RECORD("treasureRecord", UserTreasureRecord.class), // ????????????????????????
    MALL_RECORD("mallRecord", UserMallRecord.class), // ????????????
    MALL_SM_REFRESH("mallSMRefresh", UserMallRefreshRecord.class), // ????????????????????????
    CARD_POOL("cardPool", UserCardPool.class), // ??????????????????
    GOD("god", UserGod.class), // ??????
    CROSSING_RECORD("crossingRecord", UserCrossingRecord.class),//???????????????????????????
    @Deprecated
    DAILY_TASK("taskDaily", UserDailyTask.class), // ????????????
    DAILY_TASK_INFO("taskDailyInfo", UserDailyTaskInfo.class),// ??????????????????
    MAIN_TASK("taskMain", UserMainTask.class), // ????????????
    GROW_TASK("taskGrow", UserGrowTask.class), // ?????????????????????
    GOD_TRAINING_TASK("taskGodTraining", UserGodTrainingTask.class), // ??????????????????
    SXDH_SEASON_TASK("taskSxdhSeason", UserSxdhSeasonTask.class), // ????????????????????????
    DFDJ_SEASON_TASK("taskDfdjSeason", UserDfdjSeasonTask.class), // ????????????????????????
    ALONE_MAOU_DATA("maouAloneData", UserAloneMaouData.class), // ????????????????????????
    BOSS_MAOU_DATA("maouBossData", UserBossMaouData.class), // ????????????????????????

    ACHIEVEMENT("achievement", UserAchievement.class), // ????????????
    ACHIEVEMENT_AWARDED("achievement_awarded", UserAwardedAchievements.class), // ????????????????????????
    ACHIEVEMENT_RECENT("achievement_recent", UserRecentAchievements.class),// ???????????????????????????

    SYMBOL_INFO("symbol_info", UserSymbolInfo.class),// ????????????
    JLJ("jlj", UserJuLJInfo.class),// ????????????

    ZXZ("zxz", UserZxz.class), // ?????????
    // MAOU_RECORD("maourecord", MaouAttackDetail.class), // ???????????????
    /** ?????????:?????????????????? */
    USER_ZXZ_CARD_GROUP("userZxzCardGroup", UserZxzCardGroupInfo.class),
    /** ?????????????????????????????? */
    USER_ZXZ("userZxz", UserZxzInfo.class),
    /** ??????????????????????????????*/
    USER_ZXZ_REGION("userZxzRegion", UserZxzRegionInfo.class),
    USER_ENTRY("userEntry", UserEntryInfo.class),
    /** ???????????????????????????????????? */
    USER_PASS_CARD_GROUP("userPassCardGroup", UserPassRegionCardGroupInfo.class),
    /** ???????????????????????? */
    USER_ZXZ_FOUR_SAINTS("userZxzFourSaintsInfo",UserZxzFourSaintsInfo.class),
    /** ??????????????????????????????*/
    USER_ZXZ_FOUR_SAINTS_CARD_GROUP("userZxzFourSaintsCardGroup", UserZxzFourSaintsCardGroupInfo.class),
    MAIL("mail", UserMail.class), // ????????????
    MONSTER_HELP("monster", UserMonster.class), // ??????????????????

    TYF_CELL("tyf", UserTYFCell.class), // OLD?????????
    TYF_TURN("tyfTurn", UserTYFTurn.class), // OLD????????? - ??????????????????
    TYF_FILL_RECORD("tyfFillRecord", UserTyfFillRecord.class), // ?????????????????????
    MYTYF_FILL_RECORD("mytyfFillRecord", UserMYTyfFillRecord.class), // ???????????????????????????
    MYTYF_ROUND_SPECIAL("mytyfRoundSpecial", UserMYTyfRoundSpecial.class), // ???????????????????????????

    LIMIT("limit", UserLimit.class), // ????????????
    LOGIN_INFO("login", UserLoginInfo.class), // ??????????????????
    AskBuddy("buddyAsk", AskBuddy.class), // ????????????????????????
    FriendBuddy("buddyFriend", FriendBuddy.class), // ??????????????????
    RECEIPT("receipt", UserReceipt.class), // ??????????????????

    HELP_ABOUT("helpAbout", UserHelpAbout.class), // ??????????????????????????????

    Chamber_Of_Commerce_User_Info("cocUserInfo", UserCocInfo.class), // ??????????????????
    Chamber_Of_Commerce_User_Task_Info("cocUserTaskInfo", UserCocTaskInfo.class), // ??????????????????
    Chamber_Of_Commerce_User_ExpTask_Info("cocUserExpTaskInfo", UserCocExpTaskInfo.class), // ??????????????????

    ChanjieUserInfo("chanjieUserInfo", ChanjieUserInfo.class), // ????????????????????????
    Guild_User_Info("guildUserInfo", UserGuild.class), // ????????????
    Guild_User_TaskInfo("guildUserTaskInfo", UserGuildTaskInfo.class), // ??????????????????
    SXDH_FIGHTER("sxdhFighter", SxdhFighter.class), // ????????????????????????
    DFDJ_FIGHTER("dfdjFighter", DfdjFighter.class),// ????????????????????????
    SXDH_SHOP_RECORD("sxdhShopRecord", SxdhShopRecord.class), // ??????????????????????????????
    CombatVideo("CombatVideo", UserCombatVideo.class),
    BI_YOU_PALACE("byPalace", UserBYPalace.class),// ?????????
    BI_YOU_PALACE_LOCK_SKILL("byPalaceLockSkill", UserBYPalaceLockSkill.class),
    USER_HEXAGRAM("hexagram", UserHexagram.class),//??????64???
    USER_PUSH("push", UserPush.class),//??????????????????
    USER_SHOW_CARDS("userShowCards", UserShowCard.class),//?????????????????????
    FS_HEPLER("fsHepler", FsHepler.class), // ????????????
    YeG_ELITE("yeGuaiElite", UserYeGElite.class), // ????????????
    Xiong_Shou_Award("xiongShouAward", UserXionShouAward.class),
    USER_COLLECT_CARD_GROUP("collectCardGroup", UserCollectCardGroup.class),
    USER_ACHIEVEMENT_INFO("achievementInfo", UserAchievementInfo.class),
    USER_WAN_XIAN("wanxian", UserWanXian.class),
    USER_ADVENTURE("adventure", UserAdventure.class), // ??????
    USER_SNATCH_TREASURE("snatchTreasure", UserSnatchTreasure.class),// ??????
    USER_SNATCH_TREASURE_BOX("snatchTreasureBox", UserSnatchTreasureBox.class),// ????????????
    USER_XIANREN_BOX("xianRenBox", UserXianRenBox.class),//?????????????????????
    USER_YOU_HUN("youHun", UserYouHun.class),//??????????????????
    USER_GOD_BLESS_RECORD("godBlessRecord", UserGodBlessRecord.class),// ????????????????????????
    USER_ATTACK_CITY_RED_NOTICE("userAttackCityRedNotice", UserAttackCityRedNotice.class),// ????????????????????????????????????
    USER_ATTACK_DIFFICULTY("attackDifficulty", UserAttackDifficulty.class),//????????????
    USER_DICE_CAPACITY("UserDiceCapacity", UserDiceCapacity.class),//???????????????
    USER_LEADER_CARD("UserLeaderCard", UserLeaderCard.class),//?????????
    USER_LEADER_BEASTS("leaderBeasts", UserLeaderBeasts.class),//???????????????
    USER_LEADER_EQUIPMENT("leaderEquipment", UserLeaderEquipment.class),//???????????????
    USER_LEADER_FASHION("leaderFashion", UserLeaderFashion.class),//???????????????
    USER_LEADER_CARD_SKILL_TREE("UserLeaderCardSkillTree", UserLeaderCardSkillTree.class),//?????????
    USER_LEADER_CARD_SKILL_TREE_DETAIL("UserLeaderCardSkillTreeDetail", UserLeaderCardSkillTreeDetail.class),
    USER_HEXAGRAM_BUFF("HexagramBuff", UserHexagramBuff.class),
    USER_BAG_BUY_RECORD("userBagBuyRecord", UserBagBuyRecord.class),// ????????????????????????
    USER_DICE_INFO("diceInfo", UserDiceInfo.class),// ??????????????????
    USER_PAY_INFO("payInfo", UserPayInfo.class),// ????????????????????????
    USER_NIGHTMARE_MIXIAN("nightmareMixian", UserNightmareMiXian.class),//???????????????
    USER_NIGHTMARE_MIXIAN_ENEMY("nightmareMixianEnemy", UserNightmareMiXianEnemy.class),//???????????????
    USER_XIAN_JIA_BOX("xianJiaBox", UserXianJiaBox.class),//????????????
    USER_WAR_TOKEN("warToken", UserWarToken.class),//??????
    USER_WAR_TOKEN_TASK("warTokenTask", UserWarTokenTask.class),//????????????
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
    USER_CARD_XIAN_JUE("cardXianJue", UserCardXianJue.class),//???????????????
    USER_CARD_ZHI_BAO("cardZhiBao", UserCardZhiBao.class),//???????????????
    USER_YUXG_PRAY_SETTING("YuXGPraySetting", UserYuXGPraySetting.class),// ?????????????????????


    ;
    private final String redisKey;

    private final Class<? extends UserData> entityClass;

    public static UserDataType fromRedisKey(String key) {
        for (UserDataType item : values()) {
            if (item.getRedisKey().equals(key)) {
                return item;
            }
        }
        throw CoderException.fatal("???????????????[" + key + "]??????????????????");
    }

    /**
     * ????????????????????????????????????
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
        throw CoderException.fatal("??????class???[" + clazz + "]????????????");
    }
}
