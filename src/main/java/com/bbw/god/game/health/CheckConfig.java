package com.bbw.god.game.health;

import com.bbw.god.PrepareDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 健康检查
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-29 11:43
 */
@Service
public class CheckConfig {
    @Autowired
    @Lazy
    private List<PrepareDataService> prepareDataServices;

    /**
     * 检查某一天的数据
     *
     * @param date
     */
    public boolean check(Date date) {
        boolean b = true;
        for (PrepareDataService service : this.prepareDataServices) {
            boolean hasPrepared = service.check(date);
            b = b && hasPrepared;
        }
        return b;
    }
}
