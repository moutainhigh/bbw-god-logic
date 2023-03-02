package com.bbw.god.gameuser.res.dice;

import com.bbw.common.ID;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.dice.IncDiceService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author：lwb
 * @date: 2021/1/7 14:33
 * @version: 1.0
 */
@Service
public class UserDiceCapacityService {
    @Autowired
    private IncDiceService incDiceService;
    /**
     * 最大存储量
     */
    private static final Integer MAX_DICE_CAPACITY = 360;
    private static final Integer ONCE_BUY_MAX_DICE = 60;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 获取体力罐信息
     *
     * @param user
     * @return
     */
    public RDDiceCapacity getDiceCapacityInfo(GameUser user) {
        incDiceService.limitIncDice(user);
        RDDiceCapacity rd = new RDDiceCapacity();
        rd.setMaxDiceCapacity(MAX_DICE_CAPACITY);
        UserDiceCapacity capacity = getUserDiceCapacity(user.getId());
        rd.setDiceCapacity(capacity.getDice());
        return rd;
    }

    /**
     * 赎回体力
     *
     * @param uid
     * @return
     */
    public RDDiceCapacity buyDiceByCapacity(long uid) {
        RDDiceCapacity rd = new RDDiceCapacity();
        rd.setMaxDiceCapacity(MAX_DICE_CAPACITY);
        UserDiceCapacity capacity = getUserDiceCapacity(uid);
        rd.setDiceCapacity(capacity.getDice());
        int buyNum = Math.min(ONCE_BUY_MAX_DICE, capacity.getDice());
        if (buyNum <= 0) {
            return rd;
        }
        Double sum = buyNum * 0.5;
        int need = Math.max(1, sum.intValue());
        ResChecker.checkGold(gameUserService.getGameUser(uid), need);
        ResEventPublisher.pubGoldDeductEvent(uid, need, WayEnum.DICE_CAPACITY, rd);
        changeUserDice(uid, -buyNum);
        ResEventPublisher.pubDiceAddEvent(uid, buyNum, WayEnum.DICE_CAPACITY, rd);
        return rd;
    }

    /**
     * 获取体力存储  不存在时创建
     *
     * @param uid
     * @return
     */
    public UserDiceCapacity getUserDiceCapacity(long uid) {
        UserDiceCapacity item = gameUserService.getSingleItem(uid, UserDiceCapacity.class);
        if (item == null) {
            item = new UserDiceCapacity();
            item.setGameUserId(uid);
            item.setId(ID.INSTANCE.nextId());
            gameUserService.addItem(uid, item);
        }
        return item;
    }

    /**
     * 改变体力值
     *
     * @param uid
     * @param num
     * @return
     */
    public UserDiceCapacity changeUserDice(Long uid, int num) {
        synchronized (uid) {
            UserDiceCapacity diceCapacity = getUserDiceCapacity(uid);
            int newNum = diceCapacity.getDice() + num;
            if (num > 0) {
                diceCapacity.setDice(Math.min(MAX_DICE_CAPACITY, newNum));
            } else {
                diceCapacity.setDice(Math.max(0, newNum));
            }
            gameUserService.updateItem(diceCapacity);
            return diceCapacity;
        }
    }
}
