package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.card.CardSkillPosEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCard.UserCardStrengthenInfo;
import com.bbw.god.server.ServerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 玩家数据相关的操作
 *
 * @author suhq
 * @date 2019年4月13日 下午1:49:18
 */
@RestController
@RequestMapping("/gm")
public class GMUserCardCtrl extends AbstractController {
    @Autowired
    private UserGmService userGmService;
    @Autowired
    private ServerUserService serverUserService;

    /**
     * 修复玩家卡牌技能数据
     * @param sId 区服
     * @param nickname 昵称
     * @return
     * @date 2022年9月6日 下午12:44
     */
    @GetMapping("/user!repairUserSkillGroup")
    public Rst repairUserSkillGroup(int sId, String nickname){
        return userGmService.repairUserSkillGroup(sId,nickname);
    }

    /**
     * 加卡牌
     *
     * @param sId
     * @param nickname
     * @param cards
     * @return
     */
    @RequestMapping("user!addCards")
    public Rst addCards(int sId, String nickname, String cards) {
        return this.userGmService.addCards(sId, nickname, cards);
    }

    /**
     * 删除卡牌
     *
     * @param sId
     * @param nickname
     * @param cards
     * @return
     */
    @RequestMapping("user!delCards")
    public Rst delCards(int sId, String nickname, String cards) {
        return this.userGmService.delCards(sId, nickname, cards);
    }

    @RequestMapping("user!delCardsForMultiUsers")
    public Rst delCards(String uids, String cards) {
        return this.userGmService.delCards(uids, cards);
    }

    @RequestMapping("user!fixUserGroup")
    public Rst fixUserGroup(String uids) {
        return this.userGmService.fixUserGroup(uids);
    }

    /**
     * 添加灵石
     *
     * @param sId
     * @param nickname
     * @param cards
     * @param num
     * @return
     */
    @RequestMapping("user!addLingShi")
    public Rst addLingShi(int sId, String nickname, String cards, int num) {
        return this.userGmService.addLingShi(sId, nickname, cards, num);
    }

    /**
     * 调整卡牌等级
     *
     * @param sId
     * @param nickname
     * @param cards
     * @param minLevel
     * @param level
     * @return
     */
    @RequestMapping("user!updateCardToLevel")
    public Rst updateCardToLevel(int sId, String nickname, String cards, int minLevel, int level) {
        return this.userGmService.updateCardToLevel(sId, nickname, cards, minLevel, level);
    }

    /**
     * 调整卡牌等级
     *
     * @param sId
     * @param nickname
     * @param cards    cardCfgId,lv,exp,hie;cardCfgId,lv,exp,hie 如果值小于0则忽略
     * @return
     */
    @RequestMapping("user!updateCardsToLvAndHie")
    public Rst updateCardsToLvAndHie(int sId, String nickname, String cards) {
        return this.userGmService.updateCardsToLvAndHie(sId, nickname, cards);
    }

    /**
     * 调整卡牌阶数
     *
     * @param sId
     * @param nickname
     * @param cards
     * @param minHierarchy
     * @param hierarchy
     * @return
     */
    @RequestMapping("user!updateCardToHierarchy")
    public Rst updateCardToHierarchy(int sId, String nickname, String cards, int minHierarchy, int hierarchy) {
        return this.userGmService.updateCardToHierarchy(sId, nickname, cards, minHierarchy, hierarchy);
    }

    /**
     * 重置卡牌技能
     *
     * @param sid
     * @param nickname
     * @param cards
     * @return
     */
    @RequestMapping("user!resetCardSkill")
    public Rst resetCardSkill(int sid, String nickname, String cards) {
        return this.userGmService.resetCardSkill(sid, nickname, cards);
    }

    /**
     * 重置特定位置的卡牌的技能
     *
     * @param sid
     * @param nickname
     * @param cardsWithPoses 姜子牙@0,5;杨戬@10
     * @return
     */
    @RequestMapping("user!resetCardsSkillWithPoses")
    public Rst resetCardsSkillWithPoses(int sid, String nickname, String cardsWithPoses) {
        return this.userGmService.resetCardsSkillInPoses(sid, nickname, cardsWithPoses);
    }

    /**
     * 手动配置卡牌技能
     *
     * @param sId
     * @param nickname
     * @param card
     * @param s0
     * @param s5
     * @param s10
     * @return
     */
    @RequestMapping("user!cardSkillDiv")
    public Rst DivCardSkill(int sId, String nickname, String card, int s0, int s5, int s10) {
        Optional<Long> guId = this.serverUserService.getUidByNickName(sId, nickname);
        if (!guId.isPresent()) {
            return Rst.businessFAIL("无效用户名");
        }
        long uid = guId.get();
        CfgCardEntity ccs = CardTool.getCardByName(card);
        if (ccs == null) {
            return Rst.businessFAIL("无效卡名");
        }
        UserCard userCard = this.gameUserService.getCfgItem(uid, ccs.getId(), UserCard.class);
        if (userCard == null) {
            return Rst.businessFAIL("未拥有的卡");
        }
        UserCardStrengthenInfo strengthenInfo = new UserCardStrengthenInfo();
        if (s0 > 0) {
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_0,s0);
        }
        if (s5 > 0) {
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_5,s5);
        }
        if (s10 > 0) {
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_10,s10);
        }
        userCard.setStrengthenInfo(strengthenInfo);
        gameUserService.updateItem(userCard);
        return Rst.businessOK();
    }

    @RequestMapping("user!resetUserCardSkill")
    public Rst resetUserCardSkill(int sId, String nickname, String card) {
        Optional<Long> guId = this.serverUserService.getUidByNickName(sId, nickname);
        if (!guId.isPresent()) {
            return Rst.businessFAIL("无效用户名");
        }
        long uid = guId.get();
        CfgCardEntity ccs = CardTool.getCardByName(card);
        if (ccs == null) {
            return Rst.businessFAIL("无效卡名");
        }
        UserCard userCard = this.gameUserService.getCfgItem(uid, ccs.getId(), UserCard.class);
        if (userCard == null) {
            return Rst.businessFAIL("未拥有的卡");
        }
        UserCardStrengthenInfo strengthenInfo = userCard.getStrengthenInfo();
        strengthenInfo.resetCurrentSkillGroup();
        gameUserService.updateItem(userCard);
        return Rst.businessOK();
    }

    @RequestMapping("user!showUserCardInfo")
    public Rst showUserCardInfo(int sId, String nickname, String card) {
        Optional<Long> guId = this.serverUserService.getUidByNickName(sId, nickname);
        if (!guId.isPresent()) {
            return Rst.businessFAIL("无效用户名");
        }
        long uid = guId.get();
        CfgCardEntity ccs = CardTool.getCardByName(card);
        if (ccs == null) {
            return Rst.businessFAIL("无效卡名");
        }
        UserCard userCard = this.gameUserService.getCfgItem(uid, ccs.getId(), UserCard.class);
        if (userCard == null) {
            return Rst.businessFAIL("未拥有的卡");
        }
        return Rst.businessOK(userCard.toString());
    }

}
