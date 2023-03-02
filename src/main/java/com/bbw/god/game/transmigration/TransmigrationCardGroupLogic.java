package com.bbw.god.game.transmigration;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.game.combat.data.param.CPCardGroup;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgDeifyCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import com.bbw.god.game.transmigration.entity.TransmigrationDefender;
import com.bbw.god.game.transmigration.entity.UserTransmigrationCity;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.*;
import com.bbw.god.gameuser.leadercard.LeaderCardTool;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.rd.RDSuccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 轮回世界编组逻辑
 *
 * @author: suhq
 * @date: 2021/10/18 11:46 上午
 */
@Slf4j
@Service
public class TransmigrationCardGroupLogic {
	@Autowired
	private GameTransmigrationService gameTransmigrationService;
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private UserTransmigrationCityService userTransmigrationCityService;
	@Autowired
	private UserCardService userCardService;
	@Autowired
	private LeaderCardService leaderCardService;

	/**
	 * 获取当前所在城池的攻城卡组
	 *
	 * @param uid
	 * @return
	 */
	public RDCardGroups getAttackCardGroup(long uid) {
		Integer fuCeId = 0;
		List<Integer> cardIds = new ArrayList<>();
		boolean present = leaderCardService.getUserLeaderCardOp(uid).isPresent();
		if (!present) {
			cardIds.add(LeaderCardTool.getLeaderCardId());
		}
		ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
		int cityId = cache.getCityId();
		UserTransmigrationCity transmigrationCity = userTransmigrationCityService.getTransmigrationCity(uid, cityId);
		if (null != transmigrationCity) {
			updateFengShenCardId(uid, transmigrationCity);
			cardIds.addAll(transmigrationCity.getCardGroup());
			fuCeId = transmigrationCity.getFuCe();
		}
		RDCardGroups rd = new RDCardGroups();
		rd.addCardIds(CardGroupWay.TRANSMIGRATION, cardIds, fuCeId);
		return rd;
	}


	/**
	 * 设置卡组
	 *
	 * @param uid
	 * @param cardIds
	 */
	public RDSuccess setAttackCardGroup(long uid, String cardIds) {
		List<Integer> cards = CardParamParser.parseGroupParam(cardIds);
		if (ListUtil.isEmpty(cards)) {
			//不允许保存空数组
			throw new ExceptionForClientTip("card.grouping.not.blank");
		}
		ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
		int cityId = cache.getCityId();
		GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(gameUserService.getActiveGid(uid));
		gameTransmigrationService.checkTransmigration(curTransmigration);
		Integer defenderType = curTransmigration.gainDefenderType(cityId);
		if (!CardChecker.isSameType(defenderType, cards)) {
			throw new ExceptionForClientTip("card.grouping.not.the.same.type");
		}
		UserTransmigrationCity transmigrationCity = userTransmigrationCityService.getTransmigrationCity(uid, cityId);
		if (null == transmigrationCity) {
			CfgCityEntity city = CityTool.getCityById(cityId);
			transmigrationCity = UserTransmigrationCity.getInstance(city, uid);
			gameUserService.addItem(uid, transmigrationCity);
		}
		transmigrationCity.setCardGroup(cards);
		gameUserService.updateItem(transmigrationCity);
		return new RDSuccess();
	}

	/**
	 * 设置战斗符册
	 *
	 * @param uid
	 * @param fuCeId
	 */
	public void setAttackFuCe(long uid, Integer fuCeId) {
		ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
		int cityId = cache.getCityId();
		GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(gameUserService.getActiveGid(uid));
		gameTransmigrationService.checkTransmigration(curTransmigration);
		UserTransmigrationCity transmigrationCity = userTransmigrationCityService.getTransmigrationCity(uid, cityId);
		if (null == transmigrationCity) {
			CfgCityEntity city = CityTool.getCityById(cityId);
			transmigrationCity = UserTransmigrationCity.getInstance(city, uid);
			gameUserService.addItem(uid, transmigrationCity);
		}
		transmigrationCity.setFuCe(fuCeId);
		gameUserService.updateItem(transmigrationCity);
	}

