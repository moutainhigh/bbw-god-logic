package com.bbw.god.activity.holiday.processor.holidaythankflowerlanguage;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 感恩花语活动
 *
 * @author: huanghb
 * @date: 2022/11/16 9:26
 */
@Service
public class ThankFlowerLanguageProcessor extends AbstractActivityProcessor {
	@Autowired
	private HolidayThankFlowerLanguageDateService holidayThankFlowerLanguageDateService;
	@Autowired
	private UserTreasureService userTreasureService;


	public ThankFlowerLanguageProcessor() {
		this.activityTypeList = Arrays.asList(ActivityEnum.THANK_FLOWER_LANGUAGE);
	}

	@Override
	public RDSuccess getActivities(long uid, int activityType) {
		RDActivityList rd = (RDActivityList) super.getActivities(uid, ActivityEnum.THANK_FLOWER_LANGUAGE.getValue());
		UserThankFlowerLanguage flowerInfo = getFlowerInfo(uid);
		rd.setRdFlowerpotInfos(RdFlowerpotInfos.instance(flowerInfo));
		return rd;
	}

	/**
	 * 获得花盆信息
	 *
	 * @param uid
	 * @return
	 */
	private UserThankFlowerLanguage getFlowerInfo(long uid) {
		UserThankFlowerLanguage flowerpotInfo = holidayThankFlowerLanguageDateService.getSingleData(uid);
		if (null != flowerpotInfo) {
			return flowerpotInfo;
		}
		flowerpotInfo = UserThankFlowerLanguage.instance(uid);
		holidayThankFlowerLanguageDateService.addData(flowerpotInfo);
		return flowerpotInfo;
	}

	/**
	 * 种植
	 *
	 * @param uid
	 * @param flowerpotId
	 * @param seedBagId
	 * @return
	 */
	public RdFlowerpotInfos plant(long uid, Integer flowerpotId, Integer seedBagId) {
		//种子包检测
		ThankFlowerLanguageTool.isSeedBagExist(seedBagId);
		//花盆id检测
		ThankFlowerLanguageTool.isValidFlowerpotId(flowerpotId);
		//花盆检测
		UserThankFlowerLanguage flowerpotInfo = getFlowerInfo(uid);
		if (null == flowerpotInfo) {
			throw new ExceptionForClientTip("activity.thankFlowerLanguage.flowerpot.is.not.exist");
		}
		//检查种子包是否足够
		TreasureChecker.checkIsEnough(seedBagId, 1, uid);
		//种植
		flowerpotInfo.plant(seedBagId, flowerpotId);

		RdFlowerpotInfos rd = RdFlowerpotInfos.instance(flowerpotInfo);
		//扣除种子包
		TreasureEventPublisher.pubTDeductEvent(uid, seedBagId, 1, WayEnum.THANK_FLOWER_LANGUAGE, rd);
		holidayThankFlowerLanguageDateService.updateData(flowerpotInfo);
		return rd;
	}

