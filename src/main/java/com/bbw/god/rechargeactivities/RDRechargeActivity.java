package com.bbw.god.rechargeactivities;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.pay.RDProductList;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author lwb
 * @date 2020/7/1 15:46
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDRechargeActivity extends RDCommon {
    private static final long serialVersionUID = -8112574815176055249L;
    //显示的条目
    private List<Integer> showTypes = null;
    private Long countdown = null;//倒计时
    private List<GiftPackInfo> goodsList = null;
    private List<GoldPackInfo> products = null;
    private Integer firstBought = null;//首冲
    /** 天灵印礼包状态 */
    private Integer status = null;//状态 //0可购买,-1 不可购买,1是可领取
    /** 地灵印礼包状态  */
    private Integer diLingStatus = null;
    private Integer szkStatus = null;
    private Integer ykStatus = null;
    private Integer jkStatus = null;

    private Integer ykDays = null;
    private Integer jkDays = null;
    private Integer ykAwardStatus = null;
    private Integer jkAwardStatus = null;
    private Integer foreverJiKa = null;//是否是永久季卡

    private Integer goldBagTipBoxStatus = null;//状态 参考AwardStatus.enum
    private Integer countdownDays = null;//倒数天数
    /** 每日摇一摇福利id */
    private Integer dailyShakeWelfareId = 0;
    /** 是否活动每日首充 */
    private Integer activityPerDayFirstBought = null;
    private Date endDateTime;//结束时间

    @Data
    public static class GoldPackInfo implements Serializable {
        private static final long serialVersionUID = -2251449735639859413L;
        private Integer id;
        private Integer mallId;
        private Integer price;
        private Integer quantity;
        private Integer hasFirstBoughtAward;
        private int isBought;
        private String name = "";
        private int extraNum;

        public static GoldPackInfo instance(RDProductList.RDProduct product, CfgMallEntity mall) {
            GoldPackInfo info = instance(product);
            info.setName(mall.getName());
            info.setMallId(mall.getId());
            return info;
        }

        public static GoldPackInfo instance(RDProductList.RDProduct product) {
            GoldPackInfo info = new GoldPackInfo();
            info.setId(product.getId());
            info.setPrice(product.getPrice());
            info.setName(product.getName());
            info.setHasFirstBoughtAward(product.getHasFirstBoughtAward());
            info.setQuantity(product.getQuantity());
            info.setIsBought(product.getIsBought());
            info.setExtraNum(product.getExtraNum());
            return info;
        }
    }

    /**
     * 物品信息
     */
    @Data
    public static class GiftPackInfo implements Serializable {
        private static final long serialVersionUID = 5049180597349164213L;
        private Integer realId;//游戏默认配置的简写ID
        private Integer rechargeId;//游戏默认配置的实际ID
        private Integer mallId;//商城配置的ID
        private Integer price;//价格
        private Integer Limit;//限购次数
        private List<Award> awards;//物品信息
        private Integer remainTimes;//剩余次数
        private Integer status = 0;//0可购买,-1 不可购买,1是可领取
        private String title = "";//标题
        private Integer unit;//购买方式
        private Integer extraAwardStatus = null;//0未选,1已选
        private List<Award> extraAwards;//额外奖励列表，有是才返回
        private Long remainTime;//剩余时间
        /** 福利Id */
        private int welfareId = 0;
        /** 是否首充 */
        private int isBought;


        public static GiftPackInfo instance(CfgMallEntity mall, int price) {
            GiftPackInfo info = new GiftPackInfo();
            info.setPrice(price);
            info.setMallId(mall.getId());
            info.setRealId(mall.getGoodsId());
            info.setRechargeId(99000000 + mall.getGoodsId());
            info.setLimit(mall.getLimit());
            info.setRemainTimes(mall.getLimit());
            if (price == 0) {
                info.setStatus(1);
            }
            info.setUnit(mall.getUnit());
            info.setTitle(mall.getName());
            return info;
        }

        /**
         * 更新购买次数  以及状态
         *
         * @param times
         */
        public void updateRemainTimes(int times) {
            if (times < 0) {
                return;
            }
            this.remainTimes = times;
            if (this.remainTimes == 0) {
                this.status = -1;
            }
        }
    }
}
