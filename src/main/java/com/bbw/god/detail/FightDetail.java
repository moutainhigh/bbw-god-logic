package com.bbw.god.detail;

import com.alibaba.fastjson.annotation.JSONField;
import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataType;
import com.bbw.god.server.ServerGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.util.Date;

/**
 * 卡牌明细
 *
 * @author suhq
 * @date 2019年3月13日 下午2:15:18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FightDetail extends ServerData {
    @Setter
    private String challengerInfo;// 挑战者信息
    private String oppoInfo;// 对手信息
    private Integer isWin;// 战斗胜负
    private String resultData;// 客户端提交的战斗结果
    protected String serverName;
    protected Long uId;// 玩家ID
    protected Integer guLevel;// 玩家等级
    protected Integer way;// 方式
    protected String wayDes;// 方式
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    protected Date time;// 时间

    public void setUserInfo(GameUser gu) {
        this.id = ID.INSTANCE.nextId();
        this.uId = gu.getId();
        this.guLevel = gu.getLevel();
        this.sid = gu.getServerId();
        CfgServerEntity server = Cfg.I.get(getSid(), CfgServerEntity.class);
        this.serverName = String.format("【%s】【%s】", ServerGroup.fromGroup(server.getGroupId()), server.getName());
    }

    public void setWayInfo(WayEnum way) {
        this.way = way.getValue();
        this.wayDes = way.getName();
        this.time = DateUtil.now();
    }

    public void setWayInfo(int way, String wayDes) {
        this.way = way;
        this.wayDes = wayDes;
        this.time = DateUtil.now();
    }

    public void setFightInfo(String oppoInfo, boolean isWin, String resultData) {
        this.oppoInfo = oppoInfo;
        this.isWin = isWin ? 1 : 0;
        this.resultData = resultData;
    }

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.DETAIL_FIGHT;
    }

}
