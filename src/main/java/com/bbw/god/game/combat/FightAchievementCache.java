package com.bbw.god.game.combat;

import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年5月11日 上午9:37:34 类说明
 */
@Data
public class FightAchievementCache implements Serializable {
	private static final long serialVersionUID = 1L;
	private long combatId = 0;
	private boolean has4or5StarCard = false;
	// 攒心钉生效次数
	private Integer effectZanXD = 0;
	// 封神将卡牌拉回战场
	private Integer effectFenS = 0;
	// 5张卡牌中毒死亡
	private Integer effectZhongDuDie = 0;
	// 定神丹后抵挡技能
	private Set<Integer> dingSDEffect = new HashSet<Integer>();
	// 战斗中乾坤攻造成的伤害值
	private Integer loseHpByQianKG = 0;
	// 一次性使对方坟场10张卡牌变为鬼兵
	private boolean effectDiscardToGuiB = false;

	public void addLoseHpByQianKG(int hp) {
		loseHpByQianKG += hp;
	}

}
