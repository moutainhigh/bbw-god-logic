package com.bbw.god.mall.cardshop;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 许愿卡池数据
 *
 * @author suhq
 * @date 2019-05-08 08:56:01
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDWishCardPool extends RDSuccess {
	// 卡池内的卡牌
	private List<RDWishCard> cards;

	@Getter
	@Setter
	@ToString
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RDWishCard {
		// 卡牌ID
		private Integer id;
		// 需要的许愿值
		private Integer needVow;
		// 活动额外赠送的许愿值
		private Integer extraVow;

		private Integer isNew = 0;

		public RDWishCard(int id, int extraVow, int needVow, boolean isNew) {
			this.id = id;
			this.extraVow = extraVow;
			this.needVow = needVow;
			this.isNew = isNew ? 1 : 0;
		}
	}

}
