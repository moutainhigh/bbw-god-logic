package com.bbw.god.activity.processor;

import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.mall.processor.MallProcessorFactory;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author suhq
 * @description: 星君宝库
 * @date 2019-11-07 09:20
 **/
@Service
public class XingJBKProcessor extends AbstractActivityProcessor {
    @Autowired
    private MallProcessorFactory mallProcessorFactory;

    public XingJBKProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.XingJBK);
    }

    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        return this.mallProcessorFactory.getMallProcessor(MallEnum.XJBK.getValue()).getGoods(uid);
    }

}
