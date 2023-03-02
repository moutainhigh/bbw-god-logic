package com.bbw.god.server.god;

import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDCommon.RDCardInfo;
import com.bbw.god.rd.RDCommon.RDTreasureInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 神仙附体
 *
 * @author suhq
 * @date 2019年3月12日 上午10:09:12
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDAttachGod implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer addedGold = null;// 元宝
	private Long addedCopper = null;// 铜钱
	private Integer addedDices = null;// 体力
	private Integer addedGoldEle = null;// 金元素
	private Integer addedWoodEle = null;// 木元素
	private Integer addedWaterEle = null;// 水元素
	private Integer addedFireEle = null;// 火元素
	private Integer addedEarthEle = null;// 土元素
	private List<RDCardInfo> cards = null;// 卡牌
	private List<RDTreasureInfo> treasures = null;// 法宝

	private Integer godRemainTime = null;// 剩余时间
	private Integer godId = null;// 附体神仙

	public static RDAttachGod fromRDCommon(RDCommon rd) {
		RDAttachGod rdAttachGod = new RDAttachGod();
		rdAttachGod.setAddedCopper(rd.getAddedCopper());
		rdAttachGod.setAddedGold(rd.getAddedGold());
		rdAttachGod.setAddedDices(rd.getAddedDices());
		rdAttachGod.setAddedGoldEle(rd.getAddedGoldEle());
		rdAttachGod.setAddedWoodEle(rd.getAddedWoodEle());
		rdAttachGod.setAddedWaterEle(rd.getAddedWaterEle());
		rdAttachGod.setAddedFireEle(rd.getAddedFireEle());
		rdAttachGod.setAddedEarthEle(rd.getAddedEarthEle());
		rdAttachGod.setCards(rd.getCards());
		rdAttachGod.setTreasures(rd.getTreasures());

		rdAttachGod.setGodId(rd.getAttachedGod());
		// 兼容
		if (rdAttachGod.getGodId() != null && rdAttachGod.getGodId() == 520) {
			rdAttachGod.setGodId(510);
		}
		rdAttachGod.setGodRemainTime(rd.getGodRemainTime());

		// 顶层村庄获得的元素、卡牌等数据置空
		rd.setAddedCopper(null);
		rd.setAddedGold(null);
		rd.setAddedDices(null);
		rd.setAddedGoldEle(null);
		rd.setAddedWoodEle(null);
		rd.setAddedWaterEle(null);
		rd.setAddedFireEle(null);
		rd.setAddedEarthEle(null);
		rd.setCards(null);
		rd.setTreasures(null);
		return rdAttachGod;
	}
}
