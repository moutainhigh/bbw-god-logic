package com.bbw.god.activity.worldcup.gm;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.god.activity.worldcup.*;
import com.bbw.god.activity.worldcup.cfg.CfgQuizKing;
import com.bbw.god.activity.worldcup.cfg.WorldCupAwardTool;
import com.bbw.god.activity.worldcup.cfg.WorldCupTool;
import com.bbw.god.activity.worldcup.entity.UserDroiyan8Info;
import com.bbw.god.activity.worldcup.entity.UserProphetInfo;
import com.bbw.god.activity.worldcup.entity.UserQuizKingInfo;
import com.bbw.god.activity.worldcup.entity.UserSuper16Info;
import com.bbw.god.activity.worldcup.rd.RdUserBet;
import com.bbw.god.activityrank.game.guess.GuessCompetitionRankService;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.UserMail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 世界杯
 *
 * @author: hzf
 * @create: 2022-11-22 16:55
 **/
@RestController
@RequestMapping("/gm")
public class GmWorldCupController {

    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private WorldCupService worldCupService;
    @Autowired
    private UserSuper16InfoService userSuper16InfoService;
    @Autowired
    private UserDroiyan8InfoService userDroiyan8InfoService;
    @Autowired
    private UserProphetInfoService userProphetInfoService;
    @Autowired
    private UserQuizKingInfoService userQuizKingInfoService;
    @Autowired
    private JoinWorldCupService joinWorldCupService;
    @Autowired
    private GuessCompetitionRankService guessCompetitionRankService;


    /**
     * 删除玩家超级16强投注记录
     *
     * @param uids
     */
    @GetMapping("/delUserSuper16Bet")
    public void delUserSuper16Bet(String uids) {
        List<Long> uidList = ListUtil.parseStrToLongs(uids);
        for (Long uid : uidList) {
            UserSuper16Info userSuper16 = worldCupService.getUserSuper16(uid);
            if (null == userSuper16) {
                continue;
            }
            userSuper16InfoService.deleteData(userSuper16);
        }
    }

    /**
     * 删除决战8强的投注记录
     * @param uids
     */
    @GetMapping("/delUserDroiyan8Bet")
    public void delUserDroiyan8Bet(String uids){
        List<Long> uidList = ListUtil.parseStrToLongs(uids);
        for (Long uid : uidList) {
            UserDroiyan8Info userDroiyan8 = worldCupService.getUserDroiyan8(uid);
            if (null == userDroiyan8) {
                continue;
            }
            userDroiyan8InfoService.deleteData(userDroiyan8);
        }
    }

    /**
     * 删除我是预言家的投注记录
     * @param uids
     */
    @GetMapping("/delProphetBet")
    public void delProphetBet(String uids){
        List<Long> uidList = ListUtil.parseStrToLongs(uids);
        for (Long uid : uidList) {
            UserProphetInfo userProphet = worldCupService.getUserProphet(uid);
            if (null == userProphet) {
                continue;
            }
            userProphetInfoService.deleteData(userProphet);
        }
    }
    /**
     * 删除竞猜王投注记录
     *
     * @param uids
     * @param date
     */
    @GetMapping("/delQuizKingBet")
    public void delQuizKingBet(String uids, String date) {
        List<Long> uidList = ListUtil.parseStrToLongs(uids);
        List<CfgQuizKing.CfgBet> cfgQuizKingBet = WorldCupTool.getCfgQuizKingBet(date);
        for (CfgQuizKing.CfgBet cfgBet : cfgQuizKingBet) {
            for (Long uid : uidList) {
                UserQuizKingInfo userQuizKing = worldCupService.getUserQuizKing(uid);
                if (null == userQuizKing || null == userQuizKing.getBetRecords()) {
                    continue;
                }
                List<UserQuizKingInfo.BetRecord> betRecords = userQuizKing.gainBetRecords(cfgBet.getId());
                if (null == betRecords) {
                    continue;
                }
                userQuizKing.delBetRecord(cfgBet.getId());
                userQuizKingInfoService.updateData(userQuizKing);
            }

        }

    }

