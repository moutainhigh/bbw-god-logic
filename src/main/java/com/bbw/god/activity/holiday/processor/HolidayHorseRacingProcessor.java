package com.bbw.god.activity.holiday.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.JSONUtil;
import com.bbw.common.LM;
import com.bbw.common.PowerRandom;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.db.redis.RedisListUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDHorseRacing;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author lwb
 * 节日赛马
 **/
@Slf4j
@Service
public class HolidayHorseRacingProcessor extends AbstractActivityProcessor {
	@Autowired
	private RedisHashUtil<Long, String> hashUtil;
	@Autowired
	private RedisListUtil<Long> listUtil;
	@Autowired
	private UserTreasureService userTreasureService;
	@Autowired
	private RedisValueUtil<List<Long>> valueUtil;
	private static final String BASE_KEY = "game:horseRacing:";
	private static final String[] horseNames = {"哈士奇", "杜宾", "萨摩耶", "高加索犬", "奇努克犬", "罗威纳犬"};
	private static final int EXPIRE_TIME = 9;
	private static final TimeUnit EXPIRE_TIMEUNIT = TimeUnit.DAYS;
	private static final Integer HOLIDAY_TREASURE = TreasureEnum.SMALL_BOWL_DOG_FOOD.getValue();
	/** 积分id */
	private static final Integer POINT_Id = TreasureEnum.PUPPY_BRAND.getValue();
	private static final Date endDate = DateUtil.fromDateTimeString("2022-12-16 23:59:59");

	@Autowired
	private MailService mailService;

	public HolidayHorseRacingProcessor() {
		this.activityTypeList = Arrays.asList(ActivityEnum.HOLIDAY_HORSE_RACING);
	}

	@Override
	protected long getRemainTime(long uid, int sid, IActivity a) {
		if (a.gainEnd() != null) {
			return a.gainEnd().getTime() - System.currentTimeMillis();
		}
		return NO_TIME;
	}

	/**
	 * 是否在ui中展示
	 *
	 * @return
	 */
	@Override
	public boolean isShowInUi(long uid) {
		return true;
	}

