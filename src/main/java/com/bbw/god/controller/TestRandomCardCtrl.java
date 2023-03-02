package com.bbw.god.controller;

import com.bbw.common.StrUtil;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardRandomService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.random.config.RandomStrategy;
import com.bbw.god.random.config.Selector;
import com.bbw.god.random.service.RandomCardService;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.random.service.RandomResult;
import com.bbw.validator.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-09-12 15:15:04
 */
@Slf4j
@RestController
@RequestMapping(value = "/coder")
public class TestRandomCardCtrl extends Assert {
    @Autowired
    private UserCardRandomService userCardRandomService;
    @Autowired
    private GameUserService userService;
    @Autowired
    private UserCardService userCardService;

    private static RandomResult result;

    @RequestMapping("Test!randomUserCard")
    public CfgCardEntity randomUserCard(Long uid, String strategyKey, int addtion, int type, @RequestParam(required = false) String roleCards, @RequestParam(required = false) String cityCards, int loop) {
        RandomParam param = getParams(type, roleCards, cityCards);
        GameUser user = userService.getGameUser(uid);
        param.setRoleType(user.getRoleInfo().getCountry());
        List<UserCard> userCards = userCardService.getUserCards(uid);
        param.setRoleCards(userCards);
        Optional<CfgCardEntity> card = userCardRandomService.getRandomCard(uid, strategyKey, param, addtion);
        if (card.isPresent()) {
            return card.get();
        }
        return null;
    }

    @RequestMapping("Test!randomAllUserCard")
    public CfgCardEntity randomAllUserCard(Long uid, int addtion) {
        RandomParam param = new RandomParam();
        GameUser user = userService.getGameUser(uid);
        param.setRoleType(user.getRoleInfo().getCountry());
        List<UserCard> userCards = userCardService.getUserCards(uid);
        param.setRoleCards(userCards);

        List<CfgCityEntity> cities = Cfg.I.get(CfgCityEntity.class);
        for (CfgCityEntity city : cities) {
            if (StrUtil.isNull(city.getDropCards())) {
                continue;
            }
            String[] ids = city.getDropCards().split(",");
            ArrayList<Integer> ccards = new ArrayList<Integer>();
            for (String string : ids) {
                ccards.add(Integer.parseInt(string));
            }
            param.set("$排除卡牌", Arrays.asList("101", "102", "103"));
            param.setCityCards(ccards);
            List<RandomStrategy> sss = Cfg.I.get(RandomStrategy.class);
            for (RandomStrategy strategy : sss) {
                //System.out.println("----" + strategy.getKey());
                result = RandomCardService.getRandomList(strategy, param, addtion, 1);
                if (result.getFirstCard().isPresent() && "5级城池_攻城振兴".equals(strategy.getKey())) {
                    log.error("[][][]------------" + city.getName() + "-----------------");
                }
                //System.out.println(result);
            }
        }
        return null;
    }

    @RequestMapping("Test!randomCard")
    public RandomResult randomCard(String strategyKey, int addtion, int type, @RequestParam(required = false) String roleCards, @RequestParam(required = false) String cityCards, int loop) {
        RandomParam param = getParams(type, roleCards, cityCards);
        RandomStrategy setting = null;
        if (1 == loop && null != result && result.hasNextTimeStrategy()) {
            setting = result.getNextTimeStrategy();
        } else {
            setting = RandomCardService.getSetting(strategyKey);
        }
        result = RandomCardService.getRandomList(setting, param, addtion, 1);
        System.out.println("--------------- [" + setting.getKey() + "]选择范围内的卡牌-----------------");
        for (Selector selector : setting.getSelectors()) {
            List<CfgCardEntity> cards = RandomCardService.getSelectCards(selector);
            cards.forEach(System.out::println);
        }
        //System.out.println(result);
        return result;
    }

    private RandomParam getParams(int type, String roleCards, String cityCards) {
        RandomParam param = new RandomParam();
        ArrayList<Integer> rcards = new ArrayList<Integer>();
        param.setRoleType(type);
        if (StrUtil.isNotNull(roleCards)) {
            String[] ids = roleCards.split(",");
            for (String string : ids) {
                rcards.add(Integer.parseInt(string));
            }
        }
        param.set("$排除卡牌", new ArrayList<>());
        ArrayList<Integer> ccards = new ArrayList<Integer>();
        param.setRoleType(10);
        if (StrUtil.isNotNull(cityCards)) {
            String[] ids = cityCards.split(",");
            for (String string : ids) {
                ccards.add(Integer.parseInt(string));
            }
        }
        param.setRoleCardsByIds(rcards);
        param.setCityCards(ccards);
        return param;
    }

    @RequestMapping("Test!randomAll")
    public RandomResult randomAll() {
        RandomParam param = new RandomParam();
        param.setRoleType(10);
        param.setRoleCardsByIds(Arrays.asList(101, 102, 103));
        param.setCityCards(Arrays.asList(221, 220, 217, 213, 207));
        List<RandomStrategy> sss = Cfg.I.get(RandomStrategy.class);
        RandomResult result = null;
        for (RandomStrategy strategy : sss) {
            System.out.println("----" + strategy.getKey());
            result = RandomCardService.getRandomList(strategy, param, 50, 1);
            System.out.println(result);
        }
        return result;
    }

}
