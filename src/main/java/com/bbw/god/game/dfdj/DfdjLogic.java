package com.bbw.god.game.dfdj;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.dfdj.config.*;
import com.bbw.god.game.dfdj.fight.DfdjFighter;
import com.bbw.god.game.dfdj.fight.DfdjFighterService;
import com.bbw.god.game.dfdj.rank.DfdjRankService;
import com.bbw.god.game.dfdj.rd.*;
import com.bbw.god.game.dfdj.zone.DfdjZone;
import com.bbw.god.game.dfdj.zone.DfdjZoneService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.CardGroupWay;
import com.bbw.god.gameuser.card.UserCardGroup;
import com.bbw.god.gameuser.card.UserCardGroupService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.login.RDGameUser;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 巅峰对决逻辑处理
 * @date 2021/1/5 14:15
 **/
@Slf4j
@Service
public class DfdjLogic {
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private DfdjZoneService dfdjZoneService;
    @Autowired
    private DfdjFighterService dfdjFighterService;
    @Autowired
    private DfdjDateService dfdjDateService;
    @Autowired
    private DfdjRankService dfdjRankService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private DfdjMedicineService dfdjMedicineService;
    @Autowired
    private UserCardGroupService userCardGroupService;

    private static final List<Integer> MEDICINE_IDS = DfdjTool.getDfdj().getMedicines().stream()
            .mapToInt(CfgDfdjMedicineEntity::getId).boxed().collect(Collectors.toList());

    /**
     * 获得玩家神仙大会信息
     *
     * @return
     */
    public RDDfdjFighter getFighterInfo(long uid) {
        RDDfdjFighter rd = new RDDfdjFighter();
        DfdjFighter fighter = dfdjFighterService.getFighter(uid);
        rd.setBean(userTreasureService.getTreasureNum(uid, TreasureEnum.GOLD_BEAN.getValue()));
        rd.setMaxStreak(fighter.getMaxStreak());
        rd.setJoinTimes(fighter.getJoinTimes());
        rd.setWinTimes(fighter.getWinTimes());
        if (fighter.getJoinTimes() > 0) {
            rd.setWinRate((int) (fighter.getWinTimes() * 100.0 / fighter.getJoinTimes()));
        }
        DfdjZone zone = dfdjZoneService.getCurOrLastZone(uid);
        // 总积分排名
        int rank = dfdjRankService.getRank(zone, DfdjRankType.RANK, uid);
        int score = dfdjRankService.getScore(zone, DfdjRankType.RANK, uid);
        rd.setRank(rank);
        rd.setScore(score);
        //阶段积分排名
        CfgDfdj.SeasonPhase seasonPhase = dfdjDateService.getCurSeasonPhase();
        int phaseRank = dfdjRankService.getRank(zone, DfdjRankType.PHASE_RANK, uid);
        int phaseScore = dfdjRankService.getScore(zone, DfdjRankType.PHASE_RANK, uid);
        rd.setPhase(seasonPhase.getId());
        rd.setPhaseRank(phaseRank);
        rd.setPhaseScore(phaseScore);
        rd.setPhaseDes(seasonPhase.getDes() + "积分");
        rd.setIsDoubleScore(seasonPhase.getDoubles() > 1 ? 1 : 0);
        rd.setDailySprintAwardStatus(getSprintAwardStatus(fighter, seasonPhase).getValue());
        // 丹药
        List<UserTreasure> uTreasures = userTreasureService.getUserTreasures(uid, MEDICINE_IDS);
        if (ListUtil.isNotEmpty(uTreasures)) {
            rd.setMedicine(uTreasures.stream().map(RDGameUser.RDTreasure::new).collect(Collectors.toList()));
        }
        // 倒计时
        CfgDfdj dfdj = DfdjTool.getDfdj();
        Integer openBeginHour = dfdj.getOpenBeginHour();
        Integer openEndHour = dfdj.getOpenEndHour();
        int hourOfDay = DateUtil.getHourOfDay(DateUtil.now());
        boolean isOpen = hourOfDay <= openBeginHour || hourOfDay >= openEndHour;
        rd.setIsOpen(isOpen);
        return rd;
    }

