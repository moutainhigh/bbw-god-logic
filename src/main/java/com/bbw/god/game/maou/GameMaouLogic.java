package com.bbw.god.game.maou;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.game.maou.ctrl.*;
import com.bbw.god.game.maou.processor.GameMaouProcessorFactory;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 跨服魔王业务处理类
 *
 * @author: suhq
 * @date: 2022/1/7 2:20 下午
 */
@Service
public class GameMaouLogic {
    @Autowired
    private GameMaouProcessorFactory gameMaouProcessorFactory;
    @Autowired
    private GameMaouActivityService gameMaouActivityService;

    /**
     * 获取魔王信息
     *
     * @param uid
     * @return
     */
    public RDGameMaouInfo getMaou(long uid) {
        //获得并检查活动是否进行中
        IActivity activity = gameMaouActivityService.getAndCheckMauActivity(uid);
        return gameMaouProcessorFactory.getMaouProcessor(activity).getMaou(uid, activity);
    }

    /**
     * 获取血量信息
     *
     * @param
     * @return
     */
    public RDGameMaouBloodInfo getBloodInfo(long uid) {
        IActivity activity = gameMaouActivityService.getAndCheckMauActivity(uid);
        return gameMaouProcessorFactory.getMaouProcessor(activity).getBloodInfo(activity);
    }

    /**
     * 魔王编组
     *
     * @param uid
     * @param maouCards
     * @return
     */
    public RDSuccess setMaouCards(long uid, String maouCards) {
        if (maouCards.contains("undefined")) {
            throw ExceptionForClientTip.fromi18nKey("maou.card.unvalid");
        }
        IActivity activity = gameMaouActivityService.getAndCheckMauActivity(uid);
        return gameMaouProcessorFactory.getMaouProcessor(activity).setMaouCards(uid, maouCards, activity);
    }

    /**
     * 领取目标奖励
     *
     * @param uid
     * @return
     */
    public RDGameMaouAward getTargetAward(long uid) {
        IActivity activity = gameMaouActivityService.getAndCheckMauActivity(uid);
        return gameMaouProcessorFactory.getMaouProcessor(activity).getTargetAward(uid, activity);
    }

    /**
     * 攻打魔王
     *
     * @param uid
     * @return
     */
    public RDGameMaouAttack attack(long uid, CPGameMaouAttack attackParam) {
        //检查活动是否进行中
        IActivity activity = gameMaouActivityService.getAndCheckMauActivity(uid);
        return gameMaouProcessorFactory.getMaouProcessor(activity).attack(uid, attackParam, activity);
    }
}
