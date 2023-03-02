package com.bbw.god.activity.holiday.lottery.service;

import com.bbw.common.PowerRandom;
import com.bbw.common.StrUtil;
import com.bbw.common.lock.SyncLockUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.CoderException;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.holiday.lottery.*;
import com.bbw.god.activity.holiday.lottery.event.HolidayEventPublisher;
import com.bbw.god.activity.holiday.lottery.rd.RDDrawHolidayLottery;
import com.bbw.god.activity.holiday.lottery.rd.RDHolidayLotteryAward;
import com.bbw.god.activity.holiday.lottery.rd.RDHolidayLotteryInfo;
import com.bbw.god.activity.holiday.lottery.rd.RDPreviewAwards;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * 驱魔荣光|雨露均沾   翻牌子 砸金蛋
 * @description id=10的节日抽奖service
 * @date 2020/9/17 10:52
 **/
@Service
public class HolidayLotteryService10 implements RefreshableHolidayLotteryService {
    private static TreasureEnum NEED_TREASUER = TreasureEnum.GOLDEN_KEY;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private SyncLockUtil syncLockUtil;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private RedisHashUtil<Long, UserHolidayLottery10> redisHashUtil;

    private static final long TIME_OUT = 7 * 24 * 60 * 60;

    /**
     * 获取当前service对应的id
     *
     * @return
     */
    @Override
    public int getMyId() {
        return HolidayLotteryType.QMRG.getValue();
    }

    /**
     * 奖品刷新检查
     *
     * @param uid          玩家id
     */
    @Override
    public void checkForRefresh(long uid, HolidayLotteryParam param) {
        UserHolidayLottery10 userHolidayLottery = getCurUserHolidayLottery(uid);
        int needGold = getGoldForRefresh(userHolidayLottery.getRefreshTimes(), param.getLockAwardIds());
        GameUser gu = gameUserService.getGameUser(uid);
        if (gu.getGold() < needGold) {
            throw new ExceptionForClientTip("gu.gold.not.enough");
        }
    }

    /**
     * 刷新奖励
     *
     * @param uid 玩家id
     */
    @Override
    public RDHolidayLotteryInfo refreshAwards(long uid, HolidayLotteryParam param) {
        UserHolidayLottery10 userHolidayLottery = getCurUserHolidayLottery(uid);
        Integer refreshTimes = userHolidayLottery.getRefreshTimes();
        // 检查元宝
        checkForRefresh(uid, param);
        // 重置
        Map<Integer, Integer> lockAwardMap = getLockAwardMap(param.getLockAwardIds());
        freeRefreshAwards(uid, userHolidayLottery, lockAwardMap);
        RDHolidayLotteryInfo rd = getHolidayLotteryInfo(uid, param);
        // 扣除资源
        int needGold = getGoldForRefresh(refreshTimes, param.getLockAwardIds());
        ResEventPublisher.pubGoldDeductEvent(uid, needGold, WayEnum.HOLIDAY_LOTTERY_REFRESH, rd);
        return rd;
    }

    /**
     * 获取节日抽奖信息
     *
     * @param uid 玩家id
     * @return
     */
    @Override
    public RDHolidayLotteryInfo getHolidayLotteryInfo(long uid, HolidayLotteryParam param) {
        UserHolidayLottery10 userHolidayLottery = getCurUserHolidayLottery(uid);
        Integer refreshTimes = userHolidayLottery.getRefreshTimes();
        int goldForRefresh = getGoldForRefresh(refreshTimes, param.getLockAwardIds());
        int costForDraw = getNeedCostForDraw(userHolidayLottery.getAwardedIndexList().size() + 1);
        return RDHolidayLotteryInfo.getInstance(costForDraw, refreshTimes, goldForRefresh, userHolidayLottery);
    }

