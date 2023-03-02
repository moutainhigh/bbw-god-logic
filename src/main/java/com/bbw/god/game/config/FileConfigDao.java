package com.bbw.god.game.config;

import com.bbw.exception.CoderException;
import com.bbw.god.activity.cfg.*;
import com.bbw.god.activity.config.CfgGodBlessAward;
import com.bbw.god.activity.holiday.config.*;
import com.bbw.god.activity.holiday.lottery.CfgHolidayLotteryAwards;
import com.bbw.god.activity.holiday.lottery.CfgHolidayWQCY;
import com.bbw.god.activity.holiday.processor.holidaychinesezodiaccollision.CfgChineseZodiacConllision;
import com.bbw.god.activity.holiday.processor.holidaychristmaswish.CfgChristmasWish;
import com.bbw.god.activity.holiday.processor.holidaymagicwitch.CfgHolidayMagicWitch;
import com.bbw.god.activity.holiday.processor.holidaythankflowerlanguage.CfgThankFlowerLanguage;
import com.bbw.god.activity.holiday.processor.holidaytreasuretrove.CfgTreasureTrove;
import com.bbw.god.activity.holiday.processor.holidaytreasuretrovemap.CfgTreasureTroveMap;
import com.bbw.god.activity.monthlogin.CfgMonthLogin;
import com.bbw.god.activity.worldcup.cfg.*;
import com.bbw.god.activityrank.CfgActivityRankGenerateRule;
import com.bbw.god.city.cunz.CfgCunZTalk;
import com.bbw.god.city.mixd.nightmare.CfgNightmareMiXian;
import com.bbw.god.city.yed.CfgYeDiEventEntity;
import com.bbw.god.city.yeg.CfgYeGuai;
import com.bbw.god.city.yeg.CfgYeGuaiEntity;
import com.bbw.god.game.combat.runes.CfgRunes;
import com.bbw.god.game.combat.runes.CfgYgRunes;
import com.bbw.god.game.combat.weapon.CfgWeapon;
import com.bbw.god.game.config.card.*;
import com.bbw.god.game.config.card.equipment.randomrule.CfgCardEquipmentRandomRuleEntity;
import com.bbw.god.game.config.city.*;
import com.bbw.god.game.config.exchangegood.CfgExchangeGoodEntity;
import com.bbw.god.game.config.mall.*;
import com.bbw.god.game.config.special.CfgAutoBuyHolidayProps;
import com.bbw.god.game.config.special.CfgSpecial;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.CfgSpecialHierarchyMap;
import com.bbw.god.game.config.treasure.*;
import com.bbw.god.game.dfdj.config.CfgDfdj;
import com.bbw.god.game.dfdj.config.CfgDfdjRankAward;
import com.bbw.god.game.dfdj.config.CfgDfdjSegment;
import com.bbw.god.game.maou.cfg.CfgGameMaou;
import com.bbw.god.game.sxdh.config.CfgSxdh;
import com.bbw.god.game.sxdh.config.CfgSxdhRankAward;
import com.bbw.god.game.sxdh.config.CfgSxdhSegment;
import com.bbw.god.game.transmigration.cfg.CfgTransmigration;
import com.bbw.god.game.wanxianzhen.CfgWanXian;
import com.bbw.god.game.wanxianzhen.CfgWanXianBox;
import com.bbw.god.game.wanxianzhen.CfgWanXianRobot;
import com.bbw.god.game.zxz.cfg.CfgLingZhuangEntryEntity;
import com.bbw.god.game.zxz.cfg.CfgZxzEntity;
import com.bbw.god.game.zxz.cfg.CfgZxzEntryEntity;
import com.bbw.god.game.zxz.cfg.award.CfgZxzAwardEntity;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsEntity;
import com.bbw.god.gameuser.CfgShakeProp;
import com.bbw.god.gameuser.achievement.CfgAchievement;
import com.bbw.god.gameuser.achievement.CfgAchievementEntity;
import com.bbw.god.gameuser.biyoupalace.cfg.*;
import com.bbw.god.gameuser.businessgang.cfg.CfgBusinessGangEntity;
import com.bbw.god.gameuser.businessgang.cfg.CfgBusinessGangShippingTaskRules;
import com.bbw.god.gameuser.businessgang.cfg.CfgGiftEntity;
import com.bbw.god.gameuser.businessgang.cfg.CfgPrestigeEntity;
import com.bbw.god.gameuser.businessgang.digfortreasure.CfgDigTreasure;
import com.bbw.god.gameuser.businessgang.luckybeast.CfgLuckyBeast;
import com.bbw.god.gameuser.card.equipment.cfg.CfgXianJue;
import com.bbw.god.gameuser.card.juling.CfgJuLing;
import com.bbw.god.gameuser.kunls.cfg.CfgKunLS;
import com.bbw.god.gameuser.leadercard.CfgLeaderCard;
import com.bbw.god.gameuser.leadercard.beast.CfgBeastSkillEntity;
import com.bbw.god.gameuser.leadercard.equipment.CfgEquipment;
import com.bbw.god.gameuser.leadercard.skil.CfgLeaderCardSkill;
import com.bbw.god.gameuser.nightmarenvwam.cfg.CfgNightmareNvmEntity;
import com.bbw.god.gameuser.nightmarenvwam.cfg.CfgNightmareNvmOutputEntity;
import com.bbw.god.gameuser.privilege.CfgPrivilege;
import com.bbw.god.gameuser.task.CfgTaskConfig;
import com.bbw.god.gameuser.task.timelimit.CfgTimeLimitTaskRules;
import com.bbw.god.gameuser.treasure.xianrenbox.CfgXianRenBox;
import com.bbw.god.gameuser.yaozu.CfgYaoZuEntity;
import com.bbw.god.gameuser.yuxg.cfg.CfgFuTuEntity;
import com.bbw.god.gameuser.yuxg.cfg.CfgYuSuiEntity;
import com.bbw.god.gameuser.yuxg.cfg.CfgYuXGEntity;
import com.bbw.god.gameuser.yuxg.cfg.CfgYuXGExceptFuTuEntity;
import com.bbw.god.mall.lottery.CfgLotteryAward;
import com.bbw.god.mall.skillscroll.cfg.CfgDesignateSkillScroll;
import com.bbw.god.mall.skillscroll.cfg.CfgSkillScroll;
import com.bbw.god.mall.snatchtreasure.CfgSnatchTreasureAwards;
import com.bbw.god.mall.snatchtreasure.CfgSnatchTreasureBox;
import com.bbw.god.mall.snatchtreasure.CfgSnatchTreasureCard;
import com.bbw.god.random.box.BoxGoods;
import com.bbw.god.random.config.RandomStrategy;
import com.bbw.god.rechargeactivities.wartoken.CfgWarTokenBigAwards;
import com.bbw.god.rechargeactivities.wartoken.CfgWarTokenLevelAward;
import com.bbw.god.rechargeactivities.wartoken.CfgWarTokenTask;
import com.bbw.god.server.maou.bossmaou.auction.CfgMaouAuction;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;

