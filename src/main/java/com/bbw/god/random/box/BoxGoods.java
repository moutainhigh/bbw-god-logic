package com.bbw.god.random.box;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.game.config.CfgInterface;

import lombok.Data;

/**
 * 箱子、礼包的配置类
 * 
 * @author suhq
 * @date 2019年4月10日 下午11:47:54
 */
@Data
public class BoxGoods implements CfgInterface, Serializable {
	private static final long serialVersionUID = 1L;
	// -------------------------
	private String key;// 箱子、礼包名称
	private String desc;// 箱子、礼包描述

	// 当isRandom=false时，categoryNum无需配置
	private Boolean isRandom = true;// 箱子、礼包是否随机
	private Integer categoryNum = 1;// 箱子、礼包可获得品类数

	private List<BoxGood> goods;// 物品集

	@Override
	public Serializable getId() {
		return key;
	}

	@Override
	public int getSortId() {
		return 0;
	}
}
