package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.yuxg.UserFuTu;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 符图扣除参数
 *
 * @author fzj
 * @date 2022/1/5 10:06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPFuTuDeduct extends BaseEventParam {
    private UserFuTu userFuTu;
    private Integer num;

    public EPFuTuDeduct(BaseEventParam baseEP, UserFuTu userFuTu, int num) {
        setValues(baseEP);
        this.userFuTu = userFuTu;
        this.num = num;
    }
}
