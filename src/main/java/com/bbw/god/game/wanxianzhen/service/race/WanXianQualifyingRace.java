package com.bbw.god.game.wanxianzhen.service.race;

import com.bbw.common.*;
import com.bbw.god.db.entity.WanXianMatchEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.combat.video.CombatVideo;
import com.bbw.god.game.wanxianzhen.CfgWanXian.WanXianEmail;
import com.bbw.god.game.wanxianzhen.*;
import com.bbw.god.game.wanxianzhen.event.WanXianEventPublisher;
import com.bbw.god.game.wanxianzhen.event.WanXianLogDbHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lwb 资格赛
 * 每周一、周二进行的4场赛事
 * 13:00、13:15、13:30、13:45
 * @date 2020/4/22 11:08
 */
@Slf4j
@Service
public class WanXianQualifyingRace extends AbstractWanXianRace {
    @Autowired
    private WanXianLogDbHandler wanXianLogDbHandler;

    @Override
    public boolean todayRace(int weekDay) {
        //周一、周二赛事
        return weekDay == 1 || weekDay == 2;
    }

    @Override
    public int getWanxianType() {
        return WanXianPageType.QUALIFYING_RACE.getVal();
    }

    @Override
    public void getMainPageInfo(long uid,RDWanXian rd,Integer param,int type) {
        int pageSize=10;
        if (param!=null && param>0){
            pageSize=param;
        }
        int gid=gameUserService.getActiveGid(uid);
        WanXianEmailEnum emailEnum=rd.getWxShowRace();
        String baseKey=wanXianScoreRankService.getSoreRankKey(gid,type,emailEnum);
        boolean hasBegin=false;
        if (emailEnum==null){
            rd.setRanks(new ArrayList<>());
            rd.setPageNum(1);
        }else {
            //获取榜单
            List<RDWanXian.RDUser> ranks=getRankList(baseKey,pageSize,1);
            rd.setRanks(ranks);
            int count=wanXianScoreRankService.getCount(baseKey);
            int showPage=64/pageSize;
            showPage+=64%pageSize==0?0:1;
            int countPage=count/pageSize;
            countPage+=count%pageSize==0?0:1;
            rd.setPageNum(Math.min(showPage,countPage));
            hasBegin=true;
        }
        //获取战报
        if (emailEnum!=null && wanXianLogic.hasJionWanxian(uid,type)){
            Optional<UserWanXian> op=wanXianLogic.getUserWanXian(uid,type);
            rd.setLogs(wanXianLogic.getFightLogs(op.get(),type,1,2));
            if (!rd.getLogs().isEmpty()){
                Collections.reverse(rd.getLogs());
            }
        }
        int myRank=wanXianScoreRankService.getRankByKey(baseKey,uid);
        if (myRank>0){
            RDWanXian.RDUser myInfo=new RDWanXian.RDUser();
            myInfo.setRank(String.valueOf(myRank));
            myInfo.setUid(uid);
            wanXianLogic.getUserInfo(myInfo,true);
            if (hasBegin){
                int score=wanXianScoreRankService.getValByKey(baseKey,uid);
                myInfo.setScore(String.valueOf(score));
            }else {
                myInfo.setScore("0");
                myInfo.setRank("未入榜");
            }
            rd.setMyRankInfo(myInfo);
        }

    }
    @Override
    public void beginTodayAllRace(int weekday, int gid,int type) {
        if (weekday==1){
            beginFirstRace(gid,null,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_1,type);
            beginRace(gid,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_1,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_2,2,type);
            beginRace(gid,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_2,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_3,4,type);
            beginRace(gid,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_3,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_4,6,type);
        }else {
            wanXianWinRankService.rest(gid,type);
            beginFirstRace(gid,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_4,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_5,type);
            beginRace(gid,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_5,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_6,2,type);
            beginRace(gid,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_6,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_7,4,type);
            beginRace(gid,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_7,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_8,6,type);
            eliminate(gid,type);
        }
    }
    /**
     * 获取榜单
     * @param pageSize 显示数量
     * @param current 第几页
     * @return
     */
    public List<RDWanXian.RDUser> getRankList(String key,int pageSize,int current){
        List<RDWanXian.RDUser> list=new ArrayList<>();
        current=current<=0?1:current;
        int begin=(current-1)*pageSize+1;
        int end=current*pageSize;
        Set<ZSetOperations.TypedTuple<Long>> keysVals=wanXianScoreRankService.getKeysValsByRank(key,begin,end);
        Iterator<ZSetOperations.TypedTuple<Long>> iterator=keysVals.iterator();
        int rank=begin;
        while (iterator.hasNext()){
            ZSetOperations.TypedTuple<Long> item=iterator.next();
            RDWanXian.RDUser user=new RDWanXian.RDUser();
            user.setUid(item.getValue());
            wanXianLogic.getUserInfo(user,true);
            user.setScore(String.valueOf(item.getScore().intValue()));
            user.setRank(String.valueOf(rank));
            rank++;
            list.add(user);
        }
        return list;
    }
    /**
     * 第一场13:00 每人战2场、
     * </br>规则：根据报名顺序进行，随机（0<x<报名总人数）中的一个数字x为中线，对手即为玩家自身序号+x，当和超过人数则从第一位继续顺位选择
     * <br>周一周二的随机数不能相同
     */
    private void beginFirstRace(int gid,WanXianEmailEnum orderEnum,WanXianEmailEnum emailEnum,int type){
        long begin=System.currentTimeMillis();
        String currentKey=copyRank(gid,type,orderEnum,emailEnum);
        List<Long> uids=wanXianScoreRankService.getAllItemKeys(currentKey);
        int monSeed=wanXianSeasonService.getMondaySeed(gid,type);
        int seed=PowerRandom.getRandomBySeed(uids.size()-2);
        int mid=0;
        if (uids.size()%2==0){
            mid=uids.size()/2;
        }
        int loop=10;
        while (monSeed>0 && monSeed==seed && loop>0 && mid==seed){
            seed=PowerRandom.getRandomBySeed(uids.size()-2);
            loop--;//最多执行10次
        }
        if (monSeed<=0) {
            wanXianSeasonService.setMondaySeed(gid, type, seed);
        }
        String saveKey=emailEnum.getVal()+"N0";
        fightMatch(gid,uids,emailEnum,currentKey,saveKey,seed,type);
        log.info(type+"总共第一轮计时："+(System.currentTimeMillis()-begin));
    }
    private void beginRace(int gid,WanXianEmailEnum orderEnum,WanXianEmailEnum emailEnum,int maxWin,int type){
        long begin=System.currentTimeMillis();
        String currentKey=copyRank(gid,type,orderEnum,emailEnum);
        List<List<Long>> groups=beginGrouping(gid,type,maxWin);
        for (int i=0;i<groups.size();i++){
            String saveKey=emailEnum.getVal()+"N"+(i+1);
            int seed=PowerRandom.getRandomBySeed(groups.get(i).size()-2);
            int mid=0;
            if (groups.get(i).size()%2==0){
                mid=groups.get(i).size()/2;
            }
            int loop=10;
            while (mid==seed && loop>0){
                //避免随机到中位数
                seed=PowerRandom.getRandomBySeed(groups.get(i).size()-2);
                loop--;
            }
            fightMatch(gid,groups.get(i),emailEnum,currentKey,saveKey,seed,type);
        }
        log.info(type+"总共计时："+(System.currentTimeMillis()-begin));
    }

