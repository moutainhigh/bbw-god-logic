package com.bbw.god.game.combat.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.rd.RDCommon;

import lombok.Data;

/**
 * 战斗事件参数
 * 
 * @author suhq
 * @date 2019年4月18日 下午4:42:33
 */
@Data
public class EPFightEnd extends BaseEventParam {
	private Integer pos;// 触发战斗胜利事件时的位置
	private FightTypeEnum fightType;// 战斗类型
	private boolean isWin;
	private FightSubmitParam fightSubmit;

	public static EPFightEnd instance(long guId, int pos, FightTypeEnum fightType, boolean isWin, FightSubmitParam fightSubmit, RDCommon rd) {
		EPFightEnd ev = new EPFightEnd();
		ev.setGuId(guId);
		ev.setPos(pos);
		ev.setFightType(fightType);
		ev.setWin(isWin);
		ev.setFightSubmit(fightSubmit);
		ev.setRd(rd);
		return ev;
	}

}
