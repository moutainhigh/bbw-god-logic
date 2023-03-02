package com.bbw.god.fight.processor;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.video.CombatVideo;
import com.bbw.god.game.combat.video.CombatVideoSaveAsyncHandler;
import com.bbw.god.game.combat.video.service.CombatVideoService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.transmigration.*;
import com.bbw.god.game.transmigration.entity.*;
import com.bbw.god.game.transmigration.event.TransmigrationEventPublisher;
import com.bbw.god.game.transmigration.rd.RDTransmigrationRecord;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.CardSkillPosEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.oss.OSSConfig;
import com.bbw.oss.OSSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 轮回世界战斗
 *
 * @author: suhq
 * @date: 2021/9/25 3:29 上午
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TransmigrationFightProcessor extends AbstractFightProcessor {

    @Autowired
    private GameTransmigrationService gameTransmigrationService;
    @Autowired
    private TransmigrationRankCityService rankCityService;
    @Autowired
    private TransmigrationCityRecordService cityRecordService;
    @Autowired
    private TransmigrationHightLightUserService hightLightUserService;
    @Autowired
    private TransmigrationHightLightTotalService hightLightTotalService;
    @Autowired
    private CombatVideoService combatVideoService;
    @Autowired
    private TransmigrationRankCityService transmigrationRankCityService;
    @Autowired
    private UserTransmigrationCityService transmigrationCityService;
    @Autowired
    private UserTransmigrationService userTransmigrationService;
    @Autowired
    private CombatVideoSaveAsyncHandler combatVideoSaveAsyncHandler;

    @Override
    public WayEnum getWay() {
        return WayEnum.TRANSMIGRATION_FIGHT;
    }


    @Override
    public FightTypeEnum getFightType() {
        return FightTypeEnum.TRANSMIGRATION_FIGHT;
    }

    @Override
    public CombatPVEParam getOpponentInfo(Long uid, Long oppId, boolean fightAgain) {
        int sgId = gameUserService.getActiveGid(uid);
        GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(sgId);
        if (null == curTransmigration) {
            throw ExceptionForClientTip.fromi18nKey("transmigration.is.not.open");
        }
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        if (null == cache) {
            throw ExceptionForClientTip.fromi18nKey("city.cc.not.here");
        }
        if (cache.isTransmigration()) {
            throw ExceptionForClientTip.fromi18nKey("transmigration.already.fight");
        }

        TimeLimitCacheUtil.removeCache(uid, RDFightResult.class);

        Integer cityId = cache.getCityId();
        TransmigrationDefender defender = curTransmigration.gainCityDefender(cityId);

        List<CCardParam> cardParams = new ArrayList<>();
        for (TransmigrationCard card : defender.gainCards()) {
            UserCard.UserCardStrengthenInfo strengthenInfo = new UserCard.UserCardStrengthenInfo();
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_0, card.getSkills().get(0));
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_5, card.getSkills().get(1));
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_10, card.getSkills().get(2));
            cardParams.add(CCardParam.init(card.getId(), card.getLv(), card.getHv(), strengthenInfo));
        }
        CPlayerInitParam ai = new CPlayerInitParam();
        int head = cardParams.get(0).getId();
        ai.setHeadImg(head);
        ai.setNickname(CardTool.getCardById(head).getName());
        ai.setLv(defender.getLv());
        ai.setInitHP(CombatInitService.getPlayerInitHp(defender.getLv()) * 2);
        ai.setCards(cardParams);
        ai.addBuffs(defender.getRunes());
        CombatPVEParam pveParam = new CombatPVEParam();
        pveParam.setAiPlayer(ai);
        pveParam.setCityBaseId(cityId);
        pveParam.setCityLevel(CityTool.getCityById(cityId).getLevel());
        pveParam.setFightType(FightTypeEnum.TRANSMIGRATION_FIGHT.getValue());
        pveParam.setFightAgain(fightAgain);
        return pveParam;
    }

    @Override
    public void handleAward(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(ServerTool.getServerGroup(gu.getServerId()));
        if (null == curTransmigration) {
            return;
        }

        //更新挑战成功标识状态
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(gu.getId());
        cache.setTransmigration(true);
        TimeLimitCacheUtil.setChengChiInfoCache(gu.getId(), cache);

        Integer cityId = gu.gainCurCity().getId();
        boolean isFirstSuccess = !cityRecordService.hasRecord(curTransmigration, gu.getId(), cityId);
        // 战斗回合评分
        int roundScore = param.gainRoundScore();
        //使用法宝评分
        int weaponScore = param.gainWeaponScore();
        //剩余血量评分
        int remainBloodScore = param.gainRemainBloodScore();
        // 死亡神将评分
        int killedScore = param.gainKilledScore();
        //扣除血量评分
        int oppRemainBloodScore = param.gainOppRemainBloodScore();
        // 击杀神将评分
        int oppRemainScore = param.gainOppRemainScore();

        //分数处理
        List<Integer> scoreCompositions = Arrays.asList(roundScore, weaponScore, remainBloodScore, killedScore, oppRemainBloodScore, oppRemainScore);
        int score = ListUtil.sumInt(scoreCompositions);
        UserTransmigrationRecord record = UserTransmigrationRecord.getInstance(gu.getId(), cityId, scoreCompositions);
        record.setVideoUrl(null);
        boolean isNewRecord = rankCityService.updateNewRecord(curTransmigration, gu.getId(), cityId, record.getId(), score);
        if (isNewRecord) {
            record.setNewRecord(isNewRecord);
            userTransmigrationService.updateNewRecord(gu.getId(), cityId, score);
        }
        boolean addToTotalHightLight = hightLightTotalService.addToHightLight(curTransmigration, gu.getId(), record.getId(), score);
        boolean addToUserHightLight = hightLightUserService.addToHightLight(curTransmigration, gu.getId(), record.getId(), score);
        if (isNewRecord || addToTotalHightLight || addToUserHightLight) {
            Optional<CombatVideo> optional = combatVideoService.getCombatVideo(param.getCombatId());
            if (optional.isPresent()) {
                String ossPath = OSSService.getTransmigrationOssPath(gu.getId(), param.getCombatId());
                record.setVideoUrl(OSSConfig.downStr + ossPath);
                gameUserService.addItem(gu.getId(), record);
                combatVideoSaveAsyncHandler.save(optional.get(), ossPath);
            }
        }
        //奖励处理
        UserTransmigrationCity transmigrationCity = transmigrationCityService.getTransmigrationCity(gu.getId(), cityId);
        if (isFirstSuccess) {
            transmigrationCity.updateAsOwn();
        }
        transmigrationCity.updateToEnableAward(score);
        gameUserService.updateItem(transmigrationCity);
        //返回战斗记录数据
        RDTransmigrationRecord rdRecord = RDTransmigrationRecord.getInstance(record);
        int scoreAsNo1 = transmigrationRankCityService.getScoreAsNo1(curTransmigration, cityId);
        if (score == scoreAsNo1) {
            rdRecord.setIsCityNo1(1);
        }
        rdRecord.setAwardStatus(transmigrationCity.getAwardStatus());
        rd.setTransmigrationRecord(rdRecord);
        //发布挑战成功事件
        TransmigrationEventPublisher.pubTransmigrationSuccessEvent(gu.getId(), curTransmigration, record, isFirstSuccess);

    }

    @Override
    public void failure(GameUser gu, RDFightResult rd, FightSubmitParam param) {
    }

}
