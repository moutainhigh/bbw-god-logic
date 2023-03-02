package com.bbw.god.mall.store;

import com.bbw.common.StrUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.mall.RDMallInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年3月12日 上午11:22:12 类说明 积分商店所需的商品项
 */
@Data
public class RDStoreGoodsInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer mallId;// 商城实例ID（根据该ID索引到商品）
	private Integer item = null;// 物品分类
	private Integer realId = null;// 实际ID（用于客户端展示图标）
	private Integer paramInt = null;//星级
	private Integer quantity = 1;
	private List<BuyType> buyTypes = null;// 购买方式
	private List<Award> giftAwards;//礼包奖励详情列表，有是才返回
	private Integer lock = null;//锁定
	/** 礼包使用次数 */
	private Integer giftUseTimes;
	/** 保底次数 */
	private Integer minGuaranteeNum;
	/** 子类型 */
	private Integer subtype = 0;

	public static RDStoreGoodsInfo instance(RDMallInfo rdMallInfo, BuyType buyType) {
		RDStoreGoodsInfo rdStoreGoodsInfo = new RDStoreGoodsInfo();
		rdStoreGoodsInfo.addBuyType(buyType);
		rdStoreGoodsInfo.setRealId(rdMallInfo.getRealId());
		rdStoreGoodsInfo.setMallId(rdMallInfo.getMallId());
		rdStoreGoodsInfo.setItem(rdMallInfo.getItem());
		rdStoreGoodsInfo.setQuantity(rdMallInfo.getQuantity());
		if (rdMallInfo.getLock() != null) {
			rdStoreGoodsInfo.setLock(rdMallInfo.getLock());
		}
		rdStoreGoodsInfo.setSubtype(rdMallInfo.getSubtype());
		return rdStoreGoodsInfo;
	}

	public void addBuyType(BuyType type) {
		if (buyTypes == null) {
			buyTypes = new ArrayList<RDStoreGoodsInfo.BuyType>();
		}
		buyTypes.add(type);
	}
	@Data
	public static class BuyType {
		private Integer price = null;// 价格
		private Integer consume = null;// 消耗单位
		private Integer limit = null;// 购买限制
		private Integer boughtTimes = null;// 已购次数
		private String permit = null;// 等级许可文字 未解锁时需要传递
		public static BuyType instance(RDMallInfo rdMallInfo){
			BuyType buyType=new BuyType();
			buyType.setPrice(rdMallInfo.getPrice());
			buyType.setLimit(rdMallInfo.getLimit());
			if (rdMallInfo.getBoughtTimes()!=null){
				buyType.setBoughtTimes(rdMallInfo.getBoughtTimes());
			}else if (rdMallInfo.getRemainTimes()!=null){
				buyType.setBoughtTimes(rdMallInfo.getLimit() - rdMallInfo.getRemainTimes());
			}
			buyType.setConsume(rdMallInfo.getUnit());
			if (StrUtil.isNotBlank(rdMallInfo.getAuthStr())){
				buyType.setPermit(rdMallInfo.getAuthStr());
			}
			return buyType;
		}
	}

}
