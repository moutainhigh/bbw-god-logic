package com.bbw.god.gameuser.statistic.behavior.leader.equipment;

import com.bbw.common.ListUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquipment;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * 法外分身装备统计service
 *
 * @author lzc
 * @description
 * @date 2021/4/15 11:30
 */
@Service
public class LeaderEquipmentStatisticService extends BehaviorStatisticService {

    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.LEADER_EQUIPMENT;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof LeaderEquipmentStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        LeaderEquipmentStatistic equipmentStatistic = (LeaderEquipmentStatistic) statistic;
        Map<String, Integer> map = new HashMap<>();
        map.put(WEAPON_QUALITY, equipmentStatistic.getWeaponQuality());
        map.put(CLOTHES_QUALITY, equipmentStatistic.getClothesQuality());
        map.put(RING_QUALITY, equipmentStatistic.getRingQuality());
        map.put(NECKLACE_QUALITY, equipmentStatistic.getNecklaceQuality());
        map.put(WEAPON_LV, equipmentStatistic.getWeaponLv());
        map.put(CLOTHES_LV, equipmentStatistic.getClothesLv());
        map.put(RING_LV, equipmentStatistic.getRingLv());
        map.put(NECKLACE_LV, equipmentStatistic.getNecklaceLv());
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.putAllField(key, map);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseStatistic> T buildStatisticFromPreparedData(long uid, StatisticTypeEnum typeEnum, int date, Map<String, Map<String, Object>> preparedData) {
        String key = getKey(uid, typeEnum);
        Map<String, Object> map = preparedData.get(key);
        Map<String, Integer> redisMap = new HashMap<>();
        for (String s : map.keySet()) {
            redisMap.put(s, Integer.class.cast(map.get(s)));
        }
        return (T) getLeaderEquipmentStatistic(redisMap);
    }

    /**
     * 从redis读取数据并转成统计对象
     *
     * @param uid      玩家id
     * @param typeEnum 类型枚举
     * @param date     日期
     * @return 统计对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public LeaderEquipmentStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String key = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(key);
        return getLeaderEquipmentStatistic(redisMap);
    }

    private LeaderEquipmentStatistic getLeaderEquipmentStatistic(Map<String, Integer> redisMap) {
        Integer weaponQuality = redisMap.get(WEAPON_QUALITY) == null ? 0 : redisMap.get(WEAPON_QUALITY);
        Integer clothesQuality = redisMap.get(CLOTHES_QUALITY) == null ? 0 : redisMap.get(CLOTHES_QUALITY);
        Integer ringQuality = redisMap.get(RING_QUALITY) == null ? 0 : redisMap.get(RING_QUALITY);
        Integer necklaceQuality = redisMap.get(NECKLACE_QUALITY) == null ? 0 : redisMap.get(NECKLACE_QUALITY);
        Integer weaponLv = redisMap.get(WEAPON_LV) == null ? 0 : redisMap.get(WEAPON_LV);
        Integer clothesLv = redisMap.get(CLOTHES_LV) == null ? 0 : redisMap.get(CLOTHES_LV);
        Integer ringLv = redisMap.get(RING_LV) == null ? 0 : redisMap.get(RING_LV);
        Integer necklaceLv = redisMap.get(NECKLACE_LV) == null ? 0 : redisMap.get(NECKLACE_LV);
        return new LeaderEquipmentStatistic(weaponQuality, clothesQuality, ringQuality, necklaceQuality,weaponLv, clothesLv, ringLv, necklaceLv);
    }

    /** 装备ID 100010：武器，100020：衣服，100030：项链，100040：戒指 */
    public void draw(long uid, int equipmentId, int value, boolean isQuality) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        switch (equipmentId) {
            case 100010:
                redisHashUtil.putField(key, isQuality?WEAPON_QUALITY:WEAPON_LV, value);
                break;
            case 100020:
                redisHashUtil.putField(key, isQuality?CLOTHES_QUALITY:CLOTHES_LV, value);
                break;
            case 100030:
                redisHashUtil.putField(key, isQuality?NECKLACE_QUALITY:NECKLACE_LV, value);
                break;
            case 100040:
                redisHashUtil.putField(key, isQuality?RING_QUALITY:RING_LV, value);
                break;
        }
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {
        String key = getKey(uid, StatisticTypeEnum.NONE);
        List<UserLeaderEquipment> userLeaderEquipments = gameUserService.getMultiItems(uid, UserLeaderEquipment.class);
        if (ListUtil.isNotEmpty(userLeaderEquipments)) {
            for(UserLeaderEquipment equipment : userLeaderEquipments){
                switch (equipment.getEquipmentId()) {
                    case 100010:
                        redisHashUtil.putField(key, WEAPON_QUALITY, equipment.getQuality());
                        redisHashUtil.putField(key, WEAPON_LV, equipment.getLevel());
                        break;
                    case 100020:
                        redisHashUtil.putField(key, CLOTHES_QUALITY, equipment.getQuality());
                        redisHashUtil.putField(key, CLOTHES_LV, equipment.getLevel());
                        break;
                    case 100030:
                        redisHashUtil.putField(key, NECKLACE_QUALITY, equipment.getQuality());
                        redisHashUtil.putField(key, NECKLACE_LV, equipment.getLevel());
                        break;
                    case 100040:
                        redisHashUtil.putField(key, RING_QUALITY, equipment.getQuality());
                        redisHashUtil.putField(key, RING_LV, equipment.getLevel());
                        break;
                }
            }
            checkUid(uid);
            statisticPool.toUpdatePool(key);
        }
    }
}
