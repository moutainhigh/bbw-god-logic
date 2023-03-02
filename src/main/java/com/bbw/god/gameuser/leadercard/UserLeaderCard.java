package com.bbw.god.gameuser.leadercard;

import com.bbw.common.ID;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquimentService;
import lombok.Data;

import java.util.*;

/**
 * @author：lwb
 * @date: 2021/3/22 14:57
 * @version: 1.0
 */
@Data
public class UserLeaderCard extends UserSingleObj {
    /** 属性 */
    private Integer property;
    private Integer sex = 0;
    private Integer lv = 0;
    private Integer hv = 0;
    private Integer star = 1;
    @Deprecated
    private int[][] skills = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
    private Integer addAtkPoint = 0;//攻击加点
    private Integer addHpPoint = 0;//防御加点
    private Integer hvExtraAddition = 0;//阶级额外加成
    private Long exp = 0L;//经验
    private List<Integer> ownProperty = new ArrayList<>();
    /* 主角卡技能组 */
    private Map<String,UserLeaderCardSkills> propertySkills = new HashMap<>();
    /** 当前使用的时装 */
    private Integer fashion = TreasureEnum.FASHION_FaSFS.getValue();

    private static final UserLeaderEquimentService equimentService = SpringContextUtil.getBean(UserLeaderEquimentService.class);

    /**
     * 初始化实例
     *
     * @return
     */
    public static UserLeaderCard getInstance(GameUser user) {
        UserLeaderCard card = new UserLeaderCard();
        CfgLeaderCard cfgLeaderCard = LeaderCardTool.getCfgLeaderCard();
        card.setProperty(user.getRoleInfo().getCountry());
        card.setHvExtraAddition(0);
        card.instanceSkillGroups(cfgLeaderCard.getInitSkill0());
        card.setGameUserId(user.getId());
        card.setId(ID.INSTANCE.nextId());
        card.setSex(user.getRoleInfo().getSex());
        card.getOwnProperty().add(card.getProperty());
        return card;
    }

    public int getBaseId(){
        return LeaderCardTool.getLeaderCardId();
    }
    /**
     * 获取当前总攻击力
     * 只计算等级 星级 阶级 和 加点和装备
     * @return
     */
    public int settleTotalAtkWithEquip(){
        return settleTotalAtkWithoutEquip()+equimentService.getAdditions(getGameUserId()).getAttack();
    }

    /**
     * 获取当前的总防御力
     * 只计算等级 星级 阶级 和 加点 和装备
     * @return
     */
    public int settleTotalHpWithEquip(){
        return settleTotalHpWithoutEquip()+equimentService.getAdditions(getGameUserId()).getDefence();
    }

    /**
     * 获取当前总攻击力
     * 只计算等级 星级 阶级 和 加点和装备
     * @param targetHv 指定阶级
     * @return
     */
    public int settleTotalAtkWithEquipHv(int targetHv){
        return settleTotalAtkWithoutEquip(targetHv)+equimentService.getAdditions(getGameUserId()).getAttack();
    }

    /**
     * 获取当前的总防御力
     * 只计算等级 星级 阶级 和 加点 和装备
     * @param targetHv 指定阶级
     * @return
     */
    public int settleTotalHpWithEquipHv(int targetHv){
        return settleTotalHpWithoutEquip(targetHv)+equimentService.getAdditions(getGameUserId()).getDefence();
    }

    /**
     * 获取当前总攻击力: 未计算 装备加成的
     * 只计算 星级 阶级 和 加点
     * @return
     */
    public int settleTotalAtkWithoutEquip(){
        return settleTotalAtkWithoutEquip(hv);
    }
    public int settleTotalAtkWithoutEquip(int targetHv){
        int base=settleBaseAtkHpWithStar()+settlePointAddVal(targetHv)*getAddAtkPoint();
        return base;
    }
    /**
     * 获取当前的总防御力: 未计算 装备加成的
     * 只计算 星级 阶级 和 加点
     * @return
     */
    public int settleTotalHpWithoutEquip(){
        return settleTotalHpWithoutEquip(hv);
    }
    public int settleTotalHpWithoutEquip(int targetHv){
        int base=settleBaseAtkHpWithStar()+settlePointAddVal(targetHv)*getAddHpPoint();
        return base;
    }

    /**
     * 计算剩余可用的点数
     * @return
     */
    public int settleFreePoint(){
        return Math.max(0,lv*2-addAtkPoint-addHpPoint);
    }

    /**
     * 重置已用点数
     */
    public void resetPoint(){
        this.addAtkPoint=0;
        this.addHpPoint=0;
    }

    /**
     * 是否需要突破
     * @return
     */
    public boolean ifNeedBreach(){
        CfgLeaderCard.UpHvCondition condition = LeaderCardTool.getCurrentConditionByHv(hv);
        return this.hvExtraAddition>=condition.getTopLimit();
    }

