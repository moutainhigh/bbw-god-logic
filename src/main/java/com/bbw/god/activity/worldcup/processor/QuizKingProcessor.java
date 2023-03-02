package com.bbw.god.activity.worldcup.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.activity.worldcup.JoinWorldCupService;
import com.bbw.god.activity.worldcup.UserQuizKingInfoService;
import com.bbw.god.activity.worldcup.WorldCupService;
import com.bbw.god.activity.worldcup.WorldCupTypeEnum;
import com.bbw.god.activity.worldcup.cfg.CfgQuizKing;
import com.bbw.god.activity.worldcup.cfg.WorldCupAwardTool;
import com.bbw.god.activity.worldcup.cfg.WorldCupTool;
import com.bbw.god.activity.worldcup.entity.UserQuizKingInfo;
import com.bbw.god.activity.worldcup.rd.RdDayDateQuizKing;
import com.bbw.god.activity.worldcup.rd.RdQuizKing;
import com.bbw.god.activityrank.game.guess.GuessCompetitionRankService;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 我是竞猜王活动
 * @author: hzf
 * @create: 2022-11-12 13:08
 **/
@Service
public class QuizKingProcessor extends AbstractActivityProcessor {
    @Autowired
    private WorldCupService worldCupService;
    @Autowired
    private JoinWorldCupService joinWorldCupService;
    @Autowired
    private UserQuizKingInfoService userQuizKingInfoService;
    @Autowired
    private GuessCompetitionRankService guessCompetitionRankService;

    public QuizKingProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.WORLD_CUP_ACTIVITIE_I_AM_KING_OF_GUESS);
    }

    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        RDActivityList rdActivity = (RDActivityList) super.getActivities(uid, activityType);
        return RdQuizKing.instance(rdActivity.getDateInfo());
    }

    /**
     * 我是竞猜王投注
     * @param uid
     * @param id
     * @return
     */
    public RDCommon quizKingBet(Long uid, String id, String betCountryAndNums) {
        CfgQuizKing.CfgBet cfgBet = WorldCupTool.getCfgQuizKingBetById(id);
        if (null == cfgBet) {
            throw new ExceptionForClientTip("activity.worldCup.betIdentity.error");
        }
        boolean ifBet = WorldCupTool.ifBet(cfgBet.getBetBegin(), cfgBet.getBetEnd());
        if (!ifBet) {
            throw new ExceptionForClientTip("activity.worldCup.not.bet");
        }
        RDCommon rd = new RDCommon();
        List<String> betCountryAndNumList = ListUtil.parseStrToStrs(betCountryAndNums);
        UserQuizKingInfo userQuizKing = worldCupService.getUserQuizKing(uid);
        UserQuizKingInfo.BetRecord betRecord = new UserQuizKingInfo.BetRecord();
        List<UserQuizKingInfo.BetRecord> betRecords = betRecord.gainBetRecord(betCountryAndNumList);
        //计算扣除的道具数量
        Integer needNum = betRecord.countNeedNum(betRecords);
        //获取该场次需要消耗的道具
        rd = worldCupService.deductTreasure(uid, cfgBet.getNeedTreasure(), needNum);

        if (null == userQuizKing) {
            userQuizKing = UserQuizKingInfo.instance(uid,new HashMap<>());
            userQuizKing.addBetRecord(id,betRecords);
            userQuizKingInfoService.addData(userQuizKing);
        }else {
            userQuizKing.addBetRecord(id,betRecords);
            userQuizKingInfoService.updateData(userQuizKing);
        }
        //记录参与活动的玩家
        joinWorldCupService.joinActivity(uid,WorldCupTypeEnum.QUIZKING);
        return rd;
    }
    /**
     * 获取每天竞猜王的数据
     * @param uid
     * @param dayDate
     * @return
     */
    public RdDayDateQuizKing quizKingDayDate(Long uid, String dayDate) {
        UserQuizKingInfo userQuizKing = worldCupService.getUserQuizKing(uid);
        List<CfgQuizKing.CfgBet> cfgQuizKingBet = WorldCupTool.getCfgQuizKingBet(dayDate);

        return RdDayDateQuizKing.instance(userQuizKing,cfgQuizKingBet);
    }



    /**
     * 发送邮件奖励
     */
    public void sendMailAward() {
        String FILTERED_DATE ="11-22";
        //发放邮件奖励
        List<UserMail> userMails = new ArrayList<>();
        List<Long> joinQuizkingUids = joinWorldCupService.getJoinActivityUids(WorldCupTypeEnum.QUIZKING);
        if (ListUtil.isEmpty(joinQuizkingUids)){
            return;
        }
        Date frontOneDay = DateUtil.addDays(DateUtil.now(), -1);
        String currentKey = DateUtil.toString(frontOneDay, "MM-dd");
        if (FILTERED_DATE.equals(currentKey)){
            return;
        }
        //获取要开奖
        List<CfgQuizKing.CfgBet> cfgQuizKingBet = WorldCupTool.getCfgQuizKingBet(currentKey);
        for (Long uid : joinQuizkingUids) {
            //获取玩家的竞猜王的数据
            UserQuizKingInfo userQuizKing = worldCupService.getUserQuizKing(uid);
            if (null == userQuizKing) {
                continue;
            }
            //获取竞猜奖励的数量
            Integer quizkingAwrdNum = WorldCupAwardTool.getQuizkingAwrdNum(userQuizKing, cfgQuizKingBet);
            if (0 == quizkingAwrdNum) {
                continue;
            }
            //获取竞猜奖励
            List<Award> quizkingAwrd = WorldCupAwardTool.getQuizkingAwrd(quizkingAwrdNum);
            //实例化邮件奖励
            String title = LM.I.getMsgByUid(uid, "worldCup.mail.quizking.title",currentKey);
            String content = LM.I.getMsgByUid(uid, "worldCup.mail.quizking.content", quizkingAwrdNum);
            UserMail userMail = UserMail.newAwardMail(title, content, uid, quizkingAwrd);
            userMails.add(userMail);
            //添加榜单
            guessCompetitionRankService.addGuessingValue(uid,quizkingAwrdNum);
        }
        gameUserService.addItems(userMails);
    }
}
