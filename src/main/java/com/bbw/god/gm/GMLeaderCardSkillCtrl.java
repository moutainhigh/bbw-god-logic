package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.config.card.CardSkillTool;
import com.bbw.god.game.config.card.CfgCardSkill;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.gameuser.leadercard.skil.LeaderCardSkillTreeService;
import com.bbw.god.gameuser.leadercard.skil.UserLeaderCardSkillTree;
import com.bbw.god.server.ServerUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * 主角卡技能相关处理
 *
 * @author fzj
 * @date 2021/10/8 9:28
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMLeaderCardSkillCtrl extends AbstractController {
    @Autowired
    LeaderCardSkillTreeService leaderCardSkillTreeService;
    @Autowired
    ServerUserService serverUserService;
    @Autowired
    LeaderCardService leaderCardService;

    /**
     * 重置主角卡技能
     *
     * @param sid
     * @param username
     * @param skillName 技能名称
     * @param property  属性 10-金系 20-木系 30-水系 40-火系 50-土系 60-专属
     * @return
     */
    @RequestMapping("user!cancelLeaderCardSkill")
    public Rst cancelLeaderCardSkill(int sid, String username, String skillName, int property) {
        Long uid = serverUserService.getUidByNickName(sid, username).get();
        CfgCardSkill cardSkill = CardSkillTool.getCardSkillByName(skillName);
        Integer cardSkillId = cardSkill.getId();
        //获取对应属性的技能树
        UserLeaderCardSkillTree userSkillTree = leaderCardSkillTreeService.getUserSkillTree(uid, property);
        //把技能设为未激活状态
        for (UserLeaderCardSkillTree.SkillPage ownSkillPage : userSkillTree.getOwnSkillPages()) {
            if (!ownSkillPage.getOwnSkills().contains(cardSkillId)) {
                continue;
            }
            ownSkillPage.getOwnSkills().remove(cardSkillId);
        }
        gameUserService.updateItem(userSkillTree);
        //检查是否已装备改技能，若已经装备则撤下
        UserLeaderCard leaderCard = leaderCardService.getUserLeaderCard(uid);
        if (property == 60) {
            for (int index = 10; index <=50; index += 10){
                List<int[]> skillsGroupInfo = leaderCard.gainSkills(index).getSkillsGroupInfo();
                for (int[] skills : skillsGroupInfo){
                    for (int i = 0; i < skills.length; i++) {
                        if (skills[i] == cardSkillId) {
                            skills[i] = 0;
                        }
                    }
                }
            }
            gameUserService.updateItem(leaderCard);
        } else {
            int propertyIndex = property / 10 - 1;
            List<int[]> skillsGroupInfo = leaderCard.gainSkills(propertyIndex).getSkillsGroupInfo();
            for (int[] skills : skillsGroupInfo){
                for (int i = 0; i < skills.length; i++) {
                    if (skills[i] == cardSkillId) {
                        skills[i] = 0;
                    }
                }
            }
            gameUserService.updateItem(leaderCard);
        }
        return Rst.businessOK();
    }

    /**
     * 激活主角卡专属技能（仅限于专属技能）
     * @param sid
     * @param username
     * @param skillName
     * @return
     */
    @RequestMapping("user!addLeaderCardSkill")
    public Rst addLeaderCardSkill(int sid, String username, String skillName) {
        Long uid = serverUserService.getUidByNickName(sid, username).get();
        CfgCardSkill cardSkill = CardSkillTool.getCardSkillByName(skillName);
        Integer cardSkillId = cardSkill.getId();
        //获取专属技能的技能树
        UserLeaderCardSkillTree userSkillTree = leaderCardSkillTreeService.getUserSkillTree(uid, 60);
        //把技能设为激活状态
        for (UserLeaderCardSkillTree.SkillPage ownSkillPage : userSkillTree.getOwnSkillPages()) {
            ownSkillPage.getOwnSkills().add(cardSkillId);
        }
        gameUserService.updateItem(userSkillTree);
        return Rst.businessOK();
    }
}
