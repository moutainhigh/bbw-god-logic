package com.bbw.god.game.dfdj.fight;

import com.bbw.common.DateUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.dfdj.DfdjDateService;
import com.bbw.god.game.dfdj.DfdjService;
import com.bbw.god.game.dfdj.zone.DfdjZone;
import com.bbw.god.game.dfdj.zone.DfdjZoneService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.redis.UserRedisKey;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author suchaobin
 * @description 巅峰对决人员信息服务
 * @date 2021/1/5 14:20
 **/
@Slf4j
@Service
public class DfdjFighterService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private DfdjZoneService dfdjZoneService;
    @Autowired
    private DfdjDateService dfdjDateService;
    @Autowired
    private DfdjService dfdjService;
    
    /**
     * 获得巅峰对决人员信息
     *
     * @param uid
     * @return
     */
    @NonNull
    public DfdjFighter getFighter(long uid) {
        CfgServerEntity server = gameUserService.getOriServer(uid);
        DfdjFighter fighter = gameUserService.getSingleItem(uid, DfdjFighter.class);
        //首次加入巅峰对决
        if (fighter == null) {
            fighter = joinDfdj(uid, server.getMergeSid());
        }
        DfdjZone dfdjZone = dfdjZoneService.getZoneByServer(server);
        // 新赛季重置,休赛期不重置
        if (dfdjZone != null && fighter.getLastGotDate().before(dfdjZone.getBeginDate()) && fighter.getJoinTimes() > 0) {
            fighter.resetForNewSeason();
        }
        //重置仙豆购买次数
        Date buyResetDate = dfdjDateService.getDfdjBuyResetDate();
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
     * 首次加入巅峰对决调用
     *
     * @param uid
     * @param sid
     * @return
     */
    private DfdjFighter joinDfdj(long uid, int sid) {
        DfdjFighter fighter = DfdjFighter.instance(uid);
        gameUserService.addItem(uid, fighter);
        // 加入巅峰对决
        String fighterKey = UserRedisKey.getUserDataKey(fighter);
        int serverGroup = ServerTool.getServerGroup(sid);
        dfdjService.joinDfdj(fighterKey, serverGroup);
        log.info("{}成功加入巅峰对决", uid);
        return fighter;
    }
}
