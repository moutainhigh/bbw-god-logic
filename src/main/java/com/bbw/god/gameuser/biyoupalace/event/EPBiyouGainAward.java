package com.bbw.god.gameuser.biyoupalace.event;

import java.util.List;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;

import lombok.Getter;
import lombok.Setter;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年10月24日 下午4:04:19 
* 类说明 
*/
@Getter
@Setter
public class EPBiyouGainAward extends BaseEventParam{
	private List<Award> awards;//奖励
	private Integer chapter;//篇章
	private boolean newAward;//是否为新的奖励
	public static EPBiyouGainAward instance(BaseEventParam ep,List<Award> awards,Integer chapter,boolean isNewAward) {
		EPBiyouGainAward ew=new EPBiyouGainAward();
		ew.setValues(ep);
		ew.setAwards(awards);
		ew.setChapter(chapter);
		ew.setNewAward(isNewAward);
		return ew;
	}
}
