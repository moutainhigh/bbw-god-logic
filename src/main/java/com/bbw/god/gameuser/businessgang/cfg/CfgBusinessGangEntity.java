package com.bbw.god.gameuser.businessgang.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商帮基础配置类
 *
 * @author fzj
 * @date 2022/1/17 10:11
 */
@Data
public class CfgBusinessGangEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 开启商帮等级 */
    private Integer openLevel;
    /** 荣耀货币汇率 */
    private Integer gloryCurrencyRate;
    /** 解锁商帮需要好感度 */
    private Integer unlockBusinessGangNeedFavorability;
    /** 商帮基础数据 */
    private List<CfgBusinessGangData> businessGangData;
    /** 商帮npc信息 */
    private List<CfgNpcInfo> npcInfo;
    /** npc类型对应好感度等级 */
    private List<CfgFavorabilityRules> favorabilityRules;
    /** 声望等级及衰减量 */
    private List<CfgReputationAndDecay> reputationAndDecay;
    /** 需要解锁的商品 */
    private List<CfgNeedUnlockGoods> needUnlockGoods;
    /** 任务刷新概率 */
    private List<TaskRefresh> taskRefreshProbability;
    /** 刷新任务需要的元宝 */
    private Integer refreshTaskNeedGold;
    /** 派遣任务生成的概率 */
    private Integer dispatchTaskRefreshPro;
    /** 商帮任务可生成最大数量 */
    private Integer businessGangTaskNum;
    /** 解锁周常任务商帮 */
    private Integer unlockWeeklyTaskGang;
    /** 解锁周常任务需要的声望 */
    private Integer unlockWeeklyTaskNeedPrestige;
    /** 城内特产解锁礼物所需声望 */
    private Integer unlockGiftsNeedPrestige;
    /** 城内特产刷新礼物概率 */
    private Integer refreshGiftsPro;
    /** 城内特产刷新铜铲子概率 */
    private Integer refreshCopperShovelPro;
    /** 购买低级礼物需要的铜钱 */
    private Integer buyLowGiftNeedCopper;
    /** 购买高级礼物需要的铜钱 */
    private Integer buyHightGiftNeedCopper;
    /** 激城池铜铲子需要的好感度 */
    private Integer unlockCopperShovelNeedFavorability;
    /** 城池购买铜铲子需要的铜钱 */
    private Integer buyCopperShovelNeedCopper;
    /** 解锁特殊野怪宝箱的商帮 */
    private Integer unlockSpecialYeGBoxGang;
    /** 触发特殊野怪宝箱概率对应声望 */
    private List<SpecialYeGBoxAndPro> specialYeGBoxPro;
    /** 触发特产升阶概率对应声望 */
    private List<SpecialUpgradeProb> specialUpgradeProbs;
    /** 获得特产升阶永久开启需要的好感度 */
    private Integer permanentOpenNeedFavorability;

    @Data
    public static class TaskRefresh {
        /** 需要声望 */
        Integer needPrestige;
        /** 任务难度 */
        private Integer difficulty;
        /** 概率 */
        private Integer probability;
    }

    @Data
    public static class SpecialYeGBoxAndPro {
        /** 声望 */
        private Integer prestige;
        /** 概率 */
        private Integer probability;
    }

    @Data
    public static class SpecialUpgradeProb {
        /** 声望 */
        private Integer favorability;
        /** 概率 */
        private Integer probability;
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
