package com.bbw.god.mall.processor;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 花赋予神活动兑换
 *
 * @author: huanghb
 * @date: 2022/3/7 9:47
 */
@Service
public class HolidayFlowerToGodMallProcessor extends AbstractMallProcessor {
    @Autowired
    private HolidayFlowerToGodMallProcessor() {
        this.mallType = MallEnum.FLOWER_TO_GOD;
    }

    /**
     * 获得商品列表
     *
     * @param guId
     * @return
     */
    @Override
    public RDMallList getGoods(long guId) {
        RDMallList rd = new RDMallList();
        List<CfgMallEntity> list = MallTool.getMallConfig().getFlowerToGodMalls().stream()
                .sorted(Comparator.comparing(CfgMallEntity::getSerial)).collect(Collectors.toList());
        toRdMallList(guId, list, false, rd);
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
            ResEventPublisher.pubCopperAddEvent(guId, num, WayEnum.FLOWER_TO_GOD, rd);
        } else {
            TreasureEventPublisher.pubTAddEvent(guId, mall.getGoodsId(), num, WayEnum.FLOWER_TO_GOD, rd);
        }
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
