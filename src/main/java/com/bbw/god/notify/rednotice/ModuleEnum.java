package com.bbw.god.notify.rednotice;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 物品类别
 *
 * @author suhq
 * @version 创建时间：2018年9月21日 上午9:04:07
 */
@Getter
@AllArgsConstructor
public enum ModuleEnum {
    NEWER_GUIDE("新手引导", 5),
    ACTIVITY("活动", 10),
    TASK("任务", 20),
    ACHIEVEMENT("成就", 30),
    MAIL("邮件", 40),
    BUDDY("好友", 50),
    BUDDY_MONSTER("友怪", 60),
    GUILD("行会", 70),
    COC("商会", 80),
    SXDH("神仙大会", 90),
    PRIVILEGE("特权", 100),
    FST("封神台", 110),
    SETTING("设置", 120),
    WANXIAN("万仙阵", 130),
    RECHARGER_ACTIVITY_GIFT("奇珍-礼包", 140),
    RECHARGER_ACTIVITY_CARD("奇珍-月卡", 150),
    RECHARGER_ACTIVITY_TEHUI("奇珍-特惠", 160),
    // 160在客户端是特殊数字，无法正确显示
    FIRST_RECHARGE_ACTIVITY("首充活动", 170),
    ADVENTURE("奇遇", 180),
    MALL("商城", 190),
    CARD_POOL("卡池", 200),
    ZXZ("诛仙阵", 210),
    GOD_TRAINING("上仙试炼", 220),
    TRANSMIGRATION("轮回", 230),
    BIG_GOD_PLAN("大仙计划", 240),
    WAR_TOKEN("战令", 1300),
    NV_WA_MARKET("女娲集市", 1400),
    ;

    private final String name;
    private final int value;

    public static ModuleEnum fromValue(int value) {
        for (ModuleEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
