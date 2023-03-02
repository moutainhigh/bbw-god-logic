package com.bbw.god.gameuser.treasure;

import com.bbw.common.SpringContextUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;

public class TreasureChecker {
    private static UserTreasureService userTreasureService = SpringContextUtil.getBean(UserTreasureService.class);

    /**
     * 检查法宝是否存在 不存在则抛出客户端提示的异常ExceptionForClientTip
     *
     * @param treasureId
     */
    public static void checkIsExist(int treasureId) {
        CfgTreasureEntity treasure = TreasureTool.getTreasureById(treasureId);
        if (treasure == null) {
            throw new ExceptionForClientTip("treasure.not.exist", treasureId);
        }
    }

    /**
     * 是否有该法宝
     *
     * @param treasureId
     * @return 存在 返回true
     */
    public static boolean hasTreasure(long uid, int treasureId) {
        UserTreasure treasure = userTreasureService.getUserTreasure(uid, treasureId);
        return treasure != null && treasure.gainTotalNum() > 0;
    }

    public static void checkHasTreasure(long uid, int treasureId) {
        UserTreasure ut = userTreasureService.getUserTreasure(uid, treasureId);
        checkIsEnough(ut,treasureId, 1);
    }

    /**
     * 检查是否存在指定数量的法宝 不存在则抛出客户端提示的异常ExceptionForClientTip
     *
     * @param treasureId
     * @param needNum
     * @param uid
     */
    public static void checkIsEnough(int treasureId, int needNum, long uid) {
        UserTreasure ut = userTreasureService.getUserTreasure(uid, treasureId);
        checkIsEnough(ut, treasureId,needNum);
    }

    /**
     * 检查是否存在指定数量的法宝 不存在则抛出客户端提示的异常ExceptionForClientTip
     *
     * @param uTreasure
     * @param needNum
     */
    public static void checkIsEnough(UserTreasure uTreasure,int treasureId, int needNum) {
        if ( 0 == needNum) {
            return;
        }
        if (uTreasure == null || uTreasure.gainTotalNum() < needNum) {
            throw new ExceptionForClientTip("treasure.not.enough", TreasureTool.getTreasureById(treasureId).getName());
        }
    }
}
