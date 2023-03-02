package com.bbw.god.random.card;

import com.alibaba.fastjson.JSON;
import com.bbw.BaseTest;
import com.bbw.common.CloneUtil;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.game.config.FileConfigDao;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.random.config.RandomKeys;
import com.bbw.god.random.config.RandomStrategy;
import com.bbw.god.random.service.RandomCardService;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.random.service.RandomResult;
import com.bbw.god.server.monster.MonsterService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-06 22:31
 */
public class RandomStrategyTest extends BaseTest {
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private MonsterService monsterService;

    //	@Test
    public void test() {
        List<RandomStrategy> list = FileConfigDao.getByType(RandomStrategy.class);
        list.forEach(System.out::println);
        RandomStrategy rs = list.get(0);
        System.out.println(JSON.toJSONString(rs));
        String ori = JSON.toJSONString(rs);
        RandomStrategy copy = CloneUtil.clone(rs);
        //copy.setKey("ilove u");
        List<String> exclude = new ArrayList<>();
        exclude.add("添加排除测试");
        //		copy.getSelectors().get(0).getCondition().getExclude().addAll(exclude);
        //		copy.getSelectors().get(0).setKey("测试");
        //		copy.getSelectors().get(0).getCondition().getGetWay().add(111);
        System.out.println(JSON.toJSONString(copy));
        String after = JSON.toJSONString(rs);
        System.out.println(JSON.toJSONString(rs));
        System.out.println(ori.equals(after));
    }

    //    @Test
    public void testKeZ() {

        for (int i = 0; i <= 1000; i++) {
            RandomStrategy strategy = RandomCardService.getSetting(RandomKeys.KE_ZHAN_NORMAL);
            RandomParam randomParam = new RandomParam();
            randomParam.setExtraCardsToMap(userCardService.getUserCards(UID));
            RandomResult result = RandomCardService.getRandomList(strategy, randomParam);
            List<String> names = result.getCardList().stream().map(CfgCardEntity::getName).collect(Collectors.toList());
            long num = names.stream().distinct().count();
            if (num < 3) {
                System.out.println(names);
            }
        }

    }

    /*//    @Test
    public void testJxXZ() {
        UserCity userCity = gameUserService.getCfgItem(UID, 2733, UserCity.class);
        int processTimes = 1000;
        int match3Num = 0;
        int totalNum3 = 0;
        for (int i = 0; i <= processTimes; i++) {
            JuXZ juXZ = new JuXZ(userCity);
            List<CfgCardEntity> cards = juXZ.getCardsToSell(UID, userCity.gainCity(), 5);
            List<String> names = cards.stream().map(CfgCardEntity::getName).collect(Collectors.toList());
            boolean match3 = cards.stream().anyMatch(tmp -> tmp.getStar() >= 3);
            long num = names.stream().distinct().count();
            long num3 = cards.stream().filter(tmp -> tmp.getStar() >= 3).count();
            if (match3) {
                match3Num++;
                totalNum3 += num3;
                System.out.println(names);
            }
            if (num < 3) {
                System.out.println("-----------" + names);
            }
        }
        System.out.println("执行次数：" + processTimes + ",三星以上卡牌出现批次数：" + match3Num + ",三星以上卡牌出现次数：" + totalNum3);
    }*/

    @Test
    public void testFlx() {
        List<String> excludeCards = new ArrayList<>();
        excludeCards.add("姜子牙");
        for (int i = 0; i < 1000; i++) {
            RandomParam param = new RandomParam();
            param.set("$排除卡牌", excludeCards);
            RandomStrategy strategy = RandomCardService.getSetting("福临轩_前6周特殊日期");
            RandomResult randomResult = RandomCardService.getRandomList(strategy, param);
            System.out.println(randomResult.getFirstCard().get().getName());
        }

    }

    @Test
    public void testYouGuai() {
        GameUser gu = gameUserService.getGameUser(UID);
        for (int i = 0; i < 5000; i++) {
            CfgCardEntity card = monsterService.getCardAward(gu, YeGuaiEnum.YG_FRIEND, 55);
            if (card.getWay() == 4) {
                System.out.println(card);
            }
        }

        System.out.println("test you guai card completed");
    }


}
