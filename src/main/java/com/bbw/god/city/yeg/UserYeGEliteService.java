package com.bbw.god.city.yeg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.common.JSONUtil;
import com.bbw.god.gameuser.GameUserService;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年2月27日 下午3:16:25 
* 类说明 
*/
@Service
public class UserYeGEliteService {

	@Autowired
	private GameUserService gameUserService;
	private static int maxLevel = 40;// 最高40级
	public UserYeGElite getUserYeGElite(long uid) {
		return gameUserService.getSingleItem(uid, UserYeGElite.class);
	}

	/**
	 * 获取精英野怪等级
	 * 
	 * @param type
	 * @param uid
	 * @return
	 */
	public Integer getYeGLevel(int type, long uid) {
		UserYeGElite uYeGElite = getUserYeGElite(uid);
		if (uYeGElite == null) {
			return 10;
		}
		switch (type) {
		case 10:
			return uYeGElite.getAttackJ();
		case 20:
			return uYeGElite.getAttackM();
		case 30:
			return uYeGElite.getAttackS();
		case 40:
			return uYeGElite.getAttackH();
		case 50:
			return uYeGElite.getAttackT();
		default:
			break;
		}
		return 10;
	}

	/**
	 * 更新精英怪等级
	 * 
	 * @param type
	 * @param uid
	 */
	public void updateYeGLevel(int type, long uid) {
		UserYeGElite uYeGElite = getUserYeGElite(uid);
		if (uYeGElite == null) {
			uYeGElite = UserYeGElite.instance(uid);
			gameUserService.addItem(uid, uYeGElite);
		}
		switch (type) {
		case 10:
			if (uYeGElite.getAttackJ() < maxLevel) {
				uYeGElite.setAttackJ(uYeGElite.getAttackJ() + 1);
			}
			break;
		case 20:
			if (uYeGElite.getAttackM() < maxLevel) {
				uYeGElite.setAttackM(uYeGElite.getAttackM() + 1);
			}
			break;
		case 30:
			if (uYeGElite.getAttackS() < maxLevel) {
				uYeGElite.setAttackS(uYeGElite.getAttackS() + 1);
			}
			break;
		case 40:
			if (uYeGElite.getAttackH() < maxLevel) {
				uYeGElite.setAttackH(uYeGElite.getAttackH() + 1);
			}
			break;
		case 50:
			if (uYeGElite.getAttackT() < maxLevel) {
				uYeGElite.setAttackT(uYeGElite.getAttackT() + 1);
			}
			break;
		}
		gameUserService.updateItem(uYeGElite);
	}
}
