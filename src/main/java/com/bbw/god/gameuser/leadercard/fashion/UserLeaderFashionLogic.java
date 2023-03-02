package com.bbw.god.gameuser.leadercard.fashion;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 装备业务逻辑
 *
 * @author suhq
 * @date 2021-03-26 17:25
 **/
@Service
public class UserLeaderFashionLogic {
    @Autowired
    private UserLeaderFashionService userLeaderFashionService;
    @Autowired
    private LeaderCardService leaderCardService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 获取时装列表
     *
     * @param uid
     * @return
     */
    public RDFashionList getFashions(long uid) {
        RDFashionList rd = new RDFashionList();
        UserLeaderCard userLeaderCard = leaderCardService.getUserLeaderCard(uid);
        rd.setUsingFashion(userLeaderCard.getFashion());
        List<UserLeaderFashion> leaderFashions = userLeaderFashionService.getFashions(uid);
        rd.addFashions(leaderFashions);
        return rd;
    }


    /**
     * 穿戴时装
     *
     * @param uid
     * @param fashionId
     */
    public RDCommon take(long uid, int fashionId) {
        if (TreasureEnum.FASHION_FaSFS.getValue() != fashionId) {
            //非默认皮肤
            UserLeaderFashion leaderFashion = userLeaderFashionService.getFashion(uid, fashionId);
            if (null == leaderFashion) {
                throw ExceptionForClientTip.fromi18nKey("leader.fashion.not.active");
            }
        }
        UserLeaderCard userLeaderCard = leaderCardService.getUserLeaderCard(uid);
        userLeaderCard.setFashion(fashionId);
        gameUserService.updateItem(userLeaderCard);
        RDCommon rd = new RDCommon();
        return rd;
    }

    /**
     * 时装升级
     *
     * @param uid
     * @param fashionId
     * @return
     */
    public RDCommon update(long uid, int fashionId) {
        if (TreasureEnum.FASHION_FaSFS.getValue() == fashionId) {
            throw ExceptionForClientTip.fromi18nKey("leader.fashion.not.update");
        }
        //检查是否激活
        UserLeaderFashion leaderFashion = userLeaderFashionService.getFashion(uid, fashionId);
        if (null == leaderFashion) {
            throw ExceptionForClientTip.fromi18nKey("leader.fashion.not.active");
        }
        //检查等级
        int maxLevel = 50;
        if (leaderFashion.getLevel() >= maxLevel) {
            throw ExceptionForClientTip.fromi18nKey("leader.fashion.level.full");
        }
        RDCommon rd = new RDCommon();
        TreasureChecker.checkIsEnough(TreasureEnum.FASHION_JYD.getValue(), 1, uid);
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.FASHION_JYD.getValue(), 1, WayEnum.LEADER_FASHION_UPDATE, rd);
        leaderFashion.addLevel(1);
        gameUserService.updateItem(leaderFashion);
        return rd;
    }
}
