package com.bbw.god.activity.worldcup.rd;

import com.bbw.common.ListUtil;
import com.bbw.god.activity.worldcup.cfg.CfgSuper16;
import com.bbw.god.activity.worldcup.cfg.WorldCupTool;
import com.bbw.god.activity.worldcup.entity.UserSuper16Info;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 超级16强返回类
 *
 * @author: hzf
 * @create: 2022-11-11 16:24
 **/
@Data
public class RdSuper16 extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    /** 参赛国家 : 分组情况 **/
    private List<RdDivideGroup> super16List;
    private String dateInfo;
    /** 是否达到开奖时间 */
    private Integer ifOpenPrizeTime;

    public static RdSuper16 instance(String dateInfo,CfgSuper16 cfgSuper16, UserSuper16Info userSuper16){
        RdSuper16 rdSuper16 = new RdSuper16();
        boolean ifShow = WorldCupTool.ifShow(cfgSuper16.getShowBegin(), cfgSuper16.getShowEnd());

        rdSuper16.setIfOpenPrizeTime(0);
        List<RdDivideGroup> rdDivideGroups = new ArrayList<>();
        for (CfgSuper16.CfgQuiz quiz : cfgSuper16.getQuizs()) {
            RdDivideGroup rdDivideGroup = new RdDivideGroup();
            rdDivideGroup.setGroup(quiz.getGroup());
//            rdDivideGroup.setCompeteCountries(quiz.getCompeteCountries());
            rdDivideGroup.setWinCountryList(quiz.getWinCountry());
            if (ifShow) {
                rdDivideGroup.setWinCountryList(quiz.getWinCountry());
            }else {
                rdDivideGroup.setWinCountryList(new ArrayList<>());
            }
            List<Integer> betCountrys = new ArrayList<>();
            if (null != userSuper16) {
                betCountrys  = userSuper16.gainbetCountrys(quiz.getGroup());
            }
            rdDivideGroup.setBetCountryList(betCountrys);
            if (ListUtil.isEmpty(betCountrys)) {
                rdDivideGroup.setStatus(0);
            }else {
                rdDivideGroup.setStatus(1);
            }
            rdDivideGroups.add(rdDivideGroup);
        }
        rdSuper16.setSuper16List(rdDivideGroups);
        rdSuper16.setDateInfo(dateInfo);

        return rdSuper16;
    }


    @Data
    public static class RdDivideGroup{
        private String group;
        /** 是否有投注 记录 */
        private Integer status;
//        private List<Integer> competeCountries;
        /** 玩家投注记录 */
        private List<Integer> betCountryList;
        /** 晋级国家 */
        private List<Integer> winCountryList;
    }
}
