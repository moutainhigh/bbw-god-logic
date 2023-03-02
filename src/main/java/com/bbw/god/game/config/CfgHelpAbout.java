package com.bbw.god.game.config;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.gameuser.helpabout.UserHelpAbout;

import lombok.Data;

/**
* @author lwb  
* @date 2019年4月11日  
* @version 1.0  
*/
@Data
public class CfgHelpAbout implements CfgInterface{
	private String key;
	private static int defaultGroupId = 1;//默认 ID
	private List<UserHelpAbout.Info> list;
	@Override
	public Serializable getId() {
		return key;
	}

	@Override
	public int getSortId() {
		return defaultGroupId;
	}

}
