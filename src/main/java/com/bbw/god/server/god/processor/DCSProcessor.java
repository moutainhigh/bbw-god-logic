package com.bbw.god.server.god.processor;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.stereotype.Component;

/**
 * 大财神:送随机元素x1。20步内增加高星卡牌掉率。
 *
 * @author suhq
 * @date 2018年10月19日 下午2:08:45
 */
@Component
public class DCSProcessor extends AbstractGodProcessor {

    public DCSProcessor() {
        this.godType = GodEnum.DCS;
    }

    @Override
    public void processor(GameUser gameUser, UserGod userGod, RDCommon rd) {
//		addGod(gameUser, userGod, rd);
        rd.setGodAttachInfo(userGod.getBaseId());
        ResEventPublisher.pubCopperAddEvent(gameUser.getId(), 10000, WayEnum.DCS, rd);
    }

}
