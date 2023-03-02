package com.bbw.god.mall.processor;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 荣耀币商店
 *
 * @author: huanghb
 * @date: 2022/12/6 10:25
 */
@Service
public class GloryCoinMallProcessor extends AbstractMallProcessor {
    @Autowired
    private BoxService boxService;
    @Autowired
    private AwardService awardService;

    @Autowired
    private GloryCoinMallProcessor() {
        this.mallType = MallEnum.GLORY_COIN_STORE;
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
        List<CfgMallEntity> list = MallTool.getMallConfig().getGloryCoinStoreMalls().stream()
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
        //数量
        int num = mall.getNum() * buyNum;
        //奖励
        Award award = new Award(mall.getGoodsId(), AwardEnum.fromValue(mall.getItem()), num);
        awardService.sendNeedMergedAwards(guId, Arrays.asList(award), WayEnum.GLORY_COIN_STORE, "", rd);
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