    /**
     * 更新竞猜王投注的国家
     * @param betId
     * @param beforeBetCountry
     * @param afterBetCountry
     * @return
     */
    @GetMapping("/updateUserQuizKingBetCountry")
    public Rst updateUserQuizKingBetCountry(String betId,int beforeBetCountry,int afterBetCountry){
        List<Long> uids = joinWorldCupService.getJoinActivityUids(WorldCupTypeEnum.QUIZKING);
        if (ListUtil.isEmpty(uids)) {
            return Rst.businessOK();
        }
        for (Long uid : uids) {
            UserQuizKingInfo userQuizKing = worldCupService.getUserQuizKing(uid);
            if (null == userQuizKing) {
                continue;
            }
            List<UserQuizKingInfo.BetRecord> betRecords = userQuizKing.gainBetRecords(betId);
            if (ListUtil.isEmpty(betRecords)) {
                continue;
            }
            for (UserQuizKingInfo.BetRecord betRecord : betRecords) {
                if (betRecord.getBetCountry() == beforeBetCountry) {
                    betRecord.setBetCountry(afterBetCountry);
                }
            }
            userQuizKingInfoService.updateData(userQuizKing);
        }
        return Rst.businessOK();
    }

    /**
     * 更新玩家投注的数量
     * @param uids
     * @param betId
     * @param betCountry
     * @param afterBetNum
     * @return
     */
    @GetMapping("/updateUserQuizKingBetNum")
    public Rst updateUserQuizKingBetNum(String uids,String betId,int betCountry, int afterBetNum){
        List<Long> uidList = ListUtil.parseStrToLongs(uids);
        if (ListUtil.isEmpty(uidList)) {
            return Rst.businessOK();
        }
        for (Long uid : uidList) {
            UserQuizKingInfo userQuizKing = worldCupService.getUserQuizKing(uid);
            if (null == userQuizKing) {
                continue;
            }
            List<UserQuizKingInfo.BetRecord> betRecords = userQuizKing.gainBetRecords(betId);
            if (ListUtil.isEmpty(betRecords)) {
                continue;
            }
            for (UserQuizKingInfo.BetRecord betRecord : betRecords) {
                if (betRecord.getBetCountry() == betCountry) {
                    betRecord.setBetNum(afterBetNum);
                }
            }
            userQuizKingInfoService.updateData(userQuizKing);
        }
        return Rst.businessOK();
    }
    /**
     * 根据活动类型获取玩家的投注记录
     * @param type
     * @return
     */
    @GetMapping("/getUserBet")
    public RdUserBet getUserBet(int type,String dayDate) {
        RdUserBet rdUserBet = new RdUserBet();
        WorldCupTypeEnum worldCupType = WorldCupTypeEnum.fromActivityType(type);
        if (null == worldCupType) {
            return rdUserBet;
        }
        List<Long> uids = joinWorldCupService.getJoinActivityUids(worldCupType);
        rdUserBet.setTotalNum(uids.size());
        rdUserBet.setType(type);

        List<Long> notBetUids = new ArrayList<>();
        switch (worldCupType) {
            case SUPER16:
                List<RdUserBet.RdSuper16Bet> rdSuper16Bets = new ArrayList<>();
                for (Long uid : uids) {
                    UserSuper16Info userSuper16 = worldCupService.getUserSuper16(uid);
                    if (null == userSuper16 ) {
                        notBetUids.add(uid);
                        continue;
                    }
                    RdUserBet.RdSuper16Bet super16Bet = RdUserBet.RdSuper16Bet.instance(userSuper16);
                    rdSuper16Bets.add(super16Bet);
                }
                rdUserBet.setSuper16Bet(rdSuper16Bets);
                break;
            case DROIYAN8:
                List<RdUserBet.RdDroiyan8Bet> rdDroiyan8Bets = new ArrayList<>();
                for (Long uid : uids) {
                    UserDroiyan8Info userDroiyan8 = worldCupService.getUserDroiyan8(uid);
                    if (null == userDroiyan8) {
                        notBetUids.add(uid);
                        continue;
                    }
                    RdUserBet.RdDroiyan8Bet droiyan8Bet = RdUserBet.RdDroiyan8Bet.instance(userDroiyan8);
                    rdDroiyan8Bets.add(droiyan8Bet);
                }
                rdUserBet.setDroiyan8Bet(rdDroiyan8Bets);
                break;
            case PROPHET:
                List<RdUserBet.RdProphetBet> rdProphetBets = new ArrayList<>();
                for (Long uid : uids) {
                    UserProphetInfo userProphet = worldCupService.getUserProphet(uid);
                    if (null == userProphet) {
                        notBetUids.add(uid);
                        continue;
                    }
                    RdUserBet.RdProphetBet prophetBet = RdUserBet.RdProphetBet.instance(userProphet);
                    rdProphetBets.add(prophetBet);
                }
                rdUserBet.setProphetBet(rdProphetBets);
                break;
             case QUIZKING:
                 List<RdUserBet.RdQuizKingBet> rdQuizKingBets = new ArrayList<>();
                 List<CfgQuizKing.CfgBet> cfgQuizKingBet = WorldCupTool.getCfgQuizKingBet(dayDate);
                 //场次标识集合
                 List<String> idList = cfgQuizKingBet.stream().map(CfgQuizKing.CfgBet::getId).collect(Collectors.toList());
                 for (Long uid : uids) {
                     UserQuizKingInfo userQuizKing = worldCupService.getUserQuizKing(uid);
                     if (null == userQuizKing) {
                         notBetUids.add(uid);
                         continue;
                     }
                     Map<String, List<UserQuizKingInfo.BetRecord>> betRecords = userQuizKing.getBetRecords();
                     Map<String, List<UserQuizKingInfo.BetRecord>> newBetRecords = new HashMap<>();
                     for (Map.Entry<String, List<UserQuizKingInfo.BetRecord>> betRecordMap : betRecords.entrySet()) {
                         boolean contains = idList.contains(betRecordMap.getKey());
                         if (contains) {
                             newBetRecords.put(betRecordMap.getKey(),betRecordMap.getValue());
                         }
                     }
                     userQuizKing.setBetRecords(newBetRecords);
                     RdUserBet.RdQuizKingBet quizKingBet = RdUserBet.RdQuizKingBet.instance(userQuizKing);
                     rdQuizKingBets.add(quizKingBet);
                 }
                 rdUserBet.setQuizKingBet(rdQuizKingBets);
                break;
            default:
                break;
    }
        rdUserBet.setNotBetUids(notBetUids);
        return rdUserBet;
}


