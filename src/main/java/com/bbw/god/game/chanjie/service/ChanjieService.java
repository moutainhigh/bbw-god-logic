package com.bbw.god.game.chanjie.service;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.chanjie.ChanjieRd;
import com.bbw.god.game.chanjie.ChanjieRd.SpecailHonor;
import com.bbw.god.game.chanjie.ChanjieTools;
import com.bbw.god.game.chanjie.ChanjieType;
import com.bbw.god.game.chanjie.ChanjieUserInfo;
import com.bbw.god.game.chanjie.event.ChanjieEventPublisher;
import com.bbw.god.game.chanjie.event.EPChanjieGainHead;
import com.bbw.god.game.chanjie.event.EPChanjieReligionSelect;
import com.bbw.god.game.chanjie.event.EPChanjieSpecailHonor;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgChanjie;
import com.bbw.god.game.config.CfgChanjie.MailInfo;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.UserAchievementService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.res.ResEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lwb
 * @version 1.0
 * @date 2019年6月14日
 */
@Service
@Slf4j
public class ChanjieService {
	@Autowired
	private ChanjieRedisService redisService;
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private ChanjieUserService chanjieUserService;
	@Autowired
	private MailService mailService;
	@Autowired
	private ChanjieSundayFightService chanjieSundayFightService;
	@Autowired
	private UserAchievementService userAchievementService;
	@Value("${bbw-god.chanjie.beginTime:20:45:00}")//阐截斗法 战斗开启时间
	private String beginTime;
	@Value("${bbw-god.chanjie.endTime:22:00:00}") //阐截斗法 战斗结束时间
	private String endTime;
	@Value("${bbw-god.chanjie.open:false}")
	private boolean isOpen;// 是否开放阐截斗法

	/**
	 * 获取阐截斗法主页信息
	 *
	 * @param uid
	 */
	public ChanjieRd getChanjieInfo(long uid, int gid) {
		ChanjieRd rd = new ChanjieRd();
		ChanjieUserInfo userInfo = chanjieUserService.getUserInfo(uid);
		// 获取双教胜场
		rd.setJieVictory(getHashValue(ChanjieType.Religious_JIE, ChanjieType.KEY_WIN_NUM, gid));
		rd.setChanVictory(getHashValue(ChanjieType.Religious_CHAN, ChanjieType.KEY_WIN_NUM, gid));
		// 获取积分排行榜 前10的
		int limit = 10;
		List<ChanjieRd.RankingUserInfo> rankings = getRankings(userInfo.getReligiousType(), gid, 1, limit);
		rd.setRankingList(rankings);
		// 获取荣誉榜最后3条信息
		limit = 3;
		List<String> logs = getHonorList(userInfo.getReligiousType(), gid, 1, limit);
		rd.setHonorRankingList(logs);
		// 获取掌教积分信息
		Long first = getHashValue(userInfo.getReligiousType(), ChanjieType.KEY_FIRST_UID, gid);
		if (first != null && first > 0l) {
			ChanjieUserInfo firstInfo = chanjieUserService.getUserInfo(first);
			ChanjieRd.RankingUserInfo firstkingUserInfo = new ChanjieRd.RankingUserInfo();
			firstkingUserInfo.setHeadName(firstInfo.getHeadName());
			firstkingUserInfo.setHonor(firstInfo.getHonor());
			String nickname = gameUserService.getGameUser(first).getRoleInfo().getNickname();
			firstkingUserInfo.setRanking(0l + "");
			String nameStr = ChanjieTools.getServerNamePrefix(gameUserService.getActiveSid(uid)) + "." + nickname;
			firstkingUserInfo.setNickname(nameStr);
			rd.setFirstRankingInfo(firstkingUserInfo);
		}
		// 获取玩家个人信息
		ChanjieRd.UserInfo rdUserInfo = new ChanjieRd.UserInfo();
		rdUserInfo.setBloodVolume(userInfo.getBloodVolume());
		rdUserInfo.setFightNum(userInfo.getFightNum());
		rdUserInfo.setHonor(userInfo.getHonor());
		rdUserInfo.setHeadName(userInfo.getHeadName());
		rdUserInfo.setVictory(userInfo.getVictory());
		rdUserInfo.setBought(userInfo.getBought());
		if (userInfo.getVictory() == 0 || rdUserInfo.getFightNum() == 0) {
			rdUserInfo.setRateOfWinning("0%");
		} else {
			String rate = String.format("%.2f", 100 * userInfo.getVictory() / Double.valueOf(rdUserInfo.getFightNum()))
					+ "%";
			rdUserInfo.setRateOfWinning(rate);
		}
		if (userInfo.getHonor() == 0) {
			rdUserInfo.setRanking("未入榜");
		} else {
			Long rank = chanjieUserService.getRank(userInfo.getReligiousType(), uid, gid);
			rdUserInfo.setRanking(rank + "");
		}
		rd.setUserInfo(rdUserInfo);
		rd.setSurplusTime(getSeasonEndTime());
		rd.setDatetype(ChanjieType.DATE_TYPE_NOMAL.getValue());
		if (DateUtil.isWeekDay(6)) {
			rd.setDatetype(ChanjieType.DATE_TYPE_SAT.getValue());
		}
		rd.setFirstInto(userInfo.getFirstInto());
		rd.setBeginTimes(checkBeginTime());
		userInfo.setFirstInto(0);
		gameUserService.updateItem(userInfo);
		return rd;
	}

