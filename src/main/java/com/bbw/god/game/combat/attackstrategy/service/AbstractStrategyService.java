package com.bbw.god.game.combat.attackstrategy.service;

import com.bbw.god.db.entity.AbstractAttackStrategyEntity;
import com.bbw.god.game.combat.attackstrategy.PlayerInfoVO;
import com.bbw.god.game.combat.attackstrategy.StrategyEnum;
import com.bbw.god.game.combat.attackstrategy.StrategyVO;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 策略服务
 *
 * @author: suhq
 * @date: 2021/9/23 3:19 上午
 */
public abstract class AbstractStrategyService {
    @Autowired
    private GameUserService gameUserService;

    /**
     * 替换最新的
     *
     * @param strategyEntity
     */
    public abstract void addNewest(AbstractAttackStrategyEntity strategyEntity);


    /**
     * 检查是否符合替换条件
     *
     * @param vo
     * @param entity
     * @param strategyEnum
     * @return
     */
    protected boolean checkCondition(StrategyVO vo, AbstractAttackStrategyEntity entity, StrategyEnum strategyEnum) {
        if (StrategyEnum.NEWEST.equals(strategyEnum)) {
            return true;
        }
        if (StrategyEnum.CARDS_MIN.equals(strategyEnum)) {
            return vo.getCards() >= entity.getCards();
        }
        if (StrategyEnum.USER_LV_MIN.equals(strategyEnum)) {
            return vo.getLv() >= entity.getLv();
        }
        return vo.getRound() >= entity.getRound();
    }

    protected StrategyVO buildStrategyVO(AbstractAttackStrategyEntity entity, int fightType) {
        if (entity.getNickname() == null) {
            GameUser gu = gameUserService.getGameUser(entity.getUid());
            PlayerInfoVO user = PlayerInfoVO.instance(gu);
            user.setLv(entity.getLv());
            return StrategyVO.instance(entity, user, fightType);
        }
        return StrategyVO.instance(entity, fightType);
    }
}
