package com.bbw.god.gameuser.nightmarenvwam;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;

/**
 * TODO
 *
 * @author fzj
 * @date 2022/5/5 14:44
 */
@Data
public class RDIsActiveNightmareNWM extends RDSuccess {
    /** 是否开启梦魇女娲庙  0未激活 1已激活*/
    private Integer isActive = 0;
}
