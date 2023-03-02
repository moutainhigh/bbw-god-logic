package com.bbw.god.game.chanjie.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EPChanjieFight extends BaseEventParam {
	private Integer headlv = 0;// 头衔等级 外门弟子1 内门弟子2 真传弟子3 渡劫地仙4 大乘天仙5 大罗金仙6 护教法王7 掌教8
	private Integer rid;//阵营
	private Long loseUid;//失败方id
	private Integer loseRid;//失败方阵营

	public static EPChanjieFight instance(BaseEventParam bep, int level, int rid, long loseUid, int loseRid) {
		EPChanjieFight ev = new EPChanjieFight();
		ev.setHeadlv(level);
		ev.setRid(rid);
		ev.setValues(bep);
		ev.setLoseUid(loseUid);
		ev.setLoseRid(loseRid);
		return ev;
	}
}
