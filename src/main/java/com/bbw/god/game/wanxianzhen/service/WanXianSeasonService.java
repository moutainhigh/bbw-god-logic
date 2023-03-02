package com.bbw.god.game.wanxianzhen.service;

import com.bbw.common.JSONUtil;
import com.bbw.common.StrUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.game.wanxianzhen.RDWanXian;
import com.bbw.god.game.wanxianzhen.WanXianCard;
import com.bbw.god.game.wanxianzhen.WanXianSpecialType;
import com.bbw.god.game.wanxianzhen.WanXianTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lwb
 * @date 2020/4/26 16:50
 */
@Service
@Slf4j
public class WanXianSeasonService {
    @Autowired
    private RedisHashUtil<String,String> redisHashUtil;
    @Autowired
    private WanXianLogic wanXianLogic;
    private static final String MON_SEED_KEY="_Mon_seed";
    private String getKey(int gid,int type){
        gid=WanXianTool.getTargetGid(gid);
        if (WanXianLogic.TYPE_REGULAR_RACE!=type){
            return "game:wanXian:"+ WanXianTool.getThisSeason()+":"+gid+":"+type+"_group";
        }
        return "game:wanXian:"+ WanXianTool.getThisSeason()+":"+gid+":group";
    }
    
    private String getKey(int gid,int type,int order){
        gid=WanXianTool.getTargetGid(gid);
        if (WanXianLogic.TYPE_REGULAR_RACE!=type){
            return "game:wanXian:"+ WanXianTool.getSeasonByOrder(type, order,gid)+":"+gid+":"+type+"_group";
        }
        return "game:wanXian:"+ WanXianTool.getSeasonByOrder(type, order,gid)+":"+gid+":group";
    }

    /**
     * 获取周一资格赛的随机数，没有则返回0
     * @param gid
     * @return
     */
    public int getMondaySeed(int gid,int type){
        gid=WanXianTool.getTargetGid(gid);
        String val=redisHashUtil.getField(getKey(gid,type),gid+MON_SEED_KEY);
        if (val==null){
            return 0;
        }
        try {
           int ival=Integer.parseInt(val);
           return ival;
        }catch (NumberFormatException e){
            log.error("类型转换错误，资格赛随机数："+val);
        }
        return 0;
    }

    /**
     * 保存周一资格赛的 随机数
     * @param gid
     * @param seed
     */
    public void setMondaySeed(int gid,int type,int seed){
        gid=WanXianTool.getTargetGid(gid);
        redisHashUtil.putField(getKey(gid,type),gid+MON_SEED_KEY,String.valueOf(seed));
    }


    public void addGroup(int gid,int type, Map<String,String> group){
        redisHashUtil.putAllField(getKey(gid,type),group);
    }

    public void updateGroup(int gid,int type,String key,List<RDWanXian.RDFightLog> fights){
        redisHashUtil.putField(getKey(gid,type),key,JSONUtil.toJson(fights));
    }

    public void addVal(int gid,int type,String itemKey,String val){
        redisHashUtil.putField(getKey(gid,type),itemKey,val);
    }

    public String getVal(int gid,int type,String itemKey){
        return redisHashUtil.getField(getKey(gid,type),itemKey);
    }

    /**
     * 获取比赛结束的场次
     * @param gid
     * @param type
     * @return
     */
    public Integer getRaceOverVal(int gid,int type){
        String str=redisHashUtil.getField(getKey(gid,type),"raceOver");
        if (StrUtil.isNotBlank(str)){
            return Integer.parseInt(str);
        }
        return 0;
    }

    /**
     * 设置结束比赛的场次
     * @param gid
     * @param type
     * @param val
     */
    public void setRaceOverVal(int gid,int type,int val){
        redisHashUtil.putField(getKey(gid,type),"raceOver",String.valueOf(val));
    }
    public List<RDWanXian.RDFightLog> getFightUsers(int gid,int type,String key){
        String json=redisHashUtil.getField(getKey(gid,type),key);
        if (json==null){
            return new ArrayList<>();
        }
        List<RDWanXian.RDFightLog> list=JSONUtil.fromJsonArray(json,RDWanXian.RDFightLog.class);
        for (RDWanXian.RDFightLog log:list){
            wanXianLogic.getUserInfo(log.getP1());
            wanXianLogic.getUserInfo(log.getP2());
        }
        return list;
    }
    public void addHistorySeasonCardGroup(int gid,int type,Long uid,String val){
        gid=WanXianTool.getTargetGid(gid);
        String key="game:wanXian:"+ WanXianTool.getThisSeason() +":"+gid+":seasonCardGroup";
        if (WanXianLogic.TYPE_REGULAR_RACE!=type){
            key="game:wanXian:"+ WanXianTool.getThisSeason() +":"+gid+":"+type+"_seasonCardGroup";
        }
        redisHashUtil.putField(key,uid.toString(),val);
        redisHashUtil.expire(key,30,TimeUnit.DAYS);
    }

    public void addHistorySeasonCardGroups(int gid,int type,int season,Map<String, String> vals){
        gid=WanXianTool.getTargetGid(gid);
        String key="game:wanXian:"+ season +":"+gid+":seasonCardGroup";
        if (WanXianLogic.TYPE_REGULAR_RACE!=type){
            key="game:wanXian:"+ season +":"+gid+":"+type+"_seasonCardGroup";
        }
        redisHashUtil.putAllField(key, vals);
    }
    public List<WanXianCard> getHistorySeasonCardGroup(int gid,int type,Integer order, Long uid){
        gid=WanXianTool.getTargetGid(gid);
        String key="game:wanXian:"+WanXianTool.getSeasonByOrder(type,order,gid)+":"+gid+":seasonCardGroup";
        if (WanXianLogic.TYPE_REGULAR_RACE!=type){
            key="game:wanXian:"+WanXianTool.getSeasonByOrder(type,order,gid)+":"+gid+":"+type+"_seasonCardGroup";
        }
        String json=redisHashUtil.getField(key,uid.toString());
        if (json==null){
            return new ArrayList<>();
        }
       return JSONUtil.fromJsonArray(json,WanXianCard.class);
    }

    public WanXianSpecialType getCurrentSpecialType(int gid){
        gid=WanXianTool.getTargetGid(gid);
        return WanXianSpecialType.fromVal(WanXianTool.getCurrentSpecialType(gid));
    }

    public WanXianSpecialType getSpecialTypeByOrder(int gid,Integer order){
        gid=WanXianTool.getTargetGid(gid);
        if (order==null){
            return getCurrentSpecialType(gid);
        }
        String keyStr=getKey(gid,WanXianLogic.TYPE_SPECIAL_RACE,order);
        String  type=redisHashUtil.getField(keyStr,"specialType");
        if (StrUtil.isBlank(type)) {
        	 return getCurrentSpecialType(gid);
		}
        return WanXianSpecialType.fromVal(Integer.valueOf(type));
    }
    public void addSpecialType(int gid){
        int specialType=WanXianTool.getCurrentSpecialType(gid);
        redisHashUtil.putField(getKey(gid,WanXianLogic.TYPE_SPECIAL_RACE),"specialType",String.valueOf(specialType));
    }

    /**
     * 设置过期时间
     * @param key
     * @param time
     * @param timeUnit
     */
    public void expire(String key, long time, TimeUnit timeUnit){
        redisHashUtil.expire(key,time,timeUnit);
    }
}
