package com.bbw.god.rechargeactivities.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.CfgMallExtraPackEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rechargeactivities.RDRechargeActivity;
import com.bbw.god.rechargeactivities.RechargeActivityEnum;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 特惠礼包
 *
 * @author lwb
 */
@Slf4j
@Service
public class TeHuiGiftPackProcessor extends AbstractRechargeActivityProcessor {
    //特惠礼包组合ID=》商城配置ID
    private static final int GROUP_GIFT_PACK_ID = 180004;
    //一元特惠礼包
    private static final int YI_YUAN_GIFT_PACK_ID = 180001;
    //三元特惠礼包
    private static final int SAN_YUAN_GIFT_PACK_ID = 180002;
    //五元特惠礼包
    private static final int LIU_YUAN_GIFT_PACK_ID = 180003;
    /** 可选择奖励的礼包 */
    private static final List<Integer> ABLE_PICK_AWARD_PACK = Arrays.asList(YI_YUAN_GIFT_PACK_ID, SAN_YUAN_GIFT_PACK_ID, LIU_YUAN_GIFT_PACK_ID);
    /**
     * 1元宝的礼包
     */
    private static final int ONE_GOLD_GIFT_PACK_ID = 180000;

    @Override
    public RechargeActivityEnum getParent() {
        return RechargeActivityEnum.TE_HUI_PACK;
    }

    @Override
    public RechargeActivityItemEnum getCurrentEnum() {
        return RechargeActivityItemEnum.TE_HUI_PACK;
    }

    /**
     * 获取展示的等级所需值
     *
     * @return
     */
    @Override
    protected int getShowNeedLevel() {
        return 12;
    }

    /**
     * 获取展示的充值所需值
     *
     * @return
     */
    @Override
    protected int getShowNeedRecharge() {
        return 6;
    }

    @Override
    public int getCanGainAwardNum(long uid) {
        UserMallRecord groupGiftPack=mallService.getUserMallRecord(uid,GROUP_GIFT_PACK_ID);
        if (groupGiftPack!=null){
            List<UserMallRecord> userMallRecords=mallService.getUserValidMallRecord(uid,MallEnum.TE_HUI_RECHARGE_BAG);
            userMallRecords=userMallRecords.stream().filter(p->DateUtil.millisecondsInterval(p.getDateTime(),groupGiftPack.getDateTime())>=0).collect(Collectors.toList());
            return MallTool.getMallConfig().getTeHuiRechargeMalls().size()-userMallRecords.size();
        }
        UserMallRecord mallRecord=mallService.getUserMallRecord(uid,ONE_GOLD_GIFT_PACK_ID);
        if (mallRecord==null){
            return 1;
        }
        return 0;
    }

    @Override
    public RDRechargeActivity listAwards(long uid) {
        //获取商城对象
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getTeHuiRechargeMalls();
        List<RDRechargeActivity.GiftPackInfo> goodsInfos = toRdGoodsInfoList(uid, fMalls, MallEnum.TE_HUI_RECHARGE_BAG, false);
        log.info("{}商城信息最终记录{}", uid, goodsInfos);
        //获取1.3.5元特惠自选礼包集合
        List<RDRechargeActivity.GiftPackInfo> giftPackInfos = goodsInfos.stream().filter(p -> ABLE_PICK_AWARD_PACK.contains(p.getMallId())).collect(Collectors.toList());
        for (RDRechargeActivity.GiftPackInfo giftPackInfo : giftPackInfos) {
            //获取礼包信息对象
            UserMallRecord teHuiExtraMallRecord = mallService.getUserMallRecord(uid, giftPackInfo.getMallId());
            //添加额外自选商品
            List<Award> teHuiExtraAwards = getTeHuiExtraAwards(giftPackInfo.getMallId());
            giftPackInfo.setExtraAwards(teHuiExtraAwards);
            giftPackInfo.setExtraAwardStatus(RechargeStatusEnum.CAN_BUY.getStatus());

            //已选定额外自选商品
            if (teHuiExtraMallRecord != null && ListUtil.isNotEmpty(teHuiExtraMallRecord.getPickedAwards())) {
                giftPackInfo.getAwards().addAll(teHuiExtraMallRecord.getPickedAwards());
                giftPackInfo.setExtraAwardStatus(teHuiExtraMallRecord.getNum() > 0 ? null : RechargeStatusEnum.CAN_GAIN_AWARD.getStatus());
            }
        }

        //更新物品信息
        RDRechargeActivity rd = new RDRechargeActivity();
        rd.setGoodsList(goodsInfos);
        //更新特惠组合礼包倒计时
        rd.setCountdownDays(getGroupGiftPackCountDownDays(uid));
        //0为可以购买组合礼包,否则显示剩余天数
        if (rd.getCountdownDays() > 0) {
            goodsInfos.forEach(p -> {
                if (p.getRemainTimes() > 0) {
                    p.setStatus(1);
                }
            });
        }
        log.info("{}返回信息{}", uid, rd.getGoodsList());
        return rd;
    }

