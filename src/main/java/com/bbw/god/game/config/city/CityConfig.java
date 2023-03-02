package com.bbw.god.game.config.city;

import com.bbw.common.SpringContextUtil;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 地图城市相关常量配置
 * 
 * @author suhq
 * @date 2018年11月9日 上午9:34:21
 */
@Getter
@Configuration
@Component
public class CityConfig {
	private OCData ocDATA = new OCData();
	private CCData ccData = new CCData();

	public static CityConfig bean() {
		return SpringContextUtil.getBean(CityConfig.class);
	}

	/**
	 * 非城池的城市数据
	 * 
	 * @author suhq
	 * @date 2018年11月30日 下午2:44:14
	 */
	@Getter
	public class OCData {
		// 庙宇需要的元
		private Integer myNeedGold = 20;
		// 鹿台一点经验对应多少铜钱
		private Integer ltCopperPerExp = 20;
		// 梦魇鹿台一点经验对应多少铜钱
		private Integer MYLTCopperPerExp = 16;
		// 鹿台一个元素对应多少个经验
		private Integer ltExpPerEle = 500;
		// 鹿台材料退还功能卡牌等级限制
		private Integer ltBackExpLv = 5;
		// 鹿台材料退还功能退还比例，70 =》70%
		private Integer ltLvBackPercent = 70;
		/** 鹿台卡牌阶数返还比例 */
		private Integer[] ltHvBackPercent = {90, 90, 90, 90, 80, 80, 70, 70, 60, 60};
		// 太一府需要捐赠的数量
		private Integer tyfNeedFillNum = 25;
		// 梦魇太一府需要捐赠的数量
		private Integer MYTyfNeedFillNum = 25;
	}

	/**
	 * 城市数据
	 * 
	 * @author suhq
	 * @date 2018年11月30日 下午2:46:51
	 */
	@Getter
	public class CCData {
		// 城池最高阶数
		private Integer topCCHierarchy = 5;
		// 1级城卡牌掉率
		private int[] gcdl1 = new int[] { 6000, 2500, 1500, 0, 0 };
		// 2级城卡牌掉率
		private int[] gcdl2 = new int[] { 5500, 2500, 2000, 0, 0 };
		// 3级城卡牌掉率
		private int[] gcdl3 = new int[] { 2500, 4900, 2400, 200, 0 };
		// 4级城卡牌掉率
		private int[] gcdl4 = new int[] { 2500, 2500, 4500, 500, 0 };
		// 5级城卡牌掉率
		private int[] gcdl5 = new int[] { 0, 0, 3600, 4400, 2000 };
		// 钱庄每级提升比例
		private Double qzCopperRate = 1.24;

		private List<int[]> gcdl = Arrays.asList(gcdl1, gcdl2, gcdl3, gcdl4, gcdl5);
	}
}
