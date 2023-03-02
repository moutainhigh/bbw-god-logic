package com.bbw.god.game.maou;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.game.GameActivity;
import com.bbw.god.activity.game.GameActivityService;
import com.bbw.god.game.maou.cfg.GameMaouType;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 跨服魔王活动服务类
 *
 * @author: suhq
 * @date: 2022/1/7 12:02 下午
 */
@Service
public class GameMaouActivityService {
    private static List<Integer> GAME_MAOU_ACTIVITIES = GameMaouType.getMaouActivities();
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GameActivityService gameActivityService;

    /**
     * 获得并检查活动是否进行中
     *
     * @param uid
     * @return
     */
    public IActivity getMaouActivity(long uid) {
        int sid = gameUserService.getActiveSid(uid);
        List<GameActivity> gas = gameActivityService.getGameActivitiesBySid(sid);
        GameActivity gameMaouActivity = gas.stream()
                .filter(ga -> GAME_MAOU_ACTIVITIES.contains(ga.getType()) && ga.ifTimeValid())
                .findFirst().orElse(null);
        return gameMaouActivity;
    }


    /**
     * 获得并检查活动是否进行中
     *
     * @param uid
     * @return
     */
    public IActivity getAndCheckMauActivity(long uid) {
        IActivity gameActivity = getMaouActivity(uid);
        if (null == gameActivity) {
            throw ExceptionForClientTip.fromi18nKey("activity.is.timeout");
        }
        return gameActivity;
    }

}
