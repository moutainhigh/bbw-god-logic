package com.bbw.god.mall.processor;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.businessgang.BusinessGangService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.treasure.TreasureResStatisticService;
import com.bbw.god.gameuser.statistic.resource.treasure.TreasureStatistic;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 轮回商店购买
 *
 * @author: suhq
 * @date: 2021/9/25 6:36 上午
 */
@Service
public class TransmigrationMallProcessor extends AbstractMallProcessor {

	@Autowired
	private AwardService awardService;
	@Autowired
	private BusinessGangService businessGangService;
	@Autowired
	private TreasureResStatisticService statisticService;
	/** 卷轴子类型 */
	private final static Integer SCROLL_SUBTYPE = 56;
	/** 默认卷轴 */
	private final static List<Integer> DEFAULT_SCROLL = Arrays.asList(21243, 21242, 21271);

	TransmigrationMallProcessor() {
		this.mallType = MallEnum.TRANSMIGRATION;
	}

	@Override
	public RDMallList getGoods(long guId) {
		RDMallList rd = new RDMallList();
		toRdMallList(guId, MallTool.getMallConfig().getTransmigrationMalls(), false, rd);
		businessGangService.removeUnlockGoods(guId, rd);
		rd.getMallGoods().removeIf(tmp -> isNotUnlockScroll(guId, tmp.getRealId()));
		return rd;
	}

	@Override
	public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
		businessGangService.checkUnlock(guId, mall);
		int num = mall.getNum() * buyNum;
		Award award = new Award(mall.getGoodsId(), AwardEnum.fromValue(mall.getItem()), num);
		String broadcastPrefix = "在" + WayEnum.EXCHANGE_TRANSMIGRATION.getName();
		awardService.fetchAward(guId, Arrays.asList(award), WayEnum.EXCHANGE_TRANSMIGRATION, broadcastPrefix, rd);
	}

	/**
	 * 检查权限
	 *
	 * @param uid
	 * @param mall
	 */
	@Override
	public void checkAuth(long uid, CfgMallEntity mall) {

		boolean isUnlockScroll = isNotUnlockScroll(uid, mall.getGoodsId());
		if (!isUnlockScroll) {
			return;
		}
		throw new ExceptionForClientTip("mall.not.auth");
	}

	@Override
	protected List<UserMallRecord> getUserMallRecords(long guId) {
		List<UserMallRecord> records = this.mallService.getUserMallRecord(guId, this.mallType);
		List<UserMallRecord> validRecords = records.stream().filter(umr -> umr.ifValid())
				.collect(Collectors.toList());
		return validRecords;
	}

	/**
	 * 是否未解锁卷轴
	 *
	 * @param uid
	 * @param srollId
	 * @return
	 */
	private boolean isNotUnlockScroll(long uid, Integer srollId) {
		//默认卷轴,无需解锁
		if (DEFAULT_SCROLL.contains(srollId)) {
			return false;

		}
		CfgTreasureEntity treasure = TreasureTool.getTreasureById(srollId);
		//不是法宝
		if (null == treasure) {
			return false;
		}
		//不是卷轴,无需解锁
		if (!SCROLL_SUBTYPE.equals(treasure.getType())) {
			return false;
		}
		//卷轴获得数量
		TreasureStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.GAIN,
				DateUtil.getTodayInt());
		int ownScrollNum = statistic.getTotalNum(TreasureTool.getTreasureById(srollId));
		//解锁
		if (0 < ownScrollNum) {
			return false;
		}
		//未解锁
		return true;
	}
}
