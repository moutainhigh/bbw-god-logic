package com.bbw.god.game.combat.pve;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianPosEnum;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianTool;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleCardFactory;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.event.CombatEventPublisher;
import com.bbw.god.game.combat.event.EPCombat;
import com.bbw.god.game.combat.exaward.ExAwardCheckFactory;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.combat.video.service.CombatVideoService;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.CardSkillTool;
import com.bbw.god.game.config.card.CfgCardSkill;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.transmigration.GameTransmigrationService;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.beast.UserLeaderBeastService;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquimentService;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.server.god.GodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 战斗初始化服务
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-06-18 16:07
 */
@Service
public class CombatPVEInitService extends CombatInitService {
    @Autowired
    protected GameUserService gameUserService;
    @Autowired
    protected ExAwardCheckFactory exAwardCheckFactory;
    @Autowired
    private GodService godService;
    @Autowired
    private CombatVideoService videoService;
    @Autowired
    private LeaderCardService leaderCardService;
    @Autowired
    private UserLeaderBeastService userLeaderBeastService;
    @Autowired
    private UserLeaderEquimentService userLeaderEquimentService;
    @Autowired
    private GameTransmigrationService gameTransmigrationService;
    @Autowired
    private CombatRunesPerformService combatRunesPerformService;

    private static final List<Integer> KING_CARD_PRIORITY_CITY = Arrays.asList(5025, 3130, 1017, 1922, 1942, 1910, 2501, 3725, 4617, 2721);

    public Combat initCombatPVE(CPlayerInitParam p1, CPlayerInitParam ai, CombatPVEParam param) {
        CombatGodEnum godEnum = godAtkEffect(p1.getUid());
        doAsXBAndXJ(p1, godEnum, param);
        handleExtraCardRate(p1, ai);

        combatRunesPerformService.runInitCombatParamRunes(p1, ai, param);
        Player player1 = initPlayer(PlayerId.P1, p1);
        Player playerAi = initPlayer(PlayerId.P2, ai);
        GameUser gu = gameUserService.getGameUser(p1.getUid());
        //如果是梦魇世界/轮回世界，法外分身
        if (gu.getStatus().ifNotInFsdlWorld()) {
            FightTypeEnum typeEnum = FightTypeEnum.fromValue(param.getFightType());
            buildLeaderCard(player1, typeEnum, playerAi.getUid());
            buildLeaderCard(playerAi, typeEnum, player1.getUid());
        }
        doAsWXZQ(player1, playerAi, gu, param);

        player1.setGod(godEnum == null ? 0 : godEnum.getValue());
        playerAddRules(player1, param.getFightType());
        playerAiAddRules(playerAi, param);

        if (gu.getStatus().intoNightmareWord()) {
            menYanAiAddRules(param.getFightType(), playerAi);
        }
        if (param.getFightType() == FightTypeEnum.ATTACK.getValue() || param.getFightType() == FightTypeEnum.YAOZU_FIGHT.getValue()) {
            if (gu.getStatus().intoNightmareWord()) {
                if (param.getFightType() == FightTypeEnum.ATTACK.getValue()) {
                    playerAi.updateHighHp(playerAi.getHp() * 2);
                    playerAi.setOwnGYJL(true);
                    //天官赐福
                    player1.setOwnTGCF(true);
                    playerAi.setOwnTGCF(true);
                } else {
                    playerAi.updateHighHp(playerAi.getHp() * 3);
                    playerAi.setOwnYZXM(true);
                }
                if (param.getCardDisparityAtk() != 0 || param.getCardDisparityHp() != 0) {
                    for (BattleCard card : player1.getDrawCards()) {
                        if (card != null) {
                            card.resetAllAtk(getInt(card.getAtk() * (1 + param.getCardDisparityAtk())));
                            card.resetAllHp(getInt(card.getHp() * (1 + param.getCardDisparityHp())));
                        }
                    }
                }
            } else {
                //天官赐福
                player1.setOwnTGCF(true);
                playerAi.setOwnTGCF(true);
            }
        } else if (param.getFightType() == FightTypeEnum.TRANSMIGRATION_FIGHT.getValue()) {
            playerAi.setOwnGYJL(true);
        } else if (param.getFightType() == FightTypeEnum.MXD.getValue()) {
            //层主战斗时，双方血量按照万仙阵常规赛的模式，取以下两个数值的较大值
            //（1）根据召唤师等级血量
            //（2）玩家携带所有卡牌的防御之和
            NightmareMiXianPosEnum typeByAiId = NightmareMiXianTool.getTypeByAiId(param.getOpponentId());
            if (NightmareMiXianPosEnum.LEVEL_LEADER == typeByAiId) {
                int blood = Math.max(player1.getHp(), player1.gainBloodByDrawCards());
                player1.setHp(blood);
                player1.setMaxHp(blood);
            }
            /*playerAi.getHandCards()[playerAi.getHandCards().length - 1] =
                    BattleCardFactory.buildCard(561, 20,2);*/
        }
        if (param.getFightType() == FightTypeEnum.FST.getValue()) {
            //封神台血量规则
            setHpByMaxOfPlayerHpOrCardsSumHp(player1);
            setHpByMaxOfPlayerHpOrCardsSumHp(playerAi);
        }
        if (FightTypeEnum.ZXZ.getValue() == param.getFightType() || FightTypeEnum.ZXZ_FOUR_SAINTS.getValue() == param.getFightType()) {
            // 诛仙阵相关战斗 初始法力值 * 2
            int plyer1Mp = player1.getMp() * 2;
            int plyerAiMp = playerAi.getMp() * 2;
            // 初始化等级魔法值
            player1.setLvMp(player1.getMaxMp());
            playerAi.setLvMp(playerAi.getMaxMp());
            // 初始化魔法值
            player1.setMp(plyer1Mp);
            player1.setMaxMp(plyer1Mp);
            playerAi.setMp(plyerAiMp);
            playerAi.setMaxMp(plyerAiMp);
        }
        Combat combat = Combat.instance(player1, playerAi, param);
        if (param.getAwardkey() > 0) {
            String dec = exAwardCheckFactory.getExAwardCheckService(param.getAwardkey()).getDescriptor();
            combat.setAwardDesc(dec);
        }
        //初始化战斗 初始信息对象
        CombatInfo combatInfo = CombatInfo.instance(combat);
        combatInfo.setWorldType(gu.getStatus().getCurWordType());
        combatInfo.addPVEParam(param);
        redisService.saveCombatInfo(combatInfo);
        // 战斗初始化成功
        EPCombat ep = EPCombat.instance(new BaseEventParam(p1.getUid()), param.getFightType(), param.getOpponentId());
        CombatEventPublisher.pubCombatInitEvent(ep);
        //初始化录像
        videoService.initVideo(combat);
        return combat;
    }

