package com.bbw.god.fight;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.FightCardGenerateRule;
import com.bbw.god.game.transmigration.entity.TransmigrationCard;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 村庄战斗对手配置信息
 *
 * @author: suhq
 * @date: 2021/10/20 3:54 下午
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FighterInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 对方召唤师昵称 */
	private String nickname = null;
	/** 对方召唤师头像 */
	private Integer head = null;
	/** 召唤师等级 */
	private Integer lv;
	/** 阶数范围 **/
	private Integer[] cardHvInterval;
	/** 卡牌等级范围 **/
	private Integer[] cardLvInterval;
	/** 卡牌生成规则 **/
	private List<FightCardGenerateRule> cards;

}
