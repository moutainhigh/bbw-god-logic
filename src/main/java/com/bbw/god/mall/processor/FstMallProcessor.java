package com.bbw.god.mall.processor;

import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.mall.RDMallInfo;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.fst.game.FstGameRanking;
import com.bbw.god.server.fst.game.FstGameService;
import com.bbw.god.server.fst.game.FstRankingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 常规道具
 *
 * @author suhq
 * @date 2018年12月6日 上午10:58:36
 */
@Service
public class FstMallProcessor extends AbstractMallProcessor {
	@Autowired
	private AwardService awardService;
	@Autowired
	private FstGameService fstGameService;
	
	FstMallProcessor() {
		this.mallType = MallEnum.FST;
	}

	@Override
	public RDMallList getGoods(long guId) {
		RDMallList rd = new RDMallList();
		toRdMallList(guId, MallTool.getMallConfig().getFstMalls(), false, rd);
		for (RDMallInfo mallGood : rd.getMallGoods()) {
			mallGood.setAuthStr(checkAuthString(guId,mallGood.getMallId()));
		}
		return rd;
	}

	@Override
	public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
		int num = mall.getNum() * buyNum;
		Award award = new Award(mall.getGoodsId(), AwardEnum.fromValue(mall.getItem()), num);
		String broadcastPrefix = "在" + WayEnum.EXCHANGE_FST.getName();
		awardService.fetchAward(guId, Arrays.asList(award), WayEnum.EXCHANGE_FST, broadcastPrefix, rd);
	}

	@Override
	protected List<UserMallRecord> getUserMallRecords(long guId) {
		List<UserMallRecord> favorableRecords = mallService.getUserMallRecord(guId, mallType);
		return favorableRecords.stream().filter(UserMallRecord::ifValid).collect(Collectors.toList());
	}

	@Override
	public void checkAuth(long uid, CfgMallEntity mall) {
		String auth = checkAuthString(uid, mall.getId());
		if (StrUtil.isBlank(auth)){
			return;
		}
		throw new ExceptionForClientTip("mall.not.auth");
	}
	
	private String checkAuthString(long uid,int mallId){
		Optional<FstGameRanking> rankingOp = fstGameService.getFstGameRankingOp(uid);
		FstRankingType pre=FstRankingType.NONE;
		int rank=-1;
		if (rankingOp.isPresent()){
			pre=FstRankingType.fromVal(rankingOp.get().getPreRankingType());
			rank=rankingOp.get().getPreRank();
		}
		switch (mallId){
			case 6532:
				//上轮结算人榜前120名
				if (pre.getType()<FstRankingType.REN.getType() || (pre.equals(FstRankingType.REN) && rank>120)) {
					return "上轮结算人榜前120名";
				}
				break;
			case 6533:
				//上轮结算黄榜前70名
				if (pre.getType()<FstRankingType.HUANG.getType() || (pre.equals(FstRankingType.HUANG) && rank>70)) {
					return "上轮结算黄榜前70名";
				}
				break;
			case 6534:
				//上轮结算玄榜前50名
				if (pre.getType()<FstRankingType.XUAN.getType() || (pre.equals(FstRankingType.XUAN) && rank>50)) {
					return "上轮结算玄榜前50名";
				}
				break;
			case 6535:
				//上轮结算地榜前30名
				if (pre.getType()<FstRankingType.DI.getType() || (pre.equals(FstRankingType.DI) && rank>30)) {
					return "上轮结算地榜前30名";
				}
				break;
			case 6536:
//				上轮结算天榜前10名
				if (pre.getType()<FstRankingType.TIAN.getType() || (pre.equals(FstRankingType.TIAN) && rank>10)) {
					return "上轮结算天榜前10名";
				}
				break;
			default:
				return "";
		}
		return "";
	}
}
