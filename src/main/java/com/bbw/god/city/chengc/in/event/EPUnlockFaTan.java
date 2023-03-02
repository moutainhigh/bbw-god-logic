package com.bbw.god.city.chengc.in.event;

import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.event.BaseEventParam;
import lombok.Data;

import java.util.List;

/**
 * 解锁法坛
 *
 * @author fzj
 * @date 2021/11/13 17:20
 */
@Data
public class EPUnlockFaTan extends BaseEventParam{
    private Long uId;
    private Integer cityId;

    public EPUnlockFaTan(UserCity uc, BaseEventParam baseEventParam) {
        setValues(baseEventParam);
        this.uId = uc.getId();
        this.cityId = uc.getBaseId();
    }
}
