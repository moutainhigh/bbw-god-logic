package com.bbw.god.gameuser.card.equipment;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.gameuser.card.equipment.rd.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 卡牌装备接口
 *
 * @author: huanghb
 * @date: 2022/9/15 14:06
 */
@RestController
public class CardEquipmentCtrl extends AbstractController {
    @Autowired
    private UserCardXianJueLogic userCardXianJueLogic;
    @Autowired
    private UserCardZhiBaoLogic userCardZhiBaoLogic;


    /**
     * 激活仙诀
     *
     * @param cardId
     * @param treasureId 仙诀激活道具id
     * @return
     */
    @GetMapping(CR.CardEquipment.ACTIVE_CARD_XIAN_JUE)
    public RdCardXianJueInfo activeXianJue(Integer cardId, Integer treasureId, Integer xianJueType) {
        return userCardXianJueLogic.activeXianJue(getUserId(), cardId, treasureId, xianJueType);
    }

    /**
     * 获得仙诀信息
     *
     * @param cardId
     * @param xianJueType 仙诀类型
     * @return
     */
    @GetMapping(CR.CardEquipment.GET_XIAN_JUE_INFO)
    public RdCardXianJueInfo getXianJueInfo(Integer cardId, Integer xianJueType) {
        return userCardXianJueLogic.getXianJueInfo(getUserId(), cardId, xianJueType);
    }

    /**
     * 研习（强化）
     *
     * @param xianJueDataId 仙诀数据id
     * @return
     */
    @GetMapping(CR.CardEquipment.STRENGH_XIAN_JUE)
    public RdXianJueStrength strength(long xianJueDataId) {
        return userCardXianJueLogic.xianJueStrength(getUserId(), xianJueDataId);
    }

    /**
     * 升级星图
     *
     * @param xianJueDataId 仙诀数据id
     * @param protect       是否 使用星图保护符 0为否 1为是
     * @return
     */
    @GetMapping(CR.CardEquipment.UPDATE_STAR_MAP)
    public RdXianJueStarMapUpdate updateStarMap(long xianJueDataId, int protect) {
        return userCardXianJueLogic.updateStarMap(getUserId(), xianJueDataId, protect);
    }

    /**
     * 参悟
     *
     * @param xianJueDataId  仙诀数据id
     * @param comprehendType 参悟类别 30 为强度 40为韧度
     * @return
     */
    @GetMapping(CR.CardEquipment.COMPREHEND_XIAN_JUE)
    public RdComprehendInfos comprehend(long xianJueDataId, Integer comprehendType) {
        return userCardXianJueLogic.comprehend(getUserId(), xianJueDataId, comprehendType);
    }

    /**
     * 灵宝穿戴
     *
     * @param cardId
     * @param zhiBaoId     至宝id
     * @param zhiBaoDataId 至宝数据id
     * @return
     */
    @GetMapping(CR.CardEquipment.TAKE)
    public RdCardZhiBao take(Integer cardId, Integer zhiBaoId, long zhiBaoDataId) {
        return userCardZhiBaoLogic.take(getUserId(), cardId, zhiBaoId, zhiBaoDataId);
    }

    /**
     * 灵宝取下
     *
     * @param zhiBaoDataId 至宝数据id
     * @return
     */
    @GetMapping(CR.CardEquipment.TAKE_OFF)
    public RdWearingCondition takeOff(long zhiBaoDataId) {
        return userCardZhiBaoLogic.takeOff(getUserId(), zhiBaoDataId);
    }
}
