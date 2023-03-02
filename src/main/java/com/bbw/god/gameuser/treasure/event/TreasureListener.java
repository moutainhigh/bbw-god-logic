package com.bbw.god.gameuser.treasure.event;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.*;
import com.bbw.god.game.zxz.service.ZxzAwadService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.*;
import com.bbw.god.gameuser.treasure.processor.*;
import com.bbw.god.gameuser.yuxg.UserFuTu;
import com.bbw.god.gameuser.yuxg.UserFuTuInfo;
import com.bbw.god.gameuser.yuxg.UserYuXGService;
import com.bbw.god.gameuser.yuxg.YuXGService;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDCommon.RDTreasureInfo;
import com.bbw.god.rechargeactivities.wartoken.event.WarTokenEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author suhq
 * @date 2018年10月17日 下午3:01:07
 */
@Slf4j
@Component
public class TreasureListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private UserTreasureEffectService userTreasureEffectService;
    @Autowired
    private UserTreasureRecordService userTreasureRecordService;
    @Autowired
    private UserTimeLimitTreasureService userTimeLimitTreasureService;
    @Autowired
    private RandomSkillScrollProcessor randomSkillScrollProcessor;
    @Autowired
    private ZhuJDProcessor zhuJDProcessor;
    @Autowired
    private ZhiZunRandomSkillScrollProcessor zhiZunRandomSkillScrollProcessor;
    @Autowired
    private WxShenJiangProcessor wxShenJiangProcessor;
    @Autowired
    private BoxService boxService;
    @Autowired
    private RandomSecretScrollProcessor randomSecretScrollProcessor;
    @Autowired
    private YuXGService yuXGService;
    @Autowired
    private UserYuXGService userYuXGService;
    @Autowired
    private WindRustleBoxProcessor windRustleBoxProcessor;
    @Autowired
    private WarTokenThreeStarBoxProcessor warTokenThreeStarBoxProcessor;
    @Autowired
    private WarTokenFourStarBoxProcessor warTokenFourStarBoxProcessor;
    @Autowired
    private WarTokenMythicalBeastBoxProcessor warTokenMythicalBeastBoxProcessor;
    @Autowired
    private CopperBabyProcessor copperBabyProcessor;
    @Autowired
    private HonorCurrencyService honorCurrencyService;
    @Autowired
    private GiftsBagProcessor giftsBagProcessor;
    @Autowired
    private PrimaryDayNvWaGiftProcessor primaryDayNvWaGiftProcessor;
    @Autowired
    private PremiumDayNvWaGiftProcessor premiumDayNvWaGiftProcessor;
    @Autowired
    private IntermediateDayNvWaGiftProcessor intermediateDayNvWaGiftProcessor;
    @Autowired
    private AdvancedDayNvWaGiftProcessor advancedDayNvWaGiftProcessor;
    @Autowired
    private EpicDayNvWaGiftProcessor epicDayNvWaGiftProcessor;
    @Autowired
    private ZxzAwadService zxzAwadService;
    @Autowired
    private LegendFuTuBoxProcessor legendFuTuBoxProcessor;
    /** 荣耀货币 */
    private final static List<Integer> GLORY_CURRENCY = Arrays.asList(
            TreasureEnum.HONOR_SILVER_COIN.getValue(),
            TreasureEnum.HONOR_COPPER_COIN.getValue());

    @EventListener
    public void addTreasure(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        for (EVTreasure t : ep.getAddTreasures()) {
            TreasureEnum treasureEnum = TreasureEnum.fromValue(t.getId());
            // 没有配置在枚举中
            if (null == treasureEnum) {
                addTreasure(gu, t.getId(), t.getNum(), ep.getWay(), ep.getRd());
                continue;
            }
            switch (treasureEnum) {
                case RANDOM_ADVANCED_SCROLL:
                    randomSkillScrollProcessor.deliverRandomSkillScroll(ep.getGuId(), t.getNum(), ep.getWay(), ep.getRd());
                    break;
                //随机秘传卷轴
                case RANDOM_SECRET_SCROLL:
                    randomSecretScrollProcessor.deliverRandomSecretScroll(ep.getGuId(), t.getNum(), ep.getWay(), ep.getRd());
                    break;
                case ZHU_JI_DAN:
                    zhuJDProcessor.useZhuJiDan(gu, ep.getRd());
                    break;
                case ZHI_ZUN_GAO_JI_JUAN_ZHOU:
                    //至尊高级卷轴
                    zhiZunRandomSkillScrollProcessor.deliverRandomSkillScroll(ep.getGuId(), t.getNum(), ep.getWay(), ep.getRd());
                    break;
                case LEGEND_SYMBOL_DIAFRAM_BOX:
                    //传奇符图宝箱
                    legendFuTuBoxProcessor.deliverRandomFuTu(ep.getGuId(), t.getNum(), ep.getWay(), ep.getRd());
                    break;
                //随机元素
                case WAR_TOKEN_EXP:
                    ep.getRd().addTreasure(new RDTreasureInfo(t.getId(), t.getNum()));
                    WarTokenEventPublisher.pubAddExpEvent(ep.getGuId(), t.getNum(), false);
                    break;
                case WX_SHEN_JIANG:
                    wxShenJiangProcessor.open(ep.getGuId(), t.getNum(), ep.getRd());
                    break;
                case WING_RUSTLE_BOX:
                    windRustleBoxProcessor.open(ep.getGuId(), t.getNum(), ep.getRd());
                    break;
                case WAR_TOKEN_MB_BOX:
                    warTokenMythicalBeastBoxProcessor.open(ep.getGuId(), t.getNum(), ep.getRd());
                    break;
                case WAR_TOKEN_TS_BOX:
                    warTokenThreeStarBoxProcessor.open(ep.getGuId(), t.getNum(), ep.getRd());
                    break;
                case WAR_TOKEN_FS_BOX:
                    warTokenFourStarBoxProcessor.open(ep.getGuId(), t.getNum(), ep.getRd());
                    break;
                case COPPER_BABY:
                    copperBabyProcessor.open(ep.getGuId(), t.getNum(), ep.getRd());
                    break;
                case GIFTS_BAG:
                    giftsBagProcessor.open(ep.getGuId(), t.getNum(), ep.getRd());
                    break;
                case PRIMARY_DAY_NV_WA_GIFT:
                    primaryDayNvWaGiftProcessor.open(ep.getGuId(), t.getNum(), ep.getRd());
                    break;
                case INTERMEDIATE_DAY_NV_WA_GIFT:
                    intermediateDayNvWaGiftProcessor.open(ep.getGuId(), t.getNum(), ep.getRd());
                    break;
                case PREMIUM_DAY_NV_WA_GIFT:
                    premiumDayNvWaGiftProcessor.open(ep.getGuId(), t.getNum(), ep.getRd());
                    break;
                case EPIC_DAY_NV_WA_GIFT:
                    epicDayNvWaGiftProcessor.open(ep.getGuId(), t.getNum(), ep.getRd());
                    break;
                case ADVANCED_DAY_NV_WA_GIFT:
                case FIRST_CZ_BOX:
                case MIDDLE_CZ_BOX:
                case HIGH_CZ_BOX:
                case SUPER_CZ_BOX:
                case GCLD:
                case RANDOM_ELEMENT:
                case FIGHT_BOOST_BOX:
                    boxService.open(gu.getId(), treasureEnum.getValue(), ep.getWay(), ep.getRd());
                    break;
                case GIFT_OF_REINDEER:
                case GIFT_OF_RABBIT:
                    for (int i = 0; i < t.getNum(); i++) {
                        boxService.open(gu.getId(), treasureEnum.getValue(), ep.getWay(), ep.getRd());
                    }
                    break;
                case RANDOM_ORIGIN:
                    zxzAwadService.open(ep.getGuId(), t.getNum(), ep.getRd());
                    break;
                default:
                    addTreasure(gu, t.getId(), t.getNum(), ep.getWay(), ep.getRd());
                    break;
            }
        }
    }

    @EventListener
    public void deductTreasure(TreasureDeductEvent event) {
        EPTreasureDeduct ep = event.getEP();
        GameUser gameUser = this.gameUserService.getGameUser(ep.getGuId());
        EVTreasure ev = ep.getDeductTreasure();
        deductTreasure(gameUser, ev.getId(), ev.getNum(), ep.getRd());

        // 记录卷轴使用记录
        CfgTreasureEntity treasureEntity = TreasureTool.getTreasureById(ev.getId());
        if (treasureEntity.getType() == TreasureType.SKILL_SCROLL.getValue()) {
            CfgSkillScrollLimitEntity skillScrollLimitEntity = TreasureTool.getSkillScrollLimitEntity(ev.getId());
            if (skillScrollLimitEntity.getLimit() > 0) {
                addTreasureRecord(gameUser, ev.getId());
            }
        }

        //发送道具完成扣除事件
        TreasureEventPublisher.pubTFinishDeductEvent(gameUser.getId(), ev.getId(), ev.getNum(), ep.getWay(), ep.getRd());
    }

    @EventListener
    public void treasureEffectSet(TreasureEffectSetEvent event) {
        EPTreasureEffectSet ep = (EPTreasureEffectSet) event.getSource();
        Integer treasureId = ep.getTreasureId();
        Integer treasureEffect = ep.getTreasureEffect();
        setTreasureEffect(ep.getGuId(), treasureId, treasureEffect, ep.getWay());
    }

    @EventListener
    public void treasureEffectAdd(TreasureEffectAddEvent event) {
        EPTreasureEffectAdd ep = (EPTreasureEffectAdd) event.getSource();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        addTreasureEffect(gu, ep.getTreasureId(), ep.getAddEffect(), ep.getWay(), ep.getRd());
    }

    @EventListener
    public void treasureEffectDeduct(TreasureEffectDeductEvent event) {
        EPTreasureEffectDeduct ep = (EPTreasureEffectDeduct) event.getSource();
        Integer treasureId = ep.getTreasureId();
        if (this.userTreasureEffectService.isTreasureEffect(ep.getGuId(), treasureId)) {
            UserTreasureEffect utEffect = userTreasureEffectService.getEffect(ep.getGuId(), treasureId);
            utEffect.deductEffect(ep.getDeductEffect());
            this.gameUserService.updateItem(utEffect);
        }
    }

    @EventListener
    public void treasureRecordAdd(TreasureRecordAddEvent event) {
        EPTreasureRecordAdd ep = event.getEP();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        addTreasureRecord(gu, ep.getTreasureId(), ep.getWay());
    }

    @EventListener
    public void treasureRecordDel(TreasureRecordDelEvent event) {
        EPTreasureRecordDel ep = event.getEP();
        Integer treasureId = ep.getTreasureId();
        Long uid = ep.getGuId();
        UserTreasureRecord record = userTreasureRecordService.getUserTreasureRecord(uid, treasureId);
        if (null != record) {
            userTreasureRecordService.delRecord(record);
        }
    }

    @EventListener
    public void treasureRecordReset(TreasureRecordResetEvent event) {
        EPTreasureRecordReset ep = event.getEP();
        resetTreasureRecord(ep.getGuId(), ep.getTreasureId());
    }

    @EventListener
    public void deductFuTu(FuTuDeductEvent event) {
        EPFuTuDeduct ep = event.getEP();
        userYuXGService.doDelFuTu(ep.getUserFuTu());
    }

    @EventListener
    public void addFuTu(FuTuAddEvent event) {
        EPFuTuAdd ep = event.getEP();
        addFuTu(ep.getGuId(), ep.getId(), ep.getNum(), ep.getWay(), ep.getRd());
    }

    /**
     * 添加法宝
     *
     * @param gu
     * @param treasureId
     * @param addNum
     * @param way
     * @param rd
     */
    private void addTreasure(GameUser gu, int treasureId, int addNum, WayEnum way, RDCommon rd) {
        if (WayEnum.EXCHANGE_XJBK == way) {
            log.info("{},星君宝库兑换开始执行addTreasure,法宝id={},数量={}", gu.getId(), treasureId, addNum);
        }
        //兼容addTreasure事件
        if (yuXGService.isFuTu(treasureId)) {
            TreasureEventPublisher.pubTAddFuTuEvent(gu.getId(), treasureId, addNum, way, rd);
            return;
        }
        long uid = gu.getId();
        //荣耀货币转化
//        if (GLORY_CURRENCY.contains(treasureId)) {
//            honorCurrencyService.honorCurrencyAddConversion(uid, treasureId, addNum, rd);
//            //发送道具完成发放事件
//            TreasureEventPublisher.pubTFinishAddEvent(gu.getId(), treasureId, addNum, way, rd);
//            return;
//        }
        CfgTreasureEntity cfgTreasure = TreasureTool.getTreasureById(treasureId);
        if (null == cfgTreasure) {
            log.error("添加道具的不存在，treasureId:" + treasureId);
            return;
        }
        UserTreasure uTreasure = userTreasureService.getUserTreasure(uid, treasureId);
        if (uTreasure != null) {
            if (userTimeLimitTreasureService.isTimeLimit(treasureId, way)) {
                uTreasure.addTimeLimitNum(addNum, userTimeLimitTreasureService.getExpireDate(uid, treasureId));
            } else {
                uTreasure.addNum(addNum);
            }
            this.gameUserService.updateItem(uTreasure);
        } else {
            if (userTimeLimitTreasureService.isTimeLimit(treasureId, way)) {
                uTreasure = UserTreasure.instanceAsTimeLimit(uid, cfgTreasure, addNum, userTimeLimitTreasureService.getExpireDate(uid, treasureId));
            } else {
                uTreasure = UserTreasure.instance(uid, cfgTreasure, addNum);
            }
            userTreasureService.doAddTreasure(uTreasure);
        }
        Integer type = cfgTreasure.ifSkillScroll() ? cfgTreasure.getType() : null;
        rd.addTreasure(new RDTreasureInfo(treasureId, addNum, type));
        if (WayEnum.EXCHANGE_XJBK == way) {
            log.info(gu.getId() + ",星君宝库兑换执行addTreasure结束！");
        }
        //发送道具完成发放事件
        TreasureEventPublisher.pubTFinishAddEvent(gu.getId(), treasureId, addNum, way, rd);
    }

    /**
     * 发放符图
     *
     * @param uid
     * @param treasureId
     * @param addNum
     * @param way
     * @param rd
     */
    private void addFuTu(long uid, int treasureId, int addNum, WayEnum way, RDCommon rd) {
        //玉虚宫中获得的符图需要保存（成就相关）
        if (way == WayEnum.YU_XG_PRAY) {
            UserFuTuInfo userFuTuInfo = gameUserService.getSingleItem(uid, UserFuTuInfo.class);
            if (null == userFuTuInfo) {
                userFuTuInfo = UserFuTuInfo.getInstance(uid);
                gameUserService.addItem(uid, userFuTuInfo);
            }
            userFuTuInfo.addFuTuId(treasureId);
            gameUserService.updateItem(userFuTuInfo);
        }
        //发放符图
        UserFuTu userFuTu = UserFuTu.getInstance(uid, treasureId);
        userYuXGService.addFuTu(userFuTu);
        Long dataId = userFuTu.getId();
        rd.addTreasure(new RDTreasureInfo(treasureId, dataId, addNum, TreasureType.FUTU.getValue()));
    }

    /**
     * 扣除法宝
     *
     * @param gameUser
     * @param treasureId
     * @param numToDeduct
     */
    private void deductTreasure(GameUser gameUser, Integer treasureId, int numToDeduct, RDCommon rd) {
        UserTreasure uTreasure = userTreasureService.getUserTreasure(gameUser.getId(), treasureId);
        if (uTreasure != null) {
            rd.addDeductedTreasures(new RDTreasureInfo(treasureId, numToDeduct));
            uTreasure.deductNum(numToDeduct);
            if (uTreasure.gainTotalNum() > 0) {
                this.gameUserService.updateItem(uTreasure);
            } else {
                userTreasureService.doDelTreasure(uTreasure);
            }
            // 如果是漫步靴，当数量变为0时将关闭漫步靴
            if (treasureId == TreasureEnum.MBX.getValue() && uTreasure.gainTotalNum() == 0) {
                gameUser.getSetting().setActiveMbx(0);
                gameUser.updateSetting();
            }
        }
    }

    /**
     * 设置法宝使用状态 目前仅限漫步鞋使用
     *
     * @param uid
     * @param treasureId
     * @param remainEffect
     * @param way
     */
    private void setTreasureEffect(long uid, Integer treasureId, Integer remainEffect, WayEnum way) {
        UserTreasureEffect utEffect = userTreasureEffectService.getEffect(uid, treasureId);
        if (utEffect != null) {
            utEffect.setEffect(remainEffect);
            this.gameUserService.updateItem(utEffect);
        } else {
            utEffect = UserTreasureEffect.instance(uid, treasureId, remainEffect);
            userTreasureEffectService.addTreasureEffect(utEffect);
        }
    }

    /**
     * 设置法宝使用状态 四不像和青鸾取最近的为有效
     *
     * @param gu
     * @param treasureId
     * @param way
     * @param addEffect
     * @param rd
     */
    private void addTreasureEffect(GameUser gu, Integer treasureId, Integer addEffect, WayEnum way, RDCommon rd) {
        UserTreasureEffect utEffect = userTreasureEffectService.getEffect(gu.getId(), treasureId);
        if (utEffect != null) {
            utEffect.addEffect(addEffect);
            this.gameUserService.updateItem(utEffect);
        } else {
            utEffect = UserTreasureEffect.instance(gu.getId(), treasureId, addEffect);
            userTreasureEffectService.addTreasureEffect(utEffect);
        }
        // 使用四不像充值青鸾
        if (treasureId == TreasureEnum.SBX.getValue()) {
            UserTreasureEffect qlEffect = userTreasureEffectService.getEffect(gu.getId(), TreasureEnum.QL.getValue());
            if (qlEffect != null) {
                qlEffect.setRemainEffect(0);
                this.gameUserService.updateItem(qlEffect);
            }
        }
        // 使用青鸾充值漫步鞋
        if (treasureId == TreasureEnum.QL.getValue()) {
            UserTreasureEffect sbxEffect = userTreasureEffectService.getEffect(gu.getId(), TreasureEnum.SBX.getValue());
            if (sbxEffect != null) {
                sbxEffect.setRemainEffect(0);
                this.gameUserService.updateItem(sbxEffect);
            }
        }
        // 返回给客户端的数据
        if (treasureId == TreasureEnum.CSZ.getValue()) {
            rd.setCszRemain(utEffect.getRemainEffect());
        }
        if (treasureId == TreasureEnum.LBJQ.getValue()) {
            rd.setLbRemain(utEffect.getRemainEffect());
        }
    }

    /**
     * 添加法宝使用记录
     *
     * @param gu
     * @param treasureId
     */
    private void addTreasureRecord(GameUser gu, int treasureId) {
        UserTreasureRecord utr = userTreasureRecordService.getUserTreasureRecord(gu.getId(), treasureId);
        int curPos = gu.getLocation().getPosition();
        if (utr != null) {
            CfgTreasureEntity treasureEntity = TreasureTool.getTreasureById(treasureId);
            if (utr.getLastUsePos() != curPos && treasureEntity.getType() != TreasureType.SKILL_SCROLL.getValue()) {
                utr.setUseTimes(1);
            } else {
                utr.addTimes();
            }
            utr.setLastUseDate(DateUtil.now());
            utr.setLastUsePos(curPos);
            this.gameUserService.updateItem(utr);
        } else {
            utr = UserTreasureRecord.instance(gu.getId(), treasureId, curPos);
            userTreasureRecordService.addRecord(utr);
        }
    }

    private void addTreasureRecord(GameUser gu, int treasureId, WayEnum way) {
        if (TreasureEnum.XJZ.getValue() != treasureId && TreasureEnum.CHANG_ZI.getValue() != treasureId) {
            addTreasureRecord(gu, treasureId);
            return;
        }
        UserTreasureRecord utr = userTreasureRecordService.getRecords(gu.getId()).stream()
                .filter(ut -> ut.getBaseId() == treasureId && ut.getWay() == way).findFirst().orElse(null);
        int curPos = gu.getLocation().getPosition();
        if (utr != null) {
            CfgTreasureEntity treasureEntity = TreasureTool.getTreasureById(treasureId);
            if (utr.getLastUsePos() != curPos && treasureEntity.getType() != TreasureType.SKILL_SCROLL.getValue()) {
                utr.setUseTimes(1);
            } else {
                utr.addTimes();
            }
            utr.setLastUseDate(DateUtil.now());
            utr.setLastUsePos(curPos);
            this.gameUserService.updateItem(utr);
        } else {
            utr = UserTreasureRecord.instance(gu.getId(), treasureId, curPos, way);
            userTreasureRecordService.addRecord(utr);
        }
    }

    /**
     * 清除法宝使用记录
     *
     * @param guId
     * @param treasureId
     */
    private void resetTreasureRecord(long guId, int treasureId) {
        UserTreasureRecord utr = userTreasureRecordService.getUserTreasureRecord(guId, treasureId);
        if (utr != null) {
            utr.setUseTimes(0);
            this.gameUserService.updateItem(utr);
        }
    }
}
