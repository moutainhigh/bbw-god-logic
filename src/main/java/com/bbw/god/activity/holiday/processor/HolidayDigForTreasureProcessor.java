package com.bbw.god.activity.holiday.processor;

import com.bbw.common.PowerRandom;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.AbstractSpecialCityProcessor;
import com.bbw.god.city.chengc.RDTradeInfo;
import com.bbw.god.city.chengc.trade.BuyGoodInfo;
import com.bbw.god.city.chengc.trade.IChengChiTradeService;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgDigForTreasure;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 野外挖宝
 *
 * @author lwb
 */
@Service
public class HolidayDigForTreasureProcessor extends AbstractSpecialCityProcessor implements IChengChiTradeService {
	@Autowired
	private RedisHashUtil<Long, Integer> hashUtil;

	private static final String BASE_KEY = "game:digForTreasure:";
	private static final int EXPIRE_TIME = 15;
	private static final TimeUnit EXPIRE_TIMEUNIT = TimeUnit.DAYS;
	/** 购买道具需要的铜钱 */
	private static final Integer BUY_NEED_COPPER = 5000;
	/** 获得活动特产概率 */
	private static final int GET_SPECIAL_PROB = 25;

	public HolidayDigForTreasureProcessor() {
		this.activityTypeList = Arrays.asList(ActivityEnum.HOLIDAY_DIG_FOR_TREASURE);
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
		return false;
	}

