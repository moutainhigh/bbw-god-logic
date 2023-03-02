package com.bbw.god.gameuser.card;

import com.bbw.common.Rst;
import com.bbw.common.SensitiveWordUtil;
import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.game.wanxianzhen.service.WanXianLogic;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 卡组相关接口
 *
 * @author: suhq
 * @date: 2021/11/16 3:29 下午
 */
@RestController
public class UserCardGroupCtrl extends AbstractController {
    @Autowired
    private UserCardGroupLogic userCardGroupLogic;
    @Autowired
    private UserCardGroupShareService userCardGroupShareService;
    @Autowired
    private WanXianLogic wanXianLogic;
    @Autowired
    private UserCardGroupService userCardGroupService;

    /**
     * 设置可出战卡牌
     *
     * @param cardIds 101,103,120;303,305
     * @return
     */
    @GetMapping(CR.Card.SET_FIGHT_CARDS)
    public RDCommon setFightCards(String cardIds) {
        if (StrUtil.isBlank(cardIds)) {
            throw new ExceptionForClientTip("card.grouping.not.blank");
        }
        return userCardGroupLogic.setFightCards(getUserId(), cardIds);
    }

    /**
     * 设置激战的攻防卡组
     *
     * @param cardIds 101,103,120;101  前面为攻后面为防
     * @return
     */
    @GetMapping(CR.Card.SET_FIERCE_FIGHTING_CARDS)
    public Rst setFierceFightingCards(String cardIds, Integer isGameFst) {
        if (StrUtil.isBlank(cardIds)) {
            throw new ExceptionForClientTip("card.grouping.not.blank");
        }
        if (isGameFst != null && isGameFst == 1) {
            userCardGroupLogic.setGameFstCards(getUserId(), cardIds);
        } else {
            userCardGroupLogic.setFierceFightingCards(getUserId(), cardIds);
        }
        return Rst.businessOK();
    }

    /**
     * 获取激战卡组
     *
     * @param isGameFst 选传，不传时默认为获取普通激战攻防卡组
     * @return
     */
    @GetMapping(CR.Card.GET_FIERCE_FIGHTING_CARDS)
    public RDCardGroups getFierceFightingCards(Integer isGameFst) {
        if (isGameFst != null && isGameFst == 1) {
            return userCardGroupLogic.getGameFstCards(getUserId());
        }
        return userCardGroupLogic.getFierceFightingCards(getUserId());
    }

    /**
     * 设置默认卡组
     *
     * @return
     */
    @GetMapping(CR.Card.SET_DEFAULT_DECK)
    public RDSuccess setDefaultDeck(int deck) {
        if (deck < 1 || deck > 7) {
            throw new ExceptionForClientTip("card.not.validGroup");
        }
        return userCardGroupLogic.setDefaultDeck(getUserId(), deck);
    }

    /**
     * 设置卡组昵称
     *
     * @return
     */
    @GetMapping(CR.Card.SET_GROUP_NAME)
    public Rst setGroupName(int deck, String name) {
        if (StrUtil.isBlank(name)) {
            throw new ExceptionForClientTip("card.group.name.error");
        }
        name = name.replaceAll(" ", "");
        LoginPlayer user = getUser();
        if (SensitiveWordUtil.isNotPass(name, user.getChannelId(), user.getOpenId())) {
            throw new ExceptionForClientTip("card.group.name.error");
        }
        userCardGroupLogic.setCardGroupName(getUserId(), deck, name);
        return Rst.businessOK();
    }

    /**
     * 分享卡组
     *
     * @param way 分享的途径
     */
    @GetMapping(CR.Card.SHARE_CARD_GROUP)
    public RDShareCardGroup shareCardGroup(String groupName, int way) {
        return userCardGroupShareService.shareCardGroup(getUserId(), groupName, way);
    }

    /**
     * 查看分享的卡组
     *
     * @param shareId
     */
    @GetMapping(CR.Card.GET_SHARE_CARD_GROUP)
    public RDShareCardGroup getShareCardGroup(String shareId) {
        return userCardGroupShareService.getShareCardGroupById(shareId);
    }

    /**
     * 收藏卡组
     *
     * @param shareId 要收藏的卡组id
     * @return 业务成功与否
     */
    @GetMapping(CR.Card.COLLECT_CARD_GROUP)
    public Rst collectCardGroup(String shareId, Integer type, Integer season, Long uid, Integer isFst) {
        if (type != null && (WanXianLogic.TYPE_REGULAR_RACE == type || WanXianLogic.TYPE_SPECIAL_RACE == type)) {
            wanXianLogic.collectCardGroup(uid, getUserId(), type, season);
        } else if (uid != null && isFst != null && isFst == 1) {
            userCardGroupService.collectServerFstCardGroup(uid);
        } else {
            userCardGroupShareService.collectShareCardGroup(getUserId(), shareId);
        }
        return Rst.businessOK();
    }


    /**
     * 获取收藏卡组信息
     *
     * @return 收藏卡组信息
     */
    @GetMapping(CR.Card.GET_COLLECT_CARD_GROUP)
    public RDCollectCardGroups getCollectCardGroups() {
        return userCardGroupShareService.getCollectCardGroups(getUserId());
    }

    /**
     * 删除收藏卡组信息
     *
     * @return 收藏卡组信息
     */
    @GetMapping(CR.Card.DEL_COLLECT_CARD_GROUP)
    public Rst delCollectCardGroup(String shareId) {
        userCardGroupShareService.delCollectCardGroup(getUserId(), shareId);
        return Rst.businessOK();
    }

    @GetMapping(CR.Card.SET_FUCE)
    public Rst setFuCe(int fuCeId, int cardGroupType, Integer groupNumber) {
        userCardGroupLogic.setFuCe(getUserId(), fuCeId, cardGroupType, groupNumber);
        return Rst.businessOK();
    }

    @GetMapping(CR.Card.SYNC_CARD_GROUP)
    public Rst syncCardGroup(int syncWay) {
        userCardGroupLogic.syncCardGroups(getUserId(), syncWay);
        return Rst.businessOK();
    }
}
