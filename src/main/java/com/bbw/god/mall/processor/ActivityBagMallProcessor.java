package com.bbw.god.mall.processor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;

/**
 * 活动礼包
 * 
 * @author suhq
 * @date 2018年12月6日 上午10:58:36
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ActivityBagMallProcessor extends AbstractMallProcessor {
	@Autowired
	private MallService mallService;

	ActivityBagMallProcessor() {
		this.mallType = MallEnum.ACTIVITY_BAG;
	}

	@Override
	public RDMallList getGoods(long guId) {
		return null;
	}

	@Override
	public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {

	}

	@Override
	protected List<UserMallRecord> getUserMallRecords(long guId) {
		// 读取类型为特惠礼包的UserMallRecord
		// TODO:可能会有性能问题
		List<UserMallRecord> favorableRecords = mallService.getUserMallRecord(guId, mallType);
		List<UserMallRecord> validRecords = favorableRecords.stream().filter(umr -> umr.ifValid())
				.collect(Collectors.toList());
		return validRecords;
	}

}
