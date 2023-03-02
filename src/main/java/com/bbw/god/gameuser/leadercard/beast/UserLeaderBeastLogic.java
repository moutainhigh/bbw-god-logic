package com.bbw.god.gameuser.leadercard.beast;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.game.config.treasure.TreasureType;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 神兽业务逻辑
 *
 * @author suhq
 * @date 2021-03-26 17:25
 **/
@Service
public class UserLeaderBeastLogic {
    @Autowired
    private GameUserService gameUserService;

    /**
     * 转备神兽
     *
     * @param uid
     * @param beastId
     */
    public RDCommon take(long uid, int beastId) {
        // 检查神兽有效性
        TreasureChecker.checkIsExist(beastId);
        CfgTreasureEntity treasure = TreasureTool.getTreasureById(beastId);
        if (treasure.getType() != TreasureType.BEAST_XJLS.getValue() && treasure.getType() != TreasureType.BEAST_FTXS.getValue()) {
            throw ExceptionForClientTip.fromi18nKey("leader.beast.not.valid");
        }
        UserLeaderBeasts userLeaderBeasts = gameUserService.getSingleItem(uid, UserLeaderBeasts.class);
        if (null != userLeaderBeasts && userLeaderBeasts.ifTaked(beastId)) {
            throw ExceptionForClientTip.fromi18nKey("leader.beast.already.take");
        }
        //检查是否拥有该神兽
        TreasureChecker.checkIsEnough(beastId, 1, uid);

        if (null == userLeaderBeasts) {
            userLeaderBeasts = UserLeaderBeasts.getInstance(uid, beastId);
            gameUserService.addItem(uid, userLeaderBeasts);
        } else {
            userLeaderBeasts.take(beastId);
            gameUserService.updateItem(userLeaderBeasts);
        }
        return new RDCommon();
    }

    /**
     * 卸下神兽
     *
     * @param uid
     * @param beastId
     * @return
     */
    public RDCommon takeOff(long uid, int beastId) {
        // 检查神兽有否已穿上
        UserLeaderBeasts userLeaderBeasts = gameUserService.getSingleItem(uid, UserLeaderBeasts.class);
        if (null == userLeaderBeasts || !userLeaderBeasts.ifTaked(beastId)) {
            throw ExceptionForClientTip.fromi18nKey("leader.beast.not.token");
        }

        if (null == userLeaderBeasts) {
            userLeaderBeasts = UserLeaderBeasts.getInstance(uid, beastId);
            gameUserService.addItem(uid, userLeaderBeasts);
        } else {
            userLeaderBeasts.takeOff(beastId);
            gameUserService.updateItem(userLeaderBeasts);
        }
        return new RDCommon();
    }

    /**
     * 禁用技能
     *
     * @param uid
     * @param beastId
     * @param skillId
     * @param active
     * @return
     */
    public RDSuccess activeSkill(long uid, int beastId, int skillId, boolean active) {
        // 检查神兽有否已穿上
        UserLeaderBeasts userLeaderBeasts = gameUserService.getSingleItem(uid, UserLeaderBeasts.class);
        if (null == userLeaderBeasts || !userLeaderBeasts.ifTaked(beastId)) {
            throw ExceptionForClientTip.fromi18nKey("leader.beast.not.token");
        }
        if (active) {
            userLeaderBeasts.activeSkill(beastId, skillId);
        } else {
            userLeaderBeasts.unactiveSkill(beastId, skillId);
        }
        gameUserService.updateItem(userLeaderBeasts);
        return new RDSuccess();
    }

    /**
     * 获取神兽信息
     *
     * @param uid
     * @param beastId
     * @return
     */
    public RDBeastInfo getBeastInfo(long uid, int beastId) {
        // 检查神兽有否已穿上
        UserLeaderBeasts userLeaderBeasts = gameUserService.getSingleItem(uid, UserLeaderBeasts.class);
        if (null == userLeaderBeasts || !userLeaderBeasts.ifTaked(beastId)) {
            throw ExceptionForClientTip.fromi18nKey("leader.beast.not.token");
        }
        RDBeastInfo rd = new RDBeastInfo();
        rd.setUnactiveSkills(userLeaderBeasts.gainUnactiveSkills(beastId));

        return rd;
    }
}
