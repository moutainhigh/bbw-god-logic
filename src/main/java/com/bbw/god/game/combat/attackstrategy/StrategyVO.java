package com.bbw.god.game.combat.attackstrategy;

import com.bbw.common.DateUtil;
import com.bbw.god.db.entity.AbstractAttackStrategyEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 返回给客户端的数据
 *
 * @author：lwb
 * @date: 2020/11/27 16:46
 * @version: 1.0
 */
@Data
public class StrategyVO extends AbstractStrategyVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long dataId;
    private Integer fightType;
    private Integer winner;//胜利方1或2 对应P1,P2
    private PlayerInfoVO p1;
    private PlayerInfoVO P2;
    private String url;
    private String datetime;
    private Integer cards;
    private Integer round;
    private Integer lv;
    private String shortServerName;

    public static StrategyVO instance(AbstractAttackStrategyEntity entity, PlayerInfoVO user, int fightType) {
        StrategyVO vo = new StrategyVO();
        vo.setDataId(entity.getId());
        vo.setFightType(fightType);
        vo.setWinner(1);
        vo.setUrl(entity.getRecordedUrl());
        vo.setP1(user);
        vo.setP2(PlayerInfoVO.instance(entity.getAiNickname(), entity.getAiLv(), entity.getAiHead(), TreasureEnum.HEAD_ICON_Normal.getValue(), -1));
        vo.setDatetime(DateUtil.toDateTimeString(entity.getRecordedTime()));
        vo.setDatetimeInt(DateUtil.toDateTimeLong(entity.getRecordedTime()));
        vo.setRound(entity.getRound());
        vo.setLv(entity.getLv());
        vo.setCards(entity.getCards());
        vo.setShortServerName(entity.getServerPrefix());
        return vo;
    }

    public static StrategyVO instance(AbstractAttackStrategyEntity entity, int fightType) {
        StrategyVO vo = new StrategyVO();
        vo.setDataId(entity.getId());
        vo.setFightType(fightType);
        vo.setWinner(1);
        vo.setUrl(entity.getRecordedUrl());
        vo.setP1(PlayerInfoVO.instance(entity.getNickname(), entity.getLv(), entity.getHead(), entity.getIcon(), entity.getUid()));
        vo.setP2(PlayerInfoVO.instance(entity.getAiNickname(), entity.getAiLv(), entity.getAiHead(), TreasureEnum.HEAD_ICON_Normal.getValue(), -1));
        vo.setDatetime(DateUtil.toDateTimeString(entity.getRecordedTime()));
        vo.setDatetimeInt(DateUtil.toDateTimeLong(entity.getRecordedTime()));
        vo.setRound(entity.getRound());
        vo.setLv(entity.getLv());
        vo.setCards(entity.getCards());
        vo.setShortServerName(entity.getServerPrefix());
        return vo;
    }
}
