package com.bbw.god.game.wanxianzhen.service.race;

import com.bbw.common.JSONUtil;
import com.bbw.god.db.entity.WanXianMatchEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.combat.video.CombatVideo;
import com.bbw.god.game.wanxianzhen.*;
import com.bbw.god.game.wanxianzhen.event.WanXianLogDbHandler;
import com.bbw.god.game.wanxianzhen.service.ChampionPredictionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lwb 小组赛
 * 周六的8名成员将进入小组赛，将随机分成两组，每组中有4名选手。4名选手将在小组中进行双循环战斗。胜得1分
 * 战斗过后，分数较高的2位玩家出线，分数较低的2位玩家淘汰。
 * 若有玩家平分,则按照如下规则排列出线：
 * 1.彼此间的胜负关系
 * 2.资格赛的积分排名
 * @date 2020/4/22 11:11
 */
@Service
@Slf4j
public class WanXianGroupStage extends AbstractWanXianRace {
    @Autowired
    private ChampionPredictionService championPredictionService;
    @Autowired
    private WanXianLogDbHandler wanXianLogDbHandler;

    @Override
    public void getMainPageInfo(long uid, RDWanXian rd, Integer param, int type) {
        int gid = gameUserService.getActiveGid(uid);
        rd.setRanksA(getScoreRankList(gid, type, rd.getWxShowRace(), "A"));
        rd.setRanksB(getScoreRankList(gid, type, rd.getWxShowRace(), "B"));
        rd.setLogs(wanXianLogic.getFightLogs(gid, type, 8).getLogs());
    }

    private List<RDWanXian.RDUser> getScoreRankList(int gid, int type, WanXianEmailEnum emailEnum, String group) {
        List<RDWanXian.RDUser> list = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<Long>> keysVals=wanXianScoreRankService.getKeysValsByRank(wanXianScoreRankService.getGroupStageBaseKey(gid,type,emailEnum,group),1,4);
        Iterator<ZSetOperations.TypedTuple<Long>> iterator=keysVals.iterator();
        while (iterator.hasNext()){
            ZSetOperations.TypedTuple<Long> item=iterator.next();
            RDWanXian.RDUser user=new RDWanXian.RDUser();
            user.setNickname(wanXianLogic.getNickname(item.getValue()));
            user.setScore(String.valueOf(item.getScore().intValue()));
            list.add(user);
        }
        return list;
    }
    @Override
    public boolean todayRace(int weekDay) {
        return weekDay==6;
    }

    @Override
    public int getWanxianType() {
        return WanXianPageType.GROUP_STAGE.getVal();
    }

    @Override
    public void beginTodayAllRace(int weekday, int gid,int type) {
        beginRace(gid,0,1,null,WanXianEmailEnum.EMAIL_GROUP_STAGE_1,type);
        beginRace(gid,2,3,WanXianEmailEnum.EMAIL_GROUP_STAGE_1,WanXianEmailEnum.EMAIL_GROUP_STAGE_2,type);
        beginRace(gid,4,5,WanXianEmailEnum.EMAIL_GROUP_STAGE_2,WanXianEmailEnum.EMAIL_GROUP_STAGE_3,type);
        beginRace(gid,6,7,WanXianEmailEnum.EMAIL_GROUP_STAGE_3,WanXianEmailEnum.EMAIL_GROUP_STAGE_4,type);
        beginRace(gid,8,9,WanXianEmailEnum.EMAIL_GROUP_STAGE_4,WanXianEmailEnum.EMAIL_GROUP_STAGE_5,type);
        beginRace(gid,10,11,WanXianEmailEnum.EMAIL_GROUP_STAGE_5,WanXianEmailEnum.EMAIL_GROUP_STAGE_6,type);
        doPromotionRank(gid,type);
    }

