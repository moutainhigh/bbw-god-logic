package com.bbw.god.activity.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.rd.RDActivityItem;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.statistics.ServerStatistic;
import com.bbw.god.statistics.StatisticKeyEnum;
import com.bbw.god.statistics.serverstatistic.GodServerStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author suhq
 * @description: 攻城略地
 * @date 2019-11-07 09:20
 **/
@Service
public class GongCLDProcessor extends AbstractActivityProcessor {

    @Autowired
    private GodServerStatisticService godServerStatisticService;

    public GongCLDProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.GongCLD);
    }

    private ActivityEnum getActivityType() {
        return this.activityTypeList.get(0);
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
		GameUser gu = gameUserService.getGameUser(uid);
		Date regTime = gu.getRoleInfo().getRegTime();
		Date endDate = DateUtil.addSeconds(regTime, 7 * 24 * 60 * 60 - 1);
		return endDate.getTime() - DateUtil.now().getTime();
	}

    @Override
    protected RDActivityItem toRdActivity(GameUser gu, UserActivity ua, CfgActivityEntity ca, int status) {
        RDActivityItem rdActivity = super.toRdActivity(gu, ua, ca, status);
        rdActivity.setSeries(ca.getSeries());
        rdActivity.setTotalProgress(ca.getNeedValue());
        if (ca.isFirstCC5()) {
            // 第一个攻下五级城的玩家
            ServerStatistic ss = this.godServerStatisticService.getStatistic(gu.getServerId(), StatisticKeyEnum.FIRST_CC5);
            if (ss != null) {
                long firstCC5GuId = ss.getUid();
                GameUser firstCC5Gu = this.gameUserService.getGameUser(firstCC5GuId);
                rdActivity.setNickname(firstCC5Gu.getRoleInfo().getNickname());
            }

        } else {
            int progress = 0;
            if (ua != null) {
                progress = ua.getProgress();
            }
            rdActivity.setProgress(progress);
        }
        return rdActivity;
    }

    /**
     * 处理攻城略地
     *
     * @param guId
     * @param sId
     * @param city
     */
    public void handleAttackProgress(long guId, int sId, CfgCityEntity city) {
        ActivityEnum activityType = getActivityType();
        IActivity a = this.activityService.getActivity(sId, activityType);
        if (a == null) {
            return;
        }

        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(activityType);
        List<UserActivity> uas = this.activityService.getUserActivities(guId, a.gainId(), activityType);
        boolean isUasNotEmpty = ListUtil.isNotEmpty(uas);
        for (CfgActivityEntity ca : cas) {
            // 首位攻下五级城的活动
            if (ca.isFirstCC5()) {
                // 如果是五级城，则做相应的处理
                if (city.getLevel() == 5) {
                    // 获取第一个攻下五级城的玩家记录
                    ServerStatistic ss = this.godServerStatisticService.getStatistic(sId, StatisticKeyEnum.FIRST_CC5);
                    if (ss == null) {
                        ss = ServerStatistic.instance(StatisticKeyEnum.FIRST_CC5, this.gameUserService.getGameUser(guId));
                        this.godServerStatisticService.addStatistic(ss);
                        this.activityService.handleUa(guId, null, a.gainId(), 1, ca);
                    }
                }
                continue;
            }

            // 不是同一个系列的跳过
            if (ca.getSeries() != city.getLevel()) {
                continue;
            }
            // log.info("{}级{}，攻城略地系列{}", city.getLevel(), city.getName(), ca.getName());
            UserActivity uActivity = null;
            if (isUasNotEmpty) {
                uActivity = uas.stream().filter(ua -> ua.getBaseId().equals(ca.getId())).findFirst().orElse(null);
            }
            this.activityService.handleUa(guId, uActivity, a.gainId(), 1, ca);

        }

    }
}
