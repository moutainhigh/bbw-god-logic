package com.bbw.god.pay;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.processor.FirstRechargeProcessor;
import com.bbw.god.activity.holiday.processor.HolidayGiftPackProcessor;
import com.bbw.god.activity.processor.NightmareFirstRechargeProcessor;
import com.bbw.god.game.config.CfgProductGroup.CfgProduct;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.FavorableBagEnum;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.pay.UserPayInfo;
import com.bbw.god.gameuser.pay.UserPayInfoService;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rechargeactivities.wartoken.WarTokenLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bbw.god.game.config.CfgProductGroup.CfgProduct.WAR_TOKEN;
import static com.bbw.god.game.config.CfgProductGroup.CfgProduct.WAR_TOKEN_SUP;

/**
 * 直接充值
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-25 11:19
 */
@Service
public class ZhiCongService {
    // 直接充值产品ID大于99000000
    private static final int ZHI_CHONG_MIN_ID = 99000000;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserPayInfoService userPayInfoService;
    @Autowired
    private MallService mallService;
    @Autowired
    private FirstRechargeProcessor firstRechargeProcessor;
    @Autowired
    private NightmareFirstRechargeProcessor nightmareFirstRechargeProcessor;
    @Autowired
    private HolidayGiftPackProcessor holidayGiftPackProcessor;
    @Autowired
    private WarTokenLogic warTokenLogic;
    /**
     * 是否可以购买此产品
     *
     * @param uid:区服玩家ID
     * @param productId:区服的直充产品ID
     * @return
     */
    public String canBuy(long uid, int productId) {
        if (!firstRechargeProcessor.canBuy(uid, productId) || !nightmareFirstRechargeProcessor.canBuy(uid, productId)
                || !holidayGiftPackProcessor.canBuy(uid, productId)){
            return "不能重复购买";
        }
        if (WAR_TOKEN==productId || WAR_TOKEN_SUP==productId){
            if (warTokenLogic.checkCanBuyWarToken(uid)) {
                return "";
            }
            return "当前不能购买";
        }
        CfgProduct product = productService.getCfgProduct(productId);
        if (productId > ZHI_CHONG_MIN_ID) {
            int goodsId = MallTool.getGoodsId(productId);
            FavorableBagEnum favorableBag = FavorableBagEnum.fromValue(goodsId);
            CfgMallEntity mall = MallTool.getMall(favorableBag.getType(), goodsId);
            UserMallRecord umRecord = checkZhiChongRecord(uid, mall);
            if (umRecord != null && umRecord.ifOutOfLimit()) {
                return product.getName() + "已达购买次数限制，无法再购买";
            }
        } else {
            // 季卡月卡到期前3天可购
            UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(uid);
            if (product.isYueKa()) {
                if (userPayInfo.getYkEndTime() != null) {
                    int days = DateUtil.getDaysBetween(DateUtil.now(), userPayInfo.getYkEndTime());
                    if (days > 2) {
                        return "月卡到期前3天可再次购";
                    }
                }
            } else if (product.isJiKa()) {
                if (userPayInfo.getJkEndTime() != null) {
                    int days = DateUtil.getDaysBetween(DateUtil.now(), userPayInfo.getJkEndTime());
                    if (days > 2) {
                        return "季卡到期前3天可再次购";
                    }
                }
            }

        }
        return "";
    }

    /**
     * 直充产品购买记录
     *
     * @param guId 玩家ID
     * @param mall
     * @return
     */
    public UserMallRecord checkZhiChongRecord(long guId, CfgMallEntity mall) {
        List<UserMallRecord> records = mallService.getUserValidMallRecord(guId, MallEnum.fromValue(mall.getType()));
        return mallService.checkRecord(guId, mall, 1, records);
    }
}