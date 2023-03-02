package com.bbw.god.random.service;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PRDTable;
import com.bbw.exception.CoderException;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.random.config.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-07 10:41
 */
@Service
public class RandomCardService {
    private static List<CfgCardEntity> allCards = null;
    private static AbstractStrategy chain = new ExcludeStrategy();

    static {
        IncludeStrategy include = new IncludeStrategy();
        StarStrategy start = new StarStrategy();
        TypeStrategy type = new TypeStrategy();
        GroupStrategy group = new GroupStrategy();// 目前没有这个条件
        GetWayStrategy way = new GetWayStrategy();
        PassStrategy pass = new PassStrategy();
        // 定义责任链
        chain.setNext(include).setNext(start).setNext(way).setNext(type).setNext(group).setNext(pass);
        // 初始化卡库,【总集】
        allCards = CardTool.getAllCards().stream().collect(Collectors.toList());
        // 添加万能灵石卡牌
        allCards.addAll(RandomKeys.getPowerStarCards());
    }

    public static RandomResult getRandomList(String strategyName, RandomParam param) {
        RandomStrategy strategy = getSetting(strategyName);
        return getRandomList(strategy, param, 0, 1);
    }

    @NonNull
    public static RandomResult getRandomList(final RandomStrategy strategy, RandomParam param) {
        return getRandomList(strategy, param, 0, 1);
    }

    /**
     * @param strategy
     * @param addtion  概率提高或下降的百分比。以100为基准。 78=78%。
     * @return
     */
    @NonNull
    public static RandomResult getRandomList(final RandomStrategy strategy, int addtion) {
        return getRandomList(strategy, null, addtion, 1);
    }

    @NonNull
    public static RandomResult getRandomList(final RandomStrategy strategy, RandomParam param, int addtion, int addFailTimes) {
        // 从原始策略复制一个作为执行策略
        RandomStrategy runStrategy = CloneUtil.clone(strategy);
        // 概率加成
        if (addtion != 0) {
            runStrategy.setAddtion(addtion);
        }
        setParams(param, runStrategy);

        RandomResult result = null;
        // 选出满足结果限制的结果。最多执行100次。
        for (int i = 0; i < 100; i++) {
            result = getRandomListOneTime(runStrategy, param);
            if (result.acceptAble(runStrategy)) {
                break;
            }
        }
        // 如果是伪随机，且需要保底，将此次执行策略的结果作为下次执行策略
        if (strategy.isPRDRandom()) {
            RandomStrategy nextStrategy = CloneUtil.clone(strategy);
            // 遍历选择器
            for (Selector selector : nextStrategy.getSelectors()) {
                Optional<Selector> matchSelector = result.getMatchSelectors().stream().filter(match -> match.getKey().equals(selector.getKey())).findAny();
                if (matchSelector.isPresent()) {
                    selector.getProbability().setLoopTimes(0);
                } else {
                    selector.getProbability().addFailTimes(addFailTimes);
                }
            }
            result.setNextTimeStrategy(nextStrategy);
        }
        return result;
    }