    private void saveData(List<RDWanXian.RDFightLog> fightLogs,String key,int gid,int type){
        Map<Long,Integer> addScoreMap=new HashMap<>();
        Map<String,String> jsonData=new HashMap<>();
        for (RDWanXian.RDFightLog log:fightLogs){
            RDWanXian.RDUser win=log.getWinnerRDUser();
            if (addScoreMap.get(win.getUid())!=null){
                int val=addScoreMap.get(win.getUid());
                addScoreMap.put(win.getUid(),(val+win.getScoreInt()));
            }else {
                addScoreMap.put(win.getUid(),win.getScoreInt());
            }
            jsonData.put(log.getVidKey(),JSONUtil.toJson(log));
        }
        //批量加分
        for (Map.Entry<Long,Integer> entry:addScoreMap.entrySet()){
            wanXianScoreRankService.incVal(key,entry.getKey(),entry.getValue());
        }
        wanXianFightLogsService.addFightLogs(gid,type,jsonData);
    }

    /**
     * 匹配前根据胜场划分组
     * @param gid
     * @param maxWinTimes
     * @return
     */
    private List<List<Long>> beginGrouping(int gid,int type,int maxWinTimes){
        List<List<Long>> lists=new ArrayList<>();
        List<Long> pre=null;
        for (int i=maxWinTimes;i>=0;i--){
            List<Long> cut=wanXianWinRankService.getUidByWinTimes(gid,type,i);
            if (cut.isEmpty()){
                continue;
            }
            if (pre==null){
                pre=cut;
                continue;
            }
            if (pre.size()<3 ||cut.size()<3){
                pre.addAll(cut);
                continue;
            }else {
                lists.add(pre);
                pre=cut;
            }
        }
        if (pre.size()<3){
            lists.get(lists.size()-1).addAll(pre);
        }else {
            lists.add(pre);
        }
        return lists;
    }
    /**
     * 周二打完 需要通知报送失去资格的玩家
     * 排名前64的玩家出线。
     * 该模式当涉及到是否晋级有多位玩家同分时，则按如下规则选择：
     * 1)去掉连胜分后，基础分高者胜出
     * 2)周二分数高者胜出
     * 3)电脑随机选择
     */
    public void eliminate(int gid,int type) {
        int currentType=wanXianLogic.getTypeRace(type,gid);
        //先获取64~65名的分数是否相同
        String key=wanXianScoreRankService.getSoreRankKey(gid,type,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_8);
        String baseKey=key+"_old";
        //排序
        int count=wanXianScoreRankService.getCount(baseKey);
        Set<ZSetOperations.TypedTuple<Long>> keysVals=wanXianScoreRankService.getKeysValsByRank(baseKey,1,count);
        Iterator<ZSetOperations.TypedTuple<Long>> iterator=keysVals.iterator();
        double[] scores=new double[count];
        Long[] uids=new Long[count];
        int index=0;
        while (iterator.hasNext()){
            ZSetOperations.TypedTuple<Long> item=iterator.next();
            Optional<UserWanXian> op=wanXianLogic.getUserWanXian(item.getValue(),type);
            uids[index]=item.getValue();
            if (op.isPresent()){
                String baseScore= StrUtil.toNumStr(3,op.get().getBaseScore());
                String todayScore=StrUtil.toNumStr(3,op.get().getTodayScore());
                scores[index]=Double.valueOf(item.getScore().intValue()+"."+baseScore+todayScore);
            }else {
                scores[index]=0;
            }
            index++;
        }
        wanXianScoreRankService.addKeyVals(key,uids,scores);
        //前64名分8组
        List<Long> promotion=wanXianScoreRankService.getKeysByRank(key,1,64);
        List<Long> participants=ListUtil.copyList(promotion,Long.class);
        //挑出前8的玩家
        List<Long> leaders=promotion.subList(0,8);
        promotion=promotion.stream().filter(p->!leaders.contains(p)).collect(Collectors.toList());
        Map<String,String> groupMap=new HashMap<>();
        List<WanXianMatchEntity> matchs=new ArrayList<>();
        for (int i=1;i<=8;i++){
            List<Long> items=PowerRandom.getRandomsFromList(promotion,7);
            items.add(leaders.get(i-1));
            Collections.shuffle(items);
            List<RDWanXian.RDFightLog> group=new ArrayList<>();
            int order=1;
            for (int j=0;j<8;j++){
                //对手分配
                RDWanXian.RDFightLog log=RDWanXian.RDFightLog.instance(items.get(j),items.get(++j));
                WanXianMatchEntity entity=WanXianMatchEntity.instance(log,3,gid,currentType);
                String vidKey=WanXianEmailEnum.EMAIL_ELIMINATION_SERIES_RACE_1.getVal()+"N"+i+"N"+order;
                order++;
                entity.setVidKey(vidKey);
                log.setVidKey(vidKey);
                matchs.add(entity);
                group.add(log);
                updateGroup(log,type,String.valueOf(i));
            }
            promotion.removeAll(items);
            groupMap.put("group_"+i, JSONUtil.toJson(group));
        }
        wanXianSeasonService.addGroup(gid, type, groupMap);
        wanXianLogDbHandler.logMatchs(matchs);
        for (Long participant : participants) {
            WanXianEventPublisher.pubEPWanXianInto64(participant);
        }
    }

