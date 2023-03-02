package com.bbw.god.server.flx;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.data.GameDayDataService;
import com.bbw.god.game.flx.FlxDayResult;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.flx.event.FlxEventPublisher;
import com.bbw.god.server.flx.event.YaYaLeAwardTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 富临轩逻辑 以开奖结果ID作为二级业务分类
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-14 14:06
 */
@Slf4j
@Service
public class FlxService {
	@Autowired
	private ServerDataService serverDataService;
	@Autowired
	private ServerFlxResultService serverFlxResultService;
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private FlxConfig flxConfig;
	@Autowired
	private GameDayDataService gameDayDataService;

	/**
	 * 添加数字投注记录
	 *
	 * @param caishuzi
	 */
	public void addCaiShuZiBet(FlxCaiShuZiBet caishuzi) {
		serverDataService.addServerData(caishuzi);
	}

	/**
	 * 添加丫丫乐投注记录
	 * 
	 * @param yayale
	 */
	public void addYaYaLeBet(FlxYaYaLeBet yayale) {
		serverDataService.addServerData(yayale);
	}

	/**
	 * 获取某一期的玩家猜数字投注记录
	 *
	 * @param uid
	 * @param sid
	 * @param dateInt
	 * @return
	 */
	public List<FlxCaiShuZiBet> getCaiShuZiBetResult(long uid, int sid, int dateInt) {
		String loopKey = String.valueOf(dateInt);
		List<FlxCaiShuZiBet> betList = serverDataService.getServerDatas(sid, FlxCaiShuZiBet.class, loopKey);
		if (ListUtil.isNotEmpty(betList)) {
			return betList.stream().filter(csz -> csz.getUid() == uid).collect(Collectors.toList());
		}
		return betList;
	}

	/**
	 * 获取某一期玩家压压乐投注记录
	 *
	 * @param uid
	 * @param sid
	 * @param dateInt
	 * @return
	 */
	public List<FlxYaYaLeBet> getYaYaLeBetResult(long uid, int sid, int dateInt) {
		String loopKey = String.valueOf(dateInt);
		List<FlxYaYaLeBet> betList = serverDataService.getServerDatas(sid, FlxYaYaLeBet.class, loopKey);
		if (ListUtil.isNotEmpty(betList)) {
			return betList.stream().filter(yyl -> yyl.getUid() == uid).collect(Collectors.toList());
		}
		return betList;
	}

	/**
	 * 发送指定日期的奖励
	 * 
	 * @param dateInt
	 */
	public void sendFlxMailAward(int dateInt) {
		// 全服 压压乐 投入
		List<FlxYaYaLeBet> ysgBets = getALLYaYaLeBets(dateInt);
		// 按照区服分类
		Map<Long, List<FlxYaYaLeBet>> serverYylBets = ysgBets.stream().collect(Collectors.groupingBy(FlxYaYaLeBet::getServerFlxResult));

		// 全服 猜数字 投入
		List<FlxCaiShuZiBet> sgBets = getALLCaiShuZiBets(dateInt);
		Map<Long, List<FlxCaiShuZiBet>> serverCszBets = sgBets.stream().collect(Collectors.groupingBy(FlxCaiShuZiBet::getServerFlxResult));
		// 重新生成数馆的奖励
		List<FlxDayResult> flxDayResults = gameDayDataService.getGameData(FlxDayResult.class);
		//获取再前一天的开奖结果
		Date preDate = DateUtil.addDays(DateUtil.fromDateInt(dateInt), -1);
		int preDateInt = DateUtil.toDateInt(preDate);
		FlxDayResult preFlxDayResult = flxDayResults.stream().filter(s -> s.getDateInt() == preDateInt).findFirst().orElse(null);
		int preSgResult = null == preFlxDayResult ? 0 : preFlxDayResult.getSgNum();
		//新的开奖结果
		int newSgNum = getFlxSgResult(sgBets);
		//如果和前一天一样，重新执行1次
		if (preSgResult == newSgNum) {
			log.info("开奖结果为{},和前一日一样，再随机一次结果", newSgNum);
			newSgNum = getFlxSgResult(sgBets);
		}
		FlxDayResult flxDayResult = flxDayResults.stream().filter(s -> s.getDateInt() == dateInt).findFirst().orElse(null);
		flxDayResult.setSgNum(newSgNum);
		gameDayDataService.updateGameData(flxDayResult);

		List<CfgServerEntity> servers = ServerTool.getAvailableServers();
		for (CfgServerEntity server : servers) {
			// 投注开奖
			Optional<ServerFlxResult> dateResult = serverFlxResultService.getSingleResultByDate(server.getId(), dateInt);
			if (dateResult.isPresent()) {
				ServerFlxResult sfr = dateResult.get();
				// 设置新的开奖奖励
				sfr.getMatchResult().setSgNum(newSgNum);
				serverDataService.updateServerData(sfr);
				
				List<FlxYaYaLeBet> yyl = serverYylBets.get(sfr.getId());
				sendYsgMailAward(sfr, yyl, dateInt);
				log.info("{}压压乐奖励{}发送完毕！", server.getId() + server.getName(), DateUtil.nowToString());
				List<FlxCaiShuZiBet> csz = serverCszBets.get(sfr.getId());
				sendSgMailAward(sfr, csz);
				log.info("{}猜数字奖励{}发送完毕！", server.getId() + server.getName(), DateUtil.nowToString());
			} else {
				log.error("错误!!!{}福临轩奖励没有发放!", DateUtil.nowToString());
			}
		}
	}

