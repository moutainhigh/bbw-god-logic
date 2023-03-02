package com.bbw.god.city.yed;

import com.bbw.god.city.RDCityInfo;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDCommon.RDSpecail;
import com.bbw.god.rd.RDCommon.RDTreasureInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 到达野地
 * 
 * @author suhq
 * @date 2019年3月18日 下午3:52:23
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class RDArriveYeD extends RDCityInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer event = null;// 事件
	private Integer eventType = null;// 事件类型 正面10、0中性、-10负面
    private String cityName = null;// 那座城的税收
    private Integer copyNum = null;// 税收份数
    private Integer pos = null;// 穿越地
    private Integer direction = null;// 穿越地方向
    private List<Long> reduceSpecial = null;//减少的特产的dataId    小偷偷的特产/富豪收购
    private List<Integer> reduceSpcialIds = null;
    private Integer priceRate = null;// 土豪收购倍数
    private Integer sellCount = null;// 土豪收购量
    private Integer supSpecialId = null;//土豪要收购的特产（基础Id）
    private Integer treasureId = null;// 事件获得青鸾、四不像、仙人送法宝
    private Long addedCopper = null;// 铜钱
    private Integer addedDices = null;// 体力
    private Long deductCopper = null;
    private List<RDSpecail> specials = null;// 老奶奶送特产
    private List<RDTreasureInfo> treasures = null;// 法宝
    private Integer addedGoldEle = null;// 金元素
    private Integer addedWoodEle = null;// 木元素
    private Integer addedWaterEle = null;// 水元素
    private Integer addedFireEle = null;// 火元素
    private Integer addedEarthEle = null;// 土元素
    private RDFightsInfo fightsInfo = null;
    private Integer fightType = null;
    private Integer tipEle = null;//压压乐提示元素
	private Long dataId;// 玩家奇遇对象存在redis中的id
	private Integer mallId;// 商品id
	private Integer cardId; // 卡牌id
	private Integer goodsId;// 物品id
	private Integer num;// 商品数量
	private Integer exp;// 经验
	private Long remainTime;// 剩余时间
	private Long godRemainTime;// 神仙剩余时间
	private Integer originalPrice;// 原价
	private Integer currentPrice;// 现价
	private Integer godId;// 神仙id
	private Integer addAttackAloneMaouTimes;// 独占魔王攻打增加次数
	private Boolean isAlreadyExtraOperation = false;// 是否已经进行拓展操作
	private Boolean canRecommendSpecial = false;// 是否可以推荐特产
	private RDCityInfo rdCityInfo;
	private RDGodReward godReward;
	/** 活动特殊野地事件 */
	private int specialYeDEventId;

	public void addReduceSpecial(Long id) {
		if (reduceSpecial == null) {
			reduceSpecial = new ArrayList<Long>();
		}
		reduceSpecial.add(id);
	}

	public void updateByRdCommon(RDCommon rd) {
		this.addedCopper = rd.getAddedCopper();
		this.addedDices = rd.getAddedDices();
		this.treasures = rd.getTreasures();
		this.addedGoldEle = rd.getAddedGoldEle();
		this.addedWoodEle = rd.getAddedWoodEle();
		this.addedWaterEle = rd.getAddedWaterEle();
		this.addedFireEle = rd.getAddedFireEle();
		this.addedEarthEle = rd.getAddedEarthEle();
		this.specials = rd.getSpecials();
		// 将顶层野地获得的置空
		rd.setAddedCopper(null);
		rd.setAddedDices(null);
		rd.setTreasures(null);
		rd.setAddedGoldEle(null);
		rd.setAddedWoodEle(null);
		rd.setAddedWaterEle(null);
		rd.setAddedFireEle(null);
		rd.setAddedEarthEle(null);
		rd.setSpecials(null);
	}

	public void updateEvent(CfgYeDiEventEntity ydEvent) {
		event = ydEvent.getId();
		eventType = ydEvent.getType();
	}

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@EqualsAndHashCode(callSuper = true)
	public static class RDGodReward extends RDCommon {
		private static final long serialVersionUID = 1L;
	}
}
