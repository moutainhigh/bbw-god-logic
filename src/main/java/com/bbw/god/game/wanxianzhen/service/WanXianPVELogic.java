package com.bbw.god.game.wanxianzhen.service;

import com.bbw.common.ID;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.*;
import com.bbw.god.game.wanxianzhen.WanXianCard;
import com.bbw.god.game.wanxianzhen.WanXianSpecialType;
import com.bbw.god.game.wanxianzhen.WanXianTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.biyoupalace.cfg.BYPalaceTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author lwb 万仙阵PVE战斗逻辑
 * @date 2020/4/23 14:45
 */
@Service
public class WanXianPVELogic extends CombatInitService {
    @Autowired
    private WanXianLogic wanXianLogic;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private BattleCardService battleCardService;

    public Combat initCombatData(long p1, long p2, int type, int gid) {
        Combat combat = new Combat();
        combat.setId(ID.INSTANCE.nextId());
        combat.setWxType(type);
        if (type == 2000) {
            combat.setWxType(WanXianTool.getCurrentSpecialType(gid));
        }
        combat.setFightType(FightTypeEnum.WXZ);
        combat.setP1(initPlayer(p1, type, PlayerId.P1));
        combat.setP2(initPlayer(p2, type, PlayerId.P2));
        combat.setFirst(PlayerId.P1);
        if (combat.getWxType() == WanXianSpecialType.BEI_SHUI.getVal()) {
            removeBanSkill(combat.getFirstPlayer());
            removeBanSkill(combat.getSecondPlayer());
        }

        if (type == 1000) {
            //常规赛血量计算：卡牌血量之和
            combat.getP1().updateHighHp(combat.getP1().gainBloodByDrawCards());
            combat.getP2().updateHighHp(combat.getP2().gainBloodByDrawCards());
        }
        battleCardService.firstMoveDrawCardsToHand(combat.getFirstPlayer());
        battleCardService.firstMoveDrawCardsToHand(combat.getSecondPlayer());
        return combat;
    }

    private void removeBanSkill(Player player) {
        for (BattleCard card : player.getDrawCards()) {
            //禁用 复活、封神、回魂
            card.getSkills().removeIf(p -> p.getId() == 1201 || p.getId() == 3105 || p.getId() == 3104);
        }
        for (BattleCard card : player.getHandCards()) {
            //禁用 复活、封神、回魂  ,幻术那边也需要处理
            if (card == null) {
                continue;
            }
            card.getSkills().removeIf(p -> p.getId() == 1201 || p.getId() == 3105 || p.getId() == 3104);
        }
    }

