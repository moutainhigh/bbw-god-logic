package com.bbw.god.gameuser.card;

import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.random.config.RandomStrategy;
import com.bbw.god.random.service.RandomCardService;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.random.service.RandomResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-05 15:43
 */
@Service
public class UserCardRandomService {
    @Autowired
    private GameUserService gameUserService;

    public Optional<CfgCardEntity> getRandomCardWithFailTimes(Long uid, String strategyKey, RandomParam param, int addFailTimes) {
        RandomResult result = getRandomResult(uid, strategyKey, param, 0, addFailTimes);
        return result.getFirstCard();
    }

    public Optional<CfgCardEntity> getRandomCard(Long uid, String strategyKey, RandomParam param) {
        RandomResult result = getRandomResult(uid, strategyKey, param, 0, 1);
        return result.getFirstCard();
    }

    public Optional<CfgCardEntity> getRandomCard(Long uid, String strategyKey, RandomParam param, int addtion) {
        RandomResult result = getRandomResult(uid, strategyKey, param, addtion, 1);
        return result.getFirstCard();
    }

    @NonNull
    public List<CfgCardEntity> getRandomList(Long uid, String strategyKey, RandomParam param) {
        RandomResult result = getRandomResult(uid, strategyKey, param, 0, 1);
        return result.getCardList();
    }

    private RandomResult getRandomResult(Long uid, String strategyKey, RandomParam param, int addtion, int addFailTimes) {
        // ---获取概率策略
        RandomStrategy strategy = RandomCardService.getSetting(strategyKey);
        // ---真随机
        if (!strategy.isPRDRandom()) {
            RandomResult result = RandomCardService.getRandomList(strategy, param, addtion, addFailTimes);
            return result;
        }
        // ---伪随机策略
        Optional<UserCardRandom> uRnd = getUserStrategy(uid, strategyKey);
        UserCardRandom ucr = null;
        if (uRnd.isPresent()) {
            ucr = uRnd.get();
        } else {
            ucr = UserCardRandom.instance(uid, strategyKey);
            gameUserService.addItem(uid, ucr);
        }
        // 伪随机设置保底
        UserCardRandom finalUcr = ucr;
        strategy.getSelectors().forEach(tmp -> {
            Integer loopTimes = finalUcr.gainLoopTimes(tmp.getKey());
            if (loopTimes != null) {
                tmp.getProbability().setLoopTimes(loopTimes.intValue());
            }
        });
        // -- 选牌
        RandomResult result = RandomCardService.getRandomList(strategy, param, addtion, addFailTimes);
        if (result.hasNextTimeStrategy()) {// 伪随机策略
            RandomStrategy nextStrategy = result.getNextTimeStrategy();
            ucr.updateLoopTimes(nextStrategy);
            gameUserService.updateItem(ucr);
        }
        return result;
    }

    public Optional<UserCardRandom> getUserStrategy(Long uid, String strategyKey) {
        List<UserCardRandom> list = gameUserService.getMultiItems(uid, UserCardRandom.class);
        Optional<UserCardRandom> rndSetting = list.stream().filter(ucr -> ucr.getStrategyKey().equals(strategyKey)).findFirst();
        return rndSetting;
    }
}