    /**
     * 获取
     * @return
     */
    public int settleHvTotalAddition(){
        return settleHvTotalAddition(hv);
    }
    public int settleHvTotalAddition(int targetHv){
        CfgLeaderCard cfgLeaderCard = LeaderCardTool.getCfgLeaderCard();
        if (targetHv==hv){
            return this.hvExtraAddition+cfgLeaderCard.getInitBaseHvAddition();
        }
        CfgLeaderCard.UpHvCondition condition = LeaderCardTool.getCurrentConditionByHv(targetHv);
        return condition.getAdd()+cfgLeaderCard.getInitBaseHvAddition();
    }
    /**
     * 获取当前基础攻防
     * @return
     */
    public int settleBaseAtkHpWithStar(){
        switch (star){
            case 2:
                return 160;
            case 3:
                return 256;
            case 4:
                return 410;
            case 5:
                return 656;
            default:
                return 100;
        }
    }

    /**
     * 是否满级
     * @return
     */
    public boolean ifFullLv(){
        return LeaderCardTool.getCfgLeaderCard().getTopLimitLv()<=lv;
    }

    /**
     * 获取当前属性技能
     * @return
     */
    public int[]  currentSkills(){
        UserLeaderCardSkills userLeaderCardSkills = gainSkills(property);
        //未迁移数据时，获取原先的技能数据
        if (userLeaderCardSkills == null){
            return skills[property/10-1];
        }
        Integer usingIndex = userLeaderCardSkills.getUsingIndex();
        int[] skills = userLeaderCardSkills.getSkillsGroupInfo().get(usingIndex);
        return skills;
    }

    /**
     * 根据属性获取所有技能
     * @param property
     * @return
     */
    public UserLeaderCardSkills gainSkills(int property){
        UserLeaderCardSkills userLeaderCardSkills = propertySkills.get(property+"");
        if (null == userLeaderCardSkills){
            return null;
        }
        return userLeaderCardSkills;
    }

    /**
     * 初始化技能组
     * @param initSkill0
     */
    public void instanceSkillGroups(int initSkill0){
        for (int property = 10; property <= 50; property += 10){
            int[] initialSkills0 = {initSkill0, 0, 0};
            int[] initialSkills1 = {0, 0, 0};
            UserLeaderCardSkills userLeaderCardSkills = new UserLeaderCardSkills();
            userLeaderCardSkills.getSkillsGroupInfo().add(initialSkills0);
            userLeaderCardSkills.getSkillsGroupInfo().add(initialSkills1);
            propertySkills.put(property+"", userLeaderCardSkills);
        }
        setPropertySkills(propertySkills);
    }

    /**
     * 计算 当前加点可获得的加值
     * 公式：基础攻防 * 阶级加成比
     * @return
     */
    public int settlePointAddVal(){
        return settlePointAddVal(hv);
    }

    public int settlePointAddVal(int targetHv){
        Double val = settleBaseAtkHpWithStar() * settleHvTotalAddition(targetHv)/10000.0;
        return val.intValue();
    }

    /**
     * 替换对应位置技能
     * @param skillId
     * @param pos   0~2  依左到右
     */
    public void replaceSkill(int skillId,int pos){
        UserLeaderCardSkills userLeaderCardSkills = gainSkills(property);
        Integer usingIndex = userLeaderCardSkills.getUsingIndex();
        int[] skills = userLeaderCardSkills.getSkillsGroupInfo().get(usingIndex);
        skills[pos]=skillId;
    }

    /**
     * 检查当前属性下是否装配了该技能
     * @param skillId
     */
    public boolean checkCurrentUseSkill(int skillId){
        UserLeaderCardSkills userLeaderCardSkills = gainSkills(property);
        Integer usingIndex = userLeaderCardSkills.getUsingIndex();
        int[] skills = userLeaderCardSkills.getSkillsGroupInfo().get(usingIndex);
        for (int skillIdItem : skills) {
            if (skillIdItem==skillId){
                return true;
            }
        }
        return false;
    }

    /**
     * 激活当前属性新的技能组
     */
    public void activeGroupSkills(){
        int[] initialSkills = {0, 0, 0};
        UserLeaderCardSkills userLeaderCardSkills = gainSkills(property);
        userLeaderCardSkills.getSkillsGroupInfo().add(initialSkills);
    }

    /**
     * 获取当前属性所使用的技能组编号
     * @return
     */
    public int gainUsingIndex(){
        int usingIndex = 0;
        if ( !propertySkills.isEmpty() || gainSkills(property) != null){
            return gainSkills(property).getUsingIndex();
        }
        return usingIndex;
    }

    /**
     * 旧格式技能数据迁移
     */
    public void migrateLeaderSkills(){
        for (int propertyIndex = 10; propertyIndex <= 50; propertyIndex += 10){
            int[] skills = getSkills()[propertyIndex / 10 - 1];
            int[] initialSkills = {0, 0, 0};
            UserLeaderCardSkills userLeaderCardSkills = new UserLeaderCardSkills();
            userLeaderCardSkills.getSkillsGroupInfo().add(skills);
            userLeaderCardSkills.getSkillsGroupInfo().add(initialSkills);
            propertySkills.put(propertyIndex+"", userLeaderCardSkills);
        }
    }
    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_LEADER_CARD;
    }
}
