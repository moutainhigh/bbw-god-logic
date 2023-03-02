package com.bbw.god.game.combat;

import java.util.List;

import com.bbw.common.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bbw.common.PowerRandom;
import com.bbw.common.StrUtil;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.param.CardMovement;
import com.bbw.god.game.combat.deploy.DeployCardsStrategy;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-14 20:26
 */
@Slf4j
@Service
public class PlayerService {
	@Autowired
	private BattleCardService battleCardService;
	@Lazy
	@Autowired
	private List<DeployCardsStrategy> deployCardsStrategy;
	@Autowired
	private DeployCardsSolutionService deployService;

	/**
	 * 玩家手动上牌
	 *
	 * @param combat
	 * @param playing
	 * @param moveToBattle
	 */
	public void deployCards(Combat combat, Player playing, String moveToBattle) {
		// 处理玩家手动上牌
		if (!StrUtil.isBlank(moveToBattle)) {
			log.debug(playing.getUid() + "moveToBattle=" + JSON.toJSONString(moveToBattle));
			List<CardMovement> movements = ClientProtocol.parse(moveToBattle);
			if (playing.getId() == PlayerId.P2) {
				// p2玩家需要转换坐标 即上牌坐标 +1000
				for (CardMovement movement : movements) {
					if (movement.getToPos() > 0) {
						int pos = movement.getToPos() + 1000;
						movement.setToPos(pos);
					}

					if (movement.getFromPos() > 0) {
						int pos = movement.getFromPos() + 1000;
						movement.setFromPos(pos);
					}
				}
			}
			battleCardService.moveHandCardsToBattleMpPosCheck(combat.getRound(), playing, movements, combat.getFightType());
			if (ListUtil.isEmpty(movements)){
				return;
			}
			List<AnimationSequence> animations = ClientAnimationService.getCardMovementActions(combat.getAnimationSeq(), movements);
			combat.addAnimations(animations);
			battleCardService.addtionPos(combat, playing, movements);
		}
	}

	public void autoDeployCards(Combat combat, Player playing) {
		autoDeployCards(combat, playing, null);
	}
	/**
	 * 自动上牌
	 *
	 * @param combat
	 * @param playing
	 * @param moveToBattle
	 */
	public void autoDeployCards(Combat combat, Player playing, String moveToBattle) {
		// 可能先手动上了几张牌，然后点击了自动战斗
		int type = combat.getFightType().getValue();
//		if (combat.getRound() == 1 && playing.getUid() < 0
//				&& (type == FightTypeEnum.ATTACK.getValue() || type == FightTypeEnum.PROMOTE.getValue())) {
//			// 首回合 且是机器人
//			int cityLevel = CityTool.getCityById(combat.getCityId()).getLevel();
//			if (cityLevel == 5) {
//				// 五级城攻城 默认
//				moveToBattle = "100T13";
//			}
//		}
		if (!StrUtil.isBlank(moveToBattle)) {
			deployCards(combat, playing, moveToBattle);
			return;
		}
		// 处理玩家自动战斗
		DeployCardsStrategy strategy = PowerRandom.getRandomFromList(deployCardsStrategy);
		List<CardMovement> autoMovements = deployService.autoDeployCardsWithRuleLimit(combat, playing, strategy);
		// log.debug("MP:" + playing.getMp() + ",手牌:" +
		// JSON.toJSONString(playing.getHandCards()));
		log.debug("自动上牌:" + autoMovements);
		battleCardService.moveHandCardsToBattleMpPosCheck(combat.getRound(), playing, autoMovements, combat.getFightType());
		List<AnimationSequence> animations = ClientAnimationService.getCardMovementActions(combat.getAnimationSeq(), autoMovements);
		combat.addAnimations(animations);
		battleCardService.addtionPos(combat, playing, autoMovements);
	}

}