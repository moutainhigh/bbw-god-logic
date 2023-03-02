package com.bbw.god.game.config.card;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.game.config.CfgInterface;

import lombok.Data;

@Data
public class CfgCardGroup implements CfgInterface, Serializable {

	private static final long serialVersionUID = 1L;
	private String key;
	private List<CardGroup> cardGroups;

	@Data
	public static class CardGroup {
		private Integer id; //
		private String name; //
		private String shortName; //
		private String memo; //
	}

	@Override
	public Serializable getId() {
		return key;
	}

	@Override
	public int getSortId() {
		return 1;
	}
}
