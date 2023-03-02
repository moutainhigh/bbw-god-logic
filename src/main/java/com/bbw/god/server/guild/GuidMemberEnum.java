package com.bbw.god.server.guild;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年11月8日 上午9:21:29 
* 类说明 成员操作枚举
*/
@Getter
@AllArgsConstructor
public enum GuidMemberEnum {
	DISMISS(-2,"踢出成员"),
	REFUSE(-1,"拒绝申请"),
	ACCEPT(1,"接受审核"),
	SET_VICEBOSS(2,"授予副队长"),
	TRANSFER(3,"转让队长"),
	DEMOTION(4,"降级为会员"),
	;
	private int val;
	private String memo;
	
	public static GuidMemberEnum fromVal(int val) {
		for (GuidMemberEnum em:values()) {
			if (em.getVal()==val) {
				return em;
			}
		}
		return null;
	}
}