    /**
     * 1)召唤师等级将被调整至100级，法力值与血量按照100级时的计算
     * 2)卡牌阶数修改为10阶，等级修改为20级
     * 3)卡牌所添加过的符箓与卷轴将会保留，而且会显示出龙框
     *
     * @param playerId
     * @return
     */
    protected Player initPlayer(Long uid, int type, PlayerId playerId) {
        int gid = gameUserService.getActiveGid(uid);
        Player player = null;
        List<WanXianCard> cards = wanXianLogic.getUserRegularRaceCards(uid, type);
        if (uid > 0) {
            GameUser gu = gameUserService.getGameUser(uid);
            player = new Player(gu.getRoleInfo().getNickname(), WanXianTool.getPlayerLv(type), gu.getRoleInfo().getHead());
            player.setIconId(gu.getRoleInfo().getHeadIcon());
        }
        player.setId(playerId);
        player.setUid(uid);
        int hp = WanXianTool.getPlayerHp(type, gid);
        if (hp > 0) {
            player.setMaxHp(hp);
            player.setHp(hp);
            player.setBeginHp(hp);
        } else {
            player.setMaxHp(getPlayerInitHp(player.getLv()));
            player.setHp(player.getMaxHp());
            player.setBeginHp(player.getHp());
        }
        player.setMaxMp(getPlayerInitMp(player.getLv()));
        player.setMp(player.getMaxMp());
        player.setCardFromUid(player.getUid());
        int beginPos = PositionService.getDrawCardsBeginPos(player.getId());
        // 初始化牌堆
        int id = playerId.getValue() * 1000;
        int cardLv = -1;
        int cardHv = -1;
        int specialType = WanXianTool.getCurrentSpecialType(gid);
        if (type != WanXianLogic.TYPE_SPECIAL_RACE || WanXianSpecialType.SHEN_XIAN.getVal() != specialType) {
            cardLv = WanXianTool.getCardLv(type, gid);
            cardHv = WanXianTool.getCardHv(type, gid);

        }
        String specialCards = "";
        for (WanXianCard card : cards) {
            if (card.ifSpecial()) {
                specialCards += card.getCardId() + "," + card.buildSkillAndSymbolStr();
            }
            try {
                BattleCard hero = initBattleCard(card, cardLv, cardHv, id++);
                if (hero.getImgId() == CardEnum.LEADER_CARD.getCardId()) {
                    hero.setSex(card.getSex());
                    hero.setFashion(card.getFashion());
                    hero.setStars(card.getStar());
                    hero.setName(player.getName());
                    hero.setType(card.getType());
                    if (card.getAddZhsHp() > 0) {
                        player.updateHighHp(card.getAddZhsHp() + player.getBeginHp());
                    }
                }
                hero.setPos(beginPos++);
                player.getDrawCards().add(hero);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        player.setSpecialCards(specialCards);
        shuffleDrawCards(player.getDrawCards());
        return player;
    }

    public BattleCard initBattleCard(WanXianCard userCard, int lv, int hv, int id) {
        lv = lv >= 0 ? lv : userCard.getLv();
        hv = hv >= 0 ? hv : userCard.getHv();
        BattleCard hero = new BattleCard();
        hero.setId(id);
        hero.setImgId(userCard.getCardId());
        int initAtk = userCard.getInitAtk();
        int initHp = userCard.getInitHp();
        if (userCard.getCardId() != CardEnum.LEADER_CARD.getCardId()) {
            CfgCardEntity cfgCard = CardTool.getCardById(userCard.getCardId());
            Integer star = CardTool.getCardStarForFight(userCard.getCardId(), cfgCard.getStar());
            hero.setStars(star);
            hero.setName(cfgCard.getName());
            hero.setType(TypeEnum.fromValue(cfgCard.getType()));
            if (null != cfgCard.getGroup()) {
                hero.setGroupId(cfgCard.getGroup());
            }
            int normalAtk = cfgCard.getAttack() + BYPalaceTool.getSymbolEffect(userCard.getAttackSymbol());
            initAtk = getAtk(normalAtk, lv, hv);
            int normalHp = cfgCard.getHp() + BYPalaceTool.getSymbolEffect(userCard.getDefenceSymbol());
            initHp = getHp(normalHp, lv, hv);
        }
        hero.setHv(hv);
        hero.setLv(lv);
        hero.setIsUseSkillScroll(userCard.getIsUseSkillScroll());
        // 在牌堆里，默认需要法力值8
        hero.setInitAtk(initAtk);
        hero.setInitHp(initHp);
        hero.setRoundAtk(initAtk);
        hero.setRoundHp(initHp);
        hero.setAtk(initAtk);
        hero.setHp(initHp);
        // 物理攻击技能
        List<Integer> skillIds = new ArrayList<>();
        skillIds.add(userCard.getSkill0());
        if (hero.getLv() >= 5) {
            skillIds.add(userCard.getSkill5());
        }
        if (hero.getLv() >= 10) {
            skillIds.add(userCard.getSkill10());
        }
        skillIds.addAll(userCard.getExtraSkills());

        for (Integer skillId : skillIds) {
            if (skillId == null || skillId == 0 || hero.existSkill(skillId)) {
                continue;
            }
            Optional<CfgCardSkill> cardSkillOpById = CardSkillTool.getCardSkillOpById(skillId);
            if (!cardSkillOpById.isPresent()) {
                continue;
            }
            CfgCardSkill cfgCardSkill = cardSkillOpById.get();
            BattleSkill skill = BattleSkill.instanceBornSkill(cfgCardSkill);
            hero.addSkill(skill);
            for (int ownSkill : cfgCardSkill.getOwnSkills()) {
                if (hero.existSkill(ownSkill)) {
                    continue;
                }
                Optional<CfgCardSkill> ownSkillOp = CardSkillTool.getCardSkillOpById(ownSkill);
                if (ownSkillOp.isPresent()) {
                    BattleSkill bornSkill = BattleSkill.instanceBornSkill(ownSkillOp.get());
                    bornSkill.setParent(cfgCardSkill.getId());
                    hero.addSkill(bornSkill);
                }
            }
        }
        return hero;
    }
}
