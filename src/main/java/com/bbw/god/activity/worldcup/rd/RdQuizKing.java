package com.bbw.god.activity.worldcup.rd;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.worldcup.cfg.CfgQuizKing;
import com.bbw.god.activity.worldcup.cfg.WorldCupTool;
import com.bbw.god.activity.worldcup.entity.UserQuizKingInfo;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 我是竞猜王返回类
 * @author: hzf
 * @create: 2022-11-12 13:10
 **/
@Data
public class RdQuizKing extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    private String dateInfo;
    /** 当前日期 */
    private String currentTime;
    /** 展示的每天比赛的集合 */
    private List<String> dateList;
    public static RdQuizKing instance(String dateInfo){
        Date currentTime = new Date();
        String currentTimeString = DateUtil.toString(currentTime, "MM-dd");

        RdQuizKing rdQuizKing = new RdQuizKing();
        rdQuizKing.setCurrentTime(currentTimeString);

        List<String> dateList = new ArrayList<>();

        Date frontTwoDay = DateUtil.addDays(currentTime, -2);
        Date frontOneDay = DateUtil.addDays(currentTime, -1);
        Date afterOneDay = DateUtil.addDays(currentTime, 1);
        String frontTwoDayString = DateUtil.toString(frontTwoDay, "MM-dd");
        String frontOneDayString = DateUtil.toString(frontOneDay, "MM-dd");
        String afterOneDayString = DateUtil.toString(afterOneDay, "MM-dd");
        dateList.add(frontTwoDayString);
        dateList.add(frontOneDayString);
        dateList.add(currentTimeString);
        dateList.add(afterOneDayString);
        rdQuizKing.setDateList(dateList);
        return rdQuizKing;
    }

}
