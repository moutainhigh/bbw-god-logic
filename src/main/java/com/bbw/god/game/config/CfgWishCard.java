package com.bbw.god.game.config;

import com.bbw.common.DateUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 许愿卡
 *
 * @author suhq
 * @date 2019-05-07 11:29:17
 */
@Data
public class CfgWishCard implements CfgInterface {
	private String key;
	private List<WishCard> goldWishCards;// 金卡池许愿卡列表
	private List<WishCard> woodWishCards;// 木卡池许愿卡列表
	private List<WishCard> waterWishCards;// 水卡池许愿卡列表
	private List<WishCard> fireWishCards;// 火卡池许愿卡列表
	private List<WishCard> earthWishCards;// 土卡池许愿卡列表
	private List<WishCard> wanwuWishCards;// 万物卡池许愿卡列表
	private List<WishCard> juXianCards = new ArrayList<>();// 聚贤许愿卡列表
	private List<WishCard> limitTimeWishCards;

	private List<List<WishCard>> wishCards;

	public List<List<WishCard>> getWishCards() {
		if (wishCards == null) {
			wishCards = Arrays.asList(goldWishCards, woodWishCards, waterWishCards, fireWishCards,
					earthWishCards, wanwuWishCards, juXianCards, limitTimeWishCards);
		}
		return wishCards;
	}

	@Data
	public static class WishCard {
		// 卡牌ID
		private Integer id;
		// 卡牌名称
		private String name;
		// 需要心愿值
		private Integer needWish;
		// 是否新卡
		private Integer isNew = 0;
		// 新卡截止时间 yyyyMMdd
		private Integer newDateEnd;

		public boolean isNotNewCard() {
			return !isNewCard();
		}

		public boolean isNewCard() {
			int nowDateInt = DateUtil.toDateInt(DateUtil.now());
			return isNew == 1 && newDateEnd > nowDateInt;
		}
	}

	@Override
	public String getId() {
		return this.key;
	}

	@Override
	public int getSortId() {
		return 1;
	}

}
