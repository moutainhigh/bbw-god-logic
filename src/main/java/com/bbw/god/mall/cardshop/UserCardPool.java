package com.bbw.god.mall.cardshop;

import com.bbw.common.ID;
import com.bbw.god.game.config.CfgWishCard.WishCard;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

import java.util.Date;

/**
 * 玩家卡池记录
 *
 * @author suhq
 * @date 2019-05-07 14:19:19
 */
@Data
public class UserCardPool extends UserData {
    // 卡池
    private Integer cardPool;
    // 是否解锁
    private Integer isUnlock;// 1是解锁
    // 源晶奖励结束时间
    private Date awardEndDate;
    // 使用元宝抽卡次数
    private Integer goldTenDrawTimes = 0;
    // 许愿卡
    private Integer wishCard = -1;
    // 许愿值
    private Integer wishValue = 0;
    // 额外赠送的许愿值
    private Integer extraWishValue = 0;
    // 需要的许愿值
    private Integer needWish = 0;

    public static UserCardPool Instance(long guId, int cardPool, CardPoolStatusEnum status, Date awardEndDate, WishCard wishCard) {
        UserCardPool ucp = Instance(guId, cardPool, status, awardEndDate);
        ucp.setWishCard(wishCard.getId());
        ucp.setNeedWish(wishCard.getNeedWish());
        return ucp;
    }

    public static UserCardPool Instance(long guId, int cardPool, CardPoolStatusEnum status, Date awardEndDate) {
        UserCardPool ucp = new UserCardPool();
        ucp.setId(ID.INSTANCE.nextId());
        ucp.setGameUserId(guId);
        ucp.setCardPool(cardPool);
        ucp.setIsUnlock(status.getValue());
        ucp.setAwardEndDate(awardEndDate);
        return ucp;
    }

    public static UserCardPool Instance(long guId, int cardPool, CardPoolStatusEnum status) {
        UserCardPool ucp = new UserCardPool();
        ucp.setId(ID.INSTANCE.nextId());
        ucp.setGameUserId(guId);
        ucp.setCardPool(cardPool);
        ucp.setIsUnlock(status.getValue());
        return ucp;
    }

    /**
     * 重新恢复到上锁状态
     */
    public void resetLock(boolean isLock, Date awardEndDate) {
        this.isUnlock = isLock ? 0 : 1;
        this.awardEndDate = awardEndDate;
        this.goldTenDrawTimes = 0;
    }

    public void addGoldTenDrawTimes() {
        this.goldTenDrawTimes++;
    }

    public void addWishValue(int addedNum) {
        this.wishValue += addedNum;
        if (this.wishValue + this.extraWishValue >= needWish) {
            this.wishValue = needWish - this.extraWishValue;
        }
    }

    public boolean ifUnlock() {
        return isUnlock == CardPoolStatusEnum.UNLOCK.getValue();
    }

    public boolean ifHasWishCard() {
        return wishCard != -1;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.CARD_POOL;
    }

    /**
     * 重置许愿值
     *
     */
    public void resetWishValue(){
        this.wishValue = 0;
        this.extraWishValue = 0;
    }
}
