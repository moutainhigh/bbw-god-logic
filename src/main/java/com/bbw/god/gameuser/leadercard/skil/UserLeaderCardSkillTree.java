package com.bbw.god.gameuser.leadercard.skil;

import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class UserLeaderCardSkillTree extends UserData {
    private List<SkillPage> ownSkillPages =new ArrayList<>();
    private Integer property;

    /**
     * 是否在指定页中激活了技能
     * @param page
     * @param skillId
     * @return
     */
    public boolean ifOwnSkill(int page,int skillId){
        Optional<SkillPage> optional = ownSkillPages.stream().filter(p -> p.getPage() == page).findFirst();
        if (optional.isPresent()){
           return optional.get().getOwnSkills().contains(skillId);
        }
        return false;
    }

    /**
     *
     * 是否在某一页中激活了该技能
     * @param skillId
     * @return
     */
    public boolean ifOwnSkill(int skillId){
        for (SkillPage ownSkill : ownSkillPages) {
            if (ownSkill.getOwnSkills().contains(skillId)){
                return true;
            }
        }
        return false;
    }

    public void addSkill(int page,int skillId){
        Optional<SkillPage> optional = ownSkillPages.stream().filter(p -> p.getPage() == page).findFirst();
        if (optional.isPresent()){
            optional.get().addSkill(skillId);
        }else {
            SkillPage skillPage = new SkillPage(page,new ArrayList<>());
            skillPage.addSkill(skillId);
            ownSkillPages.add(skillPage);
        }
    }

    /**
     * 获得所有该属性已经激活的技能
     * @return
     */
    public List<Integer> showAllSkills(){
        List<Integer> skills=new ArrayList<>();
        for (SkillPage skillPage : ownSkillPages) {
            for (Integer skill : skillPage.getOwnSkills()) {
                if (!skills.contains(skill)){
                    skills.add(skill);
                }
            }
        }
        return skills;
    }

    /**
     * 找到一个激活技能的页码  页码小的优先
     * @param skillId
     * @return
     */
    public SkillPage firstActivePage(int skillId){
        for (SkillPage skillPage : ownSkillPages) {
            if ( skillPage.getOwnSkills().contains(skillId)){
                return skillPage;
            }
        }
        return null;
    }

    /**
     * 根据页码获取页
     * @param page
     * @return
     */
    public SkillPage getPage(int page){
        Optional<SkillPage> optional = ownSkillPages.stream().filter(p -> p.getPage() == page).findFirst();
        if (optional.isPresent()){
            return optional.get();
        }
        SkillPage skillPage = new SkillPage(page, new ArrayList<>());
        ownSkillPages.add(skillPage);
        return skillPage;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SkillPage implements Serializable {
       private int page;
       private List<Integer> ownSkills=new ArrayList<>();

       public boolean ifOwnSkill(int skillId){
            return ownSkills.contains(skillId);
       }

       public void addSkill(Integer skillId){
           if (!ownSkills.contains(skillId)){
               ownSkills.add(skillId);
           }
       }
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_LEADER_CARD_SKILL_TREE;
    }
}
