package com.bbw.god.mall.processor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.mall.RDMallList;

/**
 * 特惠礼包
 * 
 * @author suhq
 * @date 2018年12月6日 上午10:58:36
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewerFavorableMallProcessor extends FavorableMallProcessor {

	// 新手特惠商品
	private List<Integer> includes = Arrays.asList();

	NewerFavorableMallProcessor() {
		this.mallType = MallEnum.NEWER_THLB;
	}

	@Override
	public RDMallList getGoods(long guId) {
		List<CfgMallEntity> fMalls = MallTool.getMallConfig().getFavorableMalls();
		fMalls = fMalls.stream().filter(m -> includes.contains(m.getGoodsId())).collect(Collectors.toList());
		RDMallList rd = new RDMallList();
		toRdMallList(guId, fMalls, false, rd);
		return rd;
	}

}