    /**
     * 重新添加莫一天的榜单数据
     * @param date
     */
    @GetMapping("repairGuessingCompetitionDay")
    public Rst repairGuessingCompetitionDay(String date) {
        List<Long> joinQuizkingUids = joinWorldCupService.getJoinActivityUids(WorldCupTypeEnum.QUIZKING);
        if (ListUtil.isEmpty(joinQuizkingUids)){
            return Rst.businessOK();
        }

        //获取要开奖
        List<CfgQuizKing.CfgBet> cfgQuizKingBet = WorldCupTool.getCfgQuizKingBet(date);
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
            //添加榜单
            guessCompetitionRankService.addGuessingValue(uid,quizkingAwrdNum);
        }
        return Rst.businessOK();
    }

    /**
     * 补发指定场次的奖励（只计算赢的数量）
     * @param date
     * @param sessionsIds
     */
    @GetMapping("/reissueSendMailAward")
    public Rst reissueSendMailAward(String sessionsIds,String date) {
        Date frontOneDay = DateUtil.addDays(DateUtil.fromDateTimeString(date), -1);
        String currentKey = DateUtil.toString(frontOneDay, "MM-dd");
        //发放邮件奖励
        List<UserMail> userMails = new ArrayList<>();
        List<Long> joinQuizkingUids = joinWorldCupService.getJoinActivityUids(WorldCupTypeEnum.QUIZKING);
        if (ListUtil.isEmpty(joinQuizkingUids)){
            return Rst.businessOK();
        }
        List<CfgQuizKing.CfgBet> cfgQuizKingBet = new ArrayList<>();

        List<String> ids = ListUtil.parseStrToStrs(sessionsIds);
        for (String id : ids) {
            CfgQuizKing.CfgBet cfgBet = WorldCupTool.getCfgQuizKingBetById(id);
            cfgQuizKingBet.add(cfgBet);
        }
        //获取要开奖

        for (Long uid : joinQuizkingUids) {
            //获取玩家的竞猜王的数据
            UserQuizKingInfo userQuizKing = worldCupService.getUserQuizKing(uid);
            if (null == userQuizKing) {
                continue;
            }
            //获取竞猜奖励的数量
            Integer quizkingAwrdNum = WorldCupAwardTool.getQuizkingWinAwrdNum(userQuizKing, cfgQuizKingBet);
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
        }
        gameUserService.addItems(userMails);
        return Rst.businessOK();
    }
}
