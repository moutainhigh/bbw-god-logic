package com.bbw.god.game.wanxianzhen.service.race;

import com.bbw.common.CloneUtil;
import com.bbw.common.JSONUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.db.entity.WanXianMatchEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.wanxianzhen.*;
import com.bbw.god.game.wanxianzhen.event.WanXianLogDbHandler;
import com.bbw.god.game.wanxianzhen.service.ChampionPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lwb 决赛
 * 第一轮13:00，进行常规赛4进2的比赛，由A组的小组第一，对阵B组的小组第二，另一队类推。其中小组第一的玩家先手。
 * 对战双方需要进行11场对战，轮流先手，率先获得6场胜利的玩家获胜，然后从该玩家获胜的6场中，随机选择1场符合小组第一的录像，作为结果录像。
 * 第二轮13:15，进行常规赛总决赛，bo3模式  如存在第三场则 第三场叠加前2场中获胜场次的血量，且前2轮血量之和高的先手
 * @date 2020/4/22 11:12
 */
@Service
public class WanXianFinalsRace extends AbstractWanXianRace {
    public static final String FINALS_GROUP_KEY = "group_finals";
    @Autowired
    private ChampionPredictionService championPredictionService;
    @Autowired
    private WanXianLogDbHandler wanXianLogDbHandler;

    @Override
    public void getMainPageInfo(long uid, RDWanXian rd, Integer param, int type) {
        int gid = gameUserService.getActiveGid(uid);
        List<RDWanXian.RDFightLog> fightUsers = wanXianSeasonService.getFightUsers(gid, type, FINALS_GROUP_KEY);
        List<RDWanXian.RDFightLog> rdFightLogs = new ArrayList<>();
        if (rd.getWxShowRace() == null) {
            for (int i = 0; i < 2; i++) {
                fightUsers.get(i).setWinner(0);
                rdFightLogs.add(fightUsers.get(i));
            }
        }else if (WanXianEmailEnum.EMAIL_FINAL_RACE_1.getVal()<=rd.getWxShowRace().getVal() && rd.getWxShowRace().getVal()<WanXianEmailEnum.EMAIL_FINAL_RACE_3.getVal()){
            fightUsers.get(2).setWinner(0);
            rdFightLogs=fightUsers;
        }else if (WanXianEmailEnum.EMAIL_FINAL_RACE_3.getVal()==rd.getWxShowRace().getVal()){
            int times=wanXianSeasonService.getRaceOverVal(gid,type);
            if (times==2){
                fightUsers.get(2).setWinner(0);
            }
            rdFightLogs=fightUsers;
        }else {
            rdFightLogs=fightUsers;
        }
        rd.setRaces(rdFightLogs);
        rd.setLogs(wanXianLogic.getFightLogs(gid,type,9).getLogs());
    }

    @Override
    public boolean todayRace(int weekDay) {
        return weekDay==7;
    }

    @Override
    public int getWanxianType() {
        return WanXianPageType.FINALS_RACE.getVal();
    }

    @Override
    public void beginTodayAllRace(int weekday, int gid,int type) {
        beginRace(gid,WanXianEmailEnum.EMAIL_FINAL_RACE_1,0,1,type);
        beginRace(gid,WanXianEmailEnum.EMAIL_FINAL_RACE_2,2,2,type);
    }

    /**
     * 第一轮13:00，进行常规赛4进2的比赛，由A组的小组第一，对阵B组的小组第二，另一队类推。其中小组第一的玩家先手。对战双方只进行1场对战。
     */

    public void beginRace(int gid,WanXianEmailEnum emailEnum,int begin,int end,int type){
        Map<String,String> fightLogsMap=new HashMap<>();
        List<Long> winnerUids=new ArrayList<>();
        List<RDWanXian.RDFightLog> fights=wanXianSeasonService.getFightUsers(gid,type,FINALS_GROUP_KEY);
        if (fights.size()>(end+1)){
            fights=fights.subList(0,end+1);
        }
        for (int i=begin;i<=end;i++){
            //战斗
            RDWanXian.RDFightLog item=CloneUtil.clone(fights.get(i));
            if (WanXianEmailEnum.EMAIL_FINAL_RACE_2.equals(emailEnum)){
                finalRace(gid,type,item,fightLogsMap);
                fights.get(i).setVid(item.getVid());
                fights.get(i).setWinner(item.getWinnerUid()==fights.get(i).getP1().getUid()?1:2);
            }else {
                if (item.isChangePos()){
                    item.changeP1ToP2();
                }
                doFightLogic(gid,type,emailEnum,item);
                fights.get(i).setVid(item.getVid());
                fights.get(i).setWinner(item.getWinnerUid()==fights.get(i).getP1().getUid()?1:2);
                fightLogsMap.put(fights.get(i).getVidKey(),JSONUtil.toJson(fights.get(i)));
            }
            winnerUids.add(item.getWinnerUid());
        }
        if (winnerUids.size()==2){
            RDWanXian.RDFightLog fight=RDWanXian.RDFightLog.instance(winnerUids.get(0),winnerUids.get(1));
            fights.add(fight);
            int currentType=wanXianLogic.getTypeRace(type,gid);
            WanXianMatchEntity entity=WanXianMatchEntity.instance(fight,7,gid,currentType);
            entity.setVidKey(WanXianEmailEnum.EMAIL_FINAL_RACE_2.getVal()+"N1");
            fight.setVidKey(WanXianEmailEnum.EMAIL_FINAL_RACE_2.getVal() + "N1");
            wanXianLogDbHandler.logMatch(entity);
        }
        wanXianSeasonService.addVal(gid,type,FINALS_GROUP_KEY,JSONUtil.toJson(fights));
        wanXianFightLogsService.addFightLogs(gid,type,fightLogsMap);
    }

