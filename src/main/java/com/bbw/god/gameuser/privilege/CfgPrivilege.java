package com.bbw.god.gameuser.privilege;

import java.util.List;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgInterface;

import lombok.Data;

/**
 * 特权配置
 * 
 * @author suhq
 * @date 2019-09-18 16:57:58
 */
@Data
public class CfgPrivilege implements CfgInterface {
	private String key;
	private List<Award> tianlingBag;// 天灵印礼包
	/** 地灵印礼包 */
	private List<Award> dilingBag;

	@Override
	public String getId() {
		return this.key;
	}

	@Override
	public int getSortId() {
		return 1;
	}

}
