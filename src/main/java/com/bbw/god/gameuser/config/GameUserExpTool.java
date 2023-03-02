package com.bbw.god.gameuser.config;

import lombok.Data;

public class GameUserExpTool {

	/**
	 * 根据等级获得level
	 * 
	 * @param level
	 * @return
	 */
	public static int getExpByLevel(int level) {
		GameUserConfig config = GameUserConfig.bean();
		int exp = 0;

		int needExp = 0;
		int needTmp = 0;
		int levelTmp = 0;
		int expTmp = 0;
		if (level >= 150) {
			levelTmp = 150;
			needExp = config.getExpData().getGuNeedExp150();
			needTmp = config.getExpData().getGuNeedTmp150();
			expTmp = config.getExpData().getGuExp150();
		} else if (level >= 60) {

			levelTmp = 60;
			needExp = config.getExpData().getGuNeedExp60();
			needTmp = config.getExpData().getGuNeedTmp60();
			expTmp = config.getExpData().getGuExp60();

		} else if (level >= 50) {

			levelTmp = 50;
			needExp = config.getExpData().getGuNeedExp50();
			needTmp = config.getExpData().getGuNeedTmp50();
			expTmp = config.getExpData().getGuExp50();

		} else if (level >= 40) {

			levelTmp = 40;
			needExp = config.getExpData().getGuNeedExp40();
			needTmp = config.getExpData().getGuNeedTmp40();
			expTmp = config.getExpData().getGuExp40();

		} else if (level >= 30) {

			levelTmp = 30;
			needExp = config.getExpData().getGuNeedExp30();
			needTmp = config.getExpData().getGuNeedTmp30();
			expTmp = config.getExpData().getGuExp30();

		} else if (level >= 20) {

			levelTmp = 20;
			needExp = config.getExpData().getGuNeedExp20();
			needTmp = config.getExpData().getGuNeedTmp20();
			expTmp = config.getExpData().getGuExp20();

		} else if (level >= 10) {

			levelTmp = 10;
			needExp = config.getExpData().getGuNeedExp10();
			needTmp = config.getExpData().getGuNeedTmp10();
			expTmp = config.getExpData().getGuExp10();

		} else {

			int sum = 0;
			for (int i = 0; i < level; i++) {
				sum += config.getExpData().getGuNeedExps()[i];
			}
			return sum;
		}

		int levelTmp2 = level - levelTmp;
		exp = expTmp + (needTmp / 2) * (levelTmp2 * (1 + levelTmp2)) + needExp * levelTmp2;
		return exp;
	}

	/**
     * 根据经验获得等级和等级分母经验
     *
     * @param exp
     * @return
     */
    public static LevelExpRate getLevelExpRateByExp(long exp) {
		int newLevel = 1;
		int expRate = 0;

		int addRate = 0;
		int needExp = 0;
		int needTmp = 0;
		int tmp = 0;
		if (exp >= GameUserConfig.bean().getExpData().getGuExp150()) {
			newLevel = 150;
			needExp = GameUserConfig.bean().getExpData().getGuNeedExp150();
			needTmp = GameUserConfig.bean().getExpData().getGuNeedTmp150();
			tmp = GameUserConfig.bean().getExpData().getGuExp150();
		} else if (exp >= GameUserConfig.bean().getExpData().getGuExp60()) {
			newLevel = 60;
			needExp = GameUserConfig.bean().getExpData().getGuNeedExp60();
			needTmp = GameUserConfig.bean().getExpData().getGuNeedTmp60();
			tmp = GameUserConfig.bean().getExpData().getGuExp60();

		} else if (exp >= GameUserConfig.bean().getExpData().getGuExp50()) {

			newLevel = 50;
			needExp = GameUserConfig.bean().getExpData().getGuNeedExp50();
			needTmp = GameUserConfig.bean().getExpData().getGuNeedTmp50();
			tmp = GameUserConfig.bean().getExpData().getGuExp50();

		} else if (exp >= GameUserConfig.bean().getExpData().getGuExp40()) {

			newLevel = 40;
			needExp = GameUserConfig.bean().getExpData().getGuNeedExp40();
			needTmp = GameUserConfig.bean().getExpData().getGuNeedTmp40();
			tmp = GameUserConfig.bean().getExpData().getGuExp40();

		} else if (exp >= GameUserConfig.bean().getExpData().getGuExp30()) {

			newLevel = 30;
			needExp = GameUserConfig.bean().getExpData().getGuNeedExp30();
			needTmp = GameUserConfig.bean().getExpData().getGuNeedTmp30();
			tmp = GameUserConfig.bean().getExpData().getGuExp30();

		} else if (exp >= GameUserConfig.bean().getExpData().getGuExp20()) {

			newLevel = 20;
			needExp = GameUserConfig.bean().getExpData().getGuNeedExp20();
			needTmp = GameUserConfig.bean().getExpData().getGuNeedTmp20();
			tmp = GameUserConfig.bean().getExpData().getGuExp20();

		} else if (exp >= GameUserConfig.bean().getExpData().getGuExp10()) {

			newLevel = 10;
			needExp = GameUserConfig.bean().getExpData().getGuNeedExp10();
			needTmp = GameUserConfig.bean().getExpData().getGuNeedTmp10();
			tmp = GameUserConfig.bean().getExpData().getGuExp10();

		} else {

			int sum = 0;
			int[] guExps = GameUserConfig.bean().getExpData().getGuNeedExps();
			for (int i = 1; i < guExps.length; i++) {
				sum += guExps[i];
				if (exp < sum) {
					expRate = guExps[i];
					newLevel = i;
					break;
				}
			}
		}

		if (newLevel >= 10) {
			int numTmp = needTmp / 2 + needExp;
			int levelTmp = (int) ((Math.sqrt(2.0 * needTmp * (exp - tmp) + Math.pow(numTmp, 2)) - numTmp) / needTmp);

			addRate = needExp + needTmp * (levelTmp + 1);
			newLevel += levelTmp;
			expRate = addRate;
		}

		return new LevelExpRate(newLevel, expRate);
	}

	/**
	 * 
	 * @author suhq
	 * @date 2019年2月26日 下午4:23:26
	 */
	@Data
	public static class LevelExpRate {
		private int level;// 等级
		private int expRate;// 等级对应的分母经验

		public LevelExpRate(int level, int expRate) {
			this.level = level;
			this.expRate = expRate;
		}
	}
}
