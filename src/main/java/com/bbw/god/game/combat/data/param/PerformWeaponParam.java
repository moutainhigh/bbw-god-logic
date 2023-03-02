package com.bbw.god.game.combat.data.param;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.weapon.WeaponLog;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用法宝参数
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-04 18:40
 */
@Slf4j
@Data
@NoArgsConstructor
public class PerformWeaponParam {
	private Combat combat; //战斗数据对象
	private PlayerId performPlayerId;//使用法宝的玩家标识
	private int weaponId = -1;//法宝ID
	private List<Integer> multiplePos=new ArrayList<Integer>();
	private boolean RunParam=false;//法宝是否立即生效
	private int canEffectTimes=1;//法宝生效的回合数

	public static PerformWeaponParam instance(Combat combat, PlayerId performPlayerId, int weaponId,List<Integer> multiplePos) {
		PerformWeaponParam param=new PerformWeaponParam();
		param.setCombat(combat);
		param.setPerformPlayerId(performPlayerId);
		param.setWeaponId(weaponId);
		param.setMultiplePos(multiplePos);
		return param;
	}


	public PerformWeaponParam(Combat combat, PlayerId performPlayerId, int weaponId) {
		this.combat = combat;
		this.performPlayerId = performPlayerId;
		this.weaponId = weaponId;
	}
	

	public PerformWeaponParam(Combat combat, PlayerId performPlayerId, WeaponLog wl) {
		this.combat = combat;
		this.performPlayerId = performPlayerId;
		this.weaponId = wl.getWeaponId();
		multiplePos=wl.getTargetPos();
	}
	
	public void setTargetPos(int pos) {
	//	this.targetPos=pos;
	}
	
	public void resetPos(int pos) {
		this.multiplePos.clear();
		this.multiplePos.add(pos);
	}
	public void resetPos(List<Integer> pos) {
		this.multiplePos.clear();
		this.multiplePos.addAll(pos);
	}
	public Integer getTargetPos() {
		if ( this.multiplePos.isEmpty()) {
			return -1;
		}
		return this.multiplePos.get(0);
	}
	public void setMultiplePos(List<Integer> vals) {
		this.multiplePos=vals;
	}

	/**
	 * 获取武器（法宝）的玩家
	 * @return
	 */
	public Player getPerformPlayer() {
		return combat.getPlayer(performPlayerId);
	}

	/**
	 * 获取武器（法宝）攻击的目标卡牌(返回的卡牌不一定是阵上的卡)
	 * @return
	 */
	public BattleCard getBattleCard() {
		BattleCard card=combat.getBattleCardByPos(getTargetPos());
		return card;
	}

	/**
	 * 对手卡牌
	 * @return
	 */
	@NonNull
	public List<BattleCard> getPerformPlayerPlayingCards(boolean includeYunTai) {
		return combat.getPlayingCards(performPlayerId, includeYunTai);//对手卡牌
	}

	/**
	 * 获取武器名称
	 * @return
	 */
	public String getWeaponName() {
		CfgTreasureEntity t = TreasureTool.getTreasureById(weaponId);
		return t.getName();
	}

	/**
	 * 获取客户端动画序列
	 * @return
	 */
	public int getNextAnimationSeq() {
		return this.combat.getAnimationSeq();
	}
	
	/**
	 * 获取对手玩家
	 */
	@NonNull
	public Player getOppoPlayer() {
		return combat.getOppoPlayer(performPlayerId);
	}

	/**
	 * 对手卡牌
	 * @return
	 */
	@NonNull
	public List<BattleCard> getOppoPlayingCards(boolean includeYunTai) {
		PlayerId oppoPlayerId = Combat.getOppoId(performPlayerId);//对手Id
		return combat.getPlayingCards(oppoPlayerId, includeYunTai);//对手卡牌
	}

	/**
	 * 不能对自己使用法宝
	 */
	public void effectOppoPlayer() {
		PlayerId targetPlayerId = PositionService.getPlayerIdByPos(getTargetPos());
		if (targetPlayerId == performPlayerId) {
			String i18nKey = "combat.weapon.effect.oppo";
			ExceptionForClientTip tip = new ExceptionForClientTip(i18nKey, getWeaponName());
			throw tip;
		}
	}

	public void effectSelf() {
		PlayerId targetPlayerId = PositionService.getPlayerIdByPos(getTargetPos());
		if (targetPlayerId != performPlayerId) {
			String i18nKey = "combat.weapon.effect.self";
			ExceptionForClientTip tip = new ExceptionForClientTip(i18nKey, getWeaponName());
			throw tip;
		}
	}
}