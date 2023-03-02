package com.bbw.god.activity.holiday.processor.HolidayGroceryShop;

import com.bbw.common.DateUtil;
import com.bbw.god.cache.tmp.AbstractTmpDataRedisService;
import com.bbw.god.cache.tmp.TmpDataType;
import org.springframework.stereotype.Service;

/**
 * 玩家杂货小铺service
 * @author: hzf
 * @create: 2022-12-09 09:43
 **/
@Service
public class HolidayUserGroceryShopService extends AbstractTmpDataRedisService<HolidayUserGroceryShop,Long> {
    @Override
    protected Class<HolidayUserGroceryShop> getDataClazz() {
        return HolidayUserGroceryShop.class;
    }

    @Override
    protected Long getDataBelong(HolidayUserGroceryShop data) {
        return data.getGameUserId();
    }

    @Override
    protected String getDataLoop(HolidayUserGroceryShop data) {
        return "";
    }

    @Override
    protected String getRedisKey(Long belong, TmpDataType dataType, String... loop) {
         return "usr:" + belong +":"+ dataType.getRedisKey();
    }

    @Override
    protected Long getField(HolidayUserGroceryShop data) {
        return data.getId();
    }

    @Override
    protected long getExpiredMillis(HolidayUserGroceryShop data) {
        return  DateUtil.SECOND_ONE_DAY * 7 * 1000L;
    }
}
