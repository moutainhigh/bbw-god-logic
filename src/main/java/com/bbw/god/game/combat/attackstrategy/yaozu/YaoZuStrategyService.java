package com.bbw.god.game.combat.attackstrategy.yaozu;

import com.bbw.common.JSONUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.StrUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.db.entity.AbstractAttackStrategyEntity;
import com.bbw.god.db.entity.AttackYaoZuStrategyEntity;
import com.bbw.god.db.service.AttackYaoZuStrategyService;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.attackstrategy.*;
import com.bbw.god.game.combat.attackstrategy.service.AbstractStrategyService;
import com.bbw.god.game.combat.attackstrategy.service.StrategyRedisService;
import com.bbw.god.game.data.redis.RedisKeyConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 妖族攻略service
 *
 * @author fzj
 * @date 2021/9/24 13:41
 */
@Service
public class YaoZuStrategyService extends AbstractStrategyService {

    @Autowired
    private StrategyRedisService strategyRedisService;
    @Autowired
    private RedisHashUtil<String, String> redisHashUtil;
    @Autowired
    private AttackYaoZuStrategyService attackYaoZuStrategyService;
    /**
     * 替换最新的
     *
     * @param strategyEntity
     */
    @Override
    public void addNewest(AbstractAttackStrategyEntity strategyEntity) {
        AttackYaoZuStrategyEntity entity = (AttackYaoZuStrategyEntity) strategyEntity;
        for (StrategyEnum strategyEnum : StrategyEnum.values()) {
                AttackYaoZuStrategyEntity mirroringEntity = attackYaoZuStrategyService.queryAttackOntology(entity.getYaoZuId(), entity.getUid());
                if (mirroringEntity == null || entity.getOntology().equals(mirroringEntity.getOntology())) {
                    return;
                }
                StrategyVO ontology = buildStrategyVO(entity, FightTypeEnum.YAOZU_FIGHT.getType());
                StrategyVO mirroring = buildStrategyVO(mirroringEntity, FightTypeEnum.YAOZU_FIGHT.getType());
                StrategyYaoZuVO vo = StrategyYaoZuVO.instance(ontology, mirroring);
                updateNewYaoZuStrategy(strategyEnum, entity, vo);
            }
    }

    /**
     * 更新数据
     *
     * @param strategyEnum
     * @param entity
     */
    public void updateNewYaoZuStrategy(StrategyEnum strategyEnum, AttackYaoZuStrategyEntity entity, StrategyYaoZuVO vo) {
        if (entity.getSeq() == null) {
            return;
        }
        List<StrategyYaoZuVO> voList = getStrategyVO(entity.getYaoZuId(), entity.getSeq(), strategyEnum, entity.getGid());
        if (ListUtil.isEmpty(voList)) {
            voList = new ArrayList<>();
        } else if (!checkCondition(voList.get(voList.size() - 1).getOntology(), entity, strategyEnum)) {
            return;
        }
        if (voList.size() >= strategyEnum.getShowNum()) {
            //数量超了，则去除末尾的
            voList = voList.subList(0, strategyEnum.getShowNum() - 1);
        }
        if (entity.getSeq() == null) {
            return;
        }
        voList.add(0, vo);
        strategyRedisService.updateNewStrategy(StrategySourceEnum.YAOZU_ATTACK, entity.getYaoZuId(), strategyEnum, entity, voList);
    }

    private List<StrategyYaoZuVO> getStrategyVO(int yaoZuId, Integer seq, StrategyEnum strategyEnum, int gid) {
        String key = "game"+ RedisKeyConst.SPLIT  + StrategySourceEnum.YAOZU_ATTACK.getKey() + RedisKeyConst.SPLIT +gid;
        String fieldKey = yaoZuId + "_" + seq + "_" + strategyEnum.getKey();
        if (StrategyEnum.NEWEST.equals(strategyEnum)){
            fieldKey = yaoZuId + "_" + strategyEnum.getKey();
        }
        String jsons = redisHashUtil.getField(key, fieldKey);
        if (StrUtil.isBlank(jsons)) {
            return new ArrayList<>();
        }
        List<StrategyYaoZuVO> strategyVOS = JSONUtil.fromJsonArray(jsons, StrategyYaoZuVO.class);
        return strategyVOS;
    }

    /**
     * 获取策略列表
     *
     * @param yaoZuId
     * @param strategyEnum
     * @param gid
     * @return
     */
    public List<StrategyVO> getStrategyVOList(int yaoZuId, Integer seq, StrategyEnum strategyEnum, int gid) {
        List<StrategyYaoZuVO> strategyVOS = (List<StrategyYaoZuVO>) strategyRedisService.getStrategyVOList(gid, yaoZuId, seq, StrategySourceEnum.YAOZU_ATTACK, strategyEnum);
        strategyVOS.sort(Comparator.comparing(StrategyYaoZuVO::getDatetimeInt).reversed());
        List<StrategyVO> list = new ArrayList<>();
        for (StrategyYaoZuVO strategyVO : strategyVOS) {
            list.add(strategyVO.getMirroring());
            list.add(strategyVO.getOntology());
        }
        return list;
    }
}
