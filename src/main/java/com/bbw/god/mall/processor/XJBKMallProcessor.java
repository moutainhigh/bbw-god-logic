package com.bbw.god.mall.processor;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.RDXJBKMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;

/**
 * 星君宝库兑换购买
 * 
 * @author suhq
 * @date 2019-10-24 11:32:55
 */
@Slf4j
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class XJBKMallProcessor extends AbstractMallProcessor {

	@Autowired
	private AwardService awardService;
	@Autowired
	private UserTreasureService userTreasureService;

	XJBKMallProcessor() {
		this.mallType = MallEnum.XJBK;
	}

	@Override
	public RDMallList getGoods(long guId) {
		RDXJBKMallList rd = new RDXJBKMallList();
		toRdMallList(guId, MallTool.getMallConfig().getXjbkMalls(), false, rd);
		int shenShaNum = userTreasureService.getTreasureNum(guId, TreasureEnum.SS.getValue());
		rd.setOwnSsNum(shenShaNum);
		// 设置魂源数量
		int hunYuanNum = userTreasureService.getTreasureNum(guId, TreasureEnum.HY.getValue());
		rd.setOwnHyNum(hunYuanNum);
		//仙玉数量
		int xianYuNum = userTreasureService.getTreasureNum(guId, TreasureEnum.XY.getValue());
		rd.setOwnXyNum(xianYuNum);
		return rd;
	}

	@Override
	public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
        log.info(guId + ",星君宝库兑换开始执行deliver方法！");
		int num = mall.getNum() * buyNum;
		Award award = new Award(mall.getGoodsId(), AwardEnum.fromValue(mall.getItem()), num);
		String broadcastPrefix = "在" + WayEnum.EXCHANGE_XJBK.getName();
		awardService.fetchAward(guId, Arrays.asList(award), WayEnum.EXCHANGE_XJBK, broadcastPrefix, rd);
        log.info(guId + ",星君宝库兑换执行deliver方法结束！");
	}

	@Override
	protected List<UserMallRecord> getUserMallRecords(long guId) {
		return null;
	}

}
