package com.bbw.god.gameuser.yaozu;

import com.bbw.god.gameuser.card.RDCardStrengthen;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: 妖族Service
 * @author hzf
 * @create: 2023-02-02 13:51
 **/
@Service
public class YaoZuService {

    public RDCardStrengthen getCardInfo(int cardId, String extraParam) {
        Integer yaoZuId = Integer.parseInt(extraParam);
        CfgYaoZuEntity yaoZu = YaoZuTool.getYaoZu(yaoZuId);
        List<CfgYaoZuEntity.CardParam> cards = yaoZu.getYaoZuCards();
        CfgYaoZuEntity.CardParam card = cards.stream().filter(tmp -> tmp.getId().equals(cardId)).findFirst().orElse(null);
        RDCardStrengthen rd = new RDCardStrengthen();
        if (null == card) {
            return rd;
        }
        rd.setCardId(cardId);
        rd.setSkill0(card.getSkill0());
        rd.setSkill5(card.getSkill5());
        rd.setSkill10(card.getSkill10());
        rd.setAttackSymbol(0);
        rd.setDefenceSymbol(0);
        rd.setIsUseSkillScroll(1);
        return rd;
    }
}
