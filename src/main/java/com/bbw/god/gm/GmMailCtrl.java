package com.bbw.god.gm;

import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.UserMail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 邮件的接口
 *
 * @author: hzf
 * @create: 2022-11-22 18:54
 **/
@RestController
public class GmMailCtrl {
    @Autowired
    private GameUserService gameUserService;


    @RequestMapping("gm/mail!delete")
    public Rst delete(String uids, String titleLike) {
        List<Long> uidList = ListUtil.parseStrToLongs(uids);
        for (Long uid : uidList) {
            List<UserMail> multiItems = gameUserService.getMultiItems(uid, UserMail.class);
            if (null == multiItems) {
                continue;
            }
            List<UserMail> mailsToDel = multiItems.stream()
                    .filter(mail -> mail.getTitle().contains(titleLike))
                    .collect(Collectors.toList());
            this.gameUserService.deleteItems(uid, mailsToDel);
        }
        return Rst.businessOK();
    }
}
