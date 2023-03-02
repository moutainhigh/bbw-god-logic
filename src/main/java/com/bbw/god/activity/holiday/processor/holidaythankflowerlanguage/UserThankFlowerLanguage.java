package com.bbw.god.activity.holiday.processor.holidaythankflowerlanguage;

import com.bbw.common.PowerRandom;
import com.bbw.god.cache.tmp.AbstractTmpData;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 玩家感谢花语
 *
 * @author: huanghb
 * @date: 2022/11/16 9:31
 */
@Data
public class UserThankFlowerLanguage extends AbstractTmpData implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer[] flowerpotInfos;

    protected static UserThankFlowerLanguage instance(long uid) {
        UserThankFlowerLanguage userTreasureTroveMap = new UserThankFlowerLanguage();
        userTreasureTroveMap.setId(uid);
        userTreasureTroveMap.setFlowerpotInfos(new Integer[]{0, 0, 0, 0});
        return userTreasureTroveMap;
    }

    /**
     * 获得花id
     *
     * @param flowerpotId
     */
    public Integer getFlowerId(Integer flowerpotId) {
        Integer flowerpotInfo = this.flowerpotInfos[flowerpotId];
        if (0 == flowerpotInfo) {
            return flowerpotInfo;
        }
        CfgThankFlowerLanguage.FlowerInfo flowerInfo = ThankFlowerLanguageTool.getFlowerInfo(flowerpotInfo);
        return flowerInfo.getFlowerId();
    }

    /**
     * 获得花数量
     *
     * @param flowerpotId
     */
    public Integer getFlowerNum(Integer flowerpotId) {
        Integer flowerpotInfo = this.flowerpotInfos[flowerpotId];
        if (0 == flowerpotInfo) {
            return flowerpotInfo;
        }
        CfgThankFlowerLanguage.FlowerInfo flowerInfo = ThankFlowerLanguageTool.getFlowerInfo(flowerpotInfo);
        return flowerInfo.getFlowerNum();
    }

    /**
     * 种植
     *
     * @param seedBag
     */
    public void plant(int seedBag, int flowerpotId) {
        CfgThankFlowerLanguage.SeedBagInfo seedBagInfo = ThankFlowerLanguageTool.getSeedBagInfo(seedBag);
        Integer seed = PowerRandom.getRandomFromList(seedBagInfo.getSeeds());
        this.flowerpotInfos[flowerpotId] = seed;
    }

    /**
     * 一键种植
     *
     * @author: huanghb
     * @date: 2022/11/17 17:57
     */
    public void oneClickPlant(List<Award> awards) {
        for (int i = 0; i < awards.size(); i++) {
            for (int j = 0; j < awards.get(i).getNum(); j++) {
                Integer emptyFlowerpotPos = gainFirstEmptyFlowerpotPos();
                if (null == emptyFlowerpotPos) {
                    continue;
                }
                plant(awards.get(i).getAwardId(), emptyFlowerpotPos);
            }
        }
    }

    /**
     * 施肥
     *
     * @param flowerpotId
     */
    public void applyFertilizer(int flowerpotId, int fertilizerId) {
        CfgThankFlowerLanguage.FlowerInfo flowerInfo = ThankFlowerLanguageTool.getFlowerInfos(this.flowerpotInfos[flowerpotId], fertilizerId);
        this.flowerpotInfos[flowerpotId] = flowerInfo.getId();
    }

    /**
     * 一键施肥
     *
     * @author: huanghb
     * @date: 2022/11/17 17:57
     */
    public void oneClickFertilizer(List<Award> awards) {
        for (int i = 0; i < awards.size(); i++) {
            for (int j = 0; j < awards.get(i).getNum(); j++) {
                Integer firstSeedPos = gainFirstSeedPos();
                if (null == firstSeedPos) {
                    continue;
                }
                applyFertilizer(firstSeedPos, awards.get(i).getAwardId());
            }
        }
    }

    /**
     * 是否是成花
     *
     * @param flowerpotId
     * @return
     */
    public boolean ifFlower(int flowerpotId) {
        return this.flowerpotInfos[flowerpotId] > 0 && this.flowerpotInfos[flowerpotId] % 100 > 0;
    }

    /**
     * 是否是种子
     *
     * @param flowerpotId
     * @return
     */
    public boolean ifSeed(int flowerpotId) {
        return this.flowerpotInfos[flowerpotId] > 0 && this.flowerpotInfos[flowerpotId] % 100 == 0;
    }

    /**
     * 是否有成花
     *
     * @return
     */
    public boolean ifHasFlower() {
        for (int i = 0; i < this.flowerpotInfos.length; i++) {
            if (ifFlower(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 采摘
     *
     * @param flowerpotId
     */
    public Award pick(int flowerpotId) {
        Integer flowerpotInfo = this.flowerpotInfos[flowerpotId];
        Award award = new Award(flowerpotInfo / 100, AwardEnum.FB, flowerpotInfo % 100);
        this.flowerpotInfos[flowerpotId] = 0;
        return award;
    }


    /**
     * 一键采摘
     */
    public List<Award> oneClickPick() {
        List<Award> awards = new ArrayList<>();
        for (int i = 0; i < this.flowerpotInfos.length; i++) {
            if (!ifFlower(i)) {
                continue;
            }
            Award award = pick(i);
            awards.add(award);
        }
        return awards;
    }


    /**
     * 获得空花盆数量
     *
     * @return
     */
    public Integer gainEmptyFlowerpotNum() {
        int emptyFlowerpotNum = 0;
        for (int i = 0; i < this.flowerpotInfos.length; i++) {
            if (0 == flowerpotInfos[i]) {
                emptyFlowerpotNum++;
            }
        }
        return emptyFlowerpotNum;
    }

    /**
     * 获得第一个空花盆
     *
     * @return
     */
    public Integer gainFirstEmptyFlowerpotPos() {
        for (int i = 0; i < this.flowerpotInfos.length; i++) {
            if (0 == flowerpotInfos[i]) {
                return i;
            }
        }
        return null;
    }

    /**
     * 获得第一个种子位置
     *
     * @return
     */
    public Integer gainFirstSeedPos() {
        for (int i = 0; i < this.flowerpotInfos.length; i++) {
            if (ifSeed(i)) {
                return i;
            }
        }
        return null;
    }

    /**
     * 获得有种子花盆数量
     *
     * @return
     */
    public Integer gainSeedFlowerpotNum() {
        int emptyFlowerpotNum = 0;
        for (int i = 0; i < this.flowerpotInfos.length; i++) {
            if (ifSeed(i)) {
                emptyFlowerpotNum++;
            }
        }
        return emptyFlowerpotNum;
    }

}
