package com.bbw.god.activity.holiday.processor;

import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 跨服恶魔（抵抗恶魔,年兽来袭,抵御冠族）
 *
 * @author fzj
 * @date 2021/12/20 10:07
 */
@Service
public class HolidayGameMaouProcessor extends AbstractActivityProcessor {

    public HolidayGameMaouProcessor() {
        this.activityTypeList = Arrays.asList(
                ActivityEnum.RESIST_DEVIL
                , ActivityEnum.YEAR_BEAST
                , ActivityEnum.GUAN_ZU
                , ActivityEnum.COOL_SUMMER
                , ActivityEnum.LITTLE_REINDEER);
    }

    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }
}
