package com.bbw.god.rechargeactivities.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.treasure.UserTreasureRecord;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import com.bbw.god.gameuser.treasure.processor.TreasureProcessorFactory;
import com.bbw.god.gameuser.treasure.processor.TreasureUseProcessor;
import com.bbw.god.mall.store.RDStoreGoodsInfo;
import com.bbw.god.mall.store.StoreEnum;
import com.bbw.god.mall.store.StoreLogic;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rechargeactivities.RDWarToken;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 说明： 战令-兑换
 *
 * @author lwb
 * date 2021-06-02
 */
@Service
public class WarTokenExchangeProcessor extends AbstractWarTokenProcessor {
    @Autowired
    private StoreLogic storeLogic;
    @Autowired
    protected BoxService boxService;
    @Autowired
    private TreasureProcessorFactory treasureProcessorFactory;
    @Autowired
    private UserTreasureRecordService userTreasureRecordService;


    @Override
    public RechargeActivityItemEnum getCurrentEnum() {
        return RechargeActivityItemEnum.WAR_TOKEN_EXCHANGE;
    }

    @Override
    public RDWarToken listAwards(long uid) {
        RDWarToken rdWarToken = RDWarToken.getInstance(getUserWarToken(uid));
        IActivity activity = activityService.getGameActivity(gameUserService.getActiveSid(uid), ActivityEnum.WAR_TOKEN);
        List<RDStoreGoodsInfo> rdStore = getRdStoreGoodsInfos(uid);
        rdWarToken.setGoodsInfoList(rdStore);
        if (activity != null) {
            rdWarToken.setEndDateTime(activity.gainEnd());
        } else {
            rdWarToken.setEndDateTime(DateUtil.now());
        }
        return rdWarToken;
    }

    /**
     * 添加礼包内物品展示
     *
     * @param uid
     * @return
     */
    private List<RDStoreGoodsInfo> getRdStoreGoodsInfos(long uid) {
        List<RDStoreGoodsInfo> rdStore = storeLogic.getGoodsList(uid, StoreEnum.WAR_TOKEN.getType()).getIntegralGoods();
        //添加礼包内物品展示
        for (RDStoreGoodsInfo rs : rdStore) {
            List<Award> awardList = boxService.getBoxAllGoods(uid, rs.getRealId());
            if (ListUtil.isNotEmpty(awardList)) {
                rs.setGiftAwards(awardList);
            }
            TreasureUseProcessor tup = treasureProcessorFactory.getTreasureUseProcessor(rs.getRealId());
            if (null == tup) {
                continue;
            }
            //是否有保底次数
            if (0 == tup.minGuaranteeNum()) {
                continue;
            }
            //获取保底次数
            rs.setMinGuaranteeNum(tup.minGuaranteeNum());
            //获取保底记录
            UserTreasureRecord utr = userTreasureRecordService.getOrCreateRecord(uid, rs.getRealId(), 0);
            Integer giftUsedTimes = utr.getUseTimes();
            rs.setGiftUseTimes(giftUsedTimes);
        }
        return rdStore;
    }

}