    private void updateGroup(RDWanXian.RDFightLog log, int type, String group){
        UserWanXian userWanXian=wanXianLogic.getOrCreateUserWanXian(log.getP1().getUid(),type);
        userWanXian.setGroupNumber(group);
        gameUserService.updateItem(userWanXian);
        UserWanXian userWanXian2=wanXianLogic.getOrCreateUserWanXian(log.getP2().getUid(),type);
        userWanXian2.setGroupNumber(group);
        gameUserService.updateItem(userWanXian2);
    }

    public List<WanXianMatchEntity> fightMatch(int gid,List<Long> uids, WanXianEmailEnum emailEnum,String currentKey,String saveKey,int seed,int type){
        int currentType=wanXianLogic.getTypeRace(type,gid);
        List<WanXianMatchEntity> matchs=new ArrayList<>();
        for (int i=0;i<uids.size();i++){
            WanXianMatchEntity entity=new WanXianMatchEntity();
            entity.setP1(uids.get(i));
            entity.setP2(getOppont(uids,i+seed));
            entity.setSeason(WanXianTool.getThisSeason());
            entity.setGid(gid);
            entity.setVidKey(saveKey+"N"+i);
            entity.setWeekday(DateUtil.getToDayWeekDay());
            entity.setWxType(currentType);
            matchs.add(entity);
        }
        wanXianLogDbHandler.logMatchs(matchs);
        List<RDWanXian.RDFightLog> fightLogs=new ArrayList<>();
        for (WanXianMatchEntity entity:matchs){
            RDWanXian.RDFightLog fight= RDWanXian.RDFightLog.instance(entity);
            doFightLogic(gid,type,emailEnum,fight);
            fightLogs.add(fight);
        }
        saveData(fightLogs,currentKey,gid,type);
        return matchs;
    }

