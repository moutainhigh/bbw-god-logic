package com.bbw.god.gm;

import com.bbw.common.JSONUtil;
import com.bbw.common.LM;
import com.bbw.common.Rst;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.db.dao.InsRoleInfoDao;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.city.CfgNightmareChengC;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.job.tomysql.UserStatisticToDBJob;
import com.bbw.god.login.repairdata.RepairNightmareService;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import com.bbw.god.server.ServerUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author suchaobin
 * @description 玩家梦魇世界相关操作接口
 * @date 2020/10/9 11:44
 **/
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMUserNightmareCtrl {
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private RepairNightmareService repairNightmareService;
    @Autowired
    private InsRoleInfoDao insRoleInfoDao;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private GameUserService gameUserService;


    /**
     * 清理梦魇旧数据：攻城记录、成就、统计
     *
     * @param nickname
     * @param sId
     * @return
     */
    @RequestMapping("nightmare!clearOldData")
    public Rst clearOldData(String nickname, int sId) {
        Optional<Long> guId = this.serverUserService.getUidByNickName(sId, nickname);
        if (!guId.isPresent()) {
            return Rst.businessFAIL("不存在的玩家！" + nickname);
        }
        long uid = guId.get();
        repairNightmareService.cleanOldNightmareCityData(uid);
        repairNightmareService.cleanOldNightmareCityStatistic(uid);
        repairNightmareService.cleanOldNightmareCityAchievement(uid);
        return Rst.businessOK();
    }

    /**
     * 查看梦魇成就
     * @return
     */
    @RequestMapping("nightmare!achievement")
    public Rst achievement(){
        SpringContextUtil.getBean(UserStatisticToDBJob.class).job();
        List<String> achievementName = repairNightmareService.getAllNightmareCityAchievementName();
        return Rst.businessOK().put("成就名称：", JSONUtil.toJson(achievementName)).put("数量",achievementName.size());
    }

    @RequestMapping("nightmare!resetAttackDifficulty")
    public Rst resetAttackDifficulty(String nickname, int sId) {
        Optional<Long> guId = this.serverUserService.getUidByNickName(sId, nickname);
        if (!guId.isPresent()) {
            return Rst.businessFAIL("不存在的玩家！" + nickname);
        }
        repairNightmareService.reSettleNightmare(guId.get());
        return Rst.businessOK("成功！");
    }

    /**
     * 发送邮件奖励
     *
     * @param level
     * @param sinceDate
     * @return
     */
    @RequestMapping("nightmare!senAward")
    public Rst senAward(int level, int sinceDate) {
        CfgNightmareChengC cfg = Cfg.I.getUniqueConfig(CfgNightmareChengC.class);
        List<Long> uids = insRoleInfoDao.getUidByLevelAndLastLoginDate(level, sinceDate);
        List<UserMail> mailsToSend = new ArrayList<>();
        for (Long uid : uids) {
            List<UserNightmareCity> ownCities = userCityService.getUserOwnNightmareCities(uid);
            int num = ownCities.size();
            CfgNightmareChengC.CfgNightmareAward cfgNightmareAward = cfg.getNightmareAwards().stream().filter(tmp -> num >= tmp.getMinOwnNum() && num <= tmp.getMaxOwnNum()).findFirst().orElse(null);
            if (null == cfgNightmareAward) {
                continue;
            }
            List<Award> awards = cfgNightmareAward.getAwards();
            String title = LM.I.getMsgByUid(uid,"mail.gm.nightmare.attack.title");
            String content = LM.I.getMsgByUid(uid,"mail.gm.nightmare.attack.content",num);
            UserMail userMail = UserMail.newAwardMail(title, content, uid, awards);
            log.info(userMail.toString());
            mailsToSend.add(userMail);
        }
        gameUserService.addItems(mailsToSend);
        return Rst.businessOK("奖励发放成功！");
    }


}
