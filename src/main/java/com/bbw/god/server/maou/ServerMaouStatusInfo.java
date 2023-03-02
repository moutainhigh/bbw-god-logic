package com.bbw.god.server.maou;

import com.bbw.exception.ExceptionForClientTip;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 魔王状态信息
 *
 * @author suhq
 * @version 1.0.0
 * @date 2019-12-23 10:58
 */
@Slf4j
@Data
public class ServerMaouStatusInfo {
    private int status;
    private long remainTime;

    public ServerMaouStatusInfo(int status) {
        this.status = status;
        this.remainTime = 0;
    }

    public ServerMaouStatusInfo(int status, long remainTime) {
        this.status = status;
        this.remainTime = remainTime;
    }

    /**
     * 检查魔王是否处于平安期
     */
    public void isMaouPeace() {
        if (this.status == ServerMaouStatus.PEACE.getValue()) {
            //log.info("非魔王时段");
            throw new ExceptionForClientTip("maou.not.show");
        }
    }

    /**
     * 打魔王时检查魔王状态，打魔王检查
     */
    public void check() {
        ServerMaouStatus statusEnum = ServerMaouStatus.fromValue(this.status);
        if (statusEnum == ServerMaouStatus.PEACE) {
//            log.info("魔王已结束");
            throw new ExceptionForClientTip("maou.attack.is.over");
        }
        if (statusEnum == ServerMaouStatus.ASSEMBLY) {
//            log.info("魔王未开打");
            throw new ExceptionForClientTip("maou.attack.not.start");
        }

        if (statusEnum == ServerMaouStatus.KILLED) {
//            log.info("魔王已死");
            throw new ExceptionForClientTip("maou.already.die");
        }
    }
}
