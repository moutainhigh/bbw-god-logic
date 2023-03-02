package com.bbw.god.gameuser;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年2月4日 下午5:42:57 
* 类说明 玩家详细信息枚举
*/
@Getter
@AllArgsConstructor
public enum UserInfoEnum {
	FST(210,"封神台"),
	CARDS(220,"卡牌集"),
	SXDH(230,"神仙大会"),
	CITIES(240,"城池集"),
	CJDF(250,"阐截斗法"),
	ACHIEVEMENT(260,"成就数");
	private int type;
	private String memo;
}
