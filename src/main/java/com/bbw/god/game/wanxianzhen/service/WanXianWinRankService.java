package com.bbw.god.game.wanxianzhen.service;

import com.bbw.common.ListUtil;
import com.bbw.god.game.rank.BaseRankService;
import com.bbw.god.game.wanxianzhen.WanXianTool;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author lwb 万仙阵胜利榜
 * @date 2020/4/22 11:38
 */
@Service
public class WanXianWinRankService extends BaseRankService<Long> {
    /**
     * 获取榜单key
     * @return
     */
    public String getBaseKey(int gid,int type) {
        gid=WanXianTool.getTargetGid(gid);
        if (WanXianLogic.TYPE_REGULAR_RACE!=type){
            return "game:wanXian:"+ WanXianTool.getThisSeason() +":"+gid+":"+type+"_win";
        }
        return "game:wanXian:"+ WanXianTool.getThisSeason() +":"+gid+":win";
    }

    /**
     * 获取指定胜场的玩家
     * @param gid
     * @param num
     * @return
     */
    public List<Long> getUidByWinTimes(int gid,int type,int num){
        gid=WanXianTool.getTargetGid(gid);
        if (num<0){
            return new ArrayList<>();
        }
        Set<Long> uidSet=redisZSetUtil.rangeByScore(getBaseKey(gid,type),num,num);
        if (uidSet==null || uidSet.isEmpty()){
            return new ArrayList<>();
        }
        return uidSet.stream().collect(Collectors.toList());
    }

    @Override
    protected double packVal(int val) {
        return Double.valueOf(val);
    }

    @Override
    public boolean orderByVal() {
        return false;
    }

    public void rest(int gid,int type){
        gid=WanXianTool.getTargetGid(gid);
        List<Long> uidList=redisZSetUtil.rangeByScore(getBaseKey(gid,type),1,8).stream().collect(Collectors.toList());
        if (ListUtil.isEmpty(uidList)) {
			return;
		}
        double[] soc=new double[uidList.size()];
        Long[] uids=new Long[uidList.size()];
        for (int i=0;i<uidList.size();i++){
            soc[i]=0;
            uids[i]=uidList.get(i);
        }
        redisZSetUtil.remove(getBaseKey(gid,type),uids);
        redisZSetUtil.add(getBaseKey(gid,type),soc,uids);
    }

    public void expire(int gid,int season,long time, TimeUnit timeUnit){
        gid=WanXianTool.getTargetGid(gid);
        redisZSetUtil.expire("game:wanXian:"+ season +":"+gid+":win",time,timeUnit);
        redisZSetUtil.expire("game:wanXian:"+ season +":"+gid+":2000_win",time,timeUnit);
    }
}
