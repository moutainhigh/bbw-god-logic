package com.bbw.god.activity.worldcup.rd;

import com.bbw.god.activity.worldcup.cfg.CfgDroiyan8;
import com.bbw.god.activity.worldcup.cfg.WorldCupTool;
import com.bbw.god.activity.worldcup.entity.UserDroiyan8Info;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: bbw-god-game-2211v1
 * @description:
 * @author: 殇璃
 * @create: 2022-11-14 10:24
 **/
@Data
public class RdShowDroiyan8 extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 晋级的超级8强 */
    private List<Integer> droiyan8WinCountrys;
    private List<RdUserBetCountry> userBetCountrys;
    private Integer ifOpenPrizeTime;


    public static RdShowDroiyan8 instance(UserDroiyan8Info userDroiyan8, CfgDroiyan8 cfgDroiyan8){
        RdShowDroiyan8 rdShowDroiyan8 = new RdShowDroiyan8();
        //决战8强
        List<Integer> droiyan8WinCountrys = new ArrayList<>();
        //玩家的投注记录
        boolean ifShow = WorldCupTool.ifShow(cfgDroiyan8.getShowBegin(), cfgDroiyan8.getShowEnd());
        rdShowDroiyan8.setIfOpenPrizeTime(ifShow?1:0);

        List<RdUserBetCountry> userBetCountrys = new ArrayList<>();
        List<CfgDroiyan8.CfgQuiz> quizs = cfgDroiyan8.getQuizs();
        for (CfgDroiyan8.CfgQuiz quiz : quizs) {
            RdUserBetCountry rdUserBetCountry = new RdUserBetCountry();
            droiyan8WinCountrys.add(quiz.getWinCountry());
            rdUserBetCountry.setId(quiz.getId());
            if (null == userDroiyan8) {
                continue;
            }
            UserDroiyan8Info.BetRecord record = userDroiyan8.gainBetRecord(quiz.getId());
            if (null == record || null == record.getBetCountry()) {

            }else {
                rdUserBetCountry.setCountry(record.getBetCountry());
                int winCountry = quiz.getWinCountry() == null ? 0 : quiz.getWinCountry();
                rdUserBetCountry.setStatus(winCountry == record.getBetCountry()?1:0);
            }
            userBetCountrys.add(rdUserBetCountry);
        }
        rdShowDroiyan8.setDroiyan8WinCountrys(droiyan8WinCountrys);
        rdShowDroiyan8.setUserBetCountrys(userBetCountrys);
        return rdShowDroiyan8;
    }


    @Data
    public static  class RdUserBetCountry{
        /** 国家 */
        private Integer country;
        /** 开奖结果 */
        private Integer status;
        private String id;
    }
}
