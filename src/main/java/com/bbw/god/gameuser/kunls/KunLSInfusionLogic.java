package com.bbw.god.gameuser.kunls;

import com.bbw.common.MapUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.equipment.UserCardZhiBaoService;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.gameuser.card.equipment.data.UserCardZhiBao;
import com.bbw.god.gameuser.card.equipment.event.CardEquipmentEventPublisher;
import com.bbw.god.gameuser.card.equipment.rd.RdCardZhiBao;
import com.bbw.god.gameuser.kunls.Enum.InfusionPositionEnum;
import com.bbw.god.gameuser.kunls.data.UserInfusionInfo;
import com.bbw.god.gameuser.kunls.rd.RdInfusionInfo;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 昆仑山注灵室逻辑
 *
 * @author: huanghb
 * @date: 2022/9/15 11:33
 */
@Slf4j
@Service
public class KunLSInfusionLogic {
    @Autowired
    private KunLSInfusionService kunLSInfusionService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardZhiBaoService userCardZhiBaoService;


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
     * 进入注灵室
     *
     * @param uid
     * @return
     */
    protected RdInfusionInfo enterInfusion(long uid) {
        UserInfusionInfo userInfusionInfo = kunLSInfusionService.getUserInfusionInfo(uid);
        return RdInfusionInfo.getInstance(userInfusionInfo);
    }

    /**
     * 注灵
     *
     * @param uid
     * @param embryoType
     * @param optionals
     * @return
     */
    protected RdInfusionInfo infusion(long uid, Integer embryoType, String optionals, Integer infusionPosition) {
        List<Integer> allZhiBaoType = CfgKunLSTool.getAllZhiBaoType();
        //至宝胚类型检测
        if (0 != embryoType && !allZhiBaoType.contains(embryoType)) {
            throw new ExceptionForClientTip("zhiBao.is.error.type");
        }
        //参数格式化 材料id=》材料数量
        Map<Integer, Integer> optionalsMap = parameterParsing(optionals);
        UserInfusionInfo userInfusionInfo = kunLSInfusionService.getUserInfusionInfo(uid);
        int currentInfusionTimes = userInfusionInfo.getCurrentInfusionTimes();
        //注灵次数检测
        Integer infusionTimesLimit = CfgKunLSTool.getInfusionTimesLimit();
        if (infusionTimesLimit < currentInfusionTimes) {
            throw new ExceptionForClientTip("kunLs.infusion.times.limit");
        }
        //注灵位置检测
        if (infusionPosition > currentInfusionTimes) {
            throw new ExceptionForClientTip("kunLs.infusion.position.out");
        }
        //检测注灵石是否足够
        int infusionNeedTreasureId = CfgKunLSTool.getInfusionNeedTreasureId();
        int infusionNeedTreasureNum = CfgKunLSTool.getInfusionNeedTreasureNum(userInfusionInfo.getInfusionTimes());
        TreasureChecker.checkIsEnough(infusionNeedTreasureId, infusionNeedTreasureNum, uid);
        //本源灵气检测
        if (MapUtil.isNotEmpty(optionalsMap)) {
            for (Integer treasureId : optionalsMap.keySet()) {
                int optionalNum = optionalsMap.getOrDefault(treasureId, 0);
                if (0 == optionalNum) {
                    continue;
                }
                TreasureChecker.checkIsEnough(treasureId, optionalNum, uid);
            }
        }
        //第一次注灵
        if (0 == userInfusionInfo.getInfusionTimes()) {
            firstInfusionHandle(uid, userInfusionInfo, embryoType, optionalsMap);
        }
        //生成技能
        if (InfusionPositionEnum.INFUSION_POSITION_ONE.getValue() != infusionPosition) {
            Integer skill = CfgKunLSTool.randomInfusionSkill(embryoType, infusionPosition);
            userInfusionInfo.infusionSKill(infusionPosition, skill);
        }
        //生成属性
        if (InfusionPositionEnum.INFUSION_POSITION_ONE.getValue() == infusionPosition) {
            List<CardEquipmentAddition> additions = CfgKunLSTool.getInfusionProperty(embryoType);
            userInfusionInfo.setAdditions(additions);
        }
        userInfusionInfo.addInfusionTimes();
        kunLSInfusionService.cacheInfusionInfo(userInfusionInfo);
        RdInfusionInfo rd = RdInfusionInfo.getInstance(userInfusionInfo);
        //扣除注灵石
        TreasureEventPublisher.pubTDeductEvent(uid, infusionNeedTreasureId, infusionNeedTreasureNum, WayEnum.KUNLS_INFUSION, rd);
        //扣除本源灵气
        if (MapUtil.isNotEmpty(optionalsMap)) {
            for (Integer treasureId : optionalsMap.keySet()) {
                TreasureEventPublisher.pubTDeductEvent(uid, treasureId, optionalsMap.get(treasureId), WayEnum.KUNLS_INFUSION, rd);
            }
        }
        return rd;
    }

