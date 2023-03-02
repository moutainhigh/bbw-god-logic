package com.bbw.god.activity.holiday.processor;

import com.bbw.common.LM;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDUserListInfos;
import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.activityrank.game.lovevalue.LoveValueRankService;
import com.bbw.god.db.dao.InsRoleInfoDao;
import com.bbw.god.db.pool.PlayerDataDAO;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.mail.MailType;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.gameuser.redis.UserRedisKey;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.validator.GodValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 花思寄语实现类
 *
 * @author fzj
 * @date 2022/2/8 9:26
 */
@Service
public class HolidayThoughtsOfFlowersProcessor extends AbstractActivityProcessor {
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

    public HolidayThoughtsOfFlowersProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.THOUGHTS_OF_FLOWERS);
    }

    /**
     * 搜索玩家信息
     *
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
     * 赠送鲜花
     *
     * @param uid
     * @param flowersNum
     * @param recipientId
     * @param message
     * @return
     */
    public RDCommon sendFlowers(long uid, int flowersNum, long recipientId, String message) {
        if (uid == recipientId) {
            throw new ExceptionForClientTip("activity.not.send.flowers");
        }
        //检查鲜花数量
        TreasureChecker.checkIsEnough(TreasureEnum.FLOWERS.getValue(), flowersNum, uid);
        //给好友发送邮件
        sendMail(uid, recipientId, flowersNum, message);
        //扣除鲜花
        RDCommon rd = new RDCommon();
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.FLOWERS.getValue(), flowersNum, WayEnum.ACTIVITY_SENG_FLOWERS, rd);
        //情缘榜增加
        loveValueRankService.addLoveValue(uid, flowersNum);
        //魅力榜增加
        activityRankService.incrementRankValue(recipientId, flowersNum, ActivityRankEnum.CHARM_RANK);
//        //发放头像框
//        sendHeadIcon(uid, rd);
        return rd;
    }

    /**
     * 发送邮件
     *
     * @param uid
     * @param receiverId
     * @param flowersNum
     * @param message
     */
    private void sendMail(long uid, long receiverId, int flowersNum, String message) {
        UserMail mail = new UserMail();
        //默认邮件文本
        if ("".equals(message)) {
            message = LM.I.getMsgByUid(receiverId, "activity.send.flowers.default.massage");
        }
        String nickName = gameUserService.getGameUser(uid).getRoleInfo().getNickname();
        String title = LM.I.getMsgByUid(receiverId, "activity.send.flowers.title", nickName);
        String content = LM.I.getMsgByUid(receiverId, "activity.send.flowers.massage", nickName, flowersNum, flowersNum, message);
        mail.setMailId(UserRedisKey.getNewUserDataId());
        mail.setTitle(title);
        mail.setContent(content);
        mail.setReceiverId(receiverId);
        mail.setGameUserId(uid);
        mail.setSenderId(uid);
        String nickname = gameUserService.getGameUser(uid).getRoleInfo().getNickname();
        mail.setSenderNickName(nickname);
        mail.setType(MailType.PLAYER);
        GodValidator.validateEntity(mail);
        mailService.send(mail);
    }

    /**
     * 发放头像框
     *
     * @param uid
     * @param rd
     */
    private void sendHeadIcon(long uid, RDCommon rd) {
        //检查是否有头像框
        UserTreasure userTreasure = userTreasureService.getUserTreasure(uid, TreasureEnum.VALENTINE_DAY_HEAD_ICON.getValue());
        if (null != userTreasure) {
            return;
        }
        //发放头像框
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.VALENTINE_DAY_HEAD_ICON.getValue(), 1, WayEnum.ACTIVITY_SENG_FLOWERS, rd);
    }
}
