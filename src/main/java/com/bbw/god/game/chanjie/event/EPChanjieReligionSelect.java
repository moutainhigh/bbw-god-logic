package com.bbw.god.game.chanjie.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Getter;
import lombok.Setter;

/**
 * 教派选择
* @author lwb  
* @date 2019年7月8日  
* @version 1.0  
*/
@Getter
@Setter
public class EPChanjieReligionSelect extends BaseEventParam {
	private Integer nowRid;//当前选择的教派id
	private Integer preRid;//上赛季选择的教派id  首次则此处为0
	private boolean isContinuity=false;//是否是连续选择

	public static EPChanjieReligionSelect instance(BaseEventParam bep, int nowRid, int preRid, boolean isContinuity) {
		EPChanjieReligionSelect ev = new EPChanjieReligionSelect();
		ev.setNowRid(nowRid);
		ev.setPreRid(preRid);
		ev.setContinuity(isContinuity);
		ev.setValues(bep);
		return ev;
	}
}