/**
 * 文件配置的读取类
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-04 14:33
 */
@Slf4j
@Service
public class FileConfigDao {
    private static HashMap<Class<? extends CfgInterface>, String> dirMap = new HashMap<>();// 同类配置到目录
    private static final String YML_SUFFIX = ".yml";

    static {

        // 游戏全局配置
        dirMap.put(CfgGame.class, "config/game/游戏全局相关配置.yml");

        dirMap.put(CfgRandomName.class, "config/game/name/名字字典.yml");
        // 区服组配置
        dirMap.put(CfgServerGroup.class, "config/game/servergroup");
        // 渠道映射
        dirMap.put(CfgProductChannelMap.class, "config/game/product/channelmap");

        ////////////////////// 地图配置//////////////////////////////
        // 格子配置
        dirMap.put(CfgRoadEntity.class, "config/game/city/格子基础数据.yml");
        // 城市配置
        dirMap.put(CfgCityEntity.class, "config/game/city/城市基础数据.yml");
        // 城池配置
        dirMap.put(CfgChengC.class, "config/game/city/cheng_chi/城池.yml");
        //进阶阵容
        dirMap.put(CfgPromoteEntity.class, "config/game/city/cheng_chi/城池振兴阵容.yml");
        // 梦魇城池配置
        dirMap.put(CfgNightmareChengC.class, "config/game/city/cheng_chi/梦魇城池.yml");
        // 炼宝炉配置
        dirMap.put(CfgLianBL.class, "config/game/city/cheng_chi/in/炼宝炉.yml");
        // 黑市配置
        dirMap.put(CfgHeiS.class, "config/game/city/黑市.yml");
        // 野地配置
        dirMap.put(CfgYeDiEventEntity.class, "config/game/city/野地事件基础数据.yml");
        // 野怪配置
        dirMap.put(CfgYeGuai.class, "config/game/city/野怪.yml");
        dirMap.put(CfgYeGuaiEntity.class, "config/game/city/野怪对手基础数据.yml");

        ////////////////////// 资源道具商品配置 //////////////////////////////
        // 卡牌配置
        dirMap.put(CfgCard.class, "config/game/card/卡牌.yml");
        dirMap.put(CfgCardEntity.class, "config/game/card/卡牌基础数据.yml");
        dirMap.put(CfgDeifyCardEntity.class, "config/game/card/卡牌封神数据.yml");
        dirMap.put(CfgHideCard.class, "config/game/card/隐藏卡牌数据.yml");
        dirMap.put(CfgFlockStarBook.class, "config/game/card/卡牌群星册.yml");
        // 卡牌技能配置
        dirMap.put(CfgCardSkill.class, "config/game/combat/卡牌技能.yml");
        // 卡牌组合配置
        dirMap.put(CfgCardGroup.class, "config/game/card/卡牌组合.yml");
        // 选卡策略配置
        dirMap.put(RandomStrategy.class, "config/game/card/random");
        // 聚灵
        dirMap.put(CfgJuLing.class, "config/game/card/聚灵.yml");
        //卡牌装备随机规则
        dirMap.put(CfgCardEquipmentRandomRuleEntity.class, "config/game/combat/随机规则");
        // 法宝配置
        dirMap.put(CfgTreasure.class, "config/game/treasure/法宝.yml");
        dirMap.put(CfgTreasureEntity.class, "config/game/treasure/法宝基础数据.yml");
        dirMap.put(CfgSkillScrollLimitEntity.class, "config/game/treasure/技能卷轴限制配置数据.yml");
        dirMap.put(CfgDeifyToken.class, "config/game/treasure/封神令配置.yml");
        dirMap.put(CfgDeifysToken.class, "config/game/treasure/群体封神令配置.yml");
        // 特产配置
        dirMap.put(CfgSpecial.class, "config/game/special/特产.yml");
        dirMap.put(CfgSpecialEntity.class, "config/game/special/特产基础数据.yml");
        dirMap.put(CfgAutoBuyHolidayProps.class, "config/game/special/自动购买节日道具特产.yml");
        dirMap.put(CfgSpecialHierarchyMap.class, "config/game/special/特产阶级对应.yml");
        // 商城
        dirMap.put(CfgMallEntity.class, "config/game/mall/商城物品基础数据.yml");
        dirMap.put(CfgMall.class, "config/game/mall/商城.yml");
        dirMap.put(CfgMaouMallAuth.class, "config/game/mall/魔王商品权限配置.yml");
        dirMap.put(CfgMallExtraPackEntity.class, "config/game/mall/商城额外自选礼包.yml");
        // 兑换物品
        dirMap.put(CfgExchangeGoodEntity.class, "config/game/exchange_good/兑换品基础数据.yml");
        // 充值产品
        dirMap.put(CfgProductGroup.class, "config/game/product/products");
        dirMap.put(CfgUSAIps.class, "config/game/product/usaip/美国IP地址段.yml");
        // 箱子、礼包配置
        dirMap.put(BoxGoods.class, "config/game/box");

        ////////////////////// 成就任务配置//////////////////////////////
        // 成就
        dirMap.put(CfgAchievementEntity.class, "config/game/achievement/成就基础数据.yml");
        dirMap.put(CfgAchievement.class, "config/game/achievement/成就配置.yml");
        // 任务
        dirMap.put(CfgTaskConfig.class, "config/game/task");
        dirMap.put(CfgTimeLimitTaskRules.class, "config/game/task_time_limit");
        ////////////////////// 功能配置//////////////////////////////
        // 封神台配置
        dirMap.put(CfgFst.class, "config/game/fst");
        // 魔王配置
        dirMap.put(CfgAloneMaou.class, "config/game/mowang/独战魔王.yml");
        dirMap.put(CfgBossMaou.class, "config/game/mowang/魔王boss.yml");
        dirMap.put(CfgGameMaou.class, "config/game/mowang/game");
        // 诛仙阵配置
        dirMap.put(CfgZxzEntity.class, "config/game/zxz/诛仙阵.yml");
        dirMap.put(CfgZxzEntryEntity.class, "config/game/zxz/词条.yml");
        dirMap.put(CfgZxzAwardEntity.class,"config/game/zxz/诛仙阵奖励配置.yml");
        dirMap.put(CfgLingZhuangEntryEntity.class,"config/game/zxz/灵装词条.yml");
        dirMap.put(CfgFourSaintsEntity.class,"config/game/zxz/四圣挑战/四圣挑战.yml");
        // 友怪配置
        dirMap.put(CfgMonster.class, "config/game/monster");
        // 用户帮助存在阅读奖励的配置
        dirMap.put(CfgHelpAbout.class, "config/game/helpabout");
        // 商会相关配置
        dirMap.put(CfgCoc.class, "config/game/chamberofcommerce");
        // 许愿池配置
        dirMap.put(CfgWishCard.class, "config/game/wishcard");
        // 行会配置
        dirMap.put(CfgGuild.class, "config/game/guild");
        // 阐截斗法配置
        dirMap.put(CfgChanjie.class, "config/game/chanjie");
        // 神仙大会配置
        dirMap.put(CfgSxdh.class, "config/game/sxdh/神仙大会.yml");
        dirMap.put(CfgSxdhRankAward.class, "config/game/sxdh/神仙大会排名奖励.yml");
        dirMap.put(CfgSxdhSegment.class, "config/game/sxdh/神仙大会段位.yml");
        // 巅峰对决
        dirMap.put(CfgDfdj.class, "config/game/dfdj/巅峰对决.yml");
        dirMap.put(CfgDfdjRankAward.class, "config/game/dfdj/巅峰对决排名奖励.yml");
        dirMap.put(CfgDfdjSegment.class, "config/game/dfdj/巅峰对决段位.yml");
        // 特权配置
        dirMap.put(CfgPrivilege.class, "config/game/privilege/特权.yml");
        // 碧游宫
        dirMap.put(CfgBYPalace.class, "config/game/bi_you_palace/碧游宫.yml");
        dirMap.put(CfgBYPalaceSymbolEntity.class, "config/game/bi_you_palace/碧游宫符箓配置.yml");
        dirMap.put(CfgBYPalaceSkillEntity.class, "config/game/bi_you_palace/碧游宫技能配置.yml");
        dirMap.put(CfgBYPalaceWeightEntity.class, "config/game/bi_you_palace/碧游宫权重配置.yml");
        dirMap.put(CfgBYPalaceConditionEntity.class, "config/game/bi_you_palace/碧游宫条件组合.yml");
        dirMap.put(CfgBYPalaceChapterEntity.class, "config/game/bi_you_palace/碧游宫篇配置.yml");
        dirMap.put(CfgActivityRankGenerateRule.class, "config/game/activity_rank/榜单生成规则.yml");
        // 万仙阵
        dirMap.put(CfgWanXian.class, "config/game/wanxian/万仙阵配置.yml");
        dirMap.put(CfgWanXianBox.class, "config/game/wanxian/万仙阵宝箱.yml");
        //万仙阵机器人
        dirMap.put(CfgWanXianRobot.class, "config/game/wanxian/robot");
        // 夺宝
        dirMap.put(CfgSnatchTreasureAwards.class, "config/game/mall/snatchtreasure/夺宝奖励.yml");
        dirMap.put(CfgSnatchTreasureCard.class, "config/game/mall/snatchtreasure/夺宝卡牌.yml");
        dirMap.put(CfgSnatchTreasureBox.class, "config/game/mall/snatchtreasure/夺宝宝箱.yml");
        dirMap.put(CfgSnatchTreasureMallCondition.class, "config/game/mall/snatchtreasure/夺宝商品配置.yml");
        // 奖券
        dirMap.put(CfgLotteryAward.class, "config/game/mall/lottery/奖券奖励.yml");
        // 拍卖
        dirMap.put(CfgMaouAuction.class, "config/game/auction/魔王拍卖商品配置.yml");
        dirMap.put(CfgXianRenBox.class, "config/game/treasure/仙人遗落的袋子.yml");
        // 节日
        dirMap.put(CfgHolidayTaskEntity.class, "config/game/activity/节日任务.yml");
        dirMap.put(CfgTreasureTrove.class, "config/game/activity/藏宝秘境活动.yml");
        dirMap.put(CfgTreasureTroveMap.class, "config/game/activity/寻藏宝图活动.yml");
        dirMap.put(CfgHolidayLotteryAwards.class, "config/game/activity/节日抽奖.yml");
        dirMap.put(CfgHolidayWQCY.class, "config/game/activity/五气朝元配置.yml");
        dirMap.put(CfgShakeProp.class, "config/game/shake");
        dirMap.put(CfgGodBlessAward.class, "config/game/activity/仙人祝福奖励.yml");
        dirMap.put(CfgHolidayAbleChooseAwards.class, "config/game/activity/节日可选择奖励.yml");
        dirMap.put(CfgHolidayCunZEventAwardEntity.class, "config/game/activity/节日拜访村庄事件奖励.yml");
        //梦魇世界-符文
        dirMap.put(CfgRunes.class, "config/game/combat/护身符配置.yml");
        dirMap.put(CfgWeapon.class, "config/game/combat/战斗法宝.yml");
        dirMap.put(CfgYgRunes.class, "config/game/combat/野怪符文配置.yml");
        // 技能卷轴合成
        dirMap.put(CfgSkillScroll.class, "config/game/mall/skillscroll/卷轴合成配置.yml");
        dirMap.put(CfgDesignateSkillScroll.class, "config/game/mall/skillscroll/卷轴指定合成范围配置.yml");
        dirMap.put(CfgMonthLogin.class, "config/game/monthlogin/每日签到事件.yml");
        //地图挖宝
        dirMap.put(CfgDigForTreasure.class, "config/game/city/地图挖宝.yml");
        //主角卡
        dirMap.put(CfgLeaderCard.class, "config/game/leadercard/主角卡初始化配置.yml");
        dirMap.put(CfgEquipment.class, "config/game/leadercard/装备属性配置.yml");
        dirMap.put(CfgBeastSkillEntity.class, "config/game/leadercard/神兽技能配置.yml");
        dirMap.put(CfgLeaderCardSkill.class, "config/game/leadercard/技能树配置.yml");
        //梦魇迷仙洞
        dirMap.put(CfgNightmareMiXian.class, "config/game/city/梦魇迷仙洞.yml");
        dirMap.put(CfgNightmareMiXian.class, "config/game/city/梦魇迷仙洞.yml");

        dirMap.put(CfgMxdZhenShou.class, "config/game/treasure/迷仙洞阵守礼包.yml");
        dirMap.put(CfgXianJia.class, "config/game/treasure/仙家宝袋.yml");

        //战令配置
        dirMap.put(CfgWarTokenLevelAward.class, "config/game/activity/war_token/战令等级奖励配置.yml");
        dirMap.put(CfgWarTokenTask.class, "config/game/activity/war_token/战令任务配置.yml");

        //妖族来犯
        dirMap.put(CfgYaoZuEntity.class, "config/game/yaozu/妖族来犯.yml");
        //轮回世界
        dirMap.put(CfgTransmigration.class, "config/game/transmigration/轮回世界.yml");
        //神仙可能刷新的位置数据
        dirMap.put(CfgGodRoadEntity.class, "config/game/city/神仙生成特殊位置数据.yml");
        //玉虚宫
        dirMap.put(CfgYuXGEntity.class, "config/game/yuxg/玉虚宫基础配置.yml");
        //符图配置
        dirMap.put(CfgFuTuEntity.class, "config/game/yuxg/符图数据配置.yml");
        //玉髓配置
        dirMap.put(CfgYuSuiEntity.class, "config/game/yuxg/玉髓数据配置.yml");
        //碧游宫专属技能
        dirMap.put(CfgYuXGExceptFuTuEntity.class, "config/game/yuxg/玉虚宫除外符图.yml");
        //法坛配置
        dirMap.put(CfgFaTanEntity.class, "config/game/city/cheng_chi/in/法坛.yml");
        //感恩节配置
        dirMap.put(CfgThanksGiving.class, "config/game/activity/感恩节活动.yml");
        //坊间怪谈
        dirMap.put(CfgCunZTalk.class, "config/game/cunz/坊间怪谈.yml");
        //限时道具配置
        dirMap.put(CfgTimeLimitTreasureEntity.class, "config/game/treasure/限时道具.yml");
        dirMap.put(CfgLuckyBeast.class, "config/game/businessgang/招财兽.yml");
        dirMap.put(CfgDigTreasure.class, "config/game/businessgang/挖宝.yml");
        //商帮基础配置
        dirMap.put(CfgBusinessGangEntity.class, "config/game/businessgang/商帮基础配置.yml");
        //礼物基础数据配置
        dirMap.put(CfgGiftEntity.class, "config/game/businessgang/礼物数据配置.yml");
        //商帮运送任务规则
        dirMap.put(CfgBusinessGangShippingTaskRules.class, "config/game/businessgang/商帮运送任务规则.yml");
        //商帮声望配置
        dirMap.put(CfgPrestigeEntity.class, "config/game/businessgang/商帮声望配置.yml");
        //元宵节祈福天灯配置
        dirMap.put(CfgPrayerSkyLanternConfig.class, "config/game/activity/祈福天灯活动.yml");
        //锦礼配置
        dirMap.put(CfgBrocadeGiftConfig.class, "config/game/activity/锦礼活动.yml");
        //战令大奖配置
        dirMap.put(CfgWarTokenBigAwards.class, "config/game/activity/war_token/战令大奖配置.yml");
        //萌虎集市配置
        dirMap.put(CfgCuteTigerEntity.class, "config/game/activity/萌虎集市.yml");
        //小虎商城配置
        dirMap.put(CfgLittleTigerStoreEntity.class, "config/game/activity/小虎商店.yml");
        //梦魇女娲庙
        dirMap.put(CfgNightmareNvmEntity.class, "config/game/city/nightmarenvwam/梦魇女娲庙.yml");
        //梦魇女娲庙产出
        dirMap.put(CfgNightmareNvmOutputEntity.class, "config/game/city/nightmarenvwam/梦魇女娲庙产出道具.yml");
        //特殊封神卡
        dirMap.put(CfgSpecialGodCardEntity.class, "config/game/card/特殊封神卡.yml");
        //碧游宫专属技能
        dirMap.put(CfgBYPalaceExclusiveSkillEntity.class, "config/game/bi_you_palace/碧游宫专属技能.yml");
        //摇一摇
        dirMap.put(CfgDailyShake.class, "config/game/activity/每日摇一摇.yml");
        dirMap.put(CfgHolidaySummerHeat.class, "config/game/activity/暑气来袭.yml");
        dirMap.put(CfgHolidayLaborGlorious.class, "config/game/activity/劳动光荣.yml");
        dirMap.put(CfgKunLS.class, "config/game/kunls/昆仑山.yml");
        dirMap.put(CfgXianJue.class, "config/game/card/卡牌仙诀.yml");
        dirMap.put(CfgHolidayKoiPray.class, "config/game/activity/锦鲤祈愿.yml");
        dirMap.put(CfgHolidayTreatOrTrick.class, "config/game/activity/不给糖就捣乱2.yml");
        dirMap.put(CfgHalloweenRestaurant.class, "config/game/activity/万圣节餐厅.yml");
        //世界杯 - 绿茵活动
        dirMap.put(CfgSuper16.class, "config/game/activity/绿茵活动/超级16强.yml");
        dirMap.put(CfgDroiyan8.class, "config/game/activity/绿茵活动/决战8强.yml");
        dirMap.put(CfgProphet.class, "config/game/activity/绿茵活动/我是预言家.yml");
        dirMap.put(CfgQuizKing.class, "config/game/activity/绿茵活动/我是竞猜王.yml");
        dirMap.put(CfgCountry.class, "config/game/activity/绿茵活动/世界杯国家配置.yml");
        //感恩花语
        dirMap.put(CfgThankFlowerLanguage.class, "config/game/activity/感恩花语.yml");
        //杂货小铺
        dirMap.put(CfgGroceryShop.class, "config/game/activity/杂货小铺.yml");
        //魔法女巫
        dirMap.put(CfgHolidayMagicWitch.class, "config/game/activity/魔法女巫.yml");
        //圣诞心愿
        dirMap.put(CfgChristmasWish.class, "config/game/activity/圣诞心愿.yml");
        //生肖对碰
        dirMap.put(CfgChineseZodiacConllision.class, "config/game/activity/生肖对碰.yml");
    }

