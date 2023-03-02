package com.bbw.god.mall.processor;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.CfgSnatchTreasureMallCondition;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.config.treasure.CfgSkillScrollLimitEntity;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.mall.RDMallInfo;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.mall.skillscroll.cfg.SkillScrollTool;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 夺宝商品兑换购买处理器
 * @date 2020/06/30 17:21
 */
@Service
public class SnatchTreasureMallProcessor extends AbstractMallProcessor {
	@Autowired
	private AwardService awardService;
	@Autowired
	private ServerService serverService;
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private UserTreasureRecordService treasureRecordService;
	@Autowired
	private UserTreasureService userTreasureService;

	SnatchTreasureMallProcessor() {
		this.mallType = MallEnum.SNATCH_TREASURE;
	}

	@Override
	public RDMallList getGoods(long guId) {
		RDMallList rd = new RDMallList();
		setMallList(guId, MallTool.getMallConfig().getSnatchTreasureMalls(), rd);
		return rd;
	}

	@Override
	public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
		int num = mall.getNum() * buyNum;
		Award award = new Award(mall.getGoodsId(), AwardEnum.fromValue(mall.getItem()), num);
		String broadcastPrefix = WayEnum.SNATCH_TREASURE_EXCHANGE.getName();
		awardService.fetchAward(guId, Arrays.asList(award), WayEnum.SNATCH_TREASURE_EXCHANGE, broadcastPrefix, rd);
	}

	@Override
	protected List<UserMallRecord> getUserMallRecords(long guId) {
		List<UserMallRecord> userMallRecords = this.mallService.getUserMallRecord(guId, this.mallType);
		return userMallRecords.stream().filter(UserMallRecord::ifValid).collect(Collectors.toList());
	}

	private void setMallList(long uid, List<CfgMallEntity> fMalls, RDMallList rd) {
		List<RDMallInfo> rdMallInfos = new ArrayList<>();
		int sid = gameUserService.getActiveSid(uid);
		int openWeek = serverService.getOpenWeek(sid, DateUtil.now());
		for (CfgMallEntity mall : fMalls) {
			CfgSnatchTreasureMallCondition config = MallTool.getSnatchTreasureMallConfig(mall.getId());
			Integer showWeek = config.getShowWeek();
			Integer sellWeek = config.getSellWeek();
			if (openWeek >= showWeek) {
				RDMallInfo rdMallInfo = RDMallInfo.fromMall(mall);
				if (openWeek < sellWeek) {
					CfgServerEntity server = Cfg.I.get(sid, CfgServerEntity.class);
					Date date = DateUtil.addDays(server.getBeginTime(), (sellWeek - 1) * 7);
					int daysBetween = DateUtil.getDaysBetween(DateUtil.now(), date);
					rdMallInfo.setWaitDays(daysBetween);
				}
				rdMallInfos.add(rdMallInfo);
			}
		}
		rd.setMallGoods(rdMallInfos);
		rd.setMallType(mallType.getValue());
	}

	@Override
	public void checkAuth(long uid, CfgMallEntity mall) {
		CfgSnatchTreasureMallCondition config = MallTool.getSnatchTreasureMallConfig(mall.getId());
		Integer sellWeek = config.getSellWeek();
		int sid = gameUserService.getActiveSid(uid);
		CfgServerEntity server = Cfg.I.get(sid, CfgServerEntity.class);
		Date date = DateUtil.addDays(server.getBeginTime(), (sellWeek - 1) * 7);
		int daysBetween = DateUtil.getDaysBetween(DateUtil.now(), date);
		if (daysBetween > 0) {
			throw new ExceptionForClientTip("mall.not.open", daysBetween);
		}
		Optional<CfgSkillScrollLimitEntity> optional = SkillScrollTool.getCfgSkillScrollLimitEntity(mall.getGoodsId());
		if (optional.isPresent() && optional.get().getLimit()>0){
			int limitOwn=optional.get().getLimit();
			int useTimes = treasureRecordService.getUseTimes(uid, mall.getGoodsId());
			int num = userTreasureService.getTreasureNum(uid, mall.getGoodsId());
			if (useTimes >= limitOwn || num >= limitOwn) {
				throw new ExceptionForClientTip("can.not.buy.unique.skill");
			}
		}

	}
}
