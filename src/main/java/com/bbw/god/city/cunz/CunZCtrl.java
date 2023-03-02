package com.bbw.god.city.cunz;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.rd.RDCommon;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 村庄
 *
 * @author fzj
 * @date 2021/12/6 11:15
 */
@Api(tags = {"村庄接口"})
@RestController
public class CunZCtrl extends AbstractController {
    @Autowired
    CunZLogic cunZLogic;

    @ApiOperation(value = "验证怪谈")
    @GetMapping(CR.CunZ.VERIFY_TALK)
    public RDCommon verifyTalk(Integer cunZTalkId, Integer secretAchievementId) {
        return cunZLogic.verifyTalk(getUserId(), cunZTalkId, secretAchievementId);
    }
}