	/**
	 * 返回指定数量的荣誉榜信息
	 *
	 * @param ridType
	 * @param gid
	 * @param current
	 * @param limit
	 * @return
	 */
	public List<String> getHonorList(ChanjieType ridType, int gid, int current, int limit) {
		List<String> logs = redisService.getFightLogs(ridType, limit, current, gid);
		return logs;
	}

	/**
	 * 获取教派排名 100名+本人排名
	 *
	 * @param uid
	 * @param gid
	 * @param current
	 * @param pageSize
	 * @return
	 */
	public ChanjieRd getRanking(long uid, int gid, int current, int pageSize) {
		ChanjieRd rd = new ChanjieRd();
		// 获取玩家自己的排名信息
		ChanjieUserInfo userInfo = chanjieUserService.getUserInfo(uid);

		ChanjieRd.RankingUserInfo rankingUserInfo = new ChanjieRd.RankingUserInfo();
		rankingUserInfo.setHeadName(userInfo.getHeadName());
		rankingUserInfo.setHonor(userInfo.getHonor());
		if (userInfo.getHonor() == 0) {
			rankingUserInfo.setRanking("未入榜");
		} else {
			Long rank = chanjieUserService.getRank(userInfo.getReligiousType(), uid, gid);
			rankingUserInfo.setRanking(rank + "");
		}
		rankingUserInfo.setNickname(gameUserService.getGameUser(uid).getRoleInfo().getNickname());
		rd.setUserRankingInfo(rankingUserInfo);
		// 获取掌教信息
		Long first = getHashValue(ChanjieType.getType(userInfo.getReligiousId()), ChanjieType.KEY_FIRST_UID, gid);
		if (first > 0L) {
			ChanjieRd.RankingUserInfo firstkingUserInfo = new ChanjieRd.RankingUserInfo();
			String nameStr = ChanjieTools.getServerNamePrefix(gameUserService.getActiveSid(uid)) + "."
					+ gameUserService.getGameUser(first).getRoleInfo().getNickname();
			firstkingUserInfo.setNickname(nameStr);
			firstkingUserInfo.setHeadName("掌教师尊");
			rd.setFirstRankingInfo(firstkingUserInfo);
		}
		// 获取前100名排行榜
		rd.setRankingList(getRankings(userInfo.getReligiousType(), gid, current, pageSize));
		return rd;
	}

	/**
	 * 获取更多荣誉榜
	 *
	 * @param rid 教派ID
	 */
	public ChanjieRd getHonorRanking(int rid, int gid, int current, int limit) {
		if (DateUtil.isWeekDay(7)) {
			return chanjieSundayFightService.getHonorRanking(gid, current, limit);
		}
		ChanjieRd rd = new ChanjieRd();
		List<String> logs = getHonorList(ChanjieType.getType(rid), gid, current, limit);
		rd.setHonorRankingList(logs);
		return rd;
	}

	/**
	 * 每日22点30结算
	 */
	public void settleAccounts(int gid) {
		// 更新教派头衔
		if (hasSettle(gid)) {
			log.info("平台：【" + gid + "】，阐截斗法今日已结算！");
			return;
		}
		if (DateUtil.isWeekDay(7)) {
			log.info("平台：【" + gid + "】，阐截斗法周日不结算！");
			return;
		}
		Long CfirstUid = getHashValue(ChanjieType.Religious_CHAN, ChanjieType.KEY_FIRST_UID, gid);
		updateHeadName(ChanjieType.Religious_CHAN, CfirstUid, gid);
		Long JfirstUid = getHashValue(ChanjieType.Religious_JIE, ChanjieType.KEY_FIRST_UID, gid);
		updateHeadName(ChanjieType.Religious_JIE, JfirstUid, gid);
		// 更新教派奇人
		updateSpecialHonor(ChanjieType.Religious_CHAN, gid);
		updateSpecialHonor(ChanjieType.Religious_JIE, gid);
		if (DateUtil.isWeekDay(6)) {
			// 周六则赛季结算
			sendSeasonAward(gid);
		}
	}

