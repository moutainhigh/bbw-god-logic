package com.bbw.god.activity.processor;

import com.bbw.common.LM;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.gameuser.pay.UserReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author lzc
 * @description: 梦魇世界6元首充
 * @date 2021-03-24 09:20
 **/
@Service
public class NightmareFirstRechargeProcessor extends AbstractActivityProcessor {
    @Autowired
    private MailService mailService;
    private static final int FIRST_RECHARGE_198 = 73;
    private static final Integer FIRST_RECHARGE_198_CARD_IDS = 110040; //神兽-真金兽
    private static final List<Integer> FIRST_RECHARGE = Arrays.asList(70, 71, 72, 73);

    public NightmareFirstRechargeProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.NIGHTMARE_FIRST_R);
    }

    /**
     * 充值新项
     *
     * @param param
     */
    public void rechargeItem(UserReceipt param) {
        if (!FIRST_RECHARGE.contains(param.getProductId())) {
            return;
        }
        GameUser gu = gameUserService.getGameUser(param.getGameUserId());
        if (!isFirst(gu)) {
            return;
        }
        if (param.getProductId() != FIRST_RECHARGE_198) {
            return;
        }
        String title = LM.I.getMsgByUid(param.getGameUserId(), "mail.first.recharge.award.title");
        String content = LM.I.getMsgByUid(param.getGameUserId(), "mail.first.recharge.award.content");
        UserMail mail = UserMail.newAwardMail(title, content, param.getGameUserId(), Arrays.asList(Award.instance(FIRST_RECHARGE_198_CARD_IDS, AwardEnum.FB, 1)));
        mailService.send(mail);
    }

    private CfgActivityEntity getRechargeActivity() {
        return ActivityTool.getActivityByType(ActivityEnum.NIGHTMARE_FIRST_R);
    }

    /**
     * 是否是首次充值
     *
     * @param gu
     * @return
     */
    public boolean isFirst(GameUser gu) {
        IActivity a = this.activityService.getActivity(gu.getServerId(), ActivityEnum.NIGHTMARE_FIRST_R);
        CfgActivityEntity rca = this.getRechargeActivity();
        UserActivity rechargeActivity = this.activityService.getUserActivity(gu.getId(), a.gainId(), rca.getId());
        if (rechargeActivity == null) {
            return true;
        }
        if (rechargeActivity.getStatus() >= AwardStatus.ENABLE_AWARD.getValue()) {
            return false;
        }
        return true;
    }

    /**
     * 是否是首充项目
     *
     * @param pid
     * @return
     */
    public boolean isFirstRechargeItem(int pid) {
        for (Integer id : FIRST_RECHARGE) {
            if (id == pid) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否可以继续充值
     *
     * @param uid
     * @param pid
     * @return
     */
    public boolean canBuy(long uid, int pid) {
        if (FIRST_RECHARGE.contains(pid)) {
            return isFirst(gameUserService.getGameUser(uid));
        }
        return true;
    }

}
