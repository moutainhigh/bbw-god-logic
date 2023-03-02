package com.bbw.god.gameuser.chamberofcommerce.server;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.chamberofcommerce.RDCoc.CocReward;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年3月13日 上午9:32:55
 * 类说明
 */
@Service
public class UserCocAwardService {

    public void gainReward(List<Award> awards, long uid, RDCommon rd, WayEnum way) {
        gainCardReward(awards, uid, rd, "", way);
    }

    public void gainCardReward(List<Award> awards, long uid, RDCommon rd, String broadcastWayInfo, WayEnum way) {
        if (ListUtil.isEmpty(awards)) {
            throw new ExceptionForClientTip("award.service.no.awards");
        }
        for (Award award : awards) {
            // YB("元宝",10), TQ("铜钱",20), XDD("行动点",30), KP("卡牌", 40),
            // YS("元素",50),FB("法宝",60); HY("每日任务活跃度", 100);
            AwardEnum awardType = AwardEnum.fromValue(award.getItem());
            switch (awardType) {
                case TQ:
                    ResEventPublisher.pubCopperAddEvent(uid, (long) award.getNum(), way, rd);
                    break;
                case KP:
                    CardEventPublisher.pubCardAddEvent(uid, award.getAwardId(), way, broadcastWayInfo, rd);
                    break;
                case FB:
                    if (award.getAwardId() == TreasureEnum.SHQD.getValue()) {
                        // 钱袋
                        long addCopper = 0;
                        for (int i = 0; i < award.getNum(); i++) {
                            addCopper += new Random().nextInt(100001) + 100000;// 钱袋开启10W~20W铜钱
                        }
                        if (addCopper > 0) {
                            ResEventPublisher.pubCopperAddEvent(uid, addCopper, WayEnum.Chamber_Of_Commerce_QD, rd);
                        }
                        break;
                    }
                    TreasureEventPublisher.pubTAddEvent(uid, award.getAwardId(), award.getNum(), way, rd);
                    break;
            }
        }
    }

    public List<CocReward> getRewards(List<Award> awards, int giftId) {
        List<CocReward> rdCocRewards = new ArrayList<CocReward>();
        for (Award award : awards) {
            CocReward reward = CocReward.instance(award, giftId);
            rdCocRewards.add(reward);
        }
        return rdCocRewards;
    }
}
