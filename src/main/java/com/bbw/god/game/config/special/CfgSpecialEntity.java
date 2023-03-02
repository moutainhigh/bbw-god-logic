package com.bbw.god.game.config.special;

import com.baomidou.mybatisplus.annotations.TableId;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CfgSpecialEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    @TableId
    private Integer id; //
    private String name; //
    private Integer type = 0;// 特产类型
    /** 阶级 */
    private Integer hierarchy = 0;
    private Integer price; // 买进的价格
    private Integer country; //
    private String sellingCities; //
    private List<Integer> materialIds; // 合成特产所需的材料id集合
    private List<Integer> highSellCountries;// 可以高价出售的区域集合
    private boolean excludeBag = false;//默认占用背包格子
    private boolean excludeRandomEvent = false;//默认不剔除随机事件


    @Override
    public int getSortId() {
        return this.getId();
    }

    /**
     * 是否普通特产
     *
     * @return
     */
    public boolean isNormalSpecial() {
        return type.intValue() == SpecialTypeEnum.NORMAL.getValue();
    }

    /**
     * 是否高级特产
     *
     * @return
     */
    public boolean isHighSpecial() {
        return type.intValue() == SpecialTypeEnum.HIGH.getValue();
    }

    /**
     * 是否顶级特产
     *
     * @return
     */
    public boolean isTopSpecial() {
        return type.intValue() == SpecialTypeEnum.TOP.getValue();
    }

    /**
     * 是否合成特产
     *
     * @return
     */
    public boolean isSyntheticSpecialty() {
        return type.intValue() == SpecialTypeEnum.SYNTHETIC.getValue();
    }

    /**
     * 是否是升级特产
     *
     * @return
     */
    public boolean isUpdateSpecial() {
        return hierarchy > 1;
    }

    /**
     * 获得基本价格比率
     *
     * @param cityCountry
     * @return
     */
    public int getPriceRate(int cityCountry) {
        int specialCountry = getCountry();
        int specialId = getId();
        int areaRelation = -1;
        if (specialCountry == 50)// 产区中间区域
        {
            areaRelation = 0;
        } else if ((specialCountry == 10 && (cityCountry == 30 || cityCountry == 40)) || (specialCountry == 20 && (cityCountry == 30 || cityCountry == 40)) || (specialCountry == 30 && (cityCountry == 10 || cityCountry == 20)) || (specialCountry == 40 && (cityCountry == 10 || cityCountry == 20))) // 与产区相邻区域，且产区非中部
        {
            areaRelation = 2;
        } else if ((specialCountry == 10 && (cityCountry == 20)) || (specialCountry == 20 && (cityCountry == 10)) || (specialCountry == 30 && (cityCountry == 40)) || (specialCountry == 40 && (cityCountry == 30))) // 与产区相对区域，且产区非中部
        {
            areaRelation = 3;
        } else if (cityCountry == 50) // 中间区域，且产区非中部
        {
            areaRelation = 1;
        }
        // 确定价格比例
        if (areaRelation >= 0) {
            if (areaRelation == 0) {
                if (specialId <= 10) {
                    return 150;
                } else if (specialId <= 25) {
                    return 130;
                } else {
                    return 120;
                }
            } else if (specialId <= 10) {// 普通（1.4，1.6，2）
                if (areaRelation == 1) {
                    return 150;
                } else if (areaRelation == 2) {
                    return 175;
                } else {
                    return 225;
                }
            } else if (specialId <= 25) {// 高级 （1.25，1.4，1.7）
                if (areaRelation == 1) {
                    return 130;
                } else if (areaRelation == 2) {
                    return 146;
                } else {
                    return 178;
                }
            } else {// 顶级（1.2，1.3，1.50）
                if (areaRelation == 1) {
                    return 120;
                } else if (areaRelation == 2) {
                    return 132;
                } else {
                    return 156;
                }
            }
        }
        return 100;
    }

    /**
     * 获得特产比率上限
     *
     * @return
     */
    public int getMaxPriceRate() {
        SpecialTypeEnum specialType = SpecialTypeEnum.fromValue(type);
        switch (specialType) {
            case NORMAL:
                return 225;
            case HIGH:
                return 178;
            default:
                return 156;
        }
    }

    /**
     * 获得特产的最低价格
     *
     * @param country
     * @return
     */
    public int getMinPrice(int country) {
        int priceRate = getPriceRate(country);// 售价比率
        return getPrice() * (priceRate - 20) / 100;
    }

    /**
     * 获得特产的最高价格
     *
     * @param country
     * @return
     */
    public int getMaxPrice(int country) {
        int priceRate = getPriceRate(country);// 售价比率
        return getPrice() * priceRate / 100;
    }

    /**
     * 获得特产价格降低量
     *
     * @param sellCount
     * @param isSoldCity
     * @return
     */
    public int getDownPrice(int sellCount, boolean isSoldCity) {
        int downPrice = getPrice() * 5 * sellCount / 1000;// 其他城市降0.005
        if (isSoldCity) {// 卖城降0.01
            downPrice = getPrice() * sellCount / 100;
        }
        return downPrice;
    }

    /**
     * 获得特产价格上升量
     *
     * @return
     */
    public int getUpPrice() {
        return getPrice() / 100;
    }

    /**
     * 获得特产买入价格（出售方:游戏）
     *
     * @param discount
     * @return
     */
    public int getBuyPrice(int discount) {
        double price = getPrice() * (discount / 100.0);
        return (int) price;
    }

}
