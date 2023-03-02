package com.bbw.god.game.chanjie;

import com.bbw.common.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-05-26
 */
@Service
public class ChanjieStatusService {
    @Value("${bbw-god.chanjie.open:false}")
    private boolean isOpen;// 是否开放阐截斗法
    @Value("${bbw-god.chanjie.closeBeginDateTime:2020-01-01 00:00:00}")
    private String closeBeginDateTime;// 阐截斗法关闭起始时间
    @Value("${bbw-god.chanjie.closeEndDateTime:2020-01-01 00:00:00}")
    private String closeEndDateTime;// 阐截斗法结束起始时间

    /**
     * 是否开始状态：判断依据为isOpen设置为true且 当前时间不在关闭的时间区间内[closeBeginDateTime,closeEndDateTime] 则为开启，否则为关闭
     * @return
     */
    public boolean isOpen(){
        if (!isOpen){
            return false;
        }
        Date now=DateUtil.now();
        Date closeBeginDateTimeDate = DateUtil.fromDateTimeString(closeBeginDateTime);
        if (DateUtil.millisecondsInterval(closeBeginDateTimeDate,now)>0){
            //当前时间早于 关闭的开始时间，则说明还是开启状态
            return true;
        }
        Date closeEndDateTimeDate = DateUtil.fromDateTimeString(closeEndDateTime);
        if (DateUtil.millisecondsInterval(closeEndDateTimeDate,now)<0){
            //当前时间晚于 关闭的结束时间，则说明还是开启状态
            return true;
        }
        return false;
    }
}
