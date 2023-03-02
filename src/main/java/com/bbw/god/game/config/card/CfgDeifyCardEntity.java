package com.bbw.god.game.config.card;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

@Data
public class CfgDeifyCardEntity implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id; //
	private String name; //
	private Integer type; //
	private Integer star; //
	private Integer attack; //
	private Integer hp; //
	private Integer zeroSkill; //
	private Integer fiveSkill; //
	private Integer tenSkill; //
	private Integer group; //
	@Override
	public int getSortId() {
		return this.getId();
	}
}
