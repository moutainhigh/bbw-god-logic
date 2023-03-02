package com.bbw.god.gameuser.businessgang.luckybeast;

import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 招财兽工具类
 *
 * @author: huanghb
 * @date: 2022/1/18 11:06
 */
public class LuckyBeastTool {
    /**
     * 获取招财兽配置
     *
     * @return
     */
    protected static CfgLuckyBeast getLuckyBeastCfg() {
        return Cfg.I.getUniqueConfig(CfgLuckyBeast.class);
    }

    /**
     * 根据声望获得每只招财兽出现的概率
     *
     * @param prestige 声望
     * @return
     */
    public static CfgLuckyBeast.LuckyBeastOutputProb getPerLuckyBeastOutputProbs(int prestige) {
        CfgLuckyBeast cfgLuckyBeast = getLuckyBeastCfg();
        List<CfgLuckyBeast.LuckyBeastOutputProb> luckyBeastOutputProbs = cfgLuckyBeast.getLuckyBeastOutputProbs();
        return luckyBeastOutputProbs.stream().filter(tmp -> prestige >= tmp.getNeedPrestige()).findFirst().orElse(null);
    }

    /**
     * 获得刷新招财兽的最小声望值
     *
     * @return
     */
    public static Integer getRefreshLuckyBeastMinPrestige() {
        CfgLuckyBeast cfgLuckyBeast = getLuckyBeastCfg();
        List<CfgLuckyBeast.LuckyBeastOutputProb> luckyBeastOutputProbs = cfgLuckyBeast.getLuckyBeastOutputProbs();
        //最小声望值记录的下标
        int refreshLuckyBeastMinPrestigeIndex = luckyBeastOutputProbs.size() - 1;
        return luckyBeastOutputProbs.get(refreshLuckyBeastMinPrestigeIndex).getNeedPrestige();

    }

    /**
     * 获取所有招财兽
     *
     * @return
     */
    protected static List<CfgLuckyBeast.LuckyBeastInfo> getLuckyBeasts() {
        CfgLuckyBeast cfgLuckyBeast = getLuckyBeastCfg();
        return cfgLuckyBeast.getLuckyBeasts();
    }

    /**
     * 获取招财兽
     *
     * @param prestige
     * @return
     */
    protected static CfgLuckyBeast.LuckyBeastInfo getLuckyBeastInfo(Integer prestige) {
        //根据声望获得每只招财兽出现的概率
        CfgLuckyBeast.LuckyBeastOutputProb outputProbs = getPerLuckyBeastOutputProbs(prestige);
        if (null == outputProbs) {
            throw CoderException.high(String.format("prestige=%s范围的招财兽", prestige));
        }
        //返回命中的概率所在的数组下标
        Integer hitLuckyBeastIndex = PowerRandom.hitProbabilityIndex(outputProbs.getPerLuckyBeastOutputProbs());
        //根据下标返回对应的招财兽
        return getLuckyBeasts().get(hitLuckyBeastIndex);
    }

    /**
     * 获得当前招财奖励
     *
     * @param luckyBeastAwardRule
     * @return
     */
    protected static List<Award> getCurrentAwards(String luckyBeastAwardRule) {
        //获得所有奖池信息
        List<CfgLuckyBeast.awardPoolInfo> awardPoolInfos = getLuckyBeastCfg().getAwardPoolInfos();
        //获得本次奖池信息
        CfgLuckyBeast.awardPoolInfo awardPoolInfo = awardPoolInfos.stream().filter(tmp -> tmp.getLuckyBeastAwardRule().equals(luckyBeastAwardRule)).findFirst().orElse(null);
        if (null == awardPoolInfo) {
            throw CoderException.high(String.format("没有配置luckyBeastAwardRule=%s的规则", luckyBeastAwardRule));
        }
        //返回命中的概率所在的数组下标
        Integer hitAwardIndex = PowerRandom.hitProbabilityIndex(awardPoolInfo.getAwardProbs());
        //根据下标返回对应的奖励
        return Arrays.asList(awardPoolInfo.getAwards().get(hitAwardIndex));
    }

    /**
     * 获得本次招财奖励类别
     *
     * @param awardTypeProbsRule\
     * @return
     */
    protected static Integer getCurrentAwardType(String awardTypeProbsRule) {
        //获得所有奖池信息
        Map<String, List<Integer>> awardTypeProbsRules = getLuckyBeastCfg().getAwardTypeProbsRules();
        //获得本次奖励类被概率信息
        List<Integer> awardTypeProbs = awardTypeProbsRules.get(awardTypeProbsRule);
        if (null == awardTypeProbs) {
            throw CoderException.high(String.format("没有配置awardTypeProbsRule=%s的规则", awardTypeProbsRule));
        }
        //返回命中的概率所在的数组下标
        int index = PowerRandom.hitProbabilityIndex(awardTypeProbs);
        return index;
    }

    /**
     * 获得界碑位置集合
     *
     * @return
     */
    protected static List<Integer> getJieBeiPosList() {
        return getLuckyBeastCfg().getJieBeiPosList();
    }

    /**
     * 获得购买攻击次数需要的荣耀铜币数量
     *
     * @return
     */
    protected static Integer getBuyAttackTimesNeedHonorCopperCoinNum() {
        return getLuckyBeastCfg().getBuyAttackTimesNeedHonorCopperCoinNum();
    }

    /**
     * 获得重置招财兽需要的御兽铃铛数量
     *
     * @return
     */
    protected static Integer getResetNeedBellNum() {
        return getLuckyBeastCfg().getResetNeedBellNum();
    }

    /**
     * 对招财兽有影响的法宝id
     *
     * @return
     */
    public static List<Integer> getOwnEffectTReasureIds() {
        CfgLuckyBeast cfgLuckyBeast = getLuckyBeastCfg();
        return cfgLuckyBeast.getOwnEffectTReasureIds();
    }
}
