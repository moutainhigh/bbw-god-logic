package com.bbw.god.activity.holiday.processor.holidayspcialyeguai;

import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 节日特殊野怪活动工厂类
 *
 * @author fzj
 * @date 2022/2/24 9:31
 */
@Service
public class HolidaySpecialYeGuaiFactory {
    @Autowired
    @Lazy
    private List<AbstractSpecialYeGuaiProcessor> specialYeGuaiProcessors;
    @Autowired
    GameUserService gameUserService;

    /**
     * 获取活动服务实现对象
     *
     * @param uid
     * @return
     */
    public AbstractSpecialYeGuaiProcessor getSpecialYeGuaiProcessor(long uid) {
        int sid = gameUserService.getActiveSid(uid);
        return specialYeGuaiProcessors.stream().filter(s -> s.isOpened(sid)).findFirst().orElse(null);
    }

}
