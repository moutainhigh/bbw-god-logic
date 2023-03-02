package com.bbw.god.activity.worldcup.rd;

import com.bbw.god.activity.worldcup.cfg.CfgSuper16;
import com.bbw.god.activity.worldcup.cfg.WorldCupTool;
import com.bbw.god.activity.worldcup.entity.UserSuper16Info;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 超级16强开奖结果
 * @author: hzf
 * @create: 2022-11-13 16:46
 **/
@Data
public class RdShowSuper16 extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 晋级的超级16强 */
    private List<Integer> super16WinCountrys;
    private List<RdUserBetCountrys> userBetCountrys;
    private Integer ifOpenPrizeTime;

    public static RdShowSuper16 instance(UserSuper16Info userSuper16, CfgSuper16 cfgSuper16){
        RdShowSuper16 rdShowSuper16 = new RdShowSuper16();
        //超级16强
        List<Integer> super16WinCountrys = new ArrayList<>();
        rdShowSuper16.setIfOpenPrizeTime(1);
        //玩家的投注记录
        List<RdUserBetCountrys> userBetCountrys = new ArrayList<>();
        List<CfgSuper16.CfgQuiz> quizs = cfgSuper16.getQuizs();
        for (CfgSuper16.CfgQuiz quiz : quizs) {
            RdUserBetCountrys rdUserBetCountrys = new RdUserBetCountrys();
            super16WinCountrys.addAll(quiz.getWinCountry());
            rdUserBetCountrys.setGroup(quiz.getGroup());
            List<RdUserBetCountry> rdUserBetCountryList = new ArrayList<>();
            if (null == userSuper16) {
                continue;
            }
            UserSuper16Info.BetRecord record = userSuper16.gainBetRecord(quiz.getGroup());
            if (null == record || null == record.getBetCountrys()) {
                continue;
            }else {
                for (Integer betRecord : record.getBetCountrys()) {
                    RdUserBetCountry rdUserBetCountry = new RdUserBetCountry();
                    rdUserBetCountry.setCountry(betRecord);
                    rdUserBetCountry.setStatus(quiz.getWinCountry().contains(betRecord)?1:0);
                    rdUserBetCountryList.add(rdUserBetCountry);
                }
            }

            rdUserBetCountrys.setUserBetCountries(rdUserBetCountryList);
            userBetCountrys.add(rdUserBetCountrys);
        }
        rdShowSuper16.setSuper16WinCountrys(super16WinCountrys);
        rdShowSuper16.setUserBetCountrys(userBetCountrys);
        return rdShowSuper16;
    }
    @Data
    public static  class RdUserBetCountrys{
        private List<RdUserBetCountry> userBetCountries;
        private String group;
    }



    @Data
    public static  class RdUserBetCountry{
    /** 国家 */
    private Integer country;
    /** 开奖结果 */
    private Integer status;
    }
}
