package com.bbw.god.game.transmigration;

import com.bbw.common.ListUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.game.config.city.ChengC;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import com.bbw.god.game.transmigration.entity.UserTransmigrationRecord;
import com.bbw.god.gameuser.GameUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 轮回单个城市排行
 *
 * @author: suhq
 * @date: 2021/9/13 3:29 下午
 */
@Service
@Slf4j
public class TransmigrationCityRecordService {
    /** 某城池玩家最好记录 uid -> 记录ID */
    @Autowired
    private RedisHashUtil<Long, Long> uidBestRecordMap;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 获取打下过某城的所有玩家
     *
     * @param transmigration
     * @param cityId
     * @return uid -> 玩家最好记录
     */
    public Map<Long, Long> getAllRecords(GameTransmigration transmigration, int cityId) {
        // 榜单不存在返回
        if (null == transmigration) {
            return new HashMap<>();
        }
        String uidMapBestRecordKey = getUidMapBestRecordKey(transmigration, cityId);
        return uidBestRecordMap.get(uidMapBestRecordKey);
    }


    /**
     * 获取玩家在某城的最好记录
     *
     * @param transmigration
     * @param uid
     * @param cityId
     */
    public Long getBestRecordId(GameTransmigration transmigration, long uid, int cityId) {
        // 榜单不存在返回
        if (null == transmigration) {
            return -1L;
        }
        String uidMapBestRecordKey = getUidMapBestRecordKey(transmigration, cityId);
        Long bestRecordId = uidBestRecordMap.getField(uidMapBestRecordKey, uid);
        if (null == bestRecordId) {
            return -1L;
        }
        return bestRecordId;
    }

    /**
     * 获取我在某城的最好评分
     *
     * @param transmigration
     * @param uid
     * @param cityId
     * @return
     */
    public Integer getScore(GameTransmigration transmigration, long uid, int cityId) {
        Long bestRecordId = getBestRecordId(transmigration, uid, cityId);
        if (bestRecordId <= 0) {
            return null;
        }
        Optional<UserTransmigrationRecord> op = gameUserService.getUserData(uid, bestRecordId, UserTransmigrationRecord.class);
        if (!op.isPresent()) {
            return null;
        }
        return op.get().gainScore();
    }

    /**
     * 更新玩家在某城的最好记录
     *
     * @param transmigration
     * @param uid
     * @param cityId
     * @param bestRecordId
     */
    public void updateBestRecordId(GameTransmigration transmigration, long uid, int cityId, long bestRecordId) {
        // 榜单不存在返回
        if (null == transmigration) {
            return;
        }
        String uidMapBestRecordKey = getUidMapBestRecordKey(transmigration, cityId);
        uidBestRecordMap.putField(uidMapBestRecordKey, uid, bestRecordId);
    }

    /**
     * 是否有该城的记录（表示已挑战成功过）
     *
     * @param transmigration
     * @param uid
     * @param cityId
     * @return
     */
    public boolean hasRecord(GameTransmigration transmigration, long uid, int cityId) {
        // 榜单不存在返回
        if (null == transmigration) {
            return false;
        }
        String uidMapBestRecordKey = getUidMapBestRecordKey(transmigration, cityId);
        return uidBestRecordMap.hasField(uidMapBestRecordKey, uid);
    }

    /**
     * 获取我的攻城记录(每座城池对应一条最好的记录)
     *
     * @param transmigration
     * @param uid
     * @return
     */
    public List<UserTransmigrationRecord> getMyRecords(GameTransmigration transmigration, long uid) {
        List<Long> myBestRecordIds = new ArrayList<>();
        List<ChengC> chengCs = CityTool.getChengCs();
        for (ChengC chengC : chengCs) {
            int cityId = chengC.getId();
            Long myBestRecordId = getBestRecordId(transmigration, uid, cityId);
            if (myBestRecordId > 0) {
                myBestRecordIds.add(myBestRecordId);
            }
        }
        if (ListUtil.isEmpty(myBestRecordIds)) {
            return new ArrayList<>();
        }
        List<UserTransmigrationRecord> myBestRecords = gameUserService.getUserDatas(uid, myBestRecordIds, UserTransmigrationRecord.class);
        return myBestRecords;
    }

    /**
     * 城池挑战榜：game:transmigration:区服组:开始日期:bestRecord:城池ID
     *
     * @param transmigration
     * @param cityId
     * @return
     */
    private static String getUidMapBestRecordKey(GameTransmigration transmigration, int cityId) {
        String key = TransmigrationKey.getBaseKey(transmigration);
        return key + RedisKeyConst.SPLIT + "bestRecord" + RedisKeyConst.SPLIT + cityId;
    }
}
