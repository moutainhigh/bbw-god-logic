package com.bbw.god.game.combat.data;

import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.attack.Effect.EffectResultType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-23 21:05
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnimationSequence implements Serializable {
	private static final long serialVersionUID = -2735661616385786500L;
	private int type = EffectResultType.PLAY_ANIMATION.getValue();
	private int seq = 0; //sequence,排序号
	private List<Animation> list = new ArrayList<Animation>();

	public AnimationSequence(int seq, EffectResultType resultType) {
		this.type = resultType.getValue();
		this.seq = seq;
	}

	public AnimationSequence(Effect effect) {
		this.type = effect.getResultType().getValue();
		this.seq = effect.getSequence();
	}

	public void add(Animation ani) {
		this.list.add(ani);
	}

	@Data
	public static class Animation implements Serializable {
		private static final long serialVersionUID = 3003849540919298893L;
		private Integer pos = null;
		private Integer pos1 = null; // from 
		private Integer pos2 = null; // to
		private Integer skill = null; //0为普攻,[1,10000]之间为技能ID
		private Integer hp = null;
		private Integer roundHp = null;
		private Integer atk = null;
		private Integer mp = null;
		private Integer status = null;

		private String cards = null;// 卡牌数据
	}
}