    /**
     * 视频键值KEY格式 类型N组N场次  如13点进行的A组第一场战斗即为：13006NAN1   B组为13006NBN1
     * @param gid
     * @param emailEnum
     */
    public void beginRace(int gid,int begin,int end,WanXianEmailEnum preEm,WanXianEmailEnum emailEnum,int type){
        String[] groupNames={"A","B"};
        Map<String,String> fightLogsMap=new HashMap<>();
        for (String group:groupNames){
            List<RDWanXian.RDFightLog> fights =wanXianSeasonService.getFightUsers(gid,type,"group_"+group);
            String newRank=wanXianScoreRankService.getGroupStageBaseKey(gid,type,emailEnum,group);
            copyRank(wanXianScoreRankService.getGroupStageBaseKey(gid,type,preEm,group),newRank);
            for (int i=begin;i<=end;i++){
                doFightLogic(gid,type,emailEnum,fights.get(i));
                fights.get(i).setGroup(group+"组");
                fightLogsMap.put(fights.get(i).getVidKey(),JSONUtil.toJson(fights.get(i)));
                wanXianScoreRankService.incVal(newRank,fights.get(i).getWinnerUid(),1);
            }
        }
        wanXianFightLogsService.addFightLogs(gid,type,fightLogsMap);
    }
    /**
     * 整理玩家排名
     * 若有玩家平分,则按照如下规则排列出线：
     * 1.彼此间的胜负关系 即 A和B同分，那么A赢过B则A晋级
     * 2.资格赛的积分排名
     * @param gid
     * @return
     */
    public void doPromotionRank(int gid,int type){
        String[] groups={"A","B"};
        for (String group:groups){
            String key=wanXianScoreRankService.getGroupStageBaseKey(gid,type,WanXianEmailEnum.EMAIL_GROUP_STAGE_6,group);
            Set<ZSetOperations.TypedTuple<Long>> keysVals=wanXianScoreRankService.getKeysValsByRank(key,1,4);
            Iterator<ZSetOperations.TypedTuple<Long>> iterator=keysVals.iterator();
            List<RDWanXian.RDUser> users=new ArrayList<>();
            while (iterator.hasNext()){
                ZSetOperations.TypedTuple<Long> item=iterator.next();
                RDWanXian.RDUser user= RDWanXian.RDUser.instance(item.getValue(),item.getScore().intValue());
                users.add(user);
            }
            users=users.stream().sorted(Comparator.comparing(RDWanXian.RDUser::getScoreInt).reversed()).collect(Collectors.toList());
            //首先按积分从大到小排序
            int dealWith=0;
            String qualifylingRankKey=wanXianScoreRankService.getSoreRankKey(gid,type,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_8);
            for (int i=0;i<4;i=dealWith){
                int preScore=users.get(i).getScoreInt();
                List<RDWanXian.RDUser> theSameScore=new ArrayList<>();
                for (int k=i;k<4;k++){
                    if (preScore==users.get(k).getScoreInt()){
                        //整理相同分数的玩家ID
                        theSameScore.add(users.get(k));
                    }
                }
                //添加处理的玩家数量
                dealWith+=theSameScore.size();
                //处理相同分数的玩家
                if (theSameScore.size()==1){
                    continue;
                }
                if (theSameScore.size()==2){
                    //如果分数相同的只有2个则 已2人间的胜负关系判断
                    UserWanXian uwx1=wanXianLogic.getOrCreateUserWanXian(theSameScore.get(0).getUid(),type);
                    long p1=theSameScore.get(0).getUid();
                    long p2=theSameScore.get(1).getUid();
                    if (uwx1.getFightWin()!=null){
                        long num=uwx1.getFightWin().stream().filter(p->p.longValue()==theSameScore.get(1).getUid().longValue()).count();
                        if (num==2){
                            //说明玩家1和玩家2对战时  赢了2场，所以改玩家名次应该更前
                            wanXianScoreRankService.setDoubleVal(key,p1,Double.valueOf(theSameScore.get(0).getScoreInt()+".2"));
                            wanXianScoreRankService.setDoubleVal(key,p2,Double.valueOf(theSameScore.get(1).getScoreInt()+".1"));
                            continue;
                        }else if (num==0){
                            //说明玩家1和玩家2对战时  败了2场，所以改玩家名次应该更后
                            wanXianScoreRankService.setDoubleVal(key,p1,Double.valueOf(theSameScore.get(0).getScoreInt()+".1"));
                            wanXianScoreRankService.setDoubleVal(key,p2,Double.valueOf(theSameScore.get(1).getScoreInt()+".2"));
                            continue;
                        }
                    }
                }
                //执行到此处时说明 分数相同玩家数量多于2名，或者2名玩家的胜负是一样的，则按资格赛排名为准
                for (RDWanXian.RDUser user:theSameScore){
                    int rank =wanXianScoreRankService.getRankByKey(qualifylingRankKey,user.getUid());
                    wanXianScoreRankService.setDoubleVal(key,user.getUid(),Double.valueOf(user.getScoreInt()+"."+(64-rank)));
                }
            }
        }
        List<Long> aGroup=wanXianScoreRankService.getKeysByRank(wanXianScoreRankService.getGroupStageBaseKey(gid,type,WanXianEmailEnum.EMAIL_GROUP_STAGE_6,"A"),1,2);
        List<Long> bGroup=wanXianScoreRankService.getKeysByRank(wanXianScoreRankService.getGroupStageBaseKey(gid,type,WanXianEmailEnum.EMAIL_GROUP_STAGE_6,"B"),1,2);
        //分配晋级玩家的对手：小组第一对战另一组第二
        List<RDWanXian.RDFightLog> fightLogs=new ArrayList<>();
        fightLogs.add(RDWanXian.RDFightLog.instance(aGroup.get(0),bGroup.get(1)));
        fightLogs.add(RDWanXian.RDFightLog.instance(bGroup.get(0),aGroup.get(1)));
        List<WanXianMatchEntity> matchs=new ArrayList<>();
        int order=1;
        int currentType=wanXianLogic.getTypeRace(type,gid);
        for (RDWanXian.RDFightLog log:fightLogs){
            WanXianMatchEntity entity=WanXianMatchEntity.instance(log,7,gid,currentType);
            entity.setVidKey(WanXianEmailEnum.EMAIL_FINAL_RACE_1.getVal()+"N"+order);
            log.setVidKey(WanXianEmailEnum.EMAIL_FINAL_RACE_1.getVal()+"N"+order);
            order++;
            matchs.add(entity);
        }
        wanXianLogDbHandler.logMatchs(matchs);

        wanXianSeasonService.addVal(gid,type,"group_finals", JSONUtil.toJson(fightLogs));
        //生成4强预测
        List<Long> promotionUids=new ArrayList<>();
        promotionUids.add(aGroup.get(0));
        promotionUids.add(bGroup.get(1));
        promotionUids.add(bGroup.get(0));
        promotionUids.add(aGroup.get(1));
        buildChampionPrediction(gid,type,promotionUids,4);
    }

