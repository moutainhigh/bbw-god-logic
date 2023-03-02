package com.bbw.god.city.miaoy.hexagram;

import com.bbw.common.PowerRandom;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 坎为水卦
 *
 * 遗失3件随机特产
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram55Processor extends AbstractHexagram{
    @Autowired
    private UserSpecialService userSpecialService;

    @Override
    public int getHexagramId() {
        return 55;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.DOWN_DOWN;
    }

    @Override
    public boolean canEffect(long uid) {
        List<UserSpecial> ownSpecials = userSpecialService.getOwnSpecials(uid);
        List<Integer> ids = SpecialTool.getHexagramSpecialIds();
        Optional<UserSpecial> optional = ownSpecials.stream().filter(p -> ids.contains(p.getBaseId())).findFirst();
        return optional.isPresent();
    }

    @Override
    public void effect(long uid, RDHexagram rd) {
        List<UserSpecial> ownSpecials = userSpecialService.getOwnSpecials(uid);
        List<Integer> ids = SpecialTool.getHexagramSpecialIds();
        List<UserSpecial> collect = ownSpecials.stream().filter(p -> ids.contains(p.getBaseId())).collect(Collectors.toList());
        List<UserSpecial> list = PowerRandom.getRandomsFromList(3, collect);
        List<EPSpecialDeduct.SpecialInfo> specialInfos=new ArrayList<>();
        for (UserSpecial userSpecial : list) {
            specialInfos.add(EPSpecialDeduct.SpecialInfo.getInstance(userSpecial.getId(),userSpecial.getBaseId(),null));
        }
        EPSpecialDeduct epSpecialDeduct = EPSpecialDeduct.instance(new BaseEventParam(uid, getWay(),rd),specialInfos);
        SpecialEventPublisher.pubSpecialDeductEvent(epSpecialDeduct);
    }


}
