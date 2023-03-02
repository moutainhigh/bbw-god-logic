package com.bbw.god.server.maou.bossmaou;

import com.alibaba.fastjson.annotation.JSONField;
import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.CfgBossMaou;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.server.ServerDataID;
import com.bbw.god.server.ServerDataType;
import com.bbw.god.server.maou.BaseServerMaou;
import lombok.Data;

import java.util.Date;

/**
 * @author suhq
 * @description: 降临魔王boss
 * @date 2019-12-17 17:02
 **/
@Data
public class ServerBossMaou extends BaseServerMaou {
    private Integer baseMaouId;//魔王级别（低手区、高手区）
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date attackTime;// 魔王攻打时间
    private Integer cardAward;// 第一名卡牌奖励
    private String cardAwardName;// 第一名卡牌名称
    private Integer killAward;// 最后一击奖励
    private String killAwardName;// 第一名卡牌名称
    //需要更新的数据
    private Integer totalBlood;// 总血量
    private Integer remainBlood;// 剩余血量
    private Long killer = 0L;// 最后一击者
    private Date killedTime = null;//魔王被击杀的时间
    private Date sendAwardTime = null;//奖励发送时间，初始时为NULL。奖励放松后被设置为发送时间


    public static ServerBossMaou instance(int sid, CfgBossMaou.BossMaou bossMaou, int dateInt, CfgCardEntity card, CfgTreasureEntity tre) {
        ServerBossMaou sm = new ServerBossMaou();
        Long newid = ServerDataID.generateConfigID(sid, DateUtil.fromDateInt(dateInt), ServerDataType.BOSS_MAOU, bossMaou.getId());
        sm.setId(newid);
        sm.setSid(sid);
        sm.setDateInt(dateInt);
        sm.setBaseMaouId(bossMaou.getId());
        sm.setType(PowerRandom.getRandomBySeed(5) * 10);
        sm.setTotalBlood(bossMaou.getInitBlood());
        sm.setRemainBlood(sm.getTotalBlood());
        sm.setAttackTime(DateUtil.fromDateLong(Long.valueOf(dateInt + "" + bossMaou.getAttackTime())));
        sm.setBeginTime(DateUtil.fromDateLong(Long.valueOf(dateInt + "" + bossMaou.getBeginTime())));
        sm.setEndTime(DateUtil.fromDateLong(Long.valueOf(dateInt + "" + bossMaou.getEndTime())));
        sm.setCardAward(card.getId());
        sm.setCardAwardName(card.getName());
        sm.setKillAward(tre.getId());
        sm.setKillAwardName(tre.getName());
        System.out.println(DateUtil.toDateTimeString(sm.getAttackTime()) + card.getName());
        return sm;
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

    /**
     * 匹配独战魔王的等级
     *
     * @param aloneMaouLevel
     * @return
     */
    public boolean ifMatch(int aloneMaouLevel) {
        CfgBossMaou.BossMaou bossMaouConfig = BossMaouTool.getBossMaouConfig(this.baseMaouId);
        return bossMaouConfig.getAloneMaouLevelsEnable().contains(aloneMaouLevel);
    }

    public boolean ifMatch(BossMaouLevel bossMaouLevel) {
        CfgBossMaou.BossMaou bossMaouConfig = BossMaouTool.getBossMaouConfig(this.baseMaouId);
        return bossMaouConfig.getMaouLevel() == bossMaouLevel.getValue();
    }

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
     * 魔王已死
     *
     * @return
     */
    public boolean isKilled() {
        return this.remainBlood < 1;
    }

    /**
     * 设置魔王被击杀
     *
     * @param killer
     */
    public void updateAsKilled(long killer) {
        this.killer = killer;
        this.remainBlood = 0;
        this.killedTime = DateUtil.now();
    }

    /**
     * 获得魔王当前回合时间
     *
     * @return
     */
    public long gainRoundTime() {
        long roundTime = System.currentTimeMillis();
        if (isKilled()) {
            roundTime = getKilledTime().getTime();
        }
        return roundTime;
    }

    public void lostBlood(long uid, int lostBlood) {
        this.remainBlood = this.totalBlood - lostBlood;
        this.remainBlood = Math.max(this.remainBlood, 0);
        // 判定失血后状态。
        if (isKilled()) {
            // 失血过多，死了
            updateAsKilled(uid);
        }
    }

    public int gainBossMaouLevel() {
        return this.baseMaouId / 10 * 10;
    }

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.BOSS_MAOU;
    }
}
