package com.bbw.god.game.zxz.job;

import com.bbw.god.game.zxz.service.InitZxzEnemyService;
import com.bbw.god.game.zxz.service.foursaints.InitZxzFourSaintsService;
import com.bbw.god.job.game.GameJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 每周一 0点自动生成敌方配置
 * @author: hzf
 * @create: 2022-09-27 08:50
 **/
@Component("refreshEnemyJob")
public class RefreshEnemyJob extends GameJob {

    @Autowired
    private InitZxzEnemyService initZxzEnemyService;
    @Autowired
    private InitZxzFourSaintsService initZxzFourSaintsService;

    @Override
    public String getJobDesc() {
        return "每周一 0点自动生成敌方配置";
    }

    @Override
    public void job() {
        //普通模式
        initZxzEnemyService.initZxzEnemyConfig();
        //四圣模式
        initZxzFourSaintsService.initZxzFourSaints();
    }
}
