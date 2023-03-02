package com.bbw.god.mall.processor;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.RDSkyLanternWorkShopMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 节日礼包-51，商品业务相关
 *
 * @author longwh
 * @date 2022/12/15 14:30
 */
@Service
public class HolidayGiftPackMallProcessor extends AbstractMallProcessor {

    @Autowired
    private HolidayGiftPackMallProcessor() {
        this.mallType = MallEnum.HOLIDAY_GIFT_PACK_51;
    }

    /**
     * 获得商品列表
     *
     * @param uid
     * @return
     */
    @Override
    public RDMallList getGoods(long uid) {
        RDSkyLanternWorkShopMallList rd = new RDSkyLanternWorkShopMallList();
        //商品集合
        List<CfgMallEntity> list = MallTool.getMallConfig().getHolidayGiftPackMalls().stream()
                .sorted(Comparator.comparing(CfgMallEntity::getId)).collect(Collectors.toList());
        toRdMallList(uid, list, false, rd);
        return rd;
    }

    /**
     * 发放物品
     *
     * @param guId
     * @param mall
     * @param buyNum
     * @param rd
     */
    @Override
    public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
        int num = mall.getNum() * buyNum;
        if (AwardEnum.fromValue(mall.getItem()) == AwardEnum.TQ) {
            ResEventPublisher.pubCopperAddEvent(guId, num, WayEnum.HOLIDAY_GIFT_PACK_51, rd);
            return;
        }
        TreasureEventPublisher.pubTAddEvent(guId, mall.getGoodsId(), num, WayEnum.HOLIDAY_GIFT_PACK_51, rd);
    }

    /**
     * 获得可用的记录集
     *
     * @return
     */
    @Override
    protected List<UserMallRecord> getUserMallRecords(long guId) {
        List<UserMallRecord> userMallRecords = this.mallService.getUserMallRecord(guId, this.mallType);
        return userMallRecords.stream().filter(UserMallRecord::ifValid).collect(Collectors.toList());
    }
}