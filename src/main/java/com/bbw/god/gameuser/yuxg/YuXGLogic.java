package com.bbw.god.gameuser.yuxg;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.common.Rst;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.chengc.in.FaTanService;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.award.YuxgAward;
import com.bbw.god.game.award.impl.YuXGAwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.statistic.behavior.fatan.FaTanStatistucService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.gameuser.yuxg.Enum.FuShouEnum;
import com.bbw.god.gameuser.yuxg.Enum.SparEnum;
import com.bbw.god.gameuser.yuxg.cfg.CfgMeltPro;
import com.bbw.god.gameuser.yuxg.cfg.CfgPrayPro;
import com.bbw.god.gameuser.yuxg.rd.*;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 玉虚宫逻辑类
 *
 * @author fzj
 * @date 2021/11/1 18:07
 */
@Service
@Slf4j
public class YuXGLogic {
    @Autowired
    GameUserService gameUserService;
    @Autowired
    FaTanStatistucService faTanStatistucService;
    @Autowired
    UserYuXGService userYuXGService;
    @Autowired
    AwardService awardService;
    @Autowired
    FaTanService faTanService;
    @Autowired
    YuXGService yuXGService;

    /**
     * 进入玉虚宫
     *
     * @param uid
     * @return
     */
    public RDYuXGEnter enter(long uid) {
        int faTanTotalLevel = faTanService.getTotalLevel(uid);
        if (faTanTotalLevel < 10) {
            throw new ExceptionForClientTip("yuXG.unlock.tip");
        }
        UserYuXG userYuXG = yuXGService.getOrCreateYuXGData(uid);
        //初始化许愿清单
        if (faTanTotalLevel >= 850 && ListUtil.isEmpty(userYuXG.getWishingDetailed())) {
            UserYuXGWishingDetailed userYuXGWishingDetailed = new UserYuXGWishingDetailed();
            userYuXG.setWishingDetailed(userYuXGWishingDetailed.instance());
            gameUserService.updateItem(userYuXG);
        }

        fuTanNumFix(userYuXG, faTanTotalLevel);
        //回传客户端
        RDYuXGEnter rd = new RDYuXGEnter();
        rd.setFaTanTotalLevel(faTanTotalLevel);
        rd.setMeltValue(userYuXG.getMeltValue());
        rd.setCurfuTan(userYuXG.getCurFuTan());
        rd.setActiveShenShui(userYuXG.getActiveShenShui());
        double addition = faTanService.addition(uid);
        rd.setAddition(addition);
        int praySetting = getYuXGPraySetting(uid);
        rd.setPraySetting(praySetting);
        if (faTanTotalLevel >= 850) {
            List<RdWishingDetailed.RdDetailed> rdWishingDetaileds = RdWishingDetailed.RdDetailed.getWishingDetailedExcludeFuTu(userYuXG.gainWishingDetaileds());
//            rdWishingDetaileds =  ListUtil.isEmpty(rdWishingDetaileds) ? RdWishingDetailed.instance() : rdWishingDetaileds;
            rd.setWishingDetailed(rdWishingDetaileds);
        }
        return rd;
    }

    /**
     * 符坛矫正
     *
     * @param userYuXG
     * @param faTanTotalLevel
     */
    private void fuTanNumFix(UserYuXG userYuXG, int faTanTotalLevel) {
        if (faTanTotalLevel < 20) {
            return;
        }
        int fuTan = userYuXG.getFuTan().size();
        if (faTanTotalLevel < 30 && fuTan < 4) {
            userYuXG.getFuTan().add(fuTan + 1);
            gameUserService.updateItem(userYuXG);
            return;
        }
        if (fuTan >= 5) {
            return;
        }
        if (faTanTotalLevel >= 30) {
            int need = Math.max(5 - fuTan, 0);
            for (int i = 1; i <= need; i++) {
                userYuXG.getFuTan().add(fuTan + i);
            }
            gameUserService.updateItem(userYuXG);
        }
    }

