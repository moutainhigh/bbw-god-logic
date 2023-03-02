package com.bbw.god.fight;

import com.bbw.exception.ExceptionForClientTip;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FightTypeEnum {
    NONE(0, "无", 0),
    YG(100, "打野怪", 10),
    HELP_YG(100, "帮好友打怪", 15),
    ATTACK(150, "攻城", 20),
    TRAINING(150, "练兵", 30),
    PROMOTE(150, "振兴", 60),
    FST(200, "封神台", 70),
    ZXZ(200, "诛仙阵", 80),
    ZXZ_FOUR_SAINTS(200, "诛仙阵-四圣挑战", 81),
    SXDH(300, "神仙大会", 90),
    CJDF(300, "阐截斗法", 100),
    DFDJ(300, "巅峰对决", 105),
    INVESTIGATE(150, "侦查", 110),
    YED_EVENT(150, "野地事件", 120),
    WXZ(400, "万仙阵", 400),
    MXD(500, "迷仙洞战斗", 500),
    CZ_TASK_FIGHT(600, "村庄任务战斗", 600),
    YAOZU_FIGHT(700, "妖族来犯战斗", 700),
    TRANSMIGRATION_FIGHT(800, "轮回世界城池战斗", 800),
    WAN_S_ACTIVITY_FIGHT(900,"不给糖就捣乱战斗",900),
    LABOR_FIGHT(910,"劳动光荣战斗",910),
    ;
    /** 战斗类型 100地图相关 200封神台、诛仙阵 、300 PVP */
    private final int type;
    private final String name;
    private final int value;

    public static FightTypeEnum fromValue(int value) {
        for (FightTypeEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        throw new ExceptionForClientTip("combat.unknown.type");
    }

    public static FightTypeEnum fromName(String name) {
        for (FightTypeEnum item : values()) {
            if (name.equals(item.getName())) {
                return item;
            }
        }
        throw new ExceptionForClientTip("combat.unknown.type");
    }

    /**
     * 是否需要缓存战斗记录到数据库中
     *
     * @param typeEnum
     * @return
     */
    public static boolean needLogToPVEDetail(FightTypeEnum typeEnum) {
        if (ATTACK.equals(typeEnum) || PROMOTE.equals(typeEnum) || TRAINING.equals(typeEnum) || YG.equals(typeEnum)) {
            return true;
        }
        return false;
    }

    /**
     * 是否使用PVP模式的胜利判断条件
     *
     * @param fightType
     * @return
     */
    public static boolean usePvPWinRules(int fightType) {
        if (WXZ.getValue() == fightType || SXDH.getValue() == fightType || CJDF.getValue() == fightType) {
            return true;
        }
        return false;
    }

    /**
     * 使用AI王者的范围
     *
     * @param fightTypeInt
     * @return
     */
    public static boolean useAiKing(int fightTypeInt) {
        FightTypeEnum fightType = FightTypeEnum.fromValue(fightTypeInt);
        if (ATTACK == fightType || PROMOTE == fightType || TRAINING == fightType
                || YAOZU_FIGHT == fightType || TRANSMIGRATION_FIGHT == fightType) {
            return true;
        }
        return false;
    }

    /**
     * 受到神仙影响的战斗
     *
     * @param fightType
     * @return
     */
    public static boolean useGodEffect(int fightType) {
        if (ATTACK.getValue() == fightType || PROMOTE.getValue() == fightType || TRAINING.getValue() == fightType || TRANSMIGRATION_FIGHT.getValue() == fightType) {
            return true;
        }
        return false;
    }


    public static boolean isCityWar(int value) {
        return fromValue(value).getType() == 150;
    }

    /**
     * 是否是属于地图相关的战斗（含友怪）
     *
     * @param type
     * @return
     */
    public static boolean isMapPVEFight(FightTypeEnum type) {
        return type.getType() == 100 || type.getType() == 150;
    }

    /**
     * 是否是属于地图相关的战斗（含友怪）
     *
     * @param value
     * @return
     */
    public static boolean isMapPVEFight(int value) {
        return isMapPVEFight(fromValue(value));
    }

    /**
     * PVE可以用主角卡的战斗
     * @param typeEnum
     * @return
     */
    public static boolean pveUseLeaderCard(FightTypeEnum typeEnum){
        switch (typeEnum){
            case ATTACK:
            case PROMOTE:
            case TRAINING:
            case YG:
            case YAOZU_FIGHT:
            case TRANSMIGRATION_FIGHT:
                return true;
            default:
                return false;
        }
    }
}
