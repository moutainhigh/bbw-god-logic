package com.bbw.god.activity.holiday.processor.holidayspecialcity;

import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 活动特殊建筑工厂类
 *
 * @author fzj
 * @date 2022/4/28 16:43
 */
@Service
public class HolidaySpecialCityFactory {
    @Autowired
    @Lazy
    private List<AbstractSpecialCityProcessor> buildProcessors;
    @Autowired
    GameUserService gameUserService;

    /**
     * 获取活动服务实现对象
     *
     * @param uid
     * @return
     */
    public AbstractSpecialCityProcessor getSpecialCityProcessor(long uid) {
        int sid = gameUserService.getActiveSid(uid);
        return buildProcessors.stream().filter(s -> s.isOpened(sid)).findFirst().orElse(null);
    }
}
