package com.bbw.god.gameuser.statistic;

import com.bbw.exception.CoderException;
import com.bbw.god.gameuser.statistic.behavior.Transmigration.TransmigrationStatistic;
import com.bbw.god.gameuser.statistic.behavior.biyou.BiYouStatistic;
import com.bbw.god.gameuser.statistic.behavior.box.OpenDailyTaskBoxStatistic;
import com.bbw.god.gameuser.statistic.behavior.box.OpenGuildBoxStatistic;
import com.bbw.god.gameuser.statistic.behavior.building.BuildingAwardStatistic;
import com.bbw.god.gameuser.statistic.behavior.businessgang.BusinessGangStatistic;
import com.bbw.god.gameuser.statistic.behavior.card.DrawCardStatistic;
import com.bbw.god.gameuser.statistic.behavior.card.HierarchyCardStatistic;
import com.bbw.god.gameuser.statistic.behavior.changeworld.ChangeWorldStatistic;
import com.bbw.god.gameuser.statistic.behavior.chanjie.ChanJieStatistic;
import com.bbw.god.gameuser.statistic.behavior.dfdj.DfdjStatistic;
import com.bbw.god.gameuser.statistic.behavior.fatan.FaTanStatistic;
import com.bbw.god.gameuser.statistic.behavior.fight.FightStatistic;
import com.bbw.god.gameuser.statistic.behavior.fight.fightdetail.FightDetailStatistic;
import com.bbw.god.gameuser.statistic.behavior.flx.FlxStatistic;
import com.bbw.god.gameuser.statistic.behavior.fst.FstStatistic;
import com.bbw.god.gameuser.statistic.behavior.god.GodStatistic;
import com.bbw.god.gameuser.statistic.behavior.juling.JuLingStatistic;
import com.bbw.god.gameuser.statistic.behavior.leader.LeaderCardSkillTreeStatistic;
import com.bbw.god.gameuser.statistic.behavior.leader.equipment.LeaderEquipmentStatistic;
import com.bbw.god.gameuser.statistic.behavior.login.LoginStatistic;
import com.bbw.god.gameuser.statistic.behavior.maou.AloneMaouStatistic;
import com.bbw.god.gameuser.statistic.behavior.miaoy.MiaoYStatistic;
import com.bbw.god.gameuser.statistic.behavior.miaoy.hexagram.HexagramStatistic;
import com.bbw.god.gameuser.statistic.behavior.mixd.NightmareMiXDStatistic;
import com.bbw.god.gameuser.statistic.behavior.move.MoveStatistic;
import com.bbw.god.gameuser.statistic.behavior.nvwm.NvwmStatistic;
import com.bbw.god.gameuser.statistic.behavior.nvwm.pinchpeople.PinchPeopleStatistic;
import com.bbw.god.gameuser.statistic.behavior.randomevent.RandomEventStatistic;
import com.bbw.god.gameuser.statistic.behavior.recharge.RechargeStatistic;
import com.bbw.god.gameuser.statistic.behavior.snatchtreasure.SnatchTreasureStatistic;
import com.bbw.god.gameuser.statistic.behavior.sxdh.SxdhStatistic;
import com.bbw.god.gameuser.statistic.behavior.task.CocTaskStatistic;
import com.bbw.god.gameuser.statistic.behavior.task.CunZTaskStatistic;
import com.bbw.god.gameuser.statistic.behavior.task.GuildTaskStatistic;
import com.bbw.god.gameuser.statistic.behavior.yaozu.YaoZuStatistic;
import com.bbw.god.gameuser.statistic.behavior.yuxg.YuXGStatistic;
import com.bbw.god.gameuser.statistic.behavior.zhibao.ZhiBaoStatistic;
import com.bbw.god.gameuser.statistic.resource.card.CardStatistic;
import com.bbw.god.gameuser.statistic.resource.city.CityStatistic;
import com.bbw.god.gameuser.statistic.resource.copper.CopperStatistic;
import com.bbw.god.gameuser.statistic.resource.dice.DiceStatistic;
import com.bbw.god.gameuser.statistic.resource.ele.EleStatistic;
import com.bbw.god.gameuser.statistic.resource.friend.FriendStatistic;
import com.bbw.god.gameuser.statistic.resource.gold.GoldStatistic;
import com.bbw.god.gameuser.statistic.resource.nightmarecity.NightmareCityStatistic;
import com.bbw.god.gameuser.statistic.resource.special.SpecialStatistic;
import com.bbw.god.gameuser.statistic.resource.treasure.TreasureStatistic;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suchaobin
 * @description 统计类数据类型枚举，主要做一个映射，避免直接存类路劲到数据库导致的类迁移成本
 * @date 2020/5/19 9:08
 **/
