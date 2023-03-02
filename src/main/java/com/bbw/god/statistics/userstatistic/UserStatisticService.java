package com.bbw.god.statistics.userstatistic;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.db.redis.RedisListUtil;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.chamberofcommerce.event.EPTaskFinished;
import com.bbw.god.server.redis.ServerRedisKey;
import com.bbw.mc.mail.MailAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author suchaobin
 * @title: UserCocTaskStatisticService
 * @projectName bbw-god-logic-server
 * @description: 玩家个人数据统计逻辑层
 * @date 2019/6/19 15:57
 */
@Service
public class UserStatisticService {
    @Autowired
    private GameUserService gameUserService;

    @Autowired
    private MailAction mailAction;

    @Autowired
    private RedisHashUtil<String, Object> redisHashUtil;

    @Autowired
    private RedisListUtil<Long> redisListUtil;

    private final long THREE_DAYS = 3 * 24 * 60 * 60L;

    /**
     * 玩家统计的基础key
     *
     * @param uid
     * @return
     */
    private String getBaseServerStatisticKey(Long uid) {
        int sid = gameUserService.getActiveSid(uid);
        return ServerRedisKey.PREFIX + ServerRedisKey.SPLIT + sid + ServerRedisKey.SPLIT + "statistic" + ServerRedisKey.SPLIT + DateUtil.getTodayInt() + ServerRedisKey.SPLIT + "0usr" + ServerRedisKey.SPLIT + uid;
    }

    /**
     * 玩家产出统计的key
     *
     * @param uid
     * @return
     */
    private String getServerStatisticOutputKey(Long uid, String awardName) {
        return getBaseServerStatisticKey(uid) + ServerRedisKey.SPLIT + "output" + ServerRedisKey.SPLIT + awardName;
    }

    /**
     * 玩家消耗统计的key
     *
     * @param uid
     * @return
     */
    private String getServerStatisticConsumeKey(Long uid, String awardName) {
        return getBaseServerStatisticKey(uid) + ServerRedisKey.SPLIT + "consume" + ServerRedisKey.SPLIT + awardName;
    }

    /**
     * 增加玩家产出统计值
     *
     * @param
     * @param
     * @param addNum
     */
    public void addOutput(long uid, WayEnum way, long addNum, Integer[] max, AwardEnum awardEnum) {
        String wayName = way.getName();
        redisHashUtil.increment(getServerStatisticOutputKey(uid, awardEnum.getName()), wayName, addNum);
        redisHashUtil.expire(getServerStatisticOutputKey(uid, awardEnum.getName()), THREE_DAYS);
        Map<String, Object> map = redisHashUtil.get(getServerStatisticOutputKey(uid, awardEnum.getName()));
        notify(uid, map, max, awardEnum);
    }

    /**
     * 增加玩家消耗统计值
     *
     * @param
     * @param
     * @param addNum
     */
    public void addConsume(long uid, WayEnum way, long addNum, AwardEnum awardEnum) {
        String wayName = way.getName();
        redisHashUtil.increment(getServerStatisticConsumeKey(uid, awardEnum.getName()), wayName, addNum);
        redisHashUtil.expire(getServerStatisticConsumeKey(uid, awardEnum.getName()), THREE_DAYS);
    }

