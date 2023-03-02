package com.bbw.god.activity.worldcup.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.activity.worldcup.JoinWorldCupService;
import com.bbw.god.activity.worldcup.UserDroiyan8InfoService;
import com.bbw.god.activity.worldcup.WorldCupService;
import com.bbw.god.activity.worldcup.WorldCupTypeEnum;
import com.bbw.god.activity.worldcup.cfg.CfgDroiyan8;
import com.bbw.god.activity.worldcup.cfg.WorldCupAwardTool;
import com.bbw.god.activity.worldcup.cfg.WorldCupTool;
import com.bbw.god.activity.worldcup.entity.UserDroiyan8Info;
import com.bbw.god.activity.worldcup.rd.RdDivideGroup;
import com.bbw.god.activity.worldcup.rd.RdDroiyan8;
import com.bbw.god.activity.worldcup.rd.RdShowDroiyan8;
import com.bbw.god.activityrank.game.guess.GuessCompetitionRankService;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 决战8强活动
 * @author: hzf
 * @create: 2022-11-12 09:31
 **/
@Service
public class Droiyan8Processor extends AbstractActivityProcessor {

    @Autowired
    private WorldCupService worldCupService;
    @Autowired
    private UserDroiyan8InfoService userDroiyan8InfoService;
    @Autowired
    private JoinWorldCupService joinWorldCupService;
    @Autowired
    private GameUserService gameUserService;


