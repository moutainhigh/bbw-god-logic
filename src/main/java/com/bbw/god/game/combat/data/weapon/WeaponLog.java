package com.bbw.god.game.combat.data.weapon;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 武器（法宝）使用记录,使用一次记录一条
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-04 18:34
 */
@Data
@NoArgsConstructor
public class WeaponLog implements Serializable {
	private static final long serialVersionUID = -6984268274539721765L;
	private int weaponId = 0;//武器、法宝ID
	private int residueTimes;//剩余次数
	private boolean deductWeapon=false;
	private int beginRound=0;
	private List<Integer> targetPos = new ArrayList<Integer>();//法宝使用的对象位置

	public WeaponLog(int weaponId,List<Integer> pos, int residueTimes,int beginRound) {
		this.weaponId = weaponId;
		this.targetPos=pos;
		this.residueTimes=residueTimes;
		this.beginRound=beginRound;
	}

	public void deductUseTimes(){
		this.residueTimes--;
	}
	public void resetTargetPos(List<Integer> pos) {
		this.targetPos.clear();
		this.targetPos.addAll(pos);
	}
}