	/**
	 * 更新用户的头衔
	 *
	 * @param religious
	 * @param leader
	 * @param gid
	 */
	private void updateHeadName(ChanjieType religious, Long leader, int gid) {
		String keyStr = ChanjieTools.getZsetKey(religious, ChanjieType.KEY_RANKING_ZSET, gid);
		Set<Long> uids = redisService.getAllRankingLists(keyStr);
		String name = "护教法王";// 1~3名 6级
		int rank = 1;
		int lv = 7;
		int pre = lv;
		List<Long> uidList = new ArrayList<>();
		for (long uid : uids) {
			switch (rank) {
				case 4:
					name = "大罗金仙";// 4~8 6级
					lv--;
					break;
				case 9:
					name = "大乘天仙";// 9~15 5级
					lv--;
					break;
				case 16:
					name = "渡劫地仙";// 16~35 4级
					lv--;
					break;
				case 36:
					name = "真传弟子";// 36~60 3级
					lv--;
					break;
				case 61:
					name = "内门弟子";// 61~100 2级
					lv--;
					break;
				case 101:
					name = "外门弟子";// 100以后 1级
					lv--;
					break;
			}
			rank++;
			if (leader != null && leader == uid) {
				continue;
			}
			ChanjieUserInfo userInfo = chanjieUserService.getUserInfo(uid);
			if (userInfo.getHonor() == 0) {
				// 积分为0的不排头衔
				continue;
			}
			userInfo.setHeadName(name);
			userInfo.setHonorLv(lv);
			gameUserService.updateItem(userInfo);
			if (!DateUtil.isWeekDay(7) && lv != pre) {
				// 发布头衔事件 非周日 且 段位区间变更 提交上个区间的头衔变更
				EPChanjieGainHead head = EPChanjieGainHead.instance(new BaseEventParam(uid), pre, uidList);
				ChanjieEventPublisher.pubGainHeadEvent(head);
				uidList = new ArrayList<>();
				pre = lv;
			} else if (rank == uids.size() + 1 && !DateUtil.isWeekDay(7)) {
				EPChanjieGainHead head = EPChanjieGainHead.instance(new BaseEventParam(uid), pre, uidList);
				ChanjieEventPublisher.pubGainHeadEvent(head);
			}
			uidList.add(uid);
		}
	}

	/**
	 * 获取榜单
	 *
	 * @param type
	 * @param gid
	 * @param current
	 * @param limit
	 * @return
	 */
	public List<ChanjieRd.RankingUserInfo> getRankings(ChanjieType type, int gid, int current, int limit) {
		List<ChanjieRd.RankingUserInfo> rankings = new ArrayList<>();
		int start = (current - 1) * limit;
		String keyStr = ChanjieTools.getZsetKey(type, ChanjieType.KEY_RANKING_ZSET, gid);
		Set<Long> uids = redisService.getRankingLists(keyStr, current, limit);
		long rank = start + 1;
		for (Long uid : uids) {
			ChanjieRd.RankingUserInfo ru = new ChanjieRd.RankingUserInfo();
			ChanjieUserInfo info = chanjieUserService.getUserInfo(uid);
			ru.setHeadName(info.getHeadName());
			ru.setHonor(info.getHonor());
			GameUser gu = gameUserService.getGameUser(uid);
			String nicknameString = ChanjieTools.getServerNamePrefix(gu.getServerId()) + "."
					+ gu.getRoleInfo().getNickname();
			ru.setNickname(nicknameString);
			if (info.getHonor() == 0) {
				ru.setRanking("未入榜");
			} else {
				ru.setRanking(rank + "");
				rank++;
			}
			rankings.add(ru);
		}
		return rankings;
	}

