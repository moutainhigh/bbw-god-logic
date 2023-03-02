package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.weapon.Weapon;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 群嘲符 131260 每回合开始时，敌方所有手牌上场所需法力值永久+1，双方禁用法宝-如意乾坤袋。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131260 implements IInitStageRunes {
    @Override
    public int getRunesId() {
        return 131260;
    }
    @Override
    public void doInitRunes(CombatRunesParam param) {
       for (BattleCard card: param.getOppoPlayer().getHandCards()){
           if (card!=null){
               card.setRoundMpAddition(card.getRoundMpAddition()+1);
               card.setMp(card.getMp()+card.getRoundMpAddition());
           }
       }
       List<Weapon> weapons = param.getOppoPlayer().getWeapons().stream().filter(p -> p.getId() != 460).collect(Collectors.toList());
       param.getOppoPlayer().setWeapons(weapons);
    }
}
