package com.bbw.god.city.yed;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.rd.RDAdvance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 土豪事件处理器
 * @date 2020/6/1 11:03
 **/
@Service
public class TuHaoProcessor extends ExtraYeDEventProcessor implements YeDEventExtraOperation {

	/**
	 * 获取当前野地事件id
	 */
	@Override
	public int getMyId() {
		return YdEventEnum.TU_HAO.getValue();
	}

	/**
	 * 野地事件生效
	 *
	 * @param gameUser
	 * @param rdArriveYeD
	 * @param rd
	 */
	@Override
	public void effect(GameUser gameUser, RDArriveYeD rdArriveYeD, RDAdvance rd) {
		List<CfgSpecialEntity> specialEntities = SpecialTool.getSpecials().stream().filter(tmp -> tmp.getType() <= 30 && !tmp.isUpdateSpecial()).collect(Collectors.toList());
		CfgSpecialEntity specialEntity = PowerRandom.getRandomFromList(specialEntities);
		Map<String, Object> map = dealTuHao(gameUser, specialEntity, rdArriveYeD, rd);
		long addedCopper = (long) map.get("addedCopper");
		List<Integer> specialIds = (List<Integer>) map.get("specialIds");
		if (0 == addedCopper) {
			rdArriveYeD.setCanRecommendSpecial(true);
		}
		BaseEventParam bep = new BaseEventParam(gameUser.getId(), WayEnum.YD, rd);
		CityEventPublisher.pubYeDTrigger(bep, EPYeDTrigger.fromIncome(YdEventEnum.TU_HAO, addedCopper, specialIds));
	}

	/**
	 * 额外操作（如：推荐特产，反抗逃跑等）
	 */
	@Override
	public void extraOperation(GameUser gu, Integer num, RDArriveYeD arriveYeD, RDAdvance rd) {
		if (!arriveYeD.getCanRecommendSpecial()) {
			throw new ExceptionForClientTip("yeD.can.not.recommend.special");
		}
		long uid = gu.getId();
		List<UserSpecial> specialList = userSpecialService.getSpecials(uid);
		arriveYeD.setCanRecommendSpecial(false);
		// 老虎机三个数字都不一样
		if (num <= 1) {
			throw new ExceptionForClientTip("yeD.tuhao.fail");
		}
		if (3 == num) {
			// 先2倍卖高级/顶级特产
			CfgSpecialEntity highOrTopSpecial = getRandomUserHighOrTopSpecial(specialList);
			if (null != highOrTopSpecial) {
				dealTuHao(gu, highOrTopSpecial, arriveYeD, rd);
				return;
			}
			// 没有高级/顶级特产的话，4倍卖普通特产
			CfgSpecialEntity normalSpecial = getRandomUserNormalSpecial(specialList);
			if (null != normalSpecial) {
				dealTuHao(gu, normalSpecial, arriveYeD, rd);
				return;
			}
		}
		// 老虎机数字2个一样
		if (2 == num) {
			CfgSpecialEntity normalSpecial = getRandomUserNormalSpecial(specialList);
			if (null != normalSpecial) {
				dealTuHao(gu, normalSpecial, arriveYeD, rd);
				return;
			}
		}
		// 数字对了没有特产
		List<CfgSpecialEntity> specialEntities = SpecialTool.getSpecials().stream().filter(tmp -> tmp.getType() <= 30).collect(Collectors.toList());
		CfgSpecialEntity specialEntity = PowerRandom.getRandomFromList(specialEntities);
		dealTuHao(gu, specialEntity, arriveYeD, rd);
		if (0 == arriveYeD.getAddedCopper()) {
			arriveYeD.setAddedCopper(null);
		}
	}

