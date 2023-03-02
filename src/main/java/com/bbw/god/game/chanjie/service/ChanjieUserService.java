package com.bbw.god.game.chanjie.service;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisZSetUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.chanjie.ChanjieRd;
import com.bbw.god.game.chanjie.ChanjieTools;
import com.bbw.god.game.chanjie.ChanjieType;
import com.bbw.god.game.chanjie.ChanjieUserInfo;
import com.bbw.god.game.chanjie.event.ChanjieEventPublisher;
import com.bbw.god.game.chanjie.event.EPChanjieReligionSelect;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.UserAchievementService;
import com.bbw.god.gameuser.redis.UserRedisKey;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

/**
 * 阐截斗法用户相关信息
 * 
 * @author lwb
 * @date 2019年6月14日
 * @version 1.0
 */
@Service
public class ChanjieUserService {
	@Autowired
	private RedisZSetUtil<Long> rankingList;
	@Autowired
	private ChanjieRedisService redisService;
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private ChanjieService chanjieService;
	@Autowired
	private UserAchievementService userAchievementService;

	/**
	 * 检查当前赛季是否加入季教派，有加入则返回教派ID，没有加入则返回教派双方人数，以及双方教派ID
	 * 
	 * @return
	 */
	public ChanjieRd checkJoinStatus(long uid, int gid) {
		ChanjieRd rd = new ChanjieRd();
		if (DateUtil.isWeekDay(7)) {
			// 乱斗不校验
			rd.setReligionId(ChanjieType.Religious_CHAN.getValue());
			return rd;
		}
		ChanjieUserInfo info = gameUserService.getSingleItem(uid, ChanjieUserInfo.class);
		if (null == info || (!info.hasReligiousNowSeason() && !info.isNextShiZun())) {
			Long chanMembersNum = getReligiouPeopleNum(ChanjieType.Religious_CHAN, gid);
			Long jieMembersNum = getReligiouPeopleNum(ChanjieType.Religious_JIE, gid);
			rd.setChanMembersNum(chanMembersNum);
			rd.setJieMembersNum(jieMembersNum);
			rd.setChanId(ChanjieType.Religious_CHAN.getValue());
			rd.setJieId(ChanjieType.Religious_JIE.getValue());
		} else {
			rd.setReligionId(info.getReligiousId());
		}
		return rd;
	}

	/**
	 * 加入教派
	 * 
	 * @param uid
	 * @param rid 教派ID
	 */
	public ChanjieRd joinReligiou(long uid, ChanjieType Jointype, int gid) {
		ChanjieUserInfo userInfo = getUserInfo(uid);
		if (userInfo.hasReligiousNowSeason()) {
			// 同赛季不允许重复加入
			throw new ExceptionForClientTip("chanjie.have.religious");
		}
		// 检查 加入的教派人数
		// 当总人数 大于指定人数时【300】， 需要平衡双方的人数 对比人数百分比差值>=20%时 不能加入人少的一方
		Long chanMembersNum = getReligiouPeopleNum(ChanjieType.Religious_CHAN, gid);
		Long jieMembersNum = getReligiouPeopleNum(ChanjieType.Religious_JIE, gid);
		double sumMembersNum = chanMembersNum + jieMembersNum;
		if (ChanjieTools.JOIN_CHECK_NUM <= sumMembersNum) {
			if (Jointype == ChanjieType.Religious_CHAN && (chanMembersNum / sumMembersNum) >= 0.6) {
				throw new ExceptionForClientTip("chanjie.member.more");
			} else if (Jointype == ChanjieType.Religious_JIE && (jieMembersNum / sumMembersNum) >= 0.6) {
				throw new ExceptionForClientTip("chanjie.member.more");
			}
		}
		// 允许加入
		LogUtil.logDeletedUserData("加入新教派", userInfo);
		int preSeasonId=userInfo.getSeasonId();
		int preRid = userInfo.getReligiousId();
		userInfo = initJoinUserInfo(uid, Jointype);
		// 加入到阐截斗法参赛玩家人数统计
		rankingList.add(ChanjieTools.GAME_STAT_PLAYER, uid, userInfo.getId());
		// 发布加入教派事件
		boolean continuity = ChanjieTools.getPreSeason().intValue() == preSeasonId;
		EPChanjieReligionSelect select = EPChanjieReligionSelect.instance(new BaseEventParam(uid),Jointype.getValue(), preRid,continuity);
		ChanjieEventPublisher.pubReligionSelectEvent(select);
		ChanjieRd rd = new ChanjieRd();
		rd.setReligionId(Jointype.getValue());
		return rd;
	}

	/**
	 * 初始化玩家信息
	 * 
	 * @param uid
	 * @param type
	 * @return
	 */
	public ChanjieUserInfo initJoinUserInfo(long uid, ChanjieType type) {
		ChanjieUserInfo info = getUserInfo(uid);
		info.restAll();
		info.setSeasonId(ChanjieTools.getNowSeason());
		info.setJoinDatetime(new Date());
		info.setReligiousId(type.getValue());
		gameUserService.updateItem(info);
		return info;
	}

