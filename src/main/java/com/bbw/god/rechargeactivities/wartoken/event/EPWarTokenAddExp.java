package com.bbw.god.rechargeactivities.wartoken.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-03
 */
@Data
public class EPWarTokenAddExp extends BaseEventParam {
    private int addVal=0;
    private boolean addWeekExp=false;

    public static EPWarTokenAddExp getInstance(int addVal,boolean addWeekExp,BaseEventParam eventParam){
        EPWarTokenAddExp ep=new EPWarTokenAddExp();
        ep.setAddVal(addVal);
        ep.setAddWeekExp(addWeekExp);
        ep.setValues(eventParam);
        return ep;
    }
}