    @Override
    public RDRechargeActivity buyAwards(long uid, int mallId) {
        RDRechargeActivity rd = new RDRechargeActivity();
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getTeHuiRechargeMalls();
        buyAwards(fMalls, mallId, uid, rd, WayEnum.TE_HUI_1_GOLD_GIFT_PACK, MallEnum.TE_HUI_RECHARGE_BAG);
        return rd;
    }

    @Override
    public RDRechargeActivity gainAwards(long uid, int realId) {
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getTeHuiRechargeMalls();
        Optional<CfgMallEntity> op=fMalls.stream().filter(p->p.getGoodsId()==realId).findFirst();
        if (!op.isPresent()){
            //不存在该商品
            throw new ExceptionForClientTip("rechargeActivity.not.award");
        }
        return gainFreeMallAwards(uid,op.get(), WayEnum.TE_HUI_GOLD_GIFT_PACK);
    }

    @Override
    protected boolean canGainFreeAwards(long uid, CfgMallEntity mallEntity) {
        UserMallRecord item = mallService.getUserMallRecord(uid,mallEntity.getId());
        if (item!=null && item.getStatus()!=null && item.getStatus()==RechargeStatusEnum.CAN_GAIN_AWARD.getStatus()){
            return true;
        }
        UserMallRecord groupGiftPack = mallService.getUserMallRecord(uid, GROUP_GIFT_PACK_ID);
        if (groupGiftPack==null){
            return false;
        }
        if (item==null || DateUtil.millisecondsInterval(groupGiftPack.getDateTime(),item.getDateTime())>0){
            return true;
        }
        return item.getNum()<mallEntity.getLimit();
    }