    private void finalRace(int gid,int type,RDWanXian.RDFightLog fight,Map<String,String> fightLogsMap){
        int p1Win=0;
        int p1WinHp=0;
        int p2WinHp=0;
        int p1SumHp=0;
        int p2SumHp=0;
        long p1=fight.getP1().getUid();
        WanXianEmailEnum[] types={WanXianEmailEnum.EMAIL_FINAL_RACE_2,WanXianEmailEnum.EMAIL_FINAL_RACE_3,WanXianEmailEnum.EMAIL_FINAL_RACE_4};
        for (int i=0;i<2;i++){
            RDWanXian.RDFightLog item= CloneUtil.clone(fight);
            if (i==1){
                item.changeP1ToP2();
            }
            item.setVidKey(types[i].getVal()+"N1");
            Combat combat=doFightLogic(gid,type,types[i],item);
            if (item.getWinnerUid()==p1){
                p1Win++;
                p1WinHp=combat.getPlayerByUid(p1).getHp();
            }else {
                p2WinHp=combat.getPlayerByUid(fight.getP2().getUid()).getHp();
            }
            p1SumHp+=combat.getPlayerByUid(p1).getHp();
            p2SumHp+=combat.getPlayerByUid(fight.getP2().getUid()).getHp();
            fightLogsMap.put(item.getVidKey(),JSONUtil.toJson(item));
        }
        if(p1Win==2 || p1Win==0){
            //说明有P1玩家获得 2胜；
            fight.setWinner(p1Win==2?1:2);
            wanXianSeasonService.addVal(gid,type,"raceOver","1");
            return;
        }
        //进行第三场
        fight.getP1().setHp(p1WinHp);
        fight.getP2().setHp(p2WinHp);
        if (p1SumHp<p2SumHp ||((p1SumHp==p2SumHp && PowerRandom.getRandomBetween(1,2)==2))){
            fight.changeP1ToP2();
        }
        fight.setVidKey(WanXianEmailEnum.EMAIL_FINAL_RACE_4.getVal()+"N1");
        doFightLogic(gid,type,types[2],fight);
        fightLogsMap.put(fight.getVidKey(),JSONUtil.toJson(fight));
        wanXianSeasonService.addVal(gid,type,"raceOver","2");
    }
    @Override
    public void sendEliminateMail(int gid, int type,int order) {
        List<RDWanXian.RDFightLog> fights=wanXianSeasonService.getFightUsers(gid,type,FINALS_GROUP_KEY);
        Set<Long> receiverUids=new HashSet<>();
        String maxRank="总决赛第一轮";
        WanXianEmailEnum emailEnum=WanXianEmailEnum.EMAIL_FINAL_RACE_1;
        Long finalWinner=null;
        if (order==1){
            for (int i=0;i<2;i++){
                receiverUids.add(fights.get(i).getLoserUid());
                addHistoryRank(fights.get(i).getLoserUid(),gid,type,3+i);
            }
            logRank(gid,type,emailEnum,receiverUids.stream().collect(Collectors.toList()));
        }else {
            receiverUids.add(fights.get(2).getLoserUid());
            emailEnum=WanXianEmailEnum.EMAIL_FINAL_RACE_2;
            maxRank="亚军";
            addHistoryRank(fights.get(2).getLoserUid(),gid,type,2);
            finalWinner=fights.get(2).getWinnerUid();
            List<Long> logUids=new ArrayList<>();
            logUids.add(fights.get(2).getLoserUid());
            logUids.add(fights.get(2).getWinnerUid());
            logRank(gid,type,emailEnum,logUids);
        }
        CfgWanXian.WanXianEmail email=WanXianTool.getEmail(type,WanXianEmailEnum.EMAIL_FAIL);
        String content=String.format(email.getContent(), maxRank);
        List<Award> awards=WanXianTool.getAwards(emailEnum,type);
        mailService.sendAwardMail(email.getTitle(), content, receiverUids, awards);
        championPredictionService.eliminate(gid,type,receiverUids.stream().collect(Collectors.toList()));
        //发送冠军奖励
        if (finalWinner!=null){
            CfgWanXian.WanXianEmail lastMail=WanXianTool.getEmail(type,WanXianEmailEnum.EMAIL_CHAMPION);
            List<Award> lastAwards=WanXianTool.getAwards(WanXianEmailEnum.EMAIL_CHAMPION,type);
            mailService.sendAwardMail(lastMail.getTitle(), lastMail.getContent(), finalWinner, lastAwards);
            championPredictionService.setWinner(gid,type,finalWinner);
            addHistoryRank(finalWinner,gid,type,1);
            wanXianLogic.setWanxianRedisExpire(gid,WanXianTool.getThisSeason());
        }
    }

    /**
     * 清理玩家万仙阵错误的垃圾信息，执行错误时使用
     * @param gid
     * @param weekday
     */
    @Override
    public void clear(int gid,int type,int weekday) {
        Set<Long> uids=new HashSet<>();
        List<RDWanXian.RDFightLog> fights=wanXianSeasonService.getFightUsers(gid,type,FINALS_GROUP_KEY);
        for (RDWanXian.RDFightLog log:fights){
            uids.add(log.getP1().getUid());
            uids.add(log.getP2().getUid());
        }
        List<String> removeKey=new ArrayList<>(2);
        removeKey.add("13007");
        removeKey.add("13157");
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
}
