package com.bbw.god.server.fst;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.card.RDCardStrengthen;
import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 说明：封神台信息返回
 *
 * @author lwb
 * date 2021-06-29
 */
@Data
public class RDFst extends RDCommon {
    /**
     * 我的名次
     */
    private Integer myRank = null;
    /**
     * 我的积分
     */
    private Integer myPoint = null;
    /**
     * 积分增长值
     */
    private Integer myAblePoint = null;
    /**
     * 可挑战次数
     */
    private Integer pvpTimes = null;
    /**
     * 封神台增值积分状态
     */
    private Integer incrementState=null;
    /**
     * 封神台领取的增值积分
     */
    private Integer addedPoint=null;
    /**
     * 榜单
     */
    private List<FstRankerParam> ranks;
    /**
     * 前3位
     */
    private List<FstRankerParam> preRanks;
    /**
     * 可领取封神台增值积分
     */
    private Integer currentPoint=null;
    /**
     * 榜单类型
     */
    private Integer rankingType;
    /**
     * 战斗日志信息
     */
    private List<FstFightMsg> fightMsgs=null;
    /**
     *  跨服封神台结算倒计时
     */
    private Long remainTime;
    /**
     * 是否是晋级排名：-1是降级区  1是晋级区
     */
    private Integer isPromotionRank;
    /**
     * 战斗日志
     */
    private List<FightLog> logs;
    /**
     * 需要携带分身卡
     */
    private Integer needUserLeaderCard;
    
    /**
     * 卡组信息
     */
    private List<List<RDCardStrengthen>> cardGroup=null;
    
    /**
     * 封神台弹窗
     *  10 显示加入跨服封神台弹窗,20显示 新榜单开启  30显示结算提示
     */
    private Integer showPopType;
    /**
     * 是否开启跨服封神台
     */
    private Integer isJoinGameFst;
    
    private Integer isPromotion;
    
    /**
     *
     * @return
     */

    public static RDFst getIntoFst(){
        RDFst rd=new RDFst();
        rd.setMyAblePoint(0);
        //封神台积分
        rd.setMyPoint(0);
        rd.setPvpTimes(0);
        rd.setCurrentPoint(0);
        rd.setMyRank(-1);
        rd.setShowPopType(FstPopType.NONE.getType());
        return rd;
    }
    
    @Data
    public static class FightLog{
        private Player p1;
        private Player p2;
        private Integer isP1Win=0;
        private String url;
    
        public static FightLog getInstance (Player p1,Player p2,FstVideoLog.Log logLog) {
            FightLog log=new FightLog();
            if (logLog.isFirst()){
                log.setP1(p1);
                log.setP2(p2);
                log.setIsP1Win(logLog.isWin()?1:0);
            }else {
                log.setP1(p2);
                log.setP2(p1);
                log.setIsP1Win(logLog.isWin()?0:1);
            }
            log.setUrl(logLog.getUrl());
            return log;
        }
    }
    
    @Data
    public static class Player implements Serializable {
        private Long uid;
        private String nickname;
        private Integer head;
        private Integer headIcon= TreasureEnum.HEAD_ICON_Normal.getValue();
    
        public static Player getInstance (long uid) {
            Player player=new Player();
            player.setUid(uid);
            return player;
        }
    }
}
