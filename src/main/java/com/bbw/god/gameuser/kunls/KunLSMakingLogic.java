package com.bbw.god.gameuser.kunls;

import com.bbw.common.ListUtil;
import com.bbw.common.MapUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.equipment.Enum.QualityEnum;
import com.bbw.god.gameuser.card.equipment.UserCardZhiBaoService;
import com.bbw.god.gameuser.card.equipment.data.UserCardZhiBao;
import com.bbw.god.gameuser.kunls.cfg.CfgKunLS;
import com.bbw.god.gameuser.kunls.rd.RdMakingResult;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 昆仑山炼制室逻辑
 *
 * @author: huanghb
 * @date: 2022/9/15 11:33
 */
@Slf4j
@Service
public class KunLSMakingLogic {
    @Autowired
    private UserCardZhiBaoService userCardZhiBaoService;
    @Autowired
    private KunLSInfusionService kunLSInfusionService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private AwardService awardService;

    /**
     * 炼制
     *
     * @param zhiBaoDataId 献祭至宝zhiBaoDataId
     * @param essentials   必选材料 结构为 材料id，材料数量；材料id，材料数量
     * @param mapQuality   图纸品质
     * @param optionals    可选材料 结构为 材料id，材料数量；材料id，材料数量
     * @param optionals    升阶材料 结构为 材料id，材料数量；材料id，材料数量
     * @return
     */
    protected RdMakingResult making(long uid, long zhiBaoDataId, Integer mapQuality, String essentials, String optionals, String upgradeMaterials) {
        //参数转换 材料id=>材料数量
        Map<Integer, Integer> essentialsMap = parameterParsing(essentials);
        Map<Integer, Integer> optionalsMap = parameterParsing(optionals);
        Map<Integer, Integer> upgradeMaterialsMap = parameterParsing(upgradeMaterials);
        Integer mapTreasureId = CfgKunLSTool.getMapTreasureIdByQuality(mapQuality);
        boolean isHasMap = TreasureChecker.hasTreasure(uid, mapTreasureId);
        if (!isHasMap) {
            throw new ExceptionForClientTip("kunLs.making.map.no.exist");
        }
        //检测装备是否存在
        UserCardZhiBao userCardZhiBao = userCardZhiBaoService.getUserCardZhiBao(uid, zhiBaoDataId);
        if (null == userCardZhiBao && QualityEnum.FAN_PIN.getValue() != mapQuality) {
            throw new ExceptionForClientTip("zhiBao.is.not.exist");
        }
        //获得献祭至宝品质
        int zhiBaoQuality = getZhiBaoQuality(uid, zhiBaoDataId);
        //获得炼制信息
        CfgKunLS.MakingInfo makingInfo = CfgKunLSTool.getMakingInfo(mapQuality, zhiBaoQuality);
        //获得至宝id
        int zhiBaoId = getZhiBaoId(userCardZhiBao);
        //必需材料检测
        essentialsCheck(uid, makingInfo, zhiBaoId, essentialsMap);
        //升品材料检测
        upgradeMaterialsCheck(uid, makingInfo, zhiBaoId, upgradeMaterialsMap);
        //可选材料检测
        optionalsCheck(uid, makingInfo, zhiBaoId, optionalsMap);
        Map<Integer, Integer> needDeductMaterials = new HashMap<>();
        RdMakingResult rd = new RdMakingResult();
        //扣除材料
        needDeductMaterials.putAll(essentialsMap);
        needDeductMaterials.putAll(upgradeMaterialsMap);
        needDeductMaterials.putAll(optionalsMap);
        deductMaterial(uid, makingInfo, zhiBaoDataId, needDeductMaterials, rd);
        //是否炼制成功
        boolean isMakingSucess = isMakingSucess(makingInfo, optionalsMap, mapQuality);
        if (!isMakingSucess) {
            //炼制失败
            rd.makingFailure();
            return rd;
        }
        //获得至宝胚
        int zhiBaoEmbryoId = getZhiBaoEmbryo(makingInfo, zhiBaoId, mapQuality, essentialsMap);
        TreasureEventPublisher.pubTAddEvent(uid, zhiBaoEmbryoId, 1, WayEnum.KUNLS_MAKING, rd);
        return rd;
    }

    /**
     * 是否炼制成功
     *
     * @param makingInfo
     * @param optionalsMap
     * @param mapQuality
     * @return
     */
    private boolean isMakingSucess(CfgKunLS.MakingInfo makingInfo, Map<Integer, Integer> optionalsMap, Integer mapQuality) {
        if (QualityEnum.FAIRY.getValue() == mapQuality) {
            return PowerRandom.hitProbability(makingInfo.getInitProb());
        }
        if (MapUtil.isNotEmpty(optionalsMap)) {
            return true;
        }
        return PowerRandom.hitProbability(makingInfo.getInitProb());
    }

