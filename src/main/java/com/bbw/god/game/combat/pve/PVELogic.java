package com.bbw.god.game.combat.pve;

import com.alibaba.fastjson.JSONArray;
import com.bbw.App;
import com.bbw.common.JSONUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.processor.cardboost.CardBoostProcessor;
import com.bbw.god.activity.processor.cardboost.UserBoostCards;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.city.chengc.NightmareLogic;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianLogic;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.fight.processor.AbstractFightProcessor;
import com.bbw.god.fight.processor.FightProcessorFactory;
import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.RoundService;
import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPCardGroup;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.data.weapon.Weapon;
import com.bbw.god.game.combat.event.CombatEventPublisher;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.combat.video.CombatVideo;
import com.bbw.god.game.combat.video.service.CombatVideoService;
import com.bbw.god.game.combat.weapon.WeaponLogic;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.transmigration.TransmigrationCardGroupLogic;
import com.bbw.god.game.zxz.service.ZxzService;
import com.bbw.god.game.zxz.service.foursaints.UserZxzFourSaintsService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.CardGroupWay;
import com.bbw.god.gameuser.card.UserCardGroupLogic;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.pay.UserPayInfoService;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.yaozu.ArriveYaoZuCache;
import com.bbw.god.gameuser.yaozu.YaoZuLogic;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lwb
 * @date 2020/4/15 16:01
 */
@Service
@Slf4j
public class PVELogic {
    @Qualifier("combatPVEInitService")
    @Autowired
    private CombatPVEInitService combatInitService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private CombatRedisService combatService;
    @Autowired
    private RoundService roundService;
    @Autowired
    private CombatVideoService videoService;
    @Autowired
    private WeaponLogic weaponLogic;
    @Autowired
    private App app;
    @Autowired
    private BattleCardService battleCardService;
    @Autowired
    private CombatRunesPerformService runesPerformService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private PVEResultService pveResultService;
    @Autowired
    private FightProcessorFactory fightProcessorFactory;
    @Autowired
    private NightmareLogic nightmareLogic;
    @Autowired
    private TransmigrationCardGroupLogic transmigrationCardGroupLogic;
    @Autowired
    private UserPayInfoService userPayInfoService;
    @Autowired
    private NightmareMiXianLogic miXianLogic;
    @Autowired
    private UserCardGroupLogic userCardGroupLogic;
    @Autowired
    private CardBoostProcessor cardBoostProcessor;
    @Autowired
    private YaoZuLogic yaoZuLogic;
    @Autowired
    private PVEResultLogService pveResultLogService;
    @Autowired
    private ZxzService zxzService;
    @Autowired
    private UserZxzFourSaintsService zxzFourSaintsService;

    /**
     * ???????????????
     *
     * @param fightType
     * @param myUid
     * @param opponentId
     * @param fightAgain
     * @return
     */
    public RDCombat initFightData(int fightType, long myUid, long opponentId, boolean fightAgain) {
        FightTypeEnum type = FightTypeEnum.fromValue(fightType);
        CPlayerInitParam myParam = getMyFightsInfo(type, myUid, opponentId);
        CombatPVEParam pveParam = getInitPVEParam(type, myUid, opponentId, fightAgain);
        if (app.runAsDev() && fightType == FightTypeEnum.TRAINING.getValue()) {
            RDFightsInfo fightsInfo = getMyCardGroup2(myUid);
            //?????????????????????
            CPlayerInitParam aiInitParam = getCPlayerInitParam(fightsInfo);
            pveParam.setAiPlayer(aiInitParam);
            pveParam.getAiPlayer().setUid(-1L);
        }
        if (app.runAsDev()) {
            //?????????????????????
            if (ListUtil.isEmpty(pveParam.getAiPlayer().getBuffs())) {
                JSONArray buffs = TimeLimitCacheUtil.getFromCache(myUid, type.getValue() + "entry", JSONArray.class);
                if (null != buffs) {
                    List<CombatBuff> combatBuffs = JSONUtil.fromJsonArray(buffs.toJSONString(), CombatBuff.class);
                    pveParam.getAiPlayer().setBuffs(combatBuffs);
                }
            }
        }
        Combat combat = combatInitService.initCombatPVE(myParam, pveParam.getAiPlayer(), pveParam);
        //???????????????????????????
        runesPerformService.runInitCombatRunes(combat.getFirstPlayer(), combat.getSecondPlayer(), combat.getId());
        battleCardService.firstMoveDrawCardsToHand(combat.getFirstPlayer());
        battleCardService.firstMoveDrawCardsToHand(combat.getSecondPlayer());
        runesPerformService.runInitRoundRunes(combat.getFirstPlayer(), combat.getSecondPlayer(), combat.getId());
        combatService.save(combat);
        RDCombat rdc = RDCombat.fromCombat(combat);
        videoService.addRoundData(combat, 0);
        return rdc;
    }

