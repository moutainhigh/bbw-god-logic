package com.bbw.god.gameuser.businessgang.luckybeast;

import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 玩家招财兽信息
 *
 * @author: huanghb
 * @date: 2022/1/18 8:42
 */
@Data
public class UserLuckyBeast extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 招财兽Id */
    private Integer luckyBeastId;
    /** 招财兽等级 */
    private Integer luckyBeastLevel;
    /** 血量 */
    private Integer hp = 0;
    /** 攻击值 */
    private Integer attackPower = 0;
    /** 玩家卡组Id */
    private Integer cardId = 0;
    /** 已使用卡牌id */
    private List<Integer> excludedCards = new ArrayList<>();
    /** 总攻击（招财）次数 */
    private Integer totalAttackTimes;
    /** 剩余攻击（招财）次数 */
    private Integer remainfreeAttackTimes;
    /** 剩余购买攻击（招财）次数 */
    private Integer remainBuyAttacksTimes;
    /** 重置招财兽次数 */
    private Integer remainResetTimes;
    /** 招财兽加载的技能id */
    private List<Integer> skillIds;
    /** 技能加成 */
    private Integer[] skillBuffs = new Integer[]{0, 0, 0};
    /** 最后一次攻击奖励 */
    private List<Award> addAttactAwardRecords = new ArrayList<>();
    /** 记录生成时间 */
    private Date dateTime = new Date();
    /** 位置 */
    private Integer position;

    /**
     * 招财兽初始化
     *
     * @param luckyBeastInfo
     * @return
     */
    protected static UserLuckyBeast instance(long uid, CfgLuckyBeast.LuckyBeastInfo luckyBeastInfo, Integer position) {
        UserLuckyBeast userLuckyBeast = new UserLuckyBeast();
        userLuckyBeast.setId(ID.INSTANCE.nextId());
        userLuckyBeast.setGameUserId(uid);
        userLuckyBeast.setLuckyBeastId(luckyBeastInfo.getId());
        userLuckyBeast.setLuckyBeastLevel(luckyBeastInfo.getLevel());
        userLuckyBeast.setAttackPower(luckyBeastInfo.getAttackPower());
        CfgLuckyBeast cfgLuckyBeast = LuckyBeastTool.getLuckyBeastCfg();
        userLuckyBeast.setTotalAttackTimes(cfgLuckyBeast.getFreeAttackTimes());
        userLuckyBeast.setRemainfreeAttackTimes(cfgLuckyBeast.getFreeAttackTimes());
        userLuckyBeast.setRemainBuyAttacksTimes(cfgLuckyBeast.getBuyAttackTimesLimit());
        userLuckyBeast.setRemainResetTimes(cfgLuckyBeast.getResetTimesLimit());
        userLuckyBeast.setPosition(position);
        //随机生成招财兽技能
        List<Integer> exclude = new ArrayList<>();
        Integer skillId = PowerRandom.getRandomFromList(luckyBeastInfo.getFirstSkillPool());
        exclude.add(skillId);
        skillId = PowerRandom.getRandomFromList(luckyBeastInfo.getSecondSkillPool(), exclude);
        exclude.add(skillId);
        skillId = PowerRandom.getRandomFromList(luckyBeastInfo.getFirstSkillPool(), exclude);
        exclude.add(skillId);
        userLuckyBeast.setSkillIds(exclude);
        return userLuckyBeast;
    }

    /**
     * 获得奖励规则
     * 根据激活的技能buff获取对应的道具出产概率
     * 第一位代表铜钱是否加成，第二位代表元宝是否加成，第三位代表铜币是否加成，只产出一种
     *
     * @return
     */
    protected String gainAwardTypeRule() {
        return this.skillBuffs[0].toString() + this.skillBuffs[1] + this.skillBuffs[2];
    }

    /**
     * 是否技能加成
     *
     * @return
     */
    protected Integer ifSkillBuff() {
        for (int i = 0; i < this.skillBuffs.length; i++) {
            if (this.skillBuffs[i] > 0) {
                return LuckyBeastSkillBuffStatusEnum.SKILL_Buff.getValue();
            }
        }
        return LuckyBeastSkillBuffStatusEnum.NO_SKILL_Buff.getValue();
    }

    /**
     * 购买攻击次数
     *
     * @param num
     */
    protected void buyAttackTimes(Integer num) {
        this.remainfreeAttackTimes += num;
        this.remainBuyAttacksTimes -= num;
        this.totalAttackTimes += num;

    }

    /**
     * 扣除重置次数
     */
    protected void resetLuckyBeast(CfgLuckyBeast.LuckyBeastInfo luckyBeastInfo, Integer position) {
        this.luckyBeastId = luckyBeastInfo.getId();
        this.cardId = 0;
        this.hp = 0;
        this.excludedCards = new ArrayList<>();
        this.luckyBeastLevel = luckyBeastInfo.getLevel();
        this.attackPower = luckyBeastInfo.getAttackPower();
        CfgLuckyBeast cfgLuckyBeast = LuckyBeastTool.getLuckyBeastCfg();
        this.totalAttackTimes = cfgLuckyBeast.getFreeAttackTimes();
        this.remainfreeAttackTimes = cfgLuckyBeast.getFreeAttackTimes();
        this.remainBuyAttacksTimes = cfgLuckyBeast.getBuyAttackTimesLimit();
        this.remainResetTimes -= 1;
        this.addAttactAwardRecords = new ArrayList<>();
        //随机生成招财兽技能
        List<Integer> exclude = new ArrayList<>();
        Integer skillId = PowerRandom.getRandomFromList(luckyBeastInfo.getFirstSkillPool());
        exclude.add(skillId);
        skillId = PowerRandom.getRandomFromList(luckyBeastInfo.getSecondSkillPool(), exclude);
        exclude.add(skillId);
        skillId = PowerRandom.getRandomFromList(luckyBeastInfo.getFirstSkillPool(), exclude);
        exclude.add(skillId);
        this.setSkillIds(exclude);
        this.position = position;
    }

    /**
     * 更新技能加成
     *
     * @param skillIds
     */
    protected void updateSkillBuff(List<Integer> skillIds) {

        for (int i = 0; i < this.skillIds.size(); i++) {
            if (ListUtil.isNotEmpty(skillIds) && skillIds.contains(this.skillIds.get(i))) {
                this.skillBuffs[i] = 1;
            }
            this.skillBuffs[i] = 0;
        }
    }

    /**
     * 是否可以改变卡牌
     *
     * @return
     */
    protected boolean ifCanChangeCard(Integer luckyBeastCardId) {
        if (this.getHp() > 0 && this.cardId != 0) {
            return false;
        }
        if (ListUtil.isNotEmpty(this.excludedCards) && this.excludedCards.contains(luckyBeastCardId)) {
            return false;
        }
        return true;
    }

    /**
     * 扣除卡牌血量
     *
     * @param attackValue
     */
    protected void deductHp(Integer attackValue) {
        if (this.cardId == 0 && this.hp <= 0) {
            return;
        }
        this.hp -= attackValue;
        if (this.hp > 0) {
            return;
        }
        this.excludedCards.add(this.cardId);
        this.cardId = 0;
        this.skillBuffs = new Integer[]{0, 0, 0};

    }

    /**
     * 扣除剩余攻击次数
     */
    protected void deductRemainfreeAttackTimes() {
        this.remainfreeAttackTimes -= 1;
    }

    /**
     * 添加攻击奖励任务
     */
    protected void addAttactAwardRecords(List<Award> awards) {
        if (this.addAttactAwardRecords.size() == 0) {
            this.addAttactAwardRecords.addAll(awards);
            return;
        }
        Award award = this.addAttactAwardRecords.stream().filter(tmp -> tmp.getItem() == awards.get(0).getItem()).findFirst().orElse(null);
        if (null == award) {
            this.addAttactAwardRecords.addAll(awards);
            return;
        }
        for (int i = 0; i < this.addAttactAwardRecords.size(); i++) {
            boolean isSameAwardType = addAttactAwardRecords.get(i).getItem() == awards.get(0).getItem();
            if (!isSameAwardType) {
                continue;
            }
            Integer awardNum = this.addAttactAwardRecords.get(i).getNum() + awards.get(0).getNum();
            this.addAttactAwardRecords.get(i).setNum(awardNum);
        }
    }

    /**
     * 生成本次招财兽奖励规则
     * 第一位代表luckyBeastLevel（招财兽等级）;第二位代表奖励类别；第三位代表是否加成0否1是
     *
     * @param awardType
     * @return
     */
    protected String luckyBeastAwardRule(String awardType) {
        return this.luckyBeastLevel + awardType + this.ifSkillBuff();
    }

    /**
     * 重置招财兽位置
     *
     * @param position
     */
    protected void resetLuckyBeastPosition(Integer position) {
        this.position = position;
    }

    /**
     * 玩家资源类型
     *
     * @return
     */
    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_LUCKY_BEAST;
    }
}
