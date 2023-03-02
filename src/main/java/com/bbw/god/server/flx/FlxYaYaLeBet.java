package com.bbw.god.server.flx;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 玩家元素馆的投注记录
 *
 * @author suhq
 * @date 2018年10月30日 上午11:40:05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlxYaYaLeBet extends ServerData {
    private Long uid;// 玩家ID
    private Long serverFlxResult;// 指向ServerFlxResult
    private Integer bet1;
    private Integer bet2;
    private Integer bet3;
    private Date betTime;
    private int dateInt;//日期
    private Long awardMailId = -1L;//奖励邮件的ID

    public static FlxYaYaLeBet instance(GameUser gu, CPYaYaLeGBet param, ServerFlxResult result) {
        FlxYaYaLeBet userYSGBet = new FlxYaYaLeBet();
        userYSGBet.setId(ID.INSTANCE.nextId());
        userYSGBet.setUid(gu.getId());
        userYSGBet.setSid(gu.getServerId());
        userYSGBet.setBet1(param.getBet1());
        userYSGBet.setBet2(param.getBet2());
        userYSGBet.setBet3(param.getBet3());
        userYSGBet.setBetTime(DateUtil.now());
        userYSGBet.setServerFlxResult(result.getId());
        userYSGBet.setDateInt(result.getDateInt());
        return userYSGBet;
    }

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.FLXYAYALE;
    }

    @Override
    public String getLoopKey() {
        return String.valueOf(dateInt);
    }

    @Override
    public boolean isLoopData() {
        return true;
    }
}
