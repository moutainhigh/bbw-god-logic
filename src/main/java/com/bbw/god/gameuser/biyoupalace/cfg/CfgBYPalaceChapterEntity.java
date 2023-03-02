package com.bbw.god.gameuser.biyoupalace.cfg;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.game.config.CfgEntityInterface;

import lombok.Data;

/**
 * 碧游宫篇配置
 * 
 * @author suhq
 * @date 2019-09-06 17:02:32
 */
@Data
public class CfgBYPalaceChapterEntity implements CfgEntityInterface, Serializable {
	private Integer chapter;
	private Integer minTongTCJ;
	private Integer maxTongTCJ;
	private Integer costPerTime;
	private Integer skillScrollProb;
	private Integer typeSymbolProb;
	private Integer universalSymbolProb;
	private List<String> symbols;

	@Override
	public Serializable getId() {
		return chapter;
	}

	@Override
	public int getSortId() {
		return chapter;
	}

}
