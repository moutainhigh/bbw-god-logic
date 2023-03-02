package com.bbw.god.city.chengc;

import lombok.Data;

/**
 * 特产卖出价格详细参数
 * @author：lwb
 * @date: 2020/11/26 14:46
 * @version: 1.0
 */
@Data
public class SpecialSellPriceParam {
    private Integer id;
    private Integer weekCopper=0;//可计入富豪榜的铜钱数
    private Integer earnCopper=0;//常规铜钱（不计入富豪榜）
    private Integer cszCopper = 0;// 财神珠铜钱加量
    private Integer activityCopper = 0;// 活动铜钱加量

    public static SpecialSellPriceParam instance(int id,int weekCopper,int earnCopper,int cszCopper,int activityCopper){
        SpecialSellPriceParam priceParam=new SpecialSellPriceParam();
        priceParam.setId(id);
        priceParam.setEarnCopper(earnCopper);
        priceParam.setWeekCopper(weekCopper);
        priceParam.setCszCopper(cszCopper);
        priceParam.setActivityCopper(activityCopper);
        return priceParam;
    }

    public int getRealSellPrice(){
        return earnCopper+cszCopper+activityCopper;
    }
}
