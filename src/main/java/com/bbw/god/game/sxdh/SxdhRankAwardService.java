package com.bbw.god.game.sxdh;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.sxdh.config.CfgSxdh;
import com.bbw.god.game.sxdh.config.CfgSxdhRankAwardEntity;
import com.bbw.god.game.sxdh.config.SxdhRankType;
import com.bbw.god.game.sxdh.config.SxdhTool;
import com.bbw.god.game.sxdh.event.EPSxdhAwardSend;
import com.bbw.god.game.sxdh.event.SxdhEventPublisher;
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
 * TODO
 *
 * @author suhq
 * @date 2020-05-01 00:43
 **/
@Slf4j
@Service
public class SxdhRankAwardService {

    /**
     * 奖励保存时间
     */
    private static final int AWARD_REMAIN_DAY = 3;

    @Autowired
    private SxdhZoneService sxdhZoneService;
    @Autowired
    private SxdhRankService sxdhRankService;
    @Autowired
    private SxdhDateService sxdhDateService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 发放奖励
     *
     * @param sendDate
     */
    public void sendAward(Date sendDate) {
        List<SxdhZone> zones = sxdhZoneService.getZones(sendDate);
        for (SxdhZone zone : zones) {
            sendZoneAward(zone, sendDate);
        }
    }

    public void sendAward(int serverGroup, int zoneType, int rankType, Date sendDate) {
        List<SxdhZone> zones = sxdhZoneService.getZones(sendDate);
        SxdhZone sxdhZone = zones.stream().filter(tmp -> tmp.getServerGroup() == serverGroup && tmp.getZone() == zoneType).findFirst().orElse(null);
        if (sxdhZone == null) {
            return;
        }
        CfgSxdh.SeasonPhase seasonPhase = sxdhDateService.getSeasonPhase(sendDate);
        log.info("seasonPhase:" + seasonPhase.toString() + ",sendDate:" + DateUtil.toDateTimeString(sendDate));
        sendAward(sxdhZone, seasonPhase, SxdhRankType.fromValue(rankType));
    }

    public void sendZoneAward(SxdhZone zone, Date sendDate) {
        try {
            CfgSxdh.SeasonPhase seasonPhase = sxdhDateService.getSeasonPhase(sendDate);
            log.info("seasonPhase:" + seasonPhase.toString() + ",sendDate:" + DateUtil.toDateTimeString(sendDate));
            if (isToSendPhaseAward(sendDate, seasonPhase)) {
                log.info("发放阶段奖励");
                sendAward(zone, seasonPhase, SxdhRankType.LAST_PHASE_RANK);
            }
            if (isMiddleSeasonAwardDay()) {
                log.info("发放季中奖励");
                sendAward(zone, seasonPhase, SxdhRankType.MIDDLE_RANK);
            }
            if (isSeasonAwardDay(zone)) {
                log.info("发放赛季奖励");
                sendAward(zone, seasonPhase, SxdhRankType.RANK);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
//                throw new CoderException(e.getMessage(), ErrorLevel.HIGH);
        }
    }


    /**
     * 发放奖励
     *
     * @param zone     战区
     * @param rankType 榜单类型
     */
    private void sendAward(SxdhZone zone, CfgSxdh.SeasonPhase seasonPhase, SxdhRankType rankType) {
        log.info("开始发放战区" + zone.toString() + "[" + rankType.getName() + "][" + seasonPhase.toString() + "]的奖励");
        try {
            String title = getTitle(seasonPhase, rankType);
            List<UserMail> mailList = new ArrayList<>();
            Set<ZSetOperations.TypedTuple<Long>> allRankers = sxdhRankService.getAllRankers(zone, rankType);
            int rank = 1;
            for (ZSetOperations.TypedTuple<Long> ranker : allRankers) {
                List<UserMail> userMails = buildUserAwardMail(ranker.getValue(), title, rankType, rank);
                if (ListUtil.isNotEmpty(userMails)) {
                    mailList.addAll(userMails);
                    BaseEventParam bep = new BaseEventParam(ranker.getValue(), WayEnum.SXDH_RANK_AWARD, new RDCommon());
                    SxdhEventPublisher.pubSxdhAwardSendEvent(new EPSxdhAwardSend(zone.getSeason(), rankType, rank,
                            bep));
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
    private String getTitle(CfgSxdh.SeasonPhase seasonPhase, SxdhRankType rankType) {
        String title = "";
        switch (rankType) {
            case LAST_PHASE_RANK:
                title = String.format("神仙大会%s阶段排行", seasonPhase.getDes());
                break;
            case MIDDLE_RANK:
                title = String.format("神仙大会%s月季中排行", DateUtil.getTodayInt() / 100 % 100);
                break;
            case RANK:
                title = String.format("神仙大会%s月赛季排行", DateUtil.getTodayInt() / 100 % 100);
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
    private List<UserMail> buildUserAwardMail(long uid, String title, SxdhRankType rankType, int rank) {
        List<UserMail> userMails = new ArrayList<>();
        CfgSxdhRankAwardEntity awardEntity = SxdhTool.getSxdhRankAwardEntity(rank, rankType);
        List<Award> mailAward = awardEntity.getAwards().stream().filter(tmp -> !(tmp.getItem() == AwardEnum.FB.getValue() && tmp.getAwardId() == TreasureEnum.XIAN_DOU.getValue())).collect(Collectors.toList());
        Award xiandou = awardEntity.getAwards().stream().filter(tmp -> tmp.getItem() == AwardEnum.FB.getValue() && tmp.getAwardId() == TreasureEnum.XIAN_DOU.getValue()).findFirst().orElse(null);
        String content = LM.I.getMsgByUid(uid, "mail.sxdh.bean.send.content", title, rank, xiandou.getNum());
        UserMail userMail = null;
        if (ListUtil.isNotEmpty(mailAward)) {
            userMail = UserMail.newAwardMail(title, content, uid, mailAward, AWARD_REMAIN_DAY);
        } else {
            userMail = UserMail.newSystemMail(title, content, uid);
        }
        if (xiandou != null) {
            TreasureEventPublisher.pubTAddEvent(uid, xiandou.getAwardId(), xiandou.getNum(), WayEnum.SXDH_RANK_AWARD, new RDCommon());
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
    private boolean isToSendPhaseAward(Date sendDate, CfgSxdh.SeasonPhase seasonPhase) {

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
    public boolean isSeasonAwardDay(SxdhZone zone) {
        return DateUtil.getTodayInt() == DateUtil.toDateInt(zone.getEndDate());
    }
}
