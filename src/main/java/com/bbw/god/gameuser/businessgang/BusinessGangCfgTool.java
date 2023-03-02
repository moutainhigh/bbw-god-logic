package com.bbw.god.gameuser.businessgang;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.gameuser.businessgang.Enum.GiftsGradeEnum;
import com.bbw.god.gameuser.businessgang.cfg.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商帮配置工具类
 *
 * @author fzj
 * @date 2022/1/17 11:30
 */
public class BusinessGangCfgTool {
    /**
     * 获得配置信息
     *
     * @return
     */
    public static CfgBusinessGangEntity getBusinessGangInfo() {
        return Cfg.I.getUniqueConfig(CfgBusinessGangEntity.class);
    }

    /**
     * 获得所有礼物信息
     *
     * @return
     */
    public static List<CfgGiftEntity> getAllGiftInfos() {
        return Cfg.I.get(CfgGiftEntity.class);
    }

    /**
     * 获得低级礼物
     *
     * @return
     */
    public static List<Integer> getLowGifts() {
        return getAllGiftInfos().stream().filter(g -> g.getGrade() == GiftsGradeEnum.LOW.getGrade()).map(CfgGiftEntity::getGiftId).collect(Collectors.toList());
    }

    /**
     * 获得高级礼物
     *
     * @return
     */
    public static List<Integer> getHighGifts() {
        return getAllGiftInfos().stream().filter(g -> g.getGrade() == GiftsGradeEnum.ADVANCED.getGrade()).map(CfgGiftEntity::getGiftId).collect(Collectors.toList());
    }

    /**
     * 获得单个礼物信息
     *
     * @param giftId
     * @return
     */
    public static CfgGiftEntity getGiftInfo(int giftId) {
        CfgGiftEntity cfgGiftEntity = getAllGiftInfos().stream().filter(g -> g.getGiftId() == giftId).findFirst().orElse(null);
        if (null == cfgGiftEntity) {
            throw new ExceptionForClientTip("businessGang.gift.not.exit");
        }
        return cfgGiftEntity;
    }

    /**
     * 获得好感度规则
     *
     * @param totalFavorability
     * @return
     */
    public static CfgFavorabilityRules getFavorabilityRule(int totalFavorability, int npcType) {
        List<CfgFavorabilityRules> favorabilityRules = getBusinessGangInfo().getFavorabilityRules();
        return favorabilityRules.stream().filter(f -> npcType == f.getNpcType() && totalFavorability < f.getFavorability()).findFirst().orElse(null);
    }

    /**
     * 获得商帮信息
     *
     * @param businessGangId
     * @return
     */
    public static CfgBusinessGangData getBusinessGangData(Integer businessGangId) {
        CfgBusinessGangData cfgBusinessGangData = getBusinessGangInfo().getBusinessGangData()
                .stream().filter(b -> b.getBusinessGangId().equals(businessGangId)).findFirst().orElse(null);
        if (null == cfgBusinessGangData) {
            throw new ExceptionForClientTip("businessGang.not.exit");
        }
        return cfgBusinessGangData;
    }

    /**
     * 获得npc信息
     *
     * @param npcId
     * @return
     */
    public static CfgNpcInfo getNpcInfo(Integer npcId) {
        CfgNpcInfo cfgNpcInfo = getBusinessGangInfo().getNpcInfo().stream().filter(b -> b.getId().equals(npcId)).findFirst().orElse(null);
        if (null == cfgNpcInfo) {
            throw new ExceptionForClientTip("businessGang.npc.not.exit");
        }
        return cfgNpcInfo;
    }

    /**
     * 获得运送任务配置信息
     *
     * @return
     */
    public static CfgBusinessGangShippingTaskRules getShippingTaskRules() {
        return Cfg.I.getUniqueConfig(CfgBusinessGangShippingTaskRules.class);
    }

    /**
     * 获得任务规则
     *
     * @param difficulty
     * @return
     */
    public static CfgTaskRules getShippingTaskRules(int difficulty) {
        CfgTaskRules cfgTaskRules = getShippingTaskRules().getTaskRules().stream().filter(t -> t.getDifficulty().equals(difficulty)).findFirst().orElse(null);
        if (null == cfgTaskRules) {
            throw new ExceptionForClientTip("task.not.exist");
        }
        return cfgTaskRules;
    }

    /**
     * 获得声望配置信息
     *
     * @return
     */
    public static List<CfgPrestigeEntity> getAllPrestigeEntity() {
        return Cfg.I.get(CfgPrestigeEntity.class);
    }

