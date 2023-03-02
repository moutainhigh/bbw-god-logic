package com.bbw.god.fight.processor;

import com.bbw.common.PowerRandom;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidayBuGeiTangProcessor;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.yeg.IYegFightProcessor;
import com.bbw.god.city.yeg.YeGFightProcessorFactory;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 不给糖就捣乱活动战斗处理类
 *
 * @author fzj
 * @date 2021/10/21 13:49
 */
@Service
public class HolidayBuGeiTangFightProcessor extends AbstractFightProcessor {

    @Autowired
    private YeGFightProcessorFactory yeGFightProcessorFactory;

    @Autowired
    private HolidayBuGeiTangProcessor holidayBuGeiTangProcessor;

    @Override
    public FightTypeEnum getFightType() {
        return FightTypeEnum.WAN_S_ACTIVITY_FIGHT;
    }

    @Override
    public WayEnum getWay() {
        return WayEnum.FIGHT_YG;
    }

    @Override
    public CombatPVEParam getOpponentInfo(Long uid, Long oppId, boolean fightAgain) {
        GameUser gu = gameUserService.getGameUser(uid);
        int type = YeGuaiEnum.YG_ELITE.getType();
        TimeLimitCacheUtil.removeCache(uid, RDFightResult.class);
        IYegFightProcessor fightProcessor = yeGFightProcessorFactory.makeYeGFightProcessor(YeGuaiEnum.YG_ELITE);
        RDFightsInfo fightsInfo = fightProcessor.getFightsInfo(gu, type);
        List<CCardParam> cardParams = new ArrayList<>();
        for (RDFightsInfo.RDFightCard card : fightsInfo.getCards()) {
            cardParams.add(CCardParam.init(card.getBaseId(), card.getLevel(), card.getHierarchy(), card.getStrengthenInfo()));
        }
        CPlayerInitParam ai = new CPlayerInitParam();
        ai.setHeadImg(3180);
        ai.setHeadIcon(fightsInfo.getHeadIcon());
        ai.setNickname("南瓜幽灵（精英）");
        ai.setLv(fightsInfo.getLevel());
        ai.setCards(cardParams);
        CombatPVEParam pveParam = new CombatPVEParam();
        pveParam.setYgType(type);
        pveParam.setAiPlayer(ai);
        return pveParam;
    }

    @Override
    public void failure(GameUser gu, RDFightResult rd, FightSubmitParam param) {

    }

    @Override
    public void handleAward(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        for (int i = 0; i < 2; i++) {
            int awardId = getActivityAwardId();
            awardId = holidayBuGeiTangProcessor.checkGuaiWeiTangNum(gu.getId(), awardId);
            TreasureEventPublisher.pubTAddEvent(gu.getId(), awardId, 1, WayEnum.WAN_S_ACTIVITY_BOX, rd);
        }
    }

    /**
     * 获得奖励id
     *
     * @return
     */
    private int getActivityAwardId() {
        int seed = PowerRandom.getRandomBySeed(100);
        if (seed <= 59) {
            return TreasureEnum.XIONG_XRT.getValue();
        }
        if (seed > 59 && seed <= 98) {
            return TreasureEnum.TANG_SBG.getValue();
        }
        return TreasureEnum.GUAI_WTD.getValue();
    }
}
