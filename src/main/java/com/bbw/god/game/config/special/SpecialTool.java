package com.bbw.god.game.config.special;

import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import com.bbw.god.game.config.Cfg;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 特产工具类
 * 
 * @author suhq
 * @date 2018年10月22日 上午11:14:24
 */
public class SpecialTool {
	private static final int[] unlockLowCitySpecial={0,1,2,3,4,5,6,6,7,7,8};
	private static final int[] unlockHighCitySpecial={1,2,3,4,5,6,7,8,8,9,9};

	public static CfgSpecialEntity getSpecialById(int specialId) {
		CfgSpecialEntity cfgSpecial = Cfg.I.get(specialId, CfgSpecialEntity.class);
		if (cfgSpecial == null) {
			throw CoderException.high("无效的特产" + specialId);
		}
		return cfgSpecial;
	}

	public static CfgSpecialEntity getRandomSpecial() {
		List<CfgSpecialEntity> allSpecials = getSpecials();
		return PowerRandom.getRandomFromList(allSpecials);
	}

	/**
	 * 随机特产
	 * 
	 * @param type
	 * @return
	 */
	public static CfgSpecialEntity getRandomSpecial(SpecialTypeEnum type) {
		List<CfgSpecialEntity> specials = getSpecials(type).stream().filter(tmp -> !tmp.isUpdateSpecial()).collect(Collectors.toList());
		return PowerRandom.getRandomFromList(specials);
	}


	public static int getSpecialIndexByTcpLevel(int cityLevel, int tcpLevel) {
		int index = 0;
		if (tcpLevel == 0) {
			index = 1;
		} else if (tcpLevel <= 6) {
			index = tcpLevel + 1;
		} else if (tcpLevel == 8) {
			index = 8;
		} else if (tcpLevel == 10) {
			index = 9;
		}

		if (cityLevel <= 2) {
			index--;
		} else {
			if (tcpLevel == 7 || tcpLevel == 9) {
				index--;
			}
		}
		return index;
	}
	/**
	 * 根据特产铺特产解锁特产
	 * @param cityLevel
	 * @param tcpLevel
	 * @return
	 */
	public static int getCitySpecialUnlockIndexByTcpLv(int cityLevel, int tcpLevel){
		if (cityLevel<=2){
			return unlockLowCitySpecial[tcpLevel];
		}
		return unlockHighCitySpecial[tcpLevel];
	}

	/**
	 * 根据解锁的索引 获得解锁该特产需要的特产铺级别
	 * @param cityLevel
	 * @param index
	 * @return
	 */
	public static int getCitySpecialUnlockTcpLvByUnlockIndex(int cityLevel, int unlockIndex) {
		if (cityLevel <= 2) {
			for (int i = 0; i < unlockLowCitySpecial.length; i++) {
				if (unlockLowCitySpecial[i]==unlockIndex){
					return i;
				}
			}
		} else {
			for (int i = 0; i < unlockHighCitySpecial.length; i++) {
				if (unlockHighCitySpecial[i]==unlockIndex){
					return i;
				}
			}
		}
		return 10;
	}

	public static List<CfgSpecialEntity> getSpecials() {
		return Cfg.I.get(CfgSpecialEntity.class);
	}

	public static List<CfgSpecialEntity> getSpecials(SpecialTypeEnum type) {
		List<CfgSpecialEntity> allSpecials = getSpecials();
		List<CfgSpecialEntity> specials = allSpecials.stream().filter(tmp -> tmp.getType() == type.getValue()).collect(Collectors.toList());
		return specials;
	}

	public static List<CfgSpecialEntity> getLowHighSpecials() {
		List<CfgSpecialEntity> allSpecials = getSpecials();
		List<CfgSpecialEntity> specials = allSpecials.stream().filter(tmp -> tmp.getType() <= SpecialTypeEnum.HIGH.getValue()).collect(Collectors.toList());
		return specials;
	}

	public static CfgSpecial getSpecialCfg() {
		return Cfg.I.getUniqueConfig(CfgSpecial.class);
	}

	/**
	 * 六十四卦 可覆盖的特产
	 * @return
	 */
	public static List<CfgSpecialEntity> getHexagramSpecials(){
		List<CfgSpecialEntity> allSpecials = getSpecials();
		List<CfgSpecialEntity> specials = allSpecials.stream().filter(tmp -> tmp.getType() <= SpecialTypeEnum.TOP.getValue()).collect(Collectors.toList());
		return specials;
	}

	/**
	 * 六十四卦 可覆盖的特产ID
	 * @return
	 */
	public static List<Integer> getHexagramSpecialIds(){
		List<CfgSpecialEntity> specials = getHexagramSpecials();
		return specials.stream().map(CfgSpecialEntity::getId).collect(Collectors.toList());
	}

	/**
	 * 获取所有不占背包格子的特产
	 * @return
	 */
	public static List<Integer> getAllExcludeBagSpecialIds(){
		return getSpecials().stream().filter(p->p.isExcludeBag()).map(CfgSpecialEntity::getId).collect(Collectors.toList());
	}

	/**
	 * 获取剔除随机事件的特产
	 *
	 * @return
	 */
	public static List<Integer> getAllExcludeRandomEventSpecialIds() {
		return getSpecials().stream().filter(p -> p.isExcludeRandomEvent()).map(CfgSpecialEntity::getId).collect(Collectors.toList());
	}

	/**
	 * 获得自动购买节日道具id
	 *
	 * @return
	 */
	public static List<Integer> getAutoBuyHolidayPropIds() {
		List<CfgAutoBuyHolidayProps> cfgAutoBuyHolidayProps = Cfg.I.get(CfgAutoBuyHolidayProps.class);
		return cfgAutoBuyHolidayProps.stream().map(CfgAutoBuyHolidayProps::getId).collect(Collectors.toList());
	}
}
