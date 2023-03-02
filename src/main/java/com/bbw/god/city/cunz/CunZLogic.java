package com.bbw.god.city.cunz;

import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.UserAchievementLogic;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 坊间怪谈
 *
 * @author fzj
 * @date 2021/12/6 11:21
 */
@Service
public class CunZLogic {
    @Autowired
    GameUserService gameUserService;
    @Autowired
    UserAchievementLogic userAchievementLogic;

    /**
     * 验证怪谈
     *
     * @param uid
     * @param cunZTalkId
     * @param secretAchievementId
     * @return
     */
    public RDCommon verifyTalk(long uid, Integer cunZTalkId, Integer secretAchievementId) {
        CfgCunZTalk cfgCunZTalks = Cfg.I.get(cunZTalkId, CfgCunZTalk.class);
        //获得正确的成就id
        Integer correctSecretAchievementId = cfgCunZTalks.getSecretAchievementId();
        //验证失败
        if (!secretAchievementId.equals(correctSecretAchievementId)) {
            return new RDCommon();
        }
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        RDCommon rd = new RDCommon();
        //发放铜钱
        ResEventPublisher.pubCopperAddEvent(uid, 500000, WayEnum.CZ, rd);
        //加入已验证列表
        info.getVerifyAccomplishedIds().add(secretAchievementId);
        gameUserService.updateItem(info);
        return rd;
    }
}
