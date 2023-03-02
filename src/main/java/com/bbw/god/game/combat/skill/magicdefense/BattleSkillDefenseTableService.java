package com.bbw.god.game.combat.skill.magicdefense;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-08 19:35
 */
public class BattleSkillDefenseTableService {
	/**
	 * 3201回光
	 * 1001斥退、1002妖术、1007瘟君、1204怨灵
	 * 3101飞狙、3102拦截、3106魅惑、3107威风、3108闪电、3109业火、3112枷锁、3124流毒、3125圣火、3127入痘、2012四海龙王
	 * 震慑3135 绝杀1011 雷电3137  火球3139
	 */
	private static final int[] DEFENSE_3201 = { 1001, 1002, 1204, 3101, 3102, 3106, 3107, 3108, 3109, 3112, 3124, 2012,3135,3137,3139,3901,1013, 3145,3159 };
	// 反射
	private static final int[] DEFENSE_3202 = { 1001, 1002, 1204, 3101, 3102, 3106, 3107, 3108, 3109, 3112, 3124, 2012,3135,3137,3139,3901,1013, 3145,3159 };
	//3001金刚
	private static final int[] DEFENSE_3001 = { 1001, 1002, 3106, 3107, 3108, 3109, 3112, 1204, 3125, 3138, 1011,3135,3137,3139,3144,3145,3146,1015,3901,3163,3172};
	//3002刚毅，威风、妖术、魅惑技能对其无效且反弹影响到施法术卡牌。(四海龙王发动)
	private static final int[] DEFENSE_3002 = { 1002, 3106, 3107 ,2012,1011,3144,3146,3163};
	//3003心止，魅惑技能对其无效。
	private static final int[] DEFENSE_3003 = { 3106,3144,3146 };
	//3004自在，枷锁技能对其无效。
	private static final int[] DEFENSE_3004 = { 3112,3138,1015 };
	//3005避雷,闪电技能对其无效。
	private static final int[] DEFENSE_3005 = {3108, 3137};
	//3006定风,斥退\威风技能对其无效。
	private static final int[] DEFENSE_3006 = {1001, 3107, 2012};
	//3007 火球、烈焰、业火和圣火,炙焰对其无效。
	private static final int[] DEFENSE_3007 = {3109, 3125, 3139, 3901, 131130, 3158, 3159};
	//3008反照,回光技能对其无效。
	private static final int[] DEFENSE_3008 = {3201, 3202};
	//3009怯毒,流毒3124、瘟君1007、入痘3127技能对其无效。
	private static final int[] DEFENSE_3009 = {1007, 3124, 3127};
	//（3013）法身：封禁类技能对其无效。禁术，封咒，蚀月   -----3012 乾坤：同法身
	private static final int[] DEFENSE_3013_3014 = {1102, 3131, 3147, 2005};
	//【威风】、【斥退】、【枷锁】、【连锁】无效
	private static final int[] DEFENSE_3018 = {3107, 1001, 3112, 3138, 2012};
	//8001金葫芦,使用后，该回合魅惑、枷锁对我方卡牌无效。
	private static final int[] DEFENSE_8001 = {3106, 3112, 3138, 3144, 3146};
	//8002定神丹,使用后，该回合斥退、妖术、威风对我方卡牌无效。一场战斗限用1次。
	private static final int[] DEFENSE_8002 = {1001, 1002, 3107, 1011};
	private static final int[] DEFENSE_4116 = {4104};
	private static final int[] DEFENSE_4001 = {4104, 4106, 4107, 4108, 4109, 4110, 4117};
	//	【封咒】、【禁术】、【蚀月】
	private static final int[] DEFENSE_3016 = {1102, 3131, 3147};
	//【封咒】、【禁术】、【迷魂阵】、【蚀月】无效
	private static final int[] DEFENSE_3143 = {3131, 1102, 2005, 3147};

	/**
	 * 查找某个技能能防守的其他技能
	 *
	 * @param mySkillId
	 * @return
	 */
	public static int[] getDefenseTableBySkillId(int mySkillId) {
		switch (mySkillId) {
			case 3001:
				return DEFENSE_3001;
			case 3002:
				return DEFENSE_3002;
			case 3003: return DEFENSE_3003;
			case 3004: return DEFENSE_3004;
			case 3005: return DEFENSE_3005;
			case 3014://连环
			case 3006:
				return DEFENSE_3006;
			case 3007:
				return DEFENSE_3007;
			case 3008:
				return DEFENSE_3008;
			case 3009:
				return DEFENSE_3009;
			case 3012:
			case 3143:
				return DEFENSE_3143;
			case 3013:
			case 3015:
				return DEFENSE_3013_3014;
			case 3018:
				return DEFENSE_3018;
			case 3201:
				return DEFENSE_3202;
			case 3202:
				return DEFENSE_3201;
			case 8001:
				return DEFENSE_8001;
			case 8002:
				return DEFENSE_8002;
			case 4116:
				return DEFENSE_4116;
			case 4001:
				return DEFENSE_4001;
			case 3016:
				return DEFENSE_3016;
			default:
				return new int[0];
		}

	}
}