    public Droiyan8Processor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.WORLD_CUP_ACTIVITIE_FINALIZE_THE_TOP_8);
    }

    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        RDActivityList rdActivity = (RDActivityList) super.getActivities(uid, activityType);
        //获取决战8强的基本信息
        CfgDroiyan8 cfgDroiyan8 = WorldCupTool.getCfgDroiyan8();
        //获取玩家的决战8强的投注记录
        UserDroiyan8Info userDroiyan8 = worldCupService.getUserDroiyan8(uid);
        //判断是否到达展示时间
        boolean ifShow = WorldCupTool.ifShow(cfgDroiyan8.getShowBegin(), cfgDroiyan8.getShowEnd());
        if (ifShow) {
            return RdShowDroiyan8.instance(userDroiyan8,cfgDroiyan8);
        }
        return RdDroiyan8.instance(rdActivity.getDateInfo(),cfgDroiyan8,userDroiyan8);
    }

    /**
     * 决战8强投注
     * @param uid
     * @param id 场次标识
     * @param betCountry 投注国家
     * @return
     */
    public RDCommon droiyan8Bet(Long uid, String id, String betCountry) {
        //根本场次标识获取对应的比赛信息
        CfgDroiyan8.CfgQuiz cfgDroiyan8Quit = WorldCupTool.getCfgDroiyan8Quit(id);

        if (null == cfgDroiyan8Quit) {
            throw new ExceptionForClientTip("activity.worldCup.betIdentity.error");
        }
        RDCommon rd = new RDCommon();
        //判断竞猜是否开始
        boolean ifBet = WorldCupTool.ifBet(cfgDroiyan8Quit.getBetBegin(), cfgDroiyan8Quit.getBetEnd());
        if (!ifBet) {
            throw new ExceptionForClientTip("activity.worldCup.not.bet");
        }
        //扣除道具
        rd = checkDeductTreasure(uid,id,cfgDroiyan8Quit.getNeedTreasure(),cfgDroiyan8Quit.getNum());
        //投注的国家
        UserDroiyan8Info userDroiyan8 = worldCupService.getUserDroiyan8(uid);
        // betCountry 为空 ===> 重置投注记录
        if (StringUtils.isEmpty(betCountry)) {
            userDroiyan8.resetBetRecord(id);
            userDroiyan8InfoService.updateData(userDroiyan8);
            return rd;
        }
        if (null == userDroiyan8) {
            userDroiyan8 = UserDroiyan8Info.instance(uid,new HashMap<>());
            UserDroiyan8Info.BetRecord betRecord = userDroiyan8.gainBetRecord(id);
            betRecord.setBetCountry(Integer.parseInt(betCountry));
            userDroiyan8.addBetRecord(id,betRecord);
            betRecord.setIfNeedTreasure(true);
            userDroiyan8InfoService.addData(userDroiyan8);
        }else {
            UserDroiyan8Info.BetRecord betRecord = userDroiyan8.gainBetRecord(id);
            betRecord.setBetCountry(Integer.parseInt(betCountry));
            userDroiyan8.addBetRecord(id,betRecord);
            betRecord.setIfNeedTreasure(true);
            userDroiyan8InfoService.updateData(userDroiyan8);
        }
        //记录参与活动的玩家
        joinWorldCupService.joinActivity(uid, WorldCupTypeEnum.DROIYAN8);

        return rd;

    }

    /**
     * 决战8强 分组情况
     * @param id 场次标识
     * @return
     */
    public RdDivideGroup droiyan8DivideGroup(long uid,String id) {
        //根本场次标识获取对应的比赛信息
        CfgDroiyan8.CfgQuiz quiz = WorldCupTool.getCfgDroiyan8Quit(id);
        if (null == quiz) {
            throw  new ExceptionForClientTip("activity.worldCup.betIdentity.error");
        }
        RdDivideGroup rd = new RdDivideGroup();
        UserDroiyan8Info userDroiyan8 = worldCupService.getUserDroiyan8(uid);
        if (null != userDroiyan8 && null  != userDroiyan8.getBetRecords()) {
            UserDroiyan8Info.BetRecord betRecord = userDroiyan8.gainBetRecord(id);
            if (null != betRecord) {
                rd.setIfNeedTreasure(betRecord.isIfNeedTreasure());
            }
        }

        rd.setCompeteCountries(quiz.getCompeteCountries());
        Date BetEndDate = DateUtil.fromDateTimeString(quiz.getBetEnd());
        //计算剩余时间
        rd.setSurplusTime(BetEndDate.getTime() - System.currentTimeMillis());
        return rd;
    }
    /**
     * 验证是否需要扣除道具并且扣除道具
     * @param uid
     * @param id 场次标识
     * @param needTreasureId 道具id
     * @param needNum   道具数量
     * @return
     */
    public RDCommon checkDeductTreasure(long uid, String id, Integer needTreasureId, Integer needNum) {
        RDCommon rdCommon = new RDCommon();
        // 获取玩家决战8强的投注记录
        UserDroiyan8Info userDroiyan8 = worldCupService.getUserDroiyan8(uid);
        if (null != userDroiyan8 &&  userDroiyan8.gainBetRecord(id).isIfNeedTreasure()) {
            return rdCommon;
        }
        // 扣除道具
        rdCommon = worldCupService.deductTreasure(uid, needTreasureId, needNum);
        return rdCommon;
    }

    /**
     * 发送邮件奖励
     */
    public void sendMailAward() {
        //发放邮件奖励
        List<UserMail> userMails = new ArrayList<>();

        List<Long> joinDroiyan8Uids = joinWorldCupService.getJoinActivityUids(WorldCupTypeEnum.DROIYAN8);
        if (ListUtil.isEmpty(joinDroiyan8Uids)){
            return;
        }
        for (Long uid : joinDroiyan8Uids) {
            //获取决战8强
            UserDroiyan8Info userDroiyan8 = worldCupService.getUserDroiyan8(uid);
            if (null == userDroiyan8) {
                continue;
            }
            //决战8强的连中情况
            List<Integer> droiyan8List = WorldCupAwardTool.getDroiyan8List(userDroiyan8);
            //计算决战8强应该获得的奖励数量
            Integer droiyan8AwardNum = WorldCupAwardTool.getDroiyan8AwardNum(droiyan8List);
            //得决战八强对应的奖励
            List<Award> droiyan8Award = WorldCupAwardTool.getDroiyan8Award(droiyan8AwardNum);
            int winNum = WorldCupAwardTool.countDroiyan8WinNum(droiyan8List);
            //实例化邮件奖励
            String title = LM.I.getMsgByUid(uid, "worldCup.mail.droiyan8.title");
            String content = LM.I.getMsgByUid(uid, "worldCup.mail.droiyan8.content", winNum);
            UserMail userMail = UserMail.newAwardMail(title, content, uid, droiyan8Award);
            userMails.add(userMail);
        }
        gameUserService.addItems(userMails);
    }
}