    /**
     * 祈福
     *
     * @param uid
     * @param sparId
     */
    public RDYuXGPray pray(long uid, int sparId) {
        UserYuXG userYuXG = yuXGService.getOrCreateYuXGData(uid);
        int faTanTotalLevel = faTanService.getTotalLevel(uid);
        //当前符坛
        Integer curFuTan = userYuXG.getCurFuTan();
        CfgPrayPro meltingInfo = YuXGTool.getMeltingInfo(sparId, curFuTan,faTanTotalLevel);
        Integer needSparNum = meltingInfo.getPrayConsumeSparNum();
        //检查晶石数量
        TreasureChecker.checkIsEnough(sparId, needSparNum, uid);
        //获取祈福结果
        int nextFuTan;
        int useShenShui = 1;
        if (userYuXG.getActiveShenShui() == useShenShui){
            // 使用玉虚神水祈福结果
            nextFuTan = getShenShuiSparRes(userYuXG);
            // 更新神水状态
            int noUseShenShui = 0;
            userYuXG.setActiveShenShui(noUseShenShui);
        }else {
            // 普通祈福结果
            nextFuTan = getSparRes(sparId, userYuXG);
        }

        log.info("本次祈福结果为:{}", nextFuTan);
        RDYuXGPray rd = new RDYuXGPray();
        //扣除道具
        TreasureEventPublisher.pubTDeductEvent(uid, sparId, needSparNum, WayEnum.YU_XG_PRAY, rd);
        //添加许愿值
        UserYuXGWishingDetailed userYuXGWishingDetailed = userYuXG.getUserYuXGWishingDetailed(curFuTan);
        if (null != userYuXGWishingDetailed) {
            userYuXGWishingDetailed.addWishingValue(faTanTotalLevel);
        }
        //更新符坛数据
        userYuXG.setCurFuTan(nextFuTan);
        //判断是否可以领取许愿清单的奖励
        if (null != userYuXGWishingDetailed && userYuXGWishingDetailed.getWishingValue() > YuXGTool.getWishingValue(faTanTotalLevel)){
            List<Integer> fuTuIds = userYuXGWishingDetailed.getFuTuIds();
            if (ListUtil.isNotEmpty(fuTuIds)) {
                Integer fuTuId = PowerRandom.getRandomFromList(fuTuIds);
                TreasureEventPublisher.pubTAddFuTuEvent(uid, fuTuId, 1, WayEnum.YUXU_PALACE_COIN_WISHING_OUTPUT, rd);
                userYuXGWishingDetailed.receiveMustGetAward();
            }else {
                //发送奖励
                yuXGService.deliverTreasure(uid,meltingInfo,rd);
            }
        } else {
            //发送奖励
            yuXGService.deliverTreasure(uid,meltingInfo,rd);
        }
        rd.setNextFuTan(nextFuTan);
        //符图许愿清单
        List<RdWishingDetailed.RdDetailed> rdWishingDetaileds = RdWishingDetailed.RdDetailed.getWishingDetailedExcludeFuTu(userYuXG.gainWishingDetaileds());
        rd.setWishingDetailed(rdWishingDetaileds);

        gameUserService.updateItem(userYuXG);

        return rd;
    }

    /**
     * 获取祈福设置
     *
     * @param uid
     * @return
     */
    public Integer getYuXGPraySetting(long uid) {
        UserYuXGPraySetting setting = userYuXGService.getCurUserSpecialSetting(uid);
        return setting.getUserYuXGPraySetting();
    }

    /**
     * 更新祈福设置
     *
     * @param uid 玩家id
     * @return
     */
    public Rst updateYuXGPraySetting(long uid, int cpUserYuXGPraySeting) {
        //特产一键状态设置
        UserYuXGPraySetting setting = userYuXGService.getCurUserSpecialSetting(uid);
        setting.updateUserYuXGPraySetting(cpUserYuXGPraySeting);
        //更新特产设置
        gameUserService.updateItem(setting);
        return Rst.businessOK();
    }

    /**
     * 使用符首改变符坛
     *
     * @param uid
     * @param fuShouId
     * @return
     */
    public RDYuXGPray changeFuTan(long uid, int fuShouId) {
        UserYuXG userYuXG = yuXGService.getOrCreateYuXGData(uid);
        //检查符首数量
        TreasureChecker.checkIsEnough(fuShouId, 1, uid);
        int resultFuTan = FuShouEnum.fromValue(fuShouId).getFuTan();
        log.info("本次使用符首结果为:{}", resultFuTan);
        //检查当前符坛等级与使用符首的等级
        yuXGService.checkFuTanLv(userYuXG.getCurFuTan(), resultFuTan);
        RDYuXGPray rd = new RDYuXGPray();
        //扣除道具
        TreasureEventPublisher.pubTDeductEvent(uid, fuShouId, 1, WayEnum.YU_XG_PRAY, rd);
        //更新符坛数据
        userYuXG.setCurFuTan(resultFuTan);
        gameUserService.updateItem(userYuXG);
        rd.setNextFuTan(resultFuTan);
        return rd;
    }

