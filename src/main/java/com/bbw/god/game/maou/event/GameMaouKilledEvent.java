package com.bbw.god.game.maou.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 魔王被击杀事件
 *
 * @author: suhq
 * @date: 2021/12/17 4:13 下午
 */
public class GameMaouKilledEvent extends ApplicationEvent implements IEventParam {

    private static final long serialVersionUID = -4411005315807783153L;

    public GameMaouKilledEvent(EPGameMaouKilled param) {
        super(param);
    }

    @Override
    public EPGameMaouKilled getEP() {
        return (EPGameMaouKilled) getSource();
    }
}
