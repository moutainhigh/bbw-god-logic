package com.bbw.god.gameuser.treasure.processor;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.*;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 金风飒飒礼包
 *
 * @author fzj
 * @date 2021/12/1 17:33
 */
@Service
public class WindRustleBoxProcessor extends TreasureUseProcessor {

    @Autowired
    UserTreasureRecordService userTreasureRecordService;
    @Autowired
    GameUserService gameUserService;
    @Autowired
    BoxService boxService;
    @Autowired
    AwardService awardService;

    /** 保底次数 */
    private static final int MIN_GUARANTEE_NUM = 300;

    public WindRustleBoxProcessor() {
        this.treasureEnum = TreasureEnum.WING_RUSTLE_BOX;
        this.isAutoBuy = false;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
    }

    /**
     * 保底次数
     *
     * @return
     */
    @Override
    public Integer minGuaranteeNum() {
        return MIN_GUARANTEE_NUM;
    }

    /**
     * 开启宝箱
     *
     * @param uid
     * @param num
     * @param rd
     */
    public void open(long uid, int num, RDCommon rd) {
        //获取保底记录
        UserTreasureRecord utr = userTreasureRecordService.getOrCreateRecord(uid, TreasureEnum.WING_RUSTLE_BOX.getValue(), 0);
        Integer usedTimes = utr.getUseTimes();
        List<Integer> fashion = new ArrayList<>();
        //奖励集合
        List<Award> boxAwards = new ArrayList<>();
        //检查是否拥有时装
        boolean isHasFashion = TreasureChecker.hasTreasure(uid, TreasureEnum.WING_RUSTLE.getValue());
        for (int i = 1; i <= num; i++) {
            usedTimes++;
            //300次保底必得
            if (!isHasFashion && !fashion.contains(TreasureEnum.WING_RUSTLE.getValue()) && usedTimes == MIN_GUARANTEE_NUM) {
                boxAwards.add(Award.instance(TreasureEnum.WING_RUSTLE.getValue(), AwardEnum.FB, 1));
                utr.setUseTimes(usedTimes);
                fashion.add(TreasureEnum.WING_RUSTLE.getValue());
                gameUserService.updateItem(utr);
                continue;
            }
            //获得宝箱奖励
            Award award = boxService.getAward(uid, TreasureEnum.WING_RUSTLE_BOX.getValue()).stream().findFirst().orElse(null);
            if (null == award) {
                continue;
            }
            //如果已获得时装，则发放仙之源
            boolean isOwnFashion = isHasFashion || fashion.contains(TreasureEnum.WING_RUSTLE.getValue());
            if (award.getAwardId() == TreasureEnum.WING_RUSTLE.getValue() && isOwnFashion) {
                boxAwards.add(Award.instance(TreasureEnum.XZY.getValue(), AwardEnum.FB, 2));
                continue;
            }
            if (award.getAwardId() == TreasureEnum.WING_RUSTLE.getValue()) {
                fashion.add(TreasureEnum.WING_RUSTLE.getValue());
            }
            boxAwards.add(award);
        }
        awardService.sendNeedMergedAwards(uid, boxAwards, WayEnum.WING_RUSTLE_BOX, "", rd);
        //添加礼包使用次数
        rd.setGiftUseTimes(usedTimes);
        //更新保底记录
        utr.setUseTimes(usedTimes);
        gameUserService.updateItem(utr);
    }
}