	/**
	 * 发送押押乐奖励邮件
	 *
	 * @param flxResult
	 * @param ysgBets
	 */
	public void sendYsgMailAward(ServerFlxResult flxResult, List<FlxYaYaLeBet> ysgBets, int date) {
		// 获取区服昨天的所有押押乐押注记录
		if (ListUtil.isEmpty(ysgBets)) {
			return;
		}
		// 严格结果，按照顺序的
		//List<Integer> ysgResult = Arrays.asList(flxResult.getMatchResult().getYsgBet1() / 10, flxResult.getMatchResult().getYsgBet2() / 10, flxResult.getMatchResult().getYsgBet3() / 10);
		// TODO 9.30号后修改为原来的逻辑（不从全服拿数据）
		FlxDayResult flxDayResult = gameDayDataService.getGameData(FlxDayResult.class).stream().filter(s -> s.getDateInt() == date).findFirst().orElse(null);
		List<Integer> ysgResult = Arrays.asList(flxDayResult.getYsgBet1() / 10, flxDayResult.getYsgBet2() / 10, flxDayResult.getYsgBet3() / 10);
		// 模糊结果，顺序可以不同的
		List<Integer> ysgResultTmp = new ArrayList<>(ysgResult);
		Collections.sort(ysgResultTmp);
		// 元素馆 头奖 鼓励奖
		// 获得各奖励的玩家ID
		Set<FlxYaYaLeBet> firstPrizers = new HashSet<>();// 头奖获得者
		Set<FlxYaYaLeBet> encouragePrizers = new HashSet<>();// 鼓励奖获得者
		Set<Long> firstPrizerUids = new HashSet<>();// 头奖获得者
		Set<Long> encouragePrizerUids = new HashSet<>();// 鼓励奖获得者
		Set<Long> noPrizerUids = new HashSet<>();// 非获奖者
		for (FlxYaYaLeBet ysgBet : ysgBets) {
			// 不是本区服
			if (ysgBet.getServerFlxResult().longValue() != flxResult.getId().longValue()) {
				continue;
			}
			Long uid = ysgBet.getUid();
			// 已经有头奖，不再处理
			if (firstPrizerUids.contains(uid)) {
				continue;
			}
			// 头奖
			if (ysgBet.getBet1().intValue() == ysgResult.get(0).intValue() && ysgBet.getBet2().intValue() == ysgResult.get(1) && ysgBet.getBet3().intValue() == ysgResult.get(2)) {
				firstPrizers.add(ysgBet);
				firstPrizerUids.add(uid);
				encouragePrizerUids.remove(uid);
				noPrizerUids.remove(uid);
				continue;
			}
			// 已经有鼓励奖，不再处理
			if (encouragePrizerUids.contains(uid)) {
				continue;
			}
			// 是否获得鼓励奖
			List<Integer> betEles = Arrays.asList(ysgBet.getBet1(), ysgBet.getBet2(), ysgBet.getBet3());
			Collections.sort(betEles);
			boolean isEncourageAward = ListUtil.isSameList(ysgResultTmp, betEles);
			// 得了鼓励奖，并且没有中头奖
			if (isEncourageAward) {
				encouragePrizers.add(ysgBet);
				encouragePrizerUids.add(uid);
				noPrizerUids.remove(uid);
				continue;
			}
			// 没有得奖
			if (noPrizerUids.contains(uid)) {
				continue;
			}
			noPrizerUids.add(uid);
		} // end for

		List<UserMail> mailList = new ArrayList<>(ysgBets.size());
		// 发放邮件
		CfgCardEntity awardCard = CardTool.getCardById(flxResult.getAwardCardId());
		if (firstPrizers.size() > 0) {
			List<Award> awards = Arrays.asList(new Award(awardCard.getId(), AwardEnum.KP, 1));
			for (FlxYaYaLeBet yyl : firstPrizers) {
				if (yyl.getAwardMailId() > 0) {// 已经发送过了
					continue;
				}
				String title = LM.I.getMsgByUid(yyl.getUid(), "mail.flx.results.first.award.title");
				String preContent = LM.I.getMsgByUid(yyl.getUid(),"mail.flx.results.sequence.content", flxResult.getDateInt(), flxDayResult.getYsgEleNames());
				String content = LM.I.getMsgByUid(yyl.getUid(),"mail.flx.results.first.award.content", preContent);
				UserMail mail = UserMail.newAwardMail(title, content, yyl.getUid(), awards);
				yyl.setAwardMailId(mail.getId());
				mailList.add(mail);
				FlxEventPublisher.pubYaYaLeWinEvent(YaYaLeAwardTypeEnum.FIRST_PRIZE.getValue(), new BaseEventParam(yyl.getUid(), WayEnum.FLX_YSG));
			}
		}
		log.info(flxResult.getDateInt() + "福临轩头奖数量:" + firstPrizers.size());

		if (encouragePrizers.size() > 0) {
			// 头奖就不再
			List<Award> awards = Arrays.asList(new Award(awardCard.getType(), AwardEnum.YS, 3));
			for (FlxYaYaLeBet yyl : encouragePrizers) {
				if (yyl.getAwardMailId() > 0) {// 已经发送过了
					continue;
				}
				String title = LM.I.getMsgByUid(yyl.getUid(),"mail.flx.results.encourage.award.title");
				String preContent = LM.I.getMsgByUid(yyl.getUid(),"mail.flx.results.sequence.content", flxResult.getDateInt(), flxDayResult.getYsgEleNames());
				String content = LM.I.getMsgByUid(yyl.getUid(),"mail.flx.results.encourage.award.content", preContent);
				UserMail mail = UserMail.newAwardMail(title, content, yyl.getUid(), awards);
				yyl.setAwardMailId(mail.getId());
				mailList.add(mail);
				FlxEventPublisher.pubYaYaLeWinEvent(YaYaLeAwardTypeEnum.ENCOURAGE.getValue(), new BaseEventParam(yyl.getUid(), WayEnum.FLX_YSG));
			}
		}
		log.info(flxResult.getDateInt() + "福临轩鼓励奖数量:" + encouragePrizers.size());
		if (noPrizerUids.size() > 0) {
			for (Long uid : noPrizerUids) {
				String title = LM.I.getMsgByUid(uid,"mail.flx.results.content");
				String preContent = LM.I.getMsgByUid(uid,"mail.flx.results.sequence.content", flxResult.getDateInt(), flxDayResult.getYsgEleNames());
				UserMail mail = UserMail.newSystemMail(title, preContent + "!", uid);
				mailList.add(mail);
				FlxEventPublisher.pubYaYaLeFailEvent(new BaseEventParam(uid, WayEnum.FLX_YSG));
			}
		}
		gameUserService.addItems(mailList);
		serverDataService.updateServerData(ysgBets);
	}

