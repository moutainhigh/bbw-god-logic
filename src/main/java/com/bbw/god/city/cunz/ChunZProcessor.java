package com.bbw.god.city.cunz;

import com.bbw.common.PowerRandom;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.AbstractSpecialCityProcessor;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidaySpecialCityFactory;
import com.bbw.god.city.ICityArriveProcessor;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffEnum;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffService;
import com.bbw.god.city.miaoy.hexagram.event.HexagramEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.achievement.UserAchievementService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.gameuser.task.timelimit.cunz.UserCunZTaskService;
import com.bbw.god.random.config.RandomKeys;
import com.bbw.god.random.config.RandomStrategy;
import com.bbw.god.random.service.RandomCardService;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.random.service.RandomResult;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.server.god.GodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 村庄
 *
 * @author suhq
 * @date 2018年10月24日 下午5:34:24
 */
@Service
public class ChunZProcessor implements ICityArriveProcessor {
    private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.CZ);
    @Autowired
    private GodService userGodService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private HexagramBuffService hexagramBuffService;
    @Autowired
    private UserCunZTaskService userCunZTaskService;
    @Autowired
    private UserAchievementService userAchievementService;
    @Autowired
    private HolidaySpecialCityFactory holidaySpecialBulidFactory;

    @Override
    public List<CityTypeEnum> getCityTypes() {
        return this.cityTypes;
    }

    @Override
    public Class<RDArriveChunZ> getRDArriveClass() {
        return RDArriveChunZ.class;
    }

    @Override
    public RDArriveChunZ arriveProcessor(GameUser gu, CfgCityEntity city, RDAdvance rd) {
        return arrive(gu, rd);
    }

    /**
     * 到达处理
     *
     * @param gu
     * @param rd
     */
    private RDArriveChunZ arrive(GameUser gu, RDAdvance rd) {
        //活动事件
        activityEvent(gu.getId(), rd);
        if (hexagramBuffService.isHexagramBuff(gu.getId(), HexagramBuffEnum.HEXAGRAM_53.getId())) {
            BaseEventParam bep = new BaseEventParam(gu.getId(), WayEnum.CZ, rd);
            HexagramEventPublisher.pubHexagramBuffDeductEvent(bep, HexagramBuffEnum.HEXAGRAM_53.getId(), 1);
            return RDArriveChunZ.fromRDCommon(rd);
        }
        //坊间怪谈
        int talkProbability = 25;
        int random = PowerRandom.getRandomBySeed(100);
        if (random <= talkProbability) {
            return doAsFangJGT(gu, rd);
        }
        if (random <= 65) {
            return doAsNormal(gu, rd);
        }

        return doAsCunTask(gu, rd);

    }

    /**
     * 活动事件
     *
     * @param uid
     * @param rd
     */
    private void activityEvent(long uid, RDAdvance rd) {
        AbstractSpecialCityProcessor specialBuildProcessor = holidaySpecialBulidFactory.getSpecialCityProcessor(uid);
        if (null == specialBuildProcessor) {
            return;
        }
        specialBuildProcessor.cunZTriggerEvent(uid, rd);
    }

    /**
     * 存在任务
     *
     * @param gu
     * @param rd
     * @return
     */
    private RDArriveChunZ doAsCunTask(GameUser gu, RDAdvance rd) {
        //封神大陆世界
        if (!gu.getStatus().ifNotInFsdlWorld()) {
            return doAsNormal(gu, rd);
        }
        UserTimeLimitTask ut = userCunZTaskService.addNewTask(gu.getId());
        RDArriveChunZ rdArriveChunZ = new RDArriveChunZ();
        rdArriveChunZ.setCunZTaskId(ut.getBaseId());
        return rdArriveChunZ;
    }

    /**
     * 坊间怪谈
     *
     * @return
     */
    private RDArriveChunZ doAsFangJGT(GameUser gu, RDAdvance rd) {
        List<CfgCunZTalk> cfgCunZTalks = Cfg.I.get(CfgCunZTalk.class);
        //随机获得一个怪谈
        CfgCunZTalk cunZTalk = PowerRandom.getRandomFromList(cfgCunZTalks);
        //获得怪谈对应的成就
        Integer secretAchievementId = cunZTalk.getSecretAchievementId();
        //检查秘闻成就是否验证
        if (userAchievementService.isVerifySecretAchievement(gu.getId(), secretAchievementId)) {
            if (PowerRandom.getRandomBySeed(100) <= 50) {
                return doAsNormal(gu, rd);
            }
            return doAsCunTask(gu, rd);
        }
        RDArriveChunZ rdArriveChunZ = new RDArriveChunZ();
        rdArriveChunZ.setCunZTalk(cunZTalk.getTalkId());
        //获得秘闻id
        rdArriveChunZ.setSecretAchievementId(cunZTalk.getSecretAchievementId());
        //获得未验证的秘闻成就id集合
        rdArriveChunZ.setNotVerifySecretAchievement(userAchievementService.getStatusSecretAchievement(gu.getId(), false));
        //获得已验证的秘闻成就id集合
        rdArriveChunZ.setVerifySecretAchievement(userAchievementService.getStatusSecretAchievement(gu.getId(), true));
        return rdArriveChunZ;
    }

    /**
     * 元素
     *
     * @param gu
     * @param rd
     * @return
     */
    private RDArriveChunZ doAsNormal(GameUser gu, RDAdvance rd) {
        // 获得一张随机元素
        ResEventPublisher.pubEleAddEvent(gu.getId(), 1, WayEnum.CZ, rd);
        // 10%（正常）,20%（小福仙）,30%（大福仙）的概率获得随机卡牌
        int probability = 10;
        Optional<UserGod> userGod = this.userGodService.getAttachGod(gu);
        if (userGod.isPresent()) {
            if (userGod.get().getBaseId() == GodEnum.XFS.getValue()) {
                probability = 20;
            }
            if (userGod.get().getBaseId() == GodEnum.DFS.getValue()) {
                probability = 30;
            }
        }
        if (PowerRandom.getRandomBySeed(100) <= probability) {
            CfgCardEntity card = getCardForCZ(gu);
            CardEventPublisher.pubCardAddEvent(gu.getId(), card.getId(), WayEnum.CZ, "在村庄获得", rd);
        }
        return RDArriveChunZ.fromRDCommon(rd);
    }


    /**
     * 获得村庄卡牌
     *
     * @return
     */
    private CfgCardEntity getCardForCZ(GameUser gu) {
        RandomStrategy strategy = RandomCardService.getSetting(RandomKeys.CUN_ZHUANG_NORMAL);
        RandomParam randomParam = new RandomParam();
        randomParam.setExtraCardsToMap(userCardService.getUserCards(gu.getId()));
        RandomResult result = RandomCardService.getRandomList(strategy, randomParam);
        return result.getFirstCard().get();
    }

}
