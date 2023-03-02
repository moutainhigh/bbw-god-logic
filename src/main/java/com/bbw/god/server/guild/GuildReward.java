package com.bbw.god.server.guild;

import com.bbw.god.game.award.Award;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @author lwb  
* @date 2019年5月16日  
* @version 1.0  
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuildReward {
	private Integer type;
	private Integer realId;
	private Integer quantity;
	private Integer paramInt=0;

	public static GuildReward instance(Award award) {
		GuildReward reward = new GuildReward();
		reward.setQuantity(award.getNum());
		reward.setRealId(award.getAwardId());
		reward.setType(award.getItem());
		return reward;
	}
}
