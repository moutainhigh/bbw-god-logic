package com.bbw.god.gameuser.yuxg;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.gameuser.yuxg.Enum.FuTuTypeEnum;
import com.bbw.god.gameuser.yuxg.cfg.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 玉虚宫工具类
 *
 * @author fzj
 * @date 2021/10/29 13:57
 */
public class YuXGTool {
    /**
     * 获取所有符图信息
     *
     * @return
     */
    public static List<CfgFuTuEntity> getAllFuTuInfos() {
        return Cfg.I.get(CfgFuTuEntity.class);
    }

    /**
     * 获得所有玉虚宫除外符图
     *
     * @return
     */
    public static List<Integer> getYxGExceptFuTus() {
        return Cfg.I.get(CfgYuXGExceptFuTuEntity.class).stream().map(CfgYuXGExceptFuTuEntity::getFutuId).collect(Collectors.toList());
    }

    /**
     * 根据符图id获取符图
     *
     * @return
     */
    public static CfgFuTuEntity getFuTuInFo(int fuTuId) {
        return getAllFuTuInfos().stream().filter(fu -> fu.getFuTuId() == fuTuId).findFirst().orElse(null);
    }

    public static List<Integer> getFutuIds(FuTuTypeEnum tuTypeEnum, int num) {
        List<Integer> fuTuIds = new ArrayList<>();
        List<CfgFuTuEntity> cfgFuTuEntity = getAllFuTuInfos().stream().filter(fu -> fu.getType() == tuTypeEnum.getType()).collect(Collectors.toList());
        for (int i = 0; i < num; i++) {
            //打乱顺序
            Collections.shuffle(cfgFuTuEntity);
            fuTuIds.add(cfgFuTuEntity.get(0).getFuTuId());
        }
        return fuTuIds;
    }

