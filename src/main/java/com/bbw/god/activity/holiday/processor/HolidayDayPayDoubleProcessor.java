package com.bbw.god.activity.holiday.processor;

import com.bbw.common.LM;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgProductGroup;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.pay.UserReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 首冲双倍活动(钻石福利)
 *
 * @author fzj
 * @date 2022/6/23 16:37
 */
@Service
public class HolidayDayPayDoubleProcessor extends AbstractActivityProcessor {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private MailService mailService;

    public HolidayDayPayDoubleProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.PER_DAY_DOUBLE_FIRST_R);
    }

    @Override
    public boolean isShowInUi(long uid) {
        return false;
    }

    /**
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    public boolean isOpened(int sid) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.PER_DAY_DOUBLE_FIRST_R.getValue());
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return a != null;
    }

    public void firstGoldPayDouble(long uid, CfgProductGroup.CfgProduct product, UserReceipt userReceipt) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        // 是否首冲
        boolean isNotFirstDouble = product.getQuantity() * 2 > userReceipt.gainDispatchNum();
        if (!isNotFirstDouble) {
           return;
        }
        // 每日首冲翻倍
        List<Award> awards = Arrays.asList(new Award(userReceipt.gainDispatchItem(), product.getQuantity()));
        String title = LM.I.getMsgByUid(uid, "mail.first.receipt.doubled.title");
        String content = LM.I.getMsgByUid(uid, "mail.first.receipt.doubled.content", product.getName());
        this.mailService.sendAwardMail(title, content, uid, awards);
    }

    /**
     * 钻石福利
     */
    public void diamondBenefits(long uid, CfgProductGroup.CfgProduct product, UserReceipt userReceipt) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        //额外钻石
        if (product.getPrice() == 6) {
            return;
        }
        int additionalDiamond = (int) (product.getQuantity() * 0.2);
        List<Award> awards = Collections.singletonList(new Award(userReceipt.gainDispatchItem(), additionalDiamond));
        String title = LM.I.getMsgByUid(uid, "mail.first.diamond.doubled.title");
        String content = LM.I.getMsgByUid(uid, "mail.first.diamond.doubled.message", additionalDiamond);
        mailService.sendAwardMail(title, content, uid, awards);
    }
}
