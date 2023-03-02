package com.bbw.god.game.wanxianzhen.service.race;

import com.bbw.common.*;
import com.bbw.god.db.entity.WanXianMatchEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.wanxianzhen.*;
import com.bbw.god.game.wanxianzhen.event.WanXianLogDbHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 周三，周四，周五为淘汰赛。积分排名前64名的玩家，将出线参加周三开始的淘汰赛。
 * 周三的淘汰赛中，64名玩家将两两配对，生成8组，每组8人，8人间两两配对。进行对战.  8进1
 * @date 2020/4/22 11:10
 */
@Service
@Slf4j
public class WanXianEliminationSeriesRace extends AbstractWanXianRace {
    @Autowired
    private WanXianLogDbHandler wanXianLogDbHandler;

    @Override
    public void getMainPageInfo(long uid, RDWanXian rd, Integer param, int type) {
        UserWanXian userWanXian = wanXianLogic.getOrCreateUserWanXian(uid, type);
        rd.setGroup("1");
        if (userWanXian.getGroupNumber() != null) {
            rd.setGroup(userWanXian.getGroupNumber());
        }
    }

    @Override
    public boolean todayRace(int weekDay) {
        return weekDay==3||weekDay==4||weekDay==5;
    }

    @Override
    public int getWanxianType() {
        return WanXianPageType.ELIMINATION_SERIES_RACE.getVal();
    }

    @Override
    public void beginTodayAllRace(int weekday, int gid,int type) {
        if (weekday==3){
            beginRace(gid,WanXianEmailEnum.EMAIL_ELIMINATION_SERIES_RACE_1,WanXianEmailEnum.EMAIL_ELIMINATION_SERIES_RACE_2,0,3,type);
        }else if (weekday==4){
            beginRace(gid,WanXianEmailEnum.EMAIL_ELIMINATION_SERIES_RACE_2,WanXianEmailEnum.EMAIL_ELIMINATION_SERIES_RACE_3,4,5,type);
        }else if (weekday==5){
            beginRace(gid,WanXianEmailEnum.EMAIL_ELIMINATION_SERIES_RACE_3,null,6,6,type);
        }
    }

    /**
     * 本赛程保存视频key    赛事N组N队N场   如13003N1N0N1表示 周三淘汰赛 第1组 第一个队伍 第一场战斗
     * 新规则：
     * 每轮玩家将与对手进行2场战斗，双方轮流先手，获得2胜的玩家出线，失败者淘汰。若双方各获得1胜，则进行第3场战斗，由资格赛排名较高的玩家先手。第3场战斗获胜者出线。
     * @param gid
     * @param emailEnum
     * @param begin
     * @param end
     */
    public void beginRace(int gid,WanXianEmailEnum emailEnum,WanXianEmailEnum nextEnum,int begin,int end,int type){
        int currentType=wanXianLogic.getTypeRace(type,gid);
        Map<String,String> fightLogsMap=new HashMap<>();
        Map<String,String> fightGroupMap=new HashMap<>();
        List<Long> winnerUids=new ArrayList<>();
        List<WanXianMatchEntity> matchs=new ArrayList<>();
        for (int i=1;i<=8;i++){
            List<RDWanXian.RDFightLog> fights=wanXianSeasonService.getFightUsers(gid,type,"group_"+i);
            if (fights.size()>(end+1)){
                fights=fights.subList(0,end+1);
            }
            List<Long> winnerList=new ArrayList<>(4);
            for (int j=begin;j<=end;j++){
                //战斗
                RDWanXian.RDFightLog item=fights.get(j);
                fightLogsMap.putAll(doPromotion(gid,emailEnum,item,type));
                winnerList.add(item.getWinnerUid());
            }
            if (winnerList.size()>1){
                int order=1;
                for (int k=0;k<winnerList.size();k+=2){
                    RDWanXian.RDFightLog newFight=RDWanXian.RDFightLog.instance(winnerList.get(k),winnerList.get(k+1));
                    newFight.setVidKey(nextEnum.getVal()+"N"+i+"N"+order);
                    fights.add(newFight);
                    WanXianMatchEntity entity=WanXianMatchEntity.instance(newFight,DateUtil.getToDayWeekDay()+1,gid,currentType);
                    matchs.add(entity);
                    entity.setVidKey(nextEnum.getVal()+"N"+i+"N"+order);
                    order++;
                }
            }
            winnerUids.addAll(winnerList);
            fightGroupMap.put("group_"+i,JSONUtil.toJson(fights));
        }
        wanXianLogDbHandler.logMatchs(matchs);
        wanXianSeasonService.addGroup(gid, type, fightGroupMap);
        wanXianFightLogsService.addFightLogs(gid,type,fightLogsMap);
        if (WanXianEmailEnum.EMAIL_ELIMINATION_SERIES_RACE_3.getVal()==emailEnum.getVal()){
            //周五 需要生成 预测
            doPromotionNextRace(winnerUids,gid,type);
        }
    }

