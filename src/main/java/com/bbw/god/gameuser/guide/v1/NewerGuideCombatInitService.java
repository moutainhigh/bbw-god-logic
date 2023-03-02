package com.bbw.god.gameuser.guide.v1;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.CombatInfo;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.event.CombatEventPublisher;
import com.bbw.god.game.combat.event.EPCombat;
import com.bbw.god.game.combat.pve.CombatPVEInitService;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.guide.GuideConfig;
import com.bbw.god.gameuser.guide.NewerGuideService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 新手引导战斗初始化
 * @date 2020/12/12 14:23
 **/
@Log4j
@Service
public class NewerGuideCombatInitService extends CombatPVEInitService {
    @Autowired
    private NewerGuideService newerGuideService;
    @Autowired
    private GuideConfig guideConfig;
    @Autowired
    private UserCardService userCardService;

    @Override
    public Combat initCombatPVE(CPlayerInitParam p1, CPlayerInitParam ai, CombatPVEParam param) {
        Player player1 = initPlayer(PlayerId.P1, p1, ai.getUid());
        playerAddRules(player1, param.getFightType());
        Player playerAi = initPlayer(PlayerId.P2, ai, p1.getUid());
        playerAiAddRules(playerAi, param);
        //初始化战斗对象
        Combat combat = Combat.instance(player1, playerAi, param);
        if (param.getAwardkey() > 0) {
            String dec = exAwardCheckFactory.getExAwardCheckService(param.getAwardkey()).getDescriptor();
            combat.setAwardDesc(dec);
        }
        //初始化战斗 初始信息对象
        CombatInfo combatInfo = CombatInfo.instance(combat);
        combatInfo.addPVEParam(param);
        combat.setNewerGuide(newerGuideService.getNewerGuide(p1.getUid()));
        redisService.saveCombatInfo(combatInfo);
        // 战斗初始化成功
        EPCombat ep = EPCombat.instance(new BaseEventParam(p1.getUid()), param.getFightType(), param.getOpponentId());
        CombatEventPublisher.pubCombatInitEvent(ep);
        return combat;
    }

    /**
     * 初始化玩家
     *
     * @return
     */
    private Player initPlayer(PlayerId playerId, CPlayerInitParam cpp, long opponentId) {
        Player player = Player.instance(cpp, getPlayerInitHp(cpp.getLv()), getPlayerInitMp(cpp.getLv()));
        player.setId(playerId);
        int beginPos = PositionService.getDrawCardsBeginPos(player.getId());
        // 初始化牌堆
        int id = playerId.getValue() * 1000;
        int minHv = 10;
        for (CCardParam bcd : cpp.getCards()) {
            BattleCard card = initBattleCard(bcd, id++);
            card.setPos(beginPos++);
            card.setStars(bcd.getStar());
            card.setType(TypeEnum.fromValue(bcd.getType()));
            player.getDrawCards().add(card);
            if (card.getHv() < minHv) {
                minHv = card.getHv();
            }
        }
        player.setMinCardHv(minHv);
        if (cpp.getUid() > 0) {
            int newerGuide = newerGuideService.getNewerGuide(cpp.getUid());
            // 攻城的时候固定卡牌顺序
            if (NewerGuideEnum.YEGUAI.getStep() != newerGuide) {
                shuffleDrawCards(player.getDrawCards());
                return player;
            }
            // 获取卡牌id集合，按照手牌顺序
            int[] cardIdArr = getCardIdArr(cpp);
            // 设置进牌堆
            setDrawCards(player, cardIdArr);
            return player;
        }
        shuffleDrawCards(player.getDrawCards());
        return player;
    }

    /**
     * 获取卡牌id集合，按照手牌顺序
     *
     * @param cpp
     * @return
     */
    private int[] getCardIdArr(CPlayerInitParam cpp) {
        int country = gameUserService.getGameUser(cpp.getUid()).getRoleInfo().getCountry();
        Integer star2CardId = guideConfig.getDropCardsAsDfs().get(country / 10 - 1);
        Integer star3CardId = guideConfig.getDrawCardsAsJxPool().get(country / 10 - 1);
        List<Integer> cardIds = userCardService.getUserCards(cpp.getUid()).stream().map(UserCfgObj::getBaseId).collect(Collectors.toList());
        cardIds.remove(star2CardId);
        cardIds.remove(star3CardId);
        cardIds.add(0, star2CardId);
        cardIds.add(1, star3CardId);
        return cardIds.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * 设置进牌堆
     *
     * @param player
     * @param cardIdArr
     */
    private void setDrawCards(Player player, int[] cardIdArr) {
        int cardNums=player.getDrawCards().size();
        List<BattleCard> cardList = new ArrayList<>();
        try{
            int i = 0;
            int j = 0;
            while (!player.getDrawCards().isEmpty()) {
                BattleCard card = player.getDrawCards().get(j);
                if (cardIdArr[i] == card.getImgId() || cardList.size() >= cardIdArr.length) {
                    cardList.add(card);
                    player.getDrawCards().remove(j);
                    i++;
                    j = 0;
                    continue;
                }
                j++;
            }
        }catch (IndexOutOfBoundsException e){
            log.error(player.getUid()+"卡牌初始化异常：玩家的卡牌数量："+cardNums);
            log.error(e.getMessage(),e);
        }
        player.setDrawCards(cardList);
    }

}
