package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.PowerRandom;
import com.bbw.god.gameuser.nightmarenvwam.NightmareNvWamCfgTool;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 史诗级每日女娲礼包
 *
 * @author fzj
 * @date 2022/5/9 17:37
 */
@Service
public class EpicDayNvWaGiftProcessor extends TreasureUseProcessor {
    /** 获得每日女娲礼包的概率 */
    private static final Integer GET_GIFT_PRO = 100;

    @Autowired
    BoxService boxService;
    @Autowired
    AwardService awardService;


    public EpicDayNvWaGiftProcessor() {
        this.treasureEnum = TreasureEnum.EPIC_DAY_NV_WA_GIFT;
        this.isAutoBuy = false;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {

    }

    /**
     * 开启宝箱
     *
     * @param uid
     * @param num
     * @param rd
     */
    public void open(long uid, int num, RDCommon rd) {
        List<Award> boxAllGoods = boxService.getBoxAllGoods(uid, TreasureEnum.EPIC_DAY_NV_WA_GIFT.getValue());
        if (PowerRandom.hitProbability(GET_GIFT_PRO)) {
            Integer dayGiftAwardType = NightmareNvWamCfgTool.getDayGiftAwardType();
            Integer treasureIdByType = NightmareNvWamCfgTool.getTreasureIdByType(dayGiftAwardType);
            boxAllGoods.add(new Award(treasureIdByType, AwardEnum.FB, 1));
        }
        awardService.fetchAward(uid, boxAllGoods, WayEnum.EPIC_DAY_NV_WA_GIFT, "", rd);
    }
}
