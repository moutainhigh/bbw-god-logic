package com.bbw.god.activity.cfg;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 感恩节相关配置
 *
 * @author fzj
 * @date 2021/11/22 9:30
 */
@Data
public class CfgThanksGiving implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 食物对应的食材和调料 */
    private Map<Integer, List<List<Integer>>> seasoningCookFoods;
    /** 村庄npc收到对应食物获的好感度 */
    private Map<Integer, List<List<Integer>>> cunZNpcGratitude;
    /** npc好感度满值以及可兑换次数 */
    private List<List<Integer>> npcFullGratitudeAndTimes;
    /** 老者随机奖励 */
    private List<Award> oldManRandomAwards;

    /**
     * 获取配置类
     *
     * @return
     */
    public static CfgThanksGiving getThanksGivingInfo() {
        return Cfg.I.getUniqueConfig(CfgThanksGiving.class);
    }

    /**
     * 根据概率随机获得奖励
     *
     * @param
     * @return
     */
    public static Award randomAwardByProb(List<Award> randomAwards) {
        List<Integer> probs = randomAwards.stream().map(Award::getProbability).collect(Collectors.toList());
        Integer awardIndex = PowerRandom.hitProbabilityIndex(probs);
        return randomAwards.get(awardIndex);
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }
}
