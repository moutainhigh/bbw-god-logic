package com.bbw.god.gameuser.chamberofcommerce;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年3月12日 下午3:47:01 
* 类说明 
*/
@Getter
@AllArgsConstructor
public enum CocPrivilegeEnum {
	Urgent_Task(120,"任务加急概率",-1),
	QZ_Profit_Add(130, "钱庄收益提升", -1),
	HDXS_Times_Add(150,"混沌仙石购买次数",1001),
	City_Profit(160,"城池盈利加成概率",-1);
	private int type;
	private String memo;
	private int shopId;
	
	public static CocPrivilegeEnum getPrivilegeEnumByShopId(int shopId) {
		if (shopId<0) {
			return null;
		}
		for (CocPrivilegeEnum cocPrivilegeEnum:values()) {
			if (cocPrivilegeEnum.getShopId()==shopId) {
				return cocPrivilegeEnum;
			}
		}
		return null;
	}
}
