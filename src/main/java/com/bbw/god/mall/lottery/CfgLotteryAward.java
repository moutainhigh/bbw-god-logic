package com.bbw.god.mall.lottery;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 奖券奖励配置类
 * @date 2020/7/6 15:53
 **/
@Data
public class CfgLotteryAward implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 4808157319240578591L;
	private Integer id;
	private String name;
	// 奖励等级
	private Integer level;
	// 最大获奖人数
	private Integer maxPerson;
	// 人数
	private List<Award> awards;

	@Override
	public int getSortId() {
		return id;
	}
}
