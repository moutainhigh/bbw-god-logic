package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IParamInitStageRunes;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.service.ZxzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 狂暴词条  敌方卡牌的攻防增加[5]%。
 * @author: hzf
 * @create: 2022-12-16 09:11
 **/
@Service
public class Runes333001 implements IParamInitStageRunes {
    @Autowired
    private ZxzService zxzService;

    @Override
    public int getRunesId() {
        return RunesEnum.KUANG_BAO_ERTRY.getRunesId();
    }

    @Override
    public void doParamInitRunes(CPlayerInitParam performInitParam, CPlayerInitParam oppInitParam, CombatPVEParam pveParam) {
        // 狂暴词条等级
        CombatBuff combatBuff = performInitParam.getBuffs().stream().filter(tmp -> tmp.getRuneId() == getRunesId()).findFirst().orElse(null);
        if (null == combatBuff) {
            return;
        }
        int lv = combatBuff.getLevel();
        if (lv == 0) {
            return;
        }
        //敌方卡牌攻防加成：100%+词条等级*加成数值
        double extraCardRate = 0.05 * lv;
        //添加攻防比较
        performInitParam.setExtraCardRate(extraCardRate);
        //计算攻防
        handleExtraCardRate(performInitParam,oppInitParam);
    }
    private void handleExtraCardRate(CPlayerInitParam p1, CPlayerInitParam ai) {
        if (p1.gainExtraCardRate() > 0) {
            p1.getCards().forEach(card -> {
                int atk = (int) (card.getAtk() * (1 + p1.gainExtraCardRate()));
                int hp = (int) (card.getHp() * (1 + p1.gainExtraCardRate()));
                card.setAtk(atk);
                card.setHp(hp);
            });
        }
        if (ai.gainExtraCardRate() > 0) {
            ai.getCards().forEach(card -> {
                int atk = (int) (card.getAtk() * (1 + p1.gainExtraCardRate()));
                int hp = (int) (card.getHp() * (1 + p1.gainExtraCardRate()));
                card.setAtk(atk);
                card.setHp(hp);
            });
        }
    }
}
