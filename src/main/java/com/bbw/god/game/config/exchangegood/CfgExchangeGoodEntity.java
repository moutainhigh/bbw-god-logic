package com.bbw.god.game.config.exchangegood;

import java.io.Serializable;

import com.bbw.god.game.config.CfgEntityInterface;

import lombok.Data;

@Data
public class CfgExchangeGoodEntity implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id; //
	private Integer serial;
	private String name; //
	private Integer type; //
	private Integer goodId;//
	private Integer unit; //
	private Integer price; //
	private Integer num; //
	private Integer way; //
	private Boolean isValid; //

	@Override
	public int getSortId() {
		return this.getSerial();
	}
}
