package com.bbw.god.random.config;

import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 卡牌随机策略配置
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-06 21:05
 */
@Data
public class RandomStrategy implements CfgInterface, Serializable {
    private static final long serialVersionUID = 4544845373468474876L;
    //-------------------------
    private String key;//策略名称
    private String desc;//策略描述
    private String nextStrategyKey;//下一策略
    private int maxSize = 1;//最多返回多少条记录
    private List<Selector> selectors;//策略名称
    private List<ResultLimtRule> resultRules;//结果集过滤

    /**
     * 伪随机算法
     *
     * @return
     */
    public boolean isPRDRandom() {
        for (Selector selector : selectors) {
            if (selector.isPRDRandom()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是全概率
     */
    public double gainTotalProbability() {
        double p = 0;
        for (Selector selector : selectors) {
            p += selector.getProbability().gainValueDouble();
        }
        return p;
    }

    /**
     * 根据随机数挑选执行器
     *
     * @param randomNum
     * @return
     */
    public Optional<Selector> getSelector(double randomNum) {
        double p = 0;
        for (Selector selector : selectors) {
            p += selector.getProbability().gainValueDouble();
            if (p > randomNum) {
                return Optional.of(selector);
            }
        }
        return Optional.empty();
    }

    /**
     * 设置概率调整比例
     *
     * @param addtion
     */
    public void setAddtion(int addtion) {
        //遍历选择器
        for (Selector selector : selectors) {
            selector.getProbability().setAddition(addtion);
        }
    }

    /**
     * 获取拥有子策略的选择器
     *
     * @return
     */
    public List<Selector> getSubSelectors() {
        return selectors.stream().filter(tmp -> tmp.isUseSubStrategy()).collect(Collectors.toList());
    }


    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
