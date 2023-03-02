package com.bbw.god.activity.worldcup.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.activity.worldcup.JoinWorldCupService;
import com.bbw.god.activity.worldcup.UserSuper16InfoService;
import com.bbw.god.activity.worldcup.WorldCupService;
import com.bbw.god.activity.worldcup.WorldCupTypeEnum;
import com.bbw.god.activity.worldcup.cfg.CfgSuper16;
import com.bbw.god.activity.worldcup.cfg.WorldCupAwardTool;
import com.bbw.god.activity.worldcup.cfg.WorldCupTool;
import com.bbw.god.activity.worldcup.entity.UserSuper16Info;
import com.bbw.god.activity.worldcup.rd.RdDivideGroup;
import com.bbw.god.activity.worldcup.rd.RdShowSuper16;
import com.bbw.god.activity.worldcup.rd.RdSuper16;
import com.bbw.god.activityrank.game.guess.GuessCompetitionRankService;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 超级16强活动
 * @author: hzf
 * @create: 2022-11-11 15:35
 **/
@Service
public class Super16Processor extends AbstractActivityProcessor {

    @Autowired
    private WorldCupService worldCupService;
    @Autowired
    private JoinWorldCupService joinWorldCupService;
    @Autowired
    private UserSuper16InfoService userSuper16InfoService;


    public Super16Processor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.WORLD_CUP_ACTIVITIE_SUPER_16);
    }

    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        RDActivityList rdActivity = (RDActivityList) super.getActivities(uid, activityType);
        CfgSuper16 cfgSuper16 = WorldCupTool.getCfgSuper16();

        //判断是否到达展示时间
        boolean ifShow = WorldCupTool.ifShow(cfgSuper16.getShowBegin(), cfgSuper16.getShowEnd());

        UserSuper16Info userSuper16 = worldCupService.getUserSuper16(uid);
        if (ifShow) {
            return RdShowSuper16.instance(userSuper16,cfgSuper16);
        }
        return RdSuper16.instance(rdActivity.getDateInfo(),cfgSuper16,userSuper16);
    }

    /**
     *  超级16强投注
     * @param group
     * @param betCountry
     * @return
     */
    public RDCommon super16Bet(long uid, String group, String betCountry) {
        CfgSuper16 cfgSuper16 = WorldCupTool.getCfgSuper16();
        boolean ifGroup = WorldCupTool.ifGroup(group);
        if (!ifGroup) {
            throw  new ExceptionForClientTip("activity.worldCup.betIdentity.error");
        }
        CfgSuper16.CfgQuiz super16Quiz = WorldCupTool.getSuper16Quiz(group);
        boolean ifBet = WorldCupTool.ifBet(cfgSuper16.getBetBegin(), cfgSuper16.getBetEnd());
        if (!ifBet) {
            throw new ExceptionForClientTip("activity.worldCup.not.bet");
        }
        RDCommon rd = new RDCommon();
        //扣除道具
        rd = checkDeductTreasure(uid, group, super16Quiz.getNeedTreasure(), super16Quiz.getNum());

        UserSuper16Info userSuper16 = worldCupService.getUserSuper16(uid);
        //重置投注记录
        if (StringUtils.isEmpty(betCountry)){
            userSuper16.resetBetRecord(group);
            userSuper16InfoService.updateData(userSuper16);
            return rd;
        }
        //投注的国家
        List<Integer> betCountrys = ListUtil.parseStrToInts(betCountry);
        if (ListUtil.isEmpty(betCountrys) || 2 != betCountrys.size()) {
            throw new ExceptionForClientTip("activity.worldCup.betCountry.error");
        }
        if (null == userSuper16) {
            userSuper16 = UserSuper16Info.instance(uid,new HashMap<>());
            UserSuper16Info.BetRecord betRecord = userSuper16.gainBetRecord(group);
            betRecord.setBetCountrys(betCountrys);
            betRecord.setIfNeedTreasure(true);
            userSuper16.addBetRecord(group,betRecord);
            userSuper16InfoService.addData(userSuper16);
        }else {
            UserSuper16Info.BetRecord betRecord = userSuper16.gainBetRecord(group);
            betRecord.setBetCountrys(betCountrys);
            betRecord.setIfNeedTreasure(true);
            userSuper16.addBetRecord(group,betRecord);
            userSuper16InfoService.updateData(userSuper16);

        }
        //记录参与活动的玩家
        joinWorldCupService.joinActivity(uid, WorldCupTypeEnum.SUPER16);

        return rd;
    }

    /**
     * 超级16强 分组情况
     * @param uid
     * @param group
     * @return
     */
    public RdDivideGroup super16DivideGroup(long uid,String group) {
        CfgSuper16 cfgSuper16 = WorldCupTool.getCfgSuper16();
        CfgSuper16.CfgQuiz quiz = WorldCupTool.getSuper16Quiz(group);
        if (null == quiz) {
            throw  new ExceptionForClientTip("activity.worldCup.betIdentity.error");
        }
        RdDivideGroup rd = new RdDivideGroup();
        UserSuper16Info userSuper16 = worldCupService.getUserSuper16(uid);
        if (null != userSuper16 && null != userSuper16.getBetRecords()) {
            UserSuper16Info.BetRecord betRecord = userSuper16.gainBetRecord(group);
            if (null != betRecord) {
                rd.setIfNeedTreasure(betRecord.isIfNeedTreasure());
            }
        }

        rd.setCompeteCountries(quiz.getCompeteCountries());
        Date BetEndDate = DateUtil.fromDateTimeString(cfgSuper16.getBetEnd());
        rd.setSurplusTime(BetEndDate.getTime() - System.currentTimeMillis());
        return rd;
    }

    /**
     * 验证是否需要扣除道具并且扣除道具
     * @param uid
     * @param group
     * @param needTreasureId
     * @param needNum
     * @return
     */
    public RDCommon checkDeductTreasure(long uid,String group,Integer needTreasureId,Integer needNum) {
        RDCommon rdCommon = new RDCommon();
        UserSuper16Info userSuper16 = worldCupService.getUserSuper16(uid);
        if (null != userSuper16 && userSuper16.gainBetRecord(group).isIfNeedTreasure() ) {
            return rdCommon;
        }
        //扣除道具
        rdCommon = worldCupService.deductTreasure(uid, needTreasureId, needNum);
        return rdCommon;
    }

    /**
     * 发送邮件奖励
     */
    public void sendMailAward() {
        //发放邮件奖励
        List<UserMail> userMails = new ArrayList<>();

        List<Long> joinSuper16Uids = joinWorldCupService.getJoinActivityUids(WorldCupTypeEnum.SUPER16);
        if (ListUtil.isEmpty(joinSuper16Uids)){
            return;
        }
        for (Long uid : joinSuper16Uids) {
            //获取玩家的16强
            UserSuper16Info userSuper16 = worldCupService.getUserSuper16(uid);
            if (null == userSuper16) {
                continue;
            }
            //获取超级16猜中次数
            Integer successTime = WorldCupAwardTool.getSuper16SuccessTime(userSuper16);
            //超级16强猜中奖励
            List<Award> super16Award = WorldCupAwardTool.getSuper16Award(successTime);
            //实例化邮件奖励
            String title = LM.I.getMsgByUid(uid, "worldCup.mail.super16.title");
            String content = LM.I.getMsgByUid(uid, "worldCup.mail.super16.content", successTime);
            UserMail userMail = UserMail.newAwardMail(title, content, uid, super16Award);
            userMails.add(userMail);
        }
        gameUserService.addItems(userMails);
    }
}
