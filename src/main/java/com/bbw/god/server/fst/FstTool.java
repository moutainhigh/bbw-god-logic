package com.bbw.god.server.fst;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgFst;
import com.bbw.god.server.fst.game.FstRankingType;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 说明：封神台工具
 *
 * @author lwb
 * date 2021-06-29
 */
public class FstTool {
    public static final String REDIS_KEY = "fstzset";// 封神台
    public static CfgFst getCfg(){
        return Cfg.I.getUniqueConfig(CfgFst.class);
    }

    public static List<CfgFst.RankingAward> getGameRankingAwards(FstRankingType type){
        return getCfg().getGameFstRankingAwards().stream().filter(p->p.getType().equals(type.getType())).collect(Collectors.toList());
    }
    
    public static int getGameRankingAward(FstRankingType type,int rank){
        Optional<CfgFst.RankingAward> optional = getGameRankingAwards(type).stream().filter(p -> p.ifThis(rank)).findFirst();
        if (optional.isPresent()){
            return optional.get().getNum();
        }
        return 0;
    }
    
    public static CfgFst.GameFstPromotion getGameFstPromotion(FstRankingType type){
        return getCfg().getGameFstPromotions().stream().filter(p->p.getType()==type.getType()).findFirst().get();
    }
    
    public static Long getNextSettleTime(Integer pre){
        CfgFst cfg = getCfg();
        String format = String.format("%d%d", pre, cfg.getGameFstSettleBeginTime());
        Date date = DateUtil.fromDateLong(Long.parseLong(format));
        Date nextSettleTime = DateUtil.addDaysWithoutHHmmss(date, cfg.getGameFstSettleIntervalDay());
        return DateUtil.millisecondsInterval(nextSettleTime,DateUtil.now());
    }

    /**
     * 获取封神台结束的具体时间
     *
     * @param settleEndDateInt
     * @return
     */
    public static Date getSettleEndDateTime(Integer settleEndDateInt) {
        CfgFst cfg = getCfg();
        String format = String.format("%d%d", settleEndDateInt, cfg.getGameFstSettleBeginTime());
        Date settleEndDate = DateUtil.fromDateLong(Long.parseLong(format));
        return settleEndDate;
    }

    /**
     * 封神台积分
     *
     * @param rank
     * @return
     */
    public static int getPoinByRank(int rank) {
        int addedPoint = 0;
        if (rank == 1) {
            addedPoint = 400;
        } else if (rank == 2) {
            addedPoint = 360;
        } else if (rank == 3) {
            addedPoint = 350;
        } else if (rank >= 4 && rank <= 10) {
            addedPoint = 330 - (rank - 4) * 5;
        } else if (rank >= 11 && rank <= 100) {
            addedPoint = 288 - (rank - 11) / 3;
        } else if (rank >= 101 && rank <= 400) {
            addedPoint = 199 - (rank - 101) / 10;
        } else if (rank >= 401 && rank <= 1000) {
            addedPoint = 169 - (rank - 401) / 25;
        } else if (rank >= 1001 && rank <= 1600) {
            addedPoint = 144 - (rank - 1001) / 40;
        } else if (rank >= 1601 && rank <= 3200) {
            addedPoint = 129 - (rank - 1601) / 100;
        } else if (rank >= 3201 && rank <= 25000) {
            addedPoint = 113 - (rank - 3201) / 200;
        }
        return addedPoint;
    }
}