	/**
	 * 获取教派奇人
	 *
	 * @param uid
	 * @param gid
	 * @return
	 */
	public ChanjieRd getSpecailHonorList(long uid, int gid) {
		CfgChanjie cfg = Cfg.I.getUniqueConfig(CfgChanjie.class);
		ChanjieUserInfo info = chanjieUserService.getUserInfo(uid);
		List<ChanjieRd.SpecailHonor> honors = new ArrayList<>();
		getSpecailHonor(info, ChanjieType.KEY_SPECIAL_YRYY, cfg, gid, honors);
		getSpecailHonor(info, ChanjieType.KEY_SPECIAL_RBKD, cfg, gid, honors);
		getSpecailHonor(info, ChanjieType.KEY_SPECIAL_DDST, cfg, gid, honors);
		getSpecailHonor(info, ChanjieType.KEY_SPECIAL_TXZR, cfg, gid, honors);
		ChanjieRd rd = new ChanjieRd();
		rd.setSpecailHonors(honors);
		return rd;
	}


	/**
	 * 周六赛季结算=》发送奖励与资格 周日乱斗 需参与5场以上才有资格获得奖励。
	 */
	public void sendSeasonAward(int gid) {
		CfgChanjie cfg = Cfg.I.getUniqueConfig(CfgChanjie.class);
		String chankeyStr = ChanjieTools.getZsetKey(ChanjieType.Religious_CHAN, ChanjieType.KEY_RANKING_ZSET, gid);
		Set<Long> chanRankingSet = redisService.getAllRankingLists(chankeyStr);
		String jiekeyStr = ChanjieTools.getZsetKey(ChanjieType.Religious_JIE, ChanjieType.KEY_RANKING_ZSET, gid);
		Set<Long> jieRankingSet = redisService.getAllRankingLists(jiekeyStr);
		if (chanRankingSet.isEmpty() || jieRankingSet.isEmpty()) {
			log.error("区服阐教或截教人数不对，结算失败：" + gid);
			return;
		}
		// 发送 新晋级掌教通知邮件
		CfgChanjie.MailInfo firstMail = cfg.getMail(ChanjieType.Email_season_first.getValue());
		long chanF = chanRankingSet.iterator().next();
		long jieF = jieRankingSet.iterator().next();
		mailService.sendSystemMail(firstMail.getTitle(), firstMail.getContent(), chanF);
		mailService.sendSystemMail(firstMail.getTitle(), firstMail.getContent(), jieF);
		// 更新新掌教师尊
		addShiZun(ChanjieType.Religious_CHAN, chanF, gid);
		addShiZun(ChanjieType.Religious_JIE, jieF, gid);

		// 发送邀请函 排名前100 +掌教 并将 有资格参加周末封神的玩家加入到参赛列表中
		CfgChanjie.MailInfo inviteMail = cfg.getMail(ChanjieType.Email_invitation_letter.getValue());
		Set<Long> invitSet1 = chanRankingSet.stream().limit(100).collect(Collectors.toSet());
		Set<Long> invitSet2 = jieRankingSet.stream().limit(100).collect(Collectors.toSet());

		Long firstChan = getHashValue(ChanjieType.Religious_CHAN, ChanjieType.KEY_FIRST_UID, gid);
		Long firstJie = getHashValue(ChanjieType.Religious_JIE, ChanjieType.KEY_FIRST_UID, gid);
		if (firstChan > 0 && !invitSet1.contains(firstChan)) {
			invitSet1.add(firstChan);
		}
		if (firstJie > 0 && !invitSet2.contains(firstJie)) {
			invitSet2.add(firstJie);
		}
		chanjieSundayFightService.setLDFXPeople(invitSet1.size() + invitSet2.size(), gid);

		mailService.sendSystemMail(inviteMail.getTitle(), inviteMail.getContent(), invitSet1);
		mailService.sendSystemMail(inviteMail.getTitle(), inviteMail.getContent(), invitSet2);
		chanjieUserService.addInLDFX(invitSet1, gid);
		chanjieUserService.addInLDFX(invitSet2, gid);

		// 判断发送阵营胜负并发送奖励邮件
		CfgChanjie.MailInfo victoryMail = cfg.getMail(ChanjieType.Email_religious_victory.getValue());
		CfgChanjie.MailInfo defeatMail = cfg.getMail(ChanjieType.Email_religious_defeat.getValue());
		long chanWin = getReligiouWinNum(ChanjieType.Religious_CHAN, gid);
		long jieWin = getReligiouWinNum(ChanjieType.Religious_JIE, gid);
		if (chanWin == jieWin) {
			sendSeasonAwardEmail(chanRankingSet, victoryMail);
			sendSeasonAwardEmail(jieRankingSet, victoryMail);
		} else if (chanWin > jieWin) {
			// 阐教胜
			sendSeasonAwardEmail(chanRankingSet, victoryMail);
			sendSeasonAwardEmail(jieRankingSet, defeatMail);
		} else if (chanWin < jieWin) {
			// 截教胜
			sendSeasonAwardEmail(chanRankingSet, defeatMail);
			sendSeasonAwardEmail(jieRankingSet, victoryMail);
		}
		for (Long aLong : invitSet1) {
			ChanjieEventPublisher.pubLdfsInvitationEvent(aLong);
		}
		for (Long aLong : invitSet2) {
			ChanjieEventPublisher.pubLdfsInvitationEvent(aLong);
		}
	}

