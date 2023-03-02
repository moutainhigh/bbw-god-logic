package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.config.special.SpecialTypeEnum;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.special.event.EVSpecialAdd;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 雷火丰卦
 * 特产袋装满随机顶级特产
 * @author liuwenbin
 *
 */
@Service
public class Hexagram13Processor extends AbstractHexagram{
    @Autowired
    private UserSpecialService userSpecialService;
    @Override
    public int getHexagramId() {
        return 13;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.UP_UP;
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
            CfgSpecialEntity special = SpecialTool.getRandomSpecial(SpecialTypeEnum.TOP);
            specials.add(EVSpecialAdd.given(special.getId()));
        }
        SpecialEventPublisher.pubSpecialAddEvent(uid,specials,getWay(),rd);
    }
}
