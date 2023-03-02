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
 * 玩家数馆(猜数字)的投注记录
 *
 * @author suhq
 * @date 2018年10月30日 上午11:40:05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlxCaiShuZiBet extends ServerData {
    private Long uid;// 玩家ID
    private Long serverFlxResult;// 指向ServerFlxResult
    private Integer betNum;
    private Integer betCopper = 0;
    private Integer betGold = 0;
    private Date betTime;
    private int dateInt;// 日期
    private Long awardMailId = -1L;// 奖励邮件的ID

    public static FlxCaiShuZiBet instance(GameUser gu, CPCaiShuZiBet param, ServerFlxResult result) {
        FlxCaiShuZiBet userSGBet = new FlxCaiShuZiBet();
        userSGBet.setId(ID.INSTANCE.nextId());
        userSGBet.setBetNum(param.getBetNum());
        if (param.getBetKind() == 1) {
            userSGBet.setBetGold(param.getBetCount());
        } else {
            userSGBet.setBetCopper(param.getBetCount());
        }
        userSGBet.setUid(gu.getId());
        userSGBet.setSid(gu.getServerId());
        userSGBet.setBetTime(DateUtil.now());
        userSGBet.setServerFlxResult(result.getId());
        userSGBet.setDateInt(result.getDateInt());
        return userSGBet;
    }

    public void addBetCopper(int addNum) {
        this.betCopper += addNum;
    }

    public void addBetGold(int addNum) {
        this.betGold += addNum;
    }

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.FLXCAISHUZI;
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