    /**
     * 用来判断用户今天获取某一类资源时，能否满足通知条件，如果可以，则通知运营人员
     *
     * @param uid       玩家id
     * @param redisMap  redis通过key获得的map
     * @param max       用来判断的数组
     * @param awardEnum 资源枚举类
     */
    private void notify(Long uid, Map<String, Object> redisMap, Integer[] max, AwardEnum awardEnum) {
        CfgGame cfgGame = Cfg.I.getUniqueConfig(CfgGame.class);
        // 白名单账号直接跳过
        String account = gameUserService.getAccount(uid);
        if (cfgGame.isWhiteAccount(account)) {
            return;
        }
        if (redisMap.get("已通知" + max[max.length - 1] + awardEnum.getName()) == null) {
            int sum = 0;
            for (String key : redisMap.keySet()) {
                if (!addCardNeedNotify(key, awardEnum)) {
                    continue;
                }
                if (!key.equals(WayEnum.RECHARGE.getName()) && !booleanKey(key, max, awardEnum)) {
                    sum += (int) redisMap.get(key);
                }
            }
            Integer notifyNum = getNotifyNum(redisMap, max, awardEnum.getName());
            for (int i = notifyNum; i < max.length; i++) {
                String str = "";
                String msg = "";
                if (sum >= max[i]) {
                    //再达到通知阀值时再获取
                    String nickname = gameUserService.getGameUser(uid).getRoleInfo().getNickname();
                    int sid = gameUserService.getActiveSid(uid);
                    String sname = ServerTool.getServer(sid).getName();
                    msg = "服务器:" + sname + ",\n游戏昵称:" + nickname + ",\n";
                    for (int j = 0; j < i; j++) {
                        if (redisMap.get("已通知" + max[j] + awardEnum.getName()) != null) {
                            str = str + "当日获得" + awardEnum.getName() + "超过" + max[j] + "的时间为:" + getHmsTime((int) (redisMap.get(
                                    "已通知" + max[j] + awardEnum.getName()))) + ",\n";
                        }
                    }
                    redisHashUtil.putField(getServerStatisticOutputKey(uid, awardEnum.getName()),
                            "已通知" + max[i] + awardEnum.getName(), DateUtil.toHMSInt(new Date()));
                    redisMap.put("已通知" + max[i] + awardEnum.getName(), DateUtil.toHMSInt(new Date()));
                    msg = msg + "当日所获" + awardEnum.getName() + "超过" + max[i] + ",\n";
                    str = str + "当日获得" + awardEnum.getName() + "超过" + max[i] + "的时间为:" + getHmsTime(DateUtil.toHMSInt(new Date())) + ",\n";
                    int num = redisMap.get(WayEnum.RECHARGE.getName()) == null ? 0 :
                            (int) redisMap.get(WayEnum.RECHARGE.getName());
                    str = msg + str + "当日充值所获" + awardEnum.getName() + "数量为:" + num + ",未计入当日所获总数内。";
                    mailAction.notifyOperator("每日" + awardEnum.getName() + "事件", str);
                }
            }
        }
    }

