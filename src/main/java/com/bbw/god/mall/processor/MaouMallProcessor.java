package com.bbw.god.mall.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.CfgMaouMallAuth;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.mall.RDMallInfo;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.RDMaouMallInfo;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.maou.alonemaou.UserAloneMaouData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 魔王商店兑换购买
 * @date 2019/12/25 11:45
 */
@Service
public class MaouMallProcessor extends AbstractMallProcessor {
	@Autowired
	private AwardService awardService;
	@Autowired
	private GameUserService gameUserService;

	MaouMallProcessor() {
		this.mallType = MallEnum.MAOU;
	}

	@Override
	public RDMallList getGoods(long guId) {
		RDMallList rd = new RDMallList();
		setMaouMallList(guId, MallTool.getMallConfig().getMaouMalls(), rd);
		return rd;
	}

	@Override
	public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
		int num = mall.getNum() * buyNum;
		Award award = new Award(mall.getGoodsId(), AwardEnum.fromValue(mall.getItem()), num);
		String broadcastPrefix = "在" + WayEnum.EXCHANGE_MAOU.getName();
		awardService.fetchAward(guId, Arrays.asList(award), WayEnum.EXCHANGE_MAOU, broadcastPrefix, rd);
	}

	@Override
	protected List<UserMallRecord> getUserMallRecords(long guId) {
		return null;
	}

	private void setMaouMallList(long guId, List<CfgMallEntity> fMalls, RDMallList rd) {
		List<RDMallInfo> rdMallInfos = new ArrayList<>();
		for (CfgMallEntity mall : fMalls) {
			RDMaouMallInfo maouMallInfo = RDMaouMallInfo.fromMall(mall);
			CfgMaouMallAuth maouMallAuth = MallTool.getAuthMaouMallConfig(mall.getGoodsId());
			maouMallInfo.setAuthority(maouMallAuth.getAuthority());
			maouMallInfo.setMaouLevelFromAuth();
			maouMallInfo.setMaouTypeFromAuth();
			rdMallInfos.add(maouMallInfo);
		}
		rd.setMallGoods(rdMallInfos);
		rd.setMallType(mallType.getValue());
		UserAloneMaouData userAloneMaouData = this.gameUserService.getSingleItem(guId, UserAloneMaouData.class);
		if (userAloneMaouData != null) {
			rd.setUserAuthList(userAloneMaouData.getMaouAuthList());
		}
	}

	@Override
	public void checkAuth(long uid, CfgMallEntity mall) {
		if (mall.getType().equals(MallEnum.MAOU.getValue())) {
			Integer goodsId = MallTool.getMall(mall.getId()).getGoodsId();
			CfgMaouMallAuth maouMall = MallTool.getAuthMaouMallConfig(goodsId);
			UserAloneMaouData aloneMaouData = gameUserService.getSingleItem(uid, UserAloneMaouData.class);
			// 判断权限
			if (aloneMaouData == null || !aloneMaouData.getMaouKilledRecord().contains(maouMall.getAuthority())) {
				throw new ExceptionForClientTip("mall.not.auth");
			}
		}
	}
}
