package com.bbw.god.gameuser.yuxg;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.chengc.in.FaTanService;
import com.bbw.god.game.award.YuxgAward;
import com.bbw.god.game.award.impl.YuXGAwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.gameuser.yuxg.Enum.FuTuTypeEnum;
import com.bbw.god.gameuser.yuxg.cfg.CfgFuTuEntity;
import com.bbw.god.gameuser.yuxg.cfg.CfgFuTuSlotRate;
import com.bbw.god.gameuser.yuxg.cfg.CfgMeltPro;
import com.bbw.god.gameuser.yuxg.cfg.CfgPrayPro;
import com.bbw.god.gameuser.yuxg.rd.RDYuXGPray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 玉虚宫service
 *
 * @author fzj
 * @date 2021/11/13 16:29
 */
@Service
public class YuXGService {
    @Autowired
    UserYuXGService userYuXGService;
    @Autowired
    GameUserService gameUserService;
    @Autowired
    private FaTanService faTanService;
    /** 符图装备位置 */
    private static final Map<String, List<Integer>> FU_TU_POS = new HashMap() {
        private static final long serialVersionUID = -1611395453650822980L;

        {
            put(FuTuTypeEnum.ATTACK_FU_TU.getName(), Arrays.asList(1, 4, 6, 9, 12));
            put(FuTuTypeEnum.DEFENSE_FU_TU.getName(), Arrays.asList(2, 5, 7, 10, 13));
            put(FuTuTypeEnum.BLOOD_FU_TU.getName(), Arrays.asList(0, 8, 14));
            put(FuTuTypeEnum.SKILLS_FU_TU.getName(), Arrays.asList(3, 11));
        }
    };

    /**
     * 判断玉虚宫数据是否正常
     *
     * @param uid
     * @return
     */
    public UserYuXG getOrCreateYuXGData(long uid) {
        UserYuXG userYuXG = userYuXGService.getUserYuXG(uid);
        if (null != userYuXG) {
            return userYuXG;
        }
        userYuXG = UserYuXG.getInstance(uid);
        gameUserService.addItem(uid, userYuXG);
        return userYuXG;
    }

