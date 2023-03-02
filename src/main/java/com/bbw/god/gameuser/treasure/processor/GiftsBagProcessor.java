package com.bbw.god.gameuser.treasure.processor;

import com.bbw.god.game.award.Award;
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

import java.util.ArrayList;
import java.util.List;

/**
 * 得物袋
 *
 * @author fzj
 * @date 2022/1/26 8:54
 */
@Service
public class GiftsBagProcessor extends TreasureUseProcessor {

    @Autowired
    BoxService boxService;
    @Autowired
    AwardService awardService;

    public GiftsBagProcessor() {
        this.treasureEnum = TreasureEnum.GIFTS_BAG;
        this.isAutoBuy = false;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {

    }

    /**
     * 开启宝箱
     *
     * @param uid
     * @param rd
     */
    public void open(long uid, int num, RDCommon rd) {
        List<Award> awardList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            //获得宝箱奖励
            Award award = boxService.getAward(uid, TreasureEnum.GIFTS_BAG.getValue()).stream().findFirst().orElse(null);
            if (null == award) {
                return;
            }
            awardList.add(award);
        }
        awardService.sendNeedMergedAwards(uid, awardList, WayEnum.WAR_TOKEN_FS_BOX, "", rd);
    }
}
