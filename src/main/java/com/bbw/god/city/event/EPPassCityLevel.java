package com.bbw.god.city.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 *
 * 通过城池关卡事件
 * @author：lwb
 * @date: 2020/12/18 11:19
 * @version: 1.0
 */
@Data
public class EPPassCityLevel extends BaseEventParam {
    private int cityId;
    private int passLevel;
    private boolean nightmare=false;
    public static EPPassCityLevel instance(BaseEventParam bep,int cityId,int passLevel,boolean nightmare){
        EPPassCityLevel passCityLevel=new EPPassCityLevel();
        passCityLevel.setValues(bep);
        passCityLevel.setPassLevel(passLevel);
        passCityLevel.setCityId(cityId);
        passCityLevel.setNightmare(nightmare);
        return passCityLevel;
    }
}
