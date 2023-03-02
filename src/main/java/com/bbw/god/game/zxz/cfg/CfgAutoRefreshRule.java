package com.bbw.god.game.zxz.cfg;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 自动刷新配置表
 * @author: hzf
 * @create: 2022-09-15 09:09
 **/
@Data
public class CfgAutoRefreshRule implements Serializable {
    private static final long serialVersionUID = 6283485026406890074L;
    /** 难度类型 */
    private Integer difficulty;
    /** 自动刷新时间表 */
    private List<CfgRefreshTime> refreshTime;

    @Data
    public static class CfgRefreshTime implements Serializable {
        private static final long serialVersionUID = 6283485026406890074L;
        /**周几*/
        private List<Integer> weekDays;
        /** 时间 */
        private Integer hour;
    }


}
