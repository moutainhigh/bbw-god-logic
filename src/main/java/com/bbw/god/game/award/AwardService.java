package com.bbw.god.game.award;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.bbw.common.PowerRandom;
import com.bbw.common.StrUtil;
import com.bbw.exception.CoderException;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardRandomService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.special.event.EVSpecialAdd;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.gameuser.task.daily.event.DailyTaskAddPoint;
import com.bbw.god.gameuser.task.daily.event.DailyTaskEventPublisher;
import com.bbw.god.gameuser.task.godtraining.event.GodTrainingTaskEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 奖励的服务(该类的子类不能使用自动注入，需要在GodBeanConfig类中装配!!!!!)
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-02-21 16:19
 */
public abstract class AwardService<T extends Award> {
	public static final String GU_ASSIGN_AWARD = "-";
	@Autowired
	private UserCardService userCardService;
	@Autowired
	private UserCardRandomService cardRandomService;

	private static final List<Integer> WNLS=Arrays.asList(810,820,830,840,850);
	/**
	 * 领取奖励
	 *
	 * @param uid:区服玩家ID
	 * @param awardJson:奖励的json字符串。参考 doc/邮件规则.json 文件描述
	 * @param way:获取途径
	 * @param rd:返回对象
	 */
	public abstract void fetchAward(long uid, String awardJson, WayEnum way, String broadcastWayInfo, RDCommon rd);

	/**
	 * 发放需要合并奖励（特产不合并）
	 *
	 * @param uid
	 * @param awards
	 * @param way
	 * @param broadcastWayInfo
	 * @param rd
	 */
	public void sendNeedMergedAwards(long uid, List<T> awards, WayEnum way, String broadcastWayInfo, RDCommon rd){
		List<Award> awardList = new ArrayList<>();
		Map<Integer, List<Award>> awardsByItem = awards.stream().collect(Collectors.groupingBy(Award::getItem));
		for (Map.Entry<Integer, List<Award>> awardByItemList : awardsByItem.entrySet()){
			AwardEnum awardEnum = AwardEnum.fromValue(awardByItemList.getKey());
			if (null == awardEnum) {
				continue;
			}
			if (awardEnum == AwardEnum.TC || awardEnum == AwardEnum.KP) {
				awardList.addAll(awardByItemList.getValue());
				continue;
			}
			Map<Integer, Integer> boxAwards = awardByItemList.getValue().stream()
					.collect(Collectors.groupingBy(Award::getAwardId, Collectors.summingInt(Award::getNum)));
			awardList.addAll(Award.getAwards(boxAwards, awardEnum.getValue()));
		}
		if (awardList.isEmpty()){
			return;
		}
		fetchAward(uid, (List<T>) awardList, way, broadcastWayInfo, rd);
	}

