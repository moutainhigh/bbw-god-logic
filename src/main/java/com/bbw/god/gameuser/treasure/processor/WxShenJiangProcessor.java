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
import com.bbw.god.gameuser.treasure.*;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 五行神将礼包
 *
 *
 * @author lwb
 */
@Service
public class WxShenJiangProcessor extends TreasureUseProcessor {
    @Autowired
    private UserCardService userCardService;
    @Autowired
    protected BoxService boxService;
    @Autowired
    UserTreasureRecordService userTreasureRecordService;
    @Autowired
    GameUserService gameUserService;
    @Autowired
    AwardService awardService;
    /** 保底次数 */
    private static final int MIN_GUARANTEE_NUM = 300;

    /**
     * 栢鉴、女娲灵守、马元、度厄真人、夔牛
     */
    public WxShenJiangProcessor() {
        this.treasureEnum = TreasureEnum.WX_SHEN_JIANG;
        this.isAutoBuy = false;
    }

    @Override
    public void check(GameUser gu, CPUseTreasure param) {
        TreasureChecker.checkIsEnough(this.treasureEnum.getValue(),1,gu.getId());
    }

    /**
     * 0.1% 概率获得四星卡牌
     * 范围：栢鉴、女娲灵守、马元、度厄真人、夔牛
     * 已获得的卡牌不会重复获得。
     * 当获得所有卡牌后，不会再获得卡牌。（概率增加到仙之源*1）
     *
     * @param gu
     * @param param
     * @param rd
     */
    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        open(gu.getId(), 1, rd);
    }

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
        UserTreasureRecord utr = userTreasureRecordService.getOrCreateRecord(uid, TreasureEnum.WX_SHEN_JIANG.getValue(), 0);
        Integer usedTimes = utr.getUseTimes();
        //获取宝箱中卡牌奖励集合
        List<Integer> cardIds = boxService.getBoxAllGoods(uid, TreasureEnum.WX_SHEN_JIANG.getValue())
                .stream().filter(b -> b.getItem() == AwardEnum.KP.getValue()).map(Award::gainAwardId).collect(Collectors.toList());
        List<Integer> notOwnAwardCards = getNotOwnAwardCards(uid, cardIds);
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
            //随机获得宝箱的一个奖励
            Award award = boxService.getAward(uid, TreasureEnum.WX_SHEN_JIANG.getValue()).stream().findFirst().orElse(null);
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
            //如果奖励为卡牌且玩家有该卡牌 奖励转为仙之源
            if (award.getItem() == AwardEnum.KP.getValue()) {
                boxAwards.add(Award.instance(TreasureEnum.XZY.getValue(), AwardEnum.FB, 1));
                continue;
            }
            boxAwards.add(award);
        }
        awardService.sendNeedMergedAwards(uid, boxAwards, WayEnum.WX_SHEN_JIANG, "", rd);
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
