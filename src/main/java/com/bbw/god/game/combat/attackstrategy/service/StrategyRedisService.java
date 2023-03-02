package com.bbw.god.game.combat.attackstrategy.service;

import com.bbw.common.DateUtil;
import com.bbw.common.JSONUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.StrUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.db.entity.AbstractAttackStrategyEntity;
import com.bbw.god.db.entity.AttackCityStrategyEntity;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.attackstrategy.AbstractStrategyVO;
import com.bbw.god.game.combat.attackstrategy.StrategyEnum;
import com.bbw.god.game.combat.attackstrategy.StrategySourceEnum;
import com.bbw.god.game.combat.attackstrategy.StrategyVO;
import com.bbw.god.game.data.redis.RedisKeyConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * 攻城策略 redis服务
 * @author：lwb
 * @date: 2020/11/27 16:57
 * @version: 1.0
 */
@Service
public class StrategyRedisService {
    @Autowired
    private RedisHashUtil<String,String> redisHashUtil;

    /**
     * 获取策略列表
     *
     * @param baseId
     * @param strategyEnum
     * @param gid
     * @return
     */
    public List<? extends AbstractStrategyVO> getStrategyVOList(int gid, int baseId, Integer seq, StrategySourceEnum strategySource, StrategyEnum strategyEnum) {
        String key = getKey(gid, strategySource);
        String fieldKey = getFieldKey(baseId, seq, strategyEnum);
        String jsons = redisHashUtil.getField(key, fieldKey);
        if (StrUtil.isBlank(jsons)) {
            return new ArrayList<>();
        }
        List<? extends AbstractStrategyVO> strategyVOS = JSONUtil.fromJsonArray(jsons, strategySource.getVoClazz());
        return strategyVOS;
    }

    /**
     * 更新数据
     *
     * @param strategyEnum
     * @param entity
     */
    public void updateNewStrategy(StrategySourceEnum strategySource, int baseId, StrategyEnum strategyEnum, AbstractAttackStrategyEntity entity, List<? extends AbstractStrategyVO> voList) {
        redisHashUtil.putField(getKey(entity.getGid(), strategySource), getFieldKey(baseId, entity.getSeq(), strategyEnum), JSONUtil.toJson(voList));
    }

    /**
     * 批量覆盖(封神大陆)
     *
     * @param entities
     * @param cityId
     * @param seq
     * @param strategyEnum
     */
    public void addAll(List<AttackCityStrategyEntity> entities,int cityId,int seq,StrategyEnum strategyEnum,int gid){
        if (ListUtil.isEmpty(entities)){
            return;
        }
        List<StrategyVO> voList =new ArrayList<>();
        for (AttackCityStrategyEntity entity : entities) {
            voList.add(StrategyVO.instance(entity, FightTypeEnum.ATTACK.getType()));
        }
        redisHashUtil.putField(getKey(gid, StrategySourceEnum.FSDL_ATTACK_CITY), getFieldKey(cityId, seq, strategyEnum), JSONUtil.toJson(voList));
    }

    /**
     * 清除旧数据
     *
     * @param gid
     * @param endDate
     */
    public void clearOldData(int gid, Date endDate, StrategySourceEnum strategySource) {
        String baseKey = getKey(gid, strategySource);
        Set<String> fieldKeySet = redisHashUtil.getFieldKeySet(baseKey);
        long dateTimeLong = DateUtil.toDateTimeLong(endDate);
        for (String fieldKey : fieldKeySet) {
            String json = redisHashUtil.getField(baseKey, fieldKey);
            if (StrUtil.isBlank(json)) {
                continue;
            }
            List<? extends AbstractStrategyVO> strategyVOS = JSONUtil.fromJsonArray(json, strategySource.getVoClazz());
            List<? extends AbstractStrategyVO> collect = strategyVOS.stream().filter(p -> p.getDatetimeInt() > dateTimeLong).collect(Collectors.toList());
            if (ListUtil.isNotEmpty(collect)) {
                redisHashUtil.putField(baseKey, fieldKey, JSONUtil.toJson(collect));
            } else {
                redisHashUtil.removeField(baseKey, fieldKey);
            }
        }
    }


    /**
     * 获取基础的key
     *
     * @param gid
     * @return
     */
    private String getKey(int gid, StrategySourceEnum strategySource) {
        return "game"+ RedisKeyConst.SPLIT  + strategySource.getKey() + RedisKeyConst.SPLIT +gid;
    }

    /**
     * 获取字段名
     *
     * @param baseId       目标ID，如城池ID、妖族ID
     * @param seq
     * @param strategyEnum
     * @return
     */
    private String getFieldKey(int baseId, int seq, StrategyEnum strategyEnum) {
        if (StrategyEnum.NEWEST.equals(strategyEnum)) {
            return baseId + "_" + strategyEnum.getKey();
        }
        return baseId + "_" + seq + "_" + strategyEnum.getKey();
    }
}
