package com.bbw.god.game.config.card;

import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-07-13
 */
@Data
public class CfgHideCard implements CfgInterface {
	private String key;
	private List<CfgCardEntity> cards;
	
	@Override
	public Serializable getId () {
		return key;
	}
	
	@Override
	public int getSortId () {
		return 0;
	}
}
