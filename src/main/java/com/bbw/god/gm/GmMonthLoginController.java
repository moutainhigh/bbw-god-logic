package com.bbw.god.gm;

import com.bbw.god.activity.monthlogin.MonthLoginLogic;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author：lwb
 * @date: 2021/2/26 11:09
 * @version: 1.0
 */
@RestController
@RequestMapping("/gm/monthlogin")
public class GmMonthLoginController {

    @Autowired
    private MonthLoginLogic monthLoginLogic;

    @RequestMapping("/init!event")
    public RDSuccess initEvent(int sid){
        monthLoginLogic.initTodayEvent(sid);
        return new RDSuccess();
    }
}