	public void sendSeasonAwardXF(int gid) {
		CfgChanjie cfg = Cfg.I.getUniqueConfig(CfgChanjie.class);
		String chankeyStr = ChanjieTools.getZsetKey(ChanjieType.Religious_CHAN, ChanjieType.KEY_RANKING_ZSET, gid);
		Set<Long> chanRankingSet = redisService.getAllRankingLists(chankeyStr);
		String jiekeyStr = ChanjieTools.getZsetKey(ChanjieType.Religious_JIE, ChanjieType.KEY_RANKING_ZSET, gid);
		Set<Long> jieRankingSet = redisService.getAllRankingLists(jiekeyStr);
		if (chanRankingSet.isEmpty() || jieRankingSet.isEmpty()) {
			log.error("区服阐教或截教人数不对，结算失败：" + gid);
			return;
		}
		// 判断发送阵营胜负并发送奖励邮件
		CfgChanjie.MailInfo victoryMail = cfg.getMail(ChanjieType.Email_religious_victory.getValue());
		CfgChanjie.MailInfo defeatMail = cfg.getMail(ChanjieType.Email_religious_defeat.getValue());
		long chanWin = getReligiouWinNum(ChanjieType.Religious_CHAN, gid);
		long jieWin = getReligiouWinNum(ChanjieType.Religious_JIE, gid);
		if (chanWin == jieWin) {
			sendSeasonAwardEmailXF(chanRankingSet, victoryMail);
			sendSeasonAwardEmailXF(jieRankingSet, victoryMail);
		} else if (chanWin > jieWin) {
			// 阐教胜
			sendSeasonAwardEmailXF(chanRankingSet, victoryMail);
			sendSeasonAwardEmailXF(jieRankingSet, defeatMail);
		} else if (chanWin < jieWin) {
			// 截教胜
			sendSeasonAwardEmailXF(chanRankingSet, defeatMail);
			sendSeasonAwardEmailXF(jieRankingSet, victoryMail);
		}

	}