	/**
	 * 战斗资格校验=》 血量大于0，周六除外
	 * 
	 * @param uid
	 * @return
	 */
	public ChanjieRd checkEligibility(long uid, int gid) {
		// 周一至周五 每日20:45-22:00 周日20:40开启
		chanjieService.checkGameTime(gid);
		ChanjieRd rd = new ChanjieRd();
		ChanjieUserInfo info = getUserInfo(uid);
		if (!info.hasReligiousNowSeason() && !info.isNextShiZun()) {
			throw new ExceptionForClientTip("chanjie.not.religious");
		}
		rd.setDatetype(ChanjieType.DATE_TYPE_NOMAL.getValue());
		rd.setReligionId(info.getReligiousId());
		rd.setHeadName("封神召唤师");
		if (!DateUtil.isWeekDay(7)) {
			rd.setHeadName(info.getHeadName());
		}
		
		if (DateUtil.isWeekDay(6)) {
			// 周六无限血
			rd.setDatetype(ChanjieType.DATE_TYPE_SAT.getValue());
		} else if (DateUtil.isWeekDay(7)) {
			rd.setDatetype(ChanjieType.DATE_TYPE_SUN.getValue());
			if (!info.hasLDFX()) {
				// 未入围比赛 不允许战斗
				throw new ExceptionForClientTip("chanjie.not.join.game");
			}
		}
		if (info.hasNotLife()) {
			// 血量不足提醒
			throw new ExceptionForClientTip("chanjie.blood.empty");
		}
		return rd;
	}

	/**
	 * 获取玩家阐截斗法信息对象
	 * 
	 * @param uid
	 * @return
	 */
	public ChanjieUserInfo getUserInfo(long uid) {
		ChanjieUserInfo info = gameUserService.getSingleItem(uid, ChanjieUserInfo.class);
		if (info == null) {
			info = new ChanjieUserInfo();
			info.setJoinDatetime(DateUtil.fromDateInt(20190101));
			info.setGameUserId(uid);
			info.setId(UserRedisKey.getNewUserDataId());
			gameUserService.addItem(uid, info);
			return info;
		}
		// 初始化今日数据
		if (!info.todayHasInit()) {
			// 先记录下旧数据
			LogUtil.logDeletedUserData("每日数据重置", info);
			info.restShiZun();
			// 初始化每日数据
			info.dayReset();
			if (DateUtil.isWeekDay(7)) {
				info.sundayRest();
			}else if (!DateUtil.isWeekDay(6)) {
				//非周末去除乱斗封神资格标识
				info.setInLDFX(0);
			}
			// 周六首次进入需要提示
			info.setFirstInto(DateUtil.isWeekDay(6) ? 1 : 0);
			if (!info.hasReligiousNowSeason() && !info.isNextShiZun()) {
				// 非本届数据 清空
				info.restAll();
			}
			gameUserService.updateItem(info);
		}
		return info;
	}

	/**
	 * 获取教派加入的人数
	 * 
	 * @param type 教派枚举
	 * @param gid
	 * @return
	 */
	public Long getReligiouPeopleNum(ChanjieType type, int gid) {
		String keyStr=ChanjieTools.getZsetKey(type, ChanjieType.KEY_RANKING_ZSET, gid);
		Long num = redisService.getZSetSize(keyStr);
		return num;
	}

	/**
	 * 获取玩家实际排名
	 * 
	 * @return
	 */
	public Long getRank(ChanjieType type, long uid, int gid) {
		Long rank = 0l;
		if (DateUtil.isWeekDay(7)) {
			rank=redisService.getRanking(ChanjieTools.getZsetKey(null, ChanjieType.KEY_RANKING_ZSET, gid), uid);
			return rank != null ? rank + 1 : null;
		}
		rank = redisService.getRanking(ChanjieTools.getZsetKey(type, ChanjieType.KEY_RANKING_ZSET, gid), uid);
		return rank == null ? null : rank + 1;
	}

	/**
	 * 是否还在榜单中
	 * 
	 * @param uid
	 * @param gid
	 * @return
	 */
	public boolean hasInLDFXfightloseRanking(long uid, int gid) {
		Long rank = redisService.getRanking(ChanjieTools.getZsetKey(null, ChanjieType.KEY_RANKING_LOSER_ZSET, gid),uid);
		return rank != null;
	}

	/**
	 * 购买血量
	 * 
	 * @param uid
	 * @return
	 */
	public ChanjieRd buyBoold(long uid) {
		if (DateUtil.isWeekDay(6) || DateUtil.isWeekDay(7)) {
			// 周六与周日不允许购买血量
			throw new ExceptionForClientTip("chanjie.blood.cant.buy");
		}
		ChanjieRd rd = new ChanjieRd();
		ChanjieUserInfo info = getUserInfo(uid);
		if (info.getBloodVolume() >= 3) {
			throw new ExceptionForClientTip("chanjie.blood.fill");
		}
		if (!info.canBuyBlood()) {
			throw new ExceptionForClientTip("chanjie.blood.buy.limit");
		}
		// 血量购买50元宝一次
		ResChecker.checkGold(gameUserService.getGameUser(uid), ChanjieTools.BUY_BLOOD_GOLD);
		ResEventPublisher.pubGoldDeductEvent(uid, ChanjieTools.BUY_BLOOD_GOLD, WayEnum.CHANJIE_BUY_BLOOD, rd);
		info.addBlood();
		info.addbought();
		gameUserService.updateItem(info);
		rd.setAddedBlood(1);
		return rd;
	}

	/**
	 * 添加乱斗封神资格标识
	 */
	public void addInLDFX(Set<Long> uids, int gid) {
		for (Long uid : uids) {
			ChanjieUserInfo info = getUserInfo(uid);
			info.updateLDFXStatus(true);
			gameUserService.updateItem(info);
		}
	}
}
