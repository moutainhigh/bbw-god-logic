package com.bbw.god.mall.processor;

import com.bbw.god.activity.ActivityService;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 萌新礼包
 * @date 2019/12/25 11:45
 */
@Service
public class MXLBMallProcessor extends AbstractMallProcessor {
    @Autowired
    private MallService mallService;
    @Autowired
    private ActivityService activityService;

    MXLBMallProcessor() {
        this.mallType = MallEnum.MXLB;
    }

    @Override
    public RDMallList getGoods(long guId) {
        return null;
    }

    @Override
    protected List<UserMallRecord> getUserMallRecords(long guId) {
        List<UserMallRecord> favorableRecords = mallService.getUserMallRecord(guId, mallType);
        return favorableRecords.stream().filter(UserMallRecord::ifValid).collect(Collectors.toList());
    }

    @Override
    public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
        // 不会走这边
    }
}
