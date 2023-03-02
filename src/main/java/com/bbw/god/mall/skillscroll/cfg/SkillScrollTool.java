package com.bbw.god.mall.skillscroll.cfg;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.treasure.CfgSkillScrollLimitEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 技能卷轴工具
 * @date 2021/2/5 10:59
 **/
public class SkillScrollTool {

    public static List<CfgSkillScroll> getSkillScrolls() {
        return Cfg.I.get(CfgSkillScroll.class);
    }

    public static CfgSkillScroll getSkillScroll(int chapter) {
        return getSkillScrolls().stream().filter(tmp -> tmp.getChapter() == chapter).findFirst().orElse(null);
    }

    public static List<CfgDesignateSkillScroll> getDesignateSkillScrolls() {
        return Cfg.I.get(CfgDesignateSkillScroll.class);
    }

    public static CfgDesignateSkillScroll getDesignateSkillScroll(int chapter) {
        return getDesignateSkillScrolls().stream().filter(tmp -> tmp.getChapter() == chapter).findFirst().orElse(null);
    }

    /**
     * 根据 属性和 技能ID获取卷轴
     * @param skillId
     * @param property
     * @return
     */
    public static CfgSkillScrollLimitEntity getSkillScrollBySkillIdAndProperty(int skillId,int property){
        List<CfgSkillScrollLimitEntity> entities = getCfgSkillScrollLimitEntity();
        TypeEnum value = TypeEnum.fromValue(property);
        String pre=value.getName()+"-";
        for (CfgSkillScrollLimitEntity entity : entities) {
            if (entity.getName().lastIndexOf(pre)>-1 && entity.getSkillId()==skillId && entity.getLimitTypes().contains(property)){
                return entity;
            }
        }
        return null;
    }


    /**
     * 获取卡牌的专属技能配置
     *
     * @param cardId
     * @return
     */
    public static List<CfgSkillScrollLimitEntity> getExclusiveCardSkillLimits(Integer cardId) {
        //获取全部配置
        List<CfgSkillScrollLimitEntity> skillLimits = getCfgSkillScrollLimitEntity();
        //获取专属技能卡牌的配置
        List<CfgSkillScrollLimitEntity> result = skillLimits.stream()
                .filter(entitie -> ListUtil.isNotEmpty(entitie.getLimitCards()) &&  entitie.getLimitCards().get(0).equals(cardId))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * 获取可升级技能配置
     *
     * @param skillId
     * @return
     */
    public static List<CfgSkillScrollLimitEntity> getUpgradeableCardSkillLimits(Integer skillId) {
        //获取全部配置
        List<CfgSkillScrollLimitEntity> skillLimits = getCfgSkillScrollLimitEntity();
        //获取专属技能卡牌的配置
        List<CfgSkillScrollLimitEntity> result = skillLimits.stream()
                .filter(entitie -> ListUtil.isEmpty(entitie.getLimitCards()) && entitie.getLimitSkills().contains(skillId))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * 根据卡牌的属性获取技能
     *
     * @param type
     * @return
     */
    public static List<CfgSkillScrollLimitEntity> getTypeSkillLimits(int type) {
        //获取全部配置
        List<CfgSkillScrollLimitEntity> skillLimits = getCfgSkillScrollLimitEntity();
        //过滤出不是专属技能，或者是可强化的技能
        List<CfgSkillScrollLimitEntity> result = skillLimits.stream()
                .filter(entitie -> entitie.getLimitTypes().contains(type))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * 根据卷轴ID 获取卷轴配置
     * @param scrollId
     * @return
     */
    public static Optional<CfgSkillScrollLimitEntity> getCfgSkillScrollLimitEntity(int scrollId){
        List<CfgSkillScrollLimitEntity> entities = getCfgSkillScrollLimitEntity();
        Optional<CfgSkillScrollLimitEntity> optional = entities.stream().filter(p -> p.getId() == scrollId).findFirst();
        return optional;
    }

    /**
     * 获取专属卷轴
     * @param skillId
     * @return
     */
    public static CfgSkillScrollLimitEntity getExclusiveSkillScroll(int skillId){
        List<CfgSkillScrollLimitEntity> entities = getCfgSkillScrollLimitEntity();
        String pre="专属-";
        for (CfgSkillScrollLimitEntity entity : entities) {
            if (entity.getName().lastIndexOf(pre)>-1 && entity.getSkillId()==skillId){
                return entity;
            }
        }
        throw new ExceptionForClientTip("skill.scroll.not.find","专属-"+skillId);
    }

    /**
     * 通过技能ID获取密传
     * @param skillId
     * @return
     */
    public static CfgSkillScrollLimitEntity getSecretSkillScrollBySkillId(int skillId){
        List<CfgSkillScrollLimitEntity> entities = getCfgSkillScrollLimitEntity();
        String pre="全-";
        for (CfgSkillScrollLimitEntity entity : entities) {
            if (entity.getName().lastIndexOf(pre)>-1 && entity.getSkillId()==skillId && ListUtil.isEmpty(entity.getLimitTypes())){
                return entity;
            }
        }
        return null;
    }

    /**
     * 获取卷轴 优先获取对应属性的  没有则返回蜜传
     *
     * </br>
     * 如何都没有找到则抛出ExceptionForClientTip异常提示
     * @param skillId
     * @param property
     * @return
     */
    public static CfgSkillScrollLimitEntity getSkillScrollBySkillId(int skillId,Integer property){
        CfgSkillScrollLimitEntity skillScrollLimitEntity = null;
        if (property!=null) {
            skillScrollLimitEntity = getSkillScrollBySkillIdAndProperty(skillId, property);
        }
        if (skillScrollLimitEntity==null){
            skillScrollLimitEntity=getSecretSkillScrollBySkillId(skillId);
        }
        if (skillScrollLimitEntity!=null){
            return skillScrollLimitEntity;
        }
        throw new ExceptionForClientTip("skill.scroll.not.find",skillId);
    }

    public static List<CfgSkillScrollLimitEntity> getCfgSkillScrollLimitEntity(){
        return Cfg.I.get(CfgSkillScrollLimitEntity.class);
    }
}
