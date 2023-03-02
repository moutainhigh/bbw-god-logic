package com.bbw.god.game.sxdh;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.fight.fsfight.SxdhMatchLimitService;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.game.config.treasure.TreasureType;
import com.bbw.god.game.sxdh.config.CfgSxdh;
import com.bbw.god.game.sxdh.config.CfgSxdhMedicineEntity;
import com.bbw.god.game.sxdh.config.SxdhRankType;
import com.bbw.god.game.sxdh.config.SxdhTool;
import com.bbw.god.game.sxdh.rd.RDBeanBuy;
import com.bbw.god.game.sxdh.rd.RDBeanInfo;
import com.bbw.god.game.sxdh.rd.RDSxdhExchangeTicket;
import com.bbw.god.game.sxdh.rd.RDSxdhFighter;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.login.RDGameUser;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 神仙大会逻辑
 *
 * @author suhq
 * @date 2019-06-21 12:03:18
 */
@Slf4j
@Service
public class SxdhLogic {
    @Autowired
    private SxdhFighterService sxdhFighterService;
    @Autowired
    private SxdhZoneService sxdhZoneService;
    @Autowired
    private SxdhRankService sxdhRankService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private SxdhMechineService sxdhMechineService;
    @Autowired
    private SxdhDateService sxdhDateService;
    @Autowired
    private SxdhMatchLimitService sxdhMatchLimitService;

    private List<Integer> medicineIds = SxdhTool.getSxdh().getMedicines().stream().mapToInt(CfgSxdhMedicineEntity::getId).boxed().collect(Collectors.toList());

    /**
     * 获得玩家神仙大会信息
     *
     * @return
     */
    public RDSxdhFighter getFighterInfo(long uid) {
        RDSxdhFighter rd = new RDSxdhFighter();
        SxdhFighter fighter = sxdhFighterService.getFighter(uid);
        rd.setTicket(userTreasureService.getTreasureNum(uid, TreasureEnum.SXDH_TICKET.getValue()));
        rd.setFreeTimes(fighter.getFreeTimes());
        rd.setBean(userTreasureService.getTreasureNum(uid, TreasureEnum.XIAN_DOU.getValue()));
        rd.setMaxStreak(fighter.getMaxStreak());
        rd.setJoinTimes(fighter.getJoinTimes());
        rd.setWinTimes(fighter.getWinTimes());
        if (fighter.getJoinTimes() > 0) {
            rd.setWinRate((int) (fighter.getWinTimes() * 100.0 / fighter.getJoinTimes()));
        }


        SxdhZone sxdhZone = sxdhZoneService.getCurOrLastZone(uid);
        // 总积分排名
        int rank = sxdhRankService.getRank(sxdhZone, SxdhRankType.RANK, uid);
        int score = sxdhRankService.getScore(sxdhZone, SxdhRankType.RANK, uid);
        rd.setRank(rank);
        rd.setScore(score);
        //阶段积分排名
        CfgSxdh.SeasonPhase seasonPhase = sxdhDateService.getCurSeasonPhase();
        int phaseRank = sxdhRankService.getRank(sxdhZone, SxdhRankType.PHASE_RANK, uid);
        int phaseScore = sxdhRankService.getScore(sxdhZone, SxdhRankType.PHASE_RANK, uid);
        rd.setPhase(seasonPhase.getId());
        rd.setPhaseRank(phaseRank);
        rd.setPhaseScore(phaseScore);
        rd.setPhaseDes(seasonPhase.getDes() + "积分");
        rd.setIsDoubleScore(seasonPhase.getDoubles() > 1 ? 1 : 0);
        rd.setDailySprintAwardStatus(getSprintAwardStatus(fighter, seasonPhase).getValue());
        // 丹药
        List<UserTreasure> uTreasures = userTreasureService.getUserTreasures(uid, medicineIds);
        if (ListUtil.isNotEmpty(uTreasures)) {
            rd.setMedicine(uTreasures.stream().map(ut -> new RDGameUser.RDTreasure(ut)).collect(Collectors.toList()));
        }
        //特殊赛季信息
        if (SxdhTool.isSpecialSeason()) {
            rd.setRemainMatchTimes(sxdhMatchLimitService.getRemainMatchTimes(uid, sxdhZone));
        }
        return rd;
    }

    /**
     * 兑换门票
     *
     * @param uid        玩家ID
     * @param treasureId 消耗的法宝
     * @param num        兑换数量
     * @return
     */
    public RDSxdhExchangeTicket exchangeTicket(long uid, int treasureId, int num) {
        CfgTreasureEntity treasure = TreasureTool.getTreasureById(treasureId);
        checkEnableExchange(treasure);
        // 法宝检测
        TreasureChecker.checkIsEnough(treasureId, num, uid);

        RDSxdhExchangeTicket rd = new RDSxdhExchangeTicket();
        // 法宝扣除
        TreasureEventPublisher.pubTDeductEvent(uid, treasureId, num, WayEnum.SXDH_EXCHANGE_TICKET, rd);

        // 增加门票
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.SXDH_TICKET.getValue(), num,
                WayEnum.SXDH_EXCHANGE_TICKET, rd);