	/**
	 * 一键种植
	 *
	 * @param uid
	 * @return
	 */
	public RdFlowerpotInfos oneClickPlant(long uid) {
		//是否有种子包
		List<UserTreasure> seedBagIds = userTreasureService.getUserTreasures(uid, ThankFlowerLanguageTool.getCfg().getSeedBags());
		int seedBagNum = (int) seedBagIds.stream().map(UserTreasure::gainTotalNum).count();
		if (0 >= seedBagNum) {
			throw new ExceptionForClientTip("activity.thankFlowerLanguage.seedbag.is.not.exist");
		}
		//获得花盆信息
		UserThankFlowerLanguage flowerpotInfo = getFlowerInfo(uid);
		if (null == flowerpotInfo) {
			throw new ExceptionForClientTip("activity.thankFlowerLanguage.flowerpot.is.not.exist");
		}
		//是否有空花盆
		int emptyFlowerpotNum = flowerpotInfo.gainEmptyFlowerpotNum();
		if (0 == emptyFlowerpotNum) {
			throw new ExceptionForClientTip("activity.thankFlowerLanguage.flowerpot.is.not.empty");
		}
		List<Award> needSeedBags = new ArrayList<>();
		//获得高级种子包数量
		int seniorSeedBagNum = userTreasureService.getTreasureNum(uid, TreasureEnum.ADVANCED_SEED_PACKETS.getValue());
		//需要高级种子包数量
		int needSeniorSeedBagNum = seniorSeedBagNum - emptyFlowerpotNum > 0 ? emptyFlowerpotNum : seniorSeedBagNum;
		if (needSeniorSeedBagNum > 0) {
			emptyFlowerpotNum -= needSeniorSeedBagNum;
			needSeedBags.add(new Award(TreasureEnum.ADVANCED_SEED_PACKETS.getValue(), AwardEnum.FB, needSeniorSeedBagNum));
		}
		//需要中级级种子包数量
		int needIntermediateSeedBagNum = 0;
		if (emptyFlowerpotNum > needSeniorSeedBagNum) {
			emptyFlowerpotNum -= needSeniorSeedBagNum;
			//获得中级级种子包数量
			int intermediateSeedBagNum = userTreasureService.getTreasureNum(uid, TreasureEnum.INTERMEDIATE_SEED_PACKET.getValue());
			//需要中级级种子包数量
			needIntermediateSeedBagNum = intermediateSeedBagNum - emptyFlowerpotNum > 0 ? emptyFlowerpotNum : intermediateSeedBagNum;
			if (needIntermediateSeedBagNum > 0) {
				emptyFlowerpotNum -= needIntermediateSeedBagNum;
				needSeedBags.add(new Award(TreasureEnum.INTERMEDIATE_SEED_PACKET.getValue(), AwardEnum.FB, needIntermediateSeedBagNum));
			}
		}
		//需要初级种子包数量
		if (emptyFlowerpotNum > needIntermediateSeedBagNum) {
			emptyFlowerpotNum -= needIntermediateSeedBagNum;
			//获得初级级种子包数量
			int primarySeedBagNum = userTreasureService.getTreasureNum(uid, TreasureEnum.BEGINNER_SEED_PACKETS.getValue());
			//需要初级级种子包数量
			int needPrimarySeedBagNum = primarySeedBagNum - emptyFlowerpotNum > 0 ? emptyFlowerpotNum : primarySeedBagNum;
			if (needPrimarySeedBagNum > 0) {
				emptyFlowerpotNum -= needPrimarySeedBagNum;
				needSeedBags.add(new Award(TreasureEnum.BEGINNER_SEED_PACKETS.getValue(), AwardEnum.FB, needPrimarySeedBagNum));
			}
		}
		//一键种植
		flowerpotInfo.oneClickPlant(needSeedBags);
		RdFlowerpotInfos rd = RdFlowerpotInfos.instance(flowerpotInfo);
		//扣除种子包
		for (Award award : needSeedBags) {
			TreasureEventPublisher.pubTDeductEvent(uid, award.getAwardId(), award.getNum(), WayEnum.THANK_FLOWER_LANGUAGE, rd);
		}
		holidayThankFlowerLanguageDateService.updateData(flowerpotInfo);
		return rd;
	}

	/**
	 * 施肥
	 *
	 * @param uid
	 * @param flowerpotId  花盆id
	 * @param fertilizerId 肥料id
	 * @return
	 */
	public RdFlowerpotInfos applyFertilizer(long uid, Integer flowerpotId, Integer fertilizerId) {
		//肥料包检测
		ThankFlowerLanguageTool.isFertilizerExist(fertilizerId);
		//花盆id检测
		ThankFlowerLanguageTool.isValidFlowerpotId(flowerpotId);
		//花盆检测
		UserThankFlowerLanguage flowerpotInfo = getFlowerInfo(uid);
		if (null == flowerpotInfo) {
			throw new ExceptionForClientTip("activity.thankFlowerLanguage.flowerpot.is.not.exist");
		}
		//是否是种子状态
		if (!flowerpotInfo.ifSeed(flowerpotId)) {
			throw new ExceptionForClientTip("activity.thankFlowerLanguage.flowerpot.is.not.seed");
		}
		//检查肥料包是否足够
		TreasureChecker.checkIsEnough(fertilizerId, 1, uid);
		//施肥
		flowerpotInfo.applyFertilizer(flowerpotId, fertilizerId);

		RdFlowerpotInfos rd = RdFlowerpotInfos.instance(flowerpotInfo);
		//扣除肥料包
		TreasureEventPublisher.pubTDeductEvent(uid, fertilizerId, 1, WayEnum.THANK_FLOWER_LANGUAGE, rd);
		holidayThankFlowerLanguageDateService.updateData(flowerpotInfo);
		return rd;
	}