	/**
	 * 获取富临轩开奖记录
	 *
	 * @param sgBets
	 * @return
	 */
	private int getFlxSgResult(List<FlxCaiShuZiBet> sgBets) {
		int strategy = PowerRandom.getRandomBySeed(2);
		if (strategy == 1) {
			return PowerRandom.getRandomBetween(1, 36);
		}
		return getWiseSgResult(sgBets);
	}

	/**
	 * 根据投注记录智能获取中奖号码
	 *
	 * @param sgBets
	 * @return
	 */
	private int getWiseSgResult(List<FlxCaiShuZiBet> sgBets) {
		int totalCount = 36;
		List<FlxCaiShuZiBet> goldSgBets = sgBets.stream().filter(tmp -> tmp.getBetGold() != null && tmp.getBetGold() > 0).collect(Collectors.toList());
		Map<Integer, Long> groupCount = goldSgBets.stream()
				.collect(Collectors.groupingBy(FlxCaiShuZiBet::getBetNum, Collectors.counting()));
		// 总下注
		int total = goldSgBets.stream().mapToInt(FlxCaiShuZiBet::getBetGold).sum();
		// 按照下注号码分组求和
		Map<Integer, Integer> groupGold = goldSgBets.stream()
				.collect(Collectors.groupingBy(FlxCaiShuZiBet::getBetNum, Collectors.summingInt(FlxCaiShuZiBet::getBetGold)));

		// 挑选下注号码中，可以当中奖号码的号码
		List<Integer> matchRuleSelect = new ArrayList<>();
		for (Map.Entry<Integer, Integer> entry : groupGold.entrySet()) {
			if (entry.getValue() * 36 < total) {
				matchRuleSelect.add(entry.getKey());
			}
		}
//		System.out.println("---------下注可选号码-----------");
//		for (int i = 0; i < matchRuleSelect.size(); i++) {
//			System.out.println(matchRuleSelect.get(i));
//		}
		if (matchRuleSelect.size() > 0) {
			// 挑选中奖人数最多的号码
			int num = matchRuleSelect.get(0);
			long groupCountMax = 0;
			for (Map.Entry<Integer, Long> entry : groupCount.entrySet()) {
				if (!matchRuleSelect.contains(entry.getKey())) {
					continue;
				}
				if (entry.getValue() > groupCountMax) {
					groupCountMax = entry.getValue();
					num = entry.getKey();
				}
			}
			log.info("从下注号码中挑选了：" + num);
			return num;
		}
		// 如果下注的号码中没有可选号码，则从未下注的号码中随机选择一个

		List<Integer> noSelectNum = new ArrayList<>(totalCount);
		for (int i = 0; i < totalCount; i++) {
			noSelectNum.add(i + 1);
		}
		noSelectNum.removeAll(groupGold.keySet());
		if (noSelectNum.size() > 0) {
			// 采用洗牌算法随机
			Collections.shuffle(noSelectNum);
			log.info("从未下注号码中挑选了：" + noSelectNum.get(0));
			return noSelectNum.get(0);
		}
		// 如果所有号码都有人下注，则挑选中奖金额最少的号码
		int minGroupGoldValue = Integer.MAX_VALUE;
		int num = Integer.MAX_VALUE;
		for (Map.Entry<Integer, Integer> entry : groupGold.entrySet()) {
			if (entry.getValue() < minGroupGoldValue) {
				minGroupGoldValue = entry.getValue();
				num = entry.getKey();
			}
		}
		log.info("挑选了中奖金额最少的号码：" + num);
		return num;
	}
	/**
	 * 发放大乐透奖励邮件
	 *
	 * @param flxResult
	 * @param sgBets
	 */
	private void sendSgMailAward(ServerFlxResult flxResult, List<FlxCaiShuZiBet> sgBets) {
		if (ListUtil.isEmpty(sgBets)) {
			return;
		}
		List<UserMail> mailList = new ArrayList<>(sgBets.size());
		Set<Long> noMatchUids = new HashSet<>();
		Set<Long> awardUids = new HashSet<>();
		// 获取区服昨天的所有大乐透押注记录
		int odds = flxConfig.getSgOdds();
		int sgResult = flxResult.getMatchResult().getSgNum();
		for (FlxCaiShuZiBet sgBet : sgBets) {
			// 不是本区服
			if (sgBet.getServerFlxResult().longValue() != flxResult.getId().longValue()) {
				continue;
			}
			if (sgBet.getAwardMailId() > 0) {// 已经发送过奖励了
				continue;
			}
			Long uid = sgBet.getUid();
			if (sgBet.getBetNum() == sgResult) {// 获奖者
				String title = LM.I.getMsgByUid(uid,"mail.flx.dlt.award.title");
				int betCopper = sgBet.getBetCopper();
				int betGold = sgBet.getBetGold();
				List<Award> awards = new ArrayList<>();
				if (betCopper > 0) {
					awards.add(new Award(AwardEnum.TQ, betCopper * odds));
				}
				if (betGold > 0) {
					awards.add(new Award(AwardEnum.YB, betGold * odds));
				}
				String content = LM.I.getMsgByUid(uid,"mail.flx.dlt.award.content");
				String preContent = LM.I.getMsgByUid(uid,"mail.flx.dlt.results.num", flxResult.getDateInt(), sgResult);
				UserMail mail = UserMail.newAwardMail(title, preContent + content, uid, awards);
				sgBet.setAwardMailId(mail.getId());
				mailList.add(mail);
				// 控制邮件发放
				awardUids.add(uid);
				noMatchUids.remove(uid);
				FlxEventPublisher.pubCaiShuZiWinEvent(new BaseEventParam(uid, WayEnum.FLX_SG));
				continue;
			}
			// 已经添加了
			if (noMatchUids.contains(uid) || awardUids.contains(uid)) {
				continue;
			}
			noMatchUids.add(uid);
		}

		for (Long uid : noMatchUids) {
			String title = LM.I.getMsgByUid(uid,"mail.flx.dlt.results");
			String preContent = LM.I.getMsgByUid(uid,"mail.flx.dlt.results.num", flxResult.getDateInt(), sgResult);
			UserMail mail = UserMail.newSystemMail(title, preContent + "!", uid);
			mailList.add(mail);
			FlxEventPublisher.pubCaiShuZiFailEvent(new BaseEventParam(uid, WayEnum.FLX_SG));
		}

		gameUserService.addItems(mailList);
		serverDataService.updateServerData(sgBets);

	}

