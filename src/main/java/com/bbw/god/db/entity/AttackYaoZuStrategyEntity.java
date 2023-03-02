package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.bbw.common.DateUtil;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.CombatInfo;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.yaozu.ArriveYaoZuCache;
import com.bbw.god.gameuser.yaozu.CfgYaoZuEntity;
import com.bbw.god.gameuser.yaozu.YaoZuProgressEnum;
import com.bbw.god.gameuser.yaozu.YaoZuTool;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 妖族战斗攻略
 *
 * @author fzj
 * @date 2021/9/15 15:18
 */
@Data
@TableName("attack_yaozu_strategy")
public class AttackYaoZuStrategyEntity extends AbstractAttackStrategyEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 妖族Id */
    private Integer yaoZuId;
    /** 是否是本体 1是 0不是 */
    private Integer ontology;

    public static AttackYaoZuStrategyEntity getInstance(CombatInfo combatInfo, Combat combat, int gid, int sid, ArriveYaoZuCache cache) {
        Player user = combat.getP1();
        CfgYaoZuEntity yaoZu = YaoZuTool.getYaoZu(cache.getYaoZuId());
        AttackYaoZuStrategyEntity strategyEntity = new AttackYaoZuStrategyEntity();
        strategyEntity.setId(combat.getId());
        //结算后是反的
        strategyEntity.setServerPrefix(ServerTool.getServerShortName(sid));
        strategyEntity.setGid(gid);
        strategyEntity.setYaoZuId(yaoZu.getYaoZuId());
        strategyEntity.setUid(user.getUid());
        strategyEntity.setNickname(user.getName());
        strategyEntity.setHead(user.getImgId());
        strategyEntity.setIcon(user.getIconId());
        strategyEntity.setSeq(yaoZu.getYaoZuType());
        strategyEntity.setLv(user.getLv());
        List<BattleCard> cards = combatInfo.getP1().getDrawCards();
        cards.addAll(combatInfo.getP1().getHandCardList());
        int specialCards = 0;
        for (BattleCard card : cards) {
            if (card != null && card.getIsUseSkillScroll() == 1) {
                specialCards++;
            }
        }
        strategyEntity.setCards(cards.size());
        strategyEntity.setSpecialCards(specialCards);
        strategyEntity.setUseWeapons(user.getWeaponsInUse().size());
        Player ai = combat.getP2();
        strategyEntity.setAiLv(ai.getLv());
        strategyEntity.setAiHead(ai.getImgId());
        strategyEntity.setAiNickname(ai.getName());
        strategyEntity.setOntology(cache.getProgress() == YaoZuProgressEnum.BEAT_ONTOLOGY.getType() ? 1 : 0);
        strategyEntity.setRound(combat.getRound());
        strategyEntity.setResultType(combat.getCombatResultType().getVal());
        strategyEntity.setRecordedTime(DateUtil.now());
        strategyEntity.setRecordedDate(DateUtil.toDateInt(strategyEntity.getRecordedTime()));
        return strategyEntity;
    }
}
