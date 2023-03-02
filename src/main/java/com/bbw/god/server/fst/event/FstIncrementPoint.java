package com.bbw.god.server.fst.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年11月25日 上午10:09:44 
* 类说明 封神台积分增长
*/
@Data
public class FstIncrementPoint extends BaseEventParam{
	private Integer point;
	public static FstIncrementPoint instance(BaseEventParam bep,int point) {
		FstIncrementPoint fip=new FstIncrementPoint();
		fip.setValues(bep);
		fip.point=point;
		return fip;
	}
}
