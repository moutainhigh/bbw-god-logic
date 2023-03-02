package com.bbw.god.gameuser.mail;

import com.alibaba.fastjson.JSON;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.impl.DefaultAwardService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.mail.event.MailEventPublisher;
import com.bbw.god.gameuser.redis.GameUserDataRedisUtil;
import com.bbw.god.validator.GodValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-29 14:25
 */
@Service("gameMailService")
public class MailServiceImpl implements MailService {
    /**
     * 翻页每页的条数
     **/
    private static final int PER_PAGE_NO = 20;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GameUserDataRedisUtil gameUserDataRedisUtil;

    @Override
    public MailPageResult getPage(long userId, MailType mailType, int pageNo) {
        MailPageResult mpr = new MailPageResult();
        // 获取所有邮件对象
        List<UserMail> mailList = getUserMails(userId);
        if (null == mailList || mailList.isEmpty()) {
            return mpr;
        }
        // 根据邮件类型过滤一下
        List<UserMail> mailTypeList = mailList.stream().filter(mail -> (mail.getType() == mailType)).collect(Collectors.toList());
        // 根据未读优先需要排序一下
        Collections.sort(mailTypeList);
        // 分页
        int totalPage = mailTypeList.size() / PER_PAGE_NO + (mailTypeList.size() % PER_PAGE_NO == 0 ? 0 : 1);
        mpr.setTotalPage(totalPage);
        List<UserMail> resultList = totalPage > 1 ? ListUtil.subListForPage(mailTypeList, pageNo, PER_PAGE_NO) : mailTypeList;
        mpr.addEntity(resultList);
        //
        return mpr;
    }

    @Override
    public MailReadResult readMail(long userId, Long mailId) {
        Optional<UserMail> mailObj = this.gameUserService.getUserData(userId, mailId, UserMail.class);
        UserMail mail = mailObj.get();
        MailReadResult mrr = new MailReadResult();
        mrr.setContent(mail.getContent());
        if (!mail.getRead()) {
            mail.setRead(true);
            if (mail.getType() != MailType.AWARD) {
                mail.setAccepted(true);
            }
            this.gameUserService.updateItem(mail);
        }
        mrr.setIsAccept(mail.getAccepted() ? 1 : 0);
        if (mail.getType() == MailType.AWARD) {
            mrr.setAwards(mail.getAward());
        }
        return mrr;
    }

    @Override
    public void send(UserMail mailEntity) {
        this.gameUserService.addItem(mailEntity.getGameUserId(), mailEntity);
    }

    @Override
    public void delete(long userId, List<Long> ids) {
        List<UserMail> items = new ArrayList<>();
        // 获取所有要删除的邮件对象
        List<UserMail> mailList = getUserMails(userId).stream().filter(m -> ids.contains(m.getId())).collect(Collectors.toList());
        boolean present = mailList.stream().filter(m -> !m.getAward().isEmpty() && !m.getAccepted()).findFirst().isPresent();
        for (UserMail mail : mailList) {
            if (ids.stream().filter(l -> l == mail.getId().longValue()).findAny().isPresent()) {
                if (mail.getAward().isEmpty() || mail.getAccepted()) {
                    mail.setDeleted(true);
                    items.add(mail);
                }
            }
        }
        this.gameUserService.updateItems(items);
    }

    @Override
    public int[] getMailInfo(long uid) {
        int[] mailInfo = new int[]{0, 0, 0};
        List<UserMail> mailList = getUserMails(uid);
        Date now = DateUtil.now();
        for (UserMail userMail : mailList) {
            if (userMail.shouldDelete(now)) {
                this.gameUserService.deleteItem(userMail);
            }
        }
        mailList = mailList.stream().filter(mail -> !mail.shouldDelete(now)).collect(Collectors.toList());
        // 个人邮件未读数量
        long count = mailList.stream().filter(mail -> (mail.getType() == MailType.PLAYER && !mail.getRead())).count();
        mailInfo[0] = Long.valueOf(count).intValue();
        // 系统邮件未读数量
        count = mailList.stream().filter(mail -> (mail.getType() == MailType.SYSTEM && !mail.getRead())).count();
        mailInfo[1] = Long.valueOf(count).intValue();
        // 奖励邮件未读数量
        count = mailList.stream().filter(mail -> (mail.getType() == MailType.AWARD && (!mail.getRead() || !mail.getAccepted()))).count();
        mailInfo[2] = Long.valueOf(count).intValue();
        return mailInfo;
    }

