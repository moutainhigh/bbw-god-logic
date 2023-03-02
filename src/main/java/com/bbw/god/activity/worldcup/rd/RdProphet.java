package com.bbw.god.activity.worldcup.rd;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.worldcup.cfg.CfgProphet;
import com.bbw.god.activity.worldcup.cfg.WorldCupTool;
import com.bbw.god.activity.worldcup.entity.UserProphetInfo;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 我是预言家返回类
 * @author: hzf
 * @create: 2022-11-12 11:19
 **/
@Data
public class RdProphet extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 参赛国家 : 分组情况 **/
    private List<RdDivideGroup> prophetList;
    private String dateInfo;
    /** 是否有投注 记录 */
    private Integer status;
    private boolean ifNeedTreasure;

    public static RdProphet instance(String dateInfo, CfgProphet cfgProphet, UserProphetInfo userProphet) {
        RdProphet rdProphet = new RdProphet();
        boolean ifBet = WorldCupTool.ifBet(cfgProphet.getBetBegin(), cfgProphet.getBetEnd());
        int i = 0;
        List<RdDivideGroup> rdDivideGroups = new ArrayList<>();
        for (CfgProphet.CfgQuiz quiz : cfgProphet.getQuizs()) {
            i++;
            RdDivideGroup rdDivideGroup = new RdDivideGroup();
            rdDivideGroup.setId(quiz.getId());

            List<Integer> competeCountries = i<= 4 ? quiz.getCompeteCountries() : new ArrayList<>();
            rdDivideGroup.setCompeteCountries(competeCountries);

            rdProphet.setStatus(ifBet?1:0);

            Integer betRecord = 0;
            //判断玩家是否有投注
            if (null != userProphet) {
                 betRecord = userProphet.gainBetRecord(quiz.getId());
                 rdProphet.setIfNeedTreasure(userProphet.isIfNeedTreasure());
            }
            if (ifBet) {
                rdProphet.setStatus(betRecord != 0 ? 2:1);
            }
            //判断活动是否已经结束
            Date betEedDate = DateUtil.fromDateTimeString(cfgProphet.getBetEnd());
            boolean betEed = betEedDate.before(new Date());
            if (betEed) {
                rdProphet.setStatus(3);
            }
            rdDivideGroup.setBetCountry(betRecord);
            rdDivideGroups.add(rdDivideGroup);
        }
        rdProphet.setDateInfo(dateInfo);
        rdProphet.setProphetList(rdDivideGroups);
        return rdProphet;
    }

        @Data
    public static class RdDivideGroup{
        /** 场次 标识 */
        private String id;
        private List<Integer> competeCountries;
        /** 玩家投注记录 */
        private Integer betCountry;
    }
}
