package com.bbw.god.gameuser.achievement.behavior;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.AchievementServiceFactory;
import com.bbw.god.gameuser.achievement.BaseAchievementService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.login.LoginBehaviorStatisticEvent;
import com.bbw.god.gameuser.statistic.behavior.maou.AloneMaouStatistic;
import com.bbw.god.gameuser.statistic.behavior.move.MoveBehaviorStatisticEvent;
import com.bbw.god.gameuser.statistic.behavior.zhibao.ZhiBaoStatistic;
import com.bbw.god.gameuser.statistic.event.BehaviorStatisticEvent;
import com.bbw.god.gameuser.statistic.event.EPBehaviorStatistic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 行为监听
 *
 * @author: suhq
 * @date: 2021/8/18 5:12 下午
 */
@Component
@Async
@Slf4j
public class BehaviorAchievementListener {
	@Autowired
	private AchievementServiceFactory achievementServiceFactory;
	@Autowired
	private GameUserService gameUserService;

	/**
	 * 行为统计事件
	 *
	 * @param event
	 */
	@Order(1000)
	@EventListener
	public void behaviorStatistic(BehaviorStatisticEvent event) {
		if (event.getEP().getBehaviorStatistic() instanceof AloneMaouStatistic) {
			doAchieveAsAloneMaou(event.getEP());
		} else {
			doAchieve(event.getEP());
		}
	}

	/**
	 * 移动行为统计时间
	 *
	 * @param event
	 */
	@Order(1000)
	@EventListener
	public void move(MoveBehaviorStatisticEvent event) {
		doAchieve(event.getEP());
	}

	/**
	 * 登录行为统计事件
	 *
	 * @param event
	 */
	@Order(1000)
	@EventListener
	public void login(LoginBehaviorStatisticEvent event) {
		doAchieve(event.getEP());
	}

	private void doAchieve(EPBehaviorStatistic ep) {
		try {
			List<BehaviorType> excludes = Arrays.asList(
					BehaviorType.RECHARGE,
					BehaviorType.BUILDING_AWARD,
					BehaviorType.GUILD_TASK,
					BehaviorType.CHANGE_WORLD);
			Long uid = ep.getGuId();
			BehaviorStatistic statistic = ep.getBehaviorStatistic();
			if (excludes.contains(statistic.getBehaviorType())) {
				return;
			}
			List<BehaviorAchievementService> services = achievementServiceFactory.getByBehaviorType(statistic.getBehaviorType());
			UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
			for (BehaviorAchievementService service : services) {
				if (service.isAccomplished(info)) {
					continue;
				}
				int value = service.getMyValueForAchieve(uid, info);
				service.achieve(uid, value, info, ep.getRd());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void doAchieveAsAloneMaou(EPBehaviorStatistic ep) {
		try {
			Long uid = ep.getGuId();
			BehaviorStatistic behaviorStatistic = ep.getBehaviorStatistic();
			AloneMaouStatistic statistic = (AloneMaouStatistic) behaviorStatistic;
			List<BehaviorAchievementService> services = achievementServiceFactory.getByBehaviorType(statistic.getBehaviorType());
			UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
			Integer gold = statistic.getGold();
			Integer wood = statistic.getWood();
			Integer water = statistic.getWater();
			Integer fire = statistic.getFire();
			Integer earth = statistic.getEarth();
			List<Integer> list = Arrays.asList(gold, wood, water, fire, earth);
			for (BehaviorAchievementService service : services) {
				if (service.isAccomplished(info)) {
					continue;
				}
				int value = service.getMyValueForAchieve(uid, info);
				service.achieve(uid, value, info, ep.getRd());
			}
			// id=830和840的成就必须最后执行
			BaseAchievementService service830 = achievementServiceFactory.getById(830);
			int count830 = (int) list.stream().filter(s -> s >= 7).count();
			service830.achieve(uid, count830, info, ep.getRd());
			BaseAchievementService service840 = achievementServiceFactory.getById(840);
			int count840 = (int) list.stream().filter(s -> s >= 10).count();
			service840.achieve(uid, count840, info, ep.getRd());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 至宝获得事件监听
	 *
	 * @param event
	 */
	@EventListener
	@Order(1000)
	public void zhiBaoAddEvent(BehaviorStatisticEvent event) {
		try {


			EPBehaviorStatistic ep = event.getEP();
			if (WayEnum.KUNLS_INFUSION != ep.getWay() && WayEnum.Mail != ep.getWay()) {
				return;
			}
			BehaviorStatistic behaviorStatistic = ep.getBehaviorStatistic();
			if (!(behaviorStatistic instanceof ZhiBaoStatistic)) {
				return;
			}
			ZhiBaoStatistic statistic = (ZhiBaoStatistic) behaviorStatistic;

			long uid = ep.getGuId();

			//获得用户成就信息
			UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);

			//法器道人成就
			BaseAchievementService service17430 = achievementServiceFactory.getById(17430);
			//成就需要的仙品法器数量
			service17430.achieve(uid, statistic.getFairyFaQiNum(), info, ep.getRd());

			//灵宝道人成就
			BaseAchievementService service17440 = achievementServiceFactory.getById(17440);
			//成就需要的仙品灵宝数量
			service17440.achieve(uid, statistic.getFairyLingBaoNum(), info, ep.getRd());

			//金之法器成就
			BaseAchievementService service17450 = achievementServiceFactory.getById(17450);
			service17450.achieve(uid, statistic.getGoldPropertyFaQiNum(), info, ep.getRd());

			//木之法器成就
			BaseAchievementService service17460 = achievementServiceFactory.getById(17460);
			service17460.achieve(uid, statistic.getWoodPropertyFaQiNum(), info, ep.getRd());

			//水之法器成就
			BaseAchievementService service17470 = achievementServiceFactory.getById(17470);
			service17470.achieve(uid, statistic.getWaterPropertyFaQiNum(), info, ep.getRd());

			//火之法器成就
			BaseAchievementService service17480 = achievementServiceFactory.getById(17480);
			service17480.achieve(uid, statistic.getFirePropertyFaQiNum(), info, ep.getRd());

			//土之法器成就
			BaseAchievementService service17490 = achievementServiceFactory.getById(17490);
			service17490.achieve(uid, statistic.getEarthPropertyFaQiNum(), info, ep.getRd());

			//金之灵宝成就
			BaseAchievementService service17500 = achievementServiceFactory.getById(17500);
			service17500.achieve(uid, statistic.getGoldPropertyLingBaoNum(), info, ep.getRd());

			//木之灵宝成就
			BaseAchievementService service17510 = achievementServiceFactory.getById(17510);
			service17510.achieve(uid, statistic.getWoodPropertyLingBaoNum(), info, ep.getRd());

			//水之灵宝成就
			BaseAchievementService service17520 = achievementServiceFactory.getById(17520);
			service17520.achieve(uid, statistic.getWaterPropertyLingBaoNum(), info, ep.getRd());

			//火之灵宝成就
			BaseAchievementService service17530 = achievementServiceFactory.getById(17530);
			service17530.achieve(uid, statistic.getFairyLingBaoNum(), info, ep.getRd());

			//土之灵宝成就
			BaseAchievementService service17540 = achievementServiceFactory.getById(17540);
			service17540.achieve(uid, statistic.getEarthPropertyLingBaoNum(), info, ep.getRd());


		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
