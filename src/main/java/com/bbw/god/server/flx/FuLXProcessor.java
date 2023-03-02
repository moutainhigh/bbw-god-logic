package com.bbw.god.server.flx;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.ICityArriveProcessor;
import com.bbw.god.city.ICityHandleProcessor;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.ServerService;
import com.bbw.god.server.flx.RDArriveFuLX.RDSGBet;
import com.bbw.god.server.flx.RDFlxBetResults.RDFlxBetResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 福临轩 - 投注
 *
 * @author suhq
 * @date 2018年10月24日 下午5:54:34
 */
@Service
public class FuLXProcessor implements ICityArriveProcessor, ICityHandleProcessor {

	private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.FLX);

	@Autowired
	private FlxService flxService;
	@Autowired
	private ServerFlxResultService flxDayResultService;
	@Autowired
	private ServerService serverService;
	@Autowired
	private FlxConfig flxConfig;

	@Override
	public List<CityTypeEnum> getCityTypes() {
		return cityTypes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<RDArriveFuLX> getRDArriveClass() {
		return RDArriveFuLX.class;
	}

	@Override
	public RDArriveFuLX arriveProcessor(GameUser gameUser, CfgCityEntity city, RDAdvance rd) {
		RDArriveFuLX rdArriveFuLX = new RDArriveFuLX();
		Optional<ServerFlxResult> todayResult = flxDayResultService.getTodayResult(gameUser.getServerId());
		if (!todayResult.isPresent()) {
			// 0:0大 = 乐透已压:押押乐已压
			rdArriveFuLX.setHandleStatus("0,0");
			return rdArriveFuLX;
		}
		// 元素馆投注
		List<FlxYaYaLeBet> yylList = flxService.getYaYaLeBetResult(gameUser.getId(), gameUser.getServerId(),
				DateUtil.getTodayInt());
		List<List<Integer>> userYSGBets = yylList.stream()
				.map(userYSGBet -> Arrays.asList(userYSGBet.getBet1(), userYSGBet.getBet2(), userYSGBet.getBet3()))
				.collect(Collectors.toList());
		// 数馆投注
		List<FlxCaiShuZiBet> cszList = flxService.getCaiShuZiBetResult(gameUser.getId(), gameUser.getServerId(),
				DateUtil.getTodayInt());
		List<RDSGBet> userSGBets = cszList.stream()
				.map(userSGBet -> new RDSGBet(userSGBet.getBetNum(), userSGBet.getBetGold(), userSGBet.getBetCopper()))
				.collect(Collectors.toList());
		if (!userYSGBets.isEmpty()) {
			rdArriveFuLX.setYsgBets(userYSGBets);
		}
		if (!userSGBets.isEmpty()) {
			rdArriveFuLX.setSgBets(userSGBets);
		}
		rdArriveFuLX.setYsgCard(todayResult.get().getAwardCardId());
		rdArriveFuLX.setOdds(flxConfig.getSgOdds());
		// 1:1 = 大乐透可压:押押乐可压
		rdArriveFuLX.setHandleStatus("1,1");
		return rdArriveFuLX;
	}



	/**
	 * 检查是否已处理
	 */
	@Override
	public void checkIsHandle(GameUser gu, Object param) {
		String[] status = TimeLimitCacheUtil.getArriveCache(gu.getId(), getRDArriveClass()).getHandleStatus()
				.split(",");
		if (param instanceof CPCaiShuZiBet) {// 数馆
			// 是否投注过
			if (status[0].equals("0")) {
				throw new ExceptionForClientTip("city.flx.sg.already.bet");
			}
		}
		// 元素馆
		if (param instanceof CPYaYaLeGBet) {
			if (status[1].equals("0")) {
				throw new ExceptionForClientTip("city.flx.ysg.already.bet");
			}
		}
	}

	@Override
	public RDCommon handleProcessor(GameUser gu, Object param) {
		RDCommon rd = null;
		if (param instanceof CPCaiShuZiBet) {// 数馆
			rd = betSG(gu, (CPCaiShuZiBet) param);
		}
		// 元素馆
		if (param instanceof CPYaYaLeGBet) {// 元素馆
			rd = betYSG(gu, (CPYaYaLeGBet) param);
		}
		return rd;
	}

	/**
	 * 设置处理状态
	 */
	@Override
	public void setHandleStatus(GameUser gu, Object param) {
		RDArriveFuLX fuLXCache = TimeLimitCacheUtil.getArriveCache(gu.getId(), getRDArriveClass());
		String[] status = fuLXCache.getHandleStatus().split(",");
		if (param instanceof CPCaiShuZiBet) {// 数馆
			fuLXCache.setHandleStatus("0," + status[1]);
		}
		// 元素馆
		if (param instanceof CPYaYaLeGBet) {// 元素馆
			fuLXCache.setHandleStatus(status[0] + ",0");
		}
		TimeLimitCacheUtil.setArriveCache(gu.getId(), fuLXCache);
	}

	/**
	 * 获得富临轩最近开奖结果
	 *
	 * @param type
	 * @return
	 */
	public RDFlxBetResults listLastFlxResults(int sId, int type) {
		RDFlxBetResults rd = new RDFlxBetResults();
		List<ServerFlxResult> flxBetResults = flxDayResultService.getResultInDays(sId, 5);
		if (ListUtil.isEmpty(flxBetResults)) {
			return null;
		}
		List<RDFlxBetResult> results = null;
		if (type == 10) {
			results = flxBetResults.stream()
					.map(result -> new RDFlxBetResult(result.getMatchResult() + "",
							DateUtil.toDateString(DateUtil.fromDateInt(result.getDateInt()))))
					.collect(Collectors.toList());
		} else {
			results = flxBetResults.stream()
					.map(result -> new RDFlxBetResult(result.getMatchResult().getYsgEleNames(),
							DateUtil.toDateString(DateUtil.fromDateInt(result.getDateInt()))))
					.collect(Collectors.toList());
		}
		rd.setLastBetResults(results);
		return rd;
	}

	/**
	 * 投注元素馆
	 *
	 * @param gu
	 * @param param
	 * @return
	 */
	private RDCommon betYSG(GameUser gu, CPYaYaLeGBet param) {
		RDCommon rd = new RDCommon();
		// 元宝是否足够
		ResChecker.checkGold(gu, flxConfig.getYsgNeedGold());
		// 今天的投注记录
		Optional<ServerFlxResult> todayResult = flxDayResultService.getTodayResult(gu.getServerId());
		if (!todayResult.isPresent()) {
			throw new ExceptionForClientTip("city.flx.close");
		}
		ServerFlxResult flxBetResult = todayResult.get();
		// 是否投注过
		List<FlxYaYaLeBet> yylList = flxService.getYaYaLeBetResult(gu.getId(), gu.getServerId(),
				DateUtil.getTodayInt());

		Optional<FlxYaYaLeBet> userYSGBet = yylList.stream()
				.filter(bet -> bet.getServerFlxResult().longValue() == flxBetResult.getId()
						&& bet.getBet1().intValue() == param.getBet1() && bet.getBet2().intValue() == param.getBet2()
						&& bet.getBet3().intValue() == param.getBet3())
				.findFirst();
		if (userYSGBet.isPresent()) {
			throw new ExceptionForClientTip("city.flx.ysg.already.betEle");
		}

		// 扣除元宝
		ResEventPublisher.pubGoldDeductEvent(gu.getId(), flxConfig.getYsgNeedGold(), WayEnum.FLX_YSG, rd);
		// 处理投注记录
		FlxYaYaLeBet thisTimeBet = FlxYaYaLeBet.instance(gu, param, flxBetResult);
		flxService.addYaYaLeBet(thisTimeBet);

		return rd;
	}

	/**
	 * 数馆投注
	 *
	 * @param gu
	 * @param param
	 * @return
	 */
	private RDCommon betSG(GameUser gu, CPCaiShuZiBet param) {

		if (param.getBetCount() <= 0) {
			throw new ExceptionForClientTip("city.flx.sg.unvalid.betCount");
		}
		if (param.getBetNum() < 1 || param.getBetNum() > 36) {
			throw new ExceptionForClientTip("city.flx.sg.unvalid.betNum");
		}
		if (param.getBetKind() == 1) {
			return betSgAsGold(gu, param);
		} else {
			return betSgAsCopper(gu, param);
		}
	}

	/**
	 * 元宝投注数馆
	 *
	 * @param gu
	 * @param param
	 * @return
	 */
	private RDCommon betSgAsGold(GameUser gu, CPCaiShuZiBet param) {
		RDCommon rd = new RDCommon();
		if (gu.getLevel() < flxConfig.getSgGoldBetLevelLimit()) {
			throw new ExceptionForClientTip("city.flx.sg.not.enough.level");
		}

		ServerFlxResult todayFlxResult = getTodayFlxResult(gu);
		// 投注记录
		FlxCaiShuZiBet userBet = getCaiShuZiBet(gu, param.getBetNum(), todayFlxResult);

		int betedCount = userBet == null ? 0 : userBet.getBetGold();
		// 每日投注上限检查
		if (param.getBetCount() + betedCount > flxConfig.getGoldDayLimit()) {
			throw new ExceptionForClientTip("city.flx.sg.outOfGold");
		}
		ResChecker.checkGold(gu, param.getBetCount());
		// 扣除元宝
		ResEventPublisher.pubGoldDeductEvent(gu.getId(), param.getBetCount(), WayEnum.FLX_SG, rd);
		if (userBet != null) {// 投注过更新记录
			userBet.addBetGold(param.getBetCount());
			serverService.updateServerData(userBet);
		} else {// 未投注过，创建新的记录
			userBet = FlxCaiShuZiBet.instance(gu, param, todayFlxResult);
			flxService.addCaiShuZiBet(userBet);
		}
		return rd;
	}

	/**
	 * 铜钱投注数馆
	 *
	 * @param gu
	 * @param param
	 * @return
	 */
	private RDCommon betSgAsCopper(GameUser gu, CPCaiShuZiBet param) {
		RDCommon rd = new RDCommon();
		ServerFlxResult todayFlxResult = getTodayFlxResult(gu);
		FlxCaiShuZiBet userBet = getCaiShuZiBet(gu, param.getBetNum(), todayFlxResult);
		int betedCount = userBet == null ? 0 : userBet.getBetCopper();
		// 每日投注上限检查
		if (param.getBetCount() + betedCount > flxConfig.getCopperDayLimit()) {
			throw new ExceptionForClientTip("city.flx.sg.outOfCopper");
		}

		ResChecker.checkCopper(gu, param.getBetCount());
		ResEventPublisher.pubCopperDeductEvent(gu.getId(), (long) param.getBetCount(), WayEnum.FLX_SG, rd);

		if (userBet != null) {// 投注过更新记录

			userBet.addBetCopper(param.getBetCount());
			serverService.updateServerData(userBet);
		} else {// 未投注过，创建新的记录
			userBet = FlxCaiShuZiBet.instance(gu, param, todayFlxResult);
			flxService.addCaiShuZiBet(userBet);
		}

		return rd;
	}

	/**
	 * 获取今日富临轩结果
	 *
	 * @param gu
	 * @return
	 */
	private ServerFlxResult getTodayFlxResult(GameUser gu) {
		Optional<ServerFlxResult> todayResult = flxDayResultService.getTodayResult(gu.getServerId());
		if (!todayResult.isPresent()) {
			throw new ExceptionForClientTip("city.flx.close");
		}
		ServerFlxResult flxBetResult = todayResult.get();
		return flxBetResult;
	}

	/**
	 * 获得猜数字的投注记录
	 *
	 * @param gu
	 * @param betNum
	 * @param flxBetResult
	 * @return
	 */
	private FlxCaiShuZiBet getCaiShuZiBet(GameUser gu, int betNum, ServerFlxResult flxBetResult) {

		List<FlxCaiShuZiBet> cszList = flxService.getCaiShuZiBetResult(gu.getId(), gu.getServerId(),
				DateUtil.getTodayInt());
		FlxCaiShuZiBet userBet = cszList.stream().filter(
				bet -> bet.getServerFlxResult().longValue() == flxBetResult.getId() && bet.getBetNum() == betNum)
				.findFirst().orElse(null);
		return userBet;
	}

	@Override
	public String getTipCodeForAlreadyHandle() {
		return null;
	}

}
