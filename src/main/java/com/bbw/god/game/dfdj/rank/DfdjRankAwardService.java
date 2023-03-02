package com.bbw.god.game.dfdj.rank;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.dfdj.DfdjDateService;
import com.bbw.god.game.dfdj.config.CfgDfdj;
import com.bbw.god.game.dfdj.config.CfgDfdjRankAwardEntity;
import com.bbw.god.game.dfdj.config.DfdjRankType;
import com.bbw.god.game.dfdj.config.DfdjTool;
import com.bbw.god.game.dfdj.event.DfdjEventPublisher;
import com.bbw.god.game.dfdj.event.EPDfdjAwardSend;
import com.bbw.god.game.dfdj.zone.DfdjZone;
import com.bbw.god.game.dfdj.zone.DfdjZoneService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 巅峰对决排行奖励service
 * @date 2021/1/5 14:50
 **/
@Slf4j
@Service
public class DfdjRankAwardService {

    /**
     * 奖励保存时间
     */
    private static final int AWARD_REMAIN_DAY = 3;

    @Autowired
    private DfdjZoneService dfdjZoneService;
    @Autowired
    private DfdjRankService dfdjRankService;
    @Autowired
    private DfdjDateService dfdjDateService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 发放奖励
     *
     * @param sendDate
     */
    public void sendAward(Date sendDate) {
        List<DfdjZone> zones = dfdjZoneService.getZones(sendDate);
        for (DfdjZone zone : zones) {
            sendZoneAward(zone, sendDate);
        }
    }

    public void sendAward(int serverGroup, int zoneType, int rankType, Date sendDate) {
        List<DfdjZone> zones = dfdjZoneService.getZones(sendDate);
        DfdjZone zone = zones.stream().filter(tmp -> tmp.getServerGroup() == serverGroup && tmp.getZone() == zoneType).findFirst().orElse(null);
        if (zone == null) {
            return;
        }
        CfgDfdj.SeasonPhase seasonPhase = dfdjDateService.getSeasonPhase(sendDate);
        log.info("seasonPhase:" + seasonPhase.toString() + ",sendDate:" + DateUtil.toDateTimeString(sendDate));
        sendAward(zone, seasonPhase, DfdjRankType.fromValue(rankType));
    }

