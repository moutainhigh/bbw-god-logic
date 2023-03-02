package com.bbw.god.gameuser.card.equipment.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Getter;
import lombok.Setter;

/**
 * 卡牌装备获得事件
 *
 * @author: huanghb
 * @date: 2022/9/24 10:36
 */
@Getter
@Setter
public class EPCardZhiBaoAdd extends BaseEventParam {
	/** 至宝id */
	private Integer zhiBaoId;
	/** 属性 */
	private Integer property;
	/** 至宝数量 */
	private Integer zhiBaoNum = 1;
	/** 满攻数量 */
	private Integer fullAttackNum = 0;
	/** 满防数量 */
	private Integer fullDefenseNum = 0;

	public static EPCardZhiBaoAdd instance(BaseEventParam ep, Integer zhiBaoId, Integer property, Integer fullAttackNum, Integer fullDefenseNum) {
		EPCardZhiBaoAdd ew = new EPCardZhiBaoAdd();
		ew.setValues(ep);
		ew.setZhiBaoId(zhiBaoId);
		ew.setProperty(property);
		if (0 != fullAttackNum) {
			ew.setFullAttackNum(fullAttackNum);
		}
		if (0 != fullDefenseNum) {
			ew.setFullDefenseNum(fullDefenseNum);
		}
		return ew;
	}
}