    /**
     * 熔炼
     *
     * @param uid
     */
    public RDYuXGMelt melt(long uid, String smeltGoods) {
        //检查法宝
        if ("".equals(smeltGoods)) {
            return new RDYuXGMelt();
        }
        //TODO 待优化检测
        Arrays.asList(smeltGoods.split(",")).forEach(s -> {
            int treasureId = Integer.parseInt(s.split("_")[0]);
            TreasureChecker.checkIsEnough(treasureId, Integer.parseInt(s.split("_")[1]), uid);
        });
        UserYuXG userYuXG = yuXGService.getOrCreateYuXGData(uid);
        //熔炼值
        Integer meltValue = userYuXG.getMeltValue();
        log.info("熔炼前的熔炼值:{}", meltValue);
        List<Award> smeltAwards = new ArrayList<>();
        RDYuXGMelt rd = new RDYuXGMelt();
        String[] smeltTreasures = smeltGoods.split(",");
        for (String smeltTreasure : smeltTreasures) {
            String[] treasure = smeltTreasure.split("_");
            int treasureId = Integer.parseInt(treasure[0]);
            int treasureNum = Integer.parseInt(treasure[1]);
            //扣除道具
            //TODO 待优化批量扣除
            TreasureEventPublisher.pubTDeductEvent(uid, treasureId, treasureNum, WayEnum.YU_XG_MELT, rd);
            //统计获取道具
            smeltAwards.addAll(getMeltAwards(treasureId, treasureNum));
            //计算熔炼值
            meltValue += yuXGService.getMeltValue(treasureId, treasureNum);
        }
        Map<Integer, Integer> allSmeltAwards = smeltAwards.stream()
                .collect(Collectors.groupingBy(Award::getAwardId, Collectors.summingInt(Award::getNum)));
        //发放熔炼产出
        awardService.fetchAward(uid, Award.getAwards(allSmeltAwards, AwardEnum.FB.getValue()), WayEnum.YU_XG_MELT, "在玉虚宫熔炼中获得", rd);
        log.info("当前熔炼值:{}", meltValue);
        //更新符坛数据
        userYuXG.setMeltValue(meltValue);
        gameUserService.updateItem(userYuXG);
        rd.setResultMeltValue(meltValue);
        return rd;
    }

    /**
     * 升级符图
     *
     * @param uid
     * @param yuSuis
     * @param fuTus
     * @param fuTuDataId
     * @return
     */
    public RDYuXGFuTuUpdate updateFuTu(long uid, String yuSuis, String fuTus, long fuTuDataId) {
        UserFuTu userFuTu = userYuXGService.getUserFuTu(uid, fuTuDataId);
        RDYuXGFuTuUpdate rd = new RDYuXGFuTuUpdate();
        //使用玉髓升级
        long yuSuisExp = useYuSuiUpFuTu(uid, yuSuis, rd);
        //使用符图升级
        long fuTusExp = useFuTuUpFuTu(uid, fuTus, rd);
        //获取升级后的总经验
        long totalExp = userFuTu.getExp() + yuSuisExp + fuTusExp;
        //获得等级
        int lv = getPromoteLevel(userFuTu.getBaseId(), totalExp);
        log.info("符图升级后的经验：{},等级：{}", totalExp, lv);
        //更新数据
        userFuTu.setExp(totalExp);
        userFuTu.setLv(lv);
        gameUserService.updateItem(userFuTu);
        //回传客户端
        rd.setExp(totalExp);
        rd.setLevel(lv);
        return rd;
    }