    private static void setParams(RandomParam param, RandomStrategy runStrategy) {
        // 参数设置
        if (null != param) {
            for (Selector selector : runStrategy.getSelectors()) {
                SelectorCondition cdtion = selector.getCondition();
                if (cdtion == null) {
                    continue;
                }
                // 属性参数
                if (cdtion.needTypeParam()) {
                    String paramKey = cdtion.getType();
                    if (!param.containsKey(paramKey)) {
                        throw CoderException.fatal("[" + runStrategy.getKey() + "]策略缺少[" + paramKey + "]参数！");
                    }
                    cdtion.setType(param.get(paramKey));
                }
                // 卡牌集合参数
                if (cdtion.needIncludeParam()) {
                    List<String> includeCards = getParamCards(runStrategy, param, cdtion.getInclude());
                    cdtion.setInclude(includeCards);
//					String paramKey = cdtion.getInclude().get(0);
//					if (!param.containsKey(paramKey)) {
//						throw CoderException.fatal("[" + runStrategy.getKey() + "]策略缺少[" + paramKey + "]参数！");
//					}
//					cdtion.setInclude(param.getCards(paramKey));
                }
                if (cdtion.needExcludeParam()) {
                    List<String> excludeCards = getParamCards(runStrategy, param, cdtion.getExclude());
                    cdtion.setExclude(excludeCards);
//					String paramKey = cdtion.getExclude().get(0);
//					if (!param.containsKey(paramKey)) {
//						throw CoderException.fatal("[" + runStrategy.getKey() + "]策略缺少[" + paramKey + "]参数！");
//					}
//					cdtion.setExclude(param.getCards(paramKey));
                }
                // 设置概率相关参数
                SelectorProbability probability = selector.getProbability();
                // 概率
                if (probability.needValueParam()) {
                    String paramKey = probability.getValue();
                    if (!param.containsKey(paramKey)) {
                        throw CoderException.fatal("[" + runStrategy.getKey() + "]策略缺少[" + paramKey + "]参数！");
                    }
                    probability.setValue(param.get(paramKey));
                }
                // 保底
                if (probability.needMaxTimesParam()) {
                    String paramKey = probability.getMaxTimes();
                    if (!param.containsKey(paramKey)) {
                        throw CoderException.fatal("[" + runStrategy.getKey() + "]策略缺少[" + paramKey + "]参数！");
                    }
                    probability.setMaxTimes(param.get(paramKey));
                }
            }
        }
    }

    private static RandomResult getRandomListOneTime(final RandomStrategy strategy, RandomParam randomParam) {
        RandomResult result = new RandomResult();
        // 逐个执行器分别执行。执行多个选择器
        List<CfgCardEntity> resultCards = result.getCardList();
        for (int i = 0; i < strategy.getMaxSize(); i++) {
            // 遍历选择器
            for (Selector selector : strategy.getSelectors()) {
                // 达到需要到最大值，选卡结束
                if (resultCards.size() >= strategy.getMaxSize()) {
                    break;
                }
                // 测试
                // List<CfgCardEntity> matchCards1 = getSelectCards(selector);
                // System.out.println(selector);
                // System.out.println(matchCards1);
                //
                boolean match = match(selector.getProbability());
                if (!match) {
                    continue;
                }
                // 命中，则选牌
                result.addMatchSelectors(selector);
                //如果使用子策略则获取子策略的卡牌
                if (selector.isUseSubStrategy()) {
                    final RandomStrategy subStrategy = getSetting(selector.getSubStrategy());
                    setParams(randomParam, subStrategy);
                    for (int j = 0; j < selector.getRequestSize(); j++) {
                        CfgCardEntity cfgCardEntity;
                        do {
                            cfgCardEntity = getRandomListOneTime(subStrategy, randomParam).getFirstCard().get();
                        } while (cfgCardEntity == null || resultCards.contains(cfgCardEntity));
                        resultCards.add(cfgCardEntity);
                    }
                    continue;
                }
                // 设置选牌范围
                List<CfgCardEntity> matchCards = getSelectCards(selector);
                // 洗牌
                if (!matchCards.isEmpty() && matchCards.size() > 1) {
                    shuffle(matchCards);
                }
                int selectedNum = 0;
                // 选牌
                for (int index = 0; index < matchCards.size(); index++) {
                    // 选择器配置的数量限制
                    if (selectedNum >= selector.getRequestSize()) {
                        break;
                    }
                    // 策略配置的数量限制
                    if (resultCards.size() >= strategy.getMaxSize()) {
                        break;
                    }
                    //弃重
                    if (resultCards.contains(matchCards.get(index))) {
                        continue;
                    }
                    resultCards.add(matchCards.get(index));
                    selectedNum++;
                }
            }
        }
//        if (result.getFirstCard().isPresent() && result.getFirstCard().get().getStar() == 5) {
//            System.out.println(strategy.getKey() + ":" + result.getFirstCard().get().getName());
//        }
        return result;
    }