    public void sendZoneAward(DfdjZone zone, Date sendDate) {
        try {
            CfgDfdj.SeasonPhase seasonPhase = dfdjDateService.getSeasonPhase(sendDate);
            log.info("seasonPhase:" + seasonPhase.toString() + ",sendDate:" + DateUtil.toDateTimeString(sendDate));
            if (isToSendPhaseAward(sendDate, seasonPhase)) {
                log.info("发放阶段奖励");
                sendAward(zone, seasonPhase, DfdjRankType.LAST_PHASE_RANK);
            }
            if (isMiddleSeasonAwardDay()) {
                log.info("发放季中奖励");
                sendAward(zone, seasonPhase, DfdjRankType.MIDDLE_RANK);
            }
            if (isSeasonAwardDay(zone)) {
                log.info("发放赛季奖励");
                sendAward(zone, seasonPhase, DfdjRankType.RANK);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 发放奖励
     *
     * @param zone     战区
     * @param rankType 榜单类型
     */
    private void sendAward(DfdjZone zone, CfgDfdj.SeasonPhase seasonPhase, DfdjRankType rankType) {
        log.info("开始发放战区" + zone.toString() + "[" + rankType.getName() + "][" + seasonPhase.toString() + "]的奖励");
        try {
            String title = getTitle(seasonPhase, rankType);
            List<UserMail> mailList = new ArrayList<>();
            Set<ZSetOperations.TypedTuple<Long>> allRankers = dfdjRankService.getAllRankers(zone, rankType);
            int rank = 1;
            for (ZSetOperations.TypedTuple<Long> ranker : allRankers) {
                List<UserMail> userMails = buildUserAwardMail(ranker.getValue(), title, rankType, rank);
                if (ListUtil.isNotEmpty(userMails)) {
                    mailList.addAll(userMails);
                    BaseEventParam bep = new BaseEventParam(ranker.getValue(), WayEnum.DFDJ_RANK_AWARD, new RDCommon());
                    DfdjEventPublisher.pubDfdjAwardSendEvent(new EPDfdjAwardSend(zone.getSeason(), rankType, rank, bep));
                }
                // 发送下个阶段积分
                if (DfdjRankType.LAST_PHASE_RANK == rankType && DateUtil.getTodayInt() % 100 != 28) {
                    int score = dfdjRankService.getScore(zone, rankType, ranker.getValue());
                    int nextScore = Math.min(140, score - 50);
                    if (nextScore > 0) {
                        dfdjRankService.incrementRankValue(zone, DfdjRankType.PHASE_RANK, ranker.getValue(), nextScore);
                        dfdjRankService.incrementRankValue(zone, DfdjRankType.RANK, ranker.getValue(), nextScore);
                    }
                }
                rank++;
            }
            gameUserService.addItems(mailList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取邮件标题
     *
     * @param rankType
     * @return
     */
    private String getTitle(CfgDfdj.SeasonPhase seasonPhase, DfdjRankType rankType) {
        String title = "";
        switch (rankType) {
            case LAST_PHASE_RANK:
                title = String.format("巅峰对决%s阶段排行", seasonPhase.getDes());
                break;
            case MIDDLE_RANK:
                title = String.format("巅峰对决%s月季中排行", DateUtil.getTodayInt() / 100 % 100);
                break;
            case RANK:
                title = String.format("巅峰对决%s月赛季排行", DateUtil.getTodayInt() / 100 % 100);
                break;
            default:
                break;
        }
        return title;
    }

    /**
     * 生成玩家奖励邮件
     *
     * @param title
     * @param rankType
     * @param rank
     * @return
     */
    private List<UserMail> buildUserAwardMail(long uid, String title, DfdjRankType rankType, int rank) {
        List<UserMail> userMails = new ArrayList<>();
        CfgDfdjRankAwardEntity awardEntity = DfdjTool.getDfdjRankAwardEntity(rank, rankType);
        List<Award> mailAward = awardEntity.getAwards().stream().filter(tmp -> !(tmp.getItem() == AwardEnum.FB.getValue() && tmp.getAwardId() == TreasureEnum.GOLD_BEAN.getValue())).collect(Collectors.toList());
        Award goldBean = awardEntity.getAwards().stream().filter(tmp -> tmp.getItem() == AwardEnum.FB.getValue() && tmp.getAwardId() == TreasureEnum.GOLD_BEAN.getValue()).findFirst().orElse(null);
        String content = LM.I.getMsgByUid(uid,"mail.dfdj.rank.award.content", title, rank, goldBean.getNum());
        UserMail userMail = null;
        if (ListUtil.isNotEmpty(mailAward)) {
            userMail = UserMail.newAwardMail(title, content, uid, mailAward, AWARD_REMAIN_DAY);
        } else {
            userMail = UserMail.newSystemMail(title, content, uid);
        }
        if (goldBean != null) {
            TreasureEventPublisher.pubTAddEvent(uid, goldBean.getAwardId(), goldBean.getNum(), WayEnum.DFDJ_RANK_AWARD, new RDCommon());
        }
        userMails.add(userMail);
        return userMails;
    }

    /**
     * 是否发放赛季阶段奖励
     *
     * @param sendDate
     * @param seasonPhase
     * @return
     */
    private boolean isToSendPhaseAward(Date sendDate, CfgDfdj.SeasonPhase seasonPhase) {

        if (seasonPhase.getHasPhaseAwards() && seasonPhase.getEnd() == DateUtil.getDayOfMonth(sendDate)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是发送季中奖励的那天
     *
     * @return
     */
    private boolean isMiddleSeasonAwardDay() {
        int todayInt = DateUtil.getTodayInt();
        return 15 == todayInt % 100;
    }

    /**
     * 是否是发送赛季奖励的那天
     *
     * @return
     */
    public boolean isSeasonAwardDay(DfdjZone zone) {
        return DateUtil.getTodayInt() == DateUtil.toDateInt(zone.getEndDate());
    }
}