    @Override
    public void clear(int gid,int type,int weekday) {
        Set<Long> uids=new HashSet<>();
        String[] groupNames={"A","B"};
        for (String group:groupNames){
            List<Long> allUsers=wanXianScoreRankService.getAllItemKeys(wanXianScoreRankService.getGroupStageBaseKey(gid,type,null,group));
            uids.addAll(allUsers);
        }
        List<String> removeKey=new ArrayList<>(3);
        for (int k=0;k<=5;k++){
            removeKey.add(String.valueOf((13000+k*100+6)));
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
    public void sendEliminateMail(int gid, int type,int order) {
        List<Long> aGroup=wanXianScoreRankService.getKeysByRank(wanXianScoreRankService.getGroupStageBaseKey(gid,type,WanXianEmailEnum.EMAIL_GROUP_STAGE_6,"A"),3,4);
        List<Long> bGroup=wanXianScoreRankService.getKeysByRank(wanXianScoreRankService.getGroupStageBaseKey(gid,type,WanXianEmailEnum.EMAIL_GROUP_STAGE_6,"B"),3,4);
        //淘汰名单
        Set<Long> receiverUids=new HashSet<>();
        receiverUids.addAll(aGroup);
        receiverUids.addAll(bGroup);
        CfgWanXian.WanXianEmail email=WanXianTool.getEmail(type,WanXianEmailEnum.EMAIL_FAIL);
        String content=String.format(email.getContent(), "小组赛");
        List<Award> awards=WanXianTool.getAwards(WanXianEmailEnum.EMAIL_GROUP_STAGE_6,type);
        mailService.sendAwardMail(email.getTitle(), content, receiverUids, awards);
        //更新预测结果
        championPredictionService.eliminate(gid,type,receiverUids.stream().collect(Collectors.toList()));
        //添加到历史殿堂
        List<Long> uids=receiverUids.stream().collect(Collectors.toList());
        for (int i=0;i<uids.size();i++){
            long uid=uids.get(i);
            addHistoryRank(uid,gid,type,5+i);
        }
        logRank(gid,type,WanXianEmailEnum.EMAIL_GROUP_STAGE_6,uids);
    }

    @Override
    public void saveFightLog(int gid,int type,WanXianEmailEnum emailEnum, RDWanXian.RDFightLog log, CombatVideo combatVideo) {
        super.saveFightLog(gid, type,emailEnum,log, combatVideo);
        RDWanXian.RDUser winner=log.getWinner()==1?log.getP1():log.getP2();
        winner.setScore("1");
        RDWanXian.RDUser loser=log.getWinner()==1?log.getP2():log.getP1();
        loser.setScore("0");
    }
}
