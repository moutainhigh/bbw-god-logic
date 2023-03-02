package com.bbw.god.gameuser.card;

import com.bbw.common.*;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardSkillTool;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCard;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.CfgSkillScrollLimitEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.biyoupalace.cfg.BYPalaceTool;
import com.bbw.god.gameuser.biyoupalace.cfg.CfgBYPalaceSymbolEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.*;

/**
 * 用户卡牌
 *
 * @author suhq 2018年09月30日 下午2:14:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserCard extends UserCfgObj implements Serializable {
    public static final UserDataType DATA_TYPE = UserDataType.CARD;
    private static final long serialVersionUID = 1L;

    private Integer level = 0;
    private Long experience = 0L;
    private Integer hierarchy = 0;
    private Integer lingshi = 0;
    @Deprecated
    private Boolean deck1;
    @Deprecated
    private Boolean deck2;
    @Deprecated
    private Boolean deck3;
    @Deprecated
    private Boolean deck4;
    @Deprecated
    private Boolean deck5;
    private UserCardStrengthenInfo strengthenInfo;
    private Date ownTime;// 获得时间
    private Integer getWay;// 获得方式

    @Override
    public String getName() {
        // 旧数据遗留问题
        if (StrUtil.isNull(super.getName())) {
            CfgCardEntity cfgCard = CardTool.getCardById(getBaseId());
            this.setName(cfgCard.getName());
        }
        return super.getName();
    }

    public static UserCard instance(int cardId, int cardLv, int cardHv) {
        UserCard userCard = new UserCard();
        userCard.setLevel(cardLv);
        userCard.setBaseId(cardId);
        userCard.setHierarchy(cardHv);
        return userCard;
    }

    public static UserCard fromCfgCard(long guId, CfgCardEntity cfgCard, WayEnum way) {
        UserCard uCard = new UserCard();
        uCard.setId(ID.INSTANCE.nextId());
        uCard.setGameUserId(guId);
        uCard.setBaseId(cfgCard.getId());
        uCard.setName(cfgCard.getName());
        uCard.setOwnTime(DateUtil.now());
        uCard.setGetWay(way.getValue());
        return uCard;
    }

    public void addExp(int addedExp) {
        this.experience += addedExp;
    }

    public void resetLv() {
        this.experience = 0L;
        this.level = 0;
    }

    public void addLingshi(int addNum) {
        this.lingshi += addNum;
    }

    public void deductLingshi(int deductNum) {
        this.lingshi -= deductNum;
        if (this.lingshi < 0) {
            this.lingshi = 0;
        }
    }

    public void addHierarchy() {
        this.hierarchy += 1;
    }

    public CfgCardEntity gainCard() {
        return CardTool.getCardById(getBaseId());
    }


    /**
     * 获取等级0的技能
     * @return
     */
    public Integer gainSkill0(){
        return gainSkill(CardSkillPosEnum.SKILL_0);
    }

    /**
     * 获取等级5的技能
     * @return
     */
    public Integer gainSkill5() {
        return gainSkill(CardSkillPosEnum.SKILL_5);
    }

    /**
     * 获取等级10的技能
     * @return
     */
    public Integer gainSkill10(){
        return gainSkill(CardSkillPosEnum.SKILL_10);
    }
    /**
     * 获取技能
     * @param skillPos 技能位置枚举
     * @return
     */
   public int gainSkill(CardSkillPosEnum skillPos) {
        if (this.strengthenInfo != null && strengthenInfo.gainCurrentSkill(skillPos) > 0) {
            return strengthenInfo.gainCurrentSkill(skillPos);
        }
        CfgCardEntity cc = gainCard();
        switch (skillPos){
            case SKILL_0:
                return cc.getZeroSkill() == null ? 0 : cc.getZeroSkill();
            case SKILL_5:
                return cc.getFiveSkill() == null ? 0 : cc.getFiveSkill();
            case SKILL_10:
                return cc.getTenSkill() == null ? 0 : cc.getTenSkill();
            default:
                return -1;
        }
    }

    public int gainSymbol(CfgBYPalaceSymbolEntity symbolEntity) {
        if (symbolEntity.getType() == 10) {
            return gainAttackSymbol();
        }
        return gainDefenceSymbol();
    }

    public int gainAttackSymbol() {
        if (this.strengthenInfo != null && this.strengthenInfo.gainAttackSymbol() != null) {
            return this.strengthenInfo.gainAttackSymbol();
        }
        return 0;
    }

    /**
     * 是否是修改的技能
     *
     * @param skillPos
     * @return
     */
    public boolean ifSkillChanged(Integer skillPos) {
        if (strengthenInfo == null) {
            return false;
        }
        if (skillPos == null) {
            strengthenInfo.updateUseSkillScrollTimes();
            return strengthenInfo.gainUseSkillScrollTimes() > 0;
        }
        return strengthenInfo.gainCurrentSkill(CardSkillPosEnum.fromSkillPos(skillPos)) > 0;
    }

    /**
     * 获取攻击符箓的加成值
     *
     * @return
     */
    public int gainAttackSymbolEffectVal() {
        int symbolId = gainAttackSymbol();
        if (symbolId != 0) {
            CfgBYPalaceSymbolEntity entity = BYPalaceTool.getSymbolEntity(symbolId);
            if (entity != null) {
                return entity.getEffect();
            }
        }
        return 0;
    }

    /**
     * 获取防御符箓的加成值
     *
     * @return
     */
    public int gainDefenceSymbolEffectVal() {
        int symbolId = gainDefenceSymbol();
        if (symbolId != 0) {
            CfgBYPalaceSymbolEntity entity = BYPalaceTool.getSymbolEntity(symbolId);
            if (entity != null) {
                return entity.getEffect();
            }
        }
        return 0;
    }

    public int gainDefenceSymbol() {
        if (this.strengthenInfo != null && this.strengthenInfo.gainDefenceSymbol() != null) {
            return this.strengthenInfo.gainDefenceSymbol();
        }
        return 0;
    }

    public int gainAttack() {
        int baseAck = gainCard().getAttack();
        baseAck += gainAttackSymbolEffectVal();
        return (int) (baseAck * (1 + (0.1 + this.hierarchy * 0.025) * this.level));
    }

    public int gainHp() {
        Integer baseHp = gainCard().getHp();
        baseHp += gainDefenceSymbolEffectVal();
        return (int) (baseHp * (1 + (0.1 + this.hierarchy * 0.025) * this.level));
    }

    public boolean ifFullUpdate() {
        return this.level >= CardTool.getCardUpdateData().getCardTopLevel();
    }

    public boolean ifFullHierarchy() {
        return this.hierarchy >= CardTool.getCardUpdateData().getCardTopHierarchy();
    }

    /**
     * 是否还可以使用技能卷轴
     *
     * @return
     */
    public boolean ifAbleUseSkillScroll() {
        if (this.strengthenInfo == null) {
            return true;
        }
        CfgCard cfgCard = CardTool.getConfig();
        return this.strengthenInfo.gainUseSkillScrollTimes() < cfgCard.getSkillScrollUseLimitPerCard();
    }

    /**
     * 该卡牌是否持有该技能
     *
     * @param skill
     * @return
     */
    public boolean ifUsed(int skill) {
        return gainSkill0() == skill || gainSkill5() == skill || gainSkill10() == skill;
    }

    /**
     * 是否拥有复合技能
     *
     * @param skill
     * @return
     */
    public boolean ifOwnCompoundSkills(int skill) {
        for (Integer ownSkill : gainSkills()) {
            if (0 == ownSkill) {
                continue;
            }
            List<Integer> ownSkills = CardSkillTool.getCardSkillOpById(ownSkill).get().getOwnSkills();
            if (ownSkills.contains(skill)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否有使用技能卷轴
     *
     * @return
     */
    public boolean ifUseSkillScroll() {
        if (this.strengthenInfo == null) {
            return false;
        }
        if (strengthenInfo.ifUseSkillScroll()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是不是原始技能
     * @param skillPos
     * @return
     */
    public boolean ifOriginalSkill(int skillPos) {
        if (this.strengthenInfo == null) {
            return true;
        }
        return this.strengthenInfo.gainCurrentSkill(CardSkillPosEnum.fromSkillPos(skillPos)) > 0;
    }

    /**
     * 设置新技能
     *
     * @param skillLevel
     * @param newSkill
     */
    public void updateNewSkill(int skillLevel, int newSkill) {
        if (this.strengthenInfo == null) {
            this.strengthenInfo = new UserCardStrengthenInfo();
        }
        CardSkillPosEnum skillPos = CardSkillPosEnum.fromLevel(skillLevel);
        strengthenInfo.updateCurrentSkill(skillPos,newSkill);
        strengthenInfo.incUseSkillScrollTimes(1);
    }

    public void updateUsedSkillScroll(int skillLevel, int skillScrollId) {
        if (this.strengthenInfo == null) {
            this.strengthenInfo = new UserCardStrengthenInfo();
        }
        CardSkillPosEnum skillPos = CardSkillPosEnum.fromLevel(skillLevel);
        strengthenInfo.addUsingSkillScroll(skillPos.getSkillPos(), skillScrollId);
    }

    public Integer gainUsedSkillScrollId(int skillLevel) {
        if (this.strengthenInfo == null) {
            this.strengthenInfo = new UserCardStrengthenInfo();
        }
        CardSkillPosEnum skillPos = CardSkillPosEnum.fromLevel(skillLevel);
        List<Integer> usingSkillScrolls = this.strengthenInfo.gainUsingSkillScrolls(skillPos.getSkillPos());
        if (ListUtil.isNotEmpty(usingSkillScrolls)) {
            return usingSkillScrolls.get(usingSkillScrolls.size() - 1);
        }
        Integer skill = strengthenInfo.gainCurrentSkill(skillPos);
        if (null == skill || 0 == skill) {
            return 0;
        }
        Integer skillScrollId = TreasureTool.getSkillScrollId(CardTool.getCardById(this.getBaseId()).getType(), skill, getBaseId());
        return skillScrollId;
    }

    /**
     * 重置技能
     */
    public void resetSkill() {
        if (this.strengthenInfo == null) {
            return;
        }
        this.strengthenInfo.resetCurrentSkillGroup();
    }

    /**
     * 重置技能 pos==null时重置所有
     *
     * @param pos [0,5,10]
     */
    public boolean resetSkill(Integer pos) {
        if (pos == null) {
            return false;
        }
        if (this.strengthenInfo == null) {
            return false;
        }
        Integer skillScrollId = gainUsedSkillScrollId(pos);
        if (0 == skillScrollId) {
            return false;
        }
        CfgSkillScrollLimitEntity limitEntity = TreasureTool.getSkillScrollLimitEntity(skillScrollId);
        List<Integer> limitSkills = limitEntity.getLimitSkills();
        // 雷电，圣火等
        if (ListUtil.isNotEmpty(limitSkills)) {
            Integer skill = limitSkills.get(0);
            checkBeforeReset(skill);
            if (skill != null && skill.intValue() == gainCard().getSkill(pos)) {
                skill = 0;
            }
            this.strengthenInfo.updateCurrentSkill(CardSkillPosEnum.fromSkillPos(pos),skill);
            
            this.strengthenInfo.removeLastSkillMap(pos);
            this.strengthenInfo.removeLastUsingSkillScroll(pos);
            this.strengthenInfo.incUseSkillScrollTimes(-1);
            return true;
        }

        // 一个位置打了2个技能
        if (ListUtil.isEmpty(limitSkills) && this.getStrengthenInfo().gainUseSkillScrollTimes() == 2
                && MapUtil.isEmpty(this.strengthenInfo.gainLastSkillMap())) {
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.fromSkillPos(pos),0);

            this.strengthenInfo.removeUsingSkillScrolls(pos);
            this.strengthenInfo.incUseSkillScrollTimes(-2);
            return true;
        }
        //位置为空抛异常，现没对位置为空做处理
        CardSkillPosEnum cardSkillPosEnum = CardSkillPosEnum.fromSkillPos(pos);
        List<Integer> list = this.strengthenInfo.gainLastSkillMap().get(String.valueOf(cardSkillPosEnum.getSkillPos()));
        Integer skill = ListUtil.isEmpty(list) ? null : list.get(list.size() - 1);
        checkBeforeReset(skill);
        strengthenInfo.updateCurrentSkill(cardSkillPosEnum,skill);
        if (skill != null && list != null) {
            list.remove(list.size() - 1);
        }
        this.strengthenInfo.removeLastSkillMap(pos);
        this.strengthenInfo.removeLastUsingSkillScroll(pos);
        this.strengthenInfo.incUseSkillScrollTimes(-1);
        return true;
    }

    /**
     * 重置技能前的检查
     *
     * @param skill
     */
    private void checkBeforeReset(Integer skill) {
        if (gainSkill0().equals(skill)|| gainSkill5().equals(skill)|| gainSkill10().equals(skill)) {
            if (skill != 0) {
                throw new ExceptionForClientTip("card.useSkillScroll.skill.will.repeat");
            }
        }
    }

    /**
     * 设置符箓
     *
     * @param symbolEntity
     * @return
     */
    public void updateSymbol(CfgBYPalaceSymbolEntity symbolEntity) {
        if (this.strengthenInfo == null) {
            this.strengthenInfo = new UserCardStrengthenInfo();
        }
        if (symbolEntity.getType() == 10) {
            this.strengthenInfo.setAttackSymbol(symbolEntity.getId());
        } else {
            this.strengthenInfo.setDefenceSymbol(symbolEntity.getId());
        }

    }

    /**
     * 卸下符箓
     *
     * @param symbolEntity
     */
    public void removeSymbol(CfgBYPalaceSymbolEntity symbolEntity) {
        if (this.strengthenInfo == null) {
            return;
        }
        if (symbolEntity.getType() == 10) {
            this.strengthenInfo.setAttackSymbol(null);
        } else {
            this.strengthenInfo.setDefenceSymbol(null);
        }

    }

    public List<Integer> gainActivedSkills() {
        List<Integer> activedSkill = new ArrayList<>();
        if (gainSkill0() > 0) {
            activedSkill.add(gainSkill0());
        }
        if (this.level >= 5 && gainSkill5() > 0) {
            activedSkill.add(gainSkill5());
        }
        if (this.level >= 10 && gainSkill10() > 0) {
            activedSkill.add(gainSkill10());
        }
        return activedSkill;
    }

    /**
     * 获取卡牌技能
     *
     * @return [技能1, 0, 技能2]
     */
    public List<Integer> gainSkills() {
        List<Integer> skills = new ArrayList<>();
        skills.add(gainSkill0());
        skills.add(gainSkill5());
        skills.add(gainSkill10());
        return skills;
    }
    public List<Integer> gainSkillsByLv() {
        List<Integer> skills = new ArrayList<>();
        skills.add(gainSkill0());
        if (level >= 5){
            skills.add(gainSkill5());
        } else {
            skills.add(0);
        }
        if (level >= 10){
            skills.add(gainSkill10());
        } else {
            skills.add(0);
        }
        return skills;
    }

    /**
     * 是否拥有该技能ID
     *
     * @param skillId
     * @return
     */
    public boolean ifOwnSkillId(int skillId) {
        if (gainSkill0() == skillId) {
            return true;
        }
        if (gainSkill5() == skillId) {
            return true;
        }
        if (gainSkill10() == skillId) {
            return true;
        }
        return false;
    }

    @Override
    public UserDataType gainResType() {
        return DATA_TYPE;
    }

    /**
     * 卡牌加强数据
     *
     * @author suhq
     * @date 2019-09-30 15:59:27
     */
    @Data
    public static class UserCardStrengthenInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        @Deprecated
        private Integer skill0;
        @Deprecated
        private Integer skill5;
        @Deprecated
        private Integer skill10;
        // key是技能等级，0，5,10   val是对应的位置的上个技能id
        @Deprecated
        private Map<String, List<Integer>> lastSkillMap;
        /** 技能位上装配的技能卷轴 */
        @Deprecated
        private Map<String, List<Integer>> usingSkillScrolls;
        @Deprecated
        private Map<String, Integer> usedSkillScrolls;
        @Deprecated
        private Integer useSkillScrollTimes;

        /** 玩家卡牌技能组 */
        private Map<String, SkillGroup> skillGroups = new HashMap<>();
        /** 现在技能组标识 */
        private String currentSkillGroup = CardConstant.SKILL_GROUP_1;

        private Integer attackSymbol;// 攻击符箓
        private Integer defenceSymbol;// 防御符箓


        public Map<String, SkillGroup> initSkillGroup(String skillGroup) {
            skillGroups.put(skillGroup, new SkillGroup());
            return skillGroups;
        }


        /**
         * 是否激活技能组
         *
         * @param skillGroupKey 技能组key
         * @return
         */
        public boolean ifActivationSkillGroup(String skillGroupKey) {
            Set<String> skillGroupKeys = skillGroups.keySet();
            for (String groupKey : skillGroupKeys) {
                if (groupKey.equals(skillGroupKey)) {
                    return true;
                }
            }
            return false;
        }



        public Map<String, List<Integer>> gainLastSkillMap() {
            SkillGroup skillGroup = gainSkillGroup();
            Map<String, List<Integer>> skillMap = skillGroup.gainLastSkills();
            return skillMap;
        }

        /**
         * 获取当前技能组的技能
         *
         * @param skillPos 技能等级位置枚举
         * @return
         */
        public Integer gainCurrentSkill(CardSkillPosEnum skillPos) {
            SkillGroup skillGroup = gainSkillGroup();
            return skillGroup.gainSkill(skillPos);
        }

        /**
         * 是否使用技能卷轴
         *
         * @return
         */
        public Boolean ifUseSkillScroll() {
            SkillGroup skillGroup = gainSkillGroup();
            return skillGroup.ifUseSkillScroll();
        }

        /**
         * 更新当前技能组的技能
         *
         * @param skillPos 技能等级位置枚举
         * @param skill    技能
         */
        public void updateCurrentSkill(CardSkillPosEnum skillPos, Integer skill) {
            SkillGroup skillGroup = gainSkillGroup();
            skillGroup.updateSkill(skillPos, skill);
        }

        /**
         * 重置当前技能组的技能
         */
        public void resetCurrentSkillGroup() {
            SkillGroup skillGroup = gainSkillGroup();
            skillGroup.resetSkill();
        }

        /**
         * 获取当前技能组
         *
         * @return
         */
        public SkillGroup gainSkillGroup() {
            //通过技能组key获取技能组信息
            SkillGroup skillGroup = skillGroups.get(currentSkillGroup);
            if (null != skillGroup) {
                return skillGroup;
            }

            SkillGroup uGroup = new SkillGroup();
            skillGroups.put(currentSkillGroup, uGroup);
            return uGroup;

        }

        /**
         * 获得使用技能卷轴
         *
         * @param skillLevel 技能等级 0,5,10
         * @return
         */
        public List<Integer> gainUsingSkillScrolls(Integer skillLevel) {
            SkillGroup skillGroup = gainSkillGroup();
            return skillGroup.gainUsingSkillScrolls(skillLevel);
        }

        /**
         * 添加使用的技能卷轴记录
         *
         * @param skillLevel 技能等级
         * @param skillScrollId 技能卷轴id
         */
        public void addUsingSkillScroll(Integer skillLevel, int skillScrollId) {
            SkillGroup skillGroup = gainSkillGroup();
            skillGroup.addUsingSkillScroll(skillLevel, skillScrollId);
        }

        /**
         * 移除对应位置使用的技能卷轴的最近一条记录
         *
         * @param skillLevel 技能等级
         */
        public void removeLastUsingSkillScroll(Integer skillLevel) {
            SkillGroup skillGroup = gainSkillGroup();
            skillGroup.removeLastUsingSkillScroll(skillLevel);
        }

        /**
         * 移除使用中的技能卷轴
         *
         * @param skillLevel 技能等级
         */
        public void removeUsingSkillScrolls(Integer skillLevel) {
            SkillGroup skillGroup = gainSkillGroup();
            skillGroup.removeUsingSkillScrolls(skillLevel);
        }

        /**
         * 获得某个位置装配的技能
         *
         * @param pos 位置（相当于skillLevel）
         * @return
         */
        public List<Integer> gainPutedOnSkills(int pos) {
            SkillGroup skillGroup = gainSkillGroup();
            return skillGroup.gainPutedOnSkills(pos);
        }

        /**
         * 更新 lastSkillMap
         *
         * @param pos 位置（相当于skillLevel）
         * @param skillId 技能id
         */
        public void updateLastSkillMap(int pos, int skillId) {
            SkillGroup skillGroup = gainSkillGroup();
            skillGroup.updateLastSkillMap(pos, skillId);
        }

        /**
         * 移除指定位置的技能
         * @param pos 位置（相当于skillLevel）
         */
        public void removeLastSkillMap(int pos) {
            SkillGroup skillGroup = gainSkillGroup();
            skillGroup.removeLastSkillMap(pos);
        }

        /**
         * 获取技能卷轴使用的次数
         *
         * @return
         */
        public Integer gainUseSkillScrollTimes() {
            SkillGroup skillGroup = gainSkillGroup();
            return skillGroup.gainUseSkillScrollTimes();
        }
        public Integer gainSkill0(){
            return gainCurrentSkill(CardSkillPosEnum.SKILL_0);
        }
        public Integer gainSkill5(){
            return gainCurrentSkill(CardSkillPosEnum.SKILL_5);
        }
        public Integer gainSkill10(){
            return gainCurrentSkill(CardSkillPosEnum.SKILL_10);
        }
        /**
         * 获取攻击符箓
         * @return
         */
        public Integer gainAttackSymbol() {
            return attackSymbol == null ? 0 : attackSymbol;
        }

        /**
         * 获取防御符箓
         * @return
         */
        public Integer gainDefenceSymbol() {
            return defenceSymbol == null ? 0 : defenceSymbol;
        }



        /**
         * 更新技能卷轴使用的次数
         */
        public void updateUseSkillScrollTimes() {
            SkillGroup skillGroup = gainSkillGroup();
            skillGroup.updateUseSkillScrollTimes();
        }

        /**
         * 设置完技能后调用
         *
         * @param num
         */
        public void incUseSkillScrollTimes(int num) {
            SkillGroup skillGroup = gainSkillGroup();
            skillGroup.incUseSkillScrollTimes(num);
        }

        /**
         * 清除所有技能卷轴
         */
        public void clearSkillScroll() {
            SkillGroup skillGroup = gainSkillGroup();
            skillGroup.clearSkillScroll();
        }
    }

    @Data
    public static class SkillGroup implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 0级技能 */
        private Integer s0;
        /** 5级技能 */
        private Integer s5;
        /** 10级技能 */
        private Integer s10;
        // key是技能等级，0，5,10   val是对应的位置的上个技能id
        private Map<String, List<Integer>> lastSkills;

        /**
         * 技能位上装配的技能卷轴
         */
        private Map<String, List<Integer>> usingSkillScrolls;

        private Integer useSkillScrollTimes;

        public Map<String, List<Integer>> gainLastSkills() {
            if (null != lastSkills) {
                return lastSkills;
            }
            return new HashMap<>();
        }

        public Map<String, List<Integer>> gainUsingSkillScrolls() {
            if (null != usingSkillScrolls) {
                return usingSkillScrolls;
            }
            return new HashMap<>();
        }

        /**
         * 获取技能卷轴使用的次数
         *
         * @return
         */
        public Integer gainUseSkillScrollTimes() {
            if (useSkillScrollTimes == null) {
                updateUseSkillScrollTimes();
            }
            return useSkillScrollTimes;
        }
        /**
         * 获得使用技能卷轴
         *
         * @param skillLevel 技能等级 0,5,10
         * @return
         */
        public List<Integer> gainUsingSkillScrolls(Integer skillLevel) {
            String key = skillLevel + "";
            return gainUsingSkillScrolls().get(key);
        }



        /**
         * 是否使用技能卷轴
         * @return
         */
        public Boolean ifUseSkillScroll(){
            return gainSkill(CardSkillPosEnum.SKILL_0) > 0 || gainSkill(CardSkillPosEnum.SKILL_5) > 0 || gainSkill(CardSkillPosEnum.SKILL_10) > 0;
        }

        /**
         * 添加使用的技能卷轴记录
         *
         * @param skillLevel 技能等级
         * @param skillScrollId 技能卷轴id
         */
        public void addUsingSkillScroll(Integer skillLevel, int skillScrollId) {
            String key = skillLevel + "";
            List<Integer> skillScrolls = gainUsingSkillScrolls(skillLevel);
            if (null == skillScrolls) {
                skillScrolls = new ArrayList<>();
                if (null == usingSkillScrolls) {
                    usingSkillScrolls = new HashMap<>();
                }
                usingSkillScrolls.put(key, skillScrolls);
            }
            skillScrolls.add(skillScrollId);
        }



        /**
         * 获得某个位置装配的技能
         *
         * @param pos 位置
         * @return
         */
        public List<Integer> gainPutedOnSkills(int pos) {
            List<Integer> list = gainLastSkills().get(String.valueOf(pos));
            return list;
        }
        /**
         * 移除对应位置使用的技能卷轴的最近一条记录
         *
         * @param skillLevel 技能等级
         */
        public void removeLastUsingSkillScroll(Integer skillLevel) {
            List<Integer> skillScrolls = gainUsingSkillScrolls(skillLevel);
            if (ListUtil.isEmpty(skillScrolls)) {
                return;
            }
            skillScrolls.remove(skillScrolls.size() - 1);
        }

        /**
         * 移除使用中的技能卷轴
         *
         * @param skillLevel 技能等级
         */
        public void removeUsingSkillScrolls(Integer skillLevel) {
            List<Integer> skillScrolls = gainUsingSkillScrolls(skillLevel);
            if (ListUtil.isEmpty(skillScrolls)) {
                return;
            }
            skillScrolls.clear();
        }

        /**
         * 更新 lastSkillMap
         * @param pos 位置（相当于skillLevel）
         * @param skillId 技能id
         */
        public void updateLastSkillMap(int pos, int skillId) {
            List<Integer> list = gainPutedOnSkills(pos);
            if (ListUtil.isEmpty(list)) {
                list = new ArrayList<>();
            }
            list.add(skillId);
            if (null == lastSkills) {
                lastSkills = new HashMap<>();
            }
            lastSkills.put(String.valueOf(pos), list);
        }

        /**
         * 移除指定位置的技能
         * @param pos 位置（相当于skillLevel）
         */
        public void removeLastSkillMap(int pos) {
            List<Integer> list = gainPutedOnSkills(pos);
            if (ListUtil.isEmpty(list)) {
                return;
            }
            list.remove(list.size() - 1);
            lastSkills.put(String.valueOf(pos), list);
        }

        /**
         * 重置技能
         */
        public void resetSkill() {
            s0 = null;
            s5 = null;
            s10 = null;
            useSkillScrollTimes = null;
            usingSkillScrolls = null;
            lastSkills = null;
        }

        /**
         * 更新技能
         * @param skillPos 技能等级位置枚举
         * @param skill 技能
         */
        public void updateSkill(CardSkillPosEnum skillPos, Integer skill){
            switch (skillPos){
                case SKILL_0:
                    s0 = skill;
                    break;
                case SKILL_5:
                    s5 = skill;
                    break;
                case SKILL_10:
                    s10 = skill;
                    break;
                default:
                    break;
            }
        }

        /**
         * 获取当前等级的技能
         * @param skillPos 技能等级位置枚举
         * @return
         */
        public Integer gainSkill(CardSkillPosEnum skillPos){
            int result = -1;
            //如果不是默认技能就返回该技能
            switch (skillPos){
                case SKILL_0:
                    return null == s0 ? 0 : s0;
                case SKILL_5:
                    return null == s5 ? 0 : s5;
                case SKILL_10:
                    return null == s10 ? 0 : s10;
                default:
                    break;
            }
            return result;
        }



        /**
         * 更新技能卷轴使用的次数
         */
        public void updateUseSkillScrollTimes() {
            if (useSkillScrollTimes == null || useSkillScrollTimes <= 0) {
                useSkillScrollTimes = 0;
                if (gainSkill(CardSkillPosEnum.SKILL_0) > 0) {
                    useSkillScrollTimes++;
                }
                if (gainSkill(CardSkillPosEnum.SKILL_5) > 0) {
                    useSkillScrollTimes++;
                }
                if (gainSkill(CardSkillPosEnum.SKILL_10) > 0) {
                    useSkillScrollTimes++;
                }
            }
        }

        /**
         * 设置完技能后调用
         *
         * @param num
         */
        public void incUseSkillScrollTimes(int num) {
            if (useSkillScrollTimes == null || useSkillScrollTimes == 0) {
                //兼容旧号  因为最早以前没有useSkillScrollTimes参数，所以改技能时需要更新，
                //因为这是技能更新后调用的  所有当useSkillScrollTimes 更新时已经是计算使用卷轴后的结果
                updateUseSkillScrollTimes();
            } else {
                useSkillScrollTimes += num;
                useSkillScrollTimes = useSkillScrollTimes < 0 ? 0 : useSkillScrollTimes;
            }
        }

        /**
         * 清除所有技能卷轴
         */
        public void clearSkillScroll() {
            s0 = null;
            s5 = null;
            s10 = null;
            useSkillScrollTimes = null;
        }
    }

}
