package com.bbw.god.game.combat.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年11月25日 上午10:19:01 
* 类说明 战斗事件 不含战斗结果，仅为发起战斗
*/
@Data
public class EPCombat extends BaseEventParam{
	private int fightType;
	private Long oppontId=-1L;
	public static EPCombat instance(BaseEventParam ep,int fightType,Long oppontId) {
        EPCombat epCombat=new EPCombat();
        epCombat.setValues(ep);
        epCombat.setFightType(fightType);;
		epCombat.setOppontId(oppontId);
		return epCombat;
	}
}
