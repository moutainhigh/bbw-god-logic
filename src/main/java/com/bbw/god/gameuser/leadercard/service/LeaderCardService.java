package com.bbw.god.gameuser.leadercard.service;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardHvTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.wanxianzhen.WanXianCard;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.RDCardStrengthen;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.card.event.EPCardLingShi;
import com.bbw.god.gameuser.leadercard.*;
import com.bbw.god.gameuser.leadercard.beast.UserLeaderBeastService;
import com.bbw.god.gameuser.leadercard.equipment.FightAddition;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquimentService;
import com.bbw.god.gameuser.leadercard.event.EPLeaderCardUpHv;
import com.bbw.god.gameuser.leadercard.event.LeaderCardEventPublisher;
import com.bbw.god.gameuser.leadercard.skil.LeaderCardSkillTreeService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author???lwb
 * @date: 2021/3/22 13:59
 * @version: 1.0
 */
@Service
public class LeaderCardService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserSpecialService userSpecialService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserLeaderEquimentService userLeaderEquimentService;
    @Autowired
    private UserLeaderBeastService userLeaderBeastService;
    @Autowired
    private LeaderCardSkillTreeService leaderCardSkillTreeService;

    private static final int[] SYNTHESIS_CONDITION = {TreasureEnum.SHEN_WU_BAI_HU.getValue(), TreasureEnum.SHEN_WU_QING_LONG.getValue()
            , TreasureEnum.SHEN_WU_ZHU_QUE.getValue(), TreasureEnum.SHEN_WU_XUAN_WU.getValue(), TreasureEnum.SHEN_WU_QI_LIN.getValue()};

    /**
     * ????????????
     */
    private static final int UPLV_NEED_LING_SHI = 30;

    /**
     * ?????????????????????????????????
     *
     * @param uid
     * @return
     */
    public boolean showIcon(long uid) {
        Optional<UserLeaderCard> op = getUserLeaderCardOp(uid);
        if (op.isPresent()) {
            return false;
        }
        for (int id : SYNTHESIS_CONDITION) {
            if (TreasureChecker.hasTreasure(uid, id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ???????????????
     *
     * @param uid
     * @return
     */
    public RDLeaderCard synthesis(Long uid) {
        if (!isAccordWithSynthesisCondition(uid)) {
            throw new ExceptionForClientTip("leader.card.not.accord.with.synthesis.condition");
        }
        if (getUserLeaderCardOp(uid).isPresent()) {
            throw new ExceptionForClientTip("leader.card.own");
        }
        RDLeaderCard rd = new RDLeaderCard();
        //???????????????
        GameUser user = gameUserService.getGameUser(uid);
        UserLeaderCard leaderCard = UserLeaderCard.getInstance(user);
        for (int id : SYNTHESIS_CONDITION) {
            TreasureEventPublisher.pubTDeductEvent(uid, id, 1, WayEnum.LEADER_CARD_SYNTHESIS, rd);
        }
        synchronized (uid) {
            Optional<UserLeaderCard> op = getUserLeaderCardOp(uid);
            if (op.isPresent()) {
                leaderCard = op.get();
            } else {
                gameUserService.addItem(uid, leaderCard);
            }
        }
        RDLeaderCardInfo cardInfo = RDLeaderCardInfo.getInstance(leaderCard);
        rd.setInitSkills(LeaderCardTool.getInitSkillIds());
        rd.setCardInfo(cardInfo);
        leaderCardSkillTreeService.addInitExclusiveSkill(uid, LeaderCardTool.getCfgLeaderCard().getInitSkill0());
        return rd;
    }


    /**
     * ???????????????????????????
     *
     * @param uid
     * @return
     */
    public RDLeaderCard getRandomSkill(long uid) {
        CacheLeaderCard cache = TimeLimitCacheUtil.getLeaderCardCache(uid);
        if (cache == null) {
            cache = new CacheLeaderCard();
        }
        int needGold = LeaderCardTool.getRandomSKillNeedGold(cache.getRandomTimes() + 1);
        RDLeaderCard rd = new RDLeaderCard();
        if (needGold > 0) {
            ResChecker.checkGold(gameUserService.getGameUser(uid), needGold);
            ResEventPublisher.pubGoldDeductEvent(uid, needGold, WayEnum.LEADER_CARD_SYNTHESIS, rd);
        }
        int initSkill = randomInitSkill(cache.getRandomTimes() > 2);
        cache.setLastRandomSkill(initSkill);
        cache.setRandomTimes(cache.getRandomTimes() + 1);
        TimeLimitCacheUtil.setLeaderCardCache(uid, cache);
        rd.setSkill(initSkill);
        if (cache.getRandomTimes() == 1) {
            setInitRandomSkill(uid);
        }
        return rd;
    }

    /**
     * ???????????????????????????
     *
     * @param uid
     * @return
     */
    public RDLeaderCard setInitRandomSkill(long uid) {
        CacheLeaderCard cache = TimeLimitCacheUtil.getLeaderCardCache(uid);
        if (cache == null || cache.getLastRandomSkill() == 0) {
            throw new ExceptionForClientTip("leader.not.cache.skill");
        }
        UserLeaderCard leaderCard = getUserLeaderCard(uid);
        leaderCard.instanceSkillGroups(cache.getLastRandomSkill());
        gameUserService.updateItem(leaderCard);
        leaderCardSkillTreeService.addInitExclusiveSkill(uid, cache.getLastRandomSkill());
        return new RDLeaderCard();
    }

    /**
     * ????????????
     *
     * @param uid
     * @return
     */
    public RDLeaderCard getMainPageInfo(long uid) {
        UserLeaderCard leaderCard = getUserLeaderCard(uid);
        RDLeaderCard rd = RDLeaderCard.getInstance(leaderCard);
        //?????? ???????????????????????????
        rd.setEquips(userLeaderEquimentService.getTakedEquipments(uid));
        rd.setBeasts(userLeaderBeastService.getTakedBeasts(uid));
        return rd;
    }

    /**
     * ??????????????????
     *
     * @param uid
     * @return
     */
    public RDLeaderCard getUpLvInfo(long uid) {
        UserLeaderCard leaderCard = getUserLeaderCard(uid);
        RDLeaderCard rd = new RDLeaderCard();
        //????????????
        long baseExp = LeaderCardTool.getLvNeedExp(leaderCard.getLv());
        rd.setExp(leaderCard.getExp() - baseExp);
        long lvNeedExp = LeaderCardTool.getLvNeedExp(leaderCard.getLv() + 1);
        rd.setNextLvExp(lvNeedExp - baseExp);
        //???????????????????????????
        rd.setAddAtkPoint(leaderCard.getAddAtkPoint());
        rd.setAddHpPoint(leaderCard.getAddHpPoint());
        //?????????????????????????????????
        rd.setAddHpAtkVal(leaderCard.settlePointAddVal());
        //????????????
        rd.setLeaderCardPoint(leaderCard.settleFreePoint());
        //???????????????????????? ????????? ???????????????????????????????????????
        rd.setHp(leaderCard.settleTotalHpWithoutEquip());
        rd.setAtk(leaderCard.settleTotalAtkWithoutEquip());
        return rd;
    }

    /**
     * ??????????????????
     *
     * @param uid
     * @return
     */
    public RDLeaderCard restAddPoint(long uid) {
        UserLeaderCard leaderCard = getUserLeaderCard(uid);
        ResChecker.checkGold(gameUserService.getGameUser(uid), 300);
        if (leaderCard.getAddAtkPoint() + leaderCard.getAddHpPoint() == 0) {
            //????????????
            throw new ExceptionForClientTip("leader.not.need.reset");
        }
        RDLeaderCard rd = new RDLeaderCard();
        ResEventPublisher.pubGoldDeductEvent(uid, 300, WayEnum.LEADER_CARD_RESET, rd);
        leaderCard.resetPoint();
        gameUserService.updateItem(leaderCard);
        //???????????????????????????
        rd.setAddAtkPoint(leaderCard.getAddAtkPoint());
        rd.setAddHpPoint(leaderCard.getAddHpPoint());
        //?????????????????????????????????
        rd.setAddHpAtkVal(leaderCard.settlePointAddVal());
        //????????????
        rd.setLeaderCardPoint(leaderCard.settleFreePoint());
        //???????????????????????? ????????? ???????????????????????????????????????
        rd.setHp(leaderCard.settleTotalHpWithoutEquip());
        rd.setAtk(leaderCard.settleTotalAtkWithoutEquip());
        return rd;
    }

    /**
     * ????????????????????? 1???????????????????????? 2???????????????????????????????????????2???
     *
     * @param atk ????????????
     * @param hp  ????????????
     * @return
     */
    public RDLeaderCard addPoint(long uid, Integer atk, Integer hp) {
        RDLeaderCard rd = new RDLeaderCard();
        atk = atk == null || atk < 0 ? 0 : atk;
        hp = hp == null || hp < 0 ? 0 : hp;
        UserLeaderCard leaderCard = getUserLeaderCard(uid);
        int freePoint = leaderCard.settleFreePoint();
        if (freePoint < (hp + atk)) {
            //????????????
            throw new ExceptionForClientTip("leader.not.enough.point");
        }
        int tempAtk = leaderCard.getAddAtkPoint() + atk;
        int tempHp = leaderCard.getAddHpPoint() + hp;
        if (Math.abs(tempAtk - tempHp) > 1 && (tempAtk * 2 < tempHp || tempHp * 2 < tempAtk)) {
            //???2???
            throw new ExceptionForClientTip("leader.cant.add.point");
        }
        leaderCard.setAddAtkPoint(leaderCard.getAddAtkPoint() + atk);
        leaderCard.setAddHpPoint(leaderCard.getAddHpPoint() + hp);
        gameUserService.updateItem(leaderCard);
        rd.setDeductedLeaderCardPoint(-Math.abs((atk + hp)));
        return rd;
    }

    /**
     * ?????????????????????????????????
     *
     * @param uid
     * @return
     */
    public RDLeaderCard getHvInfo(long uid) {
        UserLeaderCard leaderCard = getUserLeaderCard(uid);
        RDLeaderCard rd = new RDLeaderCard();
        if (leaderCard.ifNeedBreach()) {
            rd.setNeedBreach(1);
            rd.setNeedConsume(CardHvTool.getNeededLingshiForUpdate(leaderCard.getHv()));
        } else {
            CfgLeaderCard.UpHvCondition condition = LeaderCardTool.getCurrentConditionByHv(leaderCard.getHv());
            rd.setNeedBreach(0);
            rd.setNeedConsume(condition.getConsume());
        }

        rd.setHvExtraAddition(leaderCard.settleHvTotalAddition());
        CfgLeaderCard cfgLeaderCard = LeaderCardTool.getCfgLeaderCard();
        CfgLeaderCard.UpHvCondition condition = LeaderCardTool.getCurrentConditionByHv(Math.min(cfgLeaderCard.getTopLimitHv(), leaderCard.getHv()));
        rd.setNextHvExtraAddition(condition.getTopLimit());
        rd.setHv(leaderCard.getHv());
        int baseLimit = LeaderCardTool.getCurrentAddTopLimitByHv(leaderCard.getHv() - 1);
        rd.setNextHvExtraAddition(condition.getTopLimit() - baseLimit);
        rd.setCurrentExtraAddition(rd.getHvExtraAddition() - baseLimit - cfgLeaderCard.getInitBaseHvAddition());
        return rd;
    }

    /**
     * ??????
     *
     * @param uid
     * @return
     */
    public RDLeaderCard upHv(long uid) {
        UserLeaderCard leaderCard = getUserLeaderCard(uid);
        RDLeaderCard rd = new RDLeaderCard();
        CfgLeaderCard cfgLeaderCard = LeaderCardTool.getCfgLeaderCard();
        if (cfgLeaderCard.getTopLimitHv() <= leaderCard.getHv()) {
            //????????????
            CfgLeaderCard.UpHvCondition conditionByMaxHv = LeaderCardTool.getCurrentConditionByHv(cfgLeaderCard.getTopLimitHv());
            if (leaderCard.getHvExtraAddition() >= conditionByMaxHv.getTopLimit()) {
                throw new ExceptionForClientTip("leader.card.max.hv");
            }
        }
        if (leaderCard.ifNeedBreach()) {
            upHvWithBreach(leaderCard, rd);
        } else {
            upHvWithFoster(leaderCard, rd);
        }
        rd.setHvExtraAddition(leaderCard.settleHvTotalAddition());
        CfgLeaderCard.UpHvCondition condition = LeaderCardTool.getCurrentConditionByHv(Math.min(cfgLeaderCard.getTopLimitHv(), leaderCard.getHv()));
        int baseLimit = LeaderCardTool.getCurrentAddTopLimitByHv(leaderCard.getHv() - 1);
        rd.setNextHvExtraAddition(condition.getTopLimit() - baseLimit);
        rd.setCurrentExtraAddition(rd.getHvExtraAddition() - baseLimit - cfgLeaderCard.getInitBaseHvAddition());
        rd.setHv(leaderCard.getHv());
        if (leaderCard.ifNeedBreach()) {
            rd.setNeedBreach(1);
            rd.setNeedConsume(CardHvTool.getNeededLingshiForUpdate(leaderCard.getHv()));
        } else {
            rd.setNeedBreach(0);
            rd.setNeedConsume(condition.getConsume());
        }
        rd.setAtk(leaderCard.settleTotalAtkWithEquip());
        rd.setHp(leaderCard.settleTotalHpWithEquip());
        return rd;
    }

    /**
     * ??????-??????
     *
     * @return
     */
    private void upHvWithFoster(UserLeaderCard leaderCard, RDLeaderCard rd) {
        CfgLeaderCard.UpHvCondition condition = LeaderCardTool.getCurrentConditionByHv(leaderCard.getHv());
        int consume = condition.getConsume();
        long uid = leaderCard.getGameUserId();
        List<UserSpecial> ownSpecials = userSpecialService.getOwnSpecials(uid);
        List<UserSpecial> lingzhiList = null;
        if (ListUtil.isNotEmpty(ownSpecials)) {
            lingzhiList = ownSpecials.stream().filter(p -> p.getBaseId() == 47).collect(Collectors.toList());
        }
        if (ListUtil.isEmpty(lingzhiList) || lingzhiList.size() < consume) {
            throw new ExceptionForClientTip("special.not.enough", "????????????");
        }
        List<EPSpecialDeduct.SpecialInfo> specialInfos = new ArrayList<>();
        for (int i = 0; i < consume; i++) {
            UserSpecial userSpecial = lingzhiList.get(i);
            specialInfos.add(EPSpecialDeduct.SpecialInfo.getInstance(userSpecial.getId(), userSpecial.getBaseId(), 100000));
        }
        EPSpecialDeduct epSpecialDeduct = EPSpecialDeduct.instance(new BaseEventParam(uid, WayEnum.LEADER_CARD_HV, rd), specialInfos);
        SpecialEventPublisher.pubSpecialDeductEvent(epSpecialDeduct);
        leaderCard.setHvExtraAddition(leaderCard.getHvExtraAddition() + condition.getAdd());
        gameUserService.updateItem(leaderCard);
        rd.setHvExtraAddition(leaderCard.settleHvTotalAddition());
    }

    /**
     * ??????-??????
     * ????????????????????????????????????????????????????????????????????????????????????
     *
     * @return
     */
    private void upHvWithBreach(UserLeaderCard leaderCard, RDLeaderCard rd) {
        int hv = leaderCard.getHv();
        int need = CardHvTool.getNeededLingshiForUpdate(hv);
        long uid = leaderCard.getGameUserId();
        TreasureChecker.checkIsEnough(TreasureEnum.FEN_SHEN_LING_SHI.getValue(), need, uid);
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.FEN_SHEN_LING_SHI.getValue(), need, WayEnum.LEADER_CARD_HV, rd);
        leaderCard.setHv(leaderCard.getHv() + 1);
        gameUserService.updateItem(leaderCard);
        rd.setHv(leaderCard.getHv());
        rd.setHvExtraAddition(leaderCard.settleHvTotalAddition());
        BaseEventParam bep = new BaseEventParam(uid, WayEnum.LEADER_CARD_HIERARCHY, new RDCommon());
        EPLeaderCardUpHv ep = EPLeaderCardUpHv.instance(bep, leaderCard.getHv());
        LeaderCardEventPublisher.pubLeaderCardUpHvEvent(ep);
    }

    /**
     * ??????
     * <p>
     * ????????????????????????????????????????????????????????????????????????????????????????????????30?????????????????????30?????????????????????
     * ????????????1??????2?????????1????????????????????????????????????????????????????????????2?????????????????????????????????30??????
     *
     * @param uid
     * @param cards ????????????????????? id??????   ID1,ID2,ID3
     * @return
     */
    public RDLeaderCard upStar(long uid, String cards) {
        UserLeaderCard userLeaderCard = getUserLeaderCard(uid);
        int newStar = userLeaderCard.getStar() + 1;
        //???????????????
        TreasureChecker.checkIsEnough(TreasureEnum.SHENG_XING_SHI.getValue(), 1, uid);
        if (StrUtil.isNull(cards)) {
            //??????
            throw new ExceptionForClientTip("card.lingshi.cant.upstar");
        }
        List<Integer> list = ListUtil.parseStrToInts(cards);
        //??????????????????
        if (list.size() != 5) {
            //??????
            throw new ExceptionForClientTip("card.lingshi.cant.upstar");
        }
        List<EPCardLingShi.LingShiInfo> lingShiInfos = new ArrayList<>();
        //?????????????????? ,??????????????????,?????????????????????????????????
        for (int i = 1; i < 6; i++) {
            int cardId = 0;
            for (Integer id : list) {
                if (id % 10000 / 100 == i) {
                    cardId = id;
                    break;
                }
            }
            if (cardId == 0) {
                //????????????????????????????????????????????????????????????1
                throw new ExceptionForClientTip("card.lingshi.cant.upstar");
            }
            UserCard userCard = userCardService.getUserCard(uid, cardId);
            if (userCard == null || userCard.getLingshi() < UPLV_NEED_LING_SHI || userCard.gainCard().getStar() != newStar) {
                //??????
                throw new ExceptionForClientTip("card.lingshi.cant.upstar");
            }
            lingShiInfos.add(EPCardLingShi.LingShiInfo.getInstance(userCard.getBaseId(), UPLV_NEED_LING_SHI));
        }
        //????????????=???????????????
        RDLeaderCard rd = new RDLeaderCard();
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.SHENG_XING_SHI.getValue(), 1, WayEnum.LEADER_CARD_LV, rd);
        BaseEventParam eventParam = new BaseEventParam(uid, WayEnum.LEADER_CARD_LV, rd);
        EPCardLingShi epCardLingShi = EPCardLingShi.getInstance(lingShiInfos, eventParam);
        CardEventPublisher.pubCardLingShiDeductEvent(epCardLingShi);
        userLeaderCard.setStar(newStar);
        gameUserService.updateItem(userLeaderCard);
        rd.setStar(newStar);
        rd.setAtk(userLeaderCard.settleTotalAtkWithEquip());
        rd.setHp(userLeaderCard.settleTotalHpWithoutEquip());
        return rd;
    }

    /**
     * ?????????????????????
     *
     * @param uid
     * @return
     */
    public Optional<UserLeaderCard> getUserLeaderCardOp(long uid) {
        UserLeaderCard card = gameUserService.getSingleItem(uid, UserLeaderCard.class);
        if (card == null) {
            return Optional.empty();
        }
        return Optional.of(card);
    }

    /**
     * ????????????????????? ??????????????????????????????
     *
     * @param uid
     * @return
     */
    public UserLeaderCard getUserLeaderCard(long uid) {
        Optional<UserLeaderCard> optional = getUserLeaderCardOp(uid);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new ExceptionForClientTip("leader.card.not.own");
    }

    /**
     * ????????????????????????
     *
     * @param includeZhaoCai ???????????????
     * @return
     */
    private int randomInitSkill(boolean includeZhaoCai) {
        List<CfgLeaderCard.InitSkill> skills = LeaderCardTool.getInitSkills();
        int seed = 0;
        int skillId = 0;
        do {
            skillId = PowerRandom.getRandomFromList(skills).getSkill();
            seed++;
        } while (seed < 10 && (!includeZhaoCai && skillId == CombatSkillEnum.ZC.getValue()));
        return skillId;
    }

    /**
     * ????????????????????????
     *
     * @param uid
     * @return
     */
    private boolean isAccordWithSynthesisCondition(long uid) {
        for (int id : SYNTHESIS_CONDITION) {
            if (!TreasureChecker.hasTreasure(uid, id)) {
                return false;
            }
        }
        return true;
    }

    /**
     * ?????????????????????
     *
     * @param uid
     * @param property
     * @return
     */
    public RDLeaderCard changeProperty(long uid, int property) {
        UserLeaderCard card = getUserLeaderCard(uid);
        if (card.getOwnProperty().contains(property)) {
            card.setProperty(property);
            gameUserService.updateItem(card);
        } else {
            throw new ExceptionForClientTip("leader.card.not.own.property");
        }
        return new RDLeaderCard();
    }

    /**
     * ?????????????????????????????????
     *
     * @param uid
     * @return
     */
    public RDLeaderCard listOwnProperty(long uid) {
        UserLeaderCard card = getUserLeaderCard(uid);
        RDLeaderCard rd = new RDLeaderCard();
        rd.setOwnProperty(card.getOwnProperty());
        return rd;
    }

    /**
     * ?????????????????????
     *
     * @param uid
     * @param property
     * @return
     */
    public RDLeaderCard activeProperty(long uid, int property) {
        UserLeaderCard card = getUserLeaderCard(uid);
        if (card.getOwnProperty().contains(property)) {
            throw new ExceptionForClientTip("leader.card.property.exist");
        }
        TypeEnum.checkProperty(property);
        RDLeaderCard rd = new RDLeaderCard();
        int need = LeaderCardTool.getUnlockPropertyNeedGold();
        ResChecker.checkGold(gameUserService.getGameUser(uid), need);
        ResEventPublisher.pubGoldDeductEvent(uid, need, WayEnum.LEADER_CARD_ACTIVE_PROPERTY, rd);
        card.getOwnProperty().add(property);
        gameUserService.updateItem(card);
        rd.setOwnProperty(card.getOwnProperty());
        return rd;
    }

    /**
     * ????????????????????????????????????
     *
     * @param uid
     * @return
     */
    public RDCardStrengthen getCardFightInfo(long uid) {
        UserLeaderCard leaderCard = getUserLeaderCard(uid);
        RDCardStrengthen rd = RDCardStrengthen.getInstance(leaderCard);
        rd.setCardName(gameUserService.getGameUser(uid).getRoleInfo().getNickname());
        rd.setEquips(userLeaderEquimentService.getTakedEquipments(uid));
        rd.setBeasts(userLeaderBeastService.getTakedBeasts(uid));
        rd.setHierarchy(leaderCard.getHv());
        rd.setLevel(leaderCard.getLv());
        rd.setAtk(leaderCard.settleTotalAtkWithEquip());
        rd.setHp(leaderCard.settleTotalHpWithEquip());
        return rd;
    }

    /**
     * ????????????????????????????????????
     * 1??????????????????????????????
     * 40-??????????????????????????????=???????????????????????????????????????????????????2??????????????????+1???
     * 2????????????????????????????????????1:1???????????????????????????????????????????????????????????????2:1??????????????????
     * 3????????????
     * ????????????*???1+????????????????????????+????????????????????????*35%???+????????????=??????????????????????????????
     *
     * @param leaderCard
     * @param
     * @param
     * @return
     */
    public void updateWanXianCard(UserLeaderCard leaderCard, WanXianCard wanXianCard) {
        int freePoint = Math.max(0, wanXianCard.getLv() * 2 - leaderCard.getAddAtkPoint() - leaderCard.getAddHpPoint());
        int add = freePoint / 2;
        int addHpPoint = leaderCard.getAddHpPoint() + add;
        int addAtkPoint = leaderCard.getAddAtkPoint() + add;
        FightAddition addition = userLeaderEquimentService.getAdditions(leaderCard.getGameUserId());
        int base = leaderCard.settleBaseAtkHpWithStar();
        Double atk = base * (1 + (addAtkPoint) * 0.35) + addition.getAttack();
        Double hp = base * (1 + (addHpPoint) * 0.35) + addition.getDefence();
        wanXianCard.setInitAtk(atk.intValue());
        wanXianCard.setInitHp(hp.intValue());
        wanXianCard.setAddZhsHp(addition.getBlood());
    }

    /**
     * ????????????
     *
     * @param uid
     * @return
     */
    public RDLeaderCardSkills getLeaderSkillsGroup(long uid) {
        UserLeaderCard leaderCard = getUserLeaderCard(uid);
        int property = leaderCard.getProperty();
        RDLeaderCardSkills rd = new RDLeaderCardSkills();
        rd.setSkillsGroupInfo(leaderCard.gainSkills(property));
        return rd;
    }

    /**
     * ???????????????
     *
     * @param uid
     * @param index
     */
    public RDSuccess changeLeaderSkillsGroup(long uid, int index) {
        UserLeaderCard leaderCard = getUserLeaderCard(uid);
        int property = leaderCard.getProperty();
        UserLeaderCardSkills userLeaderCardSkills = leaderCard.gainSkills(property);
        userLeaderCardSkills.setUsingIndex(index);
        gameUserService.updateItem(leaderCard);
        RDSuccess rd = new RDSuccess();
        return rd;
    }

    /**
     * ???????????????
     *
     * @param uid
     */
    public RDCommon activationSkillsGroup(long uid) {
        UserLeaderCard leaderCard = getUserLeaderCard(uid);
        int property = leaderCard.getProperty();
        GameUser gu = gameUserService.getGameUser(uid);
        CfgLeaderCard cfgLeaderCard = LeaderCardTool.getCfgLeaderCard();
        int needPay = cfgLeaderCard.getUnlockNewSkillsGroupNeedGod();
        RDCommon rd = new RDCommon();
        //??????,??????????????????
        ResChecker.checkGold(gu, needPay);
        ResEventPublisher.pubGoldDeductEvent(uid, needPay, WayEnum.LEADER_CARD_ACTIVATION, rd);
        //????????????????????????
        int openNum = leaderCard.gainSkills(property).getSkillsGroupNum();
        if (openNum >= 4) {
            //??????????????????
            throw ExceptionForClientTip.fromi18nKey("leader.card.skills.group.num");
        }
        //???????????????
        leaderCard.activeGroupSkills();
        gameUserService.updateItem(leaderCard);
        return rd;
    }

    /**
     * ????????????????????????????????????
     *
     * @param uid
     */
    public void migrateLeaderCardSkills(long uid) {
        boolean presentLeaderCard = getUserLeaderCardOp(uid).isPresent();
        if (!presentLeaderCard) {
            return;
        }
        UserLeaderCard leaderCard = getUserLeaderCard(uid);
        Map<String, UserLeaderCardSkills> propertySkills = leaderCard.getPropertySkills();
        if (!propertySkills.isEmpty()) {
            return;
        }
        leaderCard.migrateLeaderSkills();
        leaderCard.setSkills(null);
        leaderCard.setPropertySkills(propertySkills);
        gameUserService.updateItem(leaderCard);
        return;
    }
}
