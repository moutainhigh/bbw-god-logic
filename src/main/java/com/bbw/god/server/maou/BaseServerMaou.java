package com.bbw.god.server.maou;

import com.alibaba.fastjson.annotation.JSONField;
import com.bbw.common.DateUtil;
import com.bbw.god.server.ServerData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author suhq
 * @description: 魔王基类
 * @date 2019-12-19 12:00
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseServerMaou extends ServerData {
    private Integer type;//魔王属性
    private int dateInt;// 日期yyyyMMdd
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date beginTime;//开始时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;//结束时间

    /**
     * 是否是当前被攻击的魔王
     *
     * @param time
     * @return
     */
    public boolean ifMe(Date time) {
        long timeLong = DateUtil.toDateTimeLong(time);
        long begin = DateUtil.toDateTimeLong(this.beginTime);
        long end = DateUtil.toDateTimeLong(this.endTime);
        return timeLong >= begin && timeLong <= end;
    }

    @Override
    public String getLoopKey() {
        return String.valueOf(this.dateInt);
    }

    @Override
    public boolean isLoopData() {
        return true;
    }
}
