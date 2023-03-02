package com.bbw.god.game.config.city;

import com.bbw.god.city.yeg.CfgYeGuai;
import com.bbw.god.city.yeg.CfgYeGuaiEntity;
import com.bbw.god.game.config.Cfg;

/**
 * 野怪方法
 * 
 * @author suhq
 * @date 2019年2月27日 下午12:29:59
 */
public class YgTool {

	/**
	 * 获得野怪卡牌生成实例
	 * 
	 * @return
	 */
	public static CfgYeGuaiEntity getYGCards(int ygLevel) {
		return Cfg.I.get(ygLevel, CfgYeGuaiEntity.class);
	}

	/**
	 * 野怪的最高等级
	 * 
	 * @return
	 */
	public static int getMaxYGLevel() {
		return Cfg.I.get(CfgYeGuaiEntity.class).size();
	}

	/**
	 * 野怪配置
	 * 
	 * @return
	 */
	public static CfgYeGuai getYgConfig() {
		return Cfg.I.getUniqueConfig(CfgYeGuai.class);
	}
}
