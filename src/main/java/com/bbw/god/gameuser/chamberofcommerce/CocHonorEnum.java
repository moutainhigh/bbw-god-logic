package com.bbw.god.gameuser.chamberofcommerce;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suchaobin
 * @description 商会头衔枚举
 * @date 2020/2/21 17:00
 */
@Getter
@AllArgsConstructor
public enum CocHonorEnum {
	SSWS("涉世未深", 1),
	CRSQ("初入商圈", 2),
	LGDZ("临工打杂",3),
	PTHJ("跑堂伙计",4),
	JDXF("肩担小贩",5),
	XDZG("小店掌柜",6),
	WPLB("旺铺老板",7),
	LSFS("连锁富商",8),
	YFCZ("一方财主",9),
	SZJF("商周巨富",10),
	FKDG("富可敌国",11),
	FSSF("封神首富",12);

	private String name;
	private Integer level;

	public static CocHonorEnum fromLevel(int level) {
		for (CocHonorEnum item : values()) {
			if (item.getLevel() == level) {
				return item;
			}
		}
		return null;
	}

	public static CocHonorEnum fromName(String name) {
		for (CocHonorEnum item : values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}
}