    /**
     * 生成晋级人员
     *
     * @param promotionUids
     * @param gid
     */
    public void doPromotionNextRace(List<Long> promotionUids,int gid,int type){
        int currentType=wanXianLogic.getTypeRace(type,gid);
        List<Long> groupA=PowerRandom.getRandomsFromList(4,promotionUids);
        List<Long> groupB=promotionUids.stream().filter(p->!groupA.contains(p)).collect(Collectors.toList());
        //淘汰赛完成-----将随机分成两组，每组中有4名选手
        wanXianScoreRankService.jionWanXianRegularRace(wanXianScoreRankService.getGroupStageBaseKey(gid,type,null,"A"),groupA);
        wanXianScoreRankService.jionWanXianRegularRace(wanXianScoreRankService.getGroupStageBaseKey(gid,type,null,"B"),groupB);
        //按固定规则 分配对手（按照编号顺序，1V4，2V3,1V2,3V4,1V3,2V4）
        Map<String,String> groupMap=new HashMap<>();
        groupMap.put("group_A",JSONUtil.toJson(getMacthFight(groupA,"A",gid,currentType)));
        groupMap.put("group_B",JSONUtil.toJson(getMacthFight(groupB,"B",gid,currentType)));
        wanXianSeasonService.addGroup(gid,type,groupMap);
        buildChampionPrediction(gid,type,promotionUids,8);
    }

