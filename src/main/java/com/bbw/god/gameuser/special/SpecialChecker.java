package com.bbw.god.gameuser.special;

import com.bbw.common.ListUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;

import java.util.List;

/**
 * 特产检察器
 * 
 * @author suhq
 * @date 2018年11月24日 下午9:53:24
 */
public class SpecialChecker {
	private static UserSpecialService userSpecialService = SpringContextUtil.getBean(UserSpecialService.class);

	/**
	 * 检查是否拥有特产
	 * 
	 * @param userSpecial
	 */
	public static void checkIsOwnSpecial(UserSpecial userSpecial) {
		if (userSpecial == null) {
			throw new ExceptionForClientTip("special.not.own");
		}
	}

	/**
	 * 检查特产包是否已满
	 * 
	 * @param buyNum
	 */
	public static void checkIsFull(int buyNum,long uid) {
		UserSpecialService specialService = SpringContextUtil.getBean(UserSpecialService.class);
		int freeSize = specialService.getSpecialFreeSize(uid);
		if (buyNum > freeSize) {
			throw new ExceptionForClientTip("special.is.full", specialService.getSpecialLimit(uid) + "");
		}
	}

	/**
	 * 检查是否存在该特产（用于特产ID检验）
	 * 
	 * @param specialId
	 */
	public static void checkExist(int specialId) {
		CfgSpecialEntity special = SpecialTool.getSpecialById(specialId);
		if (special == null) {
			throw new ExceptionForClientTip("special.not.exist");
		}
	}

	/**
	 * 检查是否有足够的指定特产
	 * @param uid
	 * @param specialId
	 * @param num
	 */
	public static void checkIsEnough(long uid,int specialId,int num){
		List<UserSpecial> specials = userSpecialService.getOwnSpecials(uid);
		if (ListUtil.isNotEmpty(specials)){
			long count = specials.stream().filter(p -> p.getBaseId() == specialId).count();
			if (count>=num){
				return;
			}
		}
		CfgSpecialEntity special = SpecialTool.getSpecialById(specialId);
		throw new ExceptionForClientTip("special.not.enough",special.getName());
	}
}