    /**
     * 第一次注灵处理
     *
     * @param userInfusionInfo
     * @param embryoType
     * @param optionalsMap     材料id=》材料数量
     */
    private void firstInfusionHandle(long uid, UserInfusionInfo userInfusionInfo, Integer embryoType, Map<Integer, Integer> optionalsMap) {
        int optionalsTypeNum = 0;
        if (0 != optionalsMap.size()) {
            optionalsTypeNum = (int) optionalsMap.values().stream().filter(tmp -> 0 != tmp.intValue()).count();
        }
        //注灵材料种类数量检测
        if (CfgKunLSTool.getPropertyTypeLimit() < optionalsTypeNum) {
            throw new ExceptionForClientTip("kunLs.infusion.propertyType.limit");
        }
        //可选材料数量
        int optionalsMaterialNum = optionalsMap.values().stream().mapToInt(Integer::intValue).sum();
        if (CfgKunLSTool.getPropertyTrendMaterialNumLimit() < optionalsMaterialNum) {
            throw new ExceptionForClientTip("kunLS.optional.num.error");
        }
        //设置至宝胚id
        userInfusionInfo.setEmbryoType(embryoType);
        //获得属性
        int property = CfgKunLSTool.getPropertyTrendProbs(optionalsMap);
        userInfusionInfo.setProperty(property);
        //获得品质
        int quality = getQuality(embryoType);
        userInfusionInfo.setQuality(quality);
        kunLSInfusionService.addInfusionInfo(uid, userInfusionInfo);
        return;
    }

    /**
     * 获得至宝品质
     *
     * @param embryoType
     * @return
     */
    private int getQuality(Integer embryoType) {
        return embryoType % 100;
    }

    /**
     * 至宝出世
     *
     * @param uid
     * @return
     */
    protected RdCardZhiBao born(long uid) {
        //获得注灵信息
        UserInfusionInfo userInfusionInfo = kunLSInfusionService.getUserInfusionInfo(uid);
        //初始化至宝信息
        UserCardZhiBao userCardZhiBao = UserCardZhiBao.getInstance(uid, userInfusionInfo.getEmbryoType());
        //至宝出世
        userCardZhiBao.born(userInfusionInfo);

        RdCardZhiBao rd = RdCardZhiBao.instance(userCardZhiBao);
        //获得至宝胚id
        int treasireId = CfgKunLSTool.getEmbryoTreasureIds(userInfusionInfo.getEmbryoType());
        //扣除道具
        TreasureEventPublisher.pubTDeductEvent(uid, treasireId, 1, WayEnum.KUNLS_INFUSION, rd);
        //清楚注灵信息
        kunLSInfusionService.cleanInfusionInfo(userInfusionInfo);
        //添加至宝
        userCardZhiBaoService.cacheZhiBao(userCardZhiBao);
//        log.info("{}添加新的灵宝{}", uid, userInfusionInfo.toString());
        //发布至宝添加事件

        CardEquipmentEventPublisher.pubZhiBaoAddEvent(uid, WayEnum.KUNLS_INFUSION, userCardZhiBao.getZhiBaoId(),
                userCardZhiBao.getProperty(), userCardZhiBao.isFullAttack(), userCardZhiBao.isFullDefense());
        //初始化
        return rd;
    }
}
