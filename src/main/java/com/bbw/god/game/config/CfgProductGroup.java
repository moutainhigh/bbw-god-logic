package com.bbw.god.game.config;

import com.bbw.god.game.award.Award;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * 充值产品
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-04 11:26
 */
@Data
public class CfgProductGroup implements CfgInterface {

    private static int defaultGroupId = 1;// 默认产品组的组ID
    private int groupId;// 产品分组ID
    private List<CfgProduct> products;// 产品组
    private List<ProductAward> productAward;// 直冲产品的购买内容

    @Override
    public Integer getId() {
        return groupId;
    }

    @Override
    public int getSortId() {
        return groupId;
    }

    /**
     * 是否是默认
     *
     * @return
     */
    public boolean isDefault() {
        return defaultGroupId == groupId;
    }

    @Data
    public static class CfgProduct {
        /**
         * 月卡产品ID
         */
        public static final int YUEKA_ID = 11;
        /**
         * 速战卡产品ID
         */
        public static final int SUZHANKA_ID = 12;
        /**
         * 季卡产品ID
         */
        public static final int JIKA_ID = 21;
        /**
         * 永久季卡产品ID
         */
        public static final int JIKA_ID_FOREVER = 30;
        /** 充值签到卡 */
        public static final int RECHARGE_SIGN = 31;
        /**
         * 升级到永久季卡产品ID
         */
        public static final int UPGRADE_JIKA_ID_FOREVER = 32;

        /**
         * 特惠60元周套餐礼包
         */
        public static final int TE_HUI_GROUP_GIFT_PACK = 99001404;
        public static final int ZHAO_MU_GIFT_PACK = 99001043;//日-招募礼包
        public static final int LIAN_JI_GIFT_PACK = 99001105;//周-炼技礼包
        public static final int XIU_TI_GIFT_PACK = 99001103;//周-炼体礼包
        /**
         * 进阶战令
         */
        public static final int WAR_TOKEN = 99001600;
        /**
         * 高级进阶战令
         */
        public static final int WAR_TOKEN_SUP = 99001610;

        private Integer id;
        private Integer serial;
        private String name;
        private Integer quantity;// 元宝数量
        private Integer firstRate = 1;// 首次购买翻倍比例
        private Integer price;
        private Integer extraNum;// 额外赠送
        private Boolean isAvailable;
        private Boolean recommend;
        private Boolean isShow;// 是否在商品列表中展示
        private Boolean isZhiChong;// 是否直冲

        /**
         * 是否月卡
         *
         * @return
         */
        public boolean isYueKa() {
            return YUEKA_ID == id;
        }

        /**
         * 是否季卡
         *
         * @return
         */
        public boolean isJiKa() {
            return JIKA_ID == id;
        }

        /**
         * 是否是永久季卡
         *
         * @return
         */
        public boolean isForeverJiKa() {
            return JIKA_ID_FOREVER == id;
        }

        /**
         * 是否是升级到永久季卡
         *
         * @return
         */
        public boolean isUpgradeForeverJiKa() {
            return UPGRADE_JIKA_ID_FOREVER == id;
        }

        /**
         * 是否速战卡
         *
         * @return
         */
        public boolean isSuZhanKa() {
            return SUZHANKA_ID == id;
        }

        /**
         * 是否为充值签到卡
         *
         * @return
         */
        public boolean isRechargeSign() {
            return RECHARGE_SIGN == id;
        }

        /**
         * 是否是战令
         *
         * @return
         */
        public boolean isWarToken() {
            return id == WAR_TOKEN || id == WAR_TOKEN_SUP;
        }

        /**
         * 该商品是否在苹果上注册
         *
         * @return
         */
        public boolean isNotRegisteredOnIos() {
            List<Integer> notRegisteredIds = Arrays.asList(11, 12, 20, 21);
            return notRegisteredIds.contains(id);
        }

    }

    @Data
    public static class ProductAward {
        private Integer productId;// 产品ID
        private String memo;// 产品说明
        private List<Award> awardList;// 产品组
    }
}
