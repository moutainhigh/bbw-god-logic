package com.bbw.god.game.chanjie.service;

import com.bbw.common.DateUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.chanjie.ChanjieRd;
import com.bbw.god.game.chanjie.ChanjieTools;
import com.bbw.god.game.chanjie.ChanjieType;
import com.bbw.god.game.chanjie.ChanjieUserInfo;
import com.bbw.god.game.chanjie.event.ChanjieEventPublisher;
import com.bbw.god.game.chanjie.event.EPChanjieLDFSFourWin;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgChanjie;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 周日乱斗封神
 * 
 * @author lwb
 * @date 2019年6月18日
 * @version 1.0
 */
@Service
@Slf4j
public class ChanjieSundayFightService {
	@Autowired
	private ChanjieRedisService redisService;
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private ChanjieUserService chanjieUserService;
	@Autowired
	private MailService mailService;

	@Value("${bbw-god.chanjie.beginTime:20:45:00}")
	private String beginTime;
	@Value("${bbw-god.chanjie.endTime:22:00:00}")
	private String endTime;

	// 获取主页信息
	public ChanjieRd getMainInfo(long uid, int gid) {
		ChanjieRd rd = new ChanjieRd();
		ChanjieUserInfo info = chanjieUserService.getUserInfo(uid);
		rd.setKillNum(info.getVictory());
		String ranking = "未上榜";
		rd.setBloodVolume(info.getBloodVolume());
		if (info.hasLDFX()) {
			Long rank = chanjieUserService.getRank(info.getReligiousType(), uid, gid);
			if (rank != null) {
				ranking = String.valueOf(rank);
			} else if (chanjieUserService.hasInLDFXfightloseRanking(uid, gid)) {
				ranking = "已淘汰";
			}
		} else {
			ranking = "未入围";
		}
		rd.setRanking(ranking);
		rd.setHaveJoin(info.getInLDFX());
		rd.setPeopleNum(getLDFXPeople(gid));
		// 获取最新3条荣誉榜记录
		rd.setHonorRankingList(getHonorRankinglogs(gid, 1, 3));
		rd.setStop(gameIsOver(gid)?1:0);
		rd.setDatetype(ChanjieType.DATE_TYPE_SUN.getValue());
		// 排名前10
		rd.setSunRankingList(getRankingList(gid, 1, 10));
		Date beginDate = DateUtil.toDate(new Date(), beginTime);
		long time = DateUtil.millisecondsInterval(beginDate, new Date());
		if (time >= 0) {
			// 比赛未开始
			rd.setStop(-1);
		} else if (gameIsOver(gid)) {
			// 结束
			time = 0L;
		} else {
			Date endDate = DateUtil.toDate(new Date(), endTime);
			time = DateUtil.millisecondsInterval(endDate, new Date());
		}
		rd.setSurplusTime(time);
		return rd;
	}

	/**
	 * 获取更多排行榜排行榜
	 * 
	 * @return
	 */
	public ChanjieRd getRankingList(long uid, int gid, int current, int limit) {
		ChanjieRd rd = new ChanjieRd();
		rd.setSunRankingList(getRankingList(gid, current, limit));
		// 获取个人排行
		ChanjieRd.RankingSunInfo user = new ChanjieRd.RankingSunInfo();
		ChanjieUserInfo info = chanjieUserService.getUserInfo(uid);
		user.setNickname(gameUserService.getGameUser(uid).getRoleInfo().getNickname());
		if (info.hasLDFX()) {
			Long rank = chanjieUserService.getRank(null, uid, gid);
			if (info.getBloodVolume() > 0) {
				user.setVictory(info.getVictory() + "场");
			}
			if (rank != null) {
				String rankStr = rank > 100 ? "100名以后" : String.valueOf(rank);
				user.setRank(rankStr);
			} else if (chanjieUserService.hasInLDFXfightloseRanking(uid, gid)) {
				user.setVictory("已淘汰");
				user.setRank("已淘汰");
			} else {
				user.setRank("未上榜");
			}
		} else {
			user.setRank("未入围");
			user.setVictory("未入围");
		}
		rd.setRankingInfo(user);
		rd.setStop(gameIsOver(gid)?1:0);
		return rd;
	}

	/**
	 * 获取更多荣誉榜
	 *
	 * @param gid
	 * @param current
	 * @param limit
	 * @return
	 */
	public ChanjieRd getHonorRanking(int gid, int current, int limit) {
		ChanjieRd rd = new ChanjieRd();
		rd.setHonorRankingList(getHonorRankinglogs(gid, current, limit));
		return rd;
	}

