package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.common.PowerRandom;
import com.bbw.god.city.mixd.nightmare.MiXianLevelData;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianPosEnum;
import com.bbw.god.city.mixd.nightmare.RDNightmareMxd;
import com.bbw.god.city.mixd.nightmare.UserNightmareMiXian;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：30层 守卫出
 * 珍贵宝箱:从奖励列表中，根据概率收集1份奖励，收集到的奖励会显示在藏宝背包中。
 * @author lwb
 * date 2021-05-27
 */
@Service
public class BoxRichProcessor extends AbstractMiXianPosProcessor {
    @Autowired
    private UserCardService userCardService;

    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.BOX_RICH.equals(miXianPosEnum);
    }

    /**
     * 552卡牌-邱引*1	90%
     * 传奇卷轴宝箱*1	10%
     * 首次必得邱引，后续按概率获得
     * 保底设置：12次内必出一个传奇卷轴宝箱。
     * @param nightmareMiXian
     * @param rd
     * @param posData
     */
    @Override
    public void touchPos(UserNightmareMiXian nightmareMiXian,RDNightmareMxd rd, MiXianLevelData.PosData posData) {
        UserCard userCard = userCardService.getUserCard(nightmareMiXian.getGameUserId(), 552);
        List<Award> awards=new ArrayList<>();
        //12次未出传奇卷轴宝箱 按 概率获得卡牌或者传奇卷轴宝箱。 超过12次则直接出传奇卷轴宝箱。
        nightmareMiXian.setRichBoxNotGainSkill(nightmareMiXian.getRichBoxNotGainSkill()+1);
        if ((userCard==null||PowerRandom.hitProbability(90) )&& nightmareMiXian.getRichBoxNotGainSkill() < 12 ){
            awards.add(Award.instance(552,AwardEnum.KP,1));
        }else {
            awards.add(Award.instance(TreasureEnum.LEGEND_SKILL_SCROLL_BOX.getValue(), AwardEnum.FB,1));
            nightmareMiXian.setRichBoxNotGainSkill(0);
        }
        rd.setGainAwards(awards);
        nightmareMiXian.addAwardToBag(awards);
        nightmareMiXian.takeCurrentPosToEmptyType();
    }
}