    /**
     * 获取仙豆信息
     *
     * @param uid
     * @return
     */
    public RDDfdjBeanInfo getBeanInfo(long uid) {
        RDDfdjBeanInfo rd = new RDDfdjBeanInfo();
        DfdjFighter fighter = dfdjFighterService.getFighter(uid);
        UserTreasure ut = userTreasureService.getUserTreasure(uid, TreasureEnum.GOLD_BEAN.getValue());
        int totalNum = 0;
        if (ut != null) {
            totalNum = ut.gainTotalNum();
            if (ListUtil.isNotEmpty(ut.gainLimitInfosExcludeExpired())) {
                ut.getLimitInfos().sort(Comparator.comparing(UserTreasure.LimitInfo::getExpireTime));
                UserTreasure.LimitInfo limitToShow = ut.getLimitInfos().get(0);
                String expireDateInfo = DateUtil.toString(DateUtil.fromDateLong(limitToShow.getExpireTime()), "MM月dd日 HH:mm:ss");
                String expiredInfo = LM.I.getMsgByUid(uid, "dfdj.bean.expire.info", limitToShow.getTimeLimitNum(), expireDateInfo);
                rd.setExpireInfo(expiredInfo);
            }
        }
        rd.setBean(totalNum);
        rd.setLimitTimes(DfdjTool.getDfdj().getBeanBoughtLimit());
        rd.setRemainTimes(rd.getLimitTimes() - fighter.getBeanBoughtTimes());
        return rd;
    }

