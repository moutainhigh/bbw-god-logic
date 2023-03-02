package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureRecord;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 战令三星神将礼包
 *
 * @author fzj
 * @date 2021/12/1 17:35
 */
@Service
public class WarTokenThreeStarBoxProcessor extends TreasureUseProcessor {

    @Autowired
    UserTreasureRecordService userTreasureRecordService;
    @Autowired
    UserCardService userCardService;
    @Autowired
    BoxService boxService;
    @Autowired
    GameUserService gameUserService;
    @Autowired
    AwardService awardService;

    /** 保底次数 */
    public static final int MIN_GUARANTEE_NUM = 200;

    public WarTokenThreeStarBoxProcessor() {
        this.treasureEnum = TreasureEnum.WAR_TOKEN_TS_BOX;
        this.isAutoBuy = false;
    }

    @Override
    public Integer minGuaranteeNum() {
        return MIN_GUARANTEE_NUM;
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
        //获取保底记录
        UserTreasureRecord utr = userTreasureRecordService.getOrCreateRecord(uid, TreasureEnum.WAR_TOKEN_TS_BOX.getValue(), 0);
        Integer usedTimes = utr.getUseTimes();
        //获取宝箱中卡牌奖励集合
        List<Integer> awardCards = boxService.getBoxAllGoods(uid, TreasureEnum.WAR_TOKEN_TS_BOX.getValue())
                .stream().filter(b -> b.getItem() == AwardEnum.KP.getValue()).map(Award::gainAwardId).collect(Collectors.toList());
        List<Integer> notOwnAwardCards = getNotOwnAwardCards(uid, awardCards);
        //奖励集合
        List<Award> boxAwards = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            usedTimes++;
            //保底发放
            if (!notOwnAwardCards.isEmpty() && usedTimes == MIN_GUARANTEE_NUM) {
                Integer randomCardId = PowerRandom.getRandomFromList(notOwnAwardCards);
                boxAwards.add(Award.instance(randomCardId, AwardEnum.KP, 1));
                //获得卡牌,保底记录清零
                usedTimes = 0;
                notOwnAwardCards.removeIf(c -> c.equals(randomCardId));
                continue;
            }
            //如果已拥有宝箱中的全部卡牌，保底记录不增加
            if (notOwnAwardCards.isEmpty()) {
                usedTimes = 0;
            }
            //获得宝箱奖励
            Award award = boxService.getAward(uid, TreasureEnum.WAR_TOKEN_TS_BOX.getValue()).stream().findFirst().orElse(null);
            if (null == award) {
                continue;
            }
            //如果奖励为卡牌且玩家没有该卡牌
            if (award.getItem() == AwardEnum.KP.getValue() && notOwnAwardCards.contains(award.gainAwardId())) {
                boxAwards.add(award);
                //获得卡牌,保底记录清零
                usedTimes = 0;
                notOwnAwardCards.removeIf(c -> c.equals(award.getAwardId()));
                continue;
            }
            //如果奖励为卡牌且玩家拥有该卡牌
            if (award.getItem() == AwardEnum.KP.getValue()) {
                boxAwards.add(Award.instance(TreasureEnum.XZY.getValue(), AwardEnum.FB, 1));
                continue;
            }
            boxAwards.add(award);
        }
        awardService.sendNeedMergedAwards(uid, boxAwards, WayEnum.WAR_TOKEN_TS_BOX, "", rd);
        //添加礼包使用次数
        rd.setGiftUseTimes(usedTimes);
        utr.setUseTimes(usedTimes);
        gameUserService.updateItem(utr);
    }

    /**
     * 获得宝箱卡牌中未拥有的卡牌集合
     *
     * @param uid
     * @param awardCards
     * @return
     */
    private List<Integer> getNotOwnAwardCards(long uid, List<Integer> awardCards) {
        List<Integer> ownCards = userCardService.getUserCards(uid).stream().map(UserCfgObj::getBaseId).collect(Collectors.toList());
        return awardCards.stream().filter(a -> !ownCards.contains(a)).collect(Collectors.toList());
    }
}
