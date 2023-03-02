package com.bbw.god.game.combat.data;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年9月16日 上午11:36:33 
* 类说明 
*/
@Getter
@AllArgsConstructor
public enum CombatVideoEnum implements Serializable {
	VIDEO_TYPE_ROUND_RES("回合数据", 10), VIDEO_TYPE_WEAPON_RES("法宝数据", 20),
	;
	private String name;
	private Integer val;

	public static CombatVideoEnum fromVal(Integer val) {
		for (CombatVideoEnum em : values()) {
			if (em.getVal().equals(val)) {
				return em;
			}
		}
		return null;
	}
}
