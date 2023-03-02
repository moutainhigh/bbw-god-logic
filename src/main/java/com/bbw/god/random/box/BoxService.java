package com.bbw.god.random.box;

import com.bbw.common.ListUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.StarEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 开宝箱、礼包服务
 *
 * @author suhq
 * @date 2019年4月11日 上午9:05:18
 */
@Slf4j
@Service
public class BoxService extends AbstractBoxService {
    @Override
    public List<Award> toCardAwards(long uid, BoxGood cardGood) {
        String goodInfo = cardGood.getGood();
        List<CfgCardEntity> cards = new ArrayList<>();
        StarEnum starEnum = StarEnum.fromName(goodInfo);

        if (starEnum != null) {
            // 星级卡牌
            cards = CardTool.getRandomNotSpecialCards(starEnum.getValue(), cardGood.getNum());
        } else {
            // 指定卡牌
            CfgCardEntity card = CardTool.getCardByName(goodInfo);
            if (card != null) {
                cards.add(card);
            }
        }
        List<Award> awards = new ArrayList<>();
        if (ListUtil.isNotEmpty(cards)) {
            cards.stream().forEach(c -> awards.add(new Award(c.getId(), AwardEnum.KP, cardGood.getNum())));
        }
        return awards;
    }
}
