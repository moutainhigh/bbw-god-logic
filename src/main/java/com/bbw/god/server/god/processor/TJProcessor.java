package com.bbw.god.server.god.processor;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.ResWayType;
import com.bbw.god.gameuser.res.gold.EPGoldAdd;
import com.bbw.god.rd.RDCommon;
import org.springframework.stereotype.Component;

/**
 * 天将
 *
 * @author suhq
 * @date 2018年10月19日 下午2:12:41
 */
@Component
public class TJProcessor extends AbstractGodProcessor {

    public TJProcessor() {
        this.godType = GodEnum.TJ;
    }

    @Override
    public void processor(GameUser gameUser, UserGod userGod, RDCommon rd) {
//		addGod(gameUser, userGod, rd);
        rd.setGodAttachInfo(userGod.getBaseId());
        BaseEventParam bep = new BaseEventParam(gameUser.getId(), WayEnum.TJ, rd);
        EPGoldAdd evGoldAdd = new EPGoldAdd(bep, 0);
        evGoldAdd.addGold(ResWayType.God, 30);
        ResEventPublisher.pubGoldAddEvent(evGoldAdd);

    }

}
