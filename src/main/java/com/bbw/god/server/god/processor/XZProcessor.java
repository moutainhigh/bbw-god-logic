package com.bbw.god.server.god.processor;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.stereotype.Component;

/**
 * 仙长：送随机法宝一个 34/9/6
 *
 * @author suhq
 * @date 2018年10月19日 下午2:10:05
 */
@Component
public class XZProcessor extends AbstractGodProcessor {

    public XZProcessor() {
        this.godType = GodEnum.XZ;
    }

    @Override
    public void processor(GameUser gameUser, UserGod userGod, RDCommon rd) {
//		addGod(gameUser, userGod, rd);
        rd.setGodAttachInfo(userGod.getBaseId());

        CfgTreasureEntity treasure = TreasureTool.getRandomOldTreasure(60, 90, 340);
        TreasureEventPublisher.pubTAddEvent(gameUser.getId(), treasure.getId(), 1, WayEnum.XZ, rd);

    }

}