    @Override
    protected List<RDRechargeActivity.GiftPackInfo> toRdGoodsInfoList(long guId, List<CfgMallEntity> fMalls, MallEnum mallEnum, boolean isExtraDiscount) {
        List<RDRechargeActivity.GiftPackInfo> list = new ArrayList<>();
        List<UserMallRecord> userMallRecords = mallService.getUserMallRecord(guId, mallEnum);
        log.info("{}商城信息初始记录{}", guId, userMallRecords);
        checkTimeoutRecharge(guId, userMallRecords);
        log.info("商城信息购买状态第一个变更位置记录{}", userMallRecords);
        if (ListUtil.isNotEmpty(userMallRecords)) {
            userMallRecords = userMallRecords.stream().filter(p -> p.ifValid()).collect(Collectors.toList());
        }
        Optional<UserMallRecord> groupGiftPackRecordOp = userMallRecords.stream().filter(p -> p.getBaseId() == GROUP_GIFT_PACK_ID).findFirst();
        if (groupGiftPackRecordOp.isPresent()) {
            //组合礼包已购买
            UserMallRecord userMallRecord = groupGiftPackRecordOp.get();
            userMallRecords = userMallRecords.stream().filter(p -> DateUtil.millisecondsInterval(userMallRecord.getDateTime(), p.getDateTime()) < 0).collect(Collectors.toList());
        }
        for (CfgMallEntity mall : fMalls) {
            if (mall.getId() == GROUP_GIFT_PACK_ID) {
                continue;
            }
            RDRechargeActivity.GiftPackInfo goodsInfo = RDRechargeActivity.GiftPackInfo.instance(mall, mall.getPrice(isExtraDiscount));
            goodsInfo.setAwards(ListUtil.copyList(productService.getProductAward(goodsInfo.getRechargeId()).getAwardList(), Award.class));
            list.add(goodsInfo);
        }
        if (ListUtil.isNotEmpty(userMallRecords)) {
            for (RDRechargeActivity.GiftPackInfo info : list) {
                Optional<UserMallRecord> optional = userMallRecords.stream().filter(p -> p.getBaseId().equals(info.getMallId()) && null != p.getStatus()).findFirst();
                if (optional.isPresent()) {
                    UserMallRecord um = optional.get();
                    info.updateRemainTimes(info.getLimit() - um.getNum());
                    if (um.getStatus() != null && um.getStatus() == RechargeStatusEnum.CAN_GAIN_AWARD.getStatus()) {
                        info.setStatus(um.getStatus());
                        log.info("{}商城信息购买状态第二个变更位置记录{}", guId, userMallRecords);

                    }
                }
            }
        }
        list = list.stream().sorted(Comparator.comparing(RDRechargeActivity.GiftPackInfo::getPrice)).collect(Collectors.toList());
        list = list.stream().sorted(Comparator.comparing(RDRechargeActivity.GiftPackInfo::getStatus).reversed()).collect(Collectors.toList());
        return list;
    }

    /**
     * 特惠礼包可以领取的次数
     *
     * @param uid
     * @return
     */
    private int getGroupGiftPackCountDownDays(long uid) {
        UserMallRecord groupGiftPack = mallService.getUserMallRecord(uid, GROUP_GIFT_PACK_ID);
        if (groupGiftPack != null) {
            //由于2-17号停服一天，所以在特惠礼包延后一天
            String february17thString = "2023-02-17 00:00:00";
            Date february17thDate = DateUtil.fromDateTimeString(february17thString);
            //礼包的结束时间
            Date endDateTime = DateUtil.addDays(groupGiftPack.getDateTime(), 7);
            //充值时间在2-17之前，结束时间在12-06之后要延后一天
            //2-17号在这个特惠礼包领取的范围内
            boolean existFebruary17th = groupGiftPack.getDateTime().before(february17thDate) && endDateTime.after(february17thDate);
            //当前时间小于礼包领取的结束时间
            boolean lessEndDate = new Date().before(endDateTime);
            if (existFebruary17th && lessEndDate) {
                return 8 - DateUtil.getDaysBetween(groupGiftPack.getDateTime(), DateUtil.now());
            }
            return 7 - DateUtil.getDaysBetween(groupGiftPack.getDateTime(), DateUtil.now());
        }
        return 0;
    }

    @Override
    public boolean updateRechargeStatus(long uid, int mallId) {
        UserMallRecord userMallRecord = mallService.getUserMallRecord(uid, mallId);
        if (userMallRecord == null) {
            userMallRecord = UserMallRecord.instance(uid, mallId, MallEnum.TE_HUI_RECHARGE_BAG.getValue(), 0);
            userMallRecord.setStatus(RechargeStatusEnum.CAN_GAIN_AWARD.getStatus());
            mallService.addRecord(userMallRecord);
            return true;
        } else {
            userMallRecord.setStatus(RechargeStatusEnum.CAN_GAIN_AWARD.getStatus());
            gameUserService.updateItem(userMallRecord);
            return true;
        }
    }

