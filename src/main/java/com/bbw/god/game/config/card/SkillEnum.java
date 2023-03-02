package com.bbw.god.game.config.card;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 卡牌技能。2019年7月14日重新编码
 *
 * @author lsj@bamboowind.cn
 * @version 2.0.0
 * @date 2019-07-14 19:35
 */
@Getter
@AllArgsConstructor
@Deprecated
public enum SkillEnum implements Serializable {
	FX("飞行", 101),
	JC("疾驰", 102),

	CT("斥退", 1001),
	TS("妖术", 1002),
	ZS("主帅", 1003),
	XX("修仙", 1004),
	HY("混元", 1005),
	FZ("封咒", 1006),
	WJ("瘟君", 1007),
	SXIAN("升仙", 1008),
	SL("神疗", 1009),
	CD("超度", 1010),
	ZC("招财", 1090),

	WZAI("王者AI", 1101),

	FH("复活", 1201),
	SIS("死士", 1202),
	SZ("死咒", 1203),
	YL("怨灵", 1204),

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

	LG("联攻", 2201),

	GG("金刚", 3001),
	GY("刚毅", 3002),
	XZ("心止", 3003),
	ZZ("自在", 3004),
	BL("避雷", 3005),
	DFENG("定风", 3006),
	ZJ("真金", 3007),
	FZHAO("返照", 3008),
	QD("怯毒", 3009),

	FJ("飞狙", 3101),

	LJ("拦截", 3102),
	ZYU("治愈", 3103),

	FS("封神", 3104),
	HH("回魂", 3105),
	MH("魅惑", 3106),
	WF("威风", 3107),
	SD("闪电", 3108),
	TH("业火", 3109),
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
	HG("回光", 3201),

	WX("无相", 4001),

	BJ("暴击", 4104),
	JKM("金克木", 4106),
	MKT("木克土", 4107),
	TKS("土克水", 4108),
	SKF("水克火", 4109),
	HKJ("火克金", 4110),

	QX("奇袭", 4301),
	ZD("钻地", 4302),

	LX("龙息", 4402),
	TX("突袭", 4403),
	SID("死斗", 4404),
	SJIAN("神剑", 4405),
	XH("销魂", 4406),

	CC("穿刺", 4201),
	XIX("吸血", 4202),
	SX("嗜血", 4203),

	LINGD("灵动", 4501),
	NORMAL_DEFENSE("物理防御", 4599),
	NORMAL_ATTACK("物理攻击", 4401),
	WEAPON_JHL_DEFENSE("金葫芦", 8001),
	WEAPON_DSD_DEFENSE("定神丹", 8002),
	MOVE("移位", 88888),
	NO("未设置", 99999);

	private String name;
	private int value;

	public static SkillEnum fromValue(int value) {
		for (SkillEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return NO;
	}

	public static SkillEnum fromName(String name) {
		for (SkillEnum item : values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return NO;
	}
}