    /**
     * 使用符图升级符图
     *
     * @param uid
     * @param fuTus
     */
    private long useFuTuUpFuTu(long uid, String fuTus, RDYuXGFuTuUpdate rd) {
        long totalExp = 0;
        if (fuTus.isEmpty()) {
            return totalExp;
        }
        String[] fuTuIds = fuTus.split(",");
        //TODO 待优化批量检测
        for (String fuTu : fuTuIds) {
            long fuTuId = Long.parseLong(fuTu);
            UserFuTu useFuTu = userYuXGService.getUserFuTu(uid, fuTuId);
            //检查符图当前状态
            yuXGService.checkFuTuStatus(useFuTu);
            //获取基础经验
            int baseExp = yuXGService.getYuSuiAndFuTuBaseExp(YuXGTool.getFuTuInFo(useFuTu.getBaseId()).getQuality(), 1);
            //获取加成经验
            totalExp += (baseExp + useFuTu.getExp()) * 0.8;
            //扣除符图
            TreasureEventPublisher.pubTDeductFuTuEvent(uid, useFuTu, 1, WayEnum.YU_XG_UP_FUTU, rd);
        }
        return totalExp;
    }

    /**
     * 使用玉髓升级符图
     *
     * @param uid
     * @param yuSuis
     * @param rd
     */
    private long useYuSuiUpFuTu(long uid, String yuSuis, RDYuXGFuTuUpdate rd) {
        long totalExp = 0;
        if (yuSuis.isEmpty()) {
            return totalExp;
        }
        //检查玉髓
        //TODO 待优化批量检测
        Arrays.asList(yuSuis.split(",")).forEach(s -> {
            int treasureId = Integer.parseInt(s.split("_")[0]);
            TreasureChecker.checkIsEnough(treasureId, Integer.parseInt(s.split("_")[1]), uid);
        });
        String[] yuSuiList = yuSuis.split(",");
        for (String yuSui : yuSuiList) {
            String[] useYuSuis = yuSui.split("_");
            int yuSuiId = Integer.parseInt(useYuSuis[0]);
            int yuSuiNum = Integer.parseInt(useYuSuis[1]);
            //获取基础经验
            int baseExp = yuXGService.getYuSuiAndFuTuBaseExp(YuXGTool.getYuSui(yuSuiId).getQuality(), yuSuiNum);
            //获得加成经验
            totalExp += baseExp;
            //扣除道具
            //TODO 待优化扣除道具
            TreasureEventPublisher.pubTDeductEvent(uid, yuSuiId, yuSuiNum, WayEnum.YU_XG_UP_FUTU, rd);
        }
        return totalExp;
    }

    /**
     * 符图升级设置
     *
     * @param uid
     * @param settings 设置标识1,设置标识2
     */
    public RDSuccess setting(long uid, String settings) {
        UserFuTuUpSetting userFuTuUpSetting = userYuXGService.getUserFuTuUpSetting(uid);
        //升级设置
        String[] sets = settings.split(",");
        for (String set : sets) {
            String[] setting = set.split("_");
            //升级设置
            userFuTuUpSetting.getSettings().put(setting[0], Integer.valueOf(setting[1]));
        }
        gameUserService.updateItem(userFuTuUpSetting);
        return new RDSuccess();
    }

    /**
     * 获取符图升级设置
     *
     * @param uid
     */
    public RDYuXGSetting getSetting(long uid) {
        UserFuTuUpSetting userFuTuUpSetting = userYuXGService.getUserFuTuUpSetting(uid);
        if (null == userFuTuUpSetting) {
            userFuTuUpSetting = UserFuTuUpSetting.getInstance(uid);
            gameUserService.addItem(uid, userFuTuUpSetting);
        }
        List<RDYuXGSetting.RDSetting> rdSettings = new ArrayList<>();
        //设置标识
        for (Map.Entry<String, Integer> setting : userFuTuUpSetting.getSettings().entrySet()) {
            RDYuXGSetting.RDSetting rdSetting = RDYuXGSetting.getInstance(setting);
            rdSettings.add(rdSetting);
        }
        //回传到客户端
        RDYuXGSetting rd = new RDYuXGSetting();
        rd.setSettings(rdSettings);
        return rd;
    }

    /**
     * 改变符图状态
     *
     * @param uid
     * @param dataId
     */
    public RDSuccess changeFuTuStatus(long uid, long dataId, int status) {
        UserFuTu userFuTu = userYuXGService.getUserFuTu(uid, dataId);
        //改变符图状态
        userFuTu.setStatus(status);
        gameUserService.updateItem(userFuTu);
        return new RDSuccess();
    }


