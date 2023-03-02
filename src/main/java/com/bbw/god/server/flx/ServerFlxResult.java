package com.bbw.god.server.flx;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.flx.FlxDayResult;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataID;
import com.bbw.god.server.ServerDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 福临轩结果
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-09 20:23
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ServerFlxResult extends ServerData {
    private int awardCardId;// 奖励卡牌的ID
    private String awardCardName;//奖励卡牌名称
    private int dateInt;//日期yyyyMMdd
    private FlxDayResult matchResult;

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.FLXRESULT;
    }

    public static ServerFlxResult fromFlxDayResult(int sid, FlxDayResult result, CfgCardEntity card) {
        ServerFlxResult sfr = new ServerFlxResult();
        sfr.setId(ServerDataID.generateConfigID(sid, DateUtil.fromDateInt(result.getDateInt()), ServerDataType.FLXRESULT, 1));
        sfr.setSid(sid);
        sfr.setDateInt(result.getDateInt());
        sfr.setMatchResult(result);
        sfr.setAwardCardId(card.getId());
        sfr.setAwardCardName(card.getName());
        return sfr;
    }
}
