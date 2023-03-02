package com.bbw.god.login.repairdata;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.bbw.god.login.repairdata.RepairDataConst.REISSUE_SEVEN_LOGIN_AWARD_TIME;

/**
 * @author suchaobin
 * @description 补发七日之约奖励
 * @date 2020/12/19 10:18
 **/
@Service
public class ReissueSevenLoginAwardService implements BaseRepairDataService{
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserCardService userCardService;
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
        if (lastLoginDate.before(REISSUE_SEVEN_LOGIN_AWARD_TIME)) {
            IActivity a = activityService.getActivity(gu.getServerId(), ActivityEnum.SEVEN_LOGIN);
            // 活动不存在返回
            if (null == a) {
                return;
            }
            // 注册时间超过2天以上，没有木吒的直接送
            Date regTime = gu.getRoleInfo().getRegTime();
            if (null == regTime) {
                return;
            }
            int daysBetween = DateUtil.getDaysBetween(regTime, DateUtil.now());
            // 注册时间不满2天的返回
            if (daysBetween <= 1) {
                return;
            }
            final int cardId = 206;
            UserCard userCard = userCardService.getUserCard(gu.getId(), cardId);
            // 已经有木吒的不送
            if (null != userCard) {
                return;
            }
            String title = LM.I.getMsgByUid(gu.getId(),"mail.activity.repair.award.title");
            String content = LM.I.getMsgByUid(gu.getId(),"mail.activity.repair.award.content");
            UserMail userMail = UserMail.newSystemMail(title, content, gu.getId());
            gameUserService.addItem(gu.getId(), userMail);
            CardEventPublisher.pubCardAddEvent(gu.getId(), cardId, WayEnum.ACTIVITY, "", new RDCommon());
        }
    }
}
