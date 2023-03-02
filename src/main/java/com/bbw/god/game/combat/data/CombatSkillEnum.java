package com.bbw.god.game.combat.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 卡牌技能。2019年7月14日重新编码
 * @author lsj@bamboowind.cn
 * @version 2.0.0
 * @date 2019-07-14 19:35
 */
@Getter
@AllArgsConstructor
public enum CombatSkillEnum implements Serializable {
	FX("飞行", 101),
	JC("疾驰", 102),
	YING_SUI("影随", 103),

	CT("斥退", 1001),
	YS("妖术", 1002),
	ZS("主帅", 1003),
	XX("修仙", 1004),
	HY("混元", 1005),
	WJ("瘟君", 1007),
	SXIAN("升仙", 1008),
	SL("神疗", 1009),
	CD("超度", 1010),
	BAO_QI("暴起", 1012),
	RW("让位", 1014),
	HUN_LING("混绫", 1015),
	JF("解封", 1016),
	SHEN_YI("神医", 1017),
	CAI_SHEN("财神", 1018),
	YUAN_SHUAI("元帅", 1019),
	BAO_CI("暴刺", 1020),
	HUAN_HUA("幻化", 1021),
	HUANG_ZHENG("蝗征", 1023),


	ZC("招财", 1090),

	WZAI("王者AI", 1101),
	JINS("禁术", 1102),
	XIAO_TIAN("哮天", 1103),
	JI_BIAN("祭鞭", 1104),
	FENG_SHEN("奉神", 1105),
	QI_LIN("麒麟", 1106),

	FH("复活", 1201),
	SIS("死士", 1202),
	SZ("死咒", 1203),
	YL("怨灵", 1204),
	CS("长生", 1205),
	ZHOU_YUAN("咒怨", 1206),
	GX("鬼雄", 1207),
	SHANG_ZHOU("殇咒", 1208),
	ZHU_QUE("朱雀", 1209),

	ZJZ_G("十绝阵", 2001),
	HJJ_G("黄家军", 2002),
	CTJ_G("陈塘家", 2003),
	JQHHZ_G("九曲黄河阵", 2004),
	MHZ_G("迷魂阵", 2005),
	SDTW_G("四大天王", 2006),
	HHEJ_G("哼哈二将", 2007),
	QLYSFE_G("千里眼顺风耳", 2008),
	JLDSS_G("九龙岛四圣", 2009),
	MSQG_G("梅山七怪", 2010),
	ZYMS_G("左右门神", 2011),
	SHLW_G("四海龙王", 2012),
	CSXD_G("晁式兄弟", 2013),
	YHSTJ_G("殷洪四天君", 2014),
	SLHS_G("神力横扫", 2015),
	WZSTJ_G("闻仲四天君", 2016),
	GSLX_G("古兽来袭", 2017),
	XWSZ_G("行瘟使者", 2018),
	SGSL_G("上古神力", 2020),

	LG("联攻", 2201),

	JG("金刚", 3001),
	GY("刚毅", 3002),
	XZ("心止", 3003),
	ZZ("自在", 3004),
	BL("避雷", 3005),
	DFENG("定风", 3006),
	ZJ("真金", 3007),
	FZHAO("返照", 3008),
	QD("怯毒", 3009),
	FD("法盾", 3010),
	LS("灵守", 3011),
	QK("乾坤", 3012),
	FA_SHEN("法身", 3013),
	LIAN_HUAN("连环", 3014),
	JIN_SHEN("金身", 3015),
	SHEN_ZHAO("神罩", 3016),
	JIN_ZHAO("金罩", 3017),
	BI_HUAN("璧环", 3018),
	XUAN_WU("玄武", 3019),

