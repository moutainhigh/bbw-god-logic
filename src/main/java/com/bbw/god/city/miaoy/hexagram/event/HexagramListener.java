package com.bbw.god.city.miaoy.hexagram.event;

import com.bbw.god.city.miaoy.hexagram.HexagramBuffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 卦象监听
 * @author liuwenbin
 */
@Component
public class HexagramListener {
    @Autowired
    private HexagramBuffService hexagramBuffService;

    @EventListener
    public void hexagramBuffDeduct(HexagramBuffDeductEvent event){
        EPHexagramBuffDeduct ep = event.getEP();
        long uid=ep.getGuId();
        hexagramBuffService.deductBuffTimes(uid,ep.getHexagramId(),ep.getTimes());
    }
}
