package com.bbw.god.gameuser.statistic.behavior.card;

import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 进阶卡牌统计
 * @date 2020/4/21 15:42
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class HierarchyCardStatistic extends BehaviorStatistic {
	private Integer goldCard = 0;
	private Integer woodCard = 0;
	private Integer waterCard = 0;
	private Integer fireCard = 0;
	private Integer earthCard = 0;

	private Integer oneStar = 0;
	private Integer twoStar = 0;
	private Integer threeStar = 0;
	private Integer fourStar = 0;
	private Integer fiveStar = 0;

	public HierarchyCardStatistic(Integer today, Integer total, Integer date, Integer goldCard, Integer woodCard,
								  Integer waterCard, Integer fireCard, Integer earthCard, Integer oneStar,
								  Integer twoStar, Integer threeStar, Integer fourStar, Integer fiveStar) {
		super(today, total, date, BehaviorType.CARD_HIERARCHY);
		this.goldCard = goldCard;
		this.woodCard = woodCard;
		this.waterCard = waterCard;
		this.fireCard = fireCard;
		this.earthCard = earthCard;
		this.oneStar = oneStar;
		this.twoStar = twoStar;
		this.threeStar = threeStar;
		this.fourStar = fourStar;
		this.fiveStar = fiveStar;
	}

	public HierarchyCardStatistic() {
		super(BehaviorType.CARD_HIERARCHY);
	}

	public void increment(int cardType, int cardStar) {
		switch (cardStar) {
			case 1:
				this.oneStar += 1;
				break;
			case 2:
				this.twoStar += 1;
				break;
			case 3:
				this.threeStar += 1;
				break;
			case 4:
				this.fourStar += 1;
				break;
			case 5:
				this.fiveStar += 1;
				break;
			default:
				break;
		}
		TypeEnum typeEnum = TypeEnum.fromValue(cardType);
		switch (typeEnum) {
			case Gold:
				this.goldCard += 1;
				break;
			case Wood:
				this.woodCard += 1;
				break;
			case Water:
				this.waterCard += 1;
				break;
			case Fire:
				this.fireCard += 1;
				break;
			case Earth:
				this.earthCard += 1;
				break;
			default:
				break;
		}
		this.setToday(this.getToday() + 1);
		this.setTotal(this.getTotal() + 1);
	}
}
