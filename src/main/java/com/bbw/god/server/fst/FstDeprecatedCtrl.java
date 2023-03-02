package com.bbw.god.server.fst;

import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.game.CR;
import com.bbw.god.rd.RDSuccess;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-29
 * 弃用所有旧的封神台相关接口，原因为 所有接口 以gu或者pvp开头  不符合实际命名规范
 *
 */
@Deprecated
@RestController
public class FstDeprecatedCtrl{
    /**
     * 封神台榜单
     *
     * @return
     */
    @GetMapping(CR.FST_DEPRECATED_API.LIST_RANKERS)
    public RDSuccess listPvpRank() {
        return new RDSuccess();
    }

    /**
     * 竞技场挑战，获得对方卡牌
     *
     * @param opponent
     * @param opponentRank
     * @return
     */
    @GetMapping(CR.FST_DEPRECATED_API.CHALLENGE)
    public RDSuccess challenge(long opponent, int opponentRank) {
        return new RDSuccess();
    }

    /**
     * 提交挑战结果
     *
     * @return
     */
    @GetMapping(CR.FST_DEPRECATED_API.SUBMIT_RESULT)
    public RDSuccess submitChallengeResult(FightSubmitParam param) {
        return new RDSuccess();
    }

    /**
     * 获取可兑换的物品
     *
     * @return
     */
    @GetMapping(CR.FST_DEPRECATED_API.LIST_CONVERTIBLE_GOODS)
    public RDSuccess listConvertibleGoods() {
        return new RDSuccess();
    }

    /**
     * 积分兑换
     *
     * @param item
     * @param num
     * @return
     */
    @GetMapping(CR.FST_DEPRECATED_API.EXCHANGE)
    public RDSuccess exchange(int item, int num) {
        return new RDSuccess();
    }

    /**
     * 领取积分
     *
     * @return
     */
    @GetMapping(CR.FST_DEPRECATED_API.GAIN_INCREMENT_POINT)
    public RDSuccess gainIncrementPoints() {
        return new RDSuccess();
    }
}
