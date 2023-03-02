package com.bbw.god.gameuser.guide.v3.god;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 新手引导百宝箱处理器
 * @date 2020/12/11 18:36
 **/
//@Service
public class NewerGuideBBXProcessor extends BaseNewerGuideGodProcessor{
    @Autowired
    private AwardService awardService;

    public NewerGuideBBXProcessor() {
        this.godType = GodEnum.BBX;
    }

    @Override
    public void processor(GameUser gu, UserGod userGod, RDCommon rd) {
        rd.setAttachedGod(GodEnum.BBX.getValue());
        List<Award> awards = new ArrayList<>();
        awards.add(new Award(AwardEnum.TQ, 30000));
        awards.add(new Award(AwardEnum.TL, 30));
        awards.add(new Award(TreasureEnum.WNLS1.getValue(), AwardEnum.FB, 1));
        awards.add(new Award(TreasureEnum.ZXJ.getValue(), AwardEnum.FB, 1));
        this.awardService.fetchAward(gu.getId(), awards, WayEnum.BBX_PICK, "", rd);
        // 元素单独处理
        ResEventPublisher.pubEleAddEvent(gu.getId(), TypeEnum.Earth.getValue(), 2, WayEnum.BBX_PICK, rd);
        RDAdvance rdAdvance = (RDAdvance) rd;
        setAttachGod(rdAdvance);
    }
}