	/**
	 * 更新教派奇人
	 */
	public void updateSpecialHonor(ChanjieType religious, int gid) {
		Date date = new Date();
		Date nDate = DateUtil.addDays(date, 1);
		// 游刃有余 胜场第一
		long yryyuid = 0L;
		String dailykey = ChanjieTools.getDailyKey(religious, date, ChanjieType.KEY_SPECIAL_YRYY, gid);
		Set<Long> yryyCSet = redisService.getAllRankingLists(dailykey);
		String specialKeyStr = ChanjieTools.getKey(religious, gid);
		if (yryyCSet != null && !yryyCSet.isEmpty()) {
			// 记录玩家ID=
			yryyuid = yryyCSet.iterator().next();
			String specailMapKey = ChanjieTools.getSpecailMapKey(nDate, ChanjieType.KEY_SPECIAL_YRYY, false);
			redisService.putHash(specialKeyStr, specailMapKey, yryyuid);
			// 记录对应的值
			Long num = (long) redisService.getZSetScore(dailykey, yryyuid);
			String specailMapKey2 = ChanjieTools.getSpecailMapKey(nDate, ChanjieType.KEY_SPECIAL_YRYY, true);
			redisService.putHash(specialKeyStr, specailMapKey2, ChanjieTools.getValueByScore(num));
		}
		// 锐不可当 连胜第一 从胜场最多开始
		long rbkduid = 0L;
		ChanjieUserInfo preinfo = null;
		ChanjieUserInfo nowinfo = null;
		int maxV = 0;
		if (yryyCSet != null) {
			for (Long uid : yryyCSet) {
				nowinfo = chanjieUserService.getUserInfo(uid);
				int num = nowinfo.getMaxVictory();
				if (preinfo == null) {
					maxV = num;
					preinfo = nowinfo;
					continue;
				}
				if (maxV == num) {
					Date preDate = preinfo.getVictoryStats().getGainDatetime();
					Date nowDate = nowinfo.getVictoryStats().getGainDatetime();
					if (DateUtil.millisecondsInterval(preDate, nowDate) > 0) {
						preinfo = nowinfo;
					}
				} else if (maxV < num) {
					preinfo = nowinfo;
					maxV = num;
				}
				if (maxV >= nowinfo.getVictory()) {
					// 最多连胜次数 大于等于玩家的总胜场 则已经找到最大的连胜玩家
					break;
				}
			}
		}
		if (preinfo != null) {
			rbkduid = preinfo.getGameUserId();
			redisService.putHash(specialKeyStr, ChanjieTools.getSpecailMapKey(nDate, ChanjieType.KEY_SPECIAL_RBKD,
					false), rbkduid);
			redisService.putHash(specialKeyStr,
					ChanjieTools.getSpecailMapKey(nDate, ChanjieType.KEY_SPECIAL_RBKD, true), maxV);
		}
		// 得道升天 积分第一
		long ddstuid = 0L;
		String ddstKeyStr = ChanjieTools.getDailyKey(religious, date, ChanjieType.KEY_SPECIAL_DDST, gid);
		Set<Long> ddstCSet = redisService.getRankingLists(ddstKeyStr, 1, 1);
		if (ddstCSet != null && !ddstCSet.isEmpty()) {
			ddstuid = ddstCSet.iterator().next();
			redisService.putHash(specialKeyStr, ChanjieTools.getSpecailMapKey(nDate, ChanjieType.KEY_SPECIAL_DDST,
					false), ddstuid);
			Long num = (long) redisService.getZSetScore(ddstKeyStr, ddstuid);
			redisService.putHash(specialKeyStr, ChanjieTools.getSpecailMapKey(nDate, ChanjieType.KEY_SPECIAL_DDST,
					true), ChanjieTools.getValueByScore(num));
		}
		// 天选之人 匹配仙人第一
		long txzruid = 0L;
		String txzrKeyStr = ChanjieTools.getDailyKey(religious, date, ChanjieType.KEY_SPECIAL_TXZR, gid);
		Set<Long> txzrCSet = redisService.getRankingLists(txzrKeyStr, 1, 1);
		if (txzrCSet != null && !txzrCSet.isEmpty()) {
			txzruid = txzrCSet.iterator().next();
			redisService.putHash(specialKeyStr, ChanjieTools.getSpecailMapKey(nDate, ChanjieType.KEY_SPECIAL_TXZR,
					false), txzruid);
			Long num = (long) redisService.getZSetScore(txzrKeyStr, txzruid);
			redisService.putHash(specialKeyStr, ChanjieTools.getSpecailMapKey(nDate, ChanjieType.KEY_SPECIAL_TXZR,
					true), ChanjieTools.getValueByScore(num));
		}
		EPChanjieSpecailHonor honor = EPChanjieSpecailHonor.instance(new BaseEventParam(), rbkduid, txzruid, yryyuid,
				ddstuid, religious.getValue());
		ChanjieEventPublisher.pubSpecailHonorEvent(honor);
	}

	/**
	 * 获取hash值 存储位置为：game:chanjie:周一日期:教派 =》key,value
	 *
	 * @param religious
	 * @param key
	 * @return
	 */
	public Long getHashValue(ChanjieType religious, ChanjieType key, int gid) {
		Object obj = redisService.getHashVal(ChanjieTools.getKey(religious, gid), key.getMemo());
		if (obj == null) {
			return 0L;
		}
		Long value = Long.parseLong(obj.toString());
		return value;
	}

	/**
	 * 改变值 num 正为加 反之为减
	 *
	 * @param religious 教派
	 * @param key       项key
	 * @param num
	 */
	public void addHashValue(ChanjieType religious, ChanjieType key, int num, int gid) {
		String hashKey = ChanjieTools.getKey(religious, gid);
		if (num > 0) {
			redisService.incHashVal(hashKey, key.getMemo(), num);
		} else {
			redisService.decHashVal(hashKey, key.getMemo(), num);
		}
	}

