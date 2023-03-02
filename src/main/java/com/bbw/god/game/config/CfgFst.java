package com.bbw.god.game.config;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 封神台相关配置
 * 
 * @author suhq
 * @date 2018年12月25日 下午2:26:52
 */
@Data
public class CfgFst implements CfgInterface {
	private String key;
	// 封神台解锁等级
	private Integer unlockLevel;
	private Integer unlockRank;
	// 封神台每天免费次数
	private Integer freeTimes;
	// 可攻击范围
	private int fireRange;
	//最大显示数
	private Integer showCount;
	// 可领取积分上限
	private int pointAwardLimit;
	// 控制玩家从低排行到高排行的快慢
	private int stepFactor;
	//机器人基本信息
	private List<RobotRule> robotsRule;
	//机器人卡组信息
	private List<CardRule> cardsRule;
	//跨服封神台默认机器人数量
	private Integer gameRobotsNum=120;
	//区服封神台默认机器人数量
	private Integer serverRobotsNum=300;
	/**
	 * 当前榜单的奖励值
	 */
	private List<RankingAward> gameFstRankingAwards;
	/**
	 * 结算规则
	 */
	private List<GameFstPromotion> gameFstPromotions;
	//结算时间例如 193000  表示19点30分00秒
	private Integer gameFstSettleBeginTime;
	//结算结束时间例如 194000  表示19点40分00秒
	private Integer gameFstSettleEndTime;
	//结算间隔天数
	private Integer gameFstSettleIntervalDay;
	
	@Data
	public static class RobotRule implements Serializable{
		private static final long serialVersionUID = 1L;
		private Integer num;//该区间生成的人数
		private Integer lv;
		private Integer cardType;
		private String name;
	}
	@Data
	public static class CardRule implements Serializable{
		private static final long serialVersionUID = 1L;
		private Integer star;
		private Integer lv;
		private Integer hv;
		private Integer type;
	}
	@Data
	public static class RankingAward implements Serializable{
		private Integer type;
		private Integer min;
		private Integer max;
		private Integer num;
		
		public boolean ifThis(int rank){
			return min<=rank && rank<= max;
		}
	}
	
	@Data
	public static class GameFstPromotion{
		private Integer type;//类型
		private Integer settle;// 结算人数
		private Integer promotion;// 晋级人数
		private Integer standing;//保级人数
		private Integer unlock;//开放人数条件
	}
	
	@Override
	public String getId() {
		return key;
	}

	@Override
	public int getSortId() {
		return 1;
	}

	public List<CardRule> getCardRules(int type){
		return cardsRule.stream().filter(p->p.getType()==type).collect(Collectors.toList());
	}
}
