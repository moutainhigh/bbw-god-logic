package com.bbw.god.activity.holiday.processor.holidaydaydoublegold;

import com.bbw.common.DateUtil;
import com.bbw.god.cache.tmp.AbstractTmpDataRedisService;
import com.bbw.god.cache.tmp.TmpDataType;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 玩家双倍元宝service
 *
 * @author: huanghb
 * @date: 2023/1/5 9:59
 */
@Service
public class HolidayUserDayDoubleGoldService extends AbstractTmpDataRedisService<HolidayUserDayDoubleGold, Long> {
    @Override
    protected Class<HolidayUserDayDoubleGold> getDataClazz() {
        return HolidayUserDayDoubleGold.class;
    }

    @Override
    protected Long getDataBelong(HolidayUserDayDoubleGold data) {
        return data.getGameUserId();
    }

    @Override
    protected String getDataLoop(HolidayUserDayDoubleGold data) {
        return "";
    }

    @Override
    protected String getRedisKey(Long belong, TmpDataType dataType, String... loop) {
        return "usr:" + belong + ":" + dataType.getRedisKey();
    }

    @Override
    protected Long getField(HolidayUserDayDoubleGold data) {
        return Long.valueOf(DateUtil.toDateInt(data.getGenerateTime()));
    }

    @Override
    protected long getExpiredMillis(HolidayUserDayDoubleGold data) {
        return DateUtil.SECOND_ONE_DAY * 7 * 1000L;
    }

    /**
     * 获得玩家今天双倍元宝信息
     *
     * @param uid
     * @return
     */
    protected HolidayUserDayDoubleGold getHolidayUserDayDoubleGold(long uid) {
        Long today = Long.valueOf(DateUtil.toDateInt(DateUtil.now()));
        Optional<HolidayUserDayDoubleGold> optional = getData(uid, today);
        if (optional.isPresent()) {
            return optional.get();
        }
        HolidayUserDayDoubleGold holidayUserDayDoubleGold = HolidayUserDayDoubleGold.instance(uid);
        addData(holidayUserDayDoubleGold);
        return holidayUserDayDoubleGold;
    }
}
