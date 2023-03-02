package com.bbw.god.gameuser.mail;

import com.bbw.god.game.award.Award;

import java.util.List;
import java.util.Set;

/**
 * 邮件业务逻辑
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-29 10:29
 */
public interface MailService {
    /**
     * 邮件列表
     *
     * @param userId
     * @param mailType
     * @param pageNo:页码
     * @return
     */
    MailPageResult getPage(long userId, MailType mailType, int pageNo);

    /**
     * 查看邮件
     *
     * @param userId
     * @param mailId
     * @return
     */
    MailReadResult readMail(long userId, Long mailId);

    /**
     * 发送邮件
     *
     * @param mailEntity
     * @return
     */
    void send(UserMail mailEntity);

    /**
     * 发送奖励邮件
     *
     * @param title:邮件标题
     * @param content:邮件内容
     * @param receiverUid:接收者id
     * @param awardJson:奖励JSON
     */
    void sendAwardMail(String title, String content, Long receiverUid, String awardJson);

    void sendAwardMail(String title, String content, Long receiverUid, List<Award> awards);

    void sendAwardMail(String title, String content, Set<Long> receiverUids, String awardJson);

    void sendAwardMail(String title, String content, Set<Long> receiverUids, List<Award> awards);

    /**
     * 发送系统邮件
     *
     * @param title
     * @param content
     * @param receiverUid
     */
    void sendSystemMail(String title, String content, Long receiverUid);

    void sendSystemMail(String title, String content, Set<Long> receiverUids);

    /**
     * 发送个人邮件。发送成功后会发布一个邮件发送事件
     *
     * @param sender         发送者uid
     * @param receiver       接收者uid
     * @param senderNickname 发送者昵称
     * @param title          邮件标题
     * @param content        邮件内容
     */
    void sendPersonalMail(long sender, long receiver, String senderNickname, String title, String content);

    /**
     * 删除邮件
     *
     * @param userId
     * @param ids
     * @return
     */
    void delete(long userId, List<Long> ids);

    /**
     * 获取未读的邮件数
     *
     * @param guId
     */
    int getUnReadNum(long guId);

    /**
     * 获取邮件数量提示信息
     *
     * @param userId
     * @return
     */
    int[] getMailInfo(long userId);

    boolean hasLoadedMail(long uid);
}