	/**
	 * 点赞
	 *
	 * @param uid
	 * @param type
	 * @param gid
	 * @return
	 */
	public ChanjieRd addLike(long uid, ChanjieType type, int gid) {
		Date date = new Date();
		ChanjieUserInfo info = chanjieUserService.getUserInfo(uid);
		String key = ChanjieTools.getKey(info.getReligiousType(), gid);
		Long suid = redisService.getHashVal(key, ChanjieTools.getSpecailMapKey(date, type, false));
		if (info.getSpecialHonorLiskeState().contains(type.getValue())) {
			// 今日已对该项点赞
			throw new ExceptionForClientTip("chanjie.like.have");
		}
		Long num = redisService.incHashVal(key, ChanjieTools.getSpecailMapKey(date, type, false) + "+like", 1);
		ChanjieRd rd = new ChanjieRd();
		// 进行点赞 ------完成后 玩家可以获得1w铜钱的奖励
		info.getSpecialHonorLiskeState().add(type.getValue());
		gameUserService.updateItem(info);
		// 获得1w铜钱的奖励
		ResEventPublisher.pubCopperAddEvent(uid, ChanjieTools.LIKE_ADD_COPPER, WayEnum.CHANJIE_ADD_LIKE, rd);
		if (num == 500 || num == 1000 || num == 1500 || num == 2000) {
			// 被点赞奖励：30W铜钱
			String title = LM.I.getMsgByUid(suid,"mail.chanJie.addLike.title");
			String content = LM.I.getMsgByUid(suid,"mail.chanJie.addLike.content", ChanjieType.getSpecailName(type), num);
			mailService.sendAwardMail(title, content, suid, "[{\"item\":20,\"num\":300000}]");
		}
		rd.setLikeNum(num);
		return rd;
	}

	/**
	 * 获取教派奇人的uid
	 *
	 * @param info
	 * @param key
	 * @param cfg
	 * @param gid
	 * @param honors
	 */
	public void getSpecailHonor(ChanjieUserInfo info, ChanjieType key, CfgChanjie cfg, int gid,
								List<ChanjieRd.SpecailHonor> honors) {
		ChanjieType religious = info.getReligiousType();
		String hashkey = ChanjieTools.getKey(religious, gid);
		Date date = new Date();
		Long uid = redisService.getHashVal(hashkey, ChanjieTools.getSpecailMapKey(date, key, false));
		if (uid == null || uid == 0) {
			return;
		}
		Object obj = redisService.getHashVal(hashkey, ChanjieTools.getSpecailMapKey(date, key, false) + "+like");
		Long num = 0L;
		if (obj != null) {
			num = Long.parseLong(obj + "");
		}
		SpecailHonor honor = new SpecailHonor();
		GameUser gu = gameUserService.getGameUser(uid);
		Object vobj = redisService.getHashVal(hashkey, ChanjieTools.getSpecailMapKey(date, key, true));
		String value = "0";
		if (vobj != null) {
			value = vobj.toString();
		}
		String server = ChanjieTools.getServerNamePrefix(gu.getServerId());
		String nickname = gu.getRoleInfo().getNickname();
		for (CfgChanjie.SpecialInfo ispe : cfg.getSpecialHonor()) {
			if (ispe.getId().equals(key.getValue())) {
				String memo = String.format(ispe.getMemo(), value, server, nickname);
				honor.setMemo(memo);
				honor.setContent(ispe.getContent());
			}
		}
		honor.setId(key.getValue());
		honor.setLike(num);
		boolean status = info.getSpecialHonorLiskeState().contains(key.getValue());
		honor.setStatus(status ? 1 : 0);
		honors.add(honor);
	}

	/**
	 * 检查当前时间是否在 20:45-22:00 之间 小于则返回距离20点45分还剩多少毫秒 在区间则返回0 大于则返回距离第二天20点45分还剩多少毫秒
	 *
	 * @return
	 */
	public long checkBeginTime() {
		// 每日：20:45-22:00
		Date today = new Date();
		Date beginDate = DateUtil.toDate(new Date(), beginTime);
		long times = DateUtil.millisecondsInterval(beginDate, today);
		if (times > 0) {
			return times;
		}
		Date endDate = DateUtil.toDate(new Date(), endTime);
		times = DateUtil.millisecondsInterval(endDate, today);
		if (times > 0) {
			return 0L;
		}
		// 如果已过 今日战斗时间 则返回 下次战斗时间
		beginDate = DateUtil.toDate(DateUtil.addDays(today, 1), beginTime);
		return DateUtil.millisecondsInterval(beginDate, today);
	}