    /**
     * 获取符册
     *
     * @param uid
     * @return
     */
    public RDYuXGFuCes getFuCes(long uid) {
        int faTanTotalLevel = faTanService.getTotalLevel(uid);
        List<UserFuCe> userFuCes = userYuXGService.getUserFuCes(uid);
        if (ListUtil.isEmpty(userFuCes)) {
            //获取符图槽数量
            Integer fuTuSlotNum = YuXGTool.getFuTuSlotNum(faTanTotalLevel);
            //默认开启两个符册
            for (int index = 1; index <= 2; index++) {
                UserFuCe userFuCe = UserFuCe.getInstance(uid, "符册" + index, 0, index, fuTuSlotNum);
                userFuCes.add(userFuCe);
            }
            gameUserService.addItems(userFuCes);
        }
        UserYuXG userYuXG = yuXGService.getOrCreateYuXGData(uid);
        //回传客户端
        RDYuXGFuCes rd = new RDYuXGFuCes();
        rd.setFuCes(getRDFuCes(userFuCes));
        rd.setFaTanLv(faTanTotalLevel);
        rd.setMeltValue(userYuXG.getMeltValue());
        return rd;
    }

    /**
     * 替换符图
     *
     * @param uid
     * @param pos
     * @param targetFuTuDataId
     * @return
     */
    public RDFuTus replaceFuTu(long uid, int pos, Long targetFuTuDataId, int fuCeId) {
        UserFuCe userFuCe = userYuXGService.getFuCe(uid, fuCeId);
        //获取对应位置的符图
        UserFuCe.FuTu fuTuInFuCe = userFuCe.getFuTus().stream().filter(ru -> ru.getPos() == pos).findFirst().orElse(null);
        if (null == fuTuInFuCe) {
            throw new ExceptionForClientTip("yuXG.fuTu.check.pos");
        }
        long fuTuId = fuTuInFuCe.getDataId();
        //判断是否为当前位置可以装备的符图
        UserFuTu targetFuTu = userYuXGService.getUserFuTu(uid, targetFuTuDataId);
        //检查符图是否已经在该符册中装备
        yuXGService.checkFuTuInFuCe(userFuCe, targetFuTu);
        yuXGService.checkFuTuTypeInPos(pos, targetFuTu);
        RDFuTus rd = new RDFuTus();
        //改变原符图的状态
        if (fuTuId != 0 && !yuXGService.isFuTuInFuCe(uid, fuTuId, userFuCe.getBaseId())) {
            UserFuTu replacedFuTu = userYuXGService.getUserFuTu(uid, fuTuId);
            replacedFuTu.setStatus(0);
            gameUserService.updateItem(replacedFuTu);
            rd.getFuTuId().add(fuTuId);
        }
        //改变替换符图状态
        targetFuTu.setStatus(2);
        gameUserService.updateItem(targetFuTu);
        //替换符图
        fuTuInFuCe.setDataId(targetFuTuDataId);
        gameUserService.updateItem(userFuCe);
        return rd;
    }

    /**
     * 一键拆卸
     *
     * @param uid
     * @param fuCeId
     * @return
     */
    public RDFuTus batchReplaceFuTus(long uid, int fuCeId) {
        UserFuCe userFuCe = userYuXGService.getFuCe(uid, fuCeId);
        List<UserFuTu> userFuTus = new ArrayList<>();
        RDFuTus rd = new RDFuTus();
        for (UserFuCe.FuTu fuTu : userFuCe.getFuTus()) {
            long fuTuId = fuTu.getDataId();
            fuTu.setDataId(0L);
            //检查符图是否在其他符册装备
            //TODO 待优化，性能问题
            if (0 == fuTuId || yuXGService.isFuTuInFuCe(uid, fuTuId, userFuCe.getBaseId())) {
                continue;
            }
            //改变符图状态
            UserFuTu userFuTu = userYuXGService.getUserFuTu(uid, fuTuId);
            userFuTu.setStatus(0);
            userFuTus.add(userFuTu);
            rd.getFuTuId().add(fuTuId);
        }
        gameUserService.updateItems(userFuTus);
        gameUserService.updateItem(userFuCe);
        return rd;
    }

