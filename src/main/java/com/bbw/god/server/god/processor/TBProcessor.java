package com.bbw.god.server.god.processor;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.stereotype.Component;

/**
 * 天兵
 *
 * @author suhq
 * @date 2018年10月19日 下午2:12:51
 */
@Component
public class TBProcessor extends AbstractGodProcessor {

    public TBProcessor() {
        this.godType = GodEnum.TB;
    }

    @Override
    public void processor(GameUser gameUser, UserGod userGod, RDCommon rd) {
//		addGod(gameUser, userGod, rd);
        rd.setGodAttachInfo(userGod.getBaseId());
        ResEventPublisher.pubDiceAddEvent(gameUser.getId(), 18, WayEnum.TB, rd);

    }

}
