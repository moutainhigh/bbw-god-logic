package com.bbw.god.game.combat.weapon;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.combat.AcceptEffectService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.SkillRoundService;
import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.attack.Effect.EffectResultType;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.data.weapon.Weapon;
import com.bbw.god.game.combat.data.weapon.WeaponLog;
import com.bbw.god.game.combat.event.CombatEventPublisher;
import com.bbw.god.game.combat.event.EPCombatAchievement;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import com.bbw.god.game.combat.weapon.service.IWeaponInTimeEffect;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 战斗法宝服务
 * @author lwb
 * @date 2020年11月24日
 * @version 1.0
 */
@Service
@Primary
@Slf4j
public class WeaponLogic {
	@Autowired
	private WeaponEffectFactory weaponEffectFactory;
	@Autowired
	private AcceptEffectService acceptEffectService;
	@Autowired
	private SkillRoundService skillRoundService;
	/**
	 * 即时生效的法宝
	 */
	private static final List<Integer> inTimeWeapons= Arrays.asList(320,400,460);
	/**
	 * 客户端请求使用法宝
	 * 
	 * @param combat
	 * @param playerId
	 * @param weaponId
	 * @param targetPos
	 * @return
	 */
	public RDTempResult useWeapon(Combat combat, PlayerId playerId, int weaponId, List<Integer> targetPos) {
		PerformWeaponParam pwp =PerformWeaponParam.instance(combat,playerId,weaponId,targetPos);
		Optional<Weapon> op=pwp.getPerformPlayer().findWeaponById(weaponId);
		if (!op.isPresent()){
			//没有载入该法宝 不允许使用
			throw new ExceptionForClientTip("combat.weapon.cant.use");
		}
		WeaponLog log=new WeaponLog(weaponId,pwp.getMultiplePos(),pwp.getCanEffectTimes(),combat.getRound());
		RDTempResult rd=new RDTempResult();
		if (inTimeWeapons.contains(weaponId)){
			IWeaponInTimeEffect iWeaponInTimeEffect = weaponEffectFactory.matchInTimeEffectWeapon(weaponId);
			iWeaponInTimeEffect.doCheckUseLimit(pwp);
			Action action = iWeaponInTimeEffect.takeInTimeAttack(pwp);
			if (action.existsEffect()) {
				//生效成功
				acceptWeaponEffect(combat, action.getEffects());
				addEffectWeaponLog(pwp.getPerformPlayer(),log);
			}
			//标识为即时生效的法宝类型
			pwp.setCanEffectTimes(-1);
			rd = iWeaponInTimeEffect.beforehandAttack(pwp);
			checkRYQKD(pwp.getPerformPlayer(),weaponId,combat.getRound());
		}else {
			//预先生效的
			IWeaponAfterEffect iWeaponAfterEffect = weaponEffectFactory.matchAfterEffectWeapon(weaponId);
			rd = iWeaponAfterEffect.beforehandAttack(pwp);
		}
		//法宝实现逻辑里面可能会设置生效次数
		log.setResidueTimes(pwp.getCanEffectTimes());
		pwp.getPerformPlayer().addWeaponLog(log);
		return rd;
	}

	/**
	 * 添加法宝动画
	 * </br>即时生效法宝（如混元金斗,紫金钵盂,如意乾坤袋,使用时立即生效，回合开始时需要补充动画 使对方看到我方使用了该法宝）
	 *
	 * @param combat
	 */
	public void addInTimeWeaponAnimation(Combat combat) {
		for (Player player:combat.findPlayers()){
			List<WeaponLog> weaponLogs = player.findNeedAddAnimationLog();
			for (WeaponLog log:weaponLogs){
				if (log.getWeaponId()!=WeaponEnum.RYQKD.getWeaponId()){
					//法宝释放默认播放动画 只需一个即可
					int seq=combat.getAnimationSeq();
					AnimationSequence as = new AnimationSequence(seq, EffectResultType.PLAY_ANIMATION);
					AnimationSequence.Animation animSeq=ClientAnimationService.getSkillAnimation(log.getWeaponId(),PositionService.getZhaoHuanShiPos(player.getId()), -1);
					as.add(animSeq);
					combat.addAnimation(as);
				}
			}
		}
	}

