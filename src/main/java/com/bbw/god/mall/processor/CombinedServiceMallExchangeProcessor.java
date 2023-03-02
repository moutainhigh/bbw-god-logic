package com.bbw.god.mall.processor;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 节日活动兑换
 * @date 2019/12/25 11:45
 */
@Service
public class CombinedServiceMallExchangeProcessor extends AbstractMallProcessor {
    @Autowired
    private ActivityService activityService;

    @Autowired
    private CombinedServiceMallExchangeProcessor() {
        this.mallType = MallEnum.COMBINED_SERVICE_EXCHANGE;
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
        List<CfgMallEntity> list = MallTool.getMallConfig().getCombinedServiceExchangeMalls().stream()
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
            ResEventPublisher.pubCopperAddEvent(guId, num, WayEnum.COMBINED_SERVICE_EXCHANGE, rd);
        } else {
            TreasureEventPublisher.pubTAddEvent(guId, mall.getGoodsId(), num, WayEnum.COMBINED_SERVICE_EXCHANGE, rd);
        }
    }

    /**
     * 检查权限
     *
     * @param uid
     * @param mall
     */
    @Override
    public void checkAuth(long uid, CfgMallEntity mall) {
        int sid = gameUserService.getActiveSid(uid);
        IActivity a = activityService.getActivity(sid, ActivityEnum.COMBINED_SERVICE_EXCHANGE);
        Date beginTime = a.gainBegin();
        Date now = DateUtil.now();
        int daysBetween = DateUtil.getDaysBetween(beginTime, now) + 1;
        if (daysBetween < 5) {
            throw new ExceptionForClientTip("activity.function.not.open");
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