    /**
     * 初始化测试战斗对象
     *
     * @param p1
     * @param ai
     * @param param
     * @return
     */
    public Combat initCombatTestPVE(CPlayerInitParam p1, CPlayerInitParam ai, CombatPVEParam param) {
        Player player1 = initPlayer(PlayerId.P1, p1);
        player1.setGod(0);
        Player playerAi = initPlayer(PlayerId.P2, ai);
        //初始化战斗对象
        Combat combat = Combat.instance(player1, playerAi, param);
        return combat;
    }

    /**
     * 玩家特殊规则
     *
     * @param player
     */
    public void playerAddRules(Player player, int fightType) {
        Long uid = player.getUid();
        if (uid < 0) {
            return;
        }
        // 玩家隐藏设定：神力横扫组合2015 优先排列到一起
        if (player.getDrawCards().size() > 2) {
            List<BattleCard> slhs = player.getDrawCards().stream().filter(p -> p.getGroupId() == 2015).collect(Collectors.toList());
            if (slhs != null && slhs.size() == 2) {
                player.getDrawCards().removeAll(slhs);
                int index = PowerRandom.getRandomBySeed(player.getDrawCards().size()) - 1;
                player.getDrawCards().addAll(index, slhs);
            }
        }
        // 大R玩家 12级以下 保底1. 1-2手牌<=3，不修正2. 3-4号手牌 < 3星，不修正 2.
        // 如果需要修正，则挑选卡组里1-3星的卡，随机一张与1号手牌交换
        if (player.getLv() < 12 && player.getDrawCards().size() > 2) {
            boolean needRevise = true;
            for (int i = 0; i < player.getDrawCards().size(); i++) {
                if (i < 2 && player.getDrawCards().get(i).getStars() <= 3) {
                    needRevise = false;
                    break;
                } else if (i >= 2 && i < 4 && player.getDrawCards().get(i).getStars() < 3) {
                    needRevise = false;
                    break;
                }
            }
            if (!player.getDrawCards().isEmpty() && needRevise) {
                Optional<BattleCard> cardOp = player.getDrawCards().stream().filter(p -> p.getStars() <= 3).findFirst();
                BattleCard best = null;
                if (!cardOp.isPresent()) {
                    List<BattleCard> cards = player.getDrawCards().stream()
                            .sorted(Comparator.comparing(BattleCard::getStars)).collect(Collectors.toList());
                    best = cards.get(0);
                } else {
                    best = cardOp.get();
                }
                if (best != null) {
                    int index = player.getDrawCards().indexOf(best);
                    if (index > 0) {
                        BattleCard oCard = player.getDrawCards().get(0);
                        player.getDrawCards().set(0, best);
                        player.getDrawCards().set(index, oCard);
                    }
                }
            }
        }
        int atk = 0;
        if (player.getGod() == CombatGodEnum.TB.getValue()) {
            atk = 100;
        } else if (player.getGod() == CombatGodEnum.TJ.getValue()) {
            atk = 200;
        }
        if (atk > 0) {
            for (BattleCard card : player.getDrawCards()) {
                int newAtk = (card.getInitAtk() + atk);
                card.setInitAtk(newAtk);
                card.setRoundAtk(newAtk);
                card.setAtk(newAtk);
            }
        }
        boolean isUseZhuJiDan = gameUserService.getGameUser(uid).getStatus().getIsUseZhuJiDan();
        if (isUseZhuJiDan && player.getMp() < 8) {
            int mp = player.getMp() + 1;
            player.setMp(mp);
            player.setMaxMp(mp);
        }
    }

