package com.bbw.god.gameuser.yuxg.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 玉虚宫基础配置
 *
 * @author fzj
 * @date 2021/10/29 11:51
 */
@Data
public class CfgYuXGEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 三种晶石升品初始概率 */
    private Map<Integer, List<Integer>> sparInitialProbability;
    /** 符图和玉髓可以提供的经验 */
    private List<Integer> fuTuAndYuSuiSupplyExp;
    /** 开启新的符册需要的元宝 */
    private Integer unlockNewFuCeNeedGold;
    /** 使用熔炼值兑换晶石时需要消耗的熔炼值 */
    private Integer getSparNeedMeltValue;
    /** 熔炼法宝的产出概率 */
    private List<CfgMeltPro> meltPro;
    /** 默认达到的许愿值=》必出符图 */
     private int defaultwishingValue;
    /** 默认每个许愿清单需要多个符图 */
    private int defaultFutuNum;
    /** 符图、玉髓产出  规则一*/
    private List<CfgPrayPro> prayProRuleOne;
    /** 符图、玉髓产出 规则二*/
    private List<CfgPrayPro> prayProRuleTwo;
    /** 符图升级需要经验 */
    private Map<Integer, List<Integer>> fuTuUpgradeNeedExp;
    /** 法坛总等级对应的符图卡槽数量 */
    private List<CfgFuTuSlotNum> fuTuSlotNumAndFaTanAllLv;
    /** 法坛总等级对应卡槽加成 */
    private List<CfgFuTuSlotRate> fuTuSlotRates;
    /** 法坛总等级对应效果加成 */
    private List<CfgFaTanAllLvEffect> faTanAllLvEffects;

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
