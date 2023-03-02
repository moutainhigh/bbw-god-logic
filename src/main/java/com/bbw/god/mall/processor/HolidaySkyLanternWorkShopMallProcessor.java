package com.bbw.god.mall.processor;

import com.bbw.exception.ExceptionForClientTip;
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
 * 天灯工坊
 *
 * @author: huanghb
 * @date: 2022/2/9 14:30
 */
@Service
public class HolidaySkyLanternWorkShopMallProcessor extends AbstractMallProcessor {
    /** 天灯商店购买次数上限 */
    private long BUY_TIMES_LIMIT = 50;
    /** 天灯商品id */
    private static int SKY_LANTER_MALL_ID = 650001;

    @Autowired
    private HolidaySkyLanternWorkShopMallProcessor() {
        this.mallType = MallEnum.SKY_LANTERN_WORKSHOP;
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
        List<CfgMallEntity> list = MallTool.getMallConfig().getHolidaySkyLanternWorkShopMalls().stream()
                .filter(tmp -> tmp.getId() != SKY_LANTER_MALL_ID)
                .sorted(Comparator.comparing(CfgMallEntity::getId)).collect(Collectors.toList());
        toRdMallList(uid, list, false, rd);
        //购买次数权限
        List<UserMallRecord> userMallRecords = getUserMallRecords(uid);
        Integer buyNum = userMallRecords.stream().collect(Collectors.summingInt(UserMallRecord::getNum));
        rd.setBuyNum(buyNum);
        return rd;
    }

    @Override
    public UserMallRecord checkRecord(long guId, CfgMallEntity mall, int buyNum) {
        if (mall.getId() == SKY_LANTER_MALL_ID) {
            return super.checkRecord(guId, mall, buyNum);
        }
        //用户天灯材料购买记录
        List<UserMallRecord> userMallRecords = getUserMallRecords(guId);
        //购买次数
        long buyTimes = userMallRecords.stream().collect(Collectors.summingInt(UserMallRecord::getNum));
        //是否可以购买
        boolean isCanBuy = buyTimes + buyNum <= BUY_TIMES_LIMIT;
        if (!isCanBuy) {
            throw new ExceptionForClientTip("store.buy.times.limit");
        }
        return super.checkRecord(guId, mall, buyNum);
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
            ResEventPublisher.pubCopperAddEvent(guId, num, WayEnum.SKY_LANTERN_WORKSHOP, rd);
            return;
        }
        TreasureEventPublisher.pubTAddEvent(guId, mall.getGoodsId(), num, WayEnum.SKY_LANTERN_WORKSHOP, rd);
    }

    /**
     * 获得可用的记录集
     *
     * @return
     */
    @Override
    protected List<UserMallRecord> getUserMallRecords(long guId) {
        List<UserMallRecord> userMallRecords = this.mallService.getUserMallRecord(guId, this.mallType);
        return userMallRecords.stream().filter(UserMallRecord::ifValid).filter(tmp -> tmp.getBaseId() != SKY_LANTER_MALL_ID).collect(Collectors.toList());
    }
}
