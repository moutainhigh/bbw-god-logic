package com.bbw.god.game.maou.ctrl;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.game.maou.GameMaouLogic;
import com.bbw.god.rd.RDSuccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 跨服魔王相关接口
 *
 * @author: suhq
 * @date: 2021/12/17 4:57 下午
 */
@Slf4j
@RestController
public class GameMaouCtrl extends AbstractController {
    @Autowired
    private GameMaouLogic gameMaouLogic;

    /**
     * 获取魔王信息
     *
     * @return
     */
    @GetMapping(CR.Maou.GET_GAME_MAOU)
    public RDSuccess getMaou() {
        return gameMaouLogic.getMaou(getUserId());
    }

    /**
     * 定时刷新魔王信息
     *
     * @return
     */
    @GetMapping(CR.Maou.REFRESH_GAME_MAOU)
    public RDGameMaouBloodInfo refreshMaou() {
        return gameMaouLogic.getBloodInfo(getUserId());
    }

    /**
     * 设置攻打魔王的卡牌
     *
     * @return
     */
    @GetMapping(CR.Maou.SET_GAME_MAOU_CARDS)
    public RDSuccess setMaouCards(String maouCards) {
        return gameMaouLogic.setMaouCards(getUserId(), maouCards);
    }

    /**
     * 获取魔王目标奖励
     *
     * @return
     */
    @GetMapping(CR.Maou.GET_GAME_MAOU_AWARD)
    public RDGameMaouAward getGameMaouAward() {
        return gameMaouLogic.getTargetAward(getUserId());
    }

    /**
     * 攻打魔王
     *
     * @return
     */
    @GetMapping(CR.Maou.ATTACK_GAME_MAOU)
    public RDGameMaouAttack attack(CPGameMaouAttack attackParam) {
        return gameMaouLogic.attack(getUserId(), attackParam);
    }
}
