package com.bbw.god.gameuser.treasure.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.holiday.processor.HolidayDigForTreasureProcessor;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.CfgDigForTreasure;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureRecord;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 地图挖宝
 *
 */
@Service
public class DigForTreasureProcessor extends TreasureUseProcessor {

    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private HolidayDigForTreasureProcessor holidayDigForTreasureProcessor;
    @Autowired
    private AwardService awardService;
    @Autowired
    private UserTreasureRecordService userTreasureRecordService;
    private static final int NEED_NUM = 1;

    public DigForTreasureProcessor() {
        this.treasureEnum = TreasureEnum.CHANG_ZI;
        this.isAutoBuy = false;
    }

    @Override
    public int getNeedNum(GameUser gu, int useTimes, WayEnum way) {
        return NEED_NUM;
    }

    @Override
    public void check(GameUser gu, CPUseTreasure param) {
        if (!holidayDigForTreasureProcessor.opened(gu.getServerId())){
            //活动过期
            throw new ExceptionForClientTip("activity.is.timeout");
        }
        int cacheTimes = getCacheTimes(gu);
        if (cacheTimes>=3){
            throw new ExceptionForClientTip("dig.for.treasure.empty");
        }
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        TreasureEventPublisher.pubTRecordAddEvent(gu.getId(), this.treasureEnum.getValue(), WayEnum.TREASURE_USE);
        int pos = gu.getLocation().getPosition();
        int gid = gameUserService.getActiveGid(gu.getId());
        Optional<CfgDigForTreasure.AdditionalAward> awardOptional = holidayDigForTreasureProcessor.canGainAdditionalAwardAndAddLogs(gu.getId(), pos, gid);
        List<Award> awards = null;
        if (awardOptional.isPresent()){
            //大奖
            CfgDigForTreasure.AdditionalAward additionalAward = awardOptional.get();
            awards=additionalAward.getAwards();
        }else {
            awards = HolidayDigForTreasureProcessor.randomAwardsByRandomPool();
        }
        awardService.fetchAward(gu.getId(),awards, WayEnum.DIG_FOR_TREASURE,"在"+WayEnum.DIG_FOR_TREASURE.getName(),rd);
        rd.setDigEmpty(0);
    }

    /**
     * 已经在此地使用的次数
     * @param gu
     * @return
     */
    public int getCacheTimes(GameUser gu){
        // 加使用次数
        UserTreasureRecord utr = userTreasureRecordService.getRecord(gu.getId(), treasureEnum.getValue(), WayEnum.TREASURE_USE);
        if (utr != null && utr.getLastUsePos() == gu.getLocation().getPosition()) {
            return utr.getUseTimes();
        }
        return 0;
    }
}
