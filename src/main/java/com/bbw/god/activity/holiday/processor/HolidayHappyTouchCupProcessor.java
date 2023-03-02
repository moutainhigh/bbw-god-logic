package com.bbw.god.activity.holiday.processor;

import com.bbw.common.LM;
import com.bbw.common.lock.SyncLockUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.processor.holidayJiuFZJ.HolidayJiuFZJProcessor;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDUserListInfos;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.activityrank.game.carefree.CareFreeRankService;
import com.bbw.god.activityrank.game.lovevalue.LoveValueRankService;
import com.bbw.god.db.dao.InsRoleInfoDao;
import com.bbw.god.db.pool.PlayerDataDAO;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 逍遥碰杯实现类
 *
 * @author: huanghb
 * @date: 2023/2/16 9:20
 */
@Service
public class HolidayHappyTouchCupProcessor extends AbstractActivityProcessor {
    @Autowired
    GameUserService gameUserService;
    @Autowired
    MailService mailService;
    @Autowired
    ActivityRankService activityRankService;
    @Autowired
    InsRoleInfoDao insRoleInfoDao;
    @Autowired
    PlayerDataDAO playerDataDAO;
    @Autowired
    UserTreasureService userTreasureService;
    @Autowired
    LoveValueRankService loveValueRankService;
    @Autowired
    CareFreeRankService careFreeRankService;
    @Autowired
    HolidayJiuFZJProcessor holidayJiuFZJProcessor;
    @Autowired
    SyncLockUtil syncLockUtil;

    public HolidayHappyTouchCupProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.HAPPY_TOUCH_CUP);
    }

    /**
     * 搜索玩家信息
     *
     * @param nickName
     * @return
     */
    public RDUserListInfos searchUserInfo(String nickName) {
        if ("".equals(nickName)) {
            throw new ExceptionForClientTip("message.param.error");
        }
        List<Long> users = insRoleInfoDao.getUidByNickName(nickName);
        RDUserListInfos rd = new RDUserListInfos();
        if (users.isEmpty()) {
            return rd;
        }
        List<RDUserListInfos.RDBuddyUser> rdBuddyUsers = new ArrayList<>();
        for (long uid : users) {
            GameUser gameUser = gameUserService.getGameUser(uid);
            rdBuddyUsers.add(RDUserListInfos.fromInsUserEntity(gameUser));
        }
        rd.setBuddyUsers(rdBuddyUsers);
        return rd;
    }

    /**
     * 赠送逍遥酿
     *
     * @param uid
     * @param carefreeBrewingNum
     * @param recipientId
     * @param message
     * @return
     */
    public RDCommon sendCarefreeBrewing(long uid, int carefreeBrewingNum, long recipientId, String message) {
        if (uid == recipientId) {
            throw new ExceptionForClientTip("activity.not.send.carefreeBrewing");
        }
        //检查道具数量
        TreasureChecker.checkIsEnough(TreasureEnum.CAREFREE_BREWING.getValue(), carefreeBrewingNum, uid);
        //扣除道具
        RDCommon rd = new RDCommon();
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.CAREFREE_BREWING.getValue(), carefreeBrewingNum, WayEnum.HAPPY_TOUCH_CUP, rd);
        //逍遥榜增加
        careFreeRankService.addCareFreeValue(uid, carefreeBrewingNum);
        //酒逢知己活动是否开启
        GameUser gu = gameUserService.getGameUser(recipientId);
        //添加活动进度
        holidayJiuFZJProcessor.addActivityProgress(recipientId, gu.getServerId(), carefreeBrewingNum);
        //给好友发送邮件
        sendMail(uid, recipientId, carefreeBrewingNum, message);
        return rd;
    }



    /**
     * 发送邮件
     *
     * @param uid
     * @param receiverId
     * @param awardNum
     * @param message
     */
    private void sendMail(long uid, long receiverId, int awardNum, String message) {
        //默认邮件文本
        if ("".equals(message)) {
            message = LM.I.getMsgByUid(receiverId, "activity.send.carefreeBrewing.default.massage");
        }
        String nickName = gameUserService.getGameUser(uid).getRoleInfo().getNickname();
        String title = LM.I.getMsgByUid(receiverId, "activity.send.carefreeBrewing.title", nickName);
        String content = LM.I.getMsgByUid(receiverId, "activity.send.carefreeBrewing.massage", nickName, awardNum, message);
        String nickname = gameUserService.getGameUser(uid).getRoleInfo().getNickname();
        mailService.sendPersonalMail(uid, receiverId, nickname, title, content);
    }
}