	/**
	 * 一键施肥
	 *
	 * @param uid
	 * @return
	 */
	public RdFlowerpotInfos oneClickApplyFertilizer(long uid) {
		//是否有肥料包
		List<UserTreasure> fertilizerBagIds = userTreasureService.getUserTreasures(uid, ThankFlowerLanguageTool.getCfg().getFertilizers());
		int fertilizerBagNum = (int) fertilizerBagIds.stream().map(UserTreasure::gainTotalNum).count();
		if (0 >= fertilizerBagNum) {
			throw new ExceptionForClientTip("activity.thankFlowerLanguage.fertilizer.is.not.exist");
		}
		//获得花盆信息
		UserThankFlowerLanguage flowerpotInfo = getFlowerInfo(uid);
		if (null == flowerpotInfo) {
			throw new ExceptionForClientTip("activity.thankFlowerLanguage.flowerpot.is.not.exist");
		}

		//是否有空有种子花盆
		int seedFlowerpotNum = flowerpotInfo.gainSeedFlowerpotNum();
		if (0 == seedFlowerpotNum) {
			throw new ExceptionForClientTip("activity.thankFlowerLanguage.flowerpot.is.not.seed");
		}
		List<Award> needFertilizerBags = new ArrayList<>();
		//获得高级肥料包数量
		int seniorFertilizerBagNum = userTreasureService.getTreasureNum(uid, TreasureEnum.ADVANCED_FERTILIZER_PACKET.getValue());
		//需要高级肥料包数量
		int needSeniorFertilizerBagNum = seniorFertilizerBagNum - seedFlowerpotNum > 0 ? seedFlowerpotNum : seniorFertilizerBagNum;
		if (needSeniorFertilizerBagNum > 0) {
			seedFlowerpotNum -= needSeniorFertilizerBagNum;
			needFertilizerBags.add(new Award(TreasureEnum.ADVANCED_FERTILIZER_PACKET.getValue(), AwardEnum.FB, needSeniorFertilizerBagNum));
		}
		//需要中级肥料包数量
		int needIntermediateFertilizerBagNum = 0;
		if (seedFlowerpotNum > needSeniorFertilizerBagNum) {
			seedFlowerpotNum -= needSeniorFertilizerBagNum;
			//获得中级肥料包数量
			int intermediateFertilizerBagNum = userTreasureService.getTreasureNum(uid, TreasureEnum.INTERMEDIATE_FERTILIZER_PACKET.getValue());
			//需要中级肥料包数量
			needIntermediateFertilizerBagNum = intermediateFertilizerBagNum - seedFlowerpotNum > 0 ? seedFlowerpotNum : intermediateFertilizerBagNum;
			if (needIntermediateFertilizerBagNum > 0) {
				seedFlowerpotNum -= needIntermediateFertilizerBagNum;
				needFertilizerBags.add(new Award(TreasureEnum.INTERMEDIATE_FERTILIZER_PACKET.getValue(), AwardEnum.FB, needIntermediateFertilizerBagNum));
			}
		}
		//需要初级肥料包数量
		if (seedFlowerpotNum > needIntermediateFertilizerBagNum) {
			seedFlowerpotNum -= needIntermediateFertilizerBagNum;
			//获得初级级肥料包数量
			int primarySeedBagNum = userTreasureService.getTreasureNum(uid, TreasureEnum.BEGINNER_FERTILIZER_PACKET.getValue());
			//需要初级级肥料包数量
			int needPrimarySeedBagNum = primarySeedBagNum - seedFlowerpotNum > 0 ? seedFlowerpotNum : primarySeedBagNum;
			if (needPrimarySeedBagNum > 0) {
				seedFlowerpotNum -= needPrimarySeedBagNum;
				needFertilizerBags.add(new Award(TreasureEnum.BEGINNER_FERTILIZER_PACKET.getValue(), AwardEnum.FB, needPrimarySeedBagNum));
			}
		}
		//一键施肥
		flowerpotInfo.oneClickFertilizer(needFertilizerBags);
		RdFlowerpotInfos rd = RdFlowerpotInfos.instance(flowerpotInfo);
		//扣除种子包
		for (Award award : needFertilizerBags) {
			TreasureEventPublisher.pubTDeductEvent(uid, award.getAwardId(), award.getNum(), WayEnum.THANK_FLOWER_LANGUAGE, rd);
		}
		holidayThankFlowerLanguageDateService.updateData(flowerpotInfo);
		return rd;
	}

	/**
	 * 采摘
	 *
	 * @param uid
	 * @param flowerpotId
	 * @return
	 */
	public RdFlowerpotInfos pick(long uid, Integer flowerpotId) {
		//花盆id检测
		ThankFlowerLanguageTool.isValidFlowerpotId(flowerpotId);
		//花盆检测
		UserThankFlowerLanguage flowerpotInfo = getFlowerInfo(uid);
		if (null == flowerpotInfo) {
			throw new ExceptionForClientTip("activity.thankFlowerLanguage.flowerpot.is.not.exist");
		}
		boolean isFlower = flowerpotInfo.ifFlower(flowerpotId);
		if (!isFlower) {
			throw new ExceptionForClientTip("activity.thankFlowerLanguage.flowerpot.is.not.flower");
		}
		//摘花
		Award flower = flowerpotInfo.pick(flowerpotId);
		RdFlowerpotInfos rd = RdFlowerpotInfos.instance(flowerpotInfo);
		TreasureEventPublisher.pubTAddEvent(uid, flower.getAwardId(), flower.getNum(), WayEnum.THANK_FLOWER_LANGUAGE, rd);
		holidayThankFlowerLanguageDateService.updateData(flowerpotInfo);
		return rd;
	}