    public static List<Integer> getFutuIds(FuTuTypeEnum tuTypeEnum, int num, List<Integer> qualitys, List<Integer> filterFutuIds) {
        List<CfgFuTuEntity> cfgFuTuEntity = getAllFuTuInfos().stream().filter(fu -> fu.getType() == tuTypeEnum.getType() && qualitys.contains(fu.getQuality())).collect(Collectors.toList());
        List<Integer> futuIds = cfgFuTuEntity.stream().map(CfgFuTuEntity::getFuTuId).collect(Collectors.toList());
        futuIds.addAll(filterFutuIds);
        futuIds.removeAll(filterFutuIds);
        return PowerRandom.getRandomsFromList(futuIds, num);

    }
    public static boolean loop(List<Integer> list, Integer value) {
        for (Integer integer : list) {
            if (Objects.equals(integer, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有玉髓信息
     *
     * @return
     */
    public static List<CfgYuSuiEntity> getAllYuSuiInfos() {
        return Cfg.I.get(CfgYuSuiEntity.class);
    }

    /**
     * 获取指定品阶的玉髓
     *
     * @param quality
     * @return
     */
    public static CfgYuSuiEntity getYuSuiInfo(int quality) {
        return getAllYuSuiInfos().stream().filter(yu -> yu.getQuality() == quality).findFirst().orElse(null);
    }

    /**
     * 根据id指定的玉髓
     *
     * @param yuSuiId
     * @return
     */
    public static CfgYuSuiEntity getYuSui(int yuSuiId) {
        return getAllYuSuiInfos().stream().filter(yu -> yu.getYuSuiId() == yuSuiId).findFirst().orElse(null);
    }

    /**
     * 获取配置类
     *
     * @return
     */
    public static CfgYuXGEntity getYuXGInfo() {
        return Cfg.I.getUniqueConfig(CfgYuXGEntity.class);
    }

    /**
     * 获取符图槽数量
     *
     * @param faTanTotalLv
     * @return
     */
    public static Integer getFuTuSlotNum(Integer faTanTotalLv) {
        List<CfgFuTuSlotNum> cfgFuTuSlotNums = getYuXGInfo().getFuTuSlotNumAndFaTanAllLv();
        Integer fuTuSlotNums = cfgFuTuSlotNums.stream().filter(f -> f.getFaTanAllLv() <= faTanTotalLv)
                .map(CfgFuTuSlotNum::getFuTuSlotNum).findFirst().orElse(null);
        return fuTuSlotNums == null ? 4 : fuTuSlotNums;
    }

    /**
     * 获取法坛等级对应的效果
     * @param faTanTotalLv
     * @return
     */
    public static CfgFaTanAllLvEffect getFaTanAllLvEffect(int faTanTotalLv){
        List<CfgFaTanAllLvEffect> faTanAllLvEffects = getYuXGInfo().getFaTanAllLvEffects();
        return faTanAllLvEffects.stream().filter(cfg -> faTanTotalLv >= cfg.getFaTanLv()).findFirst().orElse(null);
    }

    /**
     * 获取需要的许愿需要的数量
     * @param faTanTotalLv
     * @return
     */
    public static int getWishingFuTuNum(int faTanTotalLv){
        CfgFaTanAllLvEffect faTanAllLvEffect = getFaTanAllLvEffect(faTanTotalLv);
        if (null == faTanAllLvEffect) {
            //获取默认的符图数量
            return getYuXGInfo().getDefaultFutuNum();
        }
        //最终许愿需要的符图数量
        return  faTanAllLvEffect.gainReduceFuTuNum();
    }

    /**
     * 获取许愿值（最大必得的许愿值）
     * @param faTanTotalLv
     * @return
     */
    public static int getWishingValue(int faTanTotalLv){
        CfgFaTanAllLvEffect faTanAllLvEffect = getFaTanAllLvEffect(faTanTotalLv);
        if (null == faTanAllLvEffect) {
            // 获取默认的许愿值
           return  getYuXGInfo().getDefaultwishingValue();
        }
        //最终许愿需要的最大值
        return faTanAllLvEffect.gainReduceWishingValue();
    }

    /**
     * 获得祈福信息
     *
     * @param sparId
     * @param curFuTan
     * @param faTanAllLv
     * @return
     */
    public static CfgPrayPro getMeltingInfo(int sparId, int curFuTan,int faTanAllLv) {
        CfgPrayPro cfgPrayPro = new CfgPrayPro();
        //获取法坛等级对应的消息
        CfgFaTanAllLvEffect faTanAllLvEffect = getFaTanAllLvEffect(faTanAllLv);
        //获取要提升的符坛
        if (null != faTanAllLvEffect) {
            List<Integer> fuTanLiftingProbability = faTanAllLvEffect.gainFuTanLiftingProbability();
            if (fuTanLiftingProbability.contains(curFuTan)) {
                cfgPrayPro = getYuXGInfo().getPrayProRuleTwo()
                        .stream().filter(fu -> fu.getSparId() == sparId && fu.getFuTan() == curFuTan).findFirst().orElse(null);
                if (cfgPrayPro == null) {
                    throw new ExceptionForClientTip("yuXG.smelt.check.awards");
                }
                return cfgPrayPro;
            }
        }
        cfgPrayPro = getYuXGInfo().getPrayProRuleOne()
                .stream().filter(fu -> fu.getSparId() == sparId && fu.getFuTan() == curFuTan).findFirst().orElse(null);
        if (cfgPrayPro == null) {
            throw new ExceptionForClientTip("yuXG.smelt.check.awards");
        }
        return cfgPrayPro;
    }

    /**
     * 获得已激活卡槽加成的卡槽信息
     *
     * @param faTanTotalLv
     * @return
     */
    public static List<CfgFuTuSlotRate> getActivatedFuTuSlotRates(Integer faTanTotalLv) {
        List<CfgFuTuSlotRate> cfgFuTuSlotRates = getYuXGInfo().getFuTuSlotRates();
        return cfgFuTuSlotRates.stream().filter(tmp -> faTanTotalLv >= tmp.getFaTanTotalLevel()).collect(Collectors.toList());
    }

    /**
     * 获得指定品质之上的所有符图（包括自身）
     *
     * @param fuTuQuality 符图品质
     * @return
     */
    public static List<CfgFuTuEntity> getFuTuByMinQuality(int fuTuQuality) {
        return getAllFuTuInfos().stream().filter(tmp -> tmp.getQuality() >= fuTuQuality).collect(Collectors.toList());
    }
}
