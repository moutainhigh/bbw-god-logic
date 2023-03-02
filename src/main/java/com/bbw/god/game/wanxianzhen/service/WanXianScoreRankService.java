package com.bbw.god.game.wanxianzhen.service;

import com.bbw.god.game.rank.BaseRankService;
import com.bbw.god.game.wanxianzhen.WanXianEmailEnum;
import com.bbw.god.game.wanxianzhen.WanXianTool;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author lwb 万仙阵积分榜
 * @date 2020/4/22 11:38
 */
@Service
public class WanXianScoreRankService extends BaseRankService<Long> {
    /**
     * 获取榜单key
     * @return
     */
    public String getSoreRankKey(int gid,int type,WanXianEmailEnum em) {
        if (em==null){
            return getBaseKey(gid,type);
        }
        gid=WanXianTool.getTargetGid(gid);
        if (WanXianLogic.TYPE_REGULAR_RACE!=type){
            return "game:wanXian:"+ WanXianTool.getThisSeason() +":"+gid+":"+type+"_score_"+em.getVal();
        }
        return "game:wanXian:"+ WanXianTool.getThisSeason() +":"+gid+":score_"+em.getVal();
    }

    /**
     * 小组赛分数榜单
     * @param gid
     * @return
     */
    public String getGroupStageBaseKey(int gid,int type,WanXianEmailEnum emailEnum,String group) {
        gid=WanXianTool.getTargetGid(gid);
        String key="game:wanXian:"+ WanXianTool.getThisSeason() +":"+gid+":";
        if (WanXianLogic.TYPE_REGULAR_RACE!=type){
            key+=type+"_";
        }
        if (emailEnum==null){
            return key+group+"groupStageScore";
        }
        return key+group+"groupStageScore_"+emailEnum.getSeq();
    }

    /**
     * 冠军预测
     * @param gid
     * @param order  4为4强 8为8强
     * @return
     */
    public String getChampionPredictionKey(int gid,int type,int order){
        gid=WanXianTool.getTargetGid(gid);
        if (order!=4 && order!=8){
            order=4;
        }
        if (WanXianLogic.TYPE_REGULAR_RACE!=type){
            return "game:wanXian:"+ WanXianTool.getThisSeason() +":"+gid+":"+type+"_"+order+"cp";
        }
        return "game:wanXian:"+ WanXianTool.getThisSeason() +":"+gid+":"+order+"cp";
    }

    @Override
    public boolean orderByVal() {
        return false;
    }

    public String getBaseKey(int gid,int type) {
        gid=WanXianTool.getTargetGid(gid);
        if (WanXianLogic.TYPE_REGULAR_RACE!=type){
            return "game:wanXian:"+ WanXianTool.getThisSeason() +":"+gid+":"+type+"_score";
        }
        return "game:wanXian:"+ WanXianTool.getThisSeason() +":"+gid+":score";
    }

    /**
     * 加入万仙阵常规赛
     * @param uid
     */
    public void jionWanXianRegularRace(String key,long uid){
        incVal(key,uid,0);
    }

    /**
     * 加入万仙阵常规赛
     * @param uids
     */
    public void jionWanXianRegularRace(String key, List<Long> uids){
        double[] socors=new double[uids.size()];
        for (int i=0;i<uids.size();i++){
            socors[i]=packVal(0);
        }
        redisZSetUtil.add(key,socors,uids.toArray(new Long[uids.size()]));
    }


    public void updateVal(String key,Long item,int val){
        redisZSetUtil.add(key,item,val);
    }

    public void addHistorySeason(int gid,int type,long uid,int val){
        gid=WanXianTool.getTargetGid(gid);
        String key="game:wanXian:"+ WanXianTool.getThisSeason() +":"+gid+":seasonRank";
        if (WanXianLogic.TYPE_REGULAR_RACE!=type){
            key="game:wanXian:"+ WanXianTool.getThisSeason() +":"+gid+":"+type+"_seasonRank";
        }
        incVal(key,uid,val);
        redisZSetUtil.expire(key,30,TimeUnit.DAYS);
    }
    public List<Long> getHistorySeasonRank(int gid,int type,Integer order){
        gid=WanXianTool.getTargetGid(gid);
        return getHistorySeasonRankBySeason(gid, type, WanXianTool.getSeasonByOrder(type,order,gid));
    }
    
    public List<Long> getHistorySeasonRankBySeason(int gid,int type,int season){
        gid=WanXianTool.getTargetGid(gid);
        String key="game:wanXian:"+ season +":"+gid+":seasonRank";
        if (WanXianLogic.TYPE_REGULAR_RACE!=type){
            key="game:wanXian:"+ season +":"+gid+":"+type+"_seasonRank";
        }
        List<Long> uids=getKeysByRank(key,1,8);
        if (uids!=null && !uids.isEmpty()){
            Collections.reverse(uids);
            return uids;
        }
        return new ArrayList<>();
    }

    /**
     * 设置各个排名的过期时间：周一周二分数，小组赛分数，预测
     * @param gid
     * @param season
     * @param time
     * @param timeUnit
     */
    public void  expire(int gid,int season,long time, TimeUnit timeUnit){
        gid=WanXianTool.getTargetGid(gid);
        List<Integer> qualifyingVals=WanXianEmailEnum.getQualifyingVals();
        for (int val:qualifyingVals){
            String key2000="game:wanXian:"+ season +":"+gid+":2000_score_"+val;
            redisZSetUtil.expire(key2000,time,timeUnit);
            String key="game:wanXian:"+ season +":"+gid+":score_"+val;
            redisZSetUtil.expire(key,time,timeUnit);
        }
        redisZSetUtil.expire("game:wanXian:"+ season +":"+gid+":score",time,timeUnit);
        redisZSetUtil.expire("game:wanXian:"+ season +":"+gid+":2000_score",time,timeUnit);
        redisZSetUtil.expire("game:wanXian:"+ season +":"+gid+":score_"+WanXianEmailEnum.EMAIL_QUALIFYING_RACE_8.getVal()+"_old",time,timeUnit);
        redisZSetUtil.expire("game:wanXian:"+ season +":"+gid+":2000_score_"+WanXianEmailEnum.EMAIL_QUALIFYING_RACE_8.getVal()+"_old",time,timeUnit);
        String[] groups={"A","B"};
        redisZSetUtil.expire("game:wanXian:"+season +":"+gid+":AgroupStageScore",time,timeUnit);
        redisZSetUtil.expire("game:wanXian:"+season +":"+gid+":BgroupStageScore",time,timeUnit);
        redisZSetUtil.expire("game:wanXian:"+season +":"+gid+":2000_AgroupStageScore",time,timeUnit);
        redisZSetUtil.expire("game:wanXian:"+season +":"+gid+":2000_BgroupStageScore",time,timeUnit);
        for (String group:groups){
            for (int i=1;i<7;i++){
                redisZSetUtil.expire("game:wanXian:"+season +":"+gid+":"+group+"groupStageScore_"+i,time,timeUnit);
                redisZSetUtil.expire("game:wanXian:"+season +":"+gid+":2000_"+group+"groupStageScore_"+i,time,timeUnit);
            }
        }
        int[] orders={4,8};
        for (int order:orders){
            redisZSetUtil.expire("game:wanXian:"+ season +":"+gid+":"+order+"cp",time,timeUnit);
            redisZSetUtil.expire("game:wanXian:"+ season +":"+gid+":2000_"+order+"cp",time,timeUnit);
        }

    }
}