	public void fetchAward(long uid, List<T> awards, WayEnum way, String broadcastWayInfo, RDCommon rd){
		if (null == awards || awards.isEmpty()) {
			throw new ExceptionForClientTip("award.service.no.awards");
		}
		for (T award : awards) {
			// YB("元宝",10), TQ("铜钱",20), XDD("行动点",30), KP("卡牌", 40),
			// YS("元素",50),FB("法宝",60);	HY("每日任务活跃度", 100);
			AwardEnum awardType = AwardEnum.fromValue(award.getItem());
			switch (awardType) {
				case ZS:
					ResEventPublisher.pubDiamondAddEvent(uid, award.getNum(), way, rd);
					break;
				case YB:
					ResEventPublisher.pubGoldAddEvent(uid, award.getNum(), way, rd);
					break;
				case TQ:
					deliverCopper(uid, award, way, rd);
					break;
				case TL:
					ResEventPublisher.pubDiceAddEvent(uid, award.getNum(), way, rd);
					break;
				case KP:
					deliverCard(uid, award, broadcastWayInfo, way, rd);
					break;
				case YS:
					deliverEle(uid, award, way, rd);
					break;
				case FB:
					deliverTreasure(uid, award, way, rd);
					break;
				case TC:
					deliverSpecial(uid, award, way, rd);
					break;
				case JY:
					ResEventPublisher.pubExpAddEvent(uid, award.getNum(), way, rd);
					break;
				case HY:
					DailyTaskAddPoint dta = DailyTaskAddPoint.instance(new BaseEventParam(uid, WayEnum.DAILY_TASK, rd), award.getNum());
					DailyTaskEventPublisher.pubDailyTaskAddPotintEvent(dta);
					break;
				case DFZ:
					DailyTaskAddPoint dfzDta = DailyTaskAddPoint
							.instance(new BaseEventParam(uid, WayEnum.HERO_BACK_DAILY_TASK, rd), award.getNum());
					DailyTaskEventPublisher.pubDailyTaskAddPotintEvent(dfzDta);
					break;
				case SLZ:
					GodTrainingTaskEventPublisher.pubAddGodTrainingPointEvent(award.getNum(), new BaseEventParam(uid, WayEnum.OPEN_GOD_TRAINING_TASK, rd));
					break;
				case WNLS:
					deliverWNLS(uid, award, way, rd);
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 将奖励json解析为对象
	 *
	 * @param awardJson
	 * @return
	 */
	public List<T> parseAwardJson(String awardJson, Class<T> clazz){
		boolean isValid = StrUtil.isNotBlank(awardJson) && !GU_ASSIGN_AWARD.equals(awardJson);
		if (!isValid) {
			return null;
		}
		List<T> awards = null;
		try {
			awards = JSON.parseArray(awardJson, clazz);
		} catch (Exception e) {
			throw CoderException.high("无法解析资源字符串！" + awardJson);
		}
		return awards;
	}
	/**
	 * 发放铜钱
	 *
	 * @param uid
	 * @param award
	 * @param way
	 * @param rd
	 */
	protected void deliverCopper(long uid, T award, WayEnum way, RDCommon rd) {
		if (award.gainIsFhb() == 1) {
			ResEventPublisher.pubCopperAddEvent(uid, award.getNum(), way, rd);
		} else {
			BaseEventParam bep = new BaseEventParam(uid, way, rd);
			EPCopperAdd evCopperAdd = new EPCopperAdd(bep, award.getNum());
			ResEventPublisher.pubCopperAddEvent(evCopperAdd);
		}
	}

	/**
	 * 发放卡牌
	 *
	 * @param uid
	 * @param award
	 * @param way
	 * @param rd
	 */
	protected void deliverCard(long uid, T award, String broadcastWayInfo, WayEnum way, RDCommon rd) {
		String strategy = award.getStrategy();
		// 如果使用策略，则忽略所有的卡牌参数（其他参数是客户端显示要用的）
		if (StrUtil.isNotBlank(strategy)) {
			List<CfgCardEntity> cards = new ArrayList<>();
			// if (award.getIsNotOwn() > 0) {
			RandomParam randomParams = new RandomParam();
			List<UserCard> ownCards = userCardService.getUserCards(uid);
			;
			randomParams.setRoleCards(ownCards);
			cards = cardRandomService.getRandomList(uid, strategy, randomParams);
			// } else {
			// cards = cardRandomService.getRandomList(uid, strategy, 0);
			// }
			for (CfgCardEntity card : cards) {
				CardEventPublisher.pubCardAddEvent(uid, card.getId(), way, broadcastWayInfo, rd);
			}
			return;
		}
		// 没有使用策略则使用原有的发放方式
		int cardNum = award.getNum();
		for (int i = 0; i < cardNum; i++) {
			CfgCardEntity card = null;
			// 指定卡牌
			if (award.gainAwardId() > 0) {
				card = CardTool.getCardById(award.gainAwardId());
				CardEventPublisher.pubCardAddEvent(uid, card.getId(), way, broadcastWayInfo, rd);
				continue;
			}
			// 没有指定卡牌
			List<Integer> cardWays = null;
			if (award.gainIsSpecial() > 0) {
				cardWays = Arrays.asList(0, 1, 2);
			} else {
				cardWays = Arrays.asList(0);
			}
			if (award.gainIsNotOwn() > 0) {
				// 未拥有的卡牌
				card = userCardService.getNotOwnCard(uid, award.gainStar(), cardWays);
			} else {
				// 随机卡牌
				card = CardTool.getRandomCard(award.gainStar(), cardWays);
			}
			CardEventPublisher.pubCardAddEvent(uid, card.getId(), way, broadcastWayInfo, rd);

		}
	}

	/**
	 * 发放法宝
	 *
	 * @param uid
	 * @param award
	 * @param way
	 * @param rd
	 */
	protected void deliverTreasure(long uid, T award, WayEnum way, RDCommon rd) {
		int  realId = award.gainAwardId();
		if(0 == realId){
			// 判断是否为迷仙洞 熔炼 奖励
			if (way == WayEnum.MXD_FURNACE) {
				// 查询对应 星级 所有法宝
				List<CfgTreasureEntity> allTe = TreasureTool.getTreasureConfig().getOldTreasureMapByStar().get(award.getStar());
				// 过滤玉麒麟
				List<CfgTreasureEntity> noYqlArr = allTe.stream().filter(treasureEnt ->
						!treasureEnt.getName().equals(TreasureEnum.YQL.getName())).collect(Collectors.toList());
				// 随机一个不为玉麒麟的法宝
				realId = noYqlArr.get(PowerRandom.randomInt(noYqlArr.size())).getId();
			}else {
				// 其他奖励
				realId = TreasureTool.getRandomOldTreasure(award.gainStar()).getId();
			}
		}
		TreasureEventPublisher.pubTAddEvent(uid, realId, award.getNum(), way, rd);
	}

	/**
	 * 发放随机万能灵石
	 *
	 * @param uid
	 * @param award
	 * @param way
	 * @param rd
	 */
	protected void deliverWNLS(long uid, T award, WayEnum way, RDCommon rd) {
		int id = PowerRandom.getRandomFromList(WNLS);
		TreasureEventPublisher.pubTAddEvent(uid, id, award.getNum(), way, rd);
	}

	/**
	 * 发放元素
	 *
	 * @param uid
	 * @param award
	 * @param way
	 * @param rd
	 */
	protected void deliverEle(long uid, T award, WayEnum way, RDCommon rd) {
		int propId = award.gainAwardId();
		// 本次有获得卡牌，优先提供跟卡牌相关的元素
		if (propId == 0) {
			propId = PowerRandom.getRandomBetween(1, 5) * 10;
		}
		ResEventPublisher.pubEleAddEvent(uid, propId, award.getNum(), way, rd);
	}

	/**
	 * 发放特产
	 *
	 * @param uid
	 * @param award
	 * @param way
	 * @param rd
	 */
	protected void deliverSpecial(long uid, T award, WayEnum way, RDCommon rd) {
		ArrayList<EVSpecialAdd> specials = new ArrayList<>();
		for (int j = 0; j < award.getNum(); j++) {
			specials.add(EVSpecialAdd.given(award.gainAwardId()));
		}
		SpecialEventPublisher.pubSpecialAddEvent(uid, specials, way, rd);
	}
}