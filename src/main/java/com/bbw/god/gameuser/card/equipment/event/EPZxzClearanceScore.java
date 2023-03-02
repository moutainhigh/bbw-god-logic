package com.bbw.god.gameuser.card.equipment.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * 诛仙阵扫荡分
 *
 * @author: huanghb
 * @date: 2022/9/24 11:18
 */
@Data
public class EPZxzClearanceScore extends BaseEventParam {
	/** 难度等级 */
	private Integer difficulty;
	/** 扫荡分 */
	private Integer clearanceScore;

	public static EPZxzClearanceScore instance(BaseEventParam ep, int difficulty, int clearanceScore) {
		EPZxzClearanceScore ew = new EPZxzClearanceScore();
		ew.setValues(ep);
		ew.setDifficulty(difficulty);
		ew.setClearanceScore(clearanceScore);
		return ew;
	}
}
