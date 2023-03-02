package com.bbw.god.game.config.mall;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.CfgInterface;
import com.bbw.god.game.config.CfgPrepareListInterface;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商城相关常量配置
 *
 * @author suhq
 * @date 2019-07-29 11:17:36
 */
@Slf4j
@Data
public class CfgMall implements CfgInterface, CfgPrepareListInterface, Serializable {
    private static final long serialVersionUID = 2248622441645355476L;
    private String key;
    /** 商城物品最大可购数 */
    private int maxBuyNum;
    /** 神秘的数量 */
    private int myteriousNum;
    /** 神秘刷新需要的元宝 */
    private int myteriousRefreshGold;
    /** 神秘每日可刷新次数 */
    private int mysteriousRefreshLimit;
    /** 3星灵石概率0.2 */
    private int lsRate3;
    /** 4星灵石概率0.2 */
    private int lsRate4;
    /** 5星灵石概率0.3 */
    private int lsRate5;
    /** 聚灵旗概率0.4 */
    private int jxqRate;
    /** 神秘商品分组的数量（1-铜钱商品、2-元宝商品、3-钻石商品） */
    private List<MallPartNum> mallPartNums;
    /** 随机种子数(随机池大小：默认10000) */
    private int ramSeed;
    /** 折扣类型 */
    private List<DiscountType> discounts;
    /** 折扣组合详情 */
    private List<DiscountGroup> discountGroups;

    /** goodsId:activityType */
    private Map<Integer, Integer> mallActivityBagMap;
    /** 封神大陆神秘概率 */
    private List<MysteriousMallProb> fsdlMysteriousMallProbs;
    /** 梦魇世界神秘概率 */
    private List<MysteriousMallProb> nightmareMysteriousMallProbs;

    /**
     * 商城所有物品
     **/
    private List<CfgMallEntity> malls;
    /**
     * 商城为显示的物品
     **/
    private List<CfgMallEntity> notShowMalls;
    /** 商城卡包 **/
    // private List<CfgMallEntity> kbMalls;
    /**
     * 商城道具
     **/
    private List<CfgMallEntity> treasureMalls;
    /**
     * 商城神秘道具
     **/
    private List<CfgMallEntity> mysteriousMalls;
    /**
     * 商城礼包
     **/
    private List<CfgMallEntity> favorableMalls;
    /**
     * 节日限时礼包
     **/
    private List<CfgMallEntity> holidayLimitTimeMalls;
    /**
     * 节日限时礼包51
     **/
    private List<CfgMallEntity> holidayLimitTimeMalls51;
    /**
     * 新手礼包
     */
    private List<CfgMallEntity> newerPackageMalls;
    /**
     * 通天残卷礼包
     **/
    private List<CfgMallEntity> ttcjBagMalls;
    /**
     * 封神台兑换物品
     **/
    private List<CfgMallEntity> fstMalls;

    /** 封神台卡牌ID集 */
    private List<Integer> fstCardIds;
    /**
     * 诛仙阵兑换物品
     **/
    private List<CfgMallEntity> zxzMalls;
    /**
     * 星君宝库兑换物品
     **/
    private List<CfgMallEntity> xjbkMalls;
    /**
     * 元宝消费积分兑换
     */
    private List<CfgMallEntity> goldConsumeMalls;
    /**
     * 日充值礼包
     */
    private List<CfgMallEntity> dailyRechargeMalls;
    /**
     * 周充值礼包
     */
    private List<CfgMallEntity> weekRechargeMalls;
    /**
     * 月充值礼包（元宝礼包）
     */
    private List<CfgMallEntity> goldRechargeMalls;
    /**
     * 钻石礼包
     */
    private List<CfgMallEntity> diamondRechargeMalls;
    private List<CfgMallEntity> activityMalls;
    /**
     * 助力礼包ID
     **/
    private List<Integer> zlMallIds;
    /**
     * 魔王商店兑换物品
     **/
    private List<CfgMallEntity> maouMalls;
    /**
     * 节日活动兑换物品
     **/
    private List<CfgMallEntity> holidayExchangeMalls;
    /**
     * 合服兑换物品
     **/
    private List<CfgMallEntity> combinedServiceExchangeMalls;
    /**
     * 花赋予神
     **/
    private List<CfgMallEntity> flowerToGodMalls;
    /**
     * 节日天灯工坊兑换物品
     **/
    private List<CfgMallEntity> holidaySkyLanternWorkShopMalls;
    /**
     * 节日材料商店
     **/
    private List<CfgMallEntity> holidayMaterialsStoreMalls;
    /**
     * 活动随机兑换物品
     **/
    private List<CfgMallEntity> activityRandomExchangeMalls;

