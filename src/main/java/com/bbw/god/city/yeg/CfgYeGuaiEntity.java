package com.bbw.god.city.yeg;

import java.io.Serializable;

import com.bbw.god.game.config.CfgEntityInterface;

import lombok.Data;

@Data
public class CfgYeGuaiEntity implements CfgEntityInterface, Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id; // 同时表示野怪召唤师等级
	private String cards; // 野怪卡组

	@Override
	public int getSortId() {
		return id;
	}
}