	/**
	 * 同步卡组:同步规则 优先同步 同属性 高级城 胜利的卡组
	 *
	 * @param uid
	 * @return
	 */
	public RDCardGroups synAttackCardGroup(long uid) {

		GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(gameUserService.getActiveGid(uid));
		gameTransmigrationService.checkTransmigration(curTransmigration);
		ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
		int cityId = cache.getCityId();
		int defenderType = curTransmigration.gainDefenderType(cityId);
		Map<Integer, List<Integer>> cityGroupAsLevel = getCityGroupAsLevel(curTransmigration, defenderType);
		List<UserTransmigrationCity> transmigrationCities = userTransmigrationCityService.getTransmigrationCities(uid);
		//优先获取拥有的城池
		UserTransmigrationCity targetTransmigration = null;
		boolean mustOwn = transmigrationCities.stream().filter(p -> p.isOwn() && curTransmigration.gainDefenderType(p.getBaseId()) == defenderType).findFirst().isPresent();
		//同步卡组阵容时，优先同步 同属性 高级城 胜利的卡组
		if (ListUtil.isNotEmpty(transmigrationCities)) {
			for (int i = 5; i > 0; i--) {
				List<Integer> list = cityGroupAsLevel.get(i);
				if (null == list) {
					continue;
				}
				List<UserTransmigrationCity> collect = transmigrationCities.stream().filter(p -> (p.isOwn() || !mustOwn) && list.contains(p.getBaseId())).collect(Collectors.toList());
				if (ListUtil.isNotEmpty(collect)) {
					targetTransmigration = PowerRandom.getRandomFromList(collect);
					break;
				}
			}
		}
		if (targetTransmigration == null) {
			//没有可以选择的
			throw new ExceptionForClientTip("nightmare.cardGroup.not.syn.city");
		}
		updateFengShenCardId(uid, targetTransmigration);
		UserTransmigrationCity transmigrationCity = transmigrationCities.stream().filter(tmp -> tmp.getBaseId() == cityId).findFirst().orElse(null);

		if (null == transmigrationCity) {
			CfgCityEntity city = CityTool.getCityById(cityId);
			transmigrationCity = UserTransmigrationCity.getInstance(city, uid);
			gameUserService.addItem(uid, transmigrationCity);
		}
		transmigrationCity.setCardGroup(targetTransmigration.getCardGroup());
		boolean isPresentLeaderCard = leaderCardService.getUserLeaderCardOp(uid).isPresent();
		if (!transmigrationCity.getCardGroup().contains(LeaderCardTool.getLeaderCardId()) && isPresentLeaderCard) {
			transmigrationCity.getCardGroup().add(0, LeaderCardTool.getLeaderCardId());
		}
		gameUserService.updateItem(transmigrationCity);
		RDCardGroups rd = new RDCardGroups();
		rd.addCardIds(CardGroupWay.TRANSMIGRATION, transmigrationCity.getCardGroup(), transmigrationCity.getFuCe());
		return rd;
	}

	/**
	 * 获取卡组
	 *
	 * @param uid
	 * @param cityId
	 * @return
	 */
	public CPCardGroup getCardGroup(long uid, int cityId) {
		UserTransmigrationCity transmigrationCity = userTransmigrationCityService.getTransmigrationCity(uid, cityId);
		if (null == transmigrationCity || ListUtil.isEmpty(transmigrationCity.getCardGroup())) {
			throw new ExceptionForClientTip("card.grouping.not.blank");
		}
		if (transmigrationCity.getCardGroup().size() == 1 && transmigrationCity.getCardGroup().contains(LeaderCardTool.getLeaderCardId())) {
			throw new ExceptionForClientTip("card.grouping.not.blank");
		}
		updateFengShenCardId(uid, transmigrationCity);
		List<UserCard> userCards = userCardService.getUserCards(uid, transmigrationCity.getCardGroup());
		return CPCardGroup.getInstanceByUserCards(uid, transmigrationCity.getFuCe(), userCards);

	}


	/**
	 * 获取某属性城池的等级分组
	 *
	 * @param defenderType
	 * @return
	 */
	private Map<Integer, List<Integer>> getCityGroupAsLevel(GameTransmigration transmigration, int defenderType) {
		Map<String, TransmigrationDefender> defenders = transmigration.getDefenders();
		List<Integer> cityIds = new ArrayList<>();
		for (String cityIdStr : defenders.keySet()) {
			if (defenders.get(cityIdStr).gainDefenderType() == defenderType) {
				cityIds.add(Integer.valueOf(cityIdStr));
			}
		}
		//同属性城池等级分组
		Map<Integer, List<Integer>> cityGroupAsLevel = new HashMap<>();
		for (CfgCityEntity city : CityTool.getCities()) {
			if (!city.isCC()) {
				continue;
			}
			if (!cityIds.contains(city.getId())) {
				continue;
			}
			List<Integer> cities = cityGroupAsLevel.getOrDefault(city.getLevel(), new ArrayList<>());
			cities.add(city.getId());
			cityGroupAsLevel.put(city.getLevel(), cities);
		}
		return cityGroupAsLevel;
	}

	/**
	 * 更新封神卡的信息
	 *
	 * @param uid
	 * @param transmigrationCity
	 */
	private void updateFengShenCardId(long uid, UserTransmigrationCity transmigrationCity) {
		if (transmigrationCity == null || ListUtil.isEmpty(transmigrationCity.getCardGroup())) {
			return;
		}
		boolean needUpdate = false;
		List<Integer> allDeifyCardIds = CardTool.getAllDeifyCards().stream().map(CfgDeifyCardEntity::getId).collect(Collectors.toList());
		List<Integer> newIds = new ArrayList<>();
		for (int cardId : transmigrationCity.getCardGroup()) {
			int deifyCardId = CardTool.getDeifyCardId(cardId);
			if (allDeifyCardIds.contains(deifyCardId)) {
				UserCard userCard = userCardService.getUserCard(uid, deifyCardId);
				if (userCard != null) {
					newIds.add(userCard.getBaseId());
					needUpdate = true;
					continue;
				}
			}
			if (newIds.contains(cardId) || newIds.contains(deifyCardId)) {
				continue;
			}
			newIds.add(cardId);
		}
		if (needUpdate) {
			transmigrationCity.setCardGroup(newIds);
			gameUserService.updateItem(transmigrationCity);
		}
	}
}