    /**
     * 获得至宝id
     *
     * @param userCardZhiBao
     * @return
     */
    private int getZhiBaoId(UserCardZhiBao userCardZhiBao) {
        return null == userCardZhiBao ? 0 : userCardZhiBao.getZhiBaoId();
    }

    /**
     * 获得至宝胚
     *
     * @param makingInfo
     * @param zhiBaoId
     * @param mapQuality
     * @param essentialsMap
     * @return
     */
    private Integer getZhiBaoEmbryo(CfgKunLS.MakingInfo makingInfo, Integer zhiBaoId, Integer mapQuality, Map<Integer, Integer> essentialsMap) {
        if (QualityEnum.FAN_PIN.getValue() == mapQuality) {
            List<Integer> essentialIds = new ArrayList<>(essentialsMap.values());
            Integer index = PowerRandom.hitProbabilityIndex(essentialIds);
            return makingInfo.getOutPuts().get(index);
        }
        if (0 == zhiBaoId) {
            throw new ExceptionForClientTip("zhiBao.is.not.exist");
        }
        if (null == zhiBaoId) {
            throw new ExceptionForClientTip("zhiBao.is.not.exist");
        }
        int index = zhiBaoId / 1000 - 1;
        return makingInfo.getOutPuts().get(index);
    }

    private void deductMaterial(long uid, CfgKunLS.MakingInfo makingInfo, long zhiBaoDataId, Map<Integer, Integer> needDeductMaterials, RDCommon rd) {
        for (Integer treasureId : needDeductMaterials.keySet()) {
            TreasureEventPublisher.pubTDeductEvent(uid, treasureId, needDeductMaterials.get(treasureId), WayEnum.KUNLS_MAKING, rd);
        }
        if (makingInfo.getIsNeedSacrifice()) {
            userCardZhiBaoService.deductZhiBao(uid, zhiBaoDataId);
        }
    }

    /**
     * 获得至宝品质
     *
     * @param uid
     * @param zhiBaoDataId
     * @return
     */
    private Integer getZhiBaoQuality(long uid, long zhiBaoDataId) {
        if (0 == zhiBaoDataId) {
            return QualityEnum.NONE.getValue();
        }
        UserCardZhiBao userCardZhiBao = userCardZhiBaoService.getUserCardZhiBao(uid, zhiBaoDataId);
        if (null == userCardZhiBao) {
            throw new ExceptionForClientTip("zhiBao.is.not.exist");
        }
        return userCardZhiBao.getZhiBaoId() % 100;
    }

