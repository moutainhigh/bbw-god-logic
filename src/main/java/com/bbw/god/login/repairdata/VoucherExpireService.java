package com.bbw.god.login.repairdata;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

import static com.bbw.god.login.repairdata.RepairDataConst.VOUCHER_EXPIRE_TIME;

/**
 * @author suchaobin
 * @description 上仙抵用券和萌新抵用券过期
 * @date 2020/11/30 09:22f
 **/
@Service
public class VoucherExpireService implements BaseRepairDataService{
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private MailService mailService;

    /**
     * 修复数据
     *
     * @param gu            玩家对象
     * @param lastLoginDate 上次登录时间
     */
    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (lastLoginDate.before(VOUCHER_EXPIRE_TIME)) {
            Date regTime = gu.getRoleInfo().getRegTime();
            if (regTime==null){
                return;
            }
            int daysBetween = DateUtil.getDaysBetween(regTime, DateUtil.now());
            // 注册前7天不过期
            if (daysBetween <= 7) {
                return;
            }
            long uid = gu.getId();
            int godVoucher = userTreasureService.getTreasureNum(uid, TreasureEnum.GOD_VOUCHER.getValue());
            int newerVoucher = userTreasureService.getTreasureNum(uid, TreasureEnum.NEWER_VOUCHER.getValue());
            expireVoucher(gu, TreasureEnum.GOD_VOUCHER.getValue(), godVoucher);
            expireVoucher(gu, TreasureEnum.NEWER_VOUCHER.getValue(), newerVoucher);
        }
    }

    private void expireVoucher(GameUser gu, int treasureId, int expiredNum) {
        // 没有对应数量
        if (0 == expiredNum) {
            return;
        }
        long uid = gu.getId();
        // 扣除资源
        TreasureEventPublisher.pubTDeductEvent(uid, treasureId, expiredNum, WayEnum.EXPIRE, new RDCommon());
        // 发送邮件
        int gold = treasureId == TreasureEnum.GOD_VOUCHER.getValue() ? 388 * expiredNum : 188 * expiredNum;
        Award award = new Award(AwardEnum.YB, gold);
        String treasureName = TreasureTool.getTreasureById(treasureId).getName();
        String title = LM.I.getMsgByUid(uid,"mail.voucher.expire.title", treasureName);
        String content = LM.I.getMsgByUid(uid,"mail.voucher.expire.content", treasureName);
        mailService.sendAwardMail(title, content, uid, Collections.singletonList(award));
    }
}
