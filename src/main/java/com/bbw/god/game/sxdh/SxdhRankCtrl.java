package com.bbw.god.game.sxdh;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.game.sxdh.rd.RDSxdhLastSeasonRankerList;
import com.bbw.god.game.sxdh.rd.RDSxdhRankerAwardList;
import com.bbw.god.game.sxdh.rd.RDSxdhRankerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 神仙大会排行接口
 *
 * @author suhq
 * @date 2019-06-21 09:33:11
 */
@RestController
public class SxdhRankCtrl extends AbstractController {
    @Autowired
    private SxdhRankLogic sxdhRankLogic;

    /**
     * 获得排行
     *
     * @param rankType
     * @return
     */
    @GetMapping(CR.Sxdh.GET_FIGHTER_RANK)
    public RDSxdhRankerList getFighterRank(int rankType) {
        return sxdhRankLogic.getFighterRank(getUserId(), rankType);
    }

    @GetMapping(CR.Sxdh.GET_LAST_SEASON_RANK_AWARD)
    public RDSxdhLastSeasonRankerList getLastSeasonRankers(int zone, int page, int limit) {
        return sxdhRankLogic.getLastSeasonFighterRank(getUserId(), zone, page, limit);
    }

    @GetMapping(CR.Sxdh.GET_RANK_AWARD)
    public RDSxdhRankerAwardList getRankAward(int rankType) {
        return sxdhRankLogic.getRankAward(rankType);
    }

}
