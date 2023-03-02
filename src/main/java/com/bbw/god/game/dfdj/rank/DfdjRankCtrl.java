package com.bbw.god.game.dfdj.rank;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.game.dfdj.rd.RDDfdjLastSeasonRankerList;
import com.bbw.god.game.dfdj.rd.RDDfdjRankerAwardList;
import com.bbw.god.game.dfdj.rd.RDDfdjRankerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author suchaobin
 * @description 巅峰对决排行榜接口
 * @date 2021/1/5 13:50
 **/
@RestController
public class DfdjRankCtrl extends AbstractController {
    @Autowired
    private DfdjRankLogic dfdjRankLogic;

    /**
     * 获得排行
     *
     * @param rankType,见RankType
     * @return
     */
    @GetMapping(CR.Dfdj.GET_FIGHTER_RANK)
    public RDDfdjRankerList getFighterRank(int rankType) {
        return dfdjRankLogic.getFighterRank(getUserId(), rankType);
    }

    @GetMapping(CR.Dfdj.GET_LAST_SEASON_RANK_AWARD)
    public RDDfdjLastSeasonRankerList getLastSeasonRankers(int zone, int page, int limit) {
        return dfdjRankLogic.getLastSeasonFighterRank(getUserId(), zone, page, limit);
    }

    @GetMapping(CR.Dfdj.GET_RANK_AWARD)
    public RDDfdjRankerAwardList getRankAward(int rankType) {
        return dfdjRankLogic.getRankAward(rankType);
    }
}
