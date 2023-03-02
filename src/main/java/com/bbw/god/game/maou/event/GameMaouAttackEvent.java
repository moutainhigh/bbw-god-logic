package com.bbw.god.game.maou.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 攻击魔王事件
 *
 * @author: suhq
 * @date: 2021/12/17 4:14 下午
 */
public class GameMaouAttackEvent extends ApplicationEvent implements IEventParam {

    private static final long serialVersionUID = 7989517499760485793L;

    public GameMaouAttackEvent(EPAttackGameMaou param) {
        super(param);
    }

    @Override
    public EPAttackGameMaou getEP() {
        return (EPAttackGameMaou) getSource();
    }
}
