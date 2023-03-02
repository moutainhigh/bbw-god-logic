package com.bbw.god.game.combat.event;

import com.bbw.god.event.BaseEventParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 战斗成就达成
 *
 * @author 作者 ：lwb
 * @version 创建时间：2020年5月11日 上午11:40:30 类说明
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EPCombatAchievement extends BaseEventParam {
	private List<Integer> finished=new ArrayList<>();
	public static EPCombatAchievement instance(BaseEventParam bep,int achievementId) {
		EPCombatAchievement ep=new EPCombatAchievement();
		ep.getFinished().add(achievementId);
		ep.setValues(bep);
		return ep;
	}
	public static EPCombatAchievement instance(BaseEventParam bep,List<Integer> finished) {
		EPCombatAchievement ep=new EPCombatAchievement();
		ep.setValues(bep);
		ep.setFinished(finished);
		return ep;
	}

}
