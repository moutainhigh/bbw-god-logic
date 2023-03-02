package com.bbw.god.game.wanxianzhen.service;

import com.bbw.common.JSONUtil;
import com.bbw.common.PowerRandom;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.wanxianzhen.WanXianCard;
import com.bbw.god.game.wanxianzhen.WanXianRobot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 万仙阵机器人
 * @author lwb
 * @date 2020/5/7 14:36
 */
@Service
public class WanXianRobotService {
    @Autowired
    private RedisHashUtil<Long,String> hashUtil;

    private static final String BASE_KEY="game:wanxian:robots";

    /**
     * 获取指定数量的机器人
     * @param need
     * @return
     */
    public List<Long> randomRobots(int need){
        int count=hashUtil.getSize(BASE_KEY).intValue();
        if (count<need){
            buildRobotList(count,need);
        }
        Set<Long> robotIds=hashUtil.getFieldKeySet(BASE_KEY);
        return PowerRandom.getRandomsFromList(need,robotIds.stream().collect(Collectors.toList()));
    }

    /**
     * 生成指定数量的机器人
     * @param order
     * @param max
     */
    public void buildRobotList(int order,long max){
        Map<Long,String> robots=new HashMap<>();
        for (int i=order;i<max;i++){
            WanXianRobot robot=initRobot(i);
            robots.put(robot.getRobotId(), JSONUtil.toJson(robot));
        }
        hashUtil.putAllField(BASE_KEY,robots);
    }

    /**
     * 获取机器人卡牌
     * @param robotId
     * @return
     */
    public List<WanXianCard> getRobotCards(long robotId){
        String robotJson=hashUtil.getField(BASE_KEY,robotId);
        WanXianRobot robot=JSONUtil.fromJson(robotJson,WanXianRobot.class);
        return robot.getRegularRaceCards();
    }
    /**
     * 机器人生成卡牌范围为0类卡
     * 卡牌数量：15张
     * 构成：5星卡*1,4星卡*4,3星卡*10
     */
    public WanXianRobot initRobot(int order){
        WanXianRobot robot=new WanXianRobot();
        long id=-990000000000001L-order;
        robot.setRobotId(id);
        //5星卡*1
        CfgCardEntity star5=CardTool.getRandomNotSpecialCard(5);
        robot.addCard(star5.getId());
        //4星卡*4
        for (int i=0;i<4;i++){
            CfgCardEntity star4=CardTool.getRandomNotSpecialCard(4);
            robot.addCard(star4.getId());
        }
        //3星卡*10
        for (int i=0;i<10;i++){
            CfgCardEntity star3=CardTool.getRandomNotSpecialCard(3);
            robot.addCard(star3.getId());
        }
        return robot;
    }
}
