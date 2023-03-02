package com.bbw.god.game.wanxianzhen;

import com.bbw.common.CloneUtil;
import com.bbw.god.db.entity.WanXianMatchEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 万仙阵返回信息
 * @author lwb
 * @date 2020/5/9 16:52
 */
@Data
public class RDWanXian extends RDSuccess implements Serializable{
    private static final long serialVersionUID = 6085996609860776329L;
    private Integer wxType=null;//赛事
    private Integer currentWxType=null;
    private WanXianEmailEnum wxShowRace=null;
    private List<RDUser> rankList=null;//玩家排名榜单
    private Integer seasonOrder=null;//赛季
    private Long countdown=null;//倒计时
    private String countdownName=null;//倒计时类型
    private Integer myStatus=null;//报名状态
    private List<Integer> cards=null;//玩家卡组
    private RDUser myRankInfo=null;//资格赛 自己的信息

    private Integer pageNum=null;//总页数
    private List<RDUser> ranks=null;//分数榜单
    private List<RDFightLog> logs=null;//战斗日志
    private List<List<RDFightLog>> myHistoryLogs=null;
    private List<RDFightLog> races=null;

    private String group=null;//所在分组

    private List<WanXianCard> cardGroup=null;

    private List<RDUser> betList=null;//冠军预测
    private List<RDUser> betLogs=null;//冠军预测
    private List<CfgWanXian.WanXianSeasonAward> rankAwards=null;

    private List<RDUser> ranksA=null;//8强A组榜单
    private List<RDUser> ranksB=null;//8强B组榜单

    private Integer currentRace=null;//当前赛事类型
    private Integer nextRace=null;//下一赛事类型
    private Integer preRace=null;//上一赛事类型
    
    private Integer newSeason=null;
    /**
     * 万仙阵玩家返回信息
     */
    @Data
    public static class RDUser implements Serializable {
        private static final long serialVersionUID = 5664866191160219951L;
        private String nickname=null;
        private Long uid=null;
        private Integer order=null;
        private Integer head=1;
        private Integer headIcon= TreasureEnum.HEAD_ICON_Normal.getValue();
        private String score=null;
        private Integer bet=null;//是否打赌
        private Integer multiple=null;//预测时的倍数
        private String rank=null;
        private Integer status=null;
        private Integer baseScore=null;
        private Integer todayScore=null;
        private Integer hp=0;

        public static RDUser instance(long uid){
            RDUser user=new RDUser();
            user.setUid(uid);
            return user;
        }

        public static RDUser instance(long uid,int score){
            RDUser user=new RDUser();
            user.setUid(uid);
            user.setScore(String.valueOf(score));
            return user;
        }

        public int getScoreInt(){
            if (score==null || score.equals("未上榜") || score.equals("")){
                return -1;
            }
            return Integer.parseInt(score);
        }

        public void addScore(int addVal){
            if (score==null){
                score="0";
            }
            int val=Integer.parseInt(score)+addVal;
            score=String.valueOf(val);
        }

        public void addMultiple(int val){
            if (multiple==null){
                multiple=0;
            }
            multiple+=val;
        }
        public void setUserInfo(GameUser gu){
            nickname=gu.getRoleInfo().getNickname();
            head=gu.getRoleInfo().getHead();
            headIcon=gu.getRoleInfo().getHeadIcon();
        }
    }

    /**
     * 万仙阵对战返回信息
     */
    @Data
    public static class RDFightLog implements Serializable{
        private static final long serialVersionUID = 4264197284445204807L;
        private RDUser p1=null;
        private RDUser p2=null;
        private Integer winner=0;//0为还未战斗 1为P1胜，2为p2胜
        private Integer win=null;//当前玩家视角是神还是负  0为负1为胜
        private String vid=null;//录像id
        private String group=null;//例 ：A组
        private String score=null;
        private String fight=null;// 1-0 比分
        private String vidKey="";
        private boolean changePos=false;
        public static RDFightLog instance(long p1,long p2){
            RDFightLog log=new RDFightLog();
            log.setP1(RDUser.instance(p1));
            log.setP2(RDUser.instance(p2));
            return log;
        }

        public static RDFightLog instance(WanXianMatchEntity entity){
            RDFightLog log=instance(entity.getP1(),entity.getP2());
            log.setVidKey(entity.getVidKey());
            return log;
        }
        public long getWinnerUid(){
            if (winner==1){
                return p1.uid;
            }
            return p2.uid;
        }

        public RDUser getWinnerRDUser(){
            if (winner==1){
                return p1;
            }
            return p2;
        }
        public long getLoserUid(){
            if (winner==1){
                return p2.uid;
            }
            return p1.uid;
        }

        /**
         * p1和p2交换位置
         */
        public void changeP1ToP2(){
            RDUser temp1= CloneUtil.clone(p1);
            RDUser temp2= CloneUtil.clone(p2);
            p1=temp2;
            p2=temp1;
        }
        public void updateWinner(long winneruid){
            winner=p1.getUid()==winneruid?1:2;
        }
    }
}
