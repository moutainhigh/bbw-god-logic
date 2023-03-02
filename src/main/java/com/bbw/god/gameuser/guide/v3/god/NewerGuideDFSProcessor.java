package com.bbw.god.gameuser.guide.v3.god;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.guide.GuideConfig;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 新手引导大福神处理器
 * @date 2020/12/11 17:57
 **/
//@Service
public class NewerGuideDFSProcessor extends BaseNewerGuideGodProcessor{
    @Autowired
    private GuideConfig guideConfig;

    public NewerGuideDFSProcessor() {
        this.godType = GodEnum.DFS;
    }

    @Override
    public void processor(GameUser gu, UserGod userGod, RDCommon rd) {
        RDAdvance rdAdvance = (RDAdvance) rd;
        rd.setGodAttachInfo(userGod.getBaseId());
        List<Integer> cardIds = getCardsForDFS(gu.getRoleInfo().getCountry());
        CardEventPublisher.pubCardAddEvent(gu.getId(), cardIds, WayEnum.DFS, "遇到" + WayEnum.DFS.getName(), rd);
        setAttachGod(rdAdvance);
    }

    private List<Integer> getCardsForDFS(int country) {
        List<Integer> awardCards = new ArrayList<>();
        // 第一张卡 秦天君
        awardCards.add(418);
        // 第二张卡 二星属性联功卡
        awardCards.add(this.guideConfig.getDropCardsAsDfs().get(country / 10 - 1));
        return awardCards;
    }
}
