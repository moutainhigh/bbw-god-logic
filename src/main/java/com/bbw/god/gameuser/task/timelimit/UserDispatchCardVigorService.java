package com.bbw.god.gameuser.task.timelimit;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * 派遣类任务精力服务类
 *
 * @author: suhq
 * @date: 2022/12/9 10:18 上午
 */
@Service
public class UserDispatchCardVigorService {
    @Autowired
    private GameUserService gameUserService;

    /**
     * 获取卡牌精力
     *
     * @param taskGroup
     * @param userCard
     * @param cardVigor
     * @return
     */
    public int getCardVigor(TaskGroupEnum taskGroup, UserCard userCard, UserCardVigor cardVigor) {
        int maxCardVigor = TimeLimitTaskTool.getMaxCardVigor(userCard.getBaseId(), userCard.getHierarchy());
        if (null == cardVigor) {
            return maxCardVigor;
        }
        if (isToResetDate(taskGroup, cardVigor.getLastUpdate())) {
            cardVigor.reset();
            gameUserService.updateItem(cardVigor);
        }
        Integer vigor = cardVigor.getCardVigors().getOrDefault(userCard.getBaseId().toString(), maxCardVigor);
        return vigor;
    }

    /**
     * 获得精力重置时间
     *
     * @param taskGroup
     * @return
     */
    private boolean isToResetDate(TaskGroupEnum taskGroup, Date lastUpdateDate) {
        CfgTimeLimitTaskRules rules = TimeLimitTaskTool.getRules(taskGroup);
        Integer resetHour = rules.getCardVigorRestHour();
        Calendar now = Calendar.getInstance();
        Calendar lastUpdateTime = Calendar.getInstance();
        lastUpdateTime.setTime(lastUpdateDate);
        int nowHour = now.get(Calendar.HOUR_OF_DAY);
        int lastUpdateHour = lastUpdateTime.get(Calendar.HOUR_OF_DAY);
        int daysBetween = DateUtil.getDaysBetween(lastUpdateDate, now.getTime());
        if (daysBetween > 1) {
            return true;
        }
        if (daysBetween == 0 && nowHour >= resetHour && lastUpdateHour < resetHour) {
            return true;
        }
        if (daysBetween == 1 && (nowHour >= resetHour || lastUpdateHour < resetHour)) {
            return true;
        }
        return false;
    }
}