    /**
     * 使用元宝解锁新的符册
     *
     * @param uid
     * @return
     */
    public RDCommon unlockFuCe(long uid) {
        GameUser gu = gameUserService.getGameUser(uid);
        //检查用元宝开启符册的数量
        long useGoldOpenedFuCeNum = userYuXGService.getUserFuCes(uid).stream()
                .filter(userFuCe -> userFuCe.getOpenMethod() == 2).count();
        if (useGoldOpenedFuCeNum > 5) {
            throw new ExceptionForClientTip("yuXG.fuCe.opened.num");
        }
        RDCommon rd = new RDCommon();
        //检查,扣除元宝数量
        int needPay = YuXGTool.getYuXGInfo().getUnlockNewFuCeNeedGold();
        ResChecker.checkGold(gu, needPay);
        ResEventPublisher.pubGoldDeductEvent(uid, needPay, WayEnum.YU_XG_UNLOCK_FUCE, rd);
        //开启新的符册
        int newFuCeNum = userYuXGService.getUserFuCes(uid).size() + 1;
        //获取符图槽数量
        int faTanTotalLevel = faTanService.getTotalLevel(uid);
        Integer fuTuSlotNum = YuXGTool.getFuTuSlotNum(faTanTotalLevel);
        UserFuCe userFuCe = UserFuCe.getInstance(uid, "符册" + newFuCeNum, 2, newFuCeNum, fuTuSlotNum);
        gameUserService.addItem(uid, userFuCe);
        return rd;
    }

    /**
     * 修改符册名称
     *
     * @param uid
     * @param fuCeId
     */
    public RDSuccess changeFuCeName(long uid, int fuCeId, String name) {
        UserFuCe fuCe = userYuXGService.getFuCe(uid, fuCeId);
        fuCe.setName(name);
        gameUserService.updateItem(fuCe);
        return new RDSuccess();
    }

    /**
     * 熔炼值获取晶石
     *
     * @param uid
     * @return
     */
    public RDSuccess meltValueExchangeSpar(long uid) {
        UserYuXG userYuXG = yuXGService.getOrCreateYuXGData(uid);
        //获取熔炼值
        Integer meltValue = userYuXG.getMeltValue();
        log.info("当前熔炼值：{}", meltValue);
        //检查熔炼值是否足够兑换
        yuXGService.checkMeltValue(meltValue);
        //随机获得一个晶石
        RDCommon rd = new RDCommon();
        Integer gainSparId = SparEnum.randomGainSparId();
        //获得法坛等级
        int faTanTotalLevel = faTanService.getTotalLevel(uid);
        //获得数量
        int num = YuXGTool.getMeltingInfo(gainSparId, userYuXG.getCurFuTan(),faTanTotalLevel).getPrayConsumeSparNum();
        TreasureEventPublisher.pubTAddEvent(uid, gainSparId, num, WayEnum.YU_XG_MELT, rd);
        //剩余熔炼值
        int remainMeltValue = meltValue - YuXGTool.getYuXGInfo().getGetSparNeedMeltValue();
        log.info("剩余熔炼值：{}", remainMeltValue);
        //更新数据
        userYuXG.setMeltValue(remainMeltValue);
        gameUserService.updateItem(userYuXG);
        return rd;
    }


    /**
     * @param userFuCes
     * @return
     */
    private List<RDYuXGFuCes.RDFuCes> getRDFuCes(List<UserFuCe> userFuCes) {
        List<RDYuXGFuCes.RDFuCes> rdFuCes = new ArrayList<>();
        userFuCes.forEach(userFuCe -> {
            //符册总品阶
            int fuCeQuality = 0;
            List<RDYuXGFuCes.RDFuTu> rdFuTus = new ArrayList<>();
            for (UserFuCe.FuTu fuTu : checkFuTuSlot(userFuCe)) {
                if (fuTu.getDataId() == 0) {
                    rdFuTus.add(RDYuXGFuCes.RDFuTu.getInstance(fuTu));
                }
                if (fuTu.getDataId() != 0) {
                    //TODO 待优化批量获取
                    UserFuTu userFuTu = userYuXGService.getUserFuTu(userFuCe.getGameUserId(), fuTu.getDataId());
                    fuCeQuality += YuXGTool.getFuTuInFo(userFuTu.getBaseId()).getQuality();
                    rdFuTus.add(RDYuXGFuCes.RDFuTu.getInstance(fuTu, userFuTu));
                }
            }
            rdFuCes.add(RDYuXGFuCes.getInstance(userFuCe, fuCeQuality, rdFuTus));
        });
        return rdFuCes;
    }