    /**
     * AI玩家特殊规则
     *
     * @param ai
     * @param param
     */
    public void playerAiAddRules(Player ai, CombatPVEParam param) {
        // AI设置
        FightTypeEnum fightType = FightTypeEnum.fromValue(param.getFightType());
        if (fightType == FightTypeEnum.YAOZU_FIGHT) {
            playerAiAddRulesAccomplish(ai);
        }
        if ((fightType == FightTypeEnum.ATTACK || fightType == FightTypeEnum.PROMOTE || fightType == FightTypeEnum.TRANSMIGRATION_FIGHT)
                && (param.getCityLevel() == 5 || KING_CARD_PRIORITY_CITY.contains(param.getCityBaseId()))) {
            playerAiAddRulesAccomplish(ai);
        }
        //AI王者设置
        if (FightTypeEnum.useAiKing(fightType.getValue())) {
            for (BattleCard card : ai.getDrawCards()) {
                if (card != null && card.hasKingSkill()) {
                    card.changeAikingSKill();
                }
            }
        }
    }

    /**
     * Ai特殊规则实现
     *
     * @param ai
     */
    public void playerAiAddRulesAccomplish(Player ai) {
        // 攻打五级城时 需要将王者卡 调换至手牌第一张 因为需要第一张上的是王者卡
        // 使用的方法  先找出王者卡，并随机4张王者卡相同属性的卡，先从卡堆中移除这些卡，再添加至最前面
        Optional<BattleCard> kingOp = ai.getDrawCards().stream().filter(p -> p.hasKingSkill()).findFirst();
        if (kingOp.isPresent()) {
            BattleCard king = kingOp.get();
            int type = king.getType().getValue();
            ai.getDrawCards().remove(king);
            List<BattleCard> cardList = ai.getDrawCards().stream().filter(p -> p.getType().getValue() == type).collect(Collectors.toList());
            cardList = PowerRandom.getRandomsFromList(CombatConfig.MAX_IN_HAND - 1, cardList);
            ai.getDrawCards().removeAll(cardList);
            cardList.add(king);
            ai.getDrawCards().addAll(0, cardList);
        }
    }

    /**
     * 梦魇世界特殊规则
     *
     * @param fightType
     * @param ai
     */
    private void menYanAiAddRules(int fightType, Player ai) {
        if (fightType == FightTypeEnum.ATTACK.getType()) {
            int initHp = ai.getHp() * 2;
            ai.setHp(initHp);
            ai.setMaxHp(initHp);
            ai.setBeginHp(initHp);
        }
    }