	FJ("飞狙", 3101),
	LJ("拦截", 3102),
	ZHI_YU("治愈", 3103),
	FS("封神", 3104),
	HH("回魂", 3105),
	MH("魅惑", 3106),
	WF("威风", 3107),
	SD("闪电", 3108),
	YH("业火", 3109),
	SHIH("噬魂", 3110),
	WZ("王者", 3111),
	JS("枷锁", 3112),
	DD("得道", 3113),
	SJ("生金", 3114),
	SM("生木", 3115),
	SS("生水", 3116),
	SH("生火", 3117),
	ST("生土", 3118),
	QJ("强金", 3119),
	QM("强木", 3120),
	QS("强水", 3121),
	QH("强火", 3122),
	QT("强土", 3123),
	LD("流毒", 3124),
	Sheng4H("圣火", 3125),
	DF("道法", 3126),
	RD("入痘", 3127),
	MP("灭魄", 3128),
	ZY("自愈", 3130),
	FZ("封咒", 3131),
	GH("蛊惑", 3132),
	XIXING("吸星", 3133),
	SHUANG_JU("双狙", 3134),
	ZHEN_SHE("震慑", 3135),
	GU_WU("鼓舞", 3136),
	LEI_DIAN("雷电", 3137),
	LIAN_SUO("连锁", 3138),
	HUO_QIU("火球", 3139),
	LI_DUN("力盾", 3140),
	WU_YI("巫医", 3141),
	HUAN_SHU("幻术", 3142),
	JIE_FEN("解封", 3143),
	TIAO_BO("挑拨", 3144),
	LY("落羽", 3145),
	FAN_JIAN("反间", 3146),
	SY("蚀月", 3147),
	LENG_DAO("冷刀", 3148),
	HUA_XUE("化血", 3149),
	BEI_FEN("悲愤", 3150),
	PAO_ZE("袍泽", 3151),
	SHI_SHI("食尸", 3152),
	JING_XIANG("镜像", 3153),
	TOU_YING("偷营", 3154),
	HONG_FA("弘法", 3155),
	JI_LI("激励", 3156),
	SHENG_GUANG("神光", 3157),
	LIE_YAN("烈焰", 3158),
	ZHI_YAN("炙焰", 3159),
	FEN_SHEN("分身", 3160),
	DU_SHEN("度升", 3161),
	XI_YANG("吸阳", 3162),
	QING_GUO("倾国", 3163),
	XUAN_HUAN("玄幻", 3164),
	FENG_DAI("蜂袋", 3165),
	JIE_DU("解毒", 3166),
	DU_SHI("毒矢", 3167),
	DU_BIAO("毒镖", 3168),
	JI_JUN("疾军", 3170),
	DOU_XIAN("痘仙", 3171),
	TIAN_LEI("天雷", 3172),
	BAI_HU("白虎", 3173),
	YIN_CANG_HUANG_ZHENG("隐藏蝗征", 3175),
	QUAN_YU("痊愈", 3176),

	HG("回光", 3201),
	FAN_SHE("反射", 3202),
	JIAN_YI("坚毅", 3301),
	NU_YI("怒意", 3302),

	FU_HU("伏虎", 3501),
	YIN_CANG_FU_HU("隐藏伏虎", 3502),

	HUO_QIANG("火枪", 3901),
	JU_SHA("狙杀", 3902),

	WX("无相", 4001),

	BJ("暴击", 4104),
	JKM("金克木", 4106),
	MKT("木克土", 4107),
	TKS("土克水", 4108),
	SKF("水克火", 4109),
	HKJ("火克金", 4110),
	LX("龙息", 4111),
	TX("突袭", 4112),
	SID("死斗", 4113),
	SHENJ("神剑", 4114),
	XH("销魂", 4115),
	TJ("太极", 4116),
	KD("克敌", 4117),
	ZHU_XIAN("诛仙", 4119),
	TU_JI("突击", 4120),
	QING_LONG("青龙", 4121),

	LIAN_JI("连击", 4451),

	QX("奇袭", 4301),
	ZD("钻地", 4302),
	DJ("地劫", 4303),
	QL("潜龙", 4304),

	CC("穿刺", 4201),
	XIX("吸血", 4202),
	SX("嗜血", 4203),

	NORMAL_ATTACK("物理攻击", 4401),

	LINGD("灵动", 4501),
	MANG_CI("芒刺", 4502),
	FEI_SHAN("飞闪", 4503),
	ZHAN_CI("战刺", 4504),
	CHAO_FENG("嘲讽", 4505),
	JIAN_YI_HIDE("隐藏坚毅", 4601),
	NU_YI_HIDE("隐藏怒意", 4602),
	TAO_DUN("逃遁", 4603),
	NORMAL_DEFENSE("物理防御", 4599),

	HW("护卫", 5001),
	BAO_JIAN("宝鉴", 5002),

	XF("兴法", 6002),
	JUE_XIAN("绝仙", 6004),
	ZHU_JUE("诛绝", 6005),

	ZHAN_PO_WEAPON("斩魄", 10101),
	PO_TIAN_WEAPON("破天", 10102),

	ZHUI_FENG_WEAPON("追风", 10201),
	SHI_GU_WEAPON("蚀骨", 10202),

	YUAN_JIA_WEAPON("鼋甲", 11101),
	LONG_LIN_WEAPON("龙麟", 11102),

	JUE_YING_WEAPON("绝影", 11201),
	CI_ZHOU_WEAPON("刺胄", 11202),

	WEAPON_JHL_DEFENSE("金葫芦", 8001),
	WEAPON_DSD_DEFENSE("定神丹", 8002),
	LEADER_CARD_EXP("主角卡经验", 1091),
	MOVE("移位", 88888),
	NO("未设置", 99999);

	private String name;
	private int value;

	public static CombatSkillEnum fromValue(int value) {
		for (CombatSkillEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return NO;
	}

	public static CombatSkillEnum fromName(String name) {
		for (CombatSkillEnum item : values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return NO;
	}

	public static boolean isWeaponSKill(int skillId) {
		if (WEAPON_JHL_DEFENSE.getValue() == skillId) {
			return true;
		}
		if (WEAPON_DSD_DEFENSE.getValue() == skillId) {
			return true;
		}
		return false;
	}
}