package com.bbw.god.city.chengc.investigate;

import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 侦查城池
 * @date 2020/5/29 14:08
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDInvestigate extends RDCommon implements Serializable {
	private static final long serialVersionUID = -772464028552705533L;
	private Boolean isSuccess;
	private Integer event;
	private RDInvestigateFightInfo fightInfo;


	public static RDInvestigate getInstance(Boolean isSuccess, Integer event) {
		RDInvestigate rdInvestigate = new RDInvestigate();
		rdInvestigate.setIsSuccess(isSuccess);
		rdInvestigate.setEvent(event);
		return rdInvestigate;
	}

	@Data
	@EqualsAndHashCode(callSuper = false)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RDInvestigateFightInfo {
		private Integer level = null;// 对方召唤师等级
		private String nickname = null;
		private Integer headIcon = null;
		private Integer head = null;
		private List<RDFightsInfo.RDFightCard> cards = null;// 对手卡牌
		private Boolean hasWin = false;
		private Integer country = null;// 所属地属性

		public static RDInvestigateFightInfo getInstance(RDFightsInfo rdFightsInfo, CfgCityEntity city) {
			RDInvestigateFightInfo rd = new RDInvestigateFightInfo();
			rd.setLevel(rdFightsInfo.getLevel());
			rd.setCards(rdFightsInfo.getCards());
			rd.setHeadIcon(rdFightsInfo.getHeadIcon());
			rd.setCountry(city.getCountry());
			rd.setNickname(rdFightsInfo.getNickname());
			rd.setHead(rdFightsInfo.getHead());
			return rd;
		}
	}
}
