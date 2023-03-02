package com.bbw.god.mall.processor;

import com.bbw.god.activity.ActivityService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 合服-折扣变化商店
 *
 * @author: huanghb
 * @date: 2022/2/17 14:37
 */
@Service
public class CombinedServiceDiscountChangeMallProcessor extends AbstractMallProcessor {
    @Autowired
    MallService mallService;
    @Autowired
    GameUserService gameUserService;
    @Autowired
    ActivityService activityService;

    @Autowired
    private CombinedServiceDiscountChangeMallProcessor() {
        this.mallType = MallEnum.SPECIAL_DISCOUNT;
    }

    @Override
    public RDMallList getGoods(long guId) {
        RDMallList rd = new RDMallList();
        List<CfgMallEntity> list = MallTool.getMallConfig().getCombinedServiceDiscountChangeMalls().stream()
                .sorted(Comparator.comparing(CfgMallEntity::getSerial)).collect(Collectors.toList());
        toRdMallList(guId, list, false, rd);
        return rd;
    }

    @Override
    public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
        int num = mall.getNum() * buyNum;
        TreasureEventPublisher.pubTAddEvent(guId, mall.getGoodsId(), num, WayEnum.DISCOUNT_CHANGE_MALL, rd);
    }

    @Override
    protected List<UserMallRecord> getUserMallRecords(long guId) {
        List<UserMallRecord> userMallRecords = this.mallService.getUserMallRecord(guId, this.mallType);
        return userMallRecords.stream().filter(UserMallRecord::ifValid).collect(Collectors.toList());
    }
}
