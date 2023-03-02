package com.bbw.god.game.combat.video;

import com.bbw.god.game.combat.attackstrategy.StrategyVO;
import com.bbw.god.game.combat.video.UserCombatVideo.CombatData;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年12月17日 下午2:48:48
 * 类说明
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RDVideo extends RDSuccess {
	private String shareId = null;
	private String url = null;
	private List<CombatData> videos = null;
	private Integer capacity = null;
	private List<StrategyVO> strategyVOList = null;
}
