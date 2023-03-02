package com.bbw.god.random.config;

import com.bbw.common.StrUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-06 21:14
 */
@Data
public class Selector implements Serializable {
    private static final long serialVersionUID = 297019076577195962L;
    //-------------------------
    private String key;//使用不重复的数字标识
    private int requestSize = 1;//要求的数量
    private String subStrategy;//子策略
    private SelectorCondition condition;//卡牌选择条件
    private SelectorProbability probability;//概率

    public boolean isUseSubStrategy() {
        return !StrUtil.isBlank(subStrategy);
    }

    /**
     * 伪随机算法
     *
     * @return
     */
    public boolean isPRDRandom() {
        return probability.isPRDRandom();
    }

}