	/**
	 * 获取本期的所有押押乐押注记录
	 *
	 * @param dateInt
	 * @return
	 */
	public List<FlxYaYaLeBet> getALLYaYaLeBets(int dateInt) {
		List<FlxYaYaLeBet> betResult = new ArrayList<>();
		String loopKey = String.valueOf(dateInt);
		List<CfgServerEntity> servers = ServerTool.getAvailableServers();
		for (CfgServerEntity server : servers) {
			List<FlxYaYaLeBet> serverBetList = serverDataService.getServerDatas(server.getId(), FlxYaYaLeBet.class, loopKey);
			betResult.addAll(serverBetList);
		}
		return betResult;
	}

	/**
	 * 获取本期的的所有大乐透押注记录
	 *
	 * @param dateInt
	 * @return
	 */
	public List<FlxCaiShuZiBet> getALLCaiShuZiBets(int dateInt) {
		List<FlxCaiShuZiBet> betResult = new ArrayList<>();
		String loopKey = String.valueOf(dateInt);
		List<CfgServerEntity> servers = ServerTool.getAvailableServers();
		for (CfgServerEntity server : servers) {
			List<FlxCaiShuZiBet> serverBetList = serverDataService.getServerDatas(server.getId(), FlxCaiShuZiBet.class, loopKey);
			betResult.addAll(serverBetList);
		}
		return betResult;
	}
}