    /**
     * 抽奖
     *
     * @param uid 玩家id
     * @return
     */
    @Override
    public RDCommon draw(long uid, HolidayLotteryParam param) {
        RDDrawHolidayLottery rd = new RDDrawHolidayLottery();
        UserHolidayLottery10 userHolidayLottery = getCurUserHolidayLottery(uid);
        List<Integer> remainIndexList = userHolidayLottery.getRemainIndexList();
        int index = PowerRandom.getRandomFromList(remainIndexList);
        // 检查
        checkForDraw(uid, param);
        // 扣除资源
        int needTreasureNum = getNeedCostForDraw(userHolidayLottery.getAwardedIndexList().size() + 1);
        TreasureEventPublisher.pubTDeductEvent(uid, NEED_TREASUER.getValue(), needTreasureNum,
                WayEnum.HOLIDAY_LOTTERY_DRAW, rd);
        // 发放奖励
        userHolidayLottery.draw(index);
        CfgHolidayLotteryAwards cfgLottery = HolidayLotteryTool.getById(userHolidayLottery.getLotteryIds().get(index));
        List<Award> awards = cfgLottery.getAwards();
        awardService.fetchAward(uid, awards, WayEnum.HOLIDAY_LOTTERY_DRAW, "", rd);
        // 数据保存
        toRedis(uid, userHolidayLottery);
        // 如果本轮所有东西都抽完了，进行自动刷新
        if (9 == userHolidayLottery.getAwardedIndexList().size()) {
            freeRefreshAwards(uid, userHolidayLottery, new HashMap<>());
        }
        // 发布事件
        BaseEventParam bep = new BaseEventParam(uid, WayEnum.HOLIDAY_LOTTERY_DRAW, rd);
        HolidayEventPublisher.pubHolidayLotteryDrawEvent(bep, HolidayLotteryType.QMRG);
        // 返回给客户端
        rd.setAward(RDHolidayLotteryAward.getInstance(awards.get(0), index, true));
        return rd;
    }

    /**
     * 抽奖检查
     *
     * @param uid 玩家id
     */
    @Override
    public void checkForDraw(long uid, HolidayLotteryParam param) {
        UserHolidayLottery10 userHolidayLottery = getCurUserHolidayLottery(uid);
        int treasureNum = userTreasureService.getTreasureNum(uid, NEED_TREASUER.getValue());
        int needNum = getNeedCostForDraw(userHolidayLottery.getAwardedIndexList().size() + 1);
        if (treasureNum < needNum) {
            throw new ExceptionForClientTip("treasure.not.enough", NEED_TREASUER.getName());
        }
    }

    /**
     * 奖励预览
     *
     * @return
     */
    @Override
    public RDCommon previewAwards(HolidayLotteryParam param) {
        RDPreviewAwards rd = new RDPreviewAwards();
        List<Award> awards = new ArrayList<>();
        List<CfgHolidayLotteryAwards> lotteryAwards = HolidayLotteryTool.getAll(getMyId()).stream()
                .sorted(Comparator.comparing(CfgHolidayLotteryAwards::getOrder)).collect(Collectors.toList());
        for (CfgHolidayLotteryAwards lotteryAward : lotteryAwards) {
            awards.addAll(lotteryAward.getAwards());
        }
        rd.setAwards(awards);
        return rd;
    }

    /**
     * 从redis中获取数据并转化成BaseUserHolidayLottery对象
     *
     * @param uid
     * @return
     */
    @Override
    public UserHolidayLottery10 fromRedis(long uid) {
        int sid = gameUserService.getOriServer(uid).getMergeSid();
        String key = getKey(sid);
        Map<Long, UserHolidayLottery10> redisMap = redisHashUtil.get(key);
        return redisMap.get(uid);
    }

    /**
     * 将BaseUserHolidayLottery对象保存到redis中
     *
     * @param uid                玩家id
     * @param userHolidayLottery 保存的数据对象
     */
    @Override
    public void toRedis(long uid, BaseUserHolidayLottery userHolidayLottery) {
        if (!(userHolidayLottery instanceof UserHolidayLottery10)) {
            throw CoderException.high("参数类型错误");
        }
        UserHolidayLottery10 holidayLottery10 = (UserHolidayLottery10) userHolidayLottery;
        int sid = gameUserService.getOriServer(uid).getMergeSid();
        String key = getKey(sid);
        redisHashUtil.putField(key, uid, holidayLottery10, TIME_OUT);
    }

    /**
     * 获取当前玩家节日抽奖对象
     *
     * @param uid
     * @return
     */
    private UserHolidayLottery10 getCurUserHolidayLottery(long uid) {
        UserHolidayLottery10 lottery = fromRedis(uid);
        // 双重检查，懒汉单例
        if (null == lottery) {
            return (UserHolidayLottery10) syncLockUtil.doSafe(String.valueOf(uid), tmp -> {
                UserHolidayLottery10 userLottery = fromRedis(uid);
                if (null == userLottery) {
                    List<CfgHolidayLotteryAwards> cfgHolidayLotteryAwards = getForUserHolidayLotteryInstance(uid, true);
                    List<Integer> ids = cfgHolidayLotteryAwards.stream().map(CfgHolidayLotteryAwards::getId)
                            .collect(Collectors.toList());
                    userLottery = UserHolidayLottery10.getInstance(uid, ids);
                    toRedis(uid, userLottery);
                }
                return userLottery;
            });
        }
        return lottery;
    }

