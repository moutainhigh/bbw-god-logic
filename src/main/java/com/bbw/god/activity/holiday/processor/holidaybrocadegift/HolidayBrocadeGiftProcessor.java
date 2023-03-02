package com.bbw.god.activity.holiday.processor.holidaybrocadegift;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.cfg.CfgBrocadeGiftConfig;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.BrocadeGiftTool;
import com.bbw.god.activity.game.GameActivity;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDBrocadeGift;
import com.bbw.god.activity.rd.RDLanternGiftsBetInfo;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.brocadegift.BrocadeGiftDailyTaskService;
import com.bbw.god.gameuser.task.brocadegift.UserBrocadeGiftDailyTask;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 锦礼活动
 *
 * @author fzj
 * @date 2022/2/9 13:37
 */
@Service
public class HolidayBrocadeGiftProcessor extends AbstractActivityProcessor {
    @Autowired
    HolidayBrocadeGiftService holidayBrocadeGiftService;
    @Autowired
    private BrocadeGiftDailyTaskService brocadeGiftDailyTaskService;
    @Autowired
    private GameDataService gameDataService;


    public HolidayBrocadeGiftProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.LANTERN_FESTIVAL_GIFT);
    }

    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        RDBrocadeGift rd = new RDBrocadeGift();
        //获取当前轮投注信息
        List<RDBrocadeGift.RDCurrentBetInfo> currentBetInfos = new ArrayList<>();
        List<CfgBrocadeGiftConfig.TurnAndAwards> turnAndAwards = BrocadeGiftTool.getCurrentTurnAndAwards();
        if (!turnAndAwards.isEmpty()) {
            for (CfgBrocadeGiftConfig.TurnAndAwards turnAndAward : turnAndAwards) {
                RDBrocadeGift.RDCurrentBetInfo currentBetInfo = RDBrocadeGift.RDCurrentBetInfo.getInstance(turnAndAward);
                //投注号码
                UserBrocadeGift lanternGiftsInfo = holidayBrocadeGiftService.getBrocadeGiftInfo(uid, turnAndAward.getId());
                currentBetInfo.setBetNums(lanternGiftsInfo.getBetNums());
                //全服投注次数
                Integer gameBetTimes = holidayBrocadeGiftService.getGameBetTimes(turnAndAward.getTurn(), turnAndAward.getType());
                currentBetInfo.setGameTotalBetTimes(gameBetTimes);
                currentBetInfos.add(currentBetInfo);
            }
            List<RDBrocadeGift.RDCurrentBetInfo> rdCurrentBetInfos = currentBetInfos.stream()
                    .sorted(Comparator.comparing(RDBrocadeGift.RDCurrentBetInfo::getId)).collect(Collectors.toList());
            rd.setCurrentBetInfos(rdCurrentBetInfos);
        }
        //上一轮投注信息
        List<RDBrocadeGift.RDLasTurnBetInfo> lasTurnBetInfos = new ArrayList<>();
        List<CfgBrocadeGiftConfig.TurnAndAwards> turnAndAwardsByTurn = BrocadeGiftTool.getLastTurnAndAwards();
        if (!turnAndAwardsByTurn.isEmpty()) {
            for (CfgBrocadeGiftConfig.TurnAndAwards turnAndAward : turnAndAwardsByTurn) {
                RDBrocadeGift.RDLasTurnBetInfo lasTurnBetInfo = RDBrocadeGift.RDLasTurnBetInfo.getInstance(turnAndAward);
                //投注号码
                UserBrocadeGift lanternGiftsInfo = holidayBrocadeGiftService.getBrocadeGiftInfo(uid, turnAndAward.getId());
                lasTurnBetInfo.setBetNums(lanternGiftsInfo.getBetNums());
                //中奖号码
                String lotteryNum = holidayBrocadeGiftService.getAllLotteryNums(turnAndAward.getTurn(), turnAndAward.getType());
                lasTurnBetInfo.setLotteryNum(lotteryNum);
                //中奖玩家
                Map<String, Long> allTicketNum = holidayBrocadeGiftService.getAllTicketNum(turnAndAward.getTurn(), turnAndAward.getType());
                Long lotteryPlayer = allTicketNum.getOrDefault(lotteryNum, 0L);
                if (lotteryPlayer != 0L) {
                    GameUser gameUser = gameUserService.getGameUser(lotteryPlayer);
                    String lotteryNickName = ServerTool.getServerShortName(gameUser.getServerId()) + "·" + gameUser.getRoleInfo().getNickname();
                    lasTurnBetInfo.setLotteryPlayer(lotteryNickName);
                }
                //全服投注次数
                Integer gameBetTimes = holidayBrocadeGiftService.getGameBetTimes(turnAndAward.getTurn(), turnAndAward.getType());
                lasTurnBetInfo.setGameTotalBetTimes(gameBetTimes);
                lasTurnBetInfos.add(lasTurnBetInfo);
            }
            List<RDBrocadeGift.RDLasTurnBetInfo> rdLasTurnBetInfos = lasTurnBetInfos.stream()
                    .sorted(Comparator.comparing(RDBrocadeGift.RDLasTurnBetInfo::getId)).collect(Collectors.toList());
            rd.setLasTurnBetInfos(rdLasTurnBetInfos);
        }
        //处理剩余时间
        int sid = gameUserService.getActiveSid(uid);
        IActivity a = this.activityService.getActivity(sid, ActivityEnum.LANTERN_FESTIVAL_GIFT);
        if (null == a) {
            throw new ExceptionForClientTip("activity.is.timeout");
        }
        long remainTime = this.getRemainTime(uid, sid, a);
        if (remainTime != 0) {
            rd.setActivityRemainTime(remainTime);
        }
        return rd;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }

    /**
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    public boolean isOpened(int sid) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.LANTERN_FESTIVAL_GIFT.getValue());
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return a != null;
    }

    /**
     * 活动是否进行中
     *
     * @return
     */
    private boolean isOpened() {
        List<GameActivity> gameActivities = gameDataService.getGameDatas(GameActivity.class);
        GameActivity activity = gameActivities.stream()
                .filter(ga -> ga.getType() == ActivityEnum.LANTERN_FESTIVAL_GIFT.getValue())
                .max(Comparator.comparing(GameActivity::gainEnd)).orElse(null);
        if (activity == null) {
            return false;
        }
        return DateUtil.isBetweenIn(DateUtil.now(), activity.gainBegin(), activity.gainEnd());
    }

    /**
     * 用于定时延迟开奖
     *
     * @param delayMin 延迟的分钟数
     */
    public void drawPrize(int delayMin) {
        //活动是否在进行中
        if (!isOpened()) {
            return;
        }
        Date turnDate = DateUtil.addMinutes(DateUtil.now(), -delayMin);
        drawPrize(turnDate);
    }

    /**
     * 开某个时段对应的奖励
     *
     * @param turnDate 某个轮次的时间
     */
    public void drawPrize(Date turnDate) {
        //获得需要开奖的配置
        CfgBrocadeGiftConfig.TurnAndAwards turnAward = BrocadeGiftTool.getNeedDrawPrizeTurnAward(turnDate);
        //获取所有开奖号码
        Integer turn = turnAward.getTurn();
        Map<String, Long> allTicketNumList = holidayBrocadeGiftService.getAllTicketNum(turn, turnAward.getType());
        //没有获奖号码
        if (allTicketNumList.isEmpty()) {
            return;
        }
        List<String> allTicketNums = new ArrayList<>(allTicketNumList.keySet());
        String prizeTicketNum = PowerRandom.getRandomFromList(allTicketNums);
        //保存中奖号码
        holidayBrocadeGiftService.saveLotteryNum(turn, turnAward.getType(), prizeTicketNum);
        //发放奖励
        sendMailAward(allTicketNumList, prizeTicketNum, turnAward.getAwards());
    }

    /**
     * 发送邮件奖励
     *
     * @param allTicketNumList
     * @param prizeTicketNum
     * @param awards
     */
    private void sendMailAward(Map<String, Long> allTicketNumList, String prizeTicketNum, List<Award> awards) {
        //发放邮件奖励
        List<UserMail> userMails = new ArrayList<>();
        List<Long> joiners = new ArrayList<>(allTicketNumList.values()).stream().distinct().collect(Collectors.toList());
        Long joiner = joiners.get(0);
        String title = LM.I.getMsgByUid(joiner, "activity.lanternGifts.mail.title");
        String content = LM.I.getMsgByUid(joiner, "activity.lanternGifts.mail.massage");
        Map<Long, List<Map.Entry<String, Long>>> turnResult = allTicketNumList.entrySet().stream().collect(Collectors.groupingBy(Map.Entry::getValue));
        for (long uid : joiners) {
            List<Award> awardList = new ArrayList<>();
            List<Map.Entry<String, Long>> userAllTicketNums = turnResult.get(uid);
            for (Map.Entry<String, Long> ticketNum : userAllTicketNums) {
                if (ticketNum.getKey().equals(prizeTicketNum)) {
                    awardList.addAll(awards);
                    continue;
                }
                List<Award> participateAwards = BrocadeGiftTool.getRandomParticipateAwards();
                awardList.addAll(participateAwards);
            }
            UserMail userMail = UserMail.newAwardMail(title, content, uid, mergeAwards(awardList));
            userMails.add(userMail);
        }
        gameUserService.addItems(userMails);
    }

    /**
     * 合并相同奖励
     *
     * @param awards
     * @return
     */
    private List<Award> mergeAwards(List<Award> awards) {
        List<Award> awardList = new ArrayList<>();
        Map<Integer, List<Award>> awardsByItem = awards.stream().collect(Collectors.groupingBy(Award::getItem));
        for (Map.Entry<Integer, List<Award>> awardByItemList : awardsByItem.entrySet()) {
            AwardEnum awardEnum = AwardEnum.fromValue(awardByItemList.getKey());
            Map<Integer, Integer> boxAwards = awardByItemList.getValue().stream()
                    .collect(Collectors.groupingBy(Award::getAwardId, Collectors.summingInt(Award::getNum)));
            awardList.addAll(Award.getAwards(boxAwards, awardEnum.getValue()));
        }
        return awardList;
    }

    /**
     * 投注
     *
     * @param uid
     * @param type
     * @return
     */
    public RDCommon bet(long uid, int type) {
        //获得对应轮次
        CfgBrocadeGiftConfig.TurnAndAwards turnAndAward = BrocadeGiftTool.getTurnAndAwardByType(type);
        if (null == turnAndAward) {
            throw new ExceptionForClientTip("activity.lanternGifts.not.bet");
        }
        //检查是否有下注次数
        UserBrocadeGift userBrocadeGiftInfo = holidayBrocadeGiftService.getBrocadeGiftInfo(uid, turnAndAward.getId());
        Integer betTimes = userBrocadeGiftInfo.getBetTimes();
        Integer maxBetTimes = BrocadeGiftTool.getBrocadeGiftConfig().getMaxBetTimes();
        if (betTimes >= maxBetTimes) {
            throw new ExceptionForClientTip("activity.lanternGifts.not.bet.time");
        }
        //检查是否有足够道具
        Integer needTreasure = turnAndAward.getDrawNeedTreasure();
        Integer needNum = turnAndAward.getNum();
        TreasureChecker.checkIsEnough(needTreasure, needNum, uid);
        //扣除道具
        RDLanternGiftsBetInfo rd = new RDLanternGiftsBetInfo();
        TreasureEventPublisher.pubTDeductEvent(uid, needTreasure, needNum, WayEnum.LANTERN_BET, rd);
        Integer turn = turnAndAward.getTurn();
        //生成奖券号码
        String ticketNum = generateTicketNum(turn, type);
        rd.setTicketNum(ticketNum);
        //保存奖券号码
        holidayBrocadeGiftService.saveTicketNum(uid, ticketNum, turn, type);
        //更新玩家锦礼信息
        holidayBrocadeGiftService.updateBrocadeGiftInfo(uid, ticketNum, userBrocadeGiftInfo);
        //添加全服投注次数
        holidayBrocadeGiftService.addGameBetTimes(type, turn, 1);
        return rd;
    }

    /**
     * 生成奖券号码
     *
     * @param turn
     * @return
     */
    private String generateTicketNum(Integer turn, int type) {
        //获取当前轮所有号码
        Map<String, Long> allTicketNum = holidayBrocadeGiftService.getAllTicketNum(turn, type);
        String ticketNum;
        String numPrefix = "No" + SPLIT + "FJ115";
        do {
            //生成第一位字母
            String first = (char) (Math.random() * 26 + 'A') + "";
            String randomLengthDigit = PowerRandom.getRandomLengthDigit(3);
            //拼接
            ticketNum = numPrefix + first + randomLengthDigit;
        } while (allTicketNum.containsKey(ticketNum));
        return ticketNum;
    }

    /**
     * 锦礼每日任务可领取奖励数量
     *
     * @param gu
     * @param a
     * @return
     */
    @Override
    public int getAbleAwardedNum(GameUser gu, IActivity a) {
        List<UserBrocadeGiftDailyTask> tasks = brocadeGiftDailyTaskService.getCurUseraBrocadeGiftDailyTasks(gu.getId());
        //锦礼每日任务不存在，调用父类方法
        if (ListUtil.isEmpty(tasks)) {
            return super.getAbleAwardedNum(gu, a);
        }
        //返回锦礼每日任务可领取奖励
        return (int) tasks.stream().filter(tmp -> tmp.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()).count();
    }


}
