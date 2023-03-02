package com.bbw.god.game.combat.attackstrategy.chengc;

import com.bbw.common.ListUtil;
import com.bbw.god.db.entity.AbstractAttackStrategyEntity;
import com.bbw.god.db.entity.AttackCityStrategyEntity;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.attackstrategy.StrategyEnum;
import com.bbw.god.game.combat.attackstrategy.StrategyNightmareVO;
import com.bbw.god.game.combat.attackstrategy.StrategySourceEnum;
import com.bbw.god.game.combat.attackstrategy.StrategyVO;
import com.bbw.god.game.combat.attackstrategy.service.AbstractStrategyService;
import com.bbw.god.game.combat.attackstrategy.service.StrategyRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 攻城策略
 *
 * @author：lwb
 * @date: 2020/11/27 16:57
 * @version: 1.0
 */
@Service
public class ChengCStrategyService extends AbstractStrategyService {
    @Autowired
    private StrategyRedisService strategyRedisService;

    /**
     * 替换最新的
     *
     * @param strategyEntity
     */
    @Override
    public void addNewest(AbstractAttackStrategyEntity strategyEntity) {
        AttackCityStrategyEntity entity = (AttackCityStrategyEntity) strategyEntity;
        if (entity.getNightmare() == 1 && entity.getHuweijun() == 1) {
            return;
        }
        for (StrategyEnum strategyEnum : StrategyEnum.values()) {
            if (entity.getNightmare() == 1) {
                updateNewNightmareStrategy(strategyEnum, entity);
            } else {
                updateNewStrategy(strategyEnum, entity);
            }
        }
    }

    /**
     * 获取策略列表
     *
     * @param cityId
     * @param strategyEnum
     * @param gid
     * @return
     */
    public List<StrategyVO> getStrategyVOList(int cityId, Integer seq, StrategyEnum strategyEnum, int gid) {
        List<StrategyVO> strategyVOS = (List<StrategyVO>) strategyRedisService.getStrategyVOList(gid, cityId, seq, StrategySourceEnum.FSDL_ATTACK_CITY, strategyEnum);
        if (StrategyEnum.NEWEST.equals(strategyEnum)) {
            strategyVOS.sort(Comparator.comparing(StrategyVO::getDatetime).reversed());
        } else if (StrategyEnum.CARDS_MIN.equals(strategyEnum)) {
            strategyVOS.sort(Comparator.comparing(StrategyVO::getCards));
        } else if (StrategyEnum.USER_LV_MIN.equals(strategyEnum)) {
            strategyVOS.sort(Comparator.comparing(StrategyVO::getLv));
        } else {
            strategyVOS.sort(Comparator.comparing(StrategyVO::getRound));
        }
        return strategyVOS;
    }

    public List<StrategyVO> getNightmareStrategyVOList(int cityId, Integer seq, StrategyEnum strategyEnum, int gid) {
        List<StrategyNightmareVO> strategyVOS = (List<StrategyNightmareVO>) strategyRedisService.getStrategyVOList(gid, cityId, seq, StrategySourceEnum.NIGHTMARE_ATTACK_CITY, strategyEnum);
        strategyVOS.sort(Comparator.comparing(StrategyNightmareVO::getDatetimeInt).reversed());
        List<StrategyVO> list = new ArrayList<>();
        for (StrategyNightmareVO strategyVO : strategyVOS) {
            list.add(strategyVO.getJinWei());
        }
        return list;
    }


    /**
     * 更新数据
     *
     * @param strategyEnum
     * @param entity
     */
    public void updateNewStrategy(StrategyEnum strategyEnum, AttackCityStrategyEntity entity) {
        if (entity.getSeq() == null) {
            return;
        }
        List<StrategyVO> voList = getStrategyVOList(entity.getCityId(), entity.getSeq(), strategyEnum, entity.getGid());
        if (ListUtil.isEmpty(voList)) {
            voList = new ArrayList<>();
        } else if (!checkCondition(voList.get(voList.size() - 1), entity, strategyEnum)) {
            return;
        }
        if (voList.size() >= strategyEnum.getShowNum()) {
            //数量超了，则去除末尾的
            voList = voList.subList(0, strategyEnum.getShowNum() - 1);
        }
        StrategyVO strategyVO = buildStrategyVO(entity, FightTypeEnum.ATTACK.getValue());
        voList.add(0, strategyVO);
        strategyRedisService.updateNewStrategy(StrategySourceEnum.FSDL_ATTACK_CITY, entity.getCityId(), strategyEnum, entity, voList);
    }

    public void updateNewNightmareStrategy(StrategyEnum strategyEnum, AttackCityStrategyEntity entity) {
        if (entity.getSeq() == null) {
            return;
        }
        List<StrategyNightmareVO> nightmareVOs = (List<StrategyNightmareVO>) strategyRedisService.getStrategyVOList(entity.getGid(), entity.getCityId(), entity.getSeq(), StrategySourceEnum.NIGHTMARE_ATTACK_CITY, strategyEnum);
        if (ListUtil.isEmpty(nightmareVOs)) {
            nightmareVOs = new ArrayList<>();
        } else if (!checkCondition(nightmareVOs.get(nightmareVOs.size() - 1).getJinWei(), entity, strategyEnum)) {
            return;
        }
        if (nightmareVOs.size() >= strategyEnum.getShowNum()) {
            //数量超了，则去除末尾的
            nightmareVOs = nightmareVOs.subList(0, strategyEnum.getShowNum() - 1);
        }
        StrategyVO jinwei = buildStrategyVO(entity, FightTypeEnum.ATTACK.getValue());
        StrategyNightmareVO vo = StrategyNightmareVO.instance(jinwei);
        nightmareVOs.add(0, vo);
        strategyRedisService.updateNewStrategy(StrategySourceEnum.NIGHTMARE_ATTACK_CITY, entity.getCityId(), strategyEnum, entity, nightmareVOs);
    }
}