    /**
     * 获取初始化奖池的奖励
     *
     * @param uid 玩家id
     * @return
     */
    private List<CfgHolidayLotteryAwards> getForUserHolidayLotteryInstance(long uid, boolean isFirstInstance) {
        /*// 不是第一次初始化，随机
        if (!isFirstInstance) {
            return getForUserHolidayLotteryInstance(uid, new HashMap<>());
        }
        // 第一次初始化，固定奖品
        List<Integer> ids = Arrays.asList(0, 160, 50, 260, 240, 120, 230, 210, 220);
        return ids.stream().map(HolidayLotteryTool::getById).collect(Collectors.toList());*/
        return getForUserHolidayLotteryInstance(uid, new HashMap<>());
    }

    /**
     * 初始化的奖池数据
     *
     * @param uid
     * @param lockAwardMap
     * @return
     */
    private List<CfgHolidayLotteryAwards> getForUserHolidayLotteryInstance(long uid, Map<Integer, Integer> lockAwardMap) {
        List<CfgHolidayLotteryAwards> dataList = new ArrayList<>();
        int count = 9 - lockAwardMap.size();
        for (int i = 0; i < count; i++) {
            CfgHolidayLotteryAwards random = HolidayLotteryTool.getByRandom(getMyId());
            dataList.add(random);
        }
        Set<Integer> keySet = lockAwardMap.keySet();
        for (Integer key : keySet) {
            dataList.add(HolidayLotteryTool.getByAwardId(getMyId(), lockAwardMap.get(key)));
        }
        Collections.shuffle(dataList);
        return dataList;
    }

    /**
     * 获取刷新所需要消耗的元宝输了
     *
     * @param refreshTimes 已刷新次数
     * @param lockAwardIds 锁住的奖励
     * @return
     */
    private int getGoldForRefresh(int refreshTimes, String lockAwardIds) {
        int baseNum = refreshTimes >= 15 ? 200 : 60 + refreshTimes * 10;
        if (StrUtil.isNotEmpty(lockAwardIds)) {
            String[] split = lockAwardIds.split(";");
            return baseNum + split.length * 10;
        }
        return baseNum;
    }

    /**
     * 获取抽奖所需要的的净心咒数量
     *
     * @param drawTimes 第几次抽
     * @return
     */
    private int getNeedCostForDraw(int drawTimes) {
        if (drawTimes <= 3) {
            return 1;
        }
        if (drawTimes <= 6) {
            return 2;
        }
        if (drawTimes <= 9) {
            return 3;
        }
        throw CoderException.high("节日抽奖抽取次数异常！");
    }

    /**
     * 将锁住的技能和下标组成map，key是下标，val是对应的awardId
     *
     * @param lockAwardIds
     * @return
     */
    private Map<Integer, Integer> getLockAwardMap(String lockAwardIds) {
        Map<Integer, Integer> lockAwardMap = new HashMap<>();
        if (StrUtil.isEmpty(lockAwardIds)) {
            return lockAwardMap;
        }
        String[] split = lockAwardIds.split(";");
        for (String str : split) {
            String[] strArr = str.split(",");
            lockAwardMap.put(Integer.parseInt(strArr[1]), Integer.parseInt(strArr[0]));
        }
        return lockAwardMap;
    }

    /**
     * 免费刷新（开完所有奖励后自动刷新）
     *
     * @param uid
     * @param userHolidayLottery
     * @param lockAwardMap
     */
    public void freeRefreshAwards(long uid, UserHolidayLottery10 userHolidayLottery, Map<Integer, Integer> lockAwardMap) {
        List<Integer> ids = getForUserHolidayLotteryInstance(uid, lockAwardMap).stream()
                .map(CfgHolidayLotteryAwards::getId).collect(Collectors.toList());
        List<Integer> showIds = new ArrayList<>(ids);
        Set<Integer> keySet = lockAwardMap.keySet();
        for (Integer index : keySet) {
            showIds.remove(HolidayLotteryTool.getByAwardId(getMyId(), lockAwardMap.get(index)).getId());
        }
        Collections.shuffle(showIds);
        // 展示的下标集合，从小到大排序，避免插入时候先插入后面的再插入前面的引起数组下标错误
        List<Integer> indexList = keySet.stream().sorted(Integer::compareTo).collect(Collectors.toList());
        for (Integer index : indexList) {
            showIds.add(index, HolidayLotteryTool.getByAwardId(getMyId(), lockAwardMap.get(index)).getId());
        }
        userHolidayLottery.refresh(ids, showIds);
        toRedis(uid, userHolidayLottery);
    }
}
