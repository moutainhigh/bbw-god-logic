package com.bbw.god.game.zxz.cfg.award;

import com.bbw.common.CloneUtil;
import com.bbw.common.PowerRandom;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.random.box.BoxService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 诛仙阵奖励工具
 * @author: hzf
 * @create: 2022-09-22 13:56
 **/
public class ZxzAwardTool {

    private static BoxService boxService = SpringContextUtil.getBean(BoxService.class);

    /**
     * 获取配置类
     *
     * @return
     */
    public static CfgZxzAwardEntity getCfg() {
        return CloneUtil.clone(Cfg.I.getUniqueConfig(CfgZxzAwardEntity.class));
    }

    /**
     * 获取奖励掉落数量规则
     */
    public static List<CfgQuantityRule> getQuantityRules(){
        return getCfg().getQuantityRules();
    }

    /**
     * 根据野怪种类获取对应的掉落数量
     * @param defenderKind
     * @return
     */
    public static CfgQuantityRule getQuantityNum(Integer defenderKind){
        return getQuantityRules().stream()
                .filter(rule -> rule.getDefenderKind().equals(defenderKind))
                .findFirst().orElse(null);
    }

    /**
     * 奖励物品掉落规则
     * @return
     */
    public static List<CfgWinAwardRule> getWinAwardRules(){
        return getCfg().getWinAwardRuleRules();
    }

    /**
     * 全通奖励
     * @return
     */
    public static List<CfgAllPassAwardRule> getAllPassAwardRules(){
        return getCfg().getAllPassAwardRules();
    }

    /**
     * 根据难度类型种类获取对应的掉落奖励
     * @param difficulty
     * @param defenderKind
     * @return
     */
    public static CfgWinAwardRule getWinAwardRule(Integer difficulty, Integer defenderKind){
        return getWinAwardRules().stream()
                .filter(rule -> rule.getDifficulty().equals(difficulty) && rule.getDefenderKind().equals(defenderKind))
                .findFirst().orElse(null);
    }

    /**
     * 宝箱奖励倍数的随机规则（每个道具单独计算倍数）
     * @return
     */
    public static List<CfgBoxMultipleRule> getBoxMultipleRules(){
        return getCfg().getBoxMultipleRules();
    }

    /**
     * 宝箱奖励规则
     * @return
     */
    public static List<CfgBoxAwardRule> getBoxAwardRules(){
        return getCfg().getBoxAwardRules();
    }

    /**
     * 获取随机本源
     * @return
     */
    public static List<Award> getRandomOrigin(){
        return getCfg().getRandomOrigin();
    }

    /**
     * 首次全通获得奖励
     * @return
     */
    public static List<Award> getFirstClearanceAward(){
        return getCfg().getFirstClearanceAward();
    }

    /**
     * 根据种类，区域等级 随机出相对的数量
     * @param defenderKind
     * @param regionLv
     * @return
     */
    public static Integer getRandomDropNum(Integer defenderKind, Integer regionLv) {
        Integer num = 1;
        //根据野怪获取对应的数量
        CfgQuantityRule cfgQuantityRule = getQuantityNum(defenderKind);
        //计算添加的概率
        int addProbability = (regionLv / cfgQuantityRule.getAddRegionLv()) * cfgQuantityRule.getAddProbability() ;
        //获取掉落数量奖池
        List<CfgQuantityRule.QuantityRuleAwardNumPool> awardNumPool = cfgQuantityRule.getAwardNumPool();

        //先从好的开始随机中就返回
        for (CfgQuantityRule.QuantityRuleAwardNumPool numPool : awardNumPool) {
            int probability = numPool.getProbability() + addProbability;
            if (numPool.getProbability() < 0) {
                continue;
            }
            if (PowerRandom.hitProbability(probability)){
                num = numPool.getNum();
                //中：就退出循环
                break;
            }
        }

        return num;
    }

    /**
     * 随机获取一个奖励
     * @param difficulty
     * @param defenderKind
     * @param regionLv
     * @return
     */
    public static Award getRandomFightAward(Integer difficulty, Integer defenderKind, Integer regionLv){
        CfgWinAwardRule cfgArticleRule = getWinAwardRule(difficulty, defenderKind);
        //计算添加了多少概率
        int addProbability = (regionLv / cfgArticleRule.getAddRegionLv()) * cfgArticleRule.getAddProbability() ;
        //获取奖池
        List<CfgWinAwardRule.ZxzAwardPool> awardPool = cfgArticleRule.getAwardPool();

        List<Award> awards = new ArrayList<>();
        for (CfgWinAwardRule.ZxzAwardPool zxzAwardPool : awardPool) {
            if (zxzAwardPool.getProbability() < 0) {
               continue;
            }
            //添加概率
            int probability = zxzAwardPool.getProbability() + addProbability;
            if (PowerRandom.hitProbability(probability)){
                awards.addAll(zxzAwardPool.getAwards());
                break;
            }
        }
        //随机获取一个奖励
        Award award = PowerRandom.getRandomFromList(awards);
        return award;
    }

