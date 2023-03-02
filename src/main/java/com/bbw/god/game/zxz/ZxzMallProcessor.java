package com.bbw.god.game.zxz;

import java.util.Arrays;
import java.util.List;

import com.bbw.god.mall.processor.AbstractMallProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;

/**
 * 诛仙阵兑换购买
 *
 * @author suhq
 * @date 2019-10-24 11:32:55
 */
@Service
public class ZxzMallProcessor extends AbstractMallProcessor {

    @Autowired
    private AwardService awardService;

    ZxzMallProcessor() {
        this.mallType = MallEnum.ZXZ;
    }

    @Override
    public RDMallList getGoods(long guId) {
        RDMallList rd = new RDMallList();
        toRdMallList(guId, MallTool.getMallConfig().getZxzMalls(), false, rd);
        return rd;
    }

    @Override
    public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
        int num = mall.getNum() * buyNum;
        Award award = new Award(mall.getGoodsId(), AwardEnum.fromValue(mall.getItem()), num);
        String broadcastPrefix = "在" + WayEnum.EXCHANGE_ZXZ.getName();
        awardService.fetchAward(guId, Arrays.asList(award), WayEnum.EXCHANGE_ZXZ, broadcastPrefix, rd);
    }

    @Override
    protected List<UserMallRecord> getUserMallRecords(long guId) {
        return null;
    }

}