	/**
	 * 是否在活动期间
	 *
	 * @param sid
	 * @return
	 */
	public boolean opened(int sid) {
		ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.HOLIDAY_HORSE_RACING.getValue());
		IActivity a = this.activityService.getActivity(sid, activityEnum);
		if (a == null) {
			return false;
		}
		return a.ifTimeValid();
	}

	@Override
	public RDSuccess getActivities(long uid, int activityType) {
		RDHorseRacing rd = new RDHorseRacing();
		int num = userTreasureService.getTreasureNum(uid, getPointId());
		rd.setHorseRacingPoint(num);
		Date now = DateUtil.now();
		if (DateUtil.millisecondsInterval(now, endDate) > 0) {
			rd.setSettleTime(0L);
			rd.setStopBet(1);
		} else {
			long time = getNextResultTime(new Date(), 0);
			Date endDate = DateUtil.fromDateLong(time);
			long interval = DateUtil.millisecondsInterval(endDate, new Date());
			rd.setSettleTime(interval);
		}
		int[] betData = getBetInfo(uid);
		rd.setBetInfo(betData);
		return rd;
	}

	/**
	 * 获得积分id
	 *
	 * @return
	 */
	private int getPointId() {
		return POINT_Id;
	}

	/**
	 * 投注
	 *
	 * @param number   号码 1~6 号
	 * @param multiple 倍数
	 */
	public RDCommon bet(long uid, int number, int multiple) {
		RDCommon rd = new RDCommon();
		Date now = DateUtil.now();
		if (DateUtil.millisecondsInterval(now, endDate) > 0) {
			throw new ExceptionForClientTip("activity.is.timeout");
		}
		TreasureChecker.checkIsEnough(HOLIDAY_TREASURE, multiple, uid);
		TreasureEventPublisher.pubTDeductEvent(uid, HOLIDAY_TREASURE, multiple, WayEnum.TREASURE_USE, rd);
		String betKey = getUserBetKey(gameUserService.getActiveGid(uid), getNextResultTime(new Date(), 0));
		int[] betData = getBetInfo(uid);
		betData[number - 1] += multiple;
		hashUtil.putField(betKey, uid, JSONUtil.toJson(betData));
		hashUtil.expire(betKey, EXPIRE_TIME, EXPIRE_TIMEUNIT);
		return rd;
	}

	public int[] getBetInfo(long uid){
		String betKey = getUserBetKey(gameUserService.getActiveGid(uid), getNextResultTime(new Date(), 0));
		String betData = hashUtil.getField(betKey, uid);
		if (betData==null){
			return new int[]{0, 0, 0, 0, 0, 0};
		}
		return JSONUtil.fromJsonArray(betData);
	}
	/**
	 * 正常定时器 结算
	 */
	public void settleNowByGid(int gid){
		//获取当前最近结算的时间
		long endSettleDate=getNextResultTime(new Date(),-1);
		String settleKey = getSettleKey(gid);
		List<Long> settleLogs = valueUtil.get(settleKey);
		if (settleLogs==null){
			settleLogs=new ArrayList<>();
			settleBySettleDate(gid,endSettleDate);
			settleLogs.add(endSettleDate);
			valueUtil.set(settleKey,settleLogs,EXPIRE_TIME,EXPIRE_TIMEUNIT);
		}else {
			//从记录开始结算；结算至最新的
			Long lastSettle = settleLogs.get(settleLogs.size() - 1);
			if (lastSettle>=endSettleDate){
				//已经是最新的了
				log.info(gid+"赛马结算已经是最新数据！"+endSettleDate);
				return;
			}
			Date lastSettleDate=DateUtil.fromDateLong(lastSettle);
			while (lastSettle<endSettleDate){
				lastSettleDate = DateUtil.addMinutes(lastSettleDate, 30);
				lastSettle=DateUtil.toDateTimeLong(lastSettleDate);
				if (settleLogs.contains(lastSettle)){
					log.info(gid+"赛马已经结算！"+lastSettle);
					continue;
				}
				settleBySettleDate(gid,lastSettle);
				settleLogs.add(lastSettle);
				valueUtil.set(settleKey,settleLogs,EXPIRE_TIME,EXPIRE_TIMEUNIT);
				log.info(gid+"赛马结算成功！"+lastSettle);
			}
		}
	}

	/**
	 * 结算指定阶段
	 * @param gid
	 * @param settleDate
	 */
	public void settleBySettleDate(int gid,long settleDate){
		int[] result = getRacingResult(gid, settleDate);
		String betKey = getUserBetKey(gid, settleDate);
		Map<Long, String> map = hashUtil.get(betKey);
		String settleSuccessKey=getSettleSuccessKey(gid,settleDate);
		List<Long> settleSuccessList=listUtil.get(settleSuccessKey);
		if (settleSuccessList==null){
			settleSuccessList=new ArrayList<>();
		}
		if (map!=null && !map.isEmpty()){
			String title=LM.I.getMsgByUid(map.entrySet().iterator().next().getKey(),"mail.dragon.boat.regatta.results.title");
			String content = LM.I.getMsgByUid(map.entrySet().iterator().next().getKey(),"mail.dragon.boat.regatta.results.content",settleDate,horseNames[result[0]-1],horseNames[result[1]-1],horseNames[result[2]-1]);
			for (Map.Entry<Long, String> entry : map.entrySet()) {
				long uid = entry.getKey();
				try {
					if (settleSuccessList.contains(uid)) {
						//已结算过了
						continue;
					}
					int[] bet = JSONUtil.fromJsonArray(entry.getValue());
					int add = 0;
					int myScore = userTreasureService.getTreasureNum(uid, getPointId());
					for (int rank = 0; rank < 6; rank++) {
						//number 号码 rank为名次
						int number = result[rank];
						if (rank == 0) {
							//第一名 5分
							add += 5 * bet[number - 1];
						} else if (rank >= 3) {
							//第4~6名 1分
							add += 1 * bet[number - 1];
						} else {
							//第2~3名 3分
							add += 3 * bet[number - 1];
						}
					}
					if (add > 0) {
						TreasureEventPublisher.pubTAddEvent(uid, getPointId(), add, WayEnum.HORSE_RACING_ADD, new RDCommon());
					}
					myScore += add;
					String msg = LM.I.getMsgByUid(uid, "mail.dragon.boat.regatta.user.results.content", TreasureEnum.fromValue(getPointId()).getName(), add, TreasureEnum.fromValue(getPointId()).getName(), myScore);
					String massage = content + msg;
					mailService.sendSystemMail(title, massage, entry.getKey());
					listUtil.rightPush(settleSuccessKey, uid);
				}catch (Exception e){
					log.error(e.getMessage(),e);
				}
			}
			listUtil.expire(settleSuccessKey,EXPIRE_TIME,EXPIRE_TIMEUNIT);
		}
	}

	public int[] getRacingResult(int gid,long settleDate){
		String key = getResultKey(gid);
		String results = hashUtil.getField(key, settleDate);
		if (results==null){
			List<Integer> randoms=Arrays.asList(1,2,3,4,5,6);
			PowerRandom.shuffle(randoms);
			int[] res=new int[6];
			for (int i = 0; i < 6; i++) {
				res[i]=randoms.get(i);
			}
			hashUtil.putField(key,settleDate,JSONUtil.toJson(res));
			hashUtil.expire(key,EXPIRE_TIME,EXPIRE_TIMEUNIT);
			return res;
		}else {
			return JSONUtil.fromJsonArray(results);
		}
	}
	/**
	 * 下注结果key
	 * @param gid
	 * @return
	 */
	public String getResultKey(int gid){
		return BASE_KEY+gid+":result";
	}

	/**
	 * 玩家下注的key
	 * @param gid
	 * @param datetime
	 * @return
	 */
	public String getUserBetKey(int gid,long datetime){
		return BASE_KEY+gid+":bet:"+datetime;
	}

	public String getSettleSuccessKey(int gid,long datetime){
		return BASE_KEY+gid+":settleSuccess:"+datetime;
	}
	/**
	 * 邮件通知key
	 * @param gid
	 * @return
	 */
	public String getSettleKey(int gid){
		return BASE_KEY+gid+":settle";
	}

	/**
	 * 返回 下一个 开赛结果时间
	 * @param date
	 * @param next
	 * @return
	 */
	public static long getNextResultTime(Date date,int next){
		Date newDate = DateUtil.addMinutes(date, next * 30);
		//去除2位 得到的结果为yyyyMMddHHmm
		long timeLong = DateUtil.toDateTimeLong(newDate)/100;
		long minute=timeLong%100;
		if (minute>=30){
			newDate=DateUtil.addHours(newDate,1);
			timeLong = DateUtil.toDateTimeLong(newDate)/100;
			minute=0;
		}else {
			minute=30;
		}
		return (timeLong/100*100+minute)*100;
	}
}