	/**
	 * 获取指定数量的荣誉榜
	 * 
	 * @param gid
	 * @param current
	 * @param limit
	 * @return
	 */
	private List<String> getHonorRankinglogs(int gid, int current, int limit) {
		List<String> logs = redisService.getFightLogs(null, limit, current, gid);
		return logs;
	}

	/**
	 * 获取实时战况
	 * 
	 * @param uid
	 * @param gid
	 * @return
	 */
	public ChanjieRd warSituation(long uid, int gid) {
		ChanjieRd rd = new ChanjieRd();
		ChanjieUserInfo info = chanjieUserService.getUserInfo(uid);
		rd.setKillNum(info.getVictory());
		String ranking = "未入围";
		if (info.hasLDFX()) {
			if (info.isRnakingLDFX()) {
				ranking = "已淘汰";
				Long rank =redisService.getRanking(ChanjieTools.getZsetKey(null, ChanjieType.KEY_RANKING_ZSET, gid), uid);
				if (rank!=null && rank>0) {
					ranking = rank.toString();
				}else {
					ranking = "未上榜";
				}
			}else {
				ranking = "未上榜";
			}
		}
		rd.setRanking(ranking);
		rd.setPeopleNum(getLDFXPeople(gid));
		return rd;
	}

	/**
	 * 获得指定多少排名的排行榜
	 *
	 * @param gid
	 * @param current
	 * @param limit
	 * @return
	 */
	private List<ChanjieRd.RankingSunInfo> getRankingList(int gid, int current, int limit) {
		String keyStr = ChanjieTools.getZsetKey(null, ChanjieType.KEY_RANKING_ZSET, gid);
		int start = (current - 1) * limit;
		Set<Long> uids =redisService.getRankingLists(keyStr, current, limit);
		List<ChanjieRd.RankingSunInfo> list = new ArrayList<>();
		for (Long id : uids) {
			ChanjieRd.RankingSunInfo r = new ChanjieRd.RankingSunInfo();
			GameUser gu = gameUserService.getGameUser(id);
			String serverName = ChanjieTools.getServerNamePrefix(gu.getServerId()) + "." + gu.getRoleInfo().getNickname();
			r.setNickname(serverName);
			ChanjieUserInfo userInfo = chanjieUserService.getUserInfo(id);
			r.setVictory(userInfo.getVictory() + "场");
			r.setOutState(ChanjieType.OUT_STATE_FALSE.getValue());
			list.add(r);
		}
		int dv = limit - uids.size();
		if (dv > 0) {
			//排行榜中的最大人数
			Long maxSize=redisService.getZSetSize(keyStr);
			long skip=start-maxSize;
			skip=skip>0?skip:0;
			long endIndex = skip + dv - 1;
			String keyString = ChanjieTools.getZsetKey(null, ChanjieType.KEY_RANKING_LOSER_ZSET, gid);
			uids =redisService.getRankingListsByindex(keyString, skip, endIndex);
			for (Long id : uids) {
				ChanjieRd.RankingSunInfo r = new ChanjieRd.RankingSunInfo();
				GameUser gu = gameUserService.getGameUser(id);
				String serverName = ChanjieTools.getServerNamePrefix(gu.getServerId()) + "." + gu.getRoleInfo().getNickname();
				r.setNickname(serverName);
				r.setVictory("已淘汰");
				r.setOutState(ChanjieType.OUT_STATE_TRUE.getValue());
				list.add(r);
			}
		}
		return list;
	}

	/**
	 * 结束战斗
	 * 
	 * @return
	 */
	public void stopFight(long uid, int gid) {
		if (gameIsOver(gid)) {
			BaseEventParam bep = new BaseEventParam(uid, WayEnum.CHANJIE_FIGHT, new RDCommon());
			EPChanjieLDFSFourWin ep = EPChanjieLDFSFourWin.instance(bep, false);
			ChanjieEventPublisher.pubLDFSFourWinEvent(ep);
			return;
		}
		redisService.putHash(ChanjieTools.getKey(null, gid), ChanjieType.KEY_SUNDAY_GAME_STOP.getMemo(), 1);
		// 发送最终胜利邮件奖励
		CfgChanjie cfg = Cfg.I.getUniqueConfig(CfgChanjie.class);
		CfgChanjie.MailInfo firstMail = cfg.getMail(ChanjieType.Email_final_warad.getValue());
		mailService.sendAwardMail(firstMail.getTitle(), firstMail.getContent(), uid, firstMail.getAwards());
		// 暂时用不到
		// EPChanjieLDFSFourWin ep = EPChanjieLDFSFourWin.instance(new
		// BaseEventParam(uid), true);
		// ChanjieEventPublisher.pubLDFSFourWinEvent(ep);
	}

