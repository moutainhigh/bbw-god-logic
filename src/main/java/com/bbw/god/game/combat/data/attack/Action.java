package com.bbw.god.game.combat.data.attack;

import com.bbw.god.game.combat.data.AnimationSequence;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 一次出手的结果
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-04 18:51
 */
@Data
public class Action implements Serializable {
	private static final long serialVersionUID = 5212207519327815326L;
	private List<AnimationSequence> clientActions = new ArrayList<>();
	private boolean takeEffect = false;
	private List<Effect> effects = new ArrayList<>();
	private boolean needAddAnimation=true;
	public boolean getTakeEffect() {
		return takeEffect;
	}

	/**
	 * 添加客户端动画
	 * @param action
	 */
	public void addClientAction(AnimationSequence action) {
		clientActions.add(action);
	}

	/**
	 * 添加行动效果
	 * @param effect
	 */
	public void addEffect(Effect effect) {
		effects.add(effect);
		takeEffect = true;
	}
	/**
	 * 添加行动效果
	 * @param effect
	 */
	public void addEffects(List<Effect> effectList) {
		effects.addAll(effectList);
		takeEffect = true;
	}
	/**
	 * 有供应
	 * @return
	 */
	public boolean existsEffect() {
		return !effects.isEmpty();
	}
	
	public void updateSeq(int seq) {
		for (Effect effect:effects) {
			effect.setSequence(seq);
		}
	}
}
