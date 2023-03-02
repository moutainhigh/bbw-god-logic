package com.bbw.common;

import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PowerRandomTest {

    @Test
    public void hitProbability() {
        System.out.println(PowerRandom.getRandomBySeed(0));

        for (int j = 0; j < 10000; j++) {
            int hitNum = 0;
            for (int i = 0; i < 100; i++) {
                boolean isHit = PowerRandom.hitProbability(90);
                if (!isHit) {
                    hitNum++;
                }
            }
            if (hitNum <= 0) {
                System.out.println(j + "hit the 10%,times:" + hitNum);
            }

        }

    }

    public static boolean hitProbability(double p) {
        double probability = PRDTable.getCFromP(p);
        int value = ThreadLocalRandom.current().nextInt(100);
        return value < probability;
    }


    /**
     * 从集合中获取一个随机值
     *
     * @return
     */
    @Test
    public void getRandomFromList() {
        int randomTimes = 10000;
        List<CfgCardEntity> allCards = CardTool.getAllCards();
        Map<String, Integer> result = new HashMap<>();
        for (int i = 0; i < randomTimes; i++) {
            CfgCardEntity randomCard = PowerRandom.getRandomFromList(allCards);
            result.put(randomCard.getName(), result.getOrDefault(randomCard.getName(), 0) + 1);
        }
        System.out.println("getRandomFromList(values)执行" + randomTimes + "次的结果：");
        result.entrySet().forEach(tmp -> System.out.println(tmp.getKey() + ":" + tmp.getValue()));
    }

    /**
     * 从集合中获取一个随机值
     *
     * @return
     */
    @Test
    public void getRandomFromListWithExclude() {
        int randomTimes = 100;
        List<CfgCardEntity> allCards = CardTool.getAllCards();
        List<CfgCardEntity> exclude = PowerRandom.getRandomsFromList(allCards, 100);
        Map<String, Integer> result = new HashMap<>();
        for (int i = 0; i < randomTimes; i++) {
            CfgCardEntity randomCard = PowerRandom.getRandomFromList(allCards, exclude);
            result.put(randomCard.getName(), result.getOrDefault(randomCard.getName(), 0) + 1);
        }
        System.out.println("getRandomFromList(values,excludes)排除的卡牌：" + exclude.stream().map(CfgCardEntity::getName).collect(Collectors.toList()));
        System.out.println("getRandomFromList(values,excludes)执行" + randomTimes + "次的结果：");
        result.entrySet().forEach(tmp -> System.out.println(tmp.getKey() + ":" + tmp.getValue()));
    }

    /**
     * 从集合中获取指定个数的不重复的随机值
     * <br><font color="red">注意：数量不能小于0，且确保集合有足够数量的项 </font>
     *
     * @return
     */
    @Test
    public void getRandomsFromList() {
        int randomTimes = 100;
        List<CfgCardEntity> allCards = CardTool.getAllCards();
        Map<String, Integer> result = new HashMap<>();
        for (int i = 0; i < randomTimes; i++) {
            List<CfgCardEntity> randomCards = PowerRandom.getRandomsFromList(allCards, 3);
            List<String> cardNames = randomCards.stream().map(CfgCardEntity::getName).collect(Collectors.toList());
            result.put(cardNames.toString(), result.getOrDefault(cardNames.toString(), 0) + 1);
        }
        System.out.println("getRandomsFromList(values,num)执行" + randomTimes + "次的结果：");
        result.entrySet().forEach(tmp -> System.out.println(tmp.getKey() + ":" + tmp.getValue()));
    }

    /**
     * 从集合中获取多个不重复的随机值
     *
     * @return
     */
    @Test
    public void getRandomsFromList2() {
        int randomTimes = 100;
        List<CfgCardEntity> allCards = CardTool.getAllCards();
        Map<String, Integer> result = new HashMap<>();
        for (int i = 0; i < randomTimes; i++) {
            List<CfgCardEntity> randomCards = PowerRandom.getRandomsFromList(3, allCards);
            List<String> cardNames = randomCards.stream().map(CfgCardEntity::getName).collect(Collectors.toList());
            result.put(cardNames.toString(), result.getOrDefault(cardNames.toString(), 0) + 1);
        }
        System.out.println("getRandomsFromList(max,values)执行" + randomTimes + "次的结果：");
        result.entrySet().forEach(tmp -> System.out.println(tmp.getKey() + ":" + tmp.getValue()));
    }


}