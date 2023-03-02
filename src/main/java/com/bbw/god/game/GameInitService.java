package com.bbw.god.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * spring启动完成后，初始化游戏配置。
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-04 18:28
 */
@Component
@Order(1)
public class GameInitService implements CommandLineRunner {
    @Autowired
    private GameService gameService;

    @Override
    public void run(String... args) throws Exception {
        gameService.init();
    }

}