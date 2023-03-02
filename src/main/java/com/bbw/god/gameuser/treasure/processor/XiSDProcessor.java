package com.bbw.god.gameuser.treasure.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.CardSkillPosEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.card.event.EPCardSkillReset;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 洗髓丹
 *
 * @author suhq
 * @date 2019-10-08 10:56:34
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class XiSDProcessor extends TreasureUseProcessor {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserTreasureRecordService userTreasureRecordService;

    public XiSDProcessor() {
        this.treasureEnum = TreasureEnum.XiSD;
        this.isAutoBuy = true;
    }

    @Override
    public void check(GameUser gu, CPUseTreasure param) {
        String cardIdParam = param.getCardId();
        Integer cardId = Integer.valueOf(cardIdParam);
        UserCard uc = userCardService.getUserCard(gu.getId(), cardId);
        if (!uc.ifUseSkillScroll()) {
            throw new ExceptionForClientTip("treaure.xsd.unneed.resetSkill");
        }
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        String cardIdParam = param.getCardId();
        Integer cardId = Integer.valueOf(cardIdParam);
        UserCard uc = userCardService.getUserCard(gu.getId(), cardId);
        UserCard.UserCardStrengthenInfo info = uc.getStrengthenInfo();
        int useSkillScrollTimes = info.gainUseSkillScrollTimes();
        List<Integer> oldSkill = Arrays.asList(info.gainSkill0(), info.gainSkill5(), info.gainSkill10());
        // 清除限制卷轴使用记录
        userTreasureRecordService.deductSkillScrollRecord(uc, 0);
        userTreasureRecordService.deductSkillScrollRecord(uc, 5);
        userTreasureRecordService.deductSkillScrollRecord(uc, 10);

        uc.resetSkill();
        gameUserService.updateItem(uc);
        List<Integer> newSkill =  Arrays.asList(info.gainSkill0(), info.gainSkill5(), info.gainSkill10());
        rd.setStrengthenInfo(uc);
        BaseEventParam bep = new BaseEventParam(gu.getId(), WayEnum.RESET_CARD_SKILL, rd);
        CardEventPublisher.pubCardSkillResetEvent(new EPCardSkillReset(oldSkill, newSkill, useSkillScrollTimes, bep));
    }
}
