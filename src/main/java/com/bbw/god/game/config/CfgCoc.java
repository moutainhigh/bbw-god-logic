package com.bbw.god.game.config;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.chamberofcommerce.CocExpTask;
import com.bbw.god.gameuser.chamberofcommerce.CocTask.CocSpecial;

import lombok.Data;

/**
 * 商会商店相关
* @author lwb  
* @date 2019年4月16日  
* @version 1.0  
*/
@Data
public class CfgCoc  implements CfgInterface{
	private String key;
	private List<CfgCocHonorItem> honorList;//头衔列表
	private List<Head> headList;// 解锁头像列表

	private List<CocShop> shopList;// 商品列表
	private List<CocShop> giftList;// 头衔礼包列表
	private List<ProductAward> productAwards;// 礼包映射

	private List<CfgCocPrivilegeItem> privilegeList;//特权列表

	
	private List<CocSpecial> lowSpecials; //低级特产
	private List<CocSpecial> middleSpecials;//中级特产
	private List<CocSpecial> heighSpecials;//高级特产

	private List<ProductAward> cocTaskRewards;// 商会任务奖励
	private List<CocExpTask> expList;// 卖特产任务
	private List<ProductAward> expTaskAwards;// 历练任务奖励

	@Override
	public Serializable getId() {

		return key;
	}

	@Override
	public int getSortId() {
		return 1;
	}

	@Data
	public static class ProductAward {
		private Integer productId;// 产品ID
		private String memo;
		private List<Award> awardList;// 产品组
	}

	// 头衔
	@Data
	public static class CfgCocHonorItem {
		private Integer level; // 头衔等级
		private Integer giftId;// 礼包ID
		private Integer giftPrice;// 礼包价格
		private Integer target;// 达成积分
		private Integer payType = 1;// 消耗类型 默认 元宝
		private String title;// 头衔名称
		private String memo;// 解锁信息
	}

	@Data
	public static class Head {
		private Integer baseId;// 头像ID
		private Integer lv;// 解锁等级
		private String memo;
	}
	
	//特权
	@Data
	public static class CfgCocPrivilegeItem{
		private Integer level;
		private Integer type;
		private Integer shopId;
		private Integer quantity;
		private String memo;
	}

	@Data
	public static class CocShop implements Serializable{
		private static final long serialVersionUID = 1L;
		private Integer id = null;// 物品ID
		private Integer goodId = null;// 实际 物品Id
		private Integer propType = null;// 商品类型
		private Integer price = null;// 价格
		private Integer priceType = null;// 消费类型 1 元宝 2铜钱 3 商会金币 4 神仙大会仙豆
		private Integer limitCount = null; // 限购 数量
		private Integer bought=0; //已购数量
		private Integer minLevel = null; // 购买 商会等级需求
		private String memo = null;// 商品名称
		private String permit = null;// 解锁等级

		public boolean buyUpperLimit() {
			if (bought != 0 && limitCount == bought) {
				return true;
			}
			return false;
		}
	}
}