    /**
     * @description: 获取还未通知的资源数组的下标，如果已经全部通知过了，则返回整个数组长度，否则返回还未通知的下个下标 @param @return @throws @author suchaobin @date 2019/7/16 15:09
     */
    private Integer getNotifyNum(Map<String, Object> map, Integer[] max, String awardName) {
        if (map.get("已通知" + max[max.length - 1] + awardName) != null) {
            return max.length;
        }
        for (int i = 0; i < max.length; i++) {
            if (map.get("已通知" + max[i] + awardName) == null) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @description: 判断key是不是通知的key，是就返回true，否则返回false @param @return @throws @author suchaobin @date 2019/7/15 9:54
     */
    private Boolean booleanKey(String key, Integer[] max, AwardEnum awardEnum) {
        for (int i = 0; i < max.length; i++) {
            if (key.equals("已通知" + max[i] + awardEnum.getName())) {
                return true;
            }
        }
        return false;
    }

    private String getHmsTime(int hhmmss) {
        int h = hhmmss / 10000;
        int m = (hhmmss / 100) % 100;
        int s = hhmmss % 100;
        String time = h + "点" + m + "分" + s + "秒";
        return time;
    }

    /**
     * 玩家商会任务完成情况统计的key
     *
     * @param uid
     * @return
     */
    private String getUserCocTaskKey(Long uid, EPTaskFinished evTaskFinished) {
        return getBaseServerStatisticKey(uid) + ServerRedisKey.SPLIT + "CocTask" + ServerRedisKey.SPLIT + "level" + ServerRedisKey.SPLIT + evTaskFinished.getLevel();
    }

    private String getUserCocTaskField(EPTaskFinished evTaskFinished) {
        String field = evTaskFinished.getDescription();
        return field;
    }

    public void addCocTask(EPTaskFinished evTaskFinished) {
        Long uid = evTaskFinished.getGuId();
        String userCocTaskKey = getUserCocTaskKey(uid, evTaskFinished);
        redisHashUtil.increment(userCocTaskKey, getUserCocTaskField(evTaskFinished), 1);
        redisHashUtil.expire(userCocTaskKey, THREE_DAYS);
    }

    public void addRMBUser(Long uid) {
        List<Long> uids = redisListUtil.get("RMBUser");
        if (uids == null) {
            uids = new ArrayList<Long>();
        }
        if (!uids.contains(uid)) {
            redisListUtil.rightPush("RMBUser", uid);
        }
    }

    private String getUserFunctionKey(Long uid) {
        return getBaseServerStatisticKey(uid) + ServerRedisKey.SPLIT + "Function" + ServerRedisKey.SPLIT + "statistic";
    }

    public void addFunction(Long uid, String filed, int addNum) {
        String key = getUserFunctionKey(uid);
        redisHashUtil.increment(key, filed, addNum);
        redisHashUtil.expire(key, THREE_DAYS);
    }

    private String getUserAddNewCardKey(Long uid) {
        return getBaseServerStatisticKey(uid) + ServerRedisKey.SPLIT + "Card" + ServerRedisKey.SPLIT + "addNewCard";
    }

    private String getUserOldCardLevelUpKey(Long uid) {
        return getBaseServerStatisticKey(uid) + ServerRedisKey.SPLIT + "Card" + ServerRedisKey.SPLIT + "oldCardLevelUp";
    }

    public void recordAddNewCardToRedis(Long uid, String cardName, String wayName) {
        String key = getUserAddNewCardKey(uid);
        redisHashUtil.putField(key, cardName, wayName);
        redisHashUtil.expire(key, THREE_DAYS);
    }

    public void recordOldCardLevelUpToRedis(Long uid, String cardName, int oldLevel, int newLevel) {
        String key = getUserOldCardLevelUpKey(uid);
        Map<String, Object> map = redisHashUtil.get(key);
        if (map.get(cardName) != null) {
            String value = (String) map.get(cardName);
            String[] split = value.split("->");
            oldLevel = Integer.parseInt(split[0]);
        }
        String level = oldLevel + "->" + newLevel;
        redisHashUtil.putField(key, cardName, level);
        redisHashUtil.expire(key, THREE_DAYS);
    }

    private String getSpecialStatisticKey(Long uid) {
        int sid = gameUserService.getActiveSid(uid);
        return ServerRedisKey.PREFIX + ServerRedisKey.SPLIT + sid + ServerRedisKey.SPLIT + "statistic" + ServerRedisKey.SPLIT +
                DateUtil.getTodayInt() + ServerRedisKey.SPLIT + "0usr" + ServerRedisKey.SPLIT + uid + ServerRedisKey.SPLIT + "special";
    }

    public void specialStatistic(long uid, String typeName, int addNum) {
        redisHashUtil.increment(getSpecialStatisticKey(uid), typeName, addNum);
        redisHashUtil.expire(getSpecialStatisticKey(uid), THREE_DAYS);
    }

    private boolean addCardNeedNotify(String wayName, AwardEnum awardEnum) {
        if (awardEnum.getValue() != AwardEnum.KP.getValue()) {
            return true;
        }
        if (WayEnum.OPEN_GOLD_CARD_POOL.getName().equals(wayName)) {
            return false;
        }
        if (WayEnum.OPEN_WOOD_CARD_POOL.getName().equals(wayName)) {
            return false;
        }
        if (WayEnum.OPEN_WATER_CARD_POOL.getName().equals(wayName)) {
            return false;
        }
        if (WayEnum.OPEN_FIRE_CARD_POOL.getName().equals(wayName)) {
            return false;
        }
        if (WayEnum.OPEN_EARTH_CARD_POOL.getName().equals(wayName)) {
            return false;
        }
        if (WayEnum.OPEN_WANWU_CARD_POOL.getName().equals(wayName)) {
            return false;
        }
        return true;
    }
}
