package com.bbw.god.server.god.processor;

import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.rd.RDCommon;
import org.springframework.stereotype.Component;

/**
 * 死神
 *
 * @author suhq
 * @date 2018年10月19日 下午2:13:26
 */
@Component
public class SISProcessor extends AbstractGodProcessor {

    public SISProcessor() {
        this.godType = GodEnum.SIS;
    }

    @Override
    public void processor(GameUser gameUser, UserGod userGod, RDCommon rd) {
//		addGod(gameUser, userGod, rd);
        rd.setGodAttachInfo(userGod.getBaseId());
    }

}