    /**
     * 获得声望配置信息
     *
     * @return
     */
    public static CfgPrestigeEntity getPrestigeEntity(int businessGang) {
        CfgPrestigeEntity prestigeEntity = getAllPrestigeEntity().stream().filter(p -> p.getBusinessGangId().equals(businessGang)).findFirst().orElse(null);
        if (null == prestigeEntity) {
            throw new ExceptionForClientTip("businessGang.not.prestige");
        }
        return prestigeEntity;
    }

    /**
     * 获得声望衰减规则
     *
     * @return
     */
    public static List<CfgReputationAndDecay> getDecayRules() {
        return getBusinessGangInfo().getReputationAndDecay();
    }

    /**
     * 获得对应规则
     *
     * @param prestige
     * @return
     */
    public static CfgReputationAndDecay getDecayRule(int prestige) {
        return getDecayRules().stream().filter(d -> prestige >= d.getPrestige()).findFirst().orElse(null);
    }

    /**
     * 获得礼物的好感度
     *
     * @param cfgNpcInfo
     * @param giftId
     * @return
     */
    public static Integer getGiftFavorability(CfgNpcInfo cfgNpcInfo, int giftId) {
        CfgGiftEntity giftInfo = BusinessGangCfgTool.getGiftInfo(giftId);
        //判断是否喜欢或者厌恶
        boolean isHobby = cfgNpcInfo.getHobbyGifts().contains(giftId);
        if (isHobby) {
            return giftInfo.getFavorability() * 2;
        }
        boolean isHate = cfgNpcInfo.getHateGifts().contains(giftId);
        if (isHate) {
            return giftInfo.getFavorability() / 2;
        }
        return giftInfo.getFavorability();
    }

    /**
     * 获得npc信息
     *
     * @param npcType
     * @return
     */
    public static List<CfgNpcInfo> getNpcInfos(int npcType) {
        return BusinessGangCfgTool.getBusinessGangInfo().getNpcInfo().stream().filter(n -> n.getType() == npcType).collect(Collectors.toList());
    }

    /**
     * 获得npc信息
     *
     * @param gangId
     * @return
     */
    public static List<CfgNpcInfo> getNpcInfosByGang(int gangId) {
        return BusinessGangCfgTool.getBusinessGangInfo().getNpcInfo().stream().filter(n -> n.getGangId().equals(gangId)).collect(Collectors.toList());
    }

    /**
     * 获得npc信息
     *
     * @param gangId
     * @param npcType
     * @return
     */
    public static CfgNpcInfo getNpcInfo(int gangId, int npcType) {
        CfgNpcInfo cfgNpcInfo = getBusinessGangInfo().getNpcInfo().stream()
                .filter(n -> n.getType() == npcType && n.getGangId().equals(gangId)).findFirst().orElse(null);
        if (null == cfgNpcInfo) {
            throw new ExceptionForClientTip("businessGang.npc.not.exit");
        }
        return cfgNpcInfo;
    }

    /**
     * 获得需要解锁的商品
     *
     * @param goodId
     * @param item
     * @return
     */
    public static CfgNeedUnlockGoods getNeedUnlockGood(int goodId, int item) {
        return getBusinessGangInfo().getNeedUnlockGoods().stream().filter(g -> g.getGoodId().equals(goodId) && g.getItem().equals(item)).findFirst().orElse(null);
    }

    /**
     * 获得特殊野怪宝箱概率
     *
     * @param prestige
     * @return
     */
    public static Integer getSpecialYeGBoxPro(int prestige) {
        return getBusinessGangInfo().getSpecialYeGBoxPro().stream().filter(s -> prestige >= s.getPrestige())
                .map(CfgBusinessGangEntity.SpecialYeGBoxAndPro::getProbability).findFirst().orElse(null);
    }

    /**
     * 获得特产升阶概率
     *
     * @param favorability
     * @return
     */
    public static Integer getSpecialUpgradeProb(int favorability) {
        return getBusinessGangInfo().getSpecialUpgradeProbs().stream().filter(s -> favorability >= s.getFavorability())
                .map(CfgBusinessGangEntity.SpecialUpgradeProb::getProbability).findFirst().orElse(0);
    }

    /**
     * 获得特产升阶永久开启需要的好感度
     *
     * @return
     */
    protected static Integer getPermanentOpenNeedFavorability() {
        CfgBusinessGangEntity cfgBusinessGangEntity = getBusinessGangInfo();
        return cfgBusinessGangEntity.getPermanentOpenNeedFavorability();
    }

    /**
     * 根据声望获取对应商帮
     *
     * @param prestigeId
     * @return
     */
    public static Integer getGangByPrestigeId(int prestigeId) {
        //获得对应商帮
        return BusinessGangCfgTool.getAllPrestigeEntity().stream()
                .filter(p -> p.getPrestigeId().equals(prestigeId)).map(CfgPrestigeEntity::getBusinessGangId)
                .findFirst().orElse(null);
    }
}
