package com.bbw.god.game.wanxianzhen.service;

import com.bbw.common.JSONUtil;
import com.bbw.common.StrUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.game.wanxianzhen.RDWanXian;
import com.bbw.god.game.wanxianzhen.WanXianTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 万仙阵战斗日志
 * @author lwb
 * @date 2020/5/13 12:19
 */
@Service
public class WanXianFightLogsService{
    @Autowired
    private RedisHashUtil<String,String> hashUtil;
    @Autowired
    private WanXianLogic wanXianLogic;
    public String getBaseKey(int gid,int type) {
        gid=WanXianTool.getTargetGid(gid);
        if (WanXianLogic.TYPE_SPECIAL_RACE==type){
            return "game:wanXian:"+ WanXianTool.getThisSeason() +":"+gid+":"+type+"_fightLogs";
        }
        return "game:wanXian:"+ WanXianTool.getThisSeason() +":"+gid+":fightLogs";
    }

    public void addFightLogs(int gid,int type, Map<String,String> vals){
        gid=WanXianTool.getTargetGid(gid);
        hashUtil.putAllField(getBaseKey(gid,type),vals);
    }

    /**
     * 根据key获取战斗日志,不含玩家详细信息
     * @param gid
     * @param keys
     * @return
     */
    public List<RDWanXian.RDFightLog> getFightLogs(int gid,int type,List<String> keys){
        String baseKey=getBaseKey(gid,type);
        List<RDWanXian.RDFightLog> fightLogs=new ArrayList<>();
        Collection<String> cc=keys;
        List<String> jsons=hashUtil.getFieldBatch(baseKey,cc);
        for (String json:jsons){
            if (json==null){
                continue;
            }
            RDWanXian.RDFightLog log= JSONUtil.fromJson(json, RDWanXian.RDFightLog.class);
            fightLogs.add(log);
        }
        return fightLogs;
    }
    /**
     * 根据key获取战斗日志,含玩家详细信息
     * @param gid
     * @param keys
     * @return
     */
    public List<RDWanXian.RDFightLog> getFightLogsForUserInfo(int gid,int type,List<String> keys){
        String baseKey=getBaseKey(gid,type);
        List<RDWanXian.RDFightLog> fightLogs=new ArrayList<>();
        Collection<String> cc=keys;
        List<String> jsons=hashUtil.getFieldBatch(baseKey,cc);
        for (String json:jsons){
            if (StrUtil.isBlank(json)){
                continue;
            }
            RDWanXian.RDFightLog log= JSONUtil.fromJson(json, RDWanXian.RDFightLog.class);
            wanXianLogic.getUserInfo(log.getP1());
            wanXianLogic.getUserInfo(log.getP2());
            fightLogs.add(log);
        }
        return fightLogs;
    }

    /**
     * 设置战斗记录的过期时间
     * @param gid
     * @param season
     * @param time
     * @param timeUnit
     */
    public void expire(int gid,int season,long time, TimeUnit timeUnit){
        gid=WanXianTool.getTargetGid(gid);
        String key2000= "game:wanXian:"+ season +":"+gid+":2000_fightLogs";
        String key=  "game:wanXian:"+ season +":"+gid+":fightLogs";
        hashUtil.expire(key2000,time,timeUnit);
        hashUtil.expire(key,time,timeUnit);
    }
}
