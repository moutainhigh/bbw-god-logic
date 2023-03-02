package com.bbw.god.mall.cardshop;

import com.bbw.BaseTest;
import com.bbw.common.ListUtil;
import com.bbw.god.city.yeg.CfgYeGuai;
import com.bbw.god.city.yeg.YeGBoxService;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.YgTool;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.random.box.BoxService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CardShopLogicTest extends BaseTest {
    @Autowired
    private CardShopService cardShopService;
    @Autowired
    private TypePoolDrawService typePoolDrawService;
    @Autowired
    private WanwuPoolDrawService wanwuPoolDrawService;
    @Autowired
    private JuXPoolDrawService juXPoolDrawService;
    @Autowired
    private YeGBoxService yeGBoxService;
    @Autowired
    private BoxService boxService;
    @Autowired
    private UserCardService userCardService;

    @Test
    public void test() {
//		double progress = 11 * 1.00 / 600;
//		System.out.println(String.format("%.4f", progress));
        int tenDrawTimes = 1000;
        int star5Num = 0;
        int wishCard5 = 0;
        int normal5 = 0;
        int special5 = 0;
        UserCardPool ucPool = cardShopService.getCardPoolRecords(UID, 10);
        for (int i = 0; i < tenDrawTimes; i++) {
            List<CfgCardEntity> cards = typePoolDrawService.drawByStrategy(ucPool, 10, new RDCardDraw());
//            cards.stream().filter(tmp -> tmp.getStar() == 5 && tmp.getWay() > 0).forEach(System.out::println);
            long start5NumTmp = cards.stream().filter(tmp -> tmp.getStar() == 5).count();
            normal5 += cards.stream().filter(tmp -> tmp.getStar() == 5 && tmp.getWay() == 0).count();
            special5 += cards.stream().filter(tmp -> tmp.getStar() == 5 && tmp.getWay() > 0 && tmp.getId() != 328).count();
            if (start5NumTmp > 0) {
                System.out.println(i);
            }
            star5Num += start5NumTmp;
            wishCard5 += cards.stream().filter(tmp -> tmp.getStar() == 5 && tmp.getId() == ucPool.getWishCard().intValue()).count();
        }
        System.out.println("十连抽次数：" + tenDrawTimes + ";五星卡次数：" + star5Num + "；其中许愿卡次数：" + wishCard5 + "；普5次数：" + normal5 + "；限5次数：" + special5);
    }

    @Test
    public void test2() {
        int tenDrawTimes = 1000;
        int wish = 0;
        int normal5 = 0;
        int special5 = 0;
        int star5 = 0;
        int star4 = 0;
        int star3 = 0;
        int star2 = 0;
        int star1 = 0;
        UserCardPool ucPool = cardShopService.getCardPoolRecords(UID, 70);
        for (int i = 0; i < tenDrawTimes; i++) {
            List<CfgCardEntity> cards = juXPoolDrawService.drawByStrategy(ucPool, 10, new RDCardDraw());
            star5 += cards.stream().filter(tmp -> tmp.getStar() == 5).count();
            star4 += cards.stream().filter(tmp -> tmp.getStar() == 4).count();
            star3 += cards.stream().filter(tmp -> tmp.getStar() == 3).count();
            star2 += cards.stream().filter(tmp -> tmp.getStar() == 2).count();
            star1 += cards.stream().filter(tmp -> tmp.getStar() == 1).count();
            normal5 += cards.stream().filter(tmp -> tmp.getStar() == 5 && tmp.getWay() == 0).count();
            special5 += cards.stream().filter(tmp -> tmp.getStar() == 5 && tmp.getWay() > 0 && tmp.getId() != 328).count();
            wish += cards.stream().filter(tmp -> tmp.getStar() == 5 && tmp.getId() == ucPool.getWishCard().intValue()).count();
        }
        System.out.println("十连抽次数：" + tenDrawTimes + ";五星卡次数：" + star5 + ";四星卡次数：" + star4 + ";三星卡次数：" + star3 + ";二星卡次数：" + star2 + ";一星卡次数：" + star1 + "；其中许愿卡次数：" + wish + "；普5次数：" + normal5 + "；限5次数：" + special5);
    }

    /**
     * 卡池产出测试
     */
    @Test
    public void cardPoolTest() {
        // 需要测试是否产出的卡牌id集
        List<Integer> produceCardIds = Arrays.asList(467,364,468,561,264);
//        List<Integer> produceCardIds = Arrays.asList(155);
        // 不参与测试的卡池
        List<Integer> noTestTypes = Arrays.asList(10,20,30,40,50,70,80);
        drawCard(produceCardIds, noTestTypes, 1000);
    }
    /**
     * 抽卡测试
     * @param produceCardIds 需要测试产出的卡牌ids
     * @param noTestTypes 不参与测试卡池类型
     * @param tenDrawTimes 10连抽卡次数
     */
    private void drawCard(List<Integer> produceCardIds, List<Integer> noTestTypes, int tenDrawTimes) {
        // 遍历所有卡池类型
        for (CardPoolEnum type : CardPoolEnum.values()) {
            if (noTestTypes.contains(type.getValue())){
                continue;
            }
            UserCardPool ucPool = cardShopService.getCardPoolRecords(UID, type.getValue());
            for (int i = 0; i < tenDrawTimes; i++) {
                List<CfgCardEntity> cards = juXPoolDrawService.drawByStrategy(ucPool, 10, new RDCardDraw());
                List<CfgCardEntity> collect = cards.stream().filter(c -> produceCardIds.contains(c.getId())).collect(Collectors.toList());
                if (ListUtil.isNotEmpty(collect)){
                    // 出现测试项的卡牌 则跳过该卡池测试
                    collect.forEach(card -> System.out.println("【" + type.getName() + "】" + ",十连抽产出（测试项）卡牌：" + card.getName()));
                }
            }
        }
    }

    /**
     * 野怪宝箱产出测试
     */
    @Test
    public void wildMonsterTreasureChestTest() {
        // 需要测试是否产出的卡牌id集
//        List<Integer> produceCardIds = Arrays.asList(467,364,468,561,264);
        List<Integer> produceCardIds = Arrays.asList(157);
        // 不参与测试的野怪宝箱类型
        List<Integer> noTestTypes = Arrays.asList(15, 40, 50);
        openYgBox(produceCardIds, noTestTypes, 10000);
    }
    /**
     * 野怪开箱测试
     * @param produceCardIds 需要测试产出的卡牌ids
     * @param noTestYgTypes 不参与测试野怪类型
     * @param times 开箱次数
     */
    private void openYgBox(List<Integer> produceCardIds, List<Integer> noTestYgTypes, int times) {
        // 遍历所有野怪类型
        for (YeGuaiEnum type : YeGuaiEnum.values()) {
            if (noTestYgTypes.contains(type.getType())){
                continue;
            }
            for (CfgYeGuai.YeGBoxConfig yeGBox : YgTool.getYgConfig().getYeGBoxs()) {
                if(!yeGBox.getBoxKey().equals("普通野怪宝箱_玩家等级[30,39]")){
                    continue;
                }
                for (int i = 0; i < times; i++) {
                    List<Award> awards = new ArrayList<>();
                    // 获取奖励为卡牌
                    while (true) {
                        List<Award> collect = yeGBoxService.getAward(UID, yeGBox.getBoxKey()).stream().filter(award ->
                                award.getItem() == AwardEnum.KP.getValue()).collect(Collectors.toList());
                        if (ListUtil.isNotEmpty(collect)){
                            awards.addAll(collect);
                            break;
                        }
                    }
                    List<Award> collect = awards.stream().filter(a ->
                            a.getItem() == AwardEnum.KP.getValue() &&
                            produceCardIds.contains(a.getAwardId())
                    ).collect(Collectors.toList());
                    if (ListUtil.isNotEmpty(collect)){
                        // 出现测试项的卡牌 则跳过该野怪宝箱的测试
                        collect.forEach(award -> System.out.println("【" + type.getName() + "】" + ",开箱产出（测试项）卡牌：" + CardTool.getCardById(award.gainAwardId()).getName()));
                        break;
                    }
                }
            }
        }
    }

    /**
     * 客栈产出测试
     */
    @Test
    public void InnTest() {
        // 需要测试是否产出的卡牌id集
        List<Integer> produceCardIds = Arrays.asList(467,364,468,561,264);
        // 次数
        List<UserCard> userCards = userCardService.getUserCards(UID);
        List<UserCard> collect = userCards.stream().filter(c -> produceCardIds.contains(c.getBaseId())).collect(Collectors.toList());
        collect.forEach(card -> System.out.println("【" + "客栈" + "】" + ",产出（测试项）卡牌：" + card.getName()));
    }
}