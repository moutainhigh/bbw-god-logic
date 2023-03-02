package com.bbw.god.gameuser.mail;

import com.bbw.common.*;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.ServerUserService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 邮件
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月27日 下午4:13:49
 */
@RestController
public class MailCtrl extends AbstractController {
    @Autowired
    private MailService mailService;
    @Autowired
    private MailServiceImpl mailServiceImpl;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private AwardService awardService;

    @RequestMapping("mail!send")
    public Rst send(String receiverName, String title, String content) {
        if (StrUtil.isNull(receiverName)) {
            return Rst.failFromLocalMessage("mail.send.need.receiverName");
        }
        LoginPlayer user = getUser();
        if (SensitiveWordUtil.isNotPass(title, user.getChannelId(), user.getOpenId())) {
            throw new ExceptionForClientTip("mail.title.unvalid");
        }
        if (SensitiveWordUtil.isNotPass(content, user.getChannelId(), user.getOpenId())) {
            throw new ExceptionForClientTip("mail.content.unvalid");
        }
        Optional<Long> receiverUid = serverUserService.getUidByNickName(this.getServerId(), receiverName);
        if (!receiverUid.isPresent()) {
            return Rst.failFromLocalMessage("mail.send.receiverName.error", receiverName);
        }
        GameUser sender = this.getGameUser();
        GameUser receiver = gameUserService.getGameUser(receiverUid.get());
        // 低级别用户向高级别用户发送邮件
        if (receiver.getLevel() - sender.getLevel() > 20 && sender.getLevel() <= 20) {
            return Rst.failFromLocalMessage("mail.sender.level.tolow", receiverName);
        }
        mailService.sendPersonalMail(getUserId(), receiverUid.get(), getNickName(), title, content);
        return Rst.businessOK();
    }

    /**
     * 邮件列表
     *
     * @param mailType:10 个人 20系统 30奖励邮件
     * @param num:第几页。页码
     * @return
     */
    @RequestMapping("mail!listPage")
    public Rst listPage(int mailType, @RequestParam(defaultValue = "1") int num) {
        MailType type = MailType.fromValue(mailType);
        if (null == type) {
            return Rst.failFromLocalMessage("mail.no.type");
        }
        // 页码
        int pageNo = num;
        MailPageResult mpr = mailService.getPage(this.getUserId(), type, pageNo);
        ArrayList<ClientMail> mailList = new ArrayList<>();
        for (UserMail mailEntity : mpr.getEntityList()) {
            if (mailEntity.getTitle().contains("[20200501]神仙大会") && mailEntity.getSendTime().before(DateUtil.fromDateTimeString("2020-05-01 02:00:00"))) {
                mailEntity.setTitle(mailEntity.getTitle().replace("20200501", "20200430"));
                mailEntity.setContent(mailEntity.getContent().replace("20200501", "20200430"));
                gameUserService.updateItem(mailEntity);
            }
            ClientMail tmp = new ClientMail();
            tmp.setId(mailEntity.getId());
            tmp.setSender(mailEntity.getSenderNickName());
            tmp.setTime(DateUtil.toString(mailEntity.getSendTime(), "yyyy.M.d"));
            int timeout = DateUtil.getDaysBetween(new Date(), mailEntity.getExpireTime());
            if (timeout <= 0) {
                long timeoutH = DateUtil.getHourBetween(new Date(), mailEntity.getExpireTime());
                if (timeoutH <= 0) {
                    tmp.setTimeout("1小时内过期");
                } else {
                    tmp.setTimeout(timeoutH + "小时后过期");
                }
            } else {
                tmp.setTimeout(timeout + "天后过期");
            }
            tmp.setTitle(mailEntity.getTitle());
            tmp.setStatus(mailEntity.getRead() ? 1 : 0);
            tmp.setIsAccept(mailEntity.getAccepted() ? 1 : 0);
            mailList.add(tmp);
        }
        Rst rst = Rst.businessOK();
        rst.put("totalPage", mpr.getTotalPage());
        rst.put("array", mailList);
        return rst;
    }

    /**
     * @param id：邮件ID
     * @return
     */
    @RequestMapping("mail!readMail")
    public MailReadResult readMail(Long id) {
        MailReadResult mrr = mailService.readMail(this.getUserId(), id);
        return mrr;
    }

    @RequestMapping("mail!delete")
    public Rst delete(String ids) {
        if (StrUtil.isNull(ids)) {
            String msg = LM.I.getMsg("mail.delete.need.id");
            return Rst.businessFAIL(msg);
        }
        String[] idList = ids.split(",");
        List<Long> del_ids = new ArrayList<>();
        for (int i = 0; i < idList.length; i++) {
            del_ids.add(Long.valueOf(idList[i]));
        }
        mailService.delete(this.getUserId(), del_ids);
        return Rst.businessOK();
    }

    /**
     * 领取邮件奖励
     *
     * @param id
     * @return
     */
    @RequestMapping("mail!acceptAward")
    public RDCommon acceptAward(Long id) {
        Long uid = getUserId();
        // System.out.println("to lock:" + uid);
        // 防止并发的请求
        // TODO 待重构
        synchronized (uid) {
            // System.out.println("bengin lock:" + uid);
            Optional<UserMail> mailObj = gameUserService.getUserData(this.getUserId(), id, UserMail.class);
            if (!mailObj.isPresent()) {
                throw new ExceptionForClientTip("mail.not.exists");
            }
            UserMail mail = mailObj.get();
            if (null == mail.getAward() || mail.getAward().isEmpty()) {
                throw new ExceptionForClientTip("mail.no.award");
            }
            if (mail.getAccepted()) {
                throw new ExceptionForClientTip("mail.award.accept.yet");
            }
            RDCommon rd = new RDCommon();
            String mailTitle = LM.I.getFormatMsg("mail.award.accept.title", mail.getTitle());
            mail.setAccepted(true);
            gameUserService.updateItem(mail);
            awardService.fetchAward(this.getUserId(), mail.getAward(), WayEnum.Mail, mailTitle, rd);
            // System.out.println("end lock:" + uid);
            return rd;
        }

    }

    /**
     * 一键领取所有邮件奖励
     *
     * @return
     */
    @RequestMapping("mail!acceptAllAward")
    public RDCommon acceptAllAward() {
        Long uid = getUserId();
        List<UserMail> mailList = mailServiceImpl.getUserMails(uid);
        if (mailList.isEmpty()) {
            throw new ExceptionForClientTip("mail.not.award");
        }
        mailList = mailList.stream().filter(mail -> (mail.getType() == MailType.AWARD && !mail.getAccepted())).collect(Collectors.toList());
        RDCommon rd = new RDCommon();
        List<Award> awards = new ArrayList<>();
        for (UserMail mailInfo : mailList) {
            mailInfo.setRead(true);
            mailInfo.setAccepted(true);
            awards.addAll(mailInfo.getAward());
        }
        gameUserService.updateItems(mailList);
        awardService.sendNeedMergedAwards(this.getUserId(), awards, WayEnum.Mail, "", rd);
        return rd;
    }

    /**
     * 获取未读和未领取奖励读邮件数量提示信息
     *
     * @return
     */
    @RequestMapping("mail!gainMailsInfo")
    public Rst getUnReadAndUnAcceptNums() {
        int[] mailsInfo = mailService.getMailInfo(this.getUserId());
        Rst rst = Rst.businessOK();
        rst.put("mailsInfo", mailsInfo);
        return rst;
    }

    @Getter
    @Setter
    public static class ClientMail {
        private long id;
        private String sender;
        private String title;
        private String time;
        private String timeout;
        private int status;
        private int isAccept;
    }

}
