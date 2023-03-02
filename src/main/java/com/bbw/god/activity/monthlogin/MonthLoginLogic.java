package com.bbw.god.activity.monthlogin;

import com.bbw.common.*;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author：lwb
 * @date: 2021/2/26 9:37
 * @version: 1.0
 */
@Slf4j
@Service
public class MonthLoginLogic {
    @Autowired
    private RedisHashUtil<String,String> hashUtil;
    private static final String key="game:monthLoginEvent:";
    @Autowired
    private ActivityService activityService;
    @Autowired
    private GameUserService userService;
    private static final List<Integer> emptys= Arrays.asList(221,222,223,224,314,315,316,317);

    public String getKey(int gid){
        return key+gid+":"+DateUtil.toDateInt(DateUtil.getMonthBegin(DateUtil.now(),0));
    }

    public void buildRd(long uid,RDActivityList rd){
        int gid=userService.getActiveGid(uid);
        String key=getKey(gid);
        String goodField = hashUtil.getField(key, DateUtil.getTodayInt() + "_good");
        String badField = hashUtil.getField(key, DateUtil.getTodayInt() + "_bad");
        if (StrUtil.isBlank(goodField) || StrUtil.isBlank(badField)){
            initTodayEvent(userService.getActiveSid(uid));
            goodField = hashUtil.getField(key, DateUtil.getTodayInt() + "_good");
            badField = hashUtil.getField(key, DateUtil.getTodayInt() + "_bad");
        }
        rd.setBadEvents(new ArrayList<>());
        rd.setGoodEvents(new ArrayList<>());
        if (StrUtil.isNotBlank(goodField)){
            rd.setGoodEvents(JSONUtil.fromJsonArray(goodField,Integer.class));
        }
        if (StrUtil.isNotBlank(badField)){
            rd.setBadEvents(JSONUtil.fromJsonArray(badField,Integer.class));
        }
        rd.setGoodEvents(rd.getGoodEvents().stream().filter(p->!emptys.contains(p)).collect(Collectors.toList()));
        rd.setBadEvents(rd.getBadEvents().stream().filter(p->!emptys.contains(p)).collect(Collectors.toList()));
    }

    public void initTodayEvent(int sid){
        CfgMonthLogin config = Cfg.I.getUniqueConfig(CfgMonthLogin.class);
        List<Integer> bads=randomEvents(config.getBadEvents(),4);
        List<Integer> goods=new ArrayList<>();
        int toDayNumber=DateUtil.getTodayInt()%100;
        for (CfgMonthLogin.EventInfo event : config.getActivityEvents()) {
            if (ListUtil.isNotEmpty(event.getDays())){
                //具体天类型
                if (event.getDays().contains(toDayNumber)){
                    goods.add(event.getId());
                }
            }else if (ListUtil.isNotEmpty(event.getActivitys())){
                //具体活动类型
                for (Integer id : event.getActivitys()) {
                    ActivityEnum activityEnum = ActivityEnum.fromValue(id);
                    IActivity activity = activityService.getActivity(sid, activityEnum);
                    if (activity!=null && activity.ifTimeValid()){
                        goods.add(event.getId());
                        break;
                    }
                }
            }else if (event.getId()==108){
                //斗法
                if (DateUtil.isWeekDay(6)){
                    goods.add(event.getId());
                }
            }
        }
        int num=4;
        if (goods.size()>0){
            num=Math.max(0,6-goods.size());
        }
        goods.addAll(randomEvents(config.getGoodEvents(),num));
        String key=getKey(ServerTool.getServerGroup(sid));
        hashUtil.putField(key,DateUtil.getTodayInt()+"_good",JSONUtil.toJson(goods));
        hashUtil.putField(key,DateUtil.getTodayInt()+"_bad",JSONUtil.toJson(bads));
    }

    public List<Integer> randomEvents(List<CfgMonthLogin.EventInfo> list,int num){
        if (num<=0){
            return new ArrayList<>();
        }
        int count = list.stream().collect(Collectors.summingInt(CfgMonthLogin.EventInfo::getProbability));
        List<Integer> ids=new ArrayList<>();
        while (ids.size()<num){
            int seed = PowerRandom.getRandomBySeed(count);
            int sum=0;
            for (CfgMonthLogin.EventInfo info : list) {
                sum+=info.getProbability();
                if (sum>=seed){
                    if (!ids.contains(info.getId())){
                        ids.add(info.getId());
                    }
                    break;
                }
            }
        }
        return ids;
    }

    /**
     * 是否存在该事件
     * @param type
     * @return
     */
    public boolean isExistEvent(long uid,MonthLoginEnum type){
        try {
            String fieldKey="";
            if (type.getId()>300){
                fieldKey=DateUtil.getTodayInt() + "_bad";
            }else {
                fieldKey=DateUtil.getTodayInt() + "_good";
            }
            int gid=userService.getActiveGid(uid);
            String baseKey=getKey(gid);
            String json = hashUtil.getField(baseKey, fieldKey);
            if (StrUtil.isNotBlank(json)){
                List<Integer> list = JSONUtil.fromJsonArray(json, Integer.class);
                return list.contains(type.getId());
            }else {
                int sid=userService.getActiveSid(uid);
                initTodayEvent(sid);
                json = hashUtil.getField(baseKey, fieldKey);
                if (StrUtil.isNotBlank(json)){
                    List<Integer> list = JSONUtil.fromJsonArray(json, Integer.class);
                    return list.contains(type.getId());
                }
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
