package com.bbw.god.game.sxdh.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年11月25日 上午10:31:57 
* 类说明 神仙大会领取仙豆
*/
@Data
public class GainSxdhBean extends BaseEventParam{
	private Integer bean;
	public static GainSxdhBean instance(BaseEventParam ep,int bean) {
		GainSxdhBean gsb=new GainSxdhBean();
		gsb.bean=bean;
		gsb.setValues(ep);
		return gsb;
	}
}
