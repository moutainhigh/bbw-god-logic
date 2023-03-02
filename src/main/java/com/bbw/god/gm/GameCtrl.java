package com.bbw.god.gm;

import com.bbw.App;
import com.bbw.common.Rst;
import com.bbw.god.activity.game.GameActivityGeneratorService;
import com.bbw.god.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 游戏全局相关服务
 *
 * @author suhq
 * @date 2019年4月13日 下午1:49:45
 */
@RestController
@RequestMapping("/gm")
public class GameCtrl extends AbstractController {
    @Autowired
    private GameActivityGeneratorService gameActivityGeneratorService;
    @Autowired
    private App app;

    /**
     * 初始化全服活动
     *
     * @return
     */
    @RequestMapping("game!initActivity")
    public Rst initActivity() {
        if (!app.runAsDev()) {
            return Rst.businessFAIL("只有dev模式下才可调用");
        }
        // 初始化活动
        gameActivityGeneratorService.initGameActivity();
        return Rst.businessOK();
    }

}
