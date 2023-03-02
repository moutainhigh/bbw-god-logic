package com.bbw.god.game.combat.data.weapon;

import java.io.Serializable;

import lombok.Data;

/**战斗法宝
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-04 18:46
 */
@Data
public class Weapon implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id = 0;//标识
	private int num = 0;//数量

	public Weapon(int id, int num) {
		this.id = id;
		this.num = num;
	}
	public void addNum(int number) {
		this.num+=number;
	}
	
	public void decNum(int number) {
		this.num-=number;
	}
}
