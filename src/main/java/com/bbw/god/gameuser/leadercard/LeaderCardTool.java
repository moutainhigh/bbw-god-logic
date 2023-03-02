package com.bbw.god.gameuser.leadercard;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.gameuser.leadercard.skil.CfgLeaderCardSkill;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：lwb
 * @date: 2021/3/22 14:45
 * @version: 1.0
 */
public class LeaderCardTool {

    public static int getLeaderCardId(){
        return 1001;
    }

    public static CfgLeaderCard getCfgLeaderCard(){
        return Cfg.I.getUniqueConfig(CfgLeaderCard.class);
    }

    /**
     * 获得初始化技能列表
     * @return
     */
    public static List<CfgLeaderCard.InitSkill> getInitSkills(){
        CfgLeaderCard cfgLeaderCard = getCfgLeaderCard();
        return cfgLeaderCard.getInitSkills();
    }

    /**
     * 获取所有初始化技能ID
     * @return
     */
    public static List<Integer> getInitSkillIds(){
        List<Integer> ids=new ArrayList<>();
        for (CfgLeaderCard.InitSkill skill : getInitSkills()) {
            ids.add(skill.getSkill());
        }
        return ids;
    }

    /**
     * 根据当前的阶级获取条件
     * @param hv
     * @return
     */
    public static CfgLeaderCard.UpHvCondition getCurrentConditionByHv(int hv){
        CfgLeaderCard leaderCard = getCfgLeaderCard();
        for (CfgLeaderCard.UpHvCondition upHvCondition : leaderCard.getUpHvConditions()) {
            if (upHvCondition.getHv()==hv){
                return upHvCondition;
            }
        }
        return null;
    }

    /**
     * 获取阶级加成的最大值
     * @param hv
     * @return
     */
    public static int getCurrentAddTopLimitByHv(int hv){
        if (hv<=0){
            return 0;
        }
        return getCurrentConditionByHv(hv).getTopLimit();
    }

    /**
     * 获取随机技能需要消耗的元宝数
     * @param times 第几次数
     * @return
     */
    public static int getRandomSKillNeedGold(int times){
        switch (times){
            case 1:return 0;
            case 2:return 50;
            case 3:return 70;
            case 4:return 90;
            case 5:return 110;
            case 6:return 130;
            case 7:return 150;
            case 8:return 170;
            default:
                return 200;
        }
    }

    /**
     * 根据等级获取 升到该等级需要的经验
     * @param lv
     * @return
     */
    public static long getLvNeedExp(int lv){
        if (lv==0){
            return 0;
        }
        CfgLeaderCard cfgLeaderCard = getCfgLeaderCard();
        for (CfgLeaderCard.ExpCondition condition : cfgLeaderCard.getExps()) {
            if (condition.getLv()==lv){
                return condition.getExp();
            }
        }
        return cfgLeaderCard.getExps().get(cfgLeaderCard.getExps().size()-1).getExp();
    }

    /**
     * 获取最大的升级条件
     * @return
     */
    public static CfgLeaderCard.ExpCondition getMaxExpCondition(){
        CfgLeaderCard cfgLeaderCard = getCfgLeaderCard();
        return cfgLeaderCard.getExps().get(cfgLeaderCard.getExps().size()-1);
    }


    /**
     * 获取技能树总配置
     * @return
     */
    public static CfgLeaderCardSkill getCfgLeaderCardSkill(){
        return Cfg.I.getUniqueConfig(CfgLeaderCardSkill.class);
    }

    /**
     * 根据属性和页码获取技能树配置
     * @param property 属性
     * @param page 页码
     * @return
     */
    public static CfgLeaderCardSkill.SkillTree getSkillTreeConfig(int property,int page){
        CfgLeaderCardSkill cfgCardSkill = getCfgLeaderCardSkill();
        for (CfgLeaderCardSkill.SkillTree tree : cfgCardSkill.getSkillTrees()) {
            if (tree.getProperty()==property && tree.getPage()==page){
                return tree;
            }
        }
        throw new ExceptionForClientTip("leader.card.skill.tree.not.exist",property,page);
    }

    /**
     * 解锁属性所需要的元宝数量
     * @return
     */
    public static int getUnlockPropertyNeedGold(){
        return getCfgLeaderCard().getUnlockPropertyNeedGod();
    }
}
