package com.bbw.god.server.maou;

import com.alibaba.fastjson.annotation.JSONField;
import com.bbw.common.DateUtil;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 魔王信息
 *
 * @author suhq
 * @date 2019年1月8日 上午10:17:15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Deprecated
public class ServerMaou extends ServerData {
    private int dateInt;// 日期yyyyMMdd
    private int timeInt;// 开放时间HHmmss
    private Integer totalBlood;// 总血量
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date attackTime;// 魔王攻打时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date noticeTime;// 魔王开始显示时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date leaveTime;// 魔王离开的时间
    private Integer coldTime;//冷却时间（秒）
    private Integer cardAward;// 第一名卡牌奖励
    private String cardAwardName;// 第一名卡牌名称
    private Integer killAward;// 最后一击奖励
    private String killAwardName;// 第一名卡牌名称
    //需要更新的数据
    private Integer remainBlood;// 剩余血量
    private Long killer = 0L;// 最后一击者
    private Date sendAwardTime = null;//奖励发送时间，初始时为NULL。奖励放松后被设置为发送时间

    /**
     * 是否已经发送奖励。
     * sendAwardTime为奖励发送时间，初始时为NULL。奖励放松后被设置为发送时间
     *
     * @return
     */
    public boolean hasSendedAward() {
        return null != this.sendAwardTime;
    }

    /**
     * 没有击杀者
     *
     * @return
     */
    public boolean hasKiller() {
        // killer 是Long型，
        return this.killer > 0.5;
    }

    /**
     * 是否是当前被攻击的魔王
     *
     * @param time
     * @return
     */
    public boolean isMe(Date time) {
        return time.after(this.noticeTime) && time.before(this.leaveTime);
    }

    /**
     * 魔王已死
     *
     * @return
     */
    public boolean isKilled() {
        return this.remainBlood < 1;
    }

    public void lostBlood(int value) {
        this.remainBlood = this.totalBlood - value;
        this.remainBlood = Math.max(this.remainBlood, 0);
    }

    /**
     * 根据日期获取循环key
     *
     * @param date
     * @return
     */
    public static String getLoopKey(Date date) {
        return DateUtil.toString(date, "yyyyMMdd");
    }

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.MAOU;
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
