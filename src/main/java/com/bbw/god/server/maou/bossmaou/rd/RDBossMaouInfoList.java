package com.bbw.god.server.maou.bossmaou.rd;

import com.bbw.god.rd.RDSuccess;
import com.bbw.god.server.maou.bossmaou.UserBossMaouData;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suchaobin
 * @description 魔王降临界面信息
 * @date 2020/7/21 16:39
 **/
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDBossMaouInfoList extends RDSuccess implements Serializable {
    private static final long serialVersionUID = -6529355522266082757L;
    private Map<String, List<Integer>> attackMaouCards = new HashMap<>();// 魔王编组卡牌
    private Integer passedAloneMaouLevel;// 已通过独占魔王层级
    private List<RDBossMaouInfo> bossMaouInfoList;// 魔王降临信息
    private Boolean isLimitDay = false;// 是否是限时日期
    private Long bossMaouRemainTime = null;// 魔王降临剩余时间
    private Integer bossMaouNextOpenTime = null;// 魔王降临下轮开启时间

    public static RDBossMaouInfoList getInstance(UserBossMaouData ubm, Integer passedAloneMaouLevel,
                                                 List<RDBossMaouInfo> bossMaouInfoList, Boolean isLimitDay,
                                                 Long remainTime, Integer nextOpenTime) {
        RDBossMaouInfoList infoList = new RDBossMaouInfoList();
        infoList.setAttackMaouCards(ubm.getDeckCards());
        infoList.setPassedAloneMaouLevel(passedAloneMaouLevel);
        infoList.setBossMaouInfoList(bossMaouInfoList);
        infoList.setIsLimitDay(isLimitDay);
        infoList.setBossMaouRemainTime(remainTime);
        infoList.setBossMaouNextOpenTime(nextOpenTime);
        return infoList;
    }
}