    /**
     * 神仙大会购买记录
     **/
    private List<CfgMallEntity> sxdhMalls;

    /**
     * 神仙大会购买记录
     **/
    private List<CfgMallEntity> dfdjMalls;

    /**
     * 奇遇购买记录
     **/
    private List<CfgMallEntity> adventureMalls;

    /**
     * 夺宝商店兑换物品
     */
    private List<CfgMallEntity> snatchTreasureMalls;
    /**
     * 奇珍特惠礼包
     */
    private List<CfgMallEntity> teHuiRechargeMalls;

    /**
     * 限时礼包
     */
    private List<CfgMallEntity> roleTimeLimitMalls;
    /**
     * 商城道具
     **/
    private List<CfgMallEntity> faceMalls;
    /**
     * 赛马商店
     **/
    private List<CfgMallEntity> horseRacingMalls;
    /**
     * 战令商店
     **/
    private List<CfgMallEntity> warTokenMalls;

    /** 轮回商店 */
    private List<CfgMallEntity> transmigrationMalls;

    /** 折扣变化商店 */
    private List<CfgMallEntity> discountChangeMalls;
    /** 合服折扣变化商店 */
    private List<CfgMallEntity> combinedServiceDiscountChangeMalls;
    /** 封神祭坛 */
    private List<CfgMallEntity> godsAltarMalls;
    /** 万圣餐厅买卖 */
    private List<CfgMallEntity> halloweenRestaurantMalls;
    /** 万圣餐厅兑换 */
    private List<CfgMallEntity> halloweenRestaurantRedemptionMalls;
    /** 系列活动（竞猜商店） */
    private List<CfgMallEntity> guessingStoreMalls;
    /** 荣耀币商店 */
    private List<CfgMallEntity> gloryCoinStoreMalls;
    /** 节日礼包-51 */
    private List<CfgMallEntity> holidayGiftPackMalls;
    /** 生肖对碰 */
    private List<CfgMallEntity> holidayChineseZodiacCollisionMalls;

    @Override
    public void prepare() {
        malls = MallTool.getGoods().stream().filter(tmp -> tmp.getStatus()).collect(Collectors.toList());
        notShowMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.NOT_SHOWED.getValue())
                .collect(Collectors.toList());
        treasureMalls = malls.stream().filter(mall -> mall.getType() == MallEnum.DJ.getValue())
                .collect(Collectors.toList());

        mysteriousMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.SM.getValue())
                .collect(Collectors.toList());
        // kbMalls = malls.stream().filter(mall -> mall.getType() == MallEnum.KB.getValue())
        // .collect(Collectors.toList());
        favorableMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.THLB.getValue())
                .collect(Collectors.toList());
        ttcjBagMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.TTCJ_LB.getValue())
                .collect(Collectors.toList());
        zlMallIds = malls.stream().filter(mall -> mall.getType() == MallEnum.ZLLB.getValue())
                .map(CfgMallEntity::getGoodsId).collect(Collectors.toList());
        fstMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.FST.getValue())
                .collect(Collectors.toList());
        fstCardIds = fstMalls.stream().filter(m -> m.getItem() == AwardEnum.KP.getValue()).map(CfgMallEntity::getGoodsId).collect(Collectors.toList());
        zxzMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.ZXZ.getValue())
                .collect(Collectors.toList());
        xjbkMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.XJBK.getValue())
                .collect(Collectors.toList());
        goldConsumeMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.GOLD_CONSUME.getValue())
                .collect(Collectors.toList());
        dailyRechargeMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.DAILY_RECHARGE_BAG.getValue())
                .collect(Collectors.toList());
        weekRechargeMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.WEEK_RECHARGE_BAG.getValue())
                .collect(Collectors.toList());
        goldRechargeMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.GOLD_RECHARGE_BAG.getValue())
                .collect(Collectors.toList());
        activityMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.ACTIVITY_BAG.getValue())
                .collect(Collectors.toList());
        maouMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.MAOU.getValue())
                .collect(Collectors.toList());
        holidayExchangeMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.HOLIDAY_EXCHANGE.getValue())
                .collect(Collectors.toList());
        holidaySkyLanternWorkShopMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.SKY_LANTERN_WORKSHOP.getValue())
                .collect(Collectors.toList());
        activityRandomExchangeMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.RANDOM_EXCHANGE.getValue())
                .collect(Collectors.toList());
        sxdhMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.SXDH.getValue())
                .collect(Collectors.toList());
        dfdjMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.DFDJ.getValue())
                .collect(Collectors.toList());
        adventureMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.ADVENTURE.getValue())
                .collect(Collectors.toList());
        snatchTreasureMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.SNATCH_TREASURE.getValue())
                .collect(Collectors.toList());
        newerPackageMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.NEWER_PACKAGE.getValue())
                .collect(Collectors.toList());
        teHuiRechargeMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.TE_HUI_RECHARGE_BAG.getValue())
                .collect(Collectors.toList());
        roleTimeLimitMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.ROLE_TIME_LIMIT_BAG.getValue())
                .collect(Collectors.toList());
        faceMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.EMOTICON.getValue())
                .collect(Collectors.toList());
        horseRacingMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.HORSE_RACING.getValue())
                .collect(Collectors.toList());
        holidayLimitTimeMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.HOLIDAY_MALL_LIMIT_PACK.getValue())
                .collect(Collectors.toList());
        holidayLimitTimeMalls51 = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.HOLIDAY_MALL_LIMIT_PACK_51.getValue())
                .collect(Collectors.toList());
        warTokenMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.WAR_TOKEN.getValue())
                .collect(Collectors.toList());
        transmigrationMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.TRANSMIGRATION.getValue())
                .collect(Collectors.toList());
        discountChangeMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.DISCOUNT_CHANGER.getValue())
                .collect(Collectors.toList());
        combinedServiceDiscountChangeMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.SPECIAL_DISCOUNT.getValue())
                .collect(Collectors.toList());
        combinedServiceExchangeMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.COMBINED_SERVICE_EXCHANGE.getValue())
                .collect(Collectors.toList());
        flowerToGodMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.FLOWER_TO_GOD.getValue())
                .collect(Collectors.toList());
        holidayMaterialsStoreMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.MATERIAL_STORE.getValue())
                .collect(Collectors.toList());
        godsAltarMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.GODS_ALTAR.getValue())
                .collect(Collectors.toList());
        diamondRechargeMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.DIAMOND_GIFT_PACK.getValue())
                .collect(Collectors.toList());
        halloweenRestaurantMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.HALLOWEEN_RESTAURANT.getValue())
                .collect(Collectors.toList());
        halloweenRestaurantRedemptionMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.HALLOWEEN_HALLOWEEN_RESTAURANT_REDEEM.getValue())
                .collect(Collectors.toList());
        guessingStoreMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.WORLD_CUP_ACTIVITIE_GUESS_SHOP.getValue())
                .collect(Collectors.toList());
        gloryCoinStoreMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.GLORY_COIN_STORE.getValue())
                .collect(Collectors.toList());
        holidayGiftPackMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.HOLIDAY_GIFT_PACK_51.getValue())
                .collect(Collectors.toList());
        holidayChineseZodiacCollisionMalls = malls.stream()
                .filter(mall -> mall.getType() == MallEnum.CHINESE_ZODIAC_COLLISION.getValue())
                .collect(Collectors.toList());
        log.info("商城预准备好了集合");
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }

    /**
     * 折扣类型 -- 详情见-商城.yml
     */
    @Data
    public static class DiscountType implements Serializable {
        private static final long serialVersionUID = -2659889407463234625L;
        private Integer typeId;
        private Integer probability;
        private Integer discount;
        private String remark;
    }

    @Data
    public static class MallPartNum implements Serializable {
        private static final long serialVersionUID = -877359537414774544L;
        private Integer part;
        private Integer num;
    }

    @Data
    public static class DiscountGroup implements Serializable {
        private static final long serialVersionUID = -2659889407463234625L;
        private Integer discountGroupId;
        private List<Integer> types;
    }

    /**
     * 封神大陆神秘商店列表 -- 详情见-商城.yml
     */
    @Data
    public static class MysteriousMallProb implements Serializable {
        private static final long serialVersionUID = -2659889407463234625L;
        private Integer id;
        private Integer part;
        private Integer probability;
        private Boolean isDiscount;
        private Integer discountGroupId;
    }
}