    /**
     * 获取战斗奖励
     * @param difficulty
     * @param defenderKind
     * @param regionLv
     * @return
     */
    public static List<Award> getDefenderWinAwards(Integer difficulty, Integer defenderKind, Integer regionLv){
        //随机掉落数量
        Integer dropNum = getRandomDropNum(defenderKind, regionLv);
        List<Award> awards = new ArrayList<>();
        for (int i = 0; i < dropNum; i++) {
            //随机奖励
            Award award = getRandomFightAward(difficulty, defenderKind, regionLv);
            awards.add(award);
        }
        return awards;
    }
    public static List<Award> getDefenderWinAwards(Integer difficulty, Integer defenderKind, Integer regionLv,Integer clearanceNum){
        List<Award> winAwards = ZxzAwardTool.getDefenderWinAwards(difficulty,defenderKind, regionLv);
        for (Award winAward : winAwards) {
            if (winAward.getItem() == AwardEnum.TQ.getValue()) {
                winAward.setNum(winAward.getNum() * clearanceNum);
            }
        }
        return  winAwards;
    }

    /**
     * 根据区域等级随机获取倍数
     * @param regionLv
     * @return
     */
    public static Integer getRandomMultiple(Integer regionLv){
        //过滤出区域等级宝箱奖励的随机规则
        CfgBoxMultipleRule bonusMultipleRule = getBoxMultipleRules().stream()
                .filter(rule -> isInRange(regionLv,rule.getRegionLvInterval()))
                .findFirst().orElse(null);
        //获取倍数
        List<CfgBoxMultipleRule.ZxzAwardMultiplePool> awardMultiplePool = bonusMultipleRule.getAwardMultiplePool();
        //取出所有的概率
        List<Integer> probabilitys = awardMultiplePool.stream()
                .map(CfgBoxMultipleRule.ZxzAwardMultiplePool::getProbability)
                .collect(Collectors.toList());
        //获取中的索引
        int indexWin = PowerRandom.getIndexByProbs(probabilitys, 100);
        return awardMultiplePool.get(indexWin).getMultiple();
    }

    /**
     * 获取关卡宝箱奖励
     * @param uId
     * @param difficulty
     * @param defenderKind
     * @return
     */
    public static List<Award> getDefenderBoxAwards(long uId, Integer difficulty, Integer defenderKind, Integer regionLv){
       //获取宝箱奖励配置
        CfgBoxAwardRule cfgBoxAwardRule = getBoxAwardRules().stream()
                .filter(rule -> rule.getDifficulty().equals(difficulty) && rule.getDefenderKind().equals(defenderKind))
                .findFirst().orElse(null);
        //宝箱转奖励
        List<Award> awards = boxService.getAward(uId, cfgBoxAwardRule.getBoxId());
        //计算倍数
        List<Award> boxAward = new ArrayList<>();
        for (Award award : awards) {
            //判断是不是固定奖励
            if (!award.getAwardId().equals(cfgBoxAwardRule.getFixedId())) {
                Integer multiple = getRandomMultiple(regionLv);
                //不是固定奖励的需要计算倍数
                award.setNum(award.getNum() * multiple);
            }
            boxAward.add(award);
        }
        return boxAward;
    }

    /**
     * 获取全通奖励
     * @param difficulty
     * @param difficultyTotalRegionLv 某个难度的区域等级和
     * @return
     */
    public static List<Award> getAllPassAward(Integer difficulty, Integer difficultyTotalRegionLv,Integer clearanceNum){
        List<Award> awards = new ArrayList<>();
        List<Award> allAward = ZxzAwardTool.getAllPassAward(difficulty, difficultyTotalRegionLv);
        for (Award award : allAward) {
            if (award.getItem() == AwardEnum.TQ.getValue()) {
                award.setNum(award.getNum() * clearanceNum);
            }
            awards.add(award);
        }
        return awards;
    }

    public static List<Award> getAllPassAward(Integer difficulty, Integer difficultyTotalRegionLv){
        List<Award> awards = new ArrayList<>();
        //全通宝箱配置
        List<CfgAllPassAwardRule> allpassAwardRules = getAllPassAwardRules();
        //该难度下的全通宝箱配置
        CfgAllPassAwardRule allPassAwardRule = allpassAwardRules.stream()
                .filter(rule -> rule.getDifficulty().equals(difficulty))
                .findFirst().orElse(null);

        //计算加的倍数
        int addMultiple = difficultyTotalRegionLv / allPassAwardRule.getAddRegionLv() + 1;
        //获取奖池
        List<CfgAllPassAwardRule.AllAwardPool> awardPools = allPassAwardRule.getAwardPool();

        for (CfgAllPassAwardRule.AllAwardPool allAwardPool : awardPools) {
            //判断是不是固定奖励(必出奖励)
            boolean isFixed = allAwardPool.getProbability() == 1000;
            if (isFixed) {
                List<Award> cfgAwards = allAwardPool.getAwards();
                awards.add(PowerRandom.getRandomFromList(cfgAwards));
            }else {
                //判断是不是中
                int probability = allAwardPool.getProbability() * addMultiple;
                if (PowerRandom.hitProbability(probability,1000)) {
                    List<Award> cfgAwards = allAwardPool.getAwards();
                    awards.add(PowerRandom.getRandomFromList(cfgAwards));
                }
            }
        }
        return awards;
    }

    /**
     * 判断值是否在这个区间里面
     * @param value
     * @param valueInterval {min,max}
     * @return
     */
    public static boolean isInRange(int value, List<Integer> valueInterval){
        return value >= valueInterval.get(0) && value <= valueInterval.get(1);
    }

}
