package com.bbw.god.gameuser.card;

import com.bbw.exception.ExceptionForClientTip;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年11月11日 上午11:20:36 
* 类说明 分享途径
*/
@AllArgsConstructor
@Getter
public enum ShareWayEnum {
	WORLD(110,"世界"),
	GUILD(120,"行会"),
	ALLSERVER(130,"跨服");
	
	private int val;
	private String memo;
	
	public static ShareWayEnum fromVal(int val) {
		for (ShareWayEnum way:values()) {
			if (way.getVal()==val) {
				return way;
			}
		}
		throw new ExceptionForClientTip("card.group.share.enum.error");
	}
	
}
