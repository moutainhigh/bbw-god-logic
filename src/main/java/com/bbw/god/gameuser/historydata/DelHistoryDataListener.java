package com.bbw.god.gameuser.historydata;

import com.bbw.common.ListUtil;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.OppCardService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.event.EPCardDel;
import com.bbw.god.gameuser.card.event.UserCardDelEvent;
import com.bbw.god.gameuser.task.godtraining.UserGodTrainingTask;
import com.bbw.god.login.event.LoginEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 删除旧数据
 *
 * @author suhq
 * @date 2019-06-19 09:58:17
 */
@Component
public class DelHistoryDataListener {
    @Autowired
    private DelHistoryDataService delHistoryDataService;
    @Autowired
    private OppCardService oppCardService;
    @Autowired
    private GameUserService gameUserService;

    @Async
    @EventListener
    public void delAsLogin(LoginEvent event) {
        long uid = event.getLoginPlayer().getUid();
        // 删除过期的玩家每日任务
        delHistoryDataService.delExpiredDailyTasks(uid);
        // 删除过期限时任务
        delHistoryDataService.delExpiredTimeLimitTasks(uid);
        // 删除过期的玩家神仙
        delHistoryDataService.delExpiredGod(uid);
        // 删除过期的玩家商店记录
        delHistoryDataService.delExpiredMall(uid);
        //删除过期的玩家删除的数据
        delHistoryDataService.delExpireMail(uid);
        // 删除旧成就信息
        delHistoryDataService.delOldUserAchievement(uid);
        // 删除过期太一府数据
        delHistoryDataService.delOldUserTyf(uid);
        // 删除过期神仙大会赛季挑战
        delHistoryDataService.delExpiredSxdhSeasonTasks(uid);
        // 删除四个礼拜前的战令任务
        delHistoryDataService.delExpiredWarTokenTasks(uid);
        // 删除1个月前的轮回记录
        delHistoryDataService.delExpiredTransmigrationRecord(uid);
        // 删除已完成了所有上仙试炼的的上仙任务数据
        GameUser user = gameUserService.getGameUser(uid);
        if (user.getStatus().isGrowTaskCompleted()) {
            List<UserGodTrainingTask> godTrainingTasks = gameUserService.getMultiItems(uid, UserGodTrainingTask.class);
            delHistoryDataService.delUserData(uid, godTrainingTasks);
        }
        //产出格式化卡牌缓存数据
        oppCardService.clearCache(uid);
    }

    @Async
    @EventListener
    public void delCard(UserCardDelEvent event) {
        EPCardDel ep = event.getEP();
        List<UserCard> uCards = ep.getDelCards();
        if (ListUtil.isNotEmpty(uCards)) {
            LogUtil.logDeletedUserDatas(uCards, "卡牌数据");
        }
    }


}