    /**
     * 检查符图槽数据是否正常
     *
     * @param userFuCe
     * @return
     */
    private List<UserFuCe.FuTu> checkFuTuSlot(UserFuCe userFuCe) {
        List<UserFuCe.FuTu> fuTus = userFuCe.getFuTus();
        //去重
        List<UserFuCe.FuTu> newFuTuList = fuTus.stream().collect(Collectors.collectingAndThen
                (Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(UserFuCe.FuTu::getPos))), ArrayList::new));
        //获得符图槽
        int allFaTanLv = faTanService.getTotalLevel(userFuCe.getGameUserId());
        int fuTuSlotNum = YuXGTool.getFuTuSlotNum(allFaTanLv);
        if (newFuTuList.size() >= fuTuSlotNum && newFuTuList.size() == fuTus.size()) {
            return fuTus;
        }
        //修复符图槽
        List<UserFuCe.FuTu> fuTuList = new ArrayList<>();
        for (int i = 0; i < fuTuSlotNum; i++) {
            int pos = i;
            UserFuCe.FuTu fuTu = newFuTuList.stream().filter(f -> f.getPos() == pos).findFirst().orElse(null);
            if (null != fuTu) {
                continue;
            }
            fuTuList.add(UserFuCe.FuTu.getInstance(pos));
        }
        newFuTuList.addAll(fuTuList);
        userFuCe.setFuTus(newFuTuList);
        gameUserService.updateItem(userFuCe);
        return newFuTuList;
    }

    /**
     * 获取升级后的等级
     *
     * @param fuTuId
     * @param exp
     * @return
     */
    private Integer getPromoteLevel(int fuTuId, long exp) {
        //获取品阶
        Integer quality = YuXGTool.getFuTuInFo(fuTuId).getQuality();
        //根据品阶获取对应的需要经验值
        List<Integer> needExpList = YuXGTool.getYuXGInfo().getFuTuUpgradeNeedExp().get(quality);
        if (null == needExpList) {
            throw new ExceptionForClientTip("yuXG.fuTu.check.quality");
        }
        //计算等级
        for (int lv = 0; lv < needExpList.size(); lv++) {
            if (exp < needExpList.get(lv)) {
                return lv;
            }
            if (exp == needExpList.get(lv)) {
                return lv + 1;
            }
        }
        return 10;
    }

    /**
     * 根据概率获取熔炼产出
     *
     * @param treasureId
     * @return
     */
    private List<Award> getMeltAwards(int treasureId, int treasureNum) {
        int treasureStar = TreasureTool.getTreasureById(treasureId).getStar();
        CfgMeltPro cfgMeltPro = YuXGTool.getYuXGInfo().getMeltPro()
                .stream().filter(t -> t.getTreasureStar() == treasureStar).findFirst().orElse(null);
        if (null == cfgMeltPro) {
            throw new ExceptionForClientTip("yuXG.smelt.treasure.check");
        }
        List<Award> meltAwards = new ArrayList<>();
        //获取概率集合
        List<Integer> awardsPro = cfgMeltPro.getAwards().stream().map(Award::getProbability).collect(Collectors.toList());
        for (int i = 0; i < treasureNum; i++) {
            int index = PowerRandom.getIndexByProbs(awardsPro, 10000);
            meltAwards.add(cfgMeltPro.getAwards().get(index));
        }
        return meltAwards;
    }

    /**
     * 获取玉虚神水祈福 100%升阶
     *
     * @return
     */
    private int getShenShuiSparRes(UserYuXG userYuXG) {
        int nextFuTan = userYuXG.getCurFuTan() + 1;
        long uid = userYuXG.getGameUserId();
        int openedFuTanNum = getMaxFuTan(uid);
        //如果到目前已开启最高的品质，则直接掉回最低
        if (nextFuTan > openedFuTanNum) {
            return 1;
        }
        return nextFuTan;
    }

    /**
     * 获取晶石祈福结果
     *
     * @return
     */
    private int getSparRes(int sparId, UserYuXG userYuXG) {
        int nextFuTan = userYuXG.getCurFuTan() + 1;
        long uid = userYuXG.getGameUserId();
        int openedFuTanNum = getMaxFuTan(uid);
        //如果到目前已开启最高的品质，则直接掉回最低
        if (nextFuTan > openedFuTanNum) {
            return 1;
        }
        //获取概率加成
        double addition = faTanService.addition(uid);
        //获得概率
        int sparPro = (int) (YuXGTool.getYuXGInfo().getSparInitialProbability().get(sparId).get(userYuXG.getCurFuTan() - 1) * addition);
        //判断符坛是否升级成功
        boolean hitProbability = PowerRandom.hitProbability(sparPro, 10000);
        if (!hitProbability) {
            //失败返回一级符坛
            return 1;
        }
        return nextFuTan;
    }

    /**
     * 获得当前最大符坛等级
     *
     * @param uid
     * @return
     */
    private int getMaxFuTan(long uid) {
        //获得法坛总等级
        int faTanTotalLevel = faTanService.getTotalLevel(uid);
        if (faTanTotalLevel < 20) {
            return 3;
        }
        if (faTanTotalLevel < 30) {
            return 4;
        }
        return 5;
    }

    /**
     * 设置许愿清单
     * @param uid
     * @param wishingDetailed eg:1@符图id,符图id,符图id;2@符图id,符图id,符图id;3@符图id,符图id,符图id
     * @return
     */
    public RDSuccess setWishingDetailed(Long uid, String wishingDetailed) {
        //获取玉虚信息
        UserYuXG userYuXG = userYuXGService.getUserYuXG(uid);
        if (StringUtils.isEmpty(wishingDetailed)){
            userYuXG.resetWishing();
            gameUserService.updateItem(userYuXG);
            return new  RDSuccess();
        }
        //将客户端传过来参数转化
        List<String> wishingDetaileds = ListUtil.parseStrToStrs(wishingDetailed, ";");
        //获取法坛等级
        int faTanTotalLevel = faTanService.getTotalLevel(uid);
        UserYuXGWishingDetailed userYuXGWishingDetailed = new UserYuXGWishingDetailed();
        List<UserYuXGWishingDetailed> userYuXGWishingDetaileds = userYuXGWishingDetailed.gainUserYuXGWishingDetaileds(wishingDetaileds);
        //判断是否符合添加清单的条件
        boolean satisfyFuTuNum = userYuXG.ifSatisfyFuTuNum(userYuXGWishingDetaileds, faTanTotalLevel);
        if (!satisfyFuTuNum) {
            throw new ExceptionForClientTip("yuXG.wishing.detailed.error");
        }
        //保留当前的许愿值
        for (UserYuXGWishingDetailed yuXGWishingDetailed : userYuXGWishingDetaileds) {
            UserYuXGWishingDetailed uWishingDetailed = userYuXG.gainWishingDetaileds().stream().filter(tmp -> tmp.getFuTan() == yuXGWishingDetailed.getFuTan()).findFirst().orElse(null);
            if (null != uWishingDetailed) {
                yuXGWishingDetailed.setWishingValue(uWishingDetailed.getWishingValue());
            }
        }
        userYuXG.setWishingDetailed(userYuXGWishingDetaileds);
        gameUserService.updateItem(userYuXG);
        return new  RDSuccess();
    }

    /**
     * 获取许愿清单
     * @param uid
     * @return
     */
    public RdWishingDetailed getWishingDetailed(Long uid) {
        UserYuXG userYuXG = userYuXGService.getUserYuXG(uid);
        List<RdWishingDetailed.RdDetailed> rdWishingDetailedList = RdWishingDetailed.RdDetailed.getRdWishingDetaileds(userYuXG.gainWishingDetaileds());
        RdWishingDetailed rd = new RdWishingDetailed();
        rdWishingDetailedList = ListUtil.isEmpty(rdWishingDetailedList) ? RdWishingDetailed.instance() : rdWishingDetailedList;
        rd.setWishingDetailed(rdWishingDetailedList);
        return rd;
    }
}