@AllArgsConstructor
@Getter
public enum StatisticDataType {
    CARD_RES_STATISTIC(10, CardStatistic.class.getTypeName()),
    CITY_RES_STATISTIC(20, CityStatistic.class.getTypeName()),
    COPPER_RES_STATISTIC(30, CopperStatistic.class.getTypeName()),
    ELE_RES_STATISTIC(40, EleStatistic.class.getTypeName()),
    FRIEND_RES_STATISTIC(50, FriendStatistic.class.getTypeName()),
    GOLD_RES_STATISTIC(60, GoldStatistic.class.getTypeName()),
    SPECIAL_RES_STATISTIC(70, SpecialStatistic.class.getTypeName()),
    TREASURE_RES_STATISTIC(80, TreasureStatistic.class.getTypeName()),
    OPEN_DAILY_TASK_BOX_STATISTIC(90, OpenDailyTaskBoxStatistic.class.getTypeName()),
    OPEN_GUILD_BOX_STATISTIC(100, OpenGuildBoxStatistic.class.getTypeName()),
    DRAW_CARD_STATISTIC(110, DrawCardStatistic.class.getTypeName()),
    HIERARCHY_CARD_STATISTIC(120, HierarchyCardStatistic.class.getTypeName()),
    CHAN_JIE_STATISTIC(130, ChanJieStatistic.class.getTypeName()),
    FIGHT_STATISTIC(140, FightStatistic.class.getTypeName()),
    FLX_STATISTIC(150, FlxStatistic.class.getTypeName()),
    GOD_STATISTIC(160, GodStatistic.class.getTypeName()),
    JU_LING_STATISTIC(170, JuLingStatistic.class.getTypeName()),
    LOGIN_STATISTIC(180, LoginStatistic.class.getTypeName()),
    ALONE_MAOU_STATISTIC(190, AloneMaouStatistic.class.getTypeName()),
    MIAO_YU_STATISTIC(200, MiaoYStatistic.class.getTypeName()),
    MOVE_STATISTIC(210, MoveStatistic.class.getTypeName()),
    NVWM_STATISTIC(220, NvwmStatistic.class.getTypeName()),
    RANDOM_EVENT_STATISTIC(230, RandomEventStatistic.class.getTypeName()),
    SXDH_STATISTIC(240, SxdhStatistic.class.getTypeName()),
    COC_TASK_STATISTIC(250, CocTaskStatistic.class.getTypeName()),
    RECHARGE_STATISTIC(260, RechargeStatistic.class.getTypeName()),
    SNATCH_TREASURE_STATISTIC(270, SnatchTreasureStatistic.class.getTypeName()),
    NIGHTMARE_CITY_RES_STATISTIC(280, NightmareCityStatistic.class.getTypeName()),
    CHANGE_WORLD_STATISTIC(290, ChangeWorldStatistic.class.getTypeName()),
    DICE_STATISTIC(300, DiceStatistic.class.getTypeName()),
    BUILDING_STATISTIC(310, BuildingAwardStatistic.class.getTypeName()),
    GUILD_TASK_STATISTIC(320, GuildTaskStatistic.class.getTypeName()),
    DFDJ_STATISTIC(330, DfdjStatistic.class.getTypeName()),
    BI_YOU_STATISTIC(340, BiYouStatistic.class.getTypeName()),
    FIGHT_DETAIL_STATISTIC(350, FightDetailStatistic.class.getTypeName()),
    LEADER_EQUIPMENT_STATISTIC(360, LeaderEquipmentStatistic.class.getTypeName()),
    LEADER_CARD_SKILL_TREE_STATISTIC(370, LeaderCardSkillTreeStatistic.class.getTypeName()),
    HEXAGRAM_STATISTIC(380, HexagramStatistic.class.getTypeName()),
    NIGHTMARE_MI_XD_STATISTIC(390, NightmareMiXDStatistic.class.getTypeName()),
    FST_STATISTIC(400, FstStatistic.class.getTypeName()),
    CUNZ_STATISTIC(410, CunZTaskStatistic.class.getTypeName()),
    YAOZU_STATISTIC(420, YaoZuStatistic.class.getTypeName()),
    TRANSMIGRATION_STATISTIC(430, TransmigrationStatistic.class.getTypeName()),
    FATAN_STATISTIC(440, FaTanStatistic.class.getTypeName()),
    YUXG_STATISTIC(450, YuXGStatistic.class.getTypeName()),
    BUSINESS_STATISTIC(460, BusinessGangStatistic.class.getTypeName()),
    PINCH_PEOPLE_STATISTIC(470, PinchPeopleStatistic.class.getTypeName()),
    ZHI_BAO_STATISTIC(480, ZhiBaoStatistic.class.getTypeName()),

    ;
    private final int value;
    private final String clazz;

    public static StatisticDataType fromValue(int value) {
        StatisticDataType[] dataTypes = values();
        for (StatisticDataType dataType : dataTypes) {
            if (dataType.getValue() == value) {
                return dataType;
            }
        }
        throw CoderException.high(String.format("找不到value=%s的枚举", value));
    }

    public static StatisticDataType fromClazz(String clazz) {
        StatisticDataType[] dataTypes = values();
        for (StatisticDataType dataType : dataTypes) {
            if (dataType.getClazz().equals(clazz)) {
                return dataType;
            }
        }
        throw CoderException.high(String.format("找不到clazz=%s的枚举", clazz));
    }
}