	/**
	 * 非即时生效法宝的  实际生效阶段
	 * @param combat
	 */
	public void takeUsedWeaponEffect(Combat combat) {
		for (Player player:combat.findPlayers()){
			List<WeaponLog> weaponLogs = player.findNeedTakeEffectLog();
			for (WeaponLog weaponLog: weaponLogs) {
				if (combat.hadEnded()) {
					return;
				}
				try {
					int weaponId = weaponLog.getWeaponId();
					IWeaponAfterEffect iWeaponAfterEffect = weaponEffectFactory.matchAfterEffectWeapon(weaponId);
					PerformWeaponParam pwp = PerformWeaponParam.instance(combat, player.getId(), weaponId,weaponLog.getTargetPos());
					Action action = iWeaponAfterEffect.takeAfterAttack(pwp);
					if (!action.getTakeEffect()){
						continue;
					}
					if (!weaponLog.isDeductWeapon()) {
						// 只有法宝首回合生效时才添加到已使用法宝并扣除法宝
						addEffectWeaponLog(player, weaponLog);
					}
					int sourcePos = PositionService.getZhaoHuanShiPos(pwp.getPerformPlayerId());
					for (Effect effect : action.getEffects()) {
						if (effect.getSequence() < 0) {
							effect.setSequence(pwp.getNextAnimationSeq());
						}
						if (effect.getSourcePos() < 10) {
							effect.setSourcePos(sourcePos);
						}
					}
					// 生成动画
					ClientAnimationService.getWeaponEffectAnimation(action,combat,pwp);
					acceptWeaponEffect(combat, action.getEffects());
					weaponLog.deductUseTimes();
				} catch (Exception exception) {
					log.error("法宝生效错误："+weaponLog.getWeaponId());
					log.error(exception.getMessage(),exception);
				}
			}
			player.clearDisabledWeaponLog();
		}
	}

	/**
	 * 接受法宝效果
	 * 
	 * @param combat
	 * @param effects
	 */
	private void acceptWeaponEffect(Combat combat, List<Effect> effects) {
		if (effects.isEmpty()) {
			return;
		}
		effects = skillRoundService.runZhsEffects(combat, effects);
		for (Effect effect : effects) {
			if (combat.hadEnded()) {
				return;
			}
			if (PositionService.posCardHasKingSkill(combat, effect.getTargetPos())) {
				continue;
			}
			acceptEffectService.acceptWeaponEffect(combat, effect);
			if (effect.getResultType().equals(EffectResultType.CARD_VALUE_CHANGE)
					&& PositionService.isPlayingPos(effect.getTargetPos())) {
				BattleCard targetCard = combat.getBattleCard(effect.getTargetPos());
				if (null != targetCard && targetCard.isKilled()) {
					CardPositionEffect myDieEffect = CardPositionEffect
							.getSkillEffectToTargetPos(CombatSkillEnum.MOVE.getValue(), effect.getTargetPos());
					myDieEffect.setSourcePos(effect.getTargetPos());
					myDieEffect.moveTo(PositionType.DISCARD);
					myDieEffect.setSequence(combat.getAnimationSeq());
					acceptEffectService.acceptEffect(combat, myDieEffect);
				}
			}
		}

	}

	/**
	 * 添加法宝使用记录 并扣除法宝 立即生效类型 调用此方法，此处有如意乾坤袋使用的特殊处理
	 */
	private void checkRYQKD(Player player,int wid,int round) {
		if (round == 1 && wid == 460 && player.getUid() != null && player.getUid() > 0) {
			int times = player.getUserWeaponNum();
			if (times == 3) {
				EPCombatAchievement ep=EPCombatAchievement.instance(new BaseEventParam(player.getUid()),14610);
				CombatEventPublisher.pubCombatAchievement(ep);
			}
		}

	}

	/**
	 * 添加法宝使用记录 并扣除法宝
	 * @param player
	 * @param wid
	 */
	private void addEffectWeaponLog(Player player, WeaponLog log) {
		int wid=log.getWeaponId();
		if (!player.isOwnTGCF()){
			TreasureEventPublisher.pubTDeductEvent(player.getUid(), wid, 1, WayEnum.FIGHT_ATTACK_EXPEND, new RDCommon());
		}
		player.addEffectWeaponLog(new Weapon(wid, 1));
		log.setDeductWeapon(true);
	}




}
