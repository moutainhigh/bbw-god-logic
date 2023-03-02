package com.bbw.god.server.fst.robot;

import lombok.Data;

import java.util.List;

/**
 * 说明：
 * 封神台机器人
 * @author lwb
 * date 2021-06-30
 */
@Data
public class FstGameRobot{
    private Integer robotNumber;
    private String nickname;//机器人昵称
    private Integer level;//机器人等级
    private Integer head;// 机器人头像 随机0类卡
    private List<CardInfo> cards;// 机器人默认卡牌

    public static FstGameRobot getInstance(int no,int level,String nickname){
        FstGameRobot robot=new FstGameRobot();
        robot.setRobotNumber(no);
        robot.setLevel(level);
        robot.setNickname(nickname);
        return robot;
    }

    @Data
    public static class CardInfo{
        private Integer cardId;
        private Integer hv;
        private Integer lv;

        public static CardInfo getInstance(int cardId,int cHv,int cLv) {
            CardInfo info=new CardInfo();
            info.setCardId(cardId);
            info.setHv(cHv);
            info.setLv(cLv);
            return info;
        }
    }
}
