package com.bbw.god.gm.admin.ctrl;

import com.bbw.common.Rst;
import com.bbw.god.gm.UserGmService;
import com.bbw.god.gm.admin.CRAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 后台接口-玩家卡牌相关的操作
 * @author：lzc
 * @date: 2021/03/17 11:28
 * @version: 1.0
 */
@RequestMapping("/gm/admin")
@RestController
public class GMAdminUserCardCtrl {
    @Autowired
    private UserGmService userGmService;

    /**
     * 加卡牌
     *
     * @param sId
     * @param nickname
     * @param cards    所有;姜子牙,杨戬
     * @return
     */
    @RequestMapping(CRAdmin.UserCard.ADD_CARDS)
    public Rst addCards(int sId, String nickname, String cards) {
        return userGmService.addCards(sId,nickname,cards);
    }

    /**
     * 重置卡牌技能
     *
     * @param sid
     * @param nickname
     * @param cards
     * @return
     */
    @RequestMapping(CRAdmin.UserCard.RESET_CARD_SKILL)
    public Rst resetCardSkill(int sid, String nickname, String cards) {
        return userGmService.resetCardSkill(sid,nickname,cards);
    }

    /**
     * 删除卡牌
     *
     * @param sId
     * @param nickname
     * @param cards    所有;姜子牙,杨戬
     * @return
     */
    @RequestMapping(CRAdmin.UserCard.DEL_CARDS)
    public Rst delCards(int sId, String nickname, String cards) {
        return userGmService.delCards(sId,nickname,cards);
    }

    /**
     * 删除卡牌
     *
     * @param uids
     * @param cards    所有;姜子牙,杨戬
     * @return
     */
    @RequestMapping(CRAdmin.UserCard.DEL_CARDS_OF_UIDS)
    public Rst delCardsOfUids(String uids, String cards) {
        return userGmService.delCards(uids,cards);
    }

    /**
     * 调整卡牌等级
     *
     * @param sId
     * @param nickname
     * @param cards    所有;姜子牙,杨戬
     * @param minLevel 将>=minLevel的cards调为level
     * @param level
     * @return
     */
    @RequestMapping(CRAdmin.UserCard.UPDATE_CARD_TO_LEVEL)
    public Rst updateCardToLevel(int sId, String nickname, String cards, int minLevel, int level) {
        return userGmService.updateCardToLevel(sId,nickname,cards,minLevel,level);
    }

    /**
     * 调整卡牌阶数
     *
     * @param sId
     * @param nickname
     * @param cards        所有;姜子牙,杨戬
     * @param minHierarchy 将>=minHierarchy的cards调为hierarchy
     * @param hierarchy
     * @return
     */
    @RequestMapping(CRAdmin.UserCard.UPDATE_CARD_TO_HIERARCHY)
    public Rst updateCardToHierarchy(int sId, String nickname, String cards, int minHierarchy, int hierarchy) {
        return userGmService.updateCardToHierarchy(sId,nickname,cards,minHierarchy,hierarchy);
    }
}
