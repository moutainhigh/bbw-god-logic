package com.bbw.god.gameuser.statistic.behavior;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suchaobin
 * @description 行为枚举，注意 如果WayEnum中有对应的，在这边新增时value的值要统一
 * @date 2020/3/27 9:49
 */
@Getter
@AllArgsConstructor
public enum BehaviorType {
    RECHARGE("充值", 3),
    FLX("福临轩", 80),
    CHAMBER_OF_COMMERCE_EXP_TASK("完成商会任务", 318),
    GUILD_TASK("完成行会任务", 415),
    OPEN_GUILD_BOX("开启八卦宝箱", 416),
    CARD_DRAW("抽卡", 2025),
    CARD_HIERARCHY("进阶卡牌", 3020),
    JU_LING("聚灵", 3030),
    MAOU_ALONE_FIGHT("独战魔王", 3292),
    SXDH_FIGHT("神仙大会", 4105),
    DFDJ_FIGHT("巅峰对决", 4113),
    CHAN_JIE_FIGHT("阐截斗法", 4301),
    OPEN_DAILY_TASK_BOX("开启每日任务宝箱", 10010),
    LOGIN("登录", 10020),
    FIGHT("战斗", 10030),
    MOVE("移动", 10040),
    RANDOM_EVENT("随机事件", 10050),
    MEET_GOD("遇到神仙", 10060),
    WWM_DRAW("文王庙抽签", 10070),
    NVWM_DONATE("女娲庙捐钱", 10080),
    SNATCH_TREASURE_DRAW("夺宝抽奖", 10090),
    CHANGE_WORLD("跳转世界", 10100),
    BUILDING_AWARD("领取城内建筑物产出奖励", 10110),
    WWM_HEXAGRAM("文王六十四卦", 10120),
    LEADER_CARD_SKILL_TREE("技能树", 10130),
    BI_YOU("碧游宫", 10140),
    LEADER_EQUIPMENT("法外分身装备", 10150),
    FIGHT_DETAIL("战斗细节", 10160),
    NIGHTMARE_MI_XD("梦魇迷仙洞", 10170),
    FST("封神台", 10180),
    FINISH_CUNZ_TASK("完成村庄任务", 10190),
    YAO_ZU_WIN("击败妖族", 10200),
    TRANSMIGRATION_CHALLENGE("轮回挑战", 10210),
    FA_TAN("法坛", 10220),
    YU_XG("玉虚宫", 10230),
    BUSINESS_GANG("商帮", 10240),
    PINCH_PEOPLE("捏人", 7580),
    CARD_XIAN_JUE_ACTIVE("卡牌仙诀激活", 7770),
    CARD_XIAN_JUE_STUDY("卡牌仙诀研习", 7780),
    CARD_XIAN_JUE_UPDATE_STAR("卡牌仙诀淬星", 7790),
    CARD_XIAN_JUE_COMPREHEND("卡牌仙诀参悟", 7800),
    CARD_ZHI_BAO("卡牌至宝", 7810),
    KUNLS_MAKING("昆仑山炼制室", 7820),
    KUNLS_INFUSION("昆仑山注灵室", 7830),
    KUNLS_REFINE("昆仑山提炼室", 7840),

    ;
    private final String name;
    private final Integer value;

    public static BehaviorType fromName(String name) {
        for (BehaviorType behaviorType : values()) {
            if (behaviorType.getName().equals(name)) {
                return behaviorType;
            }
        }
        return null;
    }

    public static BehaviorType fromValue(Integer value) {
        for (BehaviorType behaviorType : values()) {
            if (behaviorType.getValue().equals(value)) {
                return behaviorType;
            }
        }
        return null;
    }
}
