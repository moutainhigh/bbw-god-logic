package com.bbw.god.activity.holiday.processor.holidaythankflowerlanguage;

import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.Cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 感恩花语工具类
 *
 * @author: huanghb
 * @date: 2021/12/22 15:27
 */
public class ThankFlowerLanguageTool {

    /**
     * 获取配置类
     *
     * @return
     */
    public static CfgThankFlowerLanguage getCfg() {
        return Cfg.I.getUniqueConfig(CfgThankFlowerLanguage.class);
    }

    /**
     * 获得所有花的信息
     *
     * @return
     */
    public static List<CfgThankFlowerLanguage.FlowerInfo> getFlowerInfos() {
        return getCfg().getFlowerInfos();
    }

    /**
     * 获得指定花的信息
     *
     * @param flowerpotInfo
     * @return
     */
    public static CfgThankFlowerLanguage.FlowerInfo getFlowerInfo(int flowerpotInfo) {
        List<CfgThankFlowerLanguage.FlowerInfo> flowerInfos = getFlowerInfos();
        CfgThankFlowerLanguage.FlowerInfo flowerInfo = flowerInfos.stream().filter(tmp -> tmp.getId() == flowerpotInfo).findFirst().orElse(null);
        if (null != flowerInfo) {
            return flowerInfo;
        }
        throw CoderException.high(String.format("没有配置flowerpotInfo=%s的花", flowerpotInfo));
    }

    /**
     * 获得指定花的信息
     *
     * @param seedId
     * @param fertilizerId 肥料id
     * @return
     */
    public static CfgThankFlowerLanguage.FlowerInfo getFlowerInfos(int seedId, int fertilizerId) {
        List<CfgThankFlowerLanguage.FlowerInfo> allFlowerInfos = getFlowerInfos();
        CfgThankFlowerLanguage.FlowerInfo flowerInfo = allFlowerInfos.stream().filter(tmp -> tmp.getNeedFertilizer() == fertilizerId && tmp.isMatch(seedId)).findFirst().orElse(null);
        if (null != flowerInfo) {
            return flowerInfo;
        }
        throw CoderException.high(String.format("没有配置使用fertilizerId=%s的花", fertilizerId));
    }

    /**
     * 种子包是否存在
     *
     * @param seedBagId 种子包id
     * @return
     */
    public static void isSeedBagExist(int seedBagId) {
        //获得种子包信息
        List<Integer> seedBags = getCfg().getSeedBags();
        Integer seedBag = seedBags.stream().filter(tmp -> tmp.intValue() == seedBagId).findFirst().orElse(null);
        if (null != seedBag) {
            return;
        }
        throw new ExceptionForClientTip("activity.thankFlowerLanguage.seedbag.is.not.exist");
    }

    /**
     * 肥料包是否存在
     *
     * @param fertilizerId 肥料包id
     * @return
     */
    public static void isFertilizerExist(int fertilizerId) {
        List<Integer> fertilizers = getCfg().getFertilizers();
        Integer applyFertilizer = fertilizers.stream().filter(tmp -> tmp.intValue() == fertilizerId).findFirst().orElse(null);
        if (null != applyFertilizer) {
            return;
        }
        throw new ExceptionForClientTip("activity.thankFlowerLanguage.fertilizer.is.not.exist");
    }

    /**
     * 是否有效的花盆id
     *
     * @param flowerpotId
     */
    public static void isValidFlowerpotId(int flowerpotId) {
        boolean isValidFlowerpotId = flowerpotId >= 0 && flowerpotId < getCfg().getFlowerpotNum();
        if (isValidFlowerpotId) {
            return;
        }
        throw new ExceptionForClientTip("activity.thankFlowerLanguage.flowerpot.is.not.exist");
    }

    /**
     * 种子包是否存在
     *
     * @param seedBagId 种子包id
     * @return
     */
    public static CfgThankFlowerLanguage.SeedBagInfo getSeedBagInfo(int seedBagId) {
        List<CfgThankFlowerLanguage.SeedBagInfo> seedBags = getCfg().getSeedBagInfos();
        CfgThankFlowerLanguage.SeedBagInfo seedBag = seedBags.stream().filter(tmp -> tmp.getId() == seedBagId).findFirst().orElse(null);
        if (null != seedBag) {
            return seedBag;
        }
        throw new ExceptionForClientTip("activity.thankFlowerLanguage.seedbag.is.not.exist");
    }

    /**
     * 获得花束等级
     *
     * @param flowers 花朵=》数量
     * @return
     */
    public static int getFlowerLevel(Map<Integer, Integer> flowers) {
        List<Integer> specialFlowers = ThankFlowerLanguageTool.getCfg().getBouquetInfos().stream().filter(tmp -> 0 != tmp.getFlowerId()).map(CfgThankFlowerLanguage.BouquetInfo::getFlowerId).collect(Collectors.toList());
        //获得第一朵花的id
        Integer firstFlowerId = flowers.keySet().stream().collect(Collectors.toList()).get(0);
        //如果只有一种花，且是中低级的花，返还等级0
        if (1 == flowers.size() && specialFlowers.contains(firstFlowerId)) {
            return 0;
        }
        //获得花的等级信息
        Map<Integer, Integer> flowerLevels = getCfg().getFlowerLevels();
        List<Integer> flowerLevelPro = new ArrayList<>();
        //获得花束产生概率
        for (Map.Entry<Integer, Integer> flower : flowers.entrySet()) {
            int pro = flower.getValue() * getCfg().getPerFlowerEfferBouquetPro() / 10;
            flowerLevelPro.add(pro);
        }
        //获得花束等级
        int index = PowerRandom.hitProbabilityIndex(flowerLevelPro);
        Integer flower = flowers.keySet().stream().collect(Collectors.toList()).get(index);
        return flowerLevels.get(flower);
    }

    /**
     * 获得花束
     *
     * @param flowers
     * @return
     */
    public static Integer getBouquet(Map<Integer, Integer> flowers) {
        int flowerLevel = getFlowerLevel(flowers);
        int flowerId = flowerLevel > 0 ? 0 : flowers.keySet().stream().findFirst().get();
        List<CfgThankFlowerLanguage.BouquetInfo> bouquetInfos = getCfg().getBouquetInfos();
        CfgThankFlowerLanguage.BouquetInfo bouquetInfo = bouquetInfos.stream().filter(tmp -> tmp.getBouquetLevel() == flowerLevel && tmp.getFlowerId() == flowerId).findFirst().orElse(null);
        if (null != bouquetInfo) {
            Integer bouquet = PowerRandom.getRandomFromList(bouquetInfo.getBouquets());
            return bouquet;
        }
        throw CoderException.high(String.format("没有配置使用flowerId=%s的花束", flowerId));
    }

    /**
     * 获得所有花的id
     *
     * @return
     */
    public static List<Integer> getFlowers() {
        return getCfg().getFlowers();
    }
}