	/**
	 * 一键采摘
	 *
	 * @param uid
	 * @return
	 */
	public RdFlowerpotInfos oneClickPick(long uid) {
		//花盆检测
		UserThankFlowerLanguage flowerpotInfo = getFlowerInfo(uid);
		if (null == flowerpotInfo) {
			throw new ExceptionForClientTip("activity.thankFlowerLanguage.flowerpot.is.not.exist");
		}
		//是否有成花
		boolean isHasFlower = flowerpotInfo.ifHasFlower();
		if (!isHasFlower) {
			throw new ExceptionForClientTip("activity.thankFlowerLanguage.flowerpot.is.not.flower");
		}
		//摘花
		List<Award> flowers = flowerpotInfo.oneClickPick();
		RdFlowerpotInfos rd = RdFlowerpotInfos.instance(flowerpotInfo);
		awardService.sendNeedMergedAwards(uid, flowers, WayEnum.THANK_FLOWER_LANGUAGE, "", rd);
		holidayThankFlowerLanguageDateService.updateData(flowerpotInfo);
		return rd;
	}

	/**
	 * 制作花束
	 *
	 * @param uid
	 * @param folwerInfos 花信息
	 * @param flowerNum   花数量 非100即0
	 * @return
	 */
	public RDCommon makeBouquet(long uid, String folwerInfos, Integer flowerNum) {
		//参数处理
		List<Integer> folwers = ListUtil.parseStrToInts(folwerInfos);
		if (ListUtil.isEmpty(folwers)) {
			throw new ExceptionForClientTip("activity.thankFlowerLanguage.flowerpot.is.num.error");
		}
		if (ThankFlowerLanguageTool.getCfg().getFlowerpotNum() != folwers.size()) {
			throw new ExceptionForClientTip("activity.thankFlowerLanguage.flowerpot.is.num.error");
		}
		// 花朵数量参数检查和处理*/
		int validFlowerNum = checkAndHandleFlowerNumParam(flowerNum);
		//背包花数量检测和处理
		Map<Integer, Integer> flowerInfos = bagFlowerNumCheckAndHandle(uid, folwers, flowerNum);
		Integer bouquetId = ThankFlowerLanguageTool.getBouquet(flowerInfos);
		RDCommon rd = new RDCommon();
		for (Map.Entry<Integer, Integer> folwer : flowerInfos.entrySet()) {
			TreasureEventPublisher.pubTDeductEvent(uid, folwer.getKey(), folwer.getValue(), WayEnum.THANK_FLOWER_LANGUAGE, rd);
		}
		TreasureEventPublisher.pubTAddEvent(uid, bouquetId, validFlowerNum / 10, WayEnum.THANK_FLOWER_LANGUAGE, rd);
		return rd;
	}

	/**
	 * 花朵数量参数检查和处理
	 *
	 * @param flowerNum
	 * @return
	 */
	private int checkAndHandleFlowerNumParam(int flowerNum) {
		return flowerNum == 100 ? flowerNum : 10;
	}

	/**
	 * 背包花朵数量检测
	 *
	 * @param uid
	 * @param flowers
	 * @param flowerNum
	 */
	private Map<Integer, Integer> bagFlowerNumCheckAndHandle(long uid, List<Integer> flowers, int flowerNum) {
		Map<Integer, Integer> map = new HashMap<>();
		List<Integer> cfgFlowers = ThankFlowerLanguageTool.getFlowers();
		for (Integer flower : flowers) {
			if (!cfgFlowers.contains(flower)) {
				throw new ExceptionForClientTip("activity.thankFlowerLanguage.flowerpot.flower.is.error");
			}
			int needFlowerNum = flowerNum;
			boolean isHasSameFlower = map.containsKey(flower);
			if (isHasSameFlower) {
				needFlowerNum += map.getOrDefault(flower,0);
			}
			TreasureChecker.checkIsEnough(flower, needFlowerNum, uid);
			map.put(flower, needFlowerNum);
		}
		return map;
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
	 * /**
	 * 是否在活动期间
	 *
	 * @param sid
	 * @return
	 */
	public boolean isOpened(int sid) {
		ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.THANK_FLOWER_LANGUAGE.getValue());
		IActivity a = this.activityService.getActivity(sid, activityEnum);
		return a != null;
	}

	@Override
	protected long getRemainTime(long uid, int sid, IActivity a) {
		if (null != a.gainEnd()) {
			return a.gainEnd().getTime() - System.currentTimeMillis();
		}
		return NO_TIME;
	}
}
