package com.bbw.god.gameuser.leadercard.skil;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分身卡技海接口
 * @author：lwb
 * @date: 2021/3/22 13:54
 * @version: 1.0
 */
@RestController
public class LeaderCardSkillTreeController extends AbstractController {
    @Autowired
    private LeaderCardSkillTreeService skillTreeService;

    /**
     * 技海主页面
     * @return
     */
    @RequestMapping(CR.LeaderCard.SKILL_TREE)
    public RDLeaderCardSkillTree skillTree(){
        return skillTreeService.showAllSkill(getUserId());
    }

    /**
     * 技海 替换技能
     *
     * @param skillId 技能ID
     * @param pos 技能位置 0，1，2  对应 0级技能 5级技能 10级技能
     * @return
     */
    @RequestMapping(CR.LeaderCard.REPLACE_SKILL)
    public RDLeaderCardSkillTree replaceSkill(int skillId,int pos){
        return skillTreeService.replaceSkill(getUserId(),skillId,pos);
    }

    /**
     * 技海列表页面
     * @return
     */
    @RequestMapping(CR.LeaderCard.SKILL_TREE_LIST)
    public RDLeaderCardSkillTree listSkillTree(int property){
        return skillTreeService.listSkillTree(getUserId(),property);
    }

    /**
     * 技海 树图
     * @return
     */
    @RequestMapping(CR.LeaderCard.SKILL_TREE_INFO)
    public RDLeaderCardSkillTree getSkillTreeInfo(Integer skillId,Integer property){
        return skillTreeService.getSkillTreeInfoBySkillID(getUserId(),property,skillId);
    }
    /**
     * 技海 树图  翻页
     * @return
     */
    @RequestMapping(CR.LeaderCard.TURN_PAGE_SKILL_TREE)
    public RDLeaderCardSkillTree getTurnPageSkillTreeInfo(Integer page,Integer property){
        return skillTreeService.turnPageSkillTreeInfo(getUserId(),property,page);
    }

    /**
     * 技海 激活技能
     * @return
     */
    @RequestMapping(CR.LeaderCard.SKILL_TREE_ACTIVE)
    public RDLeaderCardSkillTree activeSkill(int property,Integer page,int skillId){
        return skillTreeService.activeSkill(getUserId(),property,page,skillId);
    }
}