	/**
	 * 是否在活动期间
	 *
	 * @param sid
	 * @return
	 */
	public boolean opened(int sid) {
		ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.HOLIDAY_DIG_FOR_TREASURE.getValue());
		IActivity a = this.activityService.getActivity(sid, activityEnum);
		if (a == null) {
			return false;
		}
		return a.ifTimeValid();
	}

	/**
	 * 初始化额外大奖的格子
	 * 随机3个
	 * 存储zset 对应为    格子ID=》额外奖励档次。
	 * @param gid
	 */
	public void initMapData(int gid){
		List<CfgRoadEntity> roads = RoadTool.getRoads();
		int additionalAwardsNums= getCfgDigForTreasure().getAdditionalAwardsPool().size();
		List<CfgRoadEntity> randomsFromList = PowerRandom.getRandomsFromList(roads,additionalAwardsNums);
		String awardRoadsKey = getAdditionalAwardRoadsKey(gid);
		int awardIndex=1;
		hashUtil.delete(awardRoadsKey);
		String memo="平台号"+gid+"节日挖宝位置:";
		Map<Long,Integer> map=new HashMap<>();
		for (CfgRoadEntity roadEntity : randomsFromList) {
			map.put(Long.valueOf(roadEntity.getId()),awardIndex);
			hashUtil.delete(getGainAdditionalAwardLogsKey(gid,awardIndex));
			awardIndex++;
			memo+="  "+roadEntity.getCity().getName();
		}

		hashUtil.putAllField(awardRoadsKey,map);
		System.err.println("初始化节日挖宝完成："+memo);
		hashUtil.expire(awardRoadsKey,EXPIRE_TIME,EXPIRE_TIMEUNIT);
	}

	/**
	 * 是否可以获得额外的奖励
	 * @param roadId
	 * @return
	 */
	public Optional<CfgDigForTreasure.AdditionalAward> canGainAdditionalAwardAndAddLogs(long uid, int roadId, Integer gid){
		String awardRoadsKey = getAdditionalAwardRoadsKey(gid);
		if (!hashUtil.exists(awardRoadsKey)){
			synchronized (gid){
				if (!hashUtil.exists(awardRoadsKey)){
					initMapData(gid);
				}
			}
		}
		Integer awardIndex = hashUtil.getField(awardRoadsKey, Long.valueOf(roadId));
		if (awardIndex==null||awardIndex<0){
			//不是大奖位置
			return Optional.empty();
		}
		String logsKey = getGainAdditionalAwardLogsKey(gid, awardIndex);
		Integer logTimes = hashUtil.getField(logsKey, uid);
		if (logTimes==null|| logTimes<=0){
			logTimes=0;
		}
		CfgDigForTreasure cfgDigForTreasure = getCfgDigForTreasure();
		if (cfgDigForTreasure.getAdditionalAwardsPool().size()<awardIndex){
			return Optional.empty();
		}
		CfgDigForTreasure.AdditionalAward additionalAward = cfgDigForTreasure.getAdditionalAwardsPool().get(awardIndex - 1);
		if (logTimes>=additionalAward.getMaxTimes()){
			return Optional.empty();
		}
		logTimes++;
		hashUtil.putField(logsKey,uid,logTimes);
		hashUtil.expire(logsKey,EXPIRE_TIME,EXPIRE_TIMEUNIT);
		return Optional.of(additionalAward);
	}


	/**
	 * 额外大奖的位置key
	 * @param gid
	 * @return
	 */
	public static String getAdditionalAwardRoadsKey(int gid){
		if(gid==17){
			gid=16;
		}
		return BASE_KEY+gid+":awardRoad";
	}

	/**
	 * 额外大奖 获取记录key
	 * @param gid
	 * @param index
	 * @return
	 */
	public static String getGainAdditionalAwardLogsKey(int gid,int index){
		if(gid==17){
			gid=16;
		}
		return BASE_KEY+gid+":logs_"+index;
	}

	public static CfgDigForTreasure getCfgDigForTreasure(){
		return Cfg.I.getUniqueConfig(CfgDigForTreasure.class);
	}

	/**
	 * 随机获得一种奖励
	 *
	 * @return
	 */
	public static List<Award> randomAwardsByRandomPool() {
		CfgDigForTreasure cfgDigForTreasure = getCfgDigForTreasure();
		List<CfgDigForTreasure.RandomAward> randomAwardsPool = cfgDigForTreasure.getRandomAwardsPool();
		int seed = PowerRandom.getRandomBySeed(10000);
		int sum = 0;
		for (CfgDigForTreasure.RandomAward randomAward : randomAwardsPool) {
			sum += randomAward.getPercentage();
			if (sum >= seed) {
				return randomAward.getAwards();
			}
		}
		return randomAwardsPool.get(0).getAwards();
	}

	/**
	 * 获取子服务可购买的物品
	 *
	 * @return
	 */
	@Override
	public List<Integer> getAbleTradeGoodIds() {
		return Arrays.asList(TreasureEnum.CHANG_ZI.getValue());
	}

	/**
	 * 获取要购买的物品价格
	 *
	 * @param goodId
	 * @return
	 */
	@Override
	public int getTradeBuyPrice(int goodId) {
		return BUY_NEED_COPPER;
	}

	/**
	 * 获取某个批次中某个子服务的物品购买信息
	 *
	 * @param uid
	 * @param specialIds
	 * @return
	 */
	@Override
	public List<BuyGoodInfo> getTradeBuyInfo(long uid, List<Integer> specialIds) {
		if (!isOpened(gameUserService.getActiveSid(uid))) {
			return new ArrayList<>();
		}
		return IChengChiTradeService.super.getTradeBuyInfo(uid, specialIds);
	}

	/**
	 * 特产交易产出
	 *
	 * @param uid
	 * @return
	 */
	public List<RDTradeInfo.RDCitySpecial> specialExtraAwards(long uid) {
		List<RDTradeInfo.RDCitySpecial> citySpecialList = new ArrayList<>();
		if (!isOpened(gameUserService.getActiveSid(uid))) {
			return citySpecialList;
		}
		if (!PowerRandom.hitProbability(GET_SPECIAL_PROB)) {
			return citySpecialList;
		}
		citySpecialList.add(new RDTradeInfo.RDCitySpecial(TreasureEnum.CHANG_ZI.getValue(), 0, 0));
		return citySpecialList;
	}
}
