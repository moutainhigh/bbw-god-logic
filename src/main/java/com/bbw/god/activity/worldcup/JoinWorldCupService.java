package com.bbw.god.activity.worldcup;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 保存参与世界杯活动存redis
 * @author: hzf
 * @create: 2022-11-15 10:08
 **/
@Service
public class JoinWorldCupService {

    @Autowired
    private RedisListUtil<Long> redisListUtil;

    private final static String WORLD_CUP = "worldCup" ;
    private final static String JOIN_ACTIVITY_USER = "joinActivityUser" ;

    @PostConstruct
    public void init(){
        for (WorldCupTypeEnum worldCupType : WorldCupTypeEnum.values()) {
            redisListUtil.expire(getWorldCupKey(worldCupType), DateUtil.SECOND_ONE_DAY * 30);
        }
    }

    /**
     * 参加活动
     * @param worldCupType
     */
    public void joinActivity(long uid,WorldCupTypeEnum worldCupType){
        List<Long> uids = redisListUtil.get(getWorldCupKey(worldCupType));
        if (!uids.contains(uid)) {
            redisListUtil.rightPush(getWorldCupKey(worldCupType),uid);
        }
    }

    /**
     * 获取参加活动的uid集合
     * @return
     */
    public  List<Long> getJoinActivityUids(WorldCupTypeEnum worldCupType){
        return  redisListUtil.get(getWorldCupKey(worldCupType));
    }
    private  String getWorldCupKey(WorldCupTypeEnum worldCupType){
        return "game" + SPLIT + WORLD_CUP + SPLIT + JOIN_ACTIVITY_USER + SPLIT + worldCupType.getName();
    }




}