    /**
     * 必需材料检测
     *
     * @param uid
     * @param makingInfo
     * @param zhiBaoId
     * @param essentialsMap
     * @return
     */
    private void essentialsCheck(long uid, CfgKunLS.MakingInfo makingInfo, Integer zhiBaoId, Map<Integer, Integer> essentialsMap) {
        //必需材料是否为空
        if (MapUtil.isEmpty(essentialsMap)) {
            throw new ExceptionForClientTip("kunLS.is.need.essential");
        }
        //材料持有检测
        for (Integer treasureId : essentialsMap.keySet()) {
            int essentialNum = essentialsMap.getOrDefault(treasureId, 0);
            if (0 == essentialNum) {
                continue;
            }
            TreasureChecker.checkIsEnough(treasureId, essentialNum, uid);
        }
        //必需材料是否正确
        List<Integer> essentials = makingInfo.getEssentials();
        List<Integer> noEssentials = essentialsMap.keySet().stream().filter(tmp -> !essentials.contains(tmp.intValue())).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(noEssentials)) {
            throw new ExceptionForClientTip("kunLS.is.not.essential");
        }
        //必需材料数量检测
        Integer needEssentialsNum = makingInfo.getEssentialNum();
        //是否随机材料
        if (makingInfo.getIsRandomEssential()) {
            int useEssentialsNum = essentialsMap.values().stream().mapToInt(Integer::intValue).sum();
            if (needEssentialsNum != useEssentialsNum) {
                throw new ExceptionForClientTip("kunLS.essential.num.error");
            }
            return;
        }
        //指定材料类型
        int needMaterialIndex = zhiBaoId / 1000 - 1;
        int materialId = essentials.get(needMaterialIndex);
        //是否包含指材料
        if (!essentialsMap.containsKey(materialId)) {
            throw new ExceptionForClientTip("kunLS.is.not.essential");
        }
        int useEssentialsNum = essentialsMap.get(materialId);
        TreasureChecker.checkIsEnough(materialId, useEssentialsNum, uid);
        if (needEssentialsNum != useEssentialsNum) {
            throw new ExceptionForClientTip("kunLS.essential.num.error");
        }
        return;
    }

    /**
     * 升品材料检测
     *
     * @param makingInfo
     * @param zhiBaoId
     * @param upgradeMaterialsMap 材料id=>材料数量
     * @return
     */
    private void upgradeMaterialsCheck(long uid, CfgKunLS.MakingInfo makingInfo, Integer zhiBaoId, Map<Integer, Integer> upgradeMaterialsMap) {
        //凡品炼制不需要升品材料
        if (0 == zhiBaoId) {
            return;
        }
        //炼制需要升品材料
        List<Integer> upgradeMaterials = makingInfo.getUpgradeMaterials();
        //升品材料是否为空
        if (MapUtil.isEmpty(upgradeMaterialsMap)) {
            throw new ExceptionForClientTip("kunLS.upgrade.is.emtry");
        }
        //材料持有检测
        for (Integer treasureId : upgradeMaterialsMap.keySet()) {
            int upgradeMaterialNum = upgradeMaterialsMap.getOrDefault(treasureId, 0);
            if (0 == upgradeMaterialNum) {
                continue;
            }
            TreasureChecker.checkIsEnough(treasureId, upgradeMaterialNum, uid);
        }
        //升品材料是否正确
        List<Integer> noUpgradeMaterials = upgradeMaterialsMap.keySet().stream().filter(tmp -> !upgradeMaterials.contains(tmp.intValue())).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(noUpgradeMaterials)) {
            throw new ExceptionForClientTip("kunLS.upgrade.is.error");
        }
        //升品材料数量检测
        List<Integer> needUpgradeMaterialNums = makingInfo.getUpgradeMaterialNums();
        for (int i = 0; i < upgradeMaterials.size(); i++) {
            boolean isHasHpgradeMaterial = upgradeMaterialsMap.containsKey(upgradeMaterials.get(i));
            if (!isHasHpgradeMaterial) {
                throw new ExceptionForClientTip("kunLS.upgrade.is.error");
            }
            int useUpgradeMaterials = upgradeMaterialsMap.get(upgradeMaterials.get(i));
            if (needUpgradeMaterialNums.get(i) != useUpgradeMaterials) {
                throw new ExceptionForClientTip("kunLS.upgrade.num.error");
            }
        }
        return;
    }

    /**
     * 可选材料检测
     *
     * @param makingInfo
     * @param zhiBaoId
     * @param optionalsMap
     * @return
     */
    private void optionalsCheck(long uid, CfgKunLS.MakingInfo makingInfo, Integer zhiBaoId, Map<Integer, Integer> optionalsMap) {
        //炼制是否需要可选材料
        List<Integer> optionals = makingInfo.getOptionals();
        if (ListUtil.isEmpty(optionals)) {
            return;
        }
        //是否有可选材料信息
        if (MapUtil.isEmpty(optionalsMap)) {
            return;
        }
        //材料持有检测
        for (Integer treasureId : optionalsMap.keySet()) {
            int optionalNum = optionalsMap.getOrDefault(treasureId, 0);
            if (0 == optionalNum) {
                continue;
            }
            TreasureChecker.checkIsEnough(treasureId, optionalNum, uid);
        }
        //可选材料是否正确
        List<Integer> noOptionals = optionalsMap.keySet().stream().filter(tmp -> !optionals.contains(tmp.intValue())).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(noOptionals)) {
            throw new ExceptionForClientTip("kunLS.is.not.optional");
        }

        List<Integer> needOptionalNum = makingInfo.getOptionalNum();
        for (int i = 0; i < optionals.size(); i++) {
            boolean isHasOptional = optionalsMap.containsKey(optionals.get(i));
            if (!isHasOptional) {
                throw new ExceptionForClientTip("kunLS.optional.is.error");
            }
            int useOptionalNum = optionalsMap.get(optionals.get(i));
            if (needOptionalNum.get(i) != useOptionalNum) {
                throw new ExceptionForClientTip("kunLS.optional.num.error");
            }
        }
    }

    /**
     * 解析字符串为map
     *
     * @param string
     */
    private Map<Integer, Integer> parameterParsing(String string) {
        if (StringUtils.isBlank(string)) {
            return new HashMap<>();
        }
        String[] StrArr = string.split(";");

        Map<Integer, Integer> map = new HashMap<>();
        for (String strInfo : StrArr) {
            String[] str = strInfo.split(",");
            map.put(Integer.valueOf(str[0]), Integer.valueOf(str[1]));
        }
        return map;
    }


    /**
     * 获得至宝品质
     *
     * @param zhiBaoId
     * @return
     */
    private int getQuality(Integer zhiBaoId) {
        return zhiBaoId % 100;
    }
}