    /**
     * 初始化玩家
     *
     * @return
     */
    protected Player initPlayer(PlayerId playerId, CPlayerInitParam cpp) {
        Player player = Player.instance(cpp, getPlayerInitHp(cpp.getLv()), getPlayerInitMp(cpp.getLv()));
        if("lwh".equals(player.getName())){
            CombatBuff combatBuff1 = new CombatBuff();
            combatBuff1.setRuneId(333304);
            combatBuff1.setLevel(1);
            cpp.getBuffs().add(combatBuff1);

        }
        player.initBuffs(cpp.getBuffs());
        player.setId(playerId);
        int beginPos = PositionService.getDrawCardsBeginPos(player.getId());
        int beginDiscardPos = PositionService.getDiscardBeginPos(player.getId());
        // 初始化牌堆
        int id = playerId.getValue() * 1000;
        int minHv = 10;
        String specialCards = "";
        for (CCardParam bcd : cpp.getCards()) {
            if (bcd.ifSpecial()) {
                specialCards += bcd.getId() + "," + bcd.buildSkillAndSymbolStr();
            }
            BattleCard card = initBattleCard(bcd, id++);
            card.setStars(bcd.getStar());
            card.setType(TypeEnum.fromValue(bcd.getType()));
            if (card.isKilled()) {
                card.setPos(beginDiscardPos++);
                player.getDiscard().add(card);
            } else {
                card.setPos(beginPos++);
                player.getDrawCards().add(card);
            }
            if (card.getHv() < minHv) {
                minHv = card.getHv();
            }
        }
        player.setSpecialCards(specialCards);
        player.setMinCardHv(minHv);
        player.setBloodBarNum(cpp.getInitBloodBarNum()==null?1:cpp.getInitBloodBarNum());
        //初始初始卡牌的统计信息
        Map<Integer, List<Integer>> cardGroupByType = player.getDrawCards().stream()
                .collect(Collectors.groupingBy(tmp -> tmp.getType().getValue(), Collectors.mapping(BattleCard::getImgId, Collectors.toList())));
        player.getStatistics().setInitialTypeMapCards(cardGroupByType);
        shuffleDrawCards(player.getDrawCards());
        return player;
    }

    /**
     * 获取玩家的战斗神仙
     *
     * @param uid
     * @return
     */
    private CombatGodEnum godAtkEffect(long uid) {
        Optional<UserGod> god = this.godService.getAttachGod(gameUserService.getGameUser(uid));
        if (!god.isPresent()) {
            return null;
        }
        int attachGodId = god.get().getBaseId();
        return CombatGodEnum.fromValue(attachGodId);
    }

    /**
     * 添加主角卡
     *
     * @param player
     * @param type
     * @param oppId
     */
    private void buildLeaderCard(Player player, FightTypeEnum type, long oppId) {
        if (!FightTypeEnum.pveUseLeaderCard(type)) {
            return;
        }
        if (player.getUid() <= 0) {
            return;
        }
        Optional<UserLeaderCard> leaderCardOp = leaderCardService.getUserLeaderCardOp(player.getUid());
        if (!leaderCardOp.isPresent()) {
            return;
        }
        /**
         * ③　当玩家身处梦魇世界进行练兵时，如果对手已激活分身卡，则对手卡组将加入分身卡，
         *   如果对手未激活分身卡，则对手卡组将不会有分身卡。
         */
        if (!gameUserService.getGameUser(player.getUid()).getStatus().ifNotInFsdlWorld()) {
            return;
        }
        if (FightTypeEnum.TRAINING.equals(type) && oppId > 0 && !leaderCardService.getUserLeaderCardOp(oppId).isPresent()) {
            return;
        }
        UserLeaderCard leaderCard = leaderCardOp.get();
        BattleCard card = initBattleCard(CCardParam.getInstance(leaderCard), 1);

        int beginPos = PositionService.getDrawCardsBeginPos(player.getId());
        card.setPos(player.getDrawCards().size() + beginPos + 1);
        List<Integer> skills = userLeaderBeastService.getSkills(player.getUid());
        skills.add(CombatSkillEnum.LEADER_CARD_EXP.getValue());
        List<BattleSkill> extraSkills = new ArrayList<>();
        for (Integer skill : skills) {
            if (skill == null || skill == 0 || card.hasSkill(skill)) {
                continue;
            }
            Optional<CfgCardSkill> optional = CardSkillTool.getCardSkillOpById(skill);
            if (optional.isPresent()) {
                extraSkills.add(BattleSkill.instanceSkill(-1, optional.get()));
            }
        }
        if (ListUtil.isNotEmpty(extraSkills)) {
            card.addSkillsToFront(extraSkills);
        }
        player.updateHighHp(player.getHp() + userLeaderEquimentService.getAdditions(player.getUid()).getBlood());
        player.getDrawCards().add(0, card);
        shuffleDrawCards(player.getDrawCards());
    }