    /**
     * ??????
     *
     * @param combatId
     * @param myuid
     * @param moveToPlaying
     * @return
     */
    public RDCombat rapidStrike(long combatId, long myuid, String moveToPlaying) {
        Date szk = userPayInfoService.getUserPayInfo(myuid).getEndFightBuyTime();
        if (szk == null) {
            throw new ExceptionForClientTip("combat.szk.not.buy");
        }
        Combat combat = combatService.get(combatId);
        if (combat == null) {
            RDCombat rd = new RDCombat();
            //?????????????????????????????????????????????????????? ??????????????????
            rd.setRes(10010);
            return rd;
        }
        if (combat.hadEnded()) {
            pveResultService.takePVEResult(combat, myuid);
            RDCombat rdc = RDCombat.fromCombat(combat);
            return rdc;
        }
        for (int i = 0; i < 30; i++) {
            runRound(combat, 1, moveToPlaying);
            moveToPlaying = "";
            if (combat.hadEnded()) {
                break;
            }
        }
        combat.setSkip(true);
        if (combat.hadEnded()) {
            //??????
            pveResultService.takePVEResult(combat, myuid);
        }
        combatService.save(combat);
        RDCombat res = RDCombat.fromCombat(combat);
        return res;
    }

    /**
     * ????????????
     */
    public RDCombat nextRound(long combatId, long myuid, int autoDeploy, String moveToPlaying, Integer round) {
        Combat combat = combatService.get(combatId);
        if (combat == null) {
            RDCombat rd = new RDCombat();
            //?????????????????????????????????????????????????????? ??????????????????
            rd.setRes(10010);
            return rd;
        }
        if (round != combat.getRound()) {
            RDCombat rd = new RDCombat();
            //???????????????????????????????????????????????????????????????
            rd.setRes(10086);
            return rd;
        }
        if (combat.hadEnded()) {
            pveResultService.takePVEResult(combat, myuid);
            RDCombat rdc = RDCombat.fromCombat(combat);
            return rdc;
        }
        runRound(combat, autoDeploy, moveToPlaying);
        if (combat.hadEnded()) {
            //??????
            pveResultService.takePVEResult(combat, myuid);
        }
        if (autoDeploy == 1) {
            combat.setAuto(true);
        }
        combatService.save(combat);
        RDCombat rdc = RDCombat.fromCombat(combat);
        return rdc;
    }

    /**
     * ??????
     *
     * @param combatId
     * @param uid
     */
    public RDTempResult surrender(long combatId, long uid) {
        Combat combat = combatService.get(combatId);
        if (combat == null) {
            RDTempResult rd = new RDTempResult();
            //?????????????????????????????????????????????????????? ??????????????????
            rd.setRes(10010);
            return rd;
        }
        Player player = combat.getPlayerByUid(uid);
        PlayerId winner = player.getId() == PlayerId.P1 ? PlayerId.P2 : PlayerId.P1;
        combat.setWinnerId(winner.getValue());
        pveResultService.takePVEResult(combat, uid);
        combatService.save(combat);
        videoService.addSurrender(combat.getRound(), combat.getId(), player.getName());
        RDTempResult rds = new RDTempResult();
        rds.setResult(combat.getResult());
        return rds;
    }