        rd.setAddedTicket(num);
        return rd;
    }

    /**
     * 获取仙豆信息
     *
     * @param uid
     * @return
     */
    public RDBeanInfo getBeanInfo(long uid) {
        RDBeanInfo rd = new RDBeanInfo();
        SxdhFighter fighter = sxdhFighterService.getFighter(uid);
        UserTreasure ut = userTreasureService.getUserTreasure(uid, TreasureEnum.XIAN_DOU.getValue());
        int totalNum = 0;
        if (ut != null) {
            totalNum = ut.gainTotalNum();
            if (ListUtil.isNotEmpty(ut.gainLimitInfosExcludeExpired())) {
                ut.getLimitInfos().sort(Comparator.comparing(UserTreasure.LimitInfo::getExpireTime));
                UserTreasure.LimitInfo limitToShow = ut.getLimitInfos().get(0);
                String expireDateInfo = DateUtil.toString(DateUtil.fromDateLong(limitToShow.getExpireTime()), "MM月dd日 HH:mm:ss");
                String expiredInfo = LM.I.getMsgByUid(uid, "sxdh.bean.expire.info", limitToShow.getTimeLimitNum(), expireDateInfo);
                rd.setExpireInfo(expiredInfo);
            }
        }
        rd.setBean(totalNum);
        rd.setLimitTimes(SxdhTool.getSxdh().getBeanBoughtLimit());
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
    public RDBeanBuy buyBean(long uid, int num) {

        SxdhFighter fighter = sxdhFighterService.getFighter(uid);
        int limit = SxdhTool.getSxdh().getBeanBoughtLimit();
        if (num > limit || (num + fighter.getBeanBoughtTimes()) > limit) {
            throw new ExceptionForClientTip("sxdh.shop.outOfLimit");
        }
        RDBeanBuy rd = new RDBeanBuy();
        int needGold = num;
        GameUser gu = gameUserService.getGameUser(uid);
        ResChecker.checkGold(gu, needGold);
        ResEventPublisher.pubGoldDeductEvent(uid, needGold, WayEnum.SXDH_BUY_BEAN, rd);
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.XIAN_DOU.getValue(), num, WayEnum.SXDH_BUY_BEAN, rd);
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
        CfgSxdhMedicineEntity medicine = SxdhTool.getMedicine(medicineId);
        GameUser gu = gameUserService.getGameUser(uid);
        int needGold = medicine.getPrice() * buyNum;
        ResChecker.checkGold(gu, needGold);
        RDCommon rd = new RDCommon();
        WayEnum way = WayEnum.SXDH_BUY_MEDICINE;
        ResEventPublisher.pubGoldDeductEvent(uid, needGold, way, rd);
        TreasureEventPublisher.pubTAddEvent(uid, medicineId, buyNum, way, rd);
        return rd;
    }

    public RDSuccess enableMechine(long uid, int roomId, int mechineId, boolean enable) {
        return sxdhMechineService.enableMechine(uid, roomId, mechineId, enable);
    }

    /**
     * 检查门票兑换
     *
     * @param treasure
     */
    private void checkEnableExchange(CfgTreasureEntity treasure) {
        // 只有战斗法宝、地图法宝、一二星灵石,诛仙令可兑换
        boolean enableExchange = treasure.getType() == TreasureType.MAP_TREASURE.getValue();
        enableExchange = enableExchange || treasure.getType() == TreasureType.FIGHT_TREASURE.getValue();
        enableExchange = enableExchange || treasure.getId() == TreasureEnum.WNLS1.getValue();
        enableExchange = enableExchange || treasure.getId() == TreasureEnum.WNLS2.getValue();
        enableExchange = enableExchange || treasure.getId() == TreasureEnum.XJZ.getValue();
        enableExchange = enableExchange || treasure.getId() == TreasureEnum.XHQ.getValue();
        enableExchange = enableExchange || treasure.getId() == TreasureEnum.WCS.getValue();
        enableExchange = enableExchange || treasure.getId() == TreasureEnum.ZXL.getValue();
        if (!enableExchange) {
            throw new ExceptionForClientTip("sxdh.ticket.unvalid.treasure");
        }
    }

    public RDCommon getSprintAward(long uid) {
        SxdhFighter sxdhFighter = sxdhFighterService.getFighter(uid);
        CfgSxdh.SeasonPhase seasonPhase = sxdhDateService.getCurSeasonPhase();
        AwardStatus awardStatus = getSprintAwardStatus(sxdhFighter, seasonPhase);
        if (awardStatus.equals(AwardStatus.UNAWARD)) {
            throw new ExceptionForClientTip("sxdh.not.sprint.award");
        }
        if (awardStatus.equals(AwardStatus.AWARDED)) {
            throw new ExceptionForClientTip("sxdh.sprint.daily.awarded");
        }
        sxdhFighter.setLastSprintAwarded(DateUtil.now());
        gameUserService.updateItem(sxdhFighter);
        RDCommon rd = new RDCommon();
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.BuSD.getValue(), 3, WayEnum.SXDH_SPRINT_AWARD, rd);
        return rd;
    }

    /**
     * 获取赛季冲刺奖励状态
     *
     * @param fighter
     * @param seasonPhase
     * @return
     */
    private AwardStatus getSprintAwardStatus(SxdhFighter fighter, CfgSxdh.SeasonPhase seasonPhase) {
        int seasonPhaseId = seasonPhase.getId();
        if (seasonPhaseId != 12929 && seasonPhaseId != 13030 && seasonPhaseId != 13131) {
            return AwardStatus.UNAWARD;
        }
        boolean isSeasonOver = sxdhZoneService.getZones().size() == 0;
        if (isSeasonOver) {
            return AwardStatus.UNAWARD;
        }
        if (fighter.getLastSprintAwarded() == null) {
            return AwardStatus.ENABLE_AWARD;
        }
        Date seasonDateEnd = sxdhDateService.getSxdhDateEnd(0);
        if (fighter.getLastSprintAwarded().getTime() < seasonDateEnd.getTime()) {
            return AwardStatus.ENABLE_AWARD;
        }
        return AwardStatus.AWARDED;
    }
}
