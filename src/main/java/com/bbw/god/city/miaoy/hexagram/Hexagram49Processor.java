package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 火风鼎卦
 *
 * 遗失特产袋最贵重的两件特产
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram49Processor extends AbstractHexagram{
    @Autowired
    private UserSpecialService userSpecialService;

    @Override
    public int getHexagramId() {
        return 49;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_DOWN;
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
        List<CfgSpecialEntity> specials = SpecialTool.getHexagramSpecials();
        List<CfgSpecialEntity> specialEntities = specials.stream().sorted(Comparator.comparing(CfgSpecialEntity::getPrice).reversed()).collect(Collectors.toList());
        List<EPSpecialDeduct.SpecialInfo> specialInfos=new ArrayList<>();
        int need=2;
        for (CfgSpecialEntity specialEntity : specialEntities) {
            if (need<=0){
                break;
            }
            List<UserSpecial> list = ownSpecials.stream().filter(p -> p.getBaseId().equals(specialEntity.getId())).collect(Collectors.toList());
            for (UserSpecial special : list) {
                if (need<=0){
                    break;
                }
                specialInfos.add(EPSpecialDeduct.SpecialInfo.getInstance(special.getId(),special.getBaseId(),null));
                need--;
            }
        }
        EPSpecialDeduct epSpecialDeduct = EPSpecialDeduct.instance(new BaseEventParam(uid, getWay(),rd),specialInfos);
        SpecialEventPublisher.pubSpecialDeductEvent(epSpecialDeduct);
    }


}