    @Override
    public int getUnReadNum(long uid) {
        List<UserMail> mailList = getUserMails(uid);
        if (mailList.isEmpty()) {
            return 0;
        }
        // 邮件未读数量
        long count = mailList.stream().filter(mail -> !mail.getRead()).count();
        return Long.valueOf(count).intValue();
    }

    @Override
    public void sendAwardMail(String title, String content, Long receiverUid, String awardJson) {
        try {
            if (DefaultAwardService.GU_ASSIGN_AWARD.equals(awardJson)) {
                return;
            }
            List<Award> awards = JSON.parseArray(awardJson, Award.class);
            sendAwardMail(title, content, receiverUid, awards);
        } catch (Exception e) {
            throw CoderException.high("无法解析邮件奖励内容！" + awardJson);
        }
    }

    @Override
    public void sendAwardMail(String title, String content, Long receiverUid, List<Award> awards) {
        UserMail mail = UserMail.newAwardMail(title, content, receiverUid, awards);
        this.send(mail);
    }

    @Override
    public void sendAwardMail(String title, String content, Set<Long> receiverUids, String awardJson) {
        List<Award> awards = null;
        try {
            awards = JSON.parseArray(awardJson, Award.class);
        } catch (Exception e) {
            throw CoderException.high("无法解析邮件奖励内容！" + awardJson);
        }
        List<UserMail> mailList = new ArrayList<>(receiverUids.size());
        for (Long uid : receiverUids) {
            UserMail mail = UserMail.newAwardMail(title, content, uid, awards);
            mailList.add(mail);
        }
        this.gameUserService.addItems(mailList);
    }

    @Override
    public void sendAwardMail(String title, String content, Set<Long> receiverUids, List<Award> awards) {
        List<UserMail> mailList = new ArrayList<>(receiverUids.size());
        for (Long uid : receiverUids) {
            UserMail mail = UserMail.newAwardMail(title, content, uid, awards);
            mailList.add(mail);
        }
        this.gameUserService.addItems(mailList);
    }

    @Override
    public void sendSystemMail(String title, String content, Long receiverUid) {
        UserMail mail = UserMail.newSystemMail(title, content, receiverUid);
        this.send(mail);
    }

    @Override
    public void sendSystemMail(String title, String content, Set<Long> receiverUids) {
        List<UserMail> mailList = new ArrayList<>(receiverUids.size());
        for (Long receiverUid : receiverUids) {
            UserMail mail = UserMail.newSystemMail(title, content, receiverUid);
            mailList.add(mail);
        }
        this.gameUserService.addItems(mailList);
    }

    @Override
    public void sendPersonalMail(long sender, long receiver, String senderNickname, String title, String content) {
        UserMail mail = UserMail.newPersonalMail(sender, receiver, senderNickname, title, content);
        GodValidator.validateEntity(mail);
        send(mail);
        MailEventPublisher.pubMailReceiveEvent(mail);
    }

    /**
     * 获取用户未删除的邮件列表
     *
     * @param uid
     * @return
     */
    public List<UserMail> getUserMails(Long uid) {
        List<UserMail> mailList = this.gameUserService.getMultiItems(uid, UserMail.class);
        if (null == mailList || mailList.isEmpty()) {
            return new ArrayList<>();
        }
        // 未删除
        Date now = DateUtil.now();
        mailList = mailList.stream().filter(mail -> mail.getDeleted() == false && !mail.mailTimeOutDate(now)).collect(Collectors.toList());
        return mailList;
    }

    @Override
    public boolean hasLoadedMail(long uid) {
        return gameUserDataRedisUtil.hasLoadFromDb(uid, UserDataType.MAIL);
    }
}
