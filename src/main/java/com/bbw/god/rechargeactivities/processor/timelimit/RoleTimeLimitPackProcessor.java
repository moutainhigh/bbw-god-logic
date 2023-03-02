package com.bbw.god.rechargeactivities.processor.timelimit;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.ConsumeType;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rechargeactivities.RDRechargeActivity;
import com.bbw.god.rechargeactivities.RechargeActivityEnum;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import com.bbw.god.rechargeactivities.processor.AbstractRechargeActivityProcessor;
import com.bbw.god.rechargeactivities.processor.RechargeStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 限时礼包
 *
 * @author suhq
 * @date 2021/7/1 下午4:19
 **/
@Service
public class RoleTimeLimitPackProcessor extends AbstractRechargeActivityProcessor {
    @Autowired
    private BoxService boxService;

    @Override
    public RechargeActivityEnum getParent() {
        return RechargeActivityEnum.NONE;
    }

    @Override
    public RechargeActivityItemEnum getCurrentEnum() {
        return RechargeActivityItemEnum.ROLE_TIME_LIMIT_BAG;
    }

    /**
     * 获取展示的等级所需值
     *
     * @return
     */
    @Override
    protected int getShowNeedLevel() {
        return 0;
    }

    /**
     * 获取展示的充值所需值
     *
     * @return
     */
    @Override
    protected int getShowNeedRecharge() {
        return 0;
    }

    @Override
    public int getCanGainAwardNum(long uid) {
        //无需红点
        return 0;
    }

    @Override
    public RDRechargeActivity listAwards(long uid) {
        RDRechargeActivity rd = new RDRechargeActivity();
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getRoleTimeLimitMalls();
        List<RDRechargeActivity.GiftPackInfo> goodsInfos = toRdGoodsInfoList(uid, fMalls, MallEnum.ROLE_TIME_LIMIT_BAG, false);
        rd.setGoodsList(goodsInfos);
        return rd;
    }

    @Override
    public RDRechargeActivity buyAwards(long uid, int mallId) {
        RDRechargeActivity rd = new RDRechargeActivity();
        CfgMallEntity cme = MallTool.getMall(mallId);
        UserMallRecord umr = mallService.getUserMallRecord(uid, mallId);
        if (cme.getLimit() != 0 && umr.getNum() >= cme.getLimit()) {
            //上限
            throw new ExceptionForClientTip("rechargeActivity.awarded");
        }
        ResChecker.checkGold(gameUserService.getGameUser(uid), cme.getPrice());
        ResEventPublisher.pubGoldDeductEvent(uid, cme.getPrice(), WayEnum.ROLE_TIME_LIMIT_PACK, rd);
        umr.setNum(umr.getNum() + 1);
        umr.setStatus(RechargeStatusEnum.DONE.getStatus());
        gameUserService.updateItem(umr);
        boxService.open(uid, cme.getGoodsId(), WayEnum.ROLE_TIME_LIMIT_PACK, rd);
        return rd;
    }

    @Override
    public RDRechargeActivity gainAwards(long uid, int realId) {
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getRoleTimeLimitMalls();
        Optional<CfgMallEntity> op = fMalls.stream().filter(p -> p.getGoodsId() == realId).findFirst();
        if (!op.isPresent()) {
            //不存在该商品
            throw new ExceptionForClientTip("rechargeActivity.not.award");
        }
        return gainFreeMallAwards(uid, op.get(), WayEnum.ROLE_TIME_LIMIT_PACK);
    }

    @Override
    protected boolean canGainFreeAwards(long uid, CfgMallEntity mallEntity) {
        UserMallRecord umr = mallService.getUserMallRecord(uid, mallEntity.getId());
        return umr.getStatus() == RechargeStatusEnum.CAN_GAIN_AWARD.getStatus();
    }

    @Override
    protected List<RDRechargeActivity.GiftPackInfo> toRdGoodsInfoList(long guId, List<CfgMallEntity> fMalls, MallEnum mallEnum, boolean isExtraDiscount) {
        List<RDRechargeActivity.GiftPackInfo> list = new ArrayList<>();
        List<UserMallRecord> userMallRecords = mallService.getUserMallRecord(guId, mallEnum);
        checkTimeoutRecharge(guId, userMallRecords);
        if (ListUtil.isNotEmpty(userMallRecords)) {
            userMallRecords = userMallRecords.stream()
                    .filter(p -> {
                        boolean isValid = p.ifValid();
                        isValid &= !(null != p.getStatus() && p.getStatus() == RechargeStatusEnum.DONE.getStatus());
                        Integer limit = MallTool.getMall(p.getBaseId()).getLimit();
                        if (limit > 0) {
                            isValid &= p.getNum() < limit;
                        }
                        return isValid;
                    })
                    .collect(Collectors.toList());
        }
        if (ListUtil.isEmpty(userMallRecords)) {
            return new ArrayList<>();
        }
        //按时间排序
        userMallRecords.sort(Comparator.comparing(UserMallRecord::getDateTime).reversed());
        for (UserMallRecord umr : userMallRecords) {
            CfgMallEntity mall = MallTool.getMall(umr.getBaseId());
            RDRechargeActivity.GiftPackInfo goodsInfo = RDRechargeActivity.GiftPackInfo.instance(mall, mall.getPrice(isExtraDiscount));
            //剩余时间
            goodsInfo.setRemainTime(umr.getDateTime().getTime() - System.currentTimeMillis());
            //剩余次数
            goodsInfo.updateRemainTimes(goodsInfo.getLimit() - umr.getNum());
            //设置状态
            if (umr.getStatus() != null && umr.getStatus() == RechargeStatusEnum.CAN_GAIN_AWARD.getStatus()) {
                goodsInfo.setStatus(umr.getStatus());
            }
            //设置奖励
            if (mall.getUnit() != ConsumeType.RMB.getValue()) {
                goodsInfo.setAwards(boxService.getAward(guId, mall.getGoodsId()));
            } else {
                goodsInfo.setAwards(productService.getProductAward(goodsInfo.getRechargeId()).getAwardList());
            }
            list.add(goodsInfo);
        }
        return list;
    }

    @Override
    public boolean updateRechargeStatus(long uid, int mallId) {
//        System.out.println("-----updateRechargeStatus,mallId:" + mallId);
        UserMallRecord userMallRecord = mallService.getUserMallRecord(uid, mallId);
        userMallRecord.setStatus(RechargeStatusEnum.CAN_GAIN_AWARD.getStatus());
        gameUserService.updateItem(userMallRecord);
        return true;
    }

//    @Override
//    protected RDRechargeActivity gainFreeMallAwards(long uid, CfgMallEntity mallEntity, WayEnum wayEnum) {
//        RDRechargeActivity rd = new RDRechargeActivity();
//        return rd;
//    }

    /**
     * 是否显示动态图标
     *
     * @param uid
     * @return
     */
    public boolean isShowIcon(long uid) {
        List<UserMallRecord> userMallRecords = mallService.getUserMallRecord(uid, MallEnum.ROLE_TIME_LIMIT_BAG);
        boolean isToShow = userMallRecords.stream().anyMatch(p -> p.ifValid() && p.getStatus() != null && p.getStatus() != RechargeStatusEnum.DONE.getStatus());
        return isToShow;
    }
}