    /**
     * 检查符图是否在已解锁的其他符册中装配
     *
     * @param uid
     * @param fuTuId
     * @return
     */
    public boolean isFuTuInFuCe(long uid, long fuTuId, int fuCeId) {
        List<UserFuCe> userFuCes = userYuXGService.getUserFuCes(uid).stream().filter(f -> f.getBaseId() != fuCeId).collect(Collectors.toList());
        for (UserFuCe userFuCe : userFuCes) {
            UserFuCe.FuTu fuTu = userFuCe.getFuTus().stream().filter(f -> f.getDataId() == fuTuId).findFirst().orElse(null);
            if (null == fuTu) {
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     * 判断是否为当前位置可以装备的符图
     *
     * @param pos
     * @return
     */
    public void checkFuTuTypeInPos(int pos, UserFuTu userFuTu) {
        Integer fuTuType = YuXGTool.getFuTuInFo(userFuTu.getBaseId()).getType();
        FuTuTypeEnum fuTuEnum = FuTuTypeEnum.fromValue(fuTuType);
        if (FU_TU_POS.get(fuTuEnum.getName()).contains(pos)){
            return;
        }
        throw new ExceptionForClientTip("yuXG.fuTu.check.type");
    }

    /**
     * 获得玉髓和符图数量计算提供的基础经验
     *
     * @param quality
     * @param num
     * @return
     */
    public Integer getYuSuiAndFuTuBaseExp(Integer quality, int num) {
        return YuXGTool.getYuXGInfo().getFuTuAndYuSuiSupplyExp().get(quality - 1) * num;
    }

    /**
     * 根据法宝id和数量获得当次熔炼的熔炼值
     *
     * @param treasureId
     * @return
     */
    public Integer getMeltValue(int treasureId, int treasureNum) {
        int treasureStar = TreasureTool.getTreasureById(treasureId).getStar();
        //获取对应法宝的熔炼值
        Integer meltValue = YuXGTool.getYuXGInfo().getMeltPro().stream()
                .filter(t -> t.getTreasureStar() == treasureStar).map(CfgMeltPro::getMeltValue).findFirst().orElse(null);
        if (null == meltValue) {
            throw new ExceptionForClientTip("yuXG.smelt.treasure.check");
        }
        return meltValue * treasureNum;
    }


    /**
     * 判断是否是符图
     *
     * @param treasureId
     * @return
     */
    public boolean isFuTu(int treasureId) {
        return YuXGTool.getAllFuTuInfos().stream()
                .map(CfgFuTuEntity::getFuTuId).collect(Collectors.toList()).contains(treasureId);
    }

    /**
     * 检查符图状态
     *
     * @param useFuTu
     */
    public void checkFuTuStatus(UserFuTu useFuTu) {
        if (useFuTu.getStatus() != 0) {
            throw new ExceptionForClientTip("yuXG.fuTu.check.status");
        }
    }

    /**
     * 检查符图是否已经装备
     *
     * @param userFuCe
     * @param userFuTu
     */
    public void checkFuTuInFuCe(UserFuCe userFuCe, UserFuTu userFuTu) {
        UserFuCe.FuTu fuTu = userFuCe.getFuTus().stream().filter(f -> f.getDataId().equals(userFuTu.getId())).findFirst().orElse(null);
        boolean isInFuCe = null != fuTu;
        if (!isInFuCe) {
            return;
        }
        throw new ExceptionForClientTip("yuXG.fuTu.check.inFuCe");
    }

    /**
     * 检查当前符坛等级与符首等级
     *
     * @param curFuTan
     * @param resultFuTan
     */
    public void checkFuTanLv(int curFuTan, int resultFuTan) {
        if (resultFuTan <= curFuTan) {
            throw new ExceptionForClientTip("yuXG.fuTan.check");
        }
    }

    /**
     * 检查熔炼值
     *
     * @param meltValue
     */
    public void checkMeltValue(int meltValue) {
        if (meltValue < YuXGTool.getYuXGInfo().getGetSparNeedMeltValue()) {
            throw new ExceptionForClientTip("yuXG.smeltValue.check");
        }
    }

    /**
     * 获得符图槽加成
     *
     * @param uid
     * @param fuTuPos
     * @return
     */
    public Integer getFuTuSlotRate(long uid, Integer fuTuPos) {
        //获得已激活卡槽加成的卡槽信息
        int faTanTotalLevel = faTanService.getTotalLevel(uid);
        List<CfgFuTuSlotRate> cfgFuTuSlotRates = YuXGTool.getActivatedFuTuSlotRates(faTanTotalLevel);
        //无已激活卡槽加成的卡槽
        if (ListUtil.isEmpty(cfgFuTuSlotRates)) {
            return 0;
        }
        //获得符图位置对应卡槽信息
        CfgFuTuSlotRate cfgFuTuSlotRate = cfgFuTuSlotRates.stream().filter(tmp -> tmp.getFuTuSlotPos().equals(fuTuPos)).findFirst().orElse(null);
        if (null == cfgFuTuSlotRate) {
            return 0;
        }
        //返回加成
        return cfgFuTuSlotRate.getRate();
    }

    /**
     * 发放奖励
     * @param uid
     * @param meltingInfo
     * @param rd
     */
    public  void deliverTreasure(long uid, CfgPrayPro meltingInfo, RDYuXGPray rd){
        //根据概率获得奖励
        List<Integer> awardsPro = meltingInfo.getAwards().stream().map(YuxgAward::getProbability).collect(Collectors.toList());
        int index = PowerRandom.getIndexByProbs(awardsPro, 10000);
        YuxgAward meltingAwards = meltingInfo.getAwards().get(index);
        //发放奖励
        YuXGAwardService yuXGAwardService = new YuXGAwardService();
        yuXGAwardService.deliverTreasure(uid, meltingAwards, WayEnum.YU_XG_PRAY, rd);
    }
}
