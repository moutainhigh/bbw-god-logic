package com.bbw.god.gameuser.config;

import com.bbw.common.SpringContextUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 游戏区服相关常量配置
 * 
 * @author suhq
 * @date 2018年11月9日 上午9:34:21
 */
@Getter
// 前缀格式规范，必须全部小写
@ConfigurationProperties(prefix = "bbw-god.newgameuser")
@Component
public class GameUserConfig {
	@Value("${bbw-god.newGameUser.copper:100000}")
	private long initCopper;
	@Value("${bbw-god.newGameUser.gold:0}")
	private int initGold;
	@Value("${bbw-god.newGameUser.dice:200}")
	private int initDice;
	// 玩家等级上限
	private Integer guTopLevel = 160;
	// 速战卡开放购买等级
	private Integer szkUnlockLevel = 10;
	// 重命名需要的元宝数
	private Integer numRenameGold = 1000;
	// 俸禄最多累计领取天数
	private Integer maxSalaryDay = 3;
	// 卡牌分享奖励
	private Integer shareCardAwardGold = 20;

	private GameUserExpData expData = new GameUserExpData();

	public static GameUserConfig bean() {
		return SpringContextUtil.getBean(GameUserConfig.class);
	}

	/**
	 * 玩家经验啊数据
	 * 
	 * @author suhq
	 * @date 2018年11月30日 下午3:57:21
	 */
	@Getter
	public class GameUserExpData {

		/** 玩家前十级升级所需要的经验 **/
		private final int[] guNeedExps = { 0, 100, 200, 300, 500, 800, 1300, 2100, 3400, 5500 };

		/** 玩家9升到10需要的经验 **/
		private final int guNeedExp10 = 5500;
		/** 玩家19升到20需要的经验 **/
		private final int guNeedExp20 = 26500;
		/** 玩家29升到30需要的经验 **/
		private final int guNeedExp30 = 81500;
		/** 玩家39升到40需要的经验 **/
		private final int guNeedExp40 = 241500;
		/** 玩家49升到50需要的经验 **/
		private final int guNeedExp50 = 727500;
		/** 玩家59升到60需要的经验 **/
		private final int guNeedExp60 = 1703500;
		/** 玩家149升到150需要的经验 **/
		private final int guNeedExp150 = 20567500;


		/** 玩家10~20每升一级需加的经验 **/
		private final int guNeedTmp10 = 2100;
		/** 玩家21~30每升一级需加的经验 **/
		private final int guNeedTmp20 = 5500;
		/** 玩家31~40每升一级需加的经验 **/
		private final int guNeedTmp30 = 16000;
		/** 玩家41~50每升一级需加的经验 **/
		private final int guNeedTmp40 = 48600;
		/** 玩家51~60每升一级需加的经验 **/
		private final int guNeedTmp50 = 97600;
		/** 玩家60级以上每升一级需加的经验 **/
		private final int guNeedTmp60 = 209600;
		/** 玩家150级以上每升一级需加的经验 **/
		private final int guNeedTmp150 = 4611200;


		/** 玩家10级总经验 **/
		private final int guExp10 = 14200;
		/** 玩家20级总经验 **/
		private final int guExp20 = 184700;
		/** 玩家30级总经验 **/
		private final int guExp30 = 752200;
		/** 玩家40级总经验 **/
		private final int guExp40 = 2447200;
		/** 玩家50级总经验 **/
		private final int guExp50 = 7535200;
		/** 玩家60级总经验 **/
		private final int guExp60 = 20178200;
		/** 玩家150级总经验 **/
		private final int guExp150 = 1031805200;
	}
}
