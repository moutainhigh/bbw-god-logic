package com.bbw.god.gameuser.treasure.processor;

import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.chengc.RDArriveChengC;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureRecord;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.gameuser.yaozu.UserYaoZuInfo;
import com.bbw.god.gameuser.yaozu.UserYaoZuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 醒酒毡
 *
 * @author suhq
 * @date 2018年11月29日 上午9:12:46
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class XingJZProcessor extends TreasureUseProcessor {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserYaoZuInfoService userYaoZuInfoService;
    @Autowired
    private UserTreasureRecordService userTreasureRecordService;

    public XingJZProcessor() {
        this.treasureEnum = TreasureEnum.XJZ;
        this.isAutoBuy = true;
    }

    @Override
    public int getNeedNum(GameUser gu, int useTimes, WayEnum way) {
        UserTreasureRecord utr = userTreasureRecordService.getRecord(gu.getId(), treasureEnum.getValue(), way);
        if (utr != null && utr.getLastUsePos() == gu.getLocation().getPosition()) {
            return 1 + utr.getUseTimes();
        }
        return 1;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        // 加使用次数
        TreasureEventPublisher.pubTRecordAddEvent(gu.getId(), treasureEnum.getValue(),
                WayEnum.fromValue(param.getWay()));
        // 初始战斗为未结算
        TimeLimitCacheUtil.removeCache(gu.getId(), RDFightResult.class);
    }

    /**
     * 生效前校验
     *
     * @param gu
     * @param param
     */
    @Override
    public void check(GameUser gu, CPUseTreasure param) {
        //妖族来犯 且 该位置上有妖族
        int pos = gu.getLocation().getPosition();
        //判断是否在梦魇世界
        boolean isNightmare = gu.getStatus().intoNightmareWord();
        if (isNightmare) {
            //判断是否有妖族
            UserYaoZuInfo yaoZuInfo = userYaoZuInfoService.getUserYaoZuInfoByPos(gu.getId(), pos);
            if (null != yaoZuInfo) {
                return;
            }
        }
        CfgCityEntity city = gu.gainCurCity();
        if (!city.isCC()) {
            return;
        }
        RDArriveChengC rdArriveChengC = TimeLimitCacheUtil.getChengCCache(gu.getId());
        Integer fightType = rdArriveChengC.getFightType();
    }
}