    /**
     * 获得策略可能获得的所有卡牌
     *
     * @param strategyKey
     * @return
     */
    public static List<CfgCardEntity> getStrategyCards(String strategyKey, RandomParam param) {
        RandomStrategy strategy = RandomCardService.getSetting(strategyKey);
        if (strategy == null) {
            return new ArrayList<>();
        }
        setParams(param, strategy);
        List<CfgCardEntity> cards = new ArrayList<>();
        for (Selector selector : strategy.getSelectors()) {
            if (selector.isUseSubStrategy()) {
                cards.addAll(getStrategyCards(selector.getSubStrategy(), param));
                continue;
            }
            List<CfgCardEntity> selectCards = getSelectCards(selector);
            if (ListUtil.isNotEmpty(selectCards)) {
                cards.addAll(selectCards);
            }
        }
        return cards;
    }

    /**
     * <pre>
     * 获取指定选择器的[候选集]
     * <B><font color=red>这个方法仅用于程序调试输出信息使用。</font></B>
     * </pre>
     */
    public static List<CfgCardEntity> getSelectCards(Selector selector) {
        // 候选卡牌集合
        ArrayList<CfgCardEntity> cards = new ArrayList<>();
        for (CfgCardEntity card : allCards) {
            if (chain.valid(selector.getCondition(), card)) {
                cards.add(card);
            }
        }
        return cards;
    }

    private static boolean match(SelectorProbability prob) {
        // 达到了最大次数
        if (prob.overMaxtimes()) {
            return true;
        }
        // 指定了100%的概率
        if (prob.gainValueDouble() >= 100) {
            return true;
        }
        //概率为0,不匹配
        if (Double.valueOf(prob.getValue()) <= 0.0) {
            return false;
        }
        double p = prob.gainValueDouble();
        // 概率影响。全概率的不受影响
        if (p < 100 && prob.getAddition() != 0) {
            p = p * (1 + prob.getAddition() / 100.0);
        }
        // 真随机
        if (prob.isMachineRandom()) {
            return machineRandomMatch(p);
        }
        // 伪随机
        double c = PRDTable.getCFromP(p);
        double thisTimeP = c * (1 + prob.getLoopTimes());
        return machineRandomMatch(thisTimeP);
    }

    /**
     * 真随机命中
     */
    private static boolean machineRandomMatch(double value) {
        double num = ThreadLocalRandom.current().nextDouble(0, 100);
        return num < value;
    }

    /**
     * <pre>
     * 根据策略名称获取策略配置
     * <B><font color=red>采用伪随机算法的，只能调用此方法一次。然后从结果中保存策略，下一次获取策略，要获取已经保存的策略。</font></B>
     * </pre>
     *
     * @param strategyKey
     * @return
     */
    public static RandomStrategy getSetting(String strategyKey) {
        RandomStrategy single = Cfg.I.get(strategyKey, RandomStrategy.class);
        if (null == single) {
            throw CoderException.high("获取不到[" + strategyKey + "]的卡牌策略！");
        }
        // 克隆对象，避免修改原始配置
        RandomStrategy copy = CloneUtil.clone(single);
        for (Selector selector : copy.getSelectors()) {
            if (null != selector.getProbability()) {
                selector.getProbability().setLoopTimes(0);
                selector.getProbability().setAddition(0);
            }
        }
        return copy;
    }

    /**
     * 洗牌
     *
     * @param list
     */
    private static void shuffle(List<CfgCardEntity> list) {
        int times = 2 + list.size() / 10;
        for (int i = 0; i < times; i++) {
            Collections.shuffle(list);
        }
    }

    private static List<String> getParamCards(RandomStrategy runStrategy, RandomParam param, List<String> paramKeys) {
        List<String> cards = new ArrayList<>();
        for (String paramKey : paramKeys) {
            if (!paramKey.startsWith(RandomKeys.PARAM_PREFIX)) {
                cards.add(paramKey);
            } else {
                if (!param.containsKey(paramKey)) {
                    throw CoderException.fatal("[" + runStrategy.getKey() + "]策略缺少[" + paramKey + "]参数！");
                }
                cards.addAll(param.getCards(paramKey));
            }
        }
        return cards;
    }
}