    /**
     * @return the dirMap
     */
    public static HashMap<Class<? extends CfgInterface>, String> getDirMap() {
        return dirMap;
    }

    /**
     * 根据文件目录获取配置类型
     *
     * @param fileDir
     * @return
     */
    public static Class<? extends CfgInterface> getDirClass(String fileDir, String parentDir) {
        for (Iterator<Entry<Class<? extends CfgInterface>, String>> iterator = dirMap.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<Class<? extends CfgInterface>, String> e = iterator.next();
            // 目录相同
            if (fileDir.endsWith(e.getValue())) {
                return e.getKey();
            }
        }
        for (Iterator<Entry<Class<? extends CfgInterface>, String>> iterator = dirMap.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<Class<? extends CfgInterface>, String> e = iterator.next();
            // 父目录相同
            if (parentDir.endsWith(e.getValue())) {
                return e.getKey();
            }
        }
        return null;
    }

    /**
     * 是否是文件配置
     *
     * @param clazz
     * @return
     */
    public static boolean isFileConfig(Class<?> clazz) {
        return dirMap.containsKey(clazz);
    }

    /**
     * 获取所有配置
     */
    public static <T extends CfgInterface> List<T> getByType(Class<T> clazz) {
        String dir = dirMap.get(clazz);
        URL url = FileConfigDao.class.getClassLoader().getResource(dir);
        // System.out.println(url.getFile());
        // System.out.println(toUTF(url.getFile()));
        File directory = new File(toUTF8(url.getFile()));
        // 是否是yml文件
        if (isYml(directory)) {
            List<?> clazzs = Arrays.asList(clazz.getInterfaces());
            if (clazzs.contains(CfgEntityInterface.class)) {
                return getEntitiesByType(directory, clazz);
            }
            T obj = getByType(directory, clazz);
            return Arrays.asList(obj);
        }
        File[] fileArray = directory.listFiles();
        // 空路径
        if (null == fileArray) {
            return new ArrayList<>();
        }
        // 是否包含子目录
        for (File file : fileArray) {
            if (file.isDirectory()) {
                throw CoderException.high("无效的配置路径" + dir + ",类型" + clazz.getName());
            }
        }
        // 加载多个配置文件
        Set<Serializable> ids = new HashSet<>();
        List<T> list = new ArrayList<>();
        for (File file : fileArray) {
            if (file.getName().endsWith(".yml")) {
                T obj = getByType(file, clazz);
                if (ids.contains(obj.getId())) {
                    throw CoderException.fatal("[" + clazz.getSimpleName() + "]配置[" + obj.getId() + "]覆盖！");
                }
                ids.add(obj.getId());
                list.add(obj);
            }
        }
        return list;
    }

