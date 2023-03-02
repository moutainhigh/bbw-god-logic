package com.bbw.god.gameuser.card;

import com.bbw.common.Rst;
import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserCardController extends AbstractController {
    @Autowired
    private UserCardLogic userCardLogic;
    @Autowired
    private UserCardService userCardService;

    /**
     * 升级卡牌
     *
     * @param param
     * @return
     */
    @GetMapping(CR.Card.UPDATE)
    public RDCommon updateCard(CPCardUpdate param) {
        param.checkVal();
        if (param.getTotalEleAndSoul() < 1) {
            throw new ExceptionForClientTip("card.not.eleOrLingshi");
        }
        return userCardLogic.updateCard(getUserId(), param);
    }

    /**
     * 卡牌进阶
     *
     * @param cardId
     * @param isUseUniversalSoul 是否使用万能灵石
     * @return
     */
    @GetMapping(CR.Card.ADVANCED)
    public RDCommon updateHierarchy(Long dataId, int cardId, String isUseUniversalSoul) {
        // 兼容客户端可能没传isUseUniversalSoul
        if (StrUtil.isBlank(isUseUniversalSoul)) {
            isUseUniversalSoul = "0";
        }
        return userCardLogic.updateHierarchy(getUserId(), dataId, cardId, Integer.valueOf(isUseUniversalSoul));
    }

    /**
     * 使用技能卷轴（炼技）
     *
     * @param param
     * @return
     */
    @GetMapping(CR.Card.USE_SKILL_SCROLL)
    public RDCardStrengthen useSkillScroll(CPCardUseSkillScroll param) {
        return userCardLogic.useSkillScroll(getUserId(), param);
    }

    /**
     * 添加技能组
     *
     * @param cardId 卡牌id
     * @return
     */
    @GetMapping(CR.Card.ADD_SKILL_GROUP)
    public RDCommon addSkillGroup(Long dataId, int cardId) {
        return userCardLogic.activationSkillGroup(getUserId(), dataId, cardId);
    }

    /**
     * 切换技能组
     *
     * @param cardId         卡牌id
     * @param skillGroupName 技能组名称
     * @return
     */
    @GetMapping(CR.Card.CHANGE_SKILL_GROUP)
    public RDChangeSkillGroups changeSkillGroup(Long dataId, int cardId, String skillGroupName) {
        return userCardLogic.changeSkillGroup(getUserId(), dataId, cardId, skillGroupName);
    }

    /**
     * 获取技能组
     *
     * @param cardId 卡牌id
     * @return
     */
    @GetMapping(CR.Card.GET_SKILL_GROUP)
    public RDSkillGroups getSkillGroup(Long dataId, int cardId) {
        return userCardLogic.getSkillGroup(getUserId(), dataId, cardId);
    }

    /**
     * 清除使用的技能卷轴
     *
     * @return
     */
    @GetMapping(CR.Card.CLEAR_SKILL_SCROLL)
    public RDCardStrengthen clearSkillScroll(Long dataId, int cardId, Integer pos) {
        return userCardLogic.clearSkillScroll(getUserId(), dataId, cardId, pos);
    }

    /**
     * 取回技能
     *
     * @return
     */
    @GetMapping(CR.Card.TAKE_OUT_SKILL_SCROLL)
    public RDCardStrengthen takeOutSkillScroll(Long dataId, int cardId, int pos) {
        return userCardLogic.takeOutSkillScroll(getUserId(), dataId, cardId, pos);
    }

    /**
     * 穿上符箓（修体）
     *
     * @param param
     * @return
     */
    @GetMapping(CR.Card.PUT_ON_SYMBOL)
    public RDCardStrengthen putOnSymbol(CPSymbol param) {
        return userCardLogic.putOnSymbol(getUserId(), param);
    }

    /**
     * 卸下符箓
     *
     * @param param
     * @return
     */
    @GetMapping(CR.Card.UNLOAD_SYMBOL)
    public RDCardStrengthen unloadSymbol(CPSymbol param) {
        return userCardLogic.unloadSymbol(getUserId(), param);
    }

    /**
     * 设置展示的卡牌
     *
     * @param cardIds
     * @return
     */
    @GetMapping(CR.Card.SET_SHOW_CARDS)
    public Rst setShowCards(String cardIds) {
        List<Integer> ids = new ArrayList<Integer>();
        if (StrUtil.isNotBlank(cardIds)) {
            String strs[] = cardIds.split(";");
            for (String str : strs) {
                try {
                    Integer id = Integer.valueOf(str);
                    ids.add(id);
                } catch (Exception e) {
                    //无效字符
                }
            }
        }
        userCardService.setShowCard(getUserId(), ids);
        return Rst.businessOK();
    }
}
