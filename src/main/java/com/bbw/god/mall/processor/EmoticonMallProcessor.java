package com.bbw.god.mall.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.RDMallInfo;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 表情包商店
 */
@Service
public class EmoticonMallProcessor extends AbstractMallProcessor {

	EmoticonMallProcessor() {
		this.mallType = MallEnum.EMOTICON;
	}

	@Override
	public RDMallList getGoods(long guId) {
		RDMallList rd = new RDMallList();
		toRdMallList(guId, MallTool.getMallConfig().getFaceMalls(), false, rd);
		return rd;
	}

	@Override
	protected void toRdMallList(long guId, List<CfgMallEntity> fMalls, boolean isExtraDiscount, RDMallList rd) {
		List<RDMallInfo> rdMallInfos = new ArrayList<>();
		for (CfgMallEntity mall : fMalls) {
			RDMallInfo mallInfo = RDMallInfo.fromMall(mall, mall.getPrice(isExtraDiscount));
			int remain=TreasureChecker.hasTreasure(guId,mall.getGoodsId())?0:1;
			mallInfo.setRemainTimes(remain);
			rdMallInfos.add(mallInfo);
		}
		rd.setMallGoods(rdMallInfos);
		rd.setMallType(mallType.getValue());
	}

	@Override
	public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
		int proId = mall.getGoodsId();
		if (TreasureChecker.hasTreasure(guId,proId) || buyNum > 1) {
			throw new ExceptionForClientTip("mall.onlyOne");
		}
		TreasureEventPublisher.pubTAddEvent(guId, proId, 1, WayEnum.MALL_BUY, rd);
	}

	@Override
	protected List<UserMallRecord> getUserMallRecords(long guId) {
		return null;
	}

}
