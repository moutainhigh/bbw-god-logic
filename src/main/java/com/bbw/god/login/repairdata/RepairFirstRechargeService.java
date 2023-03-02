package com.bbw.god.login.repairdata;

import com.alibaba.fastjson.JSON;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityStatusEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.UserMail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bbw.god.login.repairdata.RepairDataConst.FIRST_AWARD_SEND_08V1;

/**
 * @author suchaobin
 * @description 修复首充相关问题
 * @date 2020/8/14 9:45
 **/
@Service
public class RepairFirstRechargeService implements BaseRepairDataService {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 修复数据
     *
     * @param gu            玩家对象
     * @param lastLoginDate 上次登录时间
     */
    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (lastLoginDate.before(FIRST_AWARD_SEND_08V1)) {
            IActivity a = activityService.getActivity(gu.getServerId(), ActivityEnum.FIRST_R);
            if (a == null) {
                return;
            }
            List<UserActivity> userActivities = activityService.getUserActivities(gu.getId(), a.gainId(),
                    ActivityEnum.FIRST_R);
            // 当前活动是否存在可领取或者已领取的
            boolean exists = userActivities.stream().anyMatch(uc ->
                    uc.getStatus() >= ActivityStatusEnum.ENABLE_AWARD0.getValue());
            // 不存在直接return
            if (!exists) {
                return;
            }
            List<Award> awards = new ArrayList<>();
            List<UserActivity> toUpdateDatas = new ArrayList<>();
            for (UserActivity userActivity : userActivities) {
                // 已领取跳过
                if (ActivityStatusEnum.AWARDED0.getValue() == userActivity.getStatus()) {
                    continue;
                }
                // 修改状态并放入修改数据集合中
                userActivity.setStatus(ActivityStatusEnum.AWARDED0.getValue());
                toUpdateDatas.add(userActivity);
                String awardJson = ActivityTool.getActivity(userActivity.getBaseId()).getAwards();
                awards.addAll(JSON.parseArray(awardJson, Award.class));
            }
            // 奖励不为空发邮件并保存数据
            if (ListUtil.isNotEmpty(awards)) {
                String title = LM.I.getMsgByUid(gu.getId(), "mail.repair.first.recharge.title");
                String content = LM.I.getMsgByUid(gu.getId(), "mail.repair.first.recharge.content");
                UserMail userMail = UserMail.newAwardMail(title, content, gu.getId(), awards);
                gameUserService.addItem(gu.getId(), userMail);
                gameUserService.updateItems(toUpdateDatas);
            }
        }
    }
}
