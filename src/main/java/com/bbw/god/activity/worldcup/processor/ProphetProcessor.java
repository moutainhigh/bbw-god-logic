package com.bbw.god.activity.worldcup.processor;

import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.activity.worldcup.JoinWorldCupService;
import com.bbw.god.activity.worldcup.UserProphetInfoService;
import com.bbw.god.activity.worldcup.WorldCupService;
import com.bbw.god.activity.worldcup.WorldCupTypeEnum;
import com.bbw.god.activity.worldcup.cfg.CfgProphet;
import com.bbw.god.activity.worldcup.cfg.WorldCupAwardTool;
import com.bbw.god.activity.worldcup.cfg.WorldCupTool;
import com.bbw.god.activity.worldcup.entity.UserProphetInfo;
import com.bbw.god.activity.worldcup.rd.RdProphet;
import com.bbw.god.activityrank.game.guess.GuessCompetitionRankService;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 我是预言家活动
 * @author: hzf
 * @create: 2022-11-12 11:17
 **/
@Service
public class ProphetProcessor extends AbstractActivityProcessor {

    @Autowired
    private WorldCupService worldCupService;
    @Autowired
    private JoinWorldCupService joinWorldCupService;
    @Autowired
    private UserProphetInfoService userProphetInfoService;

    public ProphetProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.WORLD_CUP_ACTIVITIE_I_AM_PROPHET);
    }


    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        RDActivityList rdActivity = (RDActivityList) super.getActivities(uid, activityType);
        //获取我是预言家配置信息
        CfgProphet cfgProphet = WorldCupTool.getCfgProphet();
        //获取我是预言家玩家的投注记录
        UserProphetInfo userProphet = worldCupService.getUserProphet(uid);
        return RdProphet.instance(rdActivity.getDateInfo(),cfgProphet,userProphet);
    }

    /**
     * 我是预言家投注
     * @param uid
     * @param identAndBetCountrys 需要投注满7场比赛，场次@国家 eg:22-12-09-23@1,22-12-10-03@3
     * @return
     */
    public RDCommon prophetBet(Long uid, String identAndBetCountrys) {
        CfgProphet cfgProphet = WorldCupTool.getCfgProphet();
        //判断是否还在竞猜时间内
        boolean ifBet = WorldCupTool.ifBet(cfgProphet.getBetBegin(), cfgProphet.getBetEnd());
        if (!ifBet) {
            throw new ExceptionForClientTip("activity.worldCup.not.bet");
        }
        RDCommon rd = new RDCommon();
        //扣除道具
        rd = checkDeductTreasure(uid,cfgProphet.getNeedTreasure(),cfgProphet.getNeedNum());


        UserProphetInfo userProphet = worldCupService.getUserProphet(uid);
        // identAndBetCountrys 为空 为重置操作
        if (StringUtils.isEmpty(identAndBetCountrys)) {
            userProphet.ResetBetRecord();
            userProphetInfoService.updateData(userProphet);
            return rd;
        }
        List<String> identAndBetCountryList = ListUtil.parseStrToStrs(identAndBetCountrys);
        //判断投注的是否是7场比赛
        if (identAndBetCountryList.size() != 7){
            throw new ExceptionForClientTip("activity.worldCup.betCountry.error");
        }
        if (null == userProphet) {
            userProphet = UserProphetInfo.instance(uid,new HashMap<>());
            userProphet.addBetRecord(identAndBetCountryList);
            userProphet.setIfNeedTreasure(true);
            userProphetInfoService.addData(userProphet);
        }else {
            userProphet.addBetRecord(identAndBetCountryList);
            userProphet.setIfNeedTreasure(true);
            userProphetInfoService.updateData(userProphet);
        }
        //记录参与活动的玩家
        joinWorldCupService.joinActivity(uid, WorldCupTypeEnum.PROPHET);

        return rd;
    }

    /**
     * 验证是否需要扣除道具并且扣除道具
     * @param uid
     * @param needTreasureId
     * @param needNum
     * @return
     */
    public RDCommon checkDeductTreasure(long uid, Integer needTreasureId, Integer needNum) {
        RDCommon rdCommon = new RDCommon();
        UserProphetInfo userProphet = worldCupService.getUserProphet(uid);
        if (null != userProphet && userProphet.isIfNeedTreasure()) {
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

        List<Long> joinProphetUids = joinWorldCupService.getJoinActivityUids(WorldCupTypeEnum.PROPHET);
        if (ListUtil.isEmpty(joinProphetUids)){
            return;
        }
        for (Long uid : joinProphetUids) {
            //获取我是预言家
            UserProphetInfo userProphet = worldCupService.getUserProphet(uid);
            if (null  == userProphet) {
                continue;
            }
            //获取我是预言家连中情况
            Integer successTime = WorldCupAwardTool.getProphetSuccessTime(userProphet);
            //获取对应的奖励
            List<Award> prophetAward = WorldCupAwardTool.getProphetAward(successTime);
            //实例化邮件奖励
            String title = LM.I.getMsgByUid(uid, "worldCup.mail.prophet.title");
            String content = LM.I.getMsgByUid(uid, "worldCup.mail.prophet.content", successTime);
            UserMail userMail = UserMail.newAwardMail(title, content, uid, prophetAward);
            userMails.add(userMail);
        }
        gameUserService.addItems(userMails);
    }



}
