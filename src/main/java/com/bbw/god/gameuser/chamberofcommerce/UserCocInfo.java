package com.bbw.god.gameuser.chamberofcommerce;

import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 玩家商会相关信息的  数据实体
 *
 * @author lwb
 * @version 1.0
 * @date 2019年4月12日
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserCocInfo extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer honorLevel = 1;//默认设置1级
    private Integer taskBuildDate = 20200101;// 任务生成时间
    private Integer unclaimed = 0;// 提示玩家查看头衔信息 默认不提示
    private CocShopLimt cocShopLimt = new CocShopLimt();
    // 下面部分将分离
    private Integer honor = null;// 积分
    private Integer goldCoin = null;// 商会金币数量

    public int getHonor() {
        if (honor == null) {
            return 0;
        }
        return honor;
    }

    public int getGoldCoin() {
        if (goldCoin == null || goldCoin < 0) {
            return 0;
        }
        return goldCoin;
    }

    public boolean CocShopLimted(int shopId, int limit) {
        if (cocShopLimt == null || ListUtil.isEmpty(cocShopLimt.getLimits())) {
            return false;
        }
        Optional<CocLimtShopItem> optional = cocShopLimt.getLimits().stream().filter(p -> p.getShopId() == shopId)
                .findFirst();
        if (optional.isPresent()) {
            return optional.get().getBought() >= limit;
        }
        return false;
    }

    public void addCocShopBought(int shopId) {
        Optional<CocLimtShopItem> optional = cocShopLimt.getLimits().stream().filter(p -> p.getShopId() == shopId)
                .findFirst();
        if (optional.isPresent()) {
            optional.get().addBought();
            return;
        }
        CocLimtShopItem item = CocLimtShopItem.instance(shopId);
        cocShopLimt.getLimits().add(item);
    }

    /**
     * 是否购买了头衔礼包
     *
     * @param giftId
     * @return
     */
    public boolean boughtGift(int giftId) {
        return cocShopLimt.getHonorGiftsBuyLogs().contains(giftId);
    }

    @Data
    public static class CocShopLimt implements Serializable {
        private static final long serialVersionUID = 1L;
        private List<CocLimtShopItem> limits = new ArrayList<UserCocInfo.CocLimtShopItem>();// 限购商品 的次数信息
        private List<Integer> honorGiftsBuyLogs = new ArrayList<Integer>();// 头衔礼包购买记录 存贮的值为已购礼包 对应的头衔等级
        private Integer honorLevel = 0;// 当前商品限制的头衔等级

    }

    @Data
    public static class CocLimtShopItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer shopId;
        private Integer bought = 0;//已购买次数

        public static CocLimtShopItem instance(int shopId) {
            CocLimtShopItem item = new CocLimtShopItem();
            item.setShopId(shopId);
            item.setBought(1);
            return item;
        }

        public void addBought() {
            bought++;
        }
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.Chamber_Of_Commerce_User_Info;
    }

    public boolean isThisLevel(int level) {
        return honorLevel == level;
    }

}