    /**
     * 处理卡牌攻防额外加成
     *
     * @param p1
     * @param ai
     */
    private void handleExtraCardRate(CPlayerInitParam p1, CPlayerInitParam ai) {
        if (p1.gainExtraCardRate() > 0) {
            p1.getCards().forEach(card -> {
                int atk = (int) (card.getAtk() * (1 + p1.gainExtraCardRate()));
                card.setAtk(atk);
            });
        }
        if (ai.gainExtraCardRate() > 0) {
            ai.getCards().forEach(card -> {
                int atk = (int) (card.getAtk() * (1 + ai.gainExtraCardRate()));
                card.setAtk(atk);
            });
        }
    }

    /**
     * 处理虾兵蟹将
     *
     * @param p1
     * @param godEnum
     * @param pveParam
     */
    private void doAsXBAndXJ(CPlayerInitParam p1, CombatGodEnum godEnum, CombatPVEParam pveParam) {
        if (!FightTypeEnum.useGodEffect(pveParam.getFightType())) {
            return;
        }
        if (null == godEnum) {
            return;
        }

        if (godEnum.getValue() == CombatGodEnum.XB.getValue()) {
            //虾兵 降低卡牌基础攻击力50;
            p1.getCards().forEach(p -> {
                p.setAtk(p.getAtk() - 50);
            });
        } else if (godEnum.getValue() == CombatGodEnum.XJ.getValue()) {
            //蟹将 降低卡牌基础攻击50%
            p1.getCards().forEach(p -> {
                p.setAtk(getInt(p.getAtk() * 0.5));
            });
        }
    }

    /**
     * 处理五行之气相生相克的效果
     *
     * @param p1
     * @param ai
     * @param pveParam
     */
    private void doAsWXZQ(Player p1, Player ai, GameUser gu, CombatPVEParam pveParam) {
        if (!gu.getStatus().ifInTransmigrateWord()) {
            return;
        }
        FightTypeEnum fightType = FightTypeEnum.fromValue(pveParam.getFightType());
        if (fightType != FightTypeEnum.TRANSMIGRATION_FIGHT && fightType != FightTypeEnum.YG) {
            return;
        }
        int sgId = gameUserService.getActiveGid(p1.getUid());
        GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(sgId);
        if (null == curTransmigration) {
            return;
        }
        int areaType = 0;
        if (fightType == FightTypeEnum.TRANSMIGRATION_FIGHT) {
            areaType = curTransmigration.gainCityAreaType(pveParam.getCityBaseId());
        } else {
            int originalCountry = CityTool.getCityById(pveParam.getCityBaseId()).getCountry();
            areaType = curTransmigration.getMainCityDefenderTypes().get(originalCountry / 10 - 1);
        }

        int finalAreaType = areaType;
        p1.getDrawCards().forEach(p -> doWXZQ(p, finalAreaType));
        ai.getDrawCards().forEach(p -> doWXZQ(p, finalAreaType));

    }

    /**
     * 对单张卡进行五行之气的攻防处理
     *
     * @param p
     * @param areaType
     */
    private void doWXZQ(BattleCard p, int areaType) {
        //相生类型
        TypeEnum xiangShengType = TypeEnum.getXiangShengType(areaType);
        //相克属性降低30%
        TypeEnum counterattackType = TypeEnum.getCounterattackType(areaType);
        double rate = 1;
        if (p.getType() == xiangShengType) {
            rate = 1.2;
        } else if (p.getType() == counterattackType) {
            rate = 0.7;
        }
        if (rate != 1) {
            p.setInitAtk((int) (p.getInitAtk() * rate));
            p.setRoundAtk((int) (p.getRoundAtk() * rate));
            p.setAtk((int) (p.getAtk() * rate));

            p.setInitHp((int) (p.getInitHp() * rate));
            p.setRoundHp((int) (p.getRoundHp() * rate));
            p.setHp((int) (p.getHp() * rate));
        }
    }

}