    /**
     * 购买仙豆
     *
     * @param uid
     * @param num
     * @return
     */
    public RDDfdjBeanBuy buyBean(long uid, int num) {

        DfdjFighter fighter = dfdjFighterService.getFighter(uid);
        int limit = DfdjTool.getDfdj().getBeanBoughtLimit();
        if (num > limit || (num + fighter.getBeanBoughtTimes()) > limit) {
            throw new ExceptionForClientTip("dfdj.shop.outOfLimit");
        }
        RDDfdjBeanBuy rd = new RDDfdjBeanBuy();
        int needGold = num;
        GameUser gu = gameUserService.getGameUser(uid);
        ResChecker.checkGold(gu, needGold);
        ResEventPublisher.pubGoldDeductEvent(uid, needGold, WayEnum.DFDJ_BUY_BEAN, rd);
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.GOLD_BEAN.getValue(), num, WayEnum.DFDJ_BUY_BEAN, rd);
        fighter.addBeanBoughtTimes(num);
        gameUserService.updateItem(fighter);
        rd.setRemainTimes(limit - fighter.getBeanBoughtTimes());
        return rd;
    }

    /**
     * 购买丹药
     *
     * @param uid
     * @param medicineId
     * @param buyNum
     * @return
     */
    public RDCommon buyMedicine(long uid, int medicineId, int buyNum) {
        CfgDfdjMedicineEntity medicine = DfdjTool.getMedicine(medicineId);
        GameUser gu = gameUserService.getGameUser(uid);
        int needGold = medicine.getPrice() * buyNum;
        ResChecker.checkGold(gu, needGold);
        RDCommon rd = new RDCommon();
        WayEnum way = WayEnum.DFDJ_BUY_MEDICINE;
        ResEventPublisher.pubGoldDeductEvent(uid, needGold, way, rd);
        TreasureEventPublisher.pubTAddEvent(uid, medicineId, buyNum, way, rd);
        return rd;
    }

    public RDSuccess enableMedicine(long uid, int roomId, int mechineId, boolean enable) {
        return dfdjMedicineService.enableMedicine(uid, roomId, mechineId, enable);
    }

    public RDCommon getSprintAward(long uid) {
        DfdjFighter fighter = dfdjFighterService.getFighter(uid);
        CfgDfdj.SeasonPhase seasonPhase = dfdjDateService.getCurSeasonPhase();
        AwardStatus awardStatus = getSprintAwardStatus(fighter, seasonPhase);
        if (awardStatus.equals(AwardStatus.UNAWARD)) {
            throw new ExceptionForClientTip("dfdj.not.sprint.award");
        }
        if (awardStatus.equals(AwardStatus.AWARDED)) {
            throw new ExceptionForClientTip("dfdj.sprint.daily.awarded");
        }
        fighter.setLastSprintAwarded(DateUtil.now());
        gameUserService.updateItem(fighter);
        RDCommon rd = new RDCommon();
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.BuSD.getValue(), 3, WayEnum.DFDJ_SPRINT_AWARD, rd);
        return rd;
    }

    /**
     * 获取赛季冲刺奖励状态
     *
     * @param fighter
     * @param seasonPhase
     * @return
     */
    private AwardStatus getSprintAwardStatus(DfdjFighter fighter, CfgDfdj.SeasonPhase seasonPhase) {
        int seasonPhaseId = seasonPhase.getId();
        if (seasonPhaseId != 12929 && seasonPhaseId != 13030 && seasonPhaseId != 13131) {
            return AwardStatus.UNAWARD;
        }
        boolean isSeasonOver = dfdjZoneService.getZones().size() == 0;
        if (isSeasonOver) {
            return AwardStatus.UNAWARD;
        }
        if (fighter.getLastSprintAwarded() == null) {
            return AwardStatus.ENABLE_AWARD;
        }
        Date seasonDateEnd = dfdjDateService.getDfdjDateEnd(0);
        if (fighter.getLastSprintAwarded().getTime() < seasonDateEnd.getTime()) {
            return AwardStatus.ENABLE_AWARD;
        }
        return AwardStatus.AWARDED;
    }

    public RDCardGroup getCardGroup(long uid) {
        RDCardGroup rd = new RDCardGroup();
        UserCardGroup usingGroup = userCardGroupService.getUserCardGroups(uid, CardGroupWay.DFDJ_FIGHT).stream().findFirst().orElse(null);
        if (null == usingGroup) {
            return rd;
        }
        rd.setCardIds(usingGroup.getCards());
        return rd;
    }

    public RDSuccess setCardGroup(long uid, String cardIds) {
        String[] split = cardIds.split(",");
        List<Integer> cards = Arrays.stream(split).map(Integer::parseInt).collect(Collectors.toList());
        UserCardGroup usingGroup = userCardGroupService.getUserCardGroups(uid, CardGroupWay.DFDJ_FIGHT).stream().findFirst().orElse(null);
        if (null == usingGroup) {
            UserCardGroup group = UserCardGroup.instance(uid, CardGroupWay.DFDJ_FIGHT.getValue(), CardGroupWay.DFDJ_FIGHT, cards);
            gameUserService.addItem(uid, group);
            return new RDSuccess();
        }
        usingGroup.setCards(cards);
        gameUserService.updateItem(usingGroup);
        return new RDSuccess();
    }

    public RDDfdjFightCheck checkEligibility(long uid) {
        // 未开启
        if (!dfdjDateService.isOpenDfdj(uid)) {
            throw new ExceptionForClientTip("dfdj.not.open");
        }
        // 当前未设置卡组
        UserCardGroup group = userCardGroupService.getUserCardGroups(uid, CardGroupWay.DFDJ_FIGHT).stream().findFirst().orElse(null);
        if (null == group) {
            throw new ExceptionForClientTip("dfdj.not.card.group");
        }
        // 没加入战区
        DfdjZone zone = dfdjZoneService.getCurOrLastZone(uid);
        if (null == zone) {
            throw new ExceptionForClientTip("dfdj.zone.error");
        }
        DfdjSegment segment = dfdjRankService.getSegment(zone, DfdjRankType.PHASE_RANK, uid);
        return new RDDfdjFightCheck(zone.getZone(), segment.getValue());
    }
}
