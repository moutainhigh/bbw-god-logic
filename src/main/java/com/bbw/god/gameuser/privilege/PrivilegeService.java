package com.bbw.god.gameuser.privilege;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.processor.RechargeCardProcessor;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.pay.UserPayInfo;
import com.bbw.god.gameuser.pay.UserPayInfoService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 特权服务
 *
 * @author suhq
 * @date 2019-09-18 14:55:38
 */
@Service
public class PrivilegeService {
    @Autowired
    private RechargeCardProcessor rechargeCardProcessor;
    @Autowired
    private MailService mailService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserPayInfoService userPayInfoService;

    /**
     * 获得战斗经验加成
     *
     * @param uid
     * @return
     */
    public double getExtraFightExpRate(long uid) {
        List<Privilege> privileges = this.getPrivileges(uid);
        if (privileges.contains(Privilege.DilingYin)) {
            return 0.1;
        }
        return 0;
    }

    /**
     * 特产额外上限
     *
     * @param uid
     * @return
     */
    public int getExtraSpecialExpand(long uid) {
        List<Privilege> privileges = this.getPrivileges(uid);
        int extraExpand = 0;
        if (privileges.contains(Privilege.DilingYin)) {
            extraExpand += 2;
        }
        if (privileges.contains(Privilege.TianlingYin)) {
            extraExpand += 2;
        }
        return extraExpand;
    }


    public int getPocketNums(GameUser gu) {
        List<Privilege> privileges = this.getPrivileges(gu.getId());
        if (privileges.contains(Privilege.TianlingYin)) {
            return 8;
        }
        return 0;
    }

    /**
     * 获得额外免费宝箱次数
     *
     * @param gu
     * @return
     */
    public int getExtraFightBoxFreeTimes(GameUser gu) {
        List<Privilege> privileges = this.getPrivileges(gu.getId());
        if (privileges.contains(Privilege.DilingYin)) {
            return 1;
        }
        return 0;
    }

    /**
     * 发放头像框
     *
     * @param gu
     */
    public void sendHeadBox(GameUser gu) {
        List<Privilege> privileges = this.getPrivileges(gu.getId());
        if (privileges.contains(Privilege.DilingYin)) {
            int headBoxId = TreasureEnum.HEAD_ICON_FSXX.getValue();
            if (!TreasureChecker.hasTreasure(gu.getId(), headBoxId)) {
                // 发放头像框
                String title = LM.I.getMsgByUid(gu.getId(), "mail.head.box.title");
                String content = LM.I.getMsgByUid(gu.getId(), "mail.head.box.content");
                this.mailService.sendAwardMail(title, content, gu.getId(), Arrays.asList(new Award(headBoxId, AwardEnum.FB, 1)));
            }
        }
    }

    /**
     * 签到是否翻倍
     *
     * @param gu
     * @return
     */
    public int getMonthAwardDoubleTime(GameUser gu) {
        List<Privilege> privileges = this.getPrivileges(gu.getId());
        if (privileges.contains(Privilege.TianlingYin)) {
            return 2;
        }
        return 1;
    }

    /**
     * 是否福地翻倍
     *
     * @param gu
     * @return
     */
    public int getFuDiDoubleTime(GameUser gu) {
        List<Privilege> privileges = this.getPrivileges(gu.getId());
        if (privileges.contains(Privilege.TianlingYin)) {
            return PowerRandom.getRandomBySeed(2);
        }
        return 1;
    }

    /**
     * 天灵礼包的状态
     *
     * @param gu
     * @return
     */
    public AwardStatus getTianlingStatus(GameUser gu) {
        List<Privilege> privileges = this.getPrivileges(gu.getId());
        if (!privileges.contains(Privilege.TianlingYin)) {
            return AwardStatus.UNAWARD;
        }
        Date lastTianLingAwardTime = gu.getStatus().getLastTianlingAwardTime();
        if (lastTianLingAwardTime == null || !DateUtil.isToday(lastTianLingAwardTime)) {
            return AwardStatus.ENABLE_AWARD;
        }
        return AwardStatus.AWARDED;
    }

    /**
     * 获得天灵印礼包
     *
     * @param uid
     * @return
     */
    public RDCommon getTianlingAward(long uid) {
        GameUser gu = this.gameUserService.getGameUser(uid);
        AwardStatus status = this.getTianlingStatus(gu);
        if (status == AwardStatus.UNAWARD) {
            throw new ExceptionForClientTip("privilege.tianling.unaward");
        }
        if (status == AwardStatus.AWARDED) {
            throw new ExceptionForClientTip("privilege.tianling.awarded");
        }
        gu.getStatus().setLastTianlingAwardTime(DateUtil.now());
        gu.updateStatus();
        CfgPrivilege cfgPrivilege = Cfg.I.getUniqueConfig(CfgPrivilege.class);
        RDCommon rd = new RDCommon();
        this.awardService.fetchAward(gu.getId(), cfgPrivilege.getTianlingBag(), WayEnum.GET_TIANLING_BAG, "", rd);
        return rd;
    }

    /**
     * 地灵印礼包的状态
     *
     * @param gu
     * @return
     */
    public AwardStatus getDiLingStatus(GameUser gu) {
        List<Privilege> privileges = this.getPrivileges(gu.getId());
        if (!privileges.contains(Privilege.DilingYin)) {
            return AwardStatus.UNAWARD;
        }
        Date lastDiLingAwardTime = gu.getStatus().getLastDilingAwardTime();
        if (lastDiLingAwardTime == null || !DateUtil.isToday(lastDiLingAwardTime)) {
            return AwardStatus.ENABLE_AWARD;
        }
        return AwardStatus.AWARDED;
    }


    /**
     * 判断是否拥有天灵印
     *
     * @param uid
     * @return
     */
    public boolean isOwnTianLing(long uid) {
        return getPrivileges(uid).contains(Privilege.TianlingYin);
    }

    /**
     * 判断是否拥有地灵印
     *
     * @param uid
     * @return
     */
    public boolean isOwnDiLing(long uid) {
        return getPrivileges(uid).contains(Privilege.DilingYin);
    }

    /**
     * 获得玩家拥有的特权
     *
     * @param uid
     * @return
     */
    public List<Privilege> getPrivileges(long uid) {
        List<Privilege> privileges = new ArrayList<>();
        UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(uid);
        int ykRemainDays = this.rechargeCardProcessor.getYkRemainDays(userPayInfo);
        int jkRemainDays = this.rechargeCardProcessor.getJkRemainDays(userPayInfo);

        boolean hasBoughtSZK = userPayInfo.getEndFightBuyTime() != null;
        if (hasBoughtSZK && ykRemainDays > 0) {
            privileges.add(Privilege.DilingYin);
        }
        if (hasBoughtSZK && ykRemainDays > 0 & jkRemainDays > 0) {
            privileges.add(Privilege.TianlingYin);
        }
        return privileges;
    }
}
