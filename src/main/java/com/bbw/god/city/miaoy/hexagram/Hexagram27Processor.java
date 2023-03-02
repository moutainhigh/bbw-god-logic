package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.special.event.EVSpecialAdd;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 雷风恒卦
 *
 * 特产袋所有空位装满特产鱼
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram27Processor extends AbstractHexagram{
    @Autowired
    private UserSpecialService userSpecialService;

    @Override
    public int getHexagramId() {
        return 27;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_UP;
    }


    @Override
    public boolean canEffect(long uid) {
        return userSpecialService.getSpecialFreeSize(uid)>0;
    }

    @Override
    public void effect(long uid, RDHexagram rd) {
        int size = userSpecialService.getSpecialFreeSize(uid);
        List<EVSpecialAdd> specials=new ArrayList<>();
        for (int i = 0; i < size; i++) {
            CfgSpecialEntity special = SpecialTool.getSpecialById(6);
            specials.add(EVSpecialAdd.given(special.getId()));
        }
        SpecialEventPublisher.pubSpecialAddEvent(uid,specials,getWay(),rd);
    }


}
