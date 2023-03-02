package com.bbw.god.server.maou.alonemaou.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * @author lwb
 * @description: 通关
 **/
@Data
public class EPPassAloneMaou extends BaseEventParam {

    public static EPPassAloneMaou getInstance(BaseEventParam eventParam){
        EPPassAloneMaou epPassAloneMaou = new EPPassAloneMaou();
        epPassAloneMaou.setValues(eventParam);
        return epPassAloneMaou;
    }
}