    /**
     * ????????????
     *
     * @param combatId
     * @param wid
     * @param pos
     * @return
     */
    public RDTempResult useWeapon(long combatId, int wid, String pos) {
        Combat combat = combatService.get(combatId);
        if (combat == null) {
            RDTempResult rd = new RDTempResult();
            //?????????????????????????????????????????????????????? ??????????????????
            rd.setRes(10010);
            return rd;
        }
        List<Integer> targetPos = new ArrayList<Integer>();
        try {
            if (!pos.contains("-1")) {
                String[] poses = pos.split("N");
                for (String str : poses) {
                    targetPos.add(Integer.parseInt(str));
                }
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ExceptionForClientTip("combat.player.use.weapon.fail", wid);
        }
        RDTempResult rd = weaponLogic.useWeapon(combat, PlayerId.P1, wid, targetPos);
        combatService.save(combat);
        videoService.addRoundData(rd, combat.getId(), PlayerId.P1, wid, combat.getRound());
        return rd;
    }

    public void runRound(Combat combat, int autoDeploy, String moveToPlaying) {
        int round = combat.getRound();
        combat.getAnimationList().clear();
        //?????? ???????????????????????????????????????????????????????????????????????????????????????????????????
        weaponLogic.addInTimeWeaponAnimation(combat);
        //??????-???????????????
        roundService.deployPVE(combat, autoDeploy, moveToPlaying);
        //????????????????????????
        runesPerformService.runRoundBeginRunes(combat);
        // ?????????????????????????????????????????????????????? ???????????????
        weaponLogic.takeUsedWeaponEffect(combat);
        //????????????
        roundService.run(combat);
        //????????????
        roundService.after(combat);
        combatService.save(combat);
        videoService.addRoundData(combat, round);
    }

    /**
     * ???????????????????????????
     *
     * @param fightType
     * @param uid
     * @return
     */
    public CPlayerInitParam getMyFightsInfo(FightTypeEnum fightType, long uid, Long opponentId) {
        GameUser gu = gameUserService.getGameUser(uid);
        int playerHp = 0;//??????????????????????????????????????????????????????
        List<CCardParam> cardParams = new ArrayList<>();
        List<CombatBuff> combatBuffs = new ArrayList<>();
        if (FightTypeEnum.MXD.equals(fightType)) {
            cardParams = miXianLogic.getUserCardGroup(uid, opponentId);
        } else {
            CPCardGroup cpCardGroup = null;
            switch (fightType) {
                case ATTACK:
                    if (gu.getStatus().intoNightmareWord()) {
                        //????????????????????????
                        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
                        cpCardGroup = nightmareLogic.getUserNightmareCityCards(uid, cache.getCityId(), !cache.isAttack());
                    }
                    break;
                case TRANSMIGRATION_FIGHT:
                    ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
                    cpCardGroup = transmigrationCardGroupLogic.getCardGroup(uid, cache.getCityId());
                    break;
                case ZXZ:
                    cpCardGroup = zxzService.getZxzUserCard(uid, opponentId);
                    break;
                case ZXZ_FOUR_SAINTS:
                    cpCardGroup = zxzFourSaintsService.getZxzFourSaintsCards(uid, opponentId);
                    break;
                case YAOZU_FIGHT:
                    ArriveYaoZuCache yaoZuCache = TimeLimitCacheUtil.getArriveCache(uid, ArriveYaoZuCache.class);
                    cpCardGroup = yaoZuLogic.getUserYaoZuCards(uid, yaoZuCache);
                    break;
                default:
            }
            if (ListUtil.isEmpty(cardParams)) {
                if (cpCardGroup == null) {
                    cpCardGroup = userCardService.getFightingCardGroup(uid);
                }
                playerHp = cpCardGroup.getHp();
                cardParams = cpCardGroup.getCards();
                combatBuffs = cpCardGroup.getBuffs();
            }
        }
        //??????10?????????
        if (cardBoostProcessor.getRemainTime(uid) > 0) {
            UserBoostCards bootCards = gameUserService.getSingleItem(uid, UserBoostCards.class);
            if (null != bootCards) {
                for (CCardParam cardParam : cardParams) {
                    if (cardParam.getLv() < 10 && bootCards.ifBoostCard(cardParam.getId())) {
                        cardParam.setLv(10);
                    }
                }
            }
        }
        List<Weapon> weapons = new ArrayList<>();
        if (!FightTypeEnum.FST.equals(fightType)) {
            List<UserTreasure> userTreasures = userTreasureService.getFightTreasures(uid);
            for (UserTreasure t : userTreasures) {
                weapons.add(new Weapon(t.getBaseId(), t.gainTotalNum()));
            }
        }
        CPlayerInitParam playerInitParam = CPlayerInitParam.initParam(gu, cardParams, combatBuffs, weapons);
        playerInitParam.setHp(playerHp);
        return playerInitParam;
    }

    private RDFightsInfo getMyCardGroup2(long uid) {
        //?????????????????? ????????????????????????
        GameUser gameUser = gameUserService.getGameUser(uid);
        RDFightsInfo info = RDFightsInfo.instance(gameUser, userCardService.getGroupCards(uid, CardGroupWay.Normal_Fight.getValue(), 2));
        info.setNickname("???????????????");
        info.setCardFromUid(-1L);
        return info;
    }

    /**
     * ??????PVE?????????????????????
     *
     * @param type
     * @param uid
     * @param opponentId
     * @param fightAgain
     * @return
     */
    public CombatPVEParam getInitPVEParam(FightTypeEnum type, long uid, long opponentId, boolean fightAgain) {
        AbstractFightProcessor processor = fightProcessorFactory.makeFightProcessor(type);
        CombatPVEParam param = processor.getOpponentInfo(uid, opponentId, fightAgain);
        if (param == null) {
            log.error("?????????????????????");
            log.error("?????????????????????????????????????????????" + type);
            log.error("???????????????????????????????????????ID???" + uid + "??????ID???" + opponentId);
        }
        param.setFightType(type.getValue());
        if (param.getOpponentId() == null || param.getOpponentId() == -1) {
            param.setOpponentId(opponentId);
        }
        param.setFightAgain(fightAgain);
        return param;
    }

    public CPlayerInitParam getCPlayerInitParam(RDFightsInfo info) {
        CPlayerInitParam param = new CPlayerInitParam();
        if (info.getNickname() == null) {
            int baseId = info.getCards().get(0).getBaseId();
            CfgCardEntity cardEntity = CardTool.getCardById(baseId);
            info.setNickname(cardEntity.getName());
            info.setHead(cardEntity.getId());
        }
        if (info.getCardFromUid() != null) {
            param.setUid(info.getCardFromUid());
        }
        param.setNickname(info.getNickname());
        param.setLv(info.getLevel());
        param.setHeadImg(info.getHead());
        param.setHeadIcon(info.getHeadIcon());
        List<CCardParam> cardParams = new ArrayList<>();
        if (info.getCards() != null) {
            for (RDFightsInfo.RDFightCard card : info.getCards()) {
                cardParams.add(CCardParam.init(card.getBaseId(), card.getLevel(), card.getHierarchy(), card.getStrengthenInfo()));
            }
        }
        param.setCards(cardParams);
        param.setCardFromUid(info.getCardFromUid());
        if (info.getBlood() != null && info.getBlood() > 0) {
            param.setHp(info.getBlood());
        }
        return param;
    }

    /**
     * ???????????????
     *
     * @param combat
     * @param video
     */
    public void fstFight(Combat combat, CombatVideo video, boolean isGameFst) {
        wanXianFight(combat, video);
        pveResultService.checkCombatAchievement(combat);
        pveResultService.checkResultEvent(combat);
        FightSubmitParam param = pveResultService.getFightSubmitParam(combat, null);
        GameUser gameUser = gameUserService.getGameUser(combat.getP1().getUid());
        EPFightEnd end = EPFightEnd.instance(gameUser.getId(), gameUser.getLocation().getPosition(), FightTypeEnum.FST, param.getWin() == 1, param, new RDCommon());
        if (param.getWin() == 1) {
            CombatEventPublisher.pubWinEvent(end);
        } else {
            CombatEventPublisher.pubFailEvent(end);
        }
        pveResultLogService.logPVEResultForFst(combat, isGameFst);
    }

    /**
     * ???????????????
     *
     * @param combat
     * @param video
     */
    public void wanXianFight(Combat combat, CombatVideo video) {
        combat.getFirstPlayer().resetPos();
        combat.getSecondPlayer().resetPos();
        if (video != null) {
            RDCombat rdCombat = RDCombat.fromCombat(combat);
            video.addRoundData(rdCombat, 0);
        }
        for (int i = 0; i < 30; i++) {
            int round = combat.getRound();
            combat.getAnimationList().clear();
            //??????
            roundService.deployPVE(combat, 1, "");
            //???????????????????????????????????????
            runesPerformService.runRoundBeginRunes(combat);
            //????????????
            roundService.run(combat);
            //????????????
            roundService.after(combat);
            //??????
            if (video != null) {
                RDCombat rdCombat = RDCombat.fromCombat(combat);
                video.addRoundData(rdCombat, round);
            }
            if (combat.hadEnded()) {
                break;
            }
        }
    }

}
