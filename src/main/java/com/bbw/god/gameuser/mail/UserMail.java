package com.bbw.god.gameuser.mail;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.pay.UserReceipt;
import com.bbw.god.gameuser.redis.UserRedisKey;
import com.bbw.god.validator.CheckContent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-07 15:57
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserMail extends UserData implements Comparable<UserMail> {
    private static final long SystemSenderId = -1L;
    public static UserDataType ResType = UserDataType.MAIL;
    @TableId(type = IdType.INPUT)
    private Long mailId; //邮件ID
    private Long senderId; //发送者id
    private String senderNickName; //发送者昵称
    private Long receiverId; //接收者id
    private MailType type = MailType.SYSTEM; //类型
    @NotBlank(message = "mail.title.not.empty")
    @CheckContent(message = "mail.title.unvalid")
    private String title; //标题
    private Date sendTime = new Date(); //发送时间
    private Date expireTime = DateUtil.addDays(DateUtil.now(), 30); //过期时间
    private Boolean read = false; //0:未读；1已读
    @NotBlank(message = "mail.content.not.empty")
    @CheckContent(message = "mail.content.unvalid")
    private String content; //邮件内容
    //private Integer extype = 0; //额外数据类型。0:无额外数据。1:有奖励
    //private String exData; //额外的数据。
    //private Long replayMailId; //邮件回复ID
    private Boolean accepted = true; //0:未领取;1已领取
    private List<Award> award = new ArrayList<Award>();//奖励列表
    private Boolean deleted = false;//删除标志
    private int way = 20;//邮件来源

    public boolean mailTimeOutDate(Date now) {
        if (null != expireTime && expireTime.before(now)) {
            return true;
        }
        return false;
    }

    /**
     * 删除过期的数据和用户删除超过10天的数据
     *
     * @return
     */
    public boolean shouldDelete(Date now) {
        //过期
        if (mailTimeOutDate(now)) {
            return true;
        }
        //个人已经删除的邮件
        if (deleted == true && type == MailType.PLAYER) {
            return true;
        }
        //系统已经删除的邮件
        if (deleted == true && type == MailType.SYSTEM && DateUtil.addDays(sendTime, 3).before(now)) {
            return true;
        }
        //系统已经删除的邮件
        if (deleted == true && type == MailType.AWARD && DateUtil.addDays(sendTime, 15).before(now)) {
            return true;
        }
        return false;
    }

    @Override
    public Long getId() {
        return mailId;
    }

    @Override
    public Long getGameUserId() {
        return receiverId;
    }

    @Override
    public UserDataType gainResType() {
        return ResType;
    }

    /**
     * 设置多个邮件奖励
     *
     * @param awardsList 奖励列表
     */
    public void setAward(List<Award> awardsList) {
        if (ListUtil.isNotEmpty(awardsList)) {
            this.award = awardsList;
            this.type = MailType.AWARD;
        }
    }

    public void setType(MailType mailType) {
        if (MailType.AWARD == mailType) {
            if (ListUtil.isNotEmpty(award)) {
                this.type = MailType.AWARD;
            }
        } else {
            this.type = mailType;
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(UserMail o) {
        //先按照已读未读排序
        if (!this.getRead() && o.getRead()) {
            return -1;
        }
        //再按照id排序
        return (this.getId() - o.getId()) > 0 ? -1 : 1;
    }

    /**
     * 奖励邮件
     *
     * @param title
     * @param content
     * @param receiverUid
     * @param awardJson
     * @return
     */
    public static UserMail newAwardMail(String title, String content, Long receiverUid, String awardJson) {
        UserMail mail = new UserMail();
        mail.setTitle(title);
        mail.setContent(content);
        mail.setReceiverId(receiverUid);
        mail.setSenderId(SystemSenderId);
        mail.setSenderNickName(LM.I.getMsg("mail.sender.is.system"));
        mail.setType(MailType.AWARD);
        mail.setMailId(UserRedisKey.getNewUserDataId());
        List<Award> awards = null;
        try {
            awards = JSON.parseArray(awardJson, Award.class);
        } catch (Exception e) {
            throw CoderException.high("无法解析邮件奖励内容！" + awardJson);
        }
        mail.setAward(awards);
        mail.setAccepted(false);
        return mail;
    }

    public static UserMail newAwardMail(String title, String content, Long receiverUid, List<Award> awards) {
        UserMail mail = new UserMail();
        mail.setTitle(title);
        mail.setContent(content);
        mail.setReceiverId(receiverUid);
        mail.setSenderId(SystemSenderId);
        mail.setSenderNickName(LM.I.getMsg("mail.sender.is.system"));
        mail.setType(MailType.AWARD);
        mail.setMailId(UserRedisKey.getNewUserDataId());
        mail.setAward(awards);
        mail.setAccepted(false);
        return mail;
    }

    public static UserMail newAwardMail(String title, String content, Long receiverUid, List<Award> awards, int remainDay) {
        UserMail mail = new UserMail();
        mail.setTitle(title);
        mail.setContent(content);
        mail.setReceiverId(receiverUid);
        mail.setSenderId(SystemSenderId);
        mail.setSenderNickName(LM.I.getMsg("mail.sender.is.system"));
        mail.setType(MailType.AWARD);
        mail.setMailId(UserRedisKey.getNewUserDataId());
        mail.setAward(awards);
        mail.setAccepted(false);
        mail.setExpireTime(DateUtil.addDays(DateUtil.now(), remainDay));
        return mail;
    }

    public static UserMail newSystemMail(String title, String content, Long receiverUid) {
        UserMail mail = new UserMail();
        mail.setTitle(title);
        mail.setContent(content);
        mail.setReceiverId(receiverUid);
        mail.setSenderId(UserMail.SystemSenderId);
        mail.setSenderNickName(LM.I.getMsg("mail.sender.is.system"));
        mail.setType(MailType.SYSTEM);
        mail.setMailId(UserRedisKey.getNewUserDataId());
        return mail;
    }

    public static UserMail newRiceiptMail(UserReceipt userReceipt) {
        UserMail mail = new UserMail();
        String title = "[" + userReceipt.getProductName() + "]充值回执";
        mail.setTitle(title);
        String content = "下发详情：" + userReceipt.getResult();
        mail.setContent(content);
        mail.setReceiverId(userReceipt.getGameUserId());
        mail.setSenderId(UserMail.SystemSenderId);
        mail.setSenderNickName(LM.I.getMsg("mail.sender.is.system"));
        mail.setType(MailType.SYSTEM);
        mail.setMailId(UserRedisKey.getNewUserDataId());
        return mail;
    }

    /**
     * 发送个人邮件
     *
     * @param senderId       发送者id
     * @param receiverId     收件人id
     * @param senderNickName 发件人角色名
     * @param title          标题
     * @param content        内容
     * @return
     */
    public static UserMail newPersonalMail(long senderId, long receiverId, String senderNickName, String title, String content) {
        UserMail mail = new UserMail();
        mail.setMailId(UserRedisKey.getNewUserDataId());
        mail.setType(MailType.PLAYER);
        mail.setTitle(title);
        mail.setContent(content);
        mail.setReceiverId(receiverId);
        mail.setGameUserId(senderId);
        mail.setSenderId(senderId);
        mail.setSenderNickName(senderNickName);
        return mail;
    }
}