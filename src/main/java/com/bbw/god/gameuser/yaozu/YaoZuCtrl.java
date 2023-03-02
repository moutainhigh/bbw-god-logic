package com.bbw.god.gameuser.yaozu;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.gameuser.card.RDCardGroups;
import com.bbw.god.gameuser.yaozu.rd.RDArriveYaoZu;
import com.bbw.god.gameuser.yaozu.rd.RDYaoZuInfo;
import com.bbw.god.gameuser.yaozu.rd.RDYaoZuPos;
import com.bbw.god.gameuser.yaozu.rd.RDYaoZuPoses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 妖族相关调用接口
 *
 * @author fzj
 * @date 2021/9/9 11:19
 */
@Slf4j
@RestController
public class YaoZuCtrl extends AbstractController {

    @Autowired
    YaoZuLogic yaoZuLogic;
    @Autowired
    YaoZuGenerateProcessor yaoZuGenerateProcessor;

    /**
     * 生成妖族信息
     *
     * @return
     */
    @RequestMapping(CR.YaoZu.GENERATE)
    public RDYaoZuPoses generateYaoZu() {
        return yaoZuGenerateProcessor.generateYaoZuByUserId(getUserId());
    }

    /**
     * 获取根据妖族id获取妖族信息
     *
     * @param yaoZuId
     * @return
     */
    @RequestMapping(CR.YaoZu.GAIN_YAO_ZU_INFO)
    public RDYaoZuInfo gainYaoZuInfo(int yaoZuId) {
        return yaoZuLogic.yaoZuInfoByYaoZuId(getUserId(), yaoZuId);
    }

    /**
     * 获取斩妖卡组
     *
     * @return
     */
    @RequestMapping(CR.YaoZu.GAIN_YAO_ZU_CARD_GROUP)
    public RDCardGroups gainYaoZuCardGroup() {
        return yaoZuLogic.getAttackCardGroup(getUserId());
    }


    /**
     * 触发妖族的信息
     *
     * @return
     */
    @RequestMapping(CR.YaoZu.ARRIVE_YAO_ZU_INFO)
    public RDArriveYaoZu arriveYaoZuInfo() {
        return yaoZuLogic.arriveYaoZuInfo(getUserId());
    }

    /**
     * 撤退
     *
     * @return
     */
    @RequestMapping(CR.YaoZu.RETREAT)
    public RDYaoZuPos retreat() {
        return yaoZuGenerateProcessor.retreat(getUserId());
    }

    /**
     * 保存攻击卡组
     *
     * @param cardIds
     * @param type
     * @return
     */
    @RequestMapping(CR.YaoZu.SET_ATTACK_CARDS)
    public RDCardGroups setAttackCards(String cardIds, int type) {
        return yaoZuLogic.setAttackCardGroup(getUserId(), cardIds, type);
    }

    /**
     * 同步卡组
     *
     * @return
     */
    @RequestMapping(CR.YaoZu.SYNCHRONIZE_ATTACK_CARDS)
    public RDCardGroups synchronizeAttackCards() {
        return yaoZuLogic.synchronizeAttackCards(getUserId());
    }
}
