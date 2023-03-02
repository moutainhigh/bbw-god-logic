package com.bbw.god.game.config.special;

import java.io.Serializable;

import com.bbw.god.game.config.CfgInterface;

import lombok.Data;

/**
 * 特产参数配置
 * 
 * @author suhq
 * @date 2019-07-19 17:54:28
 */
@Data
public class CfgSpecial implements CfgInterface, Serializable {
	private static final long serialVersionUID = 1L;
	private String key; //
	private Integer specialMaxLimit;

	@Override
	public int getSortId() {
		return 1;
	}

	@Override
	public Serializable getId() {
		return this.key;
	}

}
