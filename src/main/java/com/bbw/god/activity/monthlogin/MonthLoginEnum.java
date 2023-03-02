package com.bbw.god.activity.monthlogin;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 部分 宜 忌 枚举
 * @author lwb
 */
@Getter
@AllArgsConstructor
public enum MonthLoginEnum {
    GOOD_DM(201,"大妈参与广场舞比赛，本日不会偶遇大妈"),
    GOOD_KC(202,"矿场60%概率双倍收成"),
    GOOD_LBL(203,"炼宝炉获得法宝时，10%概率额外获得一个法宝"),
    GOOD_QZ(204,"钱庄收益增加30%"),
    GOOD_LDF(205,"炼丹房经验增加30%"),
    GOOD_LB(206,"城池练兵经验增加30%"),
    GOOD_ZXZ(207,"诛仙阵积分增加1.5倍"),
    GOOD_NW(208,"女娲庙25%概率获得双倍好感度"),
    GOOD_TY(209,"太一府获得双倍奖励"),
    GOOD_LNN(210,"送老奶奶回家，获得双倍奖励"),
    GOOD_WWM(211,"不会出现下签"),

    BAD_XB(301,"本日偶遇小白，收益减少50%"),
    BAD_DAJI(303,"偷看妲己画像,遇到的卡组为精英野怪"),
    BAD_QD(304,"反抗强盗,遇到的卡组为精英野怪"),
    BAD_XT(305,"追击小偷,遇到的卡组为精英野怪");
    private int id;
    private String memo;
}
