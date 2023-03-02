package com.bbw.god.gameuser.biyoupalace;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.gameuser.biyoupalace.rd.RDEnterBYPalace;
import com.bbw.god.gameuser.biyoupalace.rd.RDExcludeInfo;
import com.bbw.god.gameuser.biyoupalace.rd.RDRealization;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 碧游宫请求接口
 *
 * @author suhq
 * @date 2019-09-09 09:41:50
 */
@RestController
public class BYPalaceCtrl extends AbstractController {
    @Autowired
    private BYPalaceLogic byPalaceLogic;

    /** 进入碧游宫 */
    @GetMapping(CR.BYPalace.ENTER_BYPALACE)
    public RDEnterBYPalace enterBYPalace() {
        return byPalaceLogic.enterBYPalace(getUserId());
    }

    /** 获得领悟奖励 */
    @GetMapping(CR.BYPalace.GET_AWARDS)
    public RDCommon getAwards(int chapterType, int chapter) {
        return byPalaceLogic.getAward(getUserId(), chapterType, chapter);
    }

    /** 刷新奖励 */
    @GetMapping(CR.BYPalace.REFRESH_AWARDS)
    public RDCommon refreshAwards(String awardsToLock) {
        return byPalaceLogic.refresh(getUserId(), awardsToLock);
    }

    /** 重置 */
    @GetMapping(CR.BYPalace.RESET)
    public RDCommon reset() {
        return byPalaceLogic.reset(getUserId());
    }

    /** 领悟 */
    @GetMapping(CR.BYPalace.REALIZATION)
    public RDRealization realization(int chapterType, int chapter, int isAutoBuy) {
        return byPalaceLogic.realization(getUserId(), chapterType, chapter, isAutoBuy == 1);
    }

    @GetMapping(CR.BYPalace.GET_EXCLUDE_INFO)
    public RDExcludeInfo getExcludeInfo(int chapter) {
        return byPalaceLogic.getExcludeInfo(getUserId(), chapter);
    }

    @GetMapping(CR.BYPalace.CHOOSE_EXCLUDE_SKILLS)
    public RDCommon chooseExcludeSkills(int chapter, String excludes, int useTTLPNum) {
        return byPalaceLogic.chooseExcludeSkills(getUserId(), chapter, excludes, useTTLPNum);
    }

}