	/**
	 * 核查阐截斗法战斗时间
	 */
	public void checkGameTime(int gid) {
		if (!isOpen) {
			throw new ExceptionForClientTip("chanjie.not.open");
		}
		if (checkBeginTime() != 0) {
			// 战斗未开始
			throw new ExceptionForClientTip("chanjie.not.begin.fight");
		}
		if (DateUtil.isWeekDay(7) && chanjieSundayFightService.gameIsOver(gid)) {
			// 比赛已结束
			throw new ExceptionForClientTip("chanjie.game.over");
		}
	}

	public long getSeasonEndTime() {
		// 获取今日到这周6 23点59分59秒的时间差 毫秒
		Date saturday = DateUtil.getWeekEndDateTime(new Date());
		saturday = DateUtil.addDays(saturday, -1);
		long timeout = DateUtil.millisecondsInterval(saturday, new Date());
		return timeout;
	}

	/**
	 * 添加新掌教
	 *
	 * @param type
	 * @param uid
	 * @param gid
	 */
	private void addShiZun(ChanjieType type, long uid, int gid) {
		redisService.putHash(ChanjieTools.getNewSeasonKey(type, gid), ChanjieType.KEY_FIRST_UID.getMemo(), uid);
		ChanjieUserInfo info = chanjieUserService.getUserInfo(uid);
		info.setReligiousId(type.getValue());
		info.setJoinDatetime(new Date());
		info.setSeasonId(ChanjieTools.getNextSeason());
		gameUserService.updateItem(info);
		// 发布掌教师尊获取头衔事件
		List<Long> uidSet = new ArrayList<Long>();
		uidSet.add(uid);
		EPChanjieGainHead head = EPChanjieGainHead.instance(new BaseEventParam(uid), 8, uidSet);
		ChanjieEventPublisher.pubGainHeadEvent(head);
		// 阵营选择
		EPChanjieReligionSelect select = EPChanjieReligionSelect.instance(new BaseEventParam(uid), type.getValue(),
				type.getValue(), true);
		ChanjieEventPublisher.pubReligionSelectEvent(select);
	}

	/**
	 * 获取教派胜场
	 *
	 * @param type
	 * @param gid
	 * @return
	 */
	private long getReligiouWinNum(ChanjieType type, int gid) {
		return getHashValue(type, ChanjieType.KEY_WIN_NUM, gid);
	}

	public void addReligiouWinNum(ChanjieType type, int gid) {
		addHashValue(type, ChanjieType.KEY_WIN_NUM, 1, gid);
	}

	private void sendSeasonAwardEmail(Set<Long> uidSet, MailInfo mail) {
		Set<Long> sendList = new HashSet<>();
		for (Long p : uidSet) {
			ChanjieUserInfo user = chanjieUserService.getUserInfo(p);
			if (user.canSendSeasonAward()) {
				sendList.add(p);
			}
		}
		mailService.sendAwardMail(mail.getTitle(), mail.getContent(), sendList, mail.getAwards());
	}

	private void sendSeasonAwardEmailXF(Set<Long> uidSet, MailInfo mail) {
		Set<Long> sendList = new HashSet<>();
		for (Long p : uidSet) {
			ChanjieUserInfo user = chanjieUserService.getUserInfo(p);
			if (user.canSendSeasonAward() || user.hasLDFX()) {
				sendList.add(p);
			}
		}
		mailService.sendAwardMail(mail.getTitle(), mail.getContent(), sendList, mail.getAwards());
	}

	/**
	 * 是否已结算
	 *
	 * @param gid
	 * @return
	 */
	private boolean hasSettle(int gid) {
		long date = DateUtil.toDateInt(new Date());
		String season = DateUtil.toDayString(DateUtil.getWeekBeginDateTime(new Date()));
		String keyString = "game:" + gid + ":chanjie:" + season + ":" + ChanjieType.KEY_HASH_SETTELE.getMemo();
		Long settle = redisService.getHashVal(keyString, "settle" + date);
		if (settle != null && settle == 1) {
			return true;
		}
		redisService.putHash(keyString, "settle" + date, 1L);
		return false;
	}

	/**
	 *
	 * 是否是战斗时间
	 * @param gid
	 * @return
	 */
	public boolean hasFightingTime(int gid){
		if (!isOpen){
			return false;
		}
		if (checkBeginTime() != 0) {
			// 战斗未开始
			return false;
		}
		if (DateUtil.isWeekDay(7) && chanjieSundayFightService.gameIsOver(gid)) {
			// 比赛已结束
			return false;
		}
		return true;
	}
}