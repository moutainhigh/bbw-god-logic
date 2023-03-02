package com.bbw.god.mall.processor;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.RDMallInfo;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.pay.ProductService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 每类产品都有自己的处理类，并且继承自该类
 *
 * @author suhq
 * @date 2018年12月6日 上午10:57:21
 */
public abstract class AbstractMallProcessor {
	@Autowired
	protected GameUserService gameUserService;
	@Autowired
	protected MallService mallService;
	@Autowired
	private ProductService productService;

	protected MallEnum mallType;

	/**
	 * 获得商品列表
	 *
	 * @param guId
	 * @return
	 */
	public abstract RDMallList getGoods(long guId);

	/**
	 * 购买商品
	 *
	 * @param guId
	 * @param mall
	 * @param buyNum
	 * @return
	 */
	public UserMallRecord checkRecord(long guId, CfgMallEntity mall, int buyNum) {
		return mallService.checkRecord(guId, mall, buyNum, getUserMallRecords(guId));
	}

	public final int boughtTimes(long guId, CfgMallEntity mall) {
		int mallId = mall.getId();
		if (mall.getLimit() > 0) {// 有限制的商品
			List<UserMallRecord> records = getUserMallRecords(guId);
			UserMallRecord record = null;
			if (records != null) {
				record = records.stream().filter(r -> r.getBaseId() == mallId).findFirst().orElse(null);
				// 是否有对应的有效纪录
				if (record != null) {
					// 是否有购买次数
					return record.getNum();
				}
			}
		}
		return 0;
	}

	/**
	 * 发放物品
	 *
	 * @param guId
	 * @param mall
	 * @param buyNum
	 * @param rd
	 */
	public abstract void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd);

	/**
	 * 是否匹配特定的产品
	 *
	 * @param mallType
	 * @return
	 */
	public boolean isMatch(int mallType) {
		return this.mallType.getValue() == mallType;
	}

	/**
	 * 返给客户端的商品集合
	 *
	 * @param guId
	 * @param fMalls
	 * @return
	 */
	protected void toRdMallList(long guId, List<CfgMallEntity> fMalls, boolean isExtraDiscount, RDMallList rd) {
		List<RDMallInfo> rdMallInfos = new ArrayList<>();
		List<UserMallRecord> validMRecord = getUserMallRecords(guId);
		for (CfgMallEntity mall : fMalls) {
			RDMallInfo mallInfo = RDMallInfo.fromMall(mall, mall.getPrice(isExtraDiscount));
			// 数量受限、时间受限的商品
			if (mall.getLimit() > 0 && validMRecord != null) {
				Date endDate = mallService.getMallEndDate(guId, mall);
				// 礼包是否过期
				if (endDate != null) {
					if (endDate.getTime() < System.currentTimeMillis() + 30 * 1000) {// 30秒前算过期
						continue;
					}
					mallInfo.setRemainTime(endDate.getTime() - System.currentTimeMillis());
				}
				// 礼包购买情况
				int limit = mall.getLimit();
				int remain = limit;
				for (int j = 0; j < validMRecord.size(); j++) {
					UserMallRecord userMallRecord = validMRecord.get(j);
					if (userMallRecord.getBaseId().equals(mall.getId())) {
						remain = remain - userMallRecord.getNum();
					}
				}
				mallInfo.setRemainTimes(remain < 0 ? 0 : remain);
				mallInfo.setLimit(limit);
				if (mallType == MallEnum.WEEK_RECHARGE_BAG || mallType == MallEnum.MONTH_RECHARGE_BAG) {
					List<Award> awards = productService.getProductAward(mallInfo.getRechargeId()).getAwardList();
					List<RDAward> rdAwards = awards.stream().map(tmp -> new RDAward(tmp.gainAwardId(), tmp.getItem(), tmp.getNum())).collect(Collectors.toList());
					mallInfo.setAwards(rdAwards);
				}
			}

			rdMallInfos.add(mallInfo);
		}
		rd.setMallGoods(rdMallInfos);
		rd.setMallType(mallType.getValue());
	}

	/**
	 * 获得可用的记录集
	 *
	 * @return
	 */
	protected abstract List<UserMallRecord> getUserMallRecords(long guId);

	/**
	 * 检查权限
	 *
	 * @param uid
	 */
	public void checkAuth(long uid, CfgMallEntity mall) {
	}
}
