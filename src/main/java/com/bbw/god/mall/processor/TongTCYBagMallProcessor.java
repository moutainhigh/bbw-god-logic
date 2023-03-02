package com.bbw.god.mall.processor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.FavorableBagEnum;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;

/**
 * 通天残卷礼包
 * 
 * @author suhq
 * @date 2019-10-09 16:29:45
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TongTCYBagMallProcessor extends AbstractMallProcessor {
	@Autowired
	private MallService mallService;
	@Autowired
	private BoxService boxService;

	TongTCYBagMallProcessor() {
		this.mallType = MallEnum.TTCJ_LB;
	}

	@Override
	public RDMallList getGoods(long guId) {
		RDMallList rd = new RDMallList();
		toRdMallList(guId, MallTool.getMallConfig().getTtcjBagMalls(), false, rd);
		return rd;
	}

	@Override
	public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
		int proId = mall.getGoodsId();
		FavorableBagEnum bag = FavorableBagEnum.fromValue(proId);
		WayEnum way = WayEnum.fromName(bag.getName());
		boxService.open(guId, proId, way, rd);

	}

	@Override
	protected List<UserMallRecord> getUserMallRecords(long guId) {
		// 读取类型为特惠礼包的UserMallRecord
		// TODO:可能会有性能问题
		List<UserMallRecord> favorableRecords = mallService.getUserMallRecord(guId, MallEnum.TTCJ_LB);
		List<UserMallRecord> validRecords = favorableRecords.stream().filter(umr -> umr.ifValid())
				.collect(Collectors.toList());
		return validRecords;
	}

}
