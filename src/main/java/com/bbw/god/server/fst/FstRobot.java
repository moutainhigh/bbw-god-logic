package com.bbw.god.server.fst;

import com.bbw.common.ID;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年10月25日 下午2:57:40
 * 类说明 封神台机器人信息
 */
@Deprecated
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class FstRobot extends ServerData implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long robotId;// 机器人ID 4位数+5位区服+6位序列号
    private String nickname;//机器人昵称
    private Integer level;//机器人等级
    private Integer head;// 机器人头像 随机0类卡
    private List<CardInfo> CardRule;// 机器人默认卡牌

    public FstRobot(long robotId) {
        this.robotId = robotId;
        this.id = ID.INSTANCE.nextId();
        this.head = CardTool.getRandomCardByWay(0).getId();
    }

    @Data
    public static class CardInfo {
        private Integer baseId;
        private Integer level;
        private Integer hv;
    }

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.FST_ROBOT;
    }

    @Override
    public String getLoopKey() {
        return "";
    }

    @Override
    public boolean isLoopData() {
        return false;
    }

}
