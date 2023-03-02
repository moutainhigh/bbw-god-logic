package com.bbw.god.game.chanjie.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Getter;
import lombok.Setter;
/**
 * 获得同时获得全部教派奇人的玩家ID
 * @author God
 *
 */
@Getter
@Setter
public class EPChanjieSpecailHonor extends BaseEventParam {
	private Long rbkd;//锐不可当
	private Long txzr;//天选之人
	private Long yryy;//游刃有余
	private Long ddst;//得道升天
	private Integer rid;//阵营

	public static EPChanjieSpecailHonor instance(BaseEventParam bep, long rbkd, long txzr, long yryy, long ddst,
			int rid) {
		EPChanjieSpecailHonor ev = new EPChanjieSpecailHonor();
		ev.setRid(rid);
		ev.setDdst(ddst);
		ev.setRbkd(rbkd);
		ev.setTxzr(txzr);
		ev.setYryy(yryy);
		ev.setValues(bep);
		return ev;
	}
}
