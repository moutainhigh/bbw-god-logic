package com.bbw.god.activity.worldcup.rd;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 超级16强返回参赛国际
 * @author: hzf
 * @create: 2022-11-13 15:25
 **/
@Data
public class RdDivideGroup  extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    private List<Integer> competeCountries;
    /** 剩余时间 */
    private long surplusTime;
    private boolean ifNeedTreasure;
}
