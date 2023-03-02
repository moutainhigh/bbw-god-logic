package com.bbw.god.server.fst.event;

import lombok.Data;

/**
 * 封神台胜利事件传递数据
 * 
 * @author suhq
 * @date 2019年3月1日 上午11:09:05
 */
@Data
public class EVFstWin {
	private long oppId;// 随手ID
	private int oldOppRank;// 对手原有排行
	private int oldOppWinStreak;// 对手原有连胜纪录
	private int myWinStreak;// 我的连胜纪录

	public EVFstWin(long oppId, int oldOppRank, int oldOppWinStreak, int myWinStreak) {
		this.oppId = oppId;
		this.oldOppRank = oldOppRank;
		this.oldOppWinStreak = oldOppWinStreak;
		this.myWinStreak = myWinStreak;
	}

}
