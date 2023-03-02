package com.bbw.god.game.combat.data;

/**
 * 战斗默认配置
 * @author lwb
 * @date 2020/10/15 16:44
 */
public class CombatConfig {

    public static final int MAX_ROUNDS = 30;// 战斗最大回合数
    public static final int ZXZ_MAX_ROUNDS = 60;// 诛仙阵最大回合数
    public static final int MAX_IN_HAND = 5;// 最大手牌数
    public static final int MAX_BATTLE_CARD = 6;// 最多上场的卡牌数

    // 属性克制 对应 4106金-》木  4107木=》土 4108土=》水 4109水=》火 4110火=》金
    public static final int[] attributeRestraintList = { 4106, 4107, 4109, 4110, 4108 };
    // 属性克制 对应 4110火=》金  4106金-》木 4108土=》水 4109水=》火 4107木=》土
    public static final int[] attributeRestraintReverseList = { 4110, 4106, 4108, 4109, 4107 };
}