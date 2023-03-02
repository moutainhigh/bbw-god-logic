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
 * 节日-材料购买商店
 *
 * @author: huanghb
 * @date: 2022/4/18 9:45
 */
@Service
public class HolidayMaterialsStoreMallProcessor extends AbstractMallProcessor {
    /** 材料购买商店购买次数上限 */
    private long BUY_TIMES_LIMIT = 10;

    @Autowired
    private HolidayMaterialsStoreMallProcessor() {
        this.mallType = MallEnum.MATERIAL_STORE;
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
        List<CfgMallEntity> list = MallTool.getMallConfig().getHolidayMaterialsStoreMalls().stream()
                .sorted(Comparator.comparing(CfgMallEntity::getId)).collect(Collectors.toList());
        toRdMallList(uid, list, false, rd);
        //购买次数权限
        List<UserMallRecord> userMallRecords = getUserMallRecords(uid);
        Integer buyNum = userMallRecords.stream().collect(Collectors.summingInt(UserMallRecord::getNum));
        rd.setBuyNum(buyNum);
        return rd;
    }

    /**
     * 检查购买次数
     *
     * @param guId
     * @param mall
     * @param buyNum
     * @return
     */
    @Override
    public UserMallRecord checkRecord(long guId, CfgMallEntity mall, int buyNum) {
        //用户材料购买记录
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
            ResEventPublisher.pubCopperAddEvent(guId, num, WayEnum.MATERIAL_STORE, rd);
            return;
        }
        TreasureEventPublisher.pubTAddEvent(guId, mall.getGoodsId(), num, WayEnum.MATERIAL_STORE, rd);
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
