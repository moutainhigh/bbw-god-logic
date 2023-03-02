package com.bbw.god.mall;

import com.bbw.god.game.award.RDAward;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDMallInfo extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long recordId;// 记录id
	private Integer mallId;// 商城实例ID
	private Integer item = null;
	private Integer realId = null;
	private Integer quantity = 1;
	private Integer unit = null;
	private Integer price = null;
	private Integer originalPrice = null;
	private Integer discount = null;
	private Integer remainTimes = null;
	private Long remainTime = null;
	private Integer limit = null;
	private Integer rechargeId = null;// 直冲ID
	private List<RDAward> awards = null;// 周、月直冲礼包
	private Integer boughtTimes = null;//已购次数
	private Integer waitDays = null;// 等待天数，xx天后才可以购买
	private Integer num = null;
	private Integer lock = null;//锁定
	private String authStr = null;//权限描述
	/** 子类型 */
	private Integer subtype = 0;

	public static RDMallInfo fromMall(CfgMallEntity mall) {
		return fromMall(mall, mall.getPrice());
	}

	public static RDMallInfo fromMall(CfgMallEntity mall, int price, int remainTimes) {
		RDMallInfo rdMallInfo = fromMall(mall, price);
		rdMallInfo.setRemainTimes(remainTimes);
		rdMallInfo.setLimit(mall.getLimit());
		return rdMallInfo;
	}

	public static RDMallInfo fromMall(CfgMallEntity mall, int price) {
		RDMallInfo rdMallInfo = new RDMallInfo();
		rdMallInfo.setMallId(mall.getId());
		rdMallInfo.setItem(mall.getItem());
		rdMallInfo.setRealId(mall.getGoodsId());
		rdMallInfo.setRechargeId(MallTool.getRechargeId(mall.getGoodsId()));
		rdMallInfo.setPrice(price);
		rdMallInfo.setOriginalPrice(mall.getOriginalPrice());
		rdMallInfo.setDiscount(mall.getDiscount());
		rdMallInfo.setUnit(mall.getUnit());
		rdMallInfo.setQuantity(mall.getNum());
		rdMallInfo.setNum(mall.getNum());
		CfgTreasureEntity cfgTreasureEntity = TreasureTool.getTreasureById(mall.getGoodsId());
		if (null == cfgTreasureEntity) {
			rdMallInfo.setSubtype(mall.getItem());
			return rdMallInfo;
		}
		rdMallInfo.setSubtype(cfgTreasureEntity.getType());
		return rdMallInfo;
	}

	/**
	 * 设置折扣
	 *
	 * @param discount
	 */
	public void setDiscount(double discount) {
		discount = discount * 10;
		this.discount = (int) discount;
	}
}