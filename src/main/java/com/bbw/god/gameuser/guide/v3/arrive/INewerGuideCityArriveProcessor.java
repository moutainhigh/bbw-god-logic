package com.bbw.god.gameuser.guide.v3.arrive;

import com.bbw.god.city.ICityArriveProcessor;

/**
 * @author suchaobin
 * @description 新手引导位置到达处理
 * @date 2020/12/11 14:43
 **/
public interface INewerGuideCityArriveProcessor extends ICityArriveProcessor {

    /**
     * 是否是新手引导的处理器
     *
     * @return
     */
    @Override
    default boolean isNewerGuideProcessor() {
        return true;
    }
}