    /**
     * 将yml文件转换为对象
     *
     * @param file
     * @param clazz
     * @return
     */
    private static <T extends CfgInterface> T getByType(File file, Class<T> clazz) {
        // System.out.println(file.getName());
        // System.out.println(file.getPath());
        if (file.isDirectory()) {
            throw CoderException.high("无效的配置路径" + file.getName() + ",配置类" + clazz.getName());
        }
        Yaml yaml = new Yaml(new Constructor(clazz));
        try {
            @Cleanup
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            @Cleanup
            BufferedReader read = new BufferedReader(isr);
            T obj = yaml.load(read);
            return obj;
        } catch (Exception e) {
            log.error("加载[{}]出错了", file.getName());
            throw CoderException.high(e.getMessage());
        }
    }

    private static <T extends CfgInterface> List<T> getEntitiesByType(File file, Class<T> clazz) {
        // System.out.println(file.getName());
        // System.out.println(file.getPath());
        if (file.isDirectory()) {
            throw CoderException.high("无效的配置路径" + file.getName() + ",配置类" + clazz.getName());
        }
        Yaml yaml = new Yaml(new Constructor(clazz));
        try {
            @Cleanup
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            @Cleanup
            BufferedReader read = new BufferedReader(isr);
            Iterable<Object> objs = yaml.loadAll(read);
            List<T> entities = new ArrayList<>();
            for (Object object : objs) {
                T t = (T) object;
                entities.add(t);
            }
            return entities;
        } catch (Exception e) {
            log.error("加载[{}]出错了", file.getName());
            throw CoderException.high(e.getMessage());
        }
    }

    /**
     * 是否是yml文件
     *
     * @param file
     * @return
     */
    private static boolean isYml(File file) {
        if (!file.isDirectory() && file.getName().endsWith(YML_SUFFIX)) {
            return true;
        }

        return false;
    }

    /**
     * utf8编码，以便支持中文文件名
     *
     * @param str
     * @return
     */
    private static String toUTF8(String str) {
        try {
            String utfStr = URLDecoder.decode(str, "UTF-8");
            return utfStr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }
}
