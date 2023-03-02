package com.bbw.god.gameuser.chamberofcommerce;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgCoc;
import com.bbw.god.game.config.CfgCoc.CfgCocHonorItem;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户帮助阅读奖励 返回集
* @author lwb  
* @date 2019年4月10日  
* @version 1.0  
*/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDCoc extends RDCommon implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer taskId=null;//任务ID
	private Long time=null;//任务刷新剩余时间  默认当前时间到24点
	private Integer honor=null;//头衔积分
	private Integer honorLevel=null; //头衔等级
	private List<RDCocHonorGift> honorList = null;// 头衔列表
	private Integer unclaimed=null;//提示是否有礼包
	private Integer refreshCount=null;//刷新次数
	private Integer complete=null;//可完成次数
	
	private Integer goldCoin=null;//金币数量
	private Integer tokenQuantity=null;//令牌数量
	private List<CocTask> taskList = null;// 任务列表
	
	private List<CocExpTask> expList = null;// 跑商历练任务列表
	private Integer expId=null;//跑商任务ID
	
	private List<CfgCoc.CocShop> shopList=null; //商品列表
	private Integer addCofcGold=null; //加金币
	private Integer addCofcScore=null;//加积分
	private CocTask taskInfo = null;
	private Integer cocLv = null;

	@Data
	public static class CocReward implements Serializable {
		private static final long serialVersionUID = 1L;
		private Integer giftId;// 礼包ID
		private int realId;// 物品id
		private int type;// 类型 其中商会的为90
		private int quantity;// 数量
		private int paramInt=0; //品级  默认0

		public static CocReward instance(Award award, int giftId) {
			CocReward cocReward = new CocReward();
			cocReward.setRealId(award.getAwardId());
			cocReward.setType(award.getItem());
			cocReward.setQuantity(award.getNum());
			cocReward.setGiftId(giftId);
			return cocReward;
		}
	}

	@Data
	public static class RDCocHonorGift implements Serializable {
		private static final long serialVersionUID = 1L;
		private Integer id;
		private Integer status; // 状态
		private Integer price; // 价格
		private Integer priceType; // 付费方式 目前为元宝
		private List<CocReward> rewards; // 礼包内容

		public static RDCocHonorGift instance(CfgCocHonorItem item) {
			RDCocHonorGift honor = new RDCocHonorGift();
			honor.setId(item.getLevel());
			honor.setPrice(item.getGiftPrice());
			honor.setPriceType(item.getPayType());
			honor.setStatus(CocConstant.STATUS_CAN_BUY);
			return honor;
		}
	}

}