    /**
     * 对晋级人员进行战斗编组
     * @param group
     * @return
     */
    private List<RDWanXian.RDFightLog> getMacthFight(List<Long> group,String groupStr,int gid,int wxtype){
        //（按照编号顺序，1V4，2V3,1V2,3V4,1V3,2V4）
        List<RDWanXian.RDFightLog> fightList=new ArrayList<>();
        fightList.add(RDWanXian.RDFightLog.instance(group.get(0),group.get(3))) ;
        fightList.add(RDWanXian.RDFightLog.instance(group.get(1),group.get(2))) ;

        fightList.add(RDWanXian.RDFightLog.instance(group.get(3),group.get(0))) ;
        fightList.add(RDWanXian.RDFightLog.instance(group.get(2),group.get(1))) ;

        fightList.add(RDWanXian.RDFightLog.instance(group.get(0),group.get(1))) ;
        fightList.add(RDWanXian.RDFightLog.instance(group.get(2),group.get(3))) ;

        fightList.add(RDWanXian.RDFightLog.instance(group.get(1),group.get(0))) ;
        fightList.add(RDWanXian.RDFightLog.instance(group.get(3),group.get(2))) ;

        fightList.add(RDWanXian.RDFightLog.instance(group.get(0),group.get(2))) ;
        fightList.add(RDWanXian.RDFightLog.instance(group.get(1),group.get(3))) ;

        fightList.add(RDWanXian.RDFightLog.instance(group.get(2),group.get(0))) ;
        fightList.add(RDWanXian.RDFightLog.instance(group.get(3),group.get(1))) ;
        int order=1;
        List<Integer> vals=WanXianEmailEnum.getGroupVals();
        int valIndex=0;
        List<WanXianMatchEntity> matchs=new ArrayList<>();
        for (int i=0;i<fightList.size();i++){
            RDWanXian.RDFightLog log=fightList.get(i);
            int val1=vals.get(valIndex);
            WanXianMatchEntity entity=WanXianMatchEntity.instance(log,6,gid,wxtype);
            log.setVidKey(val1+"N"+groupStr+"N"+order);
            entity.setVidKey(val1+"N"+groupStr+"N"+order);
            matchs.add(entity);
            order++;
            if (order==3){
                order=1;
                valIndex++;
            }
        }
        wanXianLogDbHandler.logMatchs(matchs);
        return fightList;
    }
    /**
     * 晋级
     * @param gid
     * @param fight
     */
    private  Map<String,String> doPromotion(int gid,WanXianEmailEnum emailEnum,RDWanXian.RDFightLog fight,int type){
        Map<String,String> fightLogMap=new HashMap<>();
        List<Combat> combats=new ArrayList<>(2);
        for (int i=1;i<=2;i++){
            RDWanXian.RDFightLog log=CloneUtil.clone(fight);
            if (i==2){
                log.changeP1ToP2();
            }
            log.setVidKey(log.getVidKey()+"N"+i);
            Combat cbt=doFightLogic(gid,type,emailEnum,log);
            combats.add(cbt);
            fightLogMap.put(log.getVidKey(),JSONUtil.toJson(log));
        }
        long winnerUid=combats.get(0).getWinnerId()==1?fight.getP1().getUid():fight.getP2().getUid();
        if (combats.get(0).getWinnerId()==combats.get(1).getWinnerId()){
            //说明1胜1负，则进行第3场战斗，由资格赛排名较高的玩家先手。第3场战斗获胜者出
            int p1Score=wanXianScoreRankService.getValByKey(wanXianScoreRankService.getSoreRankKey(gid,type,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_8),fight.getP1().getUid());
            int p2Score=wanXianScoreRankService.getValByKey(wanXianScoreRankService.getSoreRankKey(gid,type,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_8),fight.getP2().getUid());
            RDWanXian.RDFightLog log=CloneUtil.clone(fight);
            if (p1Score<p2Score){
                log.changeP1ToP2();
            }
            log.setVidKey(log.getVidKey()+"N"+3);
            Combat cbt=doFightLogic(gid,type,emailEnum,log);
            combats.add(cbt);
            fightLogMap.put(log.getVidKey(),JSONUtil.toJson(log));
            winnerUid=cbt.getWinnerId()==1?cbt.getP1().getUid():cbt.getP2().getUid();
            fight.setWinner(winnerUid==fight.getP1().getUid()?1:2);
        }else {
            fight.setWinner(winnerUid==fight.getP1().getUid()?1:2);
        }
        return fightLogMap;
    }

    /**
     * 首先选手A先手，与选手B进行11场对战，其中优先获得6胜的选手的选手该场获胜，并且从获胜的6场中随机选择1场作为玩家可以看到的录像。
     * 然后选手B先手，与选手A进行11场对战，也是优先获得6胜的人获胜，然后获胜者从胜利的6场中选择1场作为玩家可以看到的录像。
     * 平局处理：
     * 1、两场对战结束时玩家剩余血之和，剩余较多的一方获胜
     * 2、资格赛阶段名次高的玩家获胜
     * @return
     */
    public long getWinnerUId(Combat f1,Combat f2,int type){
        long player1=f1.getFirstPlayer().getUid();
        long player2=f1.getSecondPlayer().getUid();
        Player[] p1Players={f1.getFirstPlayer(),f2.getSecondPlayer()};
        Player[] p2Players={f1.getSecondPlayer(),f2.getFirstPlayer()};
        long winner=f1.getPlayer(PlayerId.fromValue(f1.getWinnerId())).getUid();
        if (f1.getWinnerId()==f2.getWinnerId()){
            //平局处理
            //1、两场对战结束时玩家剩余血之和，剩余较多的一方获胜
            int p1Hp=p1Players[0].getHp()+p1Players[1].getHp();
            int p2Hp=p2Players[0].getHp()+p2Players[1].getHp();
            if (p1Hp>p2Hp){
                winner=player1;
            }else if (p2Hp>p1Hp){
                winner=player2;
            }else {
                //2、资格赛阶段名次高的玩家获胜
                int gid=gameUserService.getActiveGid(player1);
                int p1Score=wanXianScoreRankService.getValByKey(wanXianScoreRankService.getSoreRankKey(gid,type,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_8),player1);
                int p2Score=wanXianScoreRankService.getValByKey(wanXianScoreRankService.getSoreRankKey(gid,type,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_8),player2);
                if (p1Score>p2Score){
                    winner=player1;
                }else{
                    winner=player2;
                }
            }
        }
        return winner;
    }