    @Override
    public RDRechargeActivity pickAwards(long uid, Integer mallId, String awardIds) {
        //参数检测
        if (!ABLE_PICK_AWARD_PACK.contains(mallId)) {
            //该项没有可选择的奖励
            throw new ExceptionForClientTip("rechargeActivity.not.extraAwards");
        }
        List<Integer> awardIdList = ListUtil.parseStrToInts(awardIds);

        //获得玩家商城对象
        UserMallRecord teHuiMallRecord = mallService.getUserMallRecord(uid, mallId);


        //如果玩家商城对象未初始，初始化商城对象
        if (teHuiMallRecord == null) {
            teHuiMallRecord = UserMallRecord.instance(uid, mallId, MallEnum.TE_HUI_RECHARGE_BAG.getValue(), 0);
            mallService.addRecord(teHuiMallRecord);
        }

        //已购买不可再换自选商品
        if (teHuiMallRecord.getStatus() != null && teHuiMallRecord.getStatus() == RechargeStatusEnum.DONE.getStatus()) {
            throw new ExceptionForClientTip("rechargeActivity.picked.Awards");
        }
        //获得自选奖励池
        List<Award> teHuiExtraAwards = getTeHuiExtraAwards(mallId);
        //筛选出玩家选定的物品
        List<Award> pickedAwards = new ArrayList<>();
        awardIdList.forEach(awardId -> {
            Optional<Award> extraAward = teHuiExtraAwards.stream().filter(p -> p.getAwardId().equals(awardId)).findFirst();
            if (!extraAward.isPresent()) {
                throw new ExceptionForClientTip("rechargeActivity.not.extraAwards");
            }
            pickedAwards.add(extraAward.get());
        });

        //把玩家选定商品的记录存入商城对象
        teHuiMallRecord.setPickedAwards(pickedAwards);
        //更新玩家商城对象
        gameUserService.updateItem(teHuiMallRecord);
        return new RDRechargeActivity();
    }

    /**
     * 获取每日特惠额外自选奖励
     *
     * @return
     */
    private List<Award> getTeHuiExtraAwards(int mallId) {
        //获得额外礼包配置
        CfgMallExtraPackEntity extraPack = MallTool.getMallExtraPack(mallId);
        return extraPack.getExtraAwards();
    }

    @Override
    protected RDRechargeActivity gainFreeMallAwards(long uid, CfgMallEntity mallEntity, WayEnum wayEnum) {
        RDRechargeActivity rd = new RDRechargeActivity();
        if (!canGainFreeAwards(uid, mallEntity)) {
            //已经领取过了
            throw new ExceptionForClientTip("rechargeActivity.awarded");
        }
        UserMallRecord record = mallService.getUserMallRecord(uid, mallEntity.getId());
        UserMallRecord groupGiftPack = mallService.getUserMallRecord(uid, GROUP_GIFT_PACK_ID);
        //没有选择自选额外商品
        if (ABLE_PICK_AWARD_PACK.contains(mallEntity.getId()) && !(record != null && ListUtil.isNotEmpty(record.getPickedAwards()))) {
            throw new ExceptionForClientTip("rechargeActivity.not.select.Awards");
        }
        List<Award> awards = new ArrayList<>();

        if (record != null && ListUtil.isNotEmpty(record.getPickedAwards())) {
            awards.addAll(record.getPickedAwards());
        }
        if (groupGiftPack != null && groupGiftPack.ifValid() && record != null && DateUtil.millisecondsInterval(groupGiftPack.getDateTime(), record.getDateTime()) > 0) {
            record = null;
        }
        if (record == null) {
            record = UserMallRecord.instance(uid, mallEntity.getId(), mallEntity.getType(), 0);
        } else if (record.getStatus() != null && record.getStatus() != RechargeStatusEnum.CAN_GAIN_AWARD.getStatus()) {
            throw new ExceptionForClientTip("rechargeActivity.awarded");
        }

        awards.addAll(productService.getProductAward(getProductGoodsId(mallEntity.getGoodsId())).getAwardList());
        awardService.fetchAward(uid, awards, wayEnum, "在【" + wayEnum.getName() + "】", rd);
        record.setStatus(RechargeStatusEnum.DONE.getStatus());
        record.addNum(1);
        mallService.addRecord(record);
        return rd;
    }
}
