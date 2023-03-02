package com.bbw.god.gameuser.guide.v1;

import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.RDCombat;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.pve.PVELogic;
import com.bbw.god.game.combat.video.service.CombatVideoService;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.config.card.CardTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 新手引导战斗逻辑
 * @date 2020/12/14 10:24
 **/
@Service
public class NewerGuideCombatLogic {
    @Autowired
    private PVELogic pveLogic;
    @Autowired
    private NewerGuideCombatInitService newerGuideCombatInitService;
    @Autowired
    private BattleCardService battleCardService;
    @Autowired
    private CombatRedisService redisService;
    @Autowired
    private CombatVideoService videoService;
    /**
     * 初始化战斗
     *
     * @param fightType
     * @param myUid
     * @param opponentId
     * @param newerGuide
     * @return
     */
    public RDCombat initFightData(int fightType, long myUid, long opponentId, Integer newerGuide) {
        //newerGuideService.check(myUid, newerGuide);
        FightTypeEnum fromValue = FightTypeEnum.fromValue(fightType);
        CPlayerInitParam myParam = pveLogic.getMyFightsInfo(fromValue, myUid, -1L);
        CombatPVEParam param = pveLogic.getInitPVEParam(fromValue, myUid, -1, false);
        CPlayerInitParam aiParam = param.getAiPlayer();
        Optional<CCardParam> optional = aiParam.getCards().stream().filter(p -> p.getId() == CardEnum.WEN_DAO_REN.getCardId()).findFirst();
        if (optional.isPresent()){
            //蚊道人不能出现在新手引导阶段
            CCardParam cardParam = optional.get();
            List<Integer> list = aiParam.getCards().stream().map(CCardParam::getId).collect(Collectors.toList());
            aiParam.setCards(aiParam.getCards().stream().filter(p->p.getId()!=CardEnum.WEN_DAO_REN.getCardId()).collect(Collectors.toList()));
            Integer id = CardTool.getRandomCard(1, 1, list).get(0).getId();
            aiParam.getCards().add(CCardParam.init(id,cardParam.getLv(),cardParam.getHv()));
        }
        Combat combat = newerGuideCombatInitService.initCombatPVE(myParam, aiParam,param);
        battleCardService.moveDrawCardsToHand(combat.getFirstPlayer());
        battleCardService.moveDrawCardsToHand(combat.getSecondPlayer());
        redisService.save(combat);
        RDCombat rdc = RDCombat.fromCombat(combat);
        videoService.addRoundData(combat, 0);
        return rdc;
    }
}
