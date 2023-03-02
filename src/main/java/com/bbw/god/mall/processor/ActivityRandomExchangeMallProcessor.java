package com.bbw.god.mall.processor;

import com.bbw.common.ListUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.random.box.BoxGood;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 随机兑换（节日-威震九州,無啥代志）
 *
 * @author: huanghb
 * @date: 2022/1/6 11:56
 */
@Service
public class ActivityRandomExchangeMallProcessor extends AbstractMallProcessor {
    @Autowired
    private BoxService boxService;
    @Autowired
    private AwardService awardService;

    @Autowired
    private ActivityRandomExchangeMallProcessor() {
        this.mallType = MallEnum.RANDOM_EXCHANGE;
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
        List<CfgMallEntity> list = MallTool.getMallConfig().getActivityRandomExchangeMalls().stream()
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
        //获得物品
        List<Award> awards = new ArrayList<>();
        for (int i = 0; i < buyNum; i++) {
            List<BoxGood> boxGoods = boxService.getBoxGoods(mall.getGoodsId());
            if (ListUtil.isEmpty(boxGoods)) {
                awards.add(new Award(mall.getGoodsId(), AwardEnum.fromValue(mall.getItem()), mall.getNum()));
            }
            awards.addAll(boxService.toAwards(guId, boxGoods));
        }
        //发放
        awardService.sendNeedMergedAwards(guId, awards, WayEnum.RANDOM_EXCHANGE, WayEnum.RANDOM_EXCHANGE.getName(), rd);
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