    /**
     * 清理玩家万仙阵错误的垃圾信息，执行错误时使用
     * @param gid
     * @param weekday
     */
    @Override
    public void clear(int gid,int type,int weekday) {
        Set<Long> uids=new HashSet<>();
        for (int i=1;i<=8;i++) {
            List<RDWanXian.RDFightLog> fights = wanXianSeasonService.getFightUsers(gid,type, "group_" + i);
            for (int j=0;j<4;j++){
                uids.add(fights.get(j).getP1().getUid());
                uids.add(fights.get(j).getP2().getUid());
            }
            log.error(type+"万仙阵清理：编组："+i+"组数据："+JSONUtil.toJson(fights));
        }
        List<String> removeKey=new ArrayList<>(3);
        for (int k=weekday;k<=5;k++){
            removeKey.add("1300"+k);
        }
        for (long uid:uids) {
            Optional<UserWanXian> op=wanXianLogic.getUserWanXian(uid,type);
            if (op.isPresent()) {
                UserWanXian userWanXian=op.get();
                userWanXian.setTodayScore(0);
                userWanXian.setContinuingWins(0);
                userWanXian.setFightFail(null);
                userWanXian.setFightWin(null);
                userWanXian.setLastWin(0);
                List<String> removeList=new ArrayList<String>();
                for (String str:userWanXian.getFightLogs()) {
                    for (String key:removeKey){
                        if (str.indexOf(key)>-1) {
                            removeList.add(str);
                            break;
                        }
                    }
                }
                userWanXian.getFightLogs().removeAll(removeList);
                gameUserService.updateItem(userWanXian);
            }
        }
    }

    @Override
    public void sendEliminateMail(int gid,int type,int order) {
        int weekday= DateUtil.getToDayWeekDay();
        int begin=0;
        int end=3;
        WanXianEmailEnum emailEnum=WanXianEmailEnum.EMAIL_ELIMINATION_SERIES_RACE_1;
        if (weekday==4){
            begin=4;
            end=5;
            emailEnum=WanXianEmailEnum.EMAIL_ELIMINATION_SERIES_RACE_2;
        }else if (weekday==5){
            begin=6;
            end=6;
            emailEnum=WanXianEmailEnum.EMAIL_ELIMINATION_SERIES_RACE_3;
        }
        Set<Long> receiverUids=new HashSet<>();
        for (int i=1;i<=8;i++) {
            List<RDWanXian.RDFightLog> fights = wanXianSeasonService.getFightUsers(gid,type, "group_" + i);
            for (int j=begin;j<=end;j++){
                receiverUids.add(fights.get(j).getLoserUid());
            }
        }
        CfgWanXian.WanXianEmail email=WanXianTool.getEmail(type,WanXianEmailEnum.EMAIL_FAIL);
        String content=String.format(email.getContent(), "淘汰赛");
        List<Award> awards=WanXianTool.getAwards(emailEnum,type);
        mailService.sendAwardMail(email.getTitle(), content, receiverUids, awards);
        logRank(gid,type,emailEnum,receiverUids.stream().collect(Collectors.toList()));
    }

    public List<RDWanXian.RDFightLog> getLogsByVidkey(int gid,int type,String vidKey){
        if (StrUtil.isBlank(vidKey)){
            return new ArrayList<>();
        }
        List<String> keys=new ArrayList<>(3);
        keys.add(vidKey+"N1");
        keys.add(vidKey+"N2");
        keys.add(vidKey+"N3");
        return wanXianFightLogsService.getFightLogs(gid,type,keys);
    }
}
