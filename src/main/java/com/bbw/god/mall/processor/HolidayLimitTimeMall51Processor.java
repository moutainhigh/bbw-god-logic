package com.bbw.god.mall.processor;

import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 节日限时礼包51
 *
 * @author: huanghb
 * @date: 2022/12/9 11:39
 */
@Service
public class HolidayLimitTimeMall51Processor extends HolidayLimitTimeMallProcessor {

    HolidayLimitTimeMall51Processor() {
        this.mallType = MallEnum.HOLIDAY_MALL_LIMIT_PACK;
        activityType = ActivityEnum.LIMIT_TIME_MALL_PACK_51;
    }

    /**
     * 获得商城信息
     *
     * @return
     */
    @Override
    protected List<CfgMallEntity> geMalls() {
        return MallTool.getMallConfig().getHolidayLimitTimeMalls();
    }
}
