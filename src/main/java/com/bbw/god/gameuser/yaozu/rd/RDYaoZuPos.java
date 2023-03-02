package com.bbw.god.gameuser.yaozu.rd;

import com.bbw.god.gameuser.yaozu.UserYaoZuInfo;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 返回客户端妖族信息位置
 *
 * @author fzj
 * @date 2021/9/15 12:57
 */
@Slf4j
@Data
public class RDYaoZuPos extends RDAdvance implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 妖族id */
    private Integer id;
    /** 位置 */
    private Integer position;
    public RDYaoZuPos() {
       super();
    }
    public RDYaoZuPos(UserYaoZuInfo yaoZuInfo) {
        this.id = yaoZuInfo.getBaseId();
        this.position = yaoZuInfo.getPosition();
    }
    public RDYaoZuPos(RDYaoZuInfo rdYaoZuInfo) {
        this.id = rdYaoZuInfo.getYaoZuId();
        this.position = rdYaoZuInfo.getPosition();

    }
}
