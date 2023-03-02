package com.bbw.god.game.sxdh;

import com.bbw.common.DateUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.redis.UserRedisKey;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 神仙大会人员信息服务
 *
 * @author suhq
 * @date 2019-06-25 15:36:34
 */
@Slf4j
@Service
public class SxdhFighterService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private SxdhService sxdhService;
    @Autowired
    private SxdhZoneService sxdhZoneService;
    @Autowired
    private SxdhRankService sxdhRankService;
    @Autowired
    private SxdhDateService sxdhDateService;

    /**
     * 获得神仙大会人员信息
     *
     * @param uid
     * @return
     */
    @NonNull
    public SxdhFighter getFighter(long uid) {
        CfgServerEntity server = gameUserService.getOriServer(uid);
        SxdhFighter fighter = gameUserService.getSingleItem(uid, SxdhFighter.class);
        //首次加入神仙大会
        if (fighter == null) {
            fighter = joinSxdh(uid, server.getMergeSid());
        }
        SxdhZone sxdhZone = sxdhZoneService.getZoneByServer(server);
        // 新赛季重置,休赛期不重置
        if (sxdhZone != null && fighter.getLastGotDate().before(sxdhZone.getBeginDate()) && fighter.getJoinTimes() > 0) {
            fighter.resetForNewSeason();
        }
        //每次挑战免费次数
        if (fighter.getLastGotDate().before(sxdhDateService.getSxdhDateEnd(0))) {
            fighter.resetFreeTimes();
        }
        //重置仙豆购买次数
        Date buyResetDate = sxdhDateService.getSxdhBuyResetDate();
        if (fighter.getLastGotDate().before(buyResetDate)) {
            if (fighter.getBeatBoughtResetDate() == null || fighter.getBeatBoughtResetDate().before(buyResetDate)) {
                fighter.setBeanBoughtTimes(0);
                fighter.setBeatBoughtResetDate(buyResetDate);
            }
        }

        //更新访问时间
        fighter.setLastGotDate(DateUtil.now());
        gameUserService.updateItem(fighter);
        return fighter;
    }

    /**
     * 首次加入神仙大会调用
     *
     * @param uid
     * @param sid
     * @return
     */
    private SxdhFighter joinSxdh(long uid, int sid) {
        SxdhFighter fighter = SxdhFighter.instance(uid);
        gameUserService.addItem(uid, fighter);
        // 加入神仙大会
        String fighterKey = UserRedisKey.getUserDataKey(fighter);
        int serverGroup = ServerTool.getServerGroup(sid);
        sxdhService.joinSxdh(fighterKey, serverGroup);
        log.info("{}成功加入神仙大会", uid);
        return fighter;
    }
}
