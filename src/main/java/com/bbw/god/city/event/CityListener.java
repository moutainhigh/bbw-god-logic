package com.bbw.god.city.event;

import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.CityProcessorFactory;
import com.bbw.god.city.ICityArriveProcessor;
import com.bbw.god.city.RDCityInfo;
import com.bbw.god.city.UserCityService;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.UserTreasureRecord;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import com.bbw.god.gameuser.yaozu.*;
import com.bbw.god.gameuser.yaozu.rd.RDArriveYaoZu;
import com.bbw.god.rd.RDAdvance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 城市事件处理
 *
 * @author suhq
 * @date 2018年11月29日 下午3:29:25
 */
@Slf4j
@Component
public class CityListener {
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private CityProcessorFactory cityProcessorFactory;
	@Autowired
	private UserYaoZuInfoService userYaoZuInfoService;
	@Autowired
	private YaoZuProcessor yaoZuProcessor;
	@Autowired
	UserCityService userCityService;
	@Autowired
	YaoZuLogic yaoZuLogic;
	@Autowired
	private UserTreasureRecordService userTreasureRecordService;
	@Autowired
	private YaoZuGenerateProcessor yaoZuGenerate;

	@EventListener
	public void arriveCity(CityArriveEvent event) {
		EventParam<Integer> ep = (EventParam<Integer>) event.getSource();
		long uid = ep.getGuId();
		//清楚宝物使用记录，醒酒毡等
		clearUserTreasureRecords(uid);
		GameUser gu = gameUserService.getGameUser(uid);
		//妖族来犯 且 该位置上有妖族
		int pos = gu.getLocation().getPosition();
		//判断是否在梦魇世界
		boolean isNightmare = gu.getStatus().intoNightmareWord();
		//判断是否通关妖族
		boolean passYaoZu = yaoZuGenerate.isPassYaoZu(uid);
		if (isNightmare && !passYaoZu) {
			UserYaoZuInfo yaoZuInfo = userYaoZuInfoService.getUserYaoZuInfoByPos(uid, pos);
			if (null != yaoZuInfo && yaoZuInfo.getProgress() != YaoZuProgressEnum.BEAT_ONTOLOGY.getType()) {
				RDAdvance rd = (RDAdvance) ep.getRd();
				ArriveYaoZuCache cache = yaoZuProcessor.arriveYaoZuProcessor(uid, yaoZuInfo);
				RDArriveYaoZu rdYaoZu = new RDArriveYaoZu();
				rdYaoZu.setMeetYaoZu(1);
				rdYaoZu.setYaoZuId(cache.getYaoZuId());
				rd.setArriveYaoZu(rdYaoZu);
				// 缓存到达位置的临时数据，每到一个位置该数据将被更新
				TimeLimitCacheUtil.setArriveCache(uid, cache);
				return;
			}
		}
		// 正常到达
		CfgRoadEntity road = RoadTool.getRoadById(ep.getValue());
		CfgCityEntity city = road.getCity();
		ICityArriveProcessor cityProcessor = cityProcessorFactory.makeArriveProcessor(gu, city);
		if (cityProcessor != null) {
			RDAdvance rd = (RDAdvance) ep.getRd();
			// log.info(city.toString());
			RDCityInfo rdCityInfo = cityProcessor.arriveProcessor(gu, city, rd);
			rdCityInfo.setArriveCityId(city.getId());
			rd.setCityInfo(rdCityInfo);
			// 缓存到达位置的临时数据，每到一个位置该数据将被更新
			TimeLimitCacheUtil.setArriveCache(uid, rd.getCityInfo());
		}
	}

	/**
	 * 清除道具使用记录（醒酒沾）
	 * @param uid
	 */
	private void clearUserTreasureRecords(long uid){
		// 清除醒酒毡记录
		List<UserTreasureRecord> recordList = userTreasureRecordService.getRecords(uid)
				.stream().filter(t -> t.getBaseId() == TreasureEnum.XJZ.getValue()).collect(Collectors.toList());
		userTreasureRecordService.delRecords(recordList);
	}
}
