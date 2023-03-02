package com.bbw.god.game.config.mall;

import com.bbw.god.ConsumeType;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-11-27 10:11:01
 */
@Data
public class CfgMallEntity implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id; //
	private Integer type; //
	private Integer item = AwardEnum.FB.getValue();// 默认道具
	private Integer goodsId; //
	private Integer num = 1;// 一份包含的数量，默认1
	private String name;
	private Integer serial; //
	private Integer unit = 1; //
	private Integer price; //
	private Integer originalPrice = price;
	private Integer discount = 100;
	private Integer limit = 0; // 0 无限制
	private Long peroid; // 商品周期，-7新手前七天 0永久，1每天 7每周 30每月 20181212111111截止时间
	private Boolean status = true;
	// private String award;// 礼包奖励内容
	private Integer probability = 0;// 概率

	/**
	 * 商店价格
	 *
	 * @param isDiscount 折扣
	 * @return
	 */
	public int getPrice(boolean isDiscount) {
		if (isDiscount) {
			return (int) (price * 8 / 10.0);
		}
		return price;
	}

	public ConsumeType gainConsumeType() {
		return ConsumeType.fromValue(unit);
	}

	@Override
	public int getSortId() {
		return this.getSerial();
	}
}