	private Map<String, Object> dealTuHao(GameUser gameUser, CfgSpecialEntity specialEntity,
										  RDArriveYeD rdArriveYeD, RDAdvance rd) {
		int priceRate = specialEntity.getId() % 100 <= 10 ? 4 : 2;
		int price = specialEntity.getPrice() * priceRate;
		long addedCopper = 0;
		List<UserSpecial> uSpecials = userSpecialService.getOwnUnLockSpecialsByBaseId(gameUser.getId(),
				specialEntity.getId());
		addedCopper = price * uSpecials.size();
		int addedWeekCopper = 0;
		List<Long> usIds = new ArrayList<Long>();
		List<EPSpecialDeduct.SpecialInfo> specialInfoList = new ArrayList<>();
		for (UserSpecial uSpecial : uSpecials) {
			usIds.add(uSpecial.getId());
			int boughtPrice = specialEntity.getPrice() * uSpecial.getDiscount() / 10;
            addedCopper += price;
			addedWeekCopper += (price - boughtPrice);
			EPSpecialDeduct.SpecialInfo info = EPSpecialDeduct.SpecialInfo.getInstance(uSpecial.getId(),
					uSpecial.getBaseId(), boughtPrice, price);
			specialInfoList.add(info);
		}
		rdArriveYeD.setSupSpecialId(specialEntity.getId());
		rdArriveYeD.setReduceSpecial(usIds);
		rdArriveYeD.setPriceRate(priceRate);
		rdArriveYeD.setSellCount(uSpecials.size());
		rdArriveYeD.setAddedCopper(addedCopper);
		if (uSpecials.size() < 0) {
			return new HashMap<>();
		}
		Map<String, Object> map = new HashMap<>();
		BaseEventParam bep = new BaseEventParam(gameUser.getId(), WayEnum.YD, rd);
		EPSpecialDeduct epSpecialDeduct = EPSpecialDeduct.instance(bep, gameUser.getLocation().getPosition(),
				specialInfoList);
		List<Integer> specialIds = specialInfoList.stream()
				.map(EPSpecialDeduct.SpecialInfo::getBaseSpecialIds).collect(Collectors.toList());
		SpecialEventPublisher.pubSpecialDeductEvent(epSpecialDeduct);
		EPCopperAdd copperInfo = new EPCopperAdd(bep, addedCopper, addedWeekCopper);
		ResEventPublisher.pubCopperAddEvent(copperInfo);
		map.put("addedCopper", addedCopper);
		map.put("specialIds", specialIds);
		return map;
	}

	/**
	 * 随机获取一种玩家高级特产
	 *
	 * @param specialList 玩家已有特产集合
	 * @return 高级特产
	 */
	private CfgSpecialEntity getRandomUserHighOrTopSpecial(List<UserSpecial> specialList) {
		List<CfgSpecialEntity> highOrTopSpecialList = new ArrayList<>();
		for (UserSpecial userSpecial : specialList) {
			Integer specialId = userSpecial.getBaseId();
			CfgSpecialEntity special = SpecialTool.getSpecialById(specialId);
			if (special.isHighSpecial() || special.isTopSpecial()) {
				highOrTopSpecialList.add(special);
			}
		}
		if (ListUtil.isEmpty(highOrTopSpecialList)) {
			return null;
		}
		return PowerRandom.getRandomFromList(highOrTopSpecialList);
	}

	/**
	 * 随机获取一种玩家普通特产
	 *
	 * @param specialList 玩家已有特产集合
	 * @return 普通特产
	 */
	private CfgSpecialEntity getRandomUserNormalSpecial(List<UserSpecial> specialList) {
		List<CfgSpecialEntity> normalSpecialList = new ArrayList<>();
		for (UserSpecial userSpecial : specialList) {
			Integer specialId = userSpecial.getBaseId();
			CfgSpecialEntity special = SpecialTool.getSpecialById(specialId);
			if (special.isNormalSpecial()) {
				normalSpecialList.add(special);
			}
		}
		if (ListUtil.isEmpty(normalSpecialList)) {
			return null;
		}
		return PowerRandom.getRandomFromList(normalSpecialList);
	}
}
