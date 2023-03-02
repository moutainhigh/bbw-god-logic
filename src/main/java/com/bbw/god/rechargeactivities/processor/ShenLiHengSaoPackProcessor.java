package com.bbw.god.rechargeactivities.processor;

import com.bbw.App;
import com.bbw.god.game.config.CfgProductGroup;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.rechargeactivities.RDRechargeActivity;
import com.bbw.god.rechargeactivities.RechargeActivityEnum;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 神力横扫礼包
 *
 * @author lwb
 * @date 2020/7/2 11:55
 */
@Service
public class ShenLiHengSaoPackProcessor extends AbstractRechargeActivityProcessor {
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private App app;
    private static int ER_LAI_ID = 127;//恶来
    private static int FEI_LIAN_ID = 128;//蜚廉
    /** 神恶来 */
    private static int SHEN_ER_LAI_ID = 10127;
    /** 神蜚廉 */
    private static int SHEN_FEI_LIAN_ID = 10128;
    private static int GOODS_ID = 1292;//礼包ID

    @Override
    public RechargeActivityEnum getParent() {
        return RechargeActivityEnum.DIAMOND_PACK;
    }

    @Override
    public RechargeActivityItemEnum getCurrentEnum() {
        return RechargeActivityItemEnum.SHEN_LI_HENG_SAO_PACK;
    }

    @Override
    public boolean isShow(long uid) {
        GameUser gu = gameUserService.getGameUser(uid);
        //当玩家充值满30元或等级达到20级时，显示神力横扫礼包
        if (gu.getLevel() < getShowNeedLevel() && rechargeStatisticService.getTotalRecharge(uid) < getShowNeedRecharge()) {
            return false;
        }
        //当玩家2张卡都获得后  神力横扫不再显示
        if (null != userCardService.getUserCard(uid, ER_LAI_ID) && null != userCardService.getUserCard(uid, FEI_LIAN_ID)) {
            return false;
        }
        if (null != userCardService.getUserCard(uid, SHEN_ER_LAI_ID) && null != userCardService.getUserCard(uid, SHEN_FEI_LIAN_ID)) {
            return false;
        }
        return true;
    }

    /**
     * 获取展示的等级所需值
     *
     * @return
     */
    @Override
    protected int getShowNeedLevel() {
        return 20;
    }

    /**
     * 获取展示的充值所需值
     *
     * @return
     */
    @Override
    protected int getShowNeedRecharge() {
        return 30;
    }

    @Override
    public RDRechargeActivity listAwards(long uid) {
        RDRechargeActivity rd = new RDRechargeActivity();
        List<RDRechargeActivity.GiftPackInfo> list = new ArrayList<>(1);
        CfgMallEntity mallEntity = MallTool.getMall(MallEnum.ACTIVITY_BAG.getValue(), GOODS_ID);
        RDRechargeActivity.GiftPackInfo goodsInfo = RDRechargeActivity.GiftPackInfo.instance(mallEntity, mallEntity.getPrice());
        CfgProductGroup.ProductAward productAward = productService.getProductAward(goodsInfo.getRechargeId());
        goodsInfo.setAwards(productAward.getAwardList());
        if (!isShow(uid)) {
            goodsInfo.setStatus(-1);
        }
        list.add(goodsInfo);
        rd.setGoodsList(list);
        rd.setCountdown(-1L);
        return rd;
    }

    /**
     * 通过元宝或钻石购买的走该方法
     *
     * @param uid
     * @param mallId
     * @return
     */
    @Override
    public RDRechargeActivity buyAwards(long uid, int mallId) {
        RDRechargeActivity rd = new RDRechargeActivity();
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getActivityMalls();
        buyAwards(fMalls, mallId, uid, rd, WayEnum.DAILY_DIAMOND_GIFT_PACK, MallEnum.DAILY_RECHARGE_BAG);
        return rd;
    }
}