	/**
	 * 出局 按分数从小到大 分数越小则越后出局
	 * 
	 * @param uid
	 */
	public void gameout(long uid, int gid) {
		String key=ChanjieTools.getZsetKey(null, ChanjieType.KEY_RANKING_ZSET, gid);
		redisService.removeZset(key,uid);
		long outTime = DateUtil.getSecondsBetween(new Date(), DateUtil.getWeekEndDateTime(new Date()));
		redisService.addZset(ChanjieTools.getZsetKey(null, ChanjieType.KEY_RANKING_LOSER_ZSET, gid), outTime,uid);
		incLDFXPeople(1,gid);
	}

	public void gameout(Long[] uids, int gid) {
		String key=ChanjieTools.getZsetKey(null, ChanjieType.KEY_RANKING_ZSET, gid);
		redisService.removeZset(key,uids);
		double[] vals = new double[uids.length];
		for (int i = 0; i < uids.length; i++) {
			long timeout = DateUtil.getSecondsBetween(new Date(), DateUtil.getWeekEndDateTime(new Date()));
			vals[i] = Double.parseDouble(timeout + "");
		}
		redisService.addZsets(key, vals, uids);
		incLDFXPeople(uids.length,gid);
	}

	/**
	 * 判断是已结束 乱斗封神
	 * 
	 * @param gid
	 * @return
	 */
	public boolean gameIsOver(int gid) {
		Long value = redisService.getHashVal(ChanjieTools.getKey(null, gid), ChanjieType.KEY_SUNDAY_GAME_STOP.getMemo());
		if (value != null && value == 1) {
			return true;
		}
		return false;
	}

	/**
	 * 淘汰 开始 当活动进行20分钟后，没有进入前100名的玩家，自动淘汰。 当活动进行40分钟后，没有进入前45名的玩家，自动淘汰。
	 * 
	 * @param min
	 * @param gid
	 */
	public void eliminate(int min, int gid) {
		if (gameIsOver(gid)) {
			return;
		}
		String key = ChanjieTools.getZsetKey(null, ChanjieType.KEY_RANKING_ZSET, gid);
		Set<Long> uids = redisService.getAllRankingLists(key);
		Long[] outs = null;
		if (uids.size() > min) {
			outs = uids.stream().skip(min).toArray(Long[]::new);
		}
		if (outs != null) {
			this.gameout(outs, gid);
		}
	}

	public long getLDFXPeople(int gid) {
		Long num = redisService.getHashVal(ChanjieTools.getKey(null, gid),  ChanjieType.KEY_SUNDAY_GAME_people.getMemo());
		if (num==null) {
			num=0l;
		}
		return num;
	}
	
	public void setLDFXPeople(int num,int gid) {
		redisService.putHash(ChanjieTools.getKey(null, gid),  ChanjieType.KEY_SUNDAY_GAME_people.getMemo(),num);
	}
	
	public void incLDFXPeople(int cut,int gid) {
		Long num=redisService.getHashVal(ChanjieTools.getKey(null, gid),  ChanjieType.KEY_SUNDAY_GAME_people.getMemo());
		long people=0l;
		if (num!=null) {
			people=num-cut;
		}
		redisService.putHash(ChanjieTools.getKey(null, gid),  ChanjieType.KEY_SUNDAY_GAME_people.getMemo(),people);
		if (people==1) {
			String key = ChanjieTools.getZsetKey(null, ChanjieType.KEY_RANKING_ZSET, gid);
			Set<Long> uids = redisService.getAllRankingLists(key);
			if (uids.size()==1) {
				//最终胜利
				stopFight(uids.iterator().next(),gid);
			}
		}
	}
	
	public void stopGame(int gid) {
		String key = ChanjieTools.getZsetKey(null, ChanjieType.KEY_RANKING_ZSET, gid);
		if (gameIsOver(gid)) {
			return;
		}
		Set<Long> uids =  redisService.getAllRankingLists(key);
		if (uids.isEmpty()) {
			log.error("平台："+gid+"，没有符合要求的最终玩家");
			redisService.putHash(ChanjieTools.getKey(null, gid), ChanjieType.KEY_SUNDAY_GAME_STOP.getMemo(), 1);
			return;
		}
		//最终胜利
		stopFight(uids.iterator().next(),gid);
	}
}
