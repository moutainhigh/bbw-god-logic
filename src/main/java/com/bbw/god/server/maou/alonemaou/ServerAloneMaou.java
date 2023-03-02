package com.bbw.god.server.maou.alonemaou;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgAloneMaou;
import com.bbw.god.server.ServerDataID;
import com.bbw.god.server.ServerDataType;
import com.bbw.god.server.maou.BaseServerMaou;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * @author suhq
 * @description: 独占魔王
 * @date 2019-12-17 16:25
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ServerAloneMaou extends BaseServerMaou {

    public static ServerAloneMaou getInstance(int sid, int type, int dateInt, CfgAloneMaou config) {
        Date date = DateUtil.fromDateInt(dateInt);
        ServerAloneMaou maou = new ServerAloneMaou();
        Long id = ServerDataID.generateConfigID(sid, date, ServerDataType.ALONE_MAOU, 0);
        maou.setId(id);
        maou.setSid(sid);
        maou.setDateInt(dateInt);
        maou.setType(type);
        Date nextDate = DateUtil.addDays(date, 1);
        String beginTime = dateInt + "" + config.getBeginTime();
        String endTime = DateUtil.toDateInt(nextDate) + "" + config.getEndTime();
        maou.setBeginTime(DateUtil.fromDateLong(Long.valueOf(beginTime)));
        maou.setEndTime(DateUtil.fromDateLong(Long.valueOf(endTime)));
        return maou;
    }

    public static String getLoopKey(Date date) {
        CfgAloneMaou config = Cfg.I.getUniqueConfig(CfgAloneMaou.class);
        int hms = DateUtil.toHMSInt(date);
        if (hms >= config.getBeginTime()) {
            return String.valueOf(DateUtil.toDateInt(date));
        } else {
            Date preDate = DateUtil.addDays(date, -1);
            return String.valueOf(DateUtil.toDateInt(preDate));
        }
    }

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.ALONE_MAOU;
    }
}