    @Override
    public void saveFightLog(int gid, int type,WanXianEmailEnum emailEnum,RDWanXian.RDFightLog log, CombatVideo combatVideo) {
        RDWanXian.RDUser winner=log.getWinner()==1?log.getP1():log.getP2();
        RDWanXian.RDUser loser=log.getWinner()==1?log.getP2():log.getP1();
        int score=6;
        if (winner.getUid()>0){
            UserWanXian userWanXian=wanXianLogic.getOrCreateUserWanXian(winner.getUid(),type);
            score=userWanXian.getContinuingWins()/2+score;
            userWanXian.updateRaceRes(true,emailEnum);
            userWanXian.addTodayScore(score);
            userWanXian.addWinUid(loser.getUid());
            userWanXian.getFightLogs().add(log.getVidKey());
            gameUserService.updateItem(userWanXian);
        }
        winner.setScore(String.valueOf(score));
        wanXianWinRankService.incVal(wanXianWinRankService.getBaseKey(gid,type),winner.getUid(),1);
        if (loser.getUid()>0){
            UserWanXian userWanXian=wanXianLogic.getOrCreateUserWanXian(loser.getUid(),type);
            userWanXian.updateRaceRes(false,emailEnum);
            userWanXian.addFailUid(winner.getUid());
            userWanXian.getFightLogs().add(log.getVidKey());
            gameUserService.updateItem(userWanXian);
        }
    }

    @Override
    public void sendEliminateMail(int gid,int type,int order) {
        //先获取64~65名的分数是否相同
        String key=wanXianScoreRankService.getSoreRankKey(gid,type,WanXianEmailEnum.EMAIL_QUALIFYING_RACE_8);
        //前64名分8组
        int count=wanXianScoreRankService.getCount(key);
        List<Long> eliminates=wanXianScoreRankService.getKeysByRank(key,65,count);
        Set<Long> receiverUids=eliminates.stream().collect(Collectors.toSet());
        WanXianEmail email=WanXianTool.getEmail(type,WanXianEmailEnum.EMAIL_FAIL);
        String content=String.format(email.getContent(), "资格赛");
        List<Award> awards=WanXianTool.getRankAwards(100,type);
        mailService.sendAwardMail(email.getTitle(), content, receiverUids, awards);
    }

    @Override
    public void clear(int gid,int type,int weekday) {
    	 String currentKey=wanXianScoreRankService.getSoreRankKey(gid,type,null);
         List<Long> uids=wanXianScoreRankService.getAllItemKeys(currentKey);
         for (long uid:uids) {
			Optional<UserWanXian> op=wanXianLogic.getUserWanXian(uid,type);
			if (op.isPresent()) {
				UserWanXian userWanXian=op.get();
				userWanXian.setTodayScore(0);
				userWanXian.setContinuingWins(0);
				userWanXian.setFightFail(null);
				userWanXian.setFightWin(null);
				userWanXian.setGroupNumber(null);
				userWanXian.setLastWin(0);
				List<String> removeList=new ArrayList<String>();
				if (weekday==2) {
					for (String str:userWanXian.getFightLogs()) {
						 if (str.indexOf("13002")>-1 || str.indexOf("13152")>-1 ||str.indexOf("13302") >-1||str.indexOf("13452")>-1) {
							removeList.add(str);
						}
					}
					userWanXian.getFightLogs().removeAll(removeList);
				}else {
					userWanXian.getFightLogs().clear();
				}
				gameUserService.updateItem(userWanXian);
			}
		}
    }
}
