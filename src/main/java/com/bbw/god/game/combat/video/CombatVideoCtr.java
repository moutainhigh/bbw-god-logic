package com.bbw.god.game.combat.video;

import com.bbw.common.Rst;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.CR;
import com.bbw.god.game.combat.attackstrategy.StrategyEnum;
import com.bbw.god.game.combat.attackstrategy.service.AbstractStrategyLogic;
import com.bbw.god.game.combat.attackstrategy.service.StrategyLogicFactory;
import com.bbw.god.game.combat.video.service.CombatVideoService;
import com.bbw.god.gameuser.card.ShareWayEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年12月17日 上午11:00:51 
* 类说明 
*/
@RestController
public class CombatVideoCtr extends AbstractController{

	@Autowired
	private CombatVideoService videoService;
	@Autowired
	private StrategyLogicFactory strategyLogicFactory;

	@RequestMapping(CR.CombatVideo.SAVE_VIDEO)
	public Rst saveVideo(long combatId) {
		return videoService.saveVideo(combatId, getUserId());
	}

	@RequestMapping(CR.CombatVideo.COLLECT_VIDEO)
	public Rst collectVideo(String shareId) {
		videoService.collectVideo(getUserId(), shareId);
		return Rst.businessOK();
	}
	@RequestMapping(CR.CombatVideo.DEL_VIDEO)
	public Rst delVideo(long dataId) {
		videoService.delVideo(getUserId(), dataId);
		return Rst.businessOK();
	}

	@RequestMapping(CR.CombatVideo.SHARE_VIDEO)
	public RDVideo shareVideo(long dataId,Integer msgType) {
		if (msgType==null) {
			msgType=ShareWayEnum.WORLD.getVal();
		}
		return videoService.shareVideo(getUserId(), dataId,msgType);
	}

	@RequestMapping(CR.CombatVideo.SHARE_VICTORY)
	public RDVideo shareVictory(long combatId,String groupName) {
		return videoService.shareVictory(getUserId(), combatId,groupName);
	}

	/**
	 * 获取保存的视频列表
	 *
	 * @return
	 */
	@RequestMapping(CR.CombatVideo.LIST_VIDEO)
	public RDVideo listVideo() {
		return videoService.getUseRdVideoList(getUserId());
	}

	/**
	 * 获取攻略
	 *
	 * @param fightType
	 * @param srcId     源ID，比如城池ID，妖族ID
	 * @param type
	 * @return
	 */
	@RequestMapping(CR.CombatVideo.STRATEGY_LIST_VIDEO)
	public RDVideo listStrategyVideo(int fightType, int srcId, Integer type) {
		AbstractStrategyLogic logic = strategyLogicFactory.getLogic(FightTypeEnum.fromValue(fightType));
		if (null == logic) {
			throw ExceptionForClientTip.fromi18nKey("fight.strategy.not.support");
		}
		type = type == null ? 0 : type;
		if (type == 15) {
			return logic.listBetterStrategy(getUserId(), getGid(), srcId);
		}
		return logic.listStrategy(getUserId(), getGid(), srcId, StrategyEnum.fromVal(type));
	}

}
