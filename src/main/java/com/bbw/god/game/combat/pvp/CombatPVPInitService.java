package com.bbw.god.game.combat.pvp;

import com.bbw.common.ID;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.combat.FightAchievementCache;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.weapon.Weapon;
import com.bbw.god.game.combat.event.CombatEventPublisher;
import com.bbw.god.game.combat.event.EPCombat;
import com.bbw.god.game.combat.pvp.PvPCombatParam.PvpCard;
import com.bbw.god.game.combat.video.service.CombatVideoService;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.beast.UserLeaderBeastService;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 战斗初始化服务
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-06-18 16:07
 */
@Service
@Slf4j
public class CombatPVPInitService extends CombatInitService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private BattleCardService battleCardService;
    @Autowired
    private CombatVideoService videoService;
    @Autowired
    private LeaderCardService leaderCardService;
    @Autowired
    private UserLeaderBeastService userLeaderBeastService;


    /**
     * 初始化战斗
     *
     * @return
     */
    public Combat initCombat(PvPCombatParam p1, PvPCombatParam p2, int type) {
        Combat cbt = new Combat();
        cbt.setId(ID.INSTANCE.nextId());// TODO:优化ID标识
        Player first = initPlayer(PlayerId.P1, p1, type);
        first.setCombatId(cbt.getId());
        cbt.setP1(first);

        Player second = initPlayer(PlayerId.P2, p2, type);
        second.setCombatId(cbt.getId());
        cbt.setP2(second);

        cbt.setFirst(PlayerId.P1);
        // 牌堆移动到手牌
        battleCardService.firstMoveDrawCardsToHand(cbt.getFirstPlayer());
        battleCardService.firstMoveDrawCardsToHand(cbt.getSecondPlayer());
        cbt.getFirstPlayer().resetPos();
        cbt.getSecondPlayer().resetPos();
        cbt.setFightType(FightTypeEnum.fromValue(type));
        // 初始化录像
        videoService.initVideo(cbt);
        try {
            // 发布战斗事件 用于监听任务参与PVP的战斗
            List<Integer> ids = CardTool.getAll4and5starCards();
            if (p1.getUid() > 0) {
                EPCombat ep = EPCombat.instance(new BaseEventParam(p1.getUid()), type,
                        p2.getUid());
                CombatEventPublisher.pubCombatInitEvent(ep);
                Optional<PvpCard> optional = p1.getCards().stream().filter(p -> ids.contains(p.getBaseId()))
                        .findFirst();
                FightAchievementCache cache = TimeLimitCacheUtil.getOrCreateFightAchievementCache(p1.getUid(),
                        cbt.getId());
                if (optional.isPresent()) {
                    if (cache != null) {
                        cache.setHas4or5StarCard(true);
                        TimeLimitCacheUtil.setFightAchievementCache(p2.getUid(), cache);
                    }
                }
            }
            if (p2.getUid() > 0) {
                EPCombat ep = EPCombat.instance(new BaseEventParam(p2.getUid()), type,
                        p1.getUid());
                CombatEventPublisher.pubCombatInitEvent(ep);
                Optional<PvpCard> optional = p2.getCards().stream().filter(p -> ids.contains(p.getBaseId()))
                        .findFirst();
                FightAchievementCache cache = TimeLimitCacheUtil.getOrCreateFightAchievementCache(p2.getUid(),
                        cbt.getId());
                if (optional.isPresent()) {
                    if (cache != null) {
                        cache.setHas4or5StarCard(true);
                        TimeLimitCacheUtil.setFightAchievementCache(p2.getUid(), cache);
                    }
                }
            }
        } catch (Exception e) {
            log.error("PVP战斗初始化事件错误", e.getMessage());
        }
        return cbt;
    }

    private Player initPlayer(PlayerId playerId, PvPCombatParam player, int type) {
        CPlayerInitParam cp = new CPlayerInitParam();
        cp.setUid(player.getUid());
        cp.setCardFromUid(player.getUid());
        if (player.getRealUid() != null) {
            cp.setCardFromUid(player.getRealUid());
        }
        cp.setNickname(player.getNickname());
        cp.setLv(player.getLv());
        cp.setHeadImg(player.getHeadImg());
        List<CCardParam> cardParams = new ArrayList<>();
        for (PvpCard pc : player.getCards()) {
            if (pc.getBaseId() == CardEnum.LEADER_CARD.getCardId()) {
                //主角卡
                Optional<UserLeaderCard> cardOp = leaderCardService.getUserLeaderCardOp(cp.getCardFromUid());
                if (cardOp.isPresent()) {
                    CCardParam cardParam = CCardParam.getInstance(cardOp.get());
                    cardParam.getSkills().addAll(0, userLeaderBeastService.getSkills(cardOp.get().getGameUserId()));
                    cardParam.addSkillId(CombatSkillEnum.LEADER_CARD_EXP.getValue());
                    cardParams.add(cardParam);
                }
                continue;
            }
            CCardParam bcip = CCardParam.initPVPCard(pc);
            cardParams.add(bcip);
        }
        cp.setCards(cardParams);
        List<UserTreasure> userTreasures = new ArrayList<>();
        if (player.getUid() > 0) {
            userTreasures = userTreasureService.getAllUserTreasures(player.getUid());
        }
        List<Weapon> weapons = new ArrayList<>();
        for (UserTreasure t : userTreasures) {
            CfgTreasureEntity treas = TreasureTool.getTreasureById(t.getBaseId());
            if (20 == treas.getType()) {
                Weapon w = new Weapon(t.getBaseId(), t.getOwnNum());
                weapons.add(w);
            }
        }
        cp.setWeapons(weapons);
        cp.setInitHP(player.getInitHP());
        cp.setInitMP(player.getInitMP());
        return buildPlayer(playerId, cp, type, player);
    }

    /**
     * 初始化玩家
     *
     * @return
     */
    protected Player buildPlayer(PlayerId playerId, CPlayerInitParam p, int type, PvPCombatParam ppp) {
        Player player = new Player(p.getNickname(), p.getLv(), p.getHeadImg());
        player.setId(playerId);
        player.setUid(p.getUid());
        if (p.getUid() > 0) {
            GameUser gu = gameUserService.getGameUser(p.getUid());
            player.setIconId(gu.getRoleInfo().getHeadIcon());
        }
        int initHp = getPlayerInitHp(player.getLv());
        if (p.getInitHP() != null && p.getInitHP() > 0) {
            initHp = p.getInitHP();
        }
        player.setMaxHp(initHp);
        player.setHp(player.getMaxHp());
        int initMp = getPlayerInitMp(player.getLv());
        if (p.getInitMP() != null && p.getInitMP() > 0) {
            initMp = p.getInitMP();
        }
        player.setMaxMp(initMp);
        player.setMp(player.getMaxMp());
        player.setCardFromUid(p.getCardFromUid());
        int beginPos = PositionService.getDrawCardsBeginPos(player.getId());
        // 初始化牌堆
        String specialCards = "";
        for (CCardParam bcd : p.getCards()) {
            if (bcd.ifSpecial()) {
                specialCards += bcd.getId() + "," + bcd.buildSkillAndSymbolStr();
            }
            BattleCard hero = initBattleCard(bcd, player.getCardInitId());
            hero.setPos(beginPos++);
            player.getDrawCards().add(hero);
        }
        player.setSpecialCards(specialCards);
        shuffleDrawCards(player.getDrawCards());
        // 初始化法宝
        player.addWeapons(p.getWeapons());
        if (type == FightTypeEnum.SXDH.getValue() || type == FightTypeEnum.DFDJ.getValue()) {
            addTreasureBuff(player, ppp);
        }
        // 巅峰对决设置血量
        if (type == FightTypeEnum.DFDJ.getValue()) {
            int sum = player.getDrawCards().stream().mapToInt(BattleCard::getHp).sum();
            if (player.getHp() < sum) {
                player.setHp(sum);
                player.setMaxHp(sum);
            }
        }
        return player;
    }

    /**
     * 添加仙丹BUFF
     *
     * @param player
     */
    public void addTreasureBuff(Player player, PvPCombatParam ppp) {
        long uid = player.getUid();
        if (uid < 0) {
            return;
        }
        List<Integer> treasures = ppp.getUseTreasures();
        if (treasures == null || treasures.isEmpty()) {
            return;
        }
        // 卡牌BUFF 仙丹 长生丹【召唤师+50%HP】，鹤龄丹【hp+20】，杨舞丹【atk+20】
        TreasureEnum[] buffs = {TreasureEnum.ChangSD, TreasureEnum.HeLD, TreasureEnum.YangWD};
        double hp = 1;
        double atk = 1;
        for (TreasureEnum xd : buffs) {
            if (treasures.contains(xd.getValue())) {
                switch (xd) {
                    case ChangSD:
                        // 长生丹定价100元宝，效果：召唤师血量翻倍；
                        player.resetHp(player.getHp() * 2);
                        break;
                    case HeLD:
                        // 鹤龄丹定价100元宝，效果：全体卡牌获得防御+50%效果；
                        hp = 1.5;
                        break;
                    case YangWD:
                        // 扬舞丹定价100元宝，效果：全体卡牌获得攻击+50%效果。
                        atk = 1.5;
                        break;
                    default:
                        break;
                }
            }
        }
        if (hp <= 1 && atk <= 1) {
            return;
        }
        for (BattleCard card : player.getDrawCards()) {
            card.resetAllAtk(getInt(card.getAtk() * atk));
            card.resetAllHp(getInt(card.getHp() * hp));
        }

    }
}
