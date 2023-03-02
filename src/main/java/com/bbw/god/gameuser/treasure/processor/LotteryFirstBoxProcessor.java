package com.bbw.god.gameuser.treasure.processor;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureRecord;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.gameuser.yuxg.Enum.SparEnum;
import com.bbw.god.random.box.BoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 奖券一等奖宝箱
 *
 * @author fzj
 * @date 2021/12/1 17:53
 */
@Service
public class LotteryFirstBoxProcessor extends TreasureUseProcessor {
    @Autowired
    UserTreasureRecordService userTreasureRecordService;
    @Autowired
    UserCardService userCardService;
    @Autowired
    BoxService boxService;
    @Autowired
    GameUserService gameUserService;
    /** 保底次数 */
    private static final int SECURITY_TIMES = 3;

    public LotteryFirstBoxProcessor() {
        this.treasureEnum = TreasureEnum.LOTTERY_FIRST_BOX;
        this.isAutoBuy = true;
    }

    /**
     * 是否宝箱类
     *
     * @return
     */
    @Override
    public boolean isChestType() {
        return true;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        long uid = gu.getId();
        //获取保底记录
        UserTreasureRecord utr = userTreasureRecordService.getOrCreateRecord(uid, TreasureEnum.LOTTERY_FIRST_BOX.getValue(), 0);
        Integer usedTimes = utr.getUseTimes();
        usedTimes++;
        //判断是否拥有金睛白虎
        boolean isOwnJinJingTiger = userCardService.getUserCard(uid, CardEnum.JINJING_TIGER.getCardId()) != null;
        //没有白虎且本次是第三次使用
        if (!isOwnJinJingTiger && usedTimes == SECURITY_TIMES) {
            CardEventPublisher.pubCardAddEvent(uid, CardEnum.JINJING_TIGER.getCardId(), WayEnum.LOTTERY_FIRST_BOX, "", rd);
            utr.setUseTimes(usedTimes);
            gameUserService.updateItem(utr);
            return;
        }
        //获得宝箱奖励
        Award award = boxService.getAward(uid, TreasureEnum.LOTTERY_FIRST_BOX.getValue()).stream().findFirst().orElse(null);
        if (null == award) {
            return;
        }
        Award boxAward = null;
        //如果已有金睛白虎则发放血晶
        if (isOwnJinJingTiger && award.getAwardId().equals(CardEnum.JINJING_TIGER.getCardId())) {
            boxAward = new Award(SparEnum.XUE_JING.getSparId(), AwardEnum.FB, 20);
        }
        //判断是否拥有腾蛇
        boolean isOwnTengSnake = userCardService.getUserCard(uid, CardEnum.TENG_SNAKE.getCardId()) != null;
        //如果已有腾蛇发放随机高级卷轴
        if (isOwnTengSnake && award.getAwardId().equals(CardEnum.TENG_SNAKE.getCardId())) {
            boxAward = new Award(TreasureEnum.RANDOM_ADVANCED_SCROLL.getValue(), AwardEnum.FB, 1);
        }
        if (null != boxAward) {
            TreasureEventPublisher.pubTAddEvent(uid, boxAward.gainAwardId(), boxAward.getNum(), WayEnum.LOTTERY_FIRST_BOX, rd);
        }
        if (null == boxAward && award.getItem() == AwardEnum.KP.getValue()) {
            CardEventPublisher.pubCardAddEvent(uid, award.gainAwardId(), WayEnum.LOTTERY_FIRST_BOX, "", rd);
        } else if (award.getItem() != AwardEnum.KP.getValue()) {
            TreasureEventPublisher.pubTAddEvent(uid, award.gainAwardId(), award.getNum(), WayEnum.LOTTERY_FIRST_BOX, rd);
        }
        utr.setUseTimes(usedTimes);
        gameUserService.updateItem(utr);
    }

}
