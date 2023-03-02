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
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 节日活动兑换
 * @date 2019/12/25 11:45
 */
@Service
public class HolidayExchangeMallProcessor extends AbstractMallProcessor {
    @Autowired
    private BoxService boxService;

    @Autowired
    private HolidayExchangeMallProcessor() {
        this.mallType = MallEnum.HOLIDAY_EXCHANGE;
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
        List<CfgMallEntity> list = MallTool.getMallConfig().getHolidayExchangeMalls().stream()
                .sorted(Comparator.comparing(CfgMallEntity::getId)).collect(Collectors.toList());
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
        if(AwardEnum.fromValue(mall.getItem()) == AwardEnum.TQ){
            ResEventPublisher.pubCopperAddEvent(guId,num,WayEnum.HOLIDAY_EXCHANGE,rd);
        }else {
            TreasureEventPublisher.pubTAddEvent(guId, mall.getGoodsId(), num, WayEnum.HOLIDAY_EXCHANGE, rd);
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
