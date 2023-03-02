package com.bbw.god.city.yeg;

import com.bbw.common.PowerRandom;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.combat.exaward.YeGExawardEnum;
import com.bbw.god.game.combat.runes.RunesTool;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年2月26日 下午11:30:55 类说明 精英怪
 */
@Service
public class YeGEliteFightProcessor extends AbstractYeGFightProcessor {

    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserYeGEliteService userYeGEliteService;

    @Override
    public YeGuaiEnum getYeGEnum() {
        return YeGuaiEnum.YG_ELITE;
    }

    @Override
    public boolean open(long uid) {
        // 24级解锁精英怪
        if (gameUserService.getGameUser(uid).getLevel() >= 24) {
            return true;
        }
        return false;
    }

    @Override
    public RDFightsInfo getFightsInfo(GameUser gu, int type) {
        RDFightsInfo info = super.getFightsInfo(gu, type);
        int level = userYeGEliteService.getYeGLevel(type, gu.getId());
        for (RDFightsInfo.RDFightCard card : info.getCards()) {
            if (card.getLevel() < level) {
                card.setLevel(level);
            }
        }
        CfgCardEntity cardEntity = CardTool.getCardById(info.getCards().get(0).getBaseId());
        info.setNickname(cardEntity.getName() + "(精英)");
        info.setHead(cardEntity.getId());
        info.setHeadIcon(TreasureEnum.HEAD_ICON_JYYG.getValue());
        //1、精英怪召唤师等级=玩家等级+10±5级（上限120级）
        int max = gu.getLevel() + 15;
        int min = gu.getLevel() + 5;
        int targetLevel = PowerRandom.getRandomBetween(min, max);
        if (targetLevel > 120) {
            targetLevel = 120;
        }
        info.setLevel(targetLevel);
        return info;
    }

    @Override
    public YeGExawardEnum getAdditionGoal() {
        return YeGExawardEnum.WIN_ELITE;
    }

    @Override
    public WayEnum getWay() {
        return WayEnum.YG_ELITE_OPEN_BOX;
    }

    @Override
    public int getRunesId() {
        return RunesTool.getRandomYGRunesId(true);
    }

}
