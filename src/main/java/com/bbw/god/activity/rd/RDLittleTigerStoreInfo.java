package com.bbw.god.activity.rd;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.List;

/**
 * 小虎商店返回信息
 *
 * @author fzj
 * @date 2022/3/12 0:08
 */
@Data
public class RDLittleTigerStoreInfo extends RDSuccess {
    /** 累计刷新次数 */
    private int totalRefreshTimes;
    /** 累计刷新奖励 */
    private List<RefreshAwards> refreshAwards;
    /** 当前奖励 */
    private List<CurrentAwards> currentAwards;

    @Data
    public static class CurrentAwards {
        /** 号码 */
        private int number;
        /** 奖励 */
        private RDAward award;
        /** 状态 */
        private int status;
    }

    @Data
    public static class RefreshAwards {
        /** 需要刷新次数 */
        private int needRefreshTimes;
        /** 奖励 */
        private List<RDAward> award;
        /** 状态 */
        private int status;
    }

}
