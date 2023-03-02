package com.bbw.god.game.zxz.cfg.award;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import com.bbw.god.game.config.CfgPrepareListInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * 诛仙阵 奖励配置表
 * @author: hzf
 * @create: 2022-09-22 11:12
 **/
@Data
public class CfgZxzAwardEntity implements CfgEntityInterface, CfgPrepareListInterface, Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    private String key;
    /** 通关次数上限限制 */
    private Integer clearanceNumLimit;

    /** 奖励掉落数量规则 */
    private List<CfgQuantityRule> quantityRules;
    /** 奖励物品掉落规则 */
    private List<CfgWinAwardRule> winAwardRuleRules;
    /** 宝箱奖励倍数的随机规则（每个道具单独计算倍数）*/
    private List<CfgBoxMultipleRule> boxMultipleRules;
    /** 宝箱奖励规则 */
    private List<CfgBoxAwardRule> boxAwardRules;
    /**全通奖励 */
    private List<CfgAllPassAwardRule> allPassAwardRules;
    /** 随机本源 */
    private List<Award> randomOrigin;
    /**首次全通获得奖励 */
    private List<Award> firstClearanceAward;


    @Override
    public void prepare() {
        //排序：掉落顺序
        for (CfgQuantityRule quantityRule : quantityRules) {
            quantityRule.getAwardNumPool().sort(Comparator.comparing(CfgQuantityRule.QuantityRuleAwardNumPool::getOrder));
        }
        //排序：战斗奖励
        for (CfgWinAwardRule winAwardRuleRule : winAwardRuleRules) {
            winAwardRuleRule.getAwardPool().sort(Comparator.comparing(CfgWinAwardRule.ZxzAwardPool::getOrder));

        }

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
