package com.bbw.god.game;

import com.alibaba.fastjson.JSON;
import com.bbw.App;
import com.bbw.common.DateUtil;
import com.bbw.common.HttpClientNewUtil;
import com.bbw.common.ID;
import com.bbw.common.IpUtil;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.game.config.FileConfigMonitor;
import com.bbw.god.game.config.WayTool;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.game.health.CheckConfig;
import com.bbw.god.mall.lottery.LotteryService;
import com.bbw.god.road.RoadPathTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 富甲封神传游戏对象
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-13 22:29
 */
@Slf4j
@Service
public class GameService {

    @Autowired
    private CheckConfig checkConfig;
    @Value("${game-data-result-days:30}")
    private int prepareDays;// 提前生成多少天的结果数据
    @Value("${health-check-days:7}")
    private int checkDays;// 检查未来天数的数据完整性情况
    @Value("${maven.timestamp:2022-12-08 00:00:00}")
    private String buildTime;// 编译时间
    @Autowired
    private LotteryService lotteryService;

    @Autowired
    private App app;

    /**
     * 游戏初始化
     */
    public void init() {
        Date buildDateTime = DateUtil.fromDateTimeString(buildTime);
        Date buildTimeAsGmt8 = DateUtil.addHours(buildDateTime, 8);
        System.out.println("====================================================================================================================");
        System.out.println("                                                    富甲逻辑服-" + app.getActive());
        System.out.println("                                                    构建时间 " + DateUtil.toDateTimeString(buildTimeAsGmt8));
        System.out.println("====================================================================================================================");

        System.out.println("====================================================================================================================");
        System.out.println("【富甲封神传】游戏开始初始化...");
        System.out.println(" 逻辑服务器IP:" + IpUtil.getInet4Address());
        System.out.println(" ID生成器标识:" + ID.INSTANCE.getMachineId());
        long begin = System.currentTimeMillis();
        // 载入所有文件配置
        Cfg.I.loadAllConfig();
        TreasureTool.init();
        CardTool.init();
        CityTool.init();
        WayTool.init();
        RoadPathTool.I.init();
        System.out.println(" 配置文件载入完成....");
        FileConfigMonitor monitor = new FileConfigMonitor();
        try {
            monitor.start();
            System.out.println(" yml配置文件[" + this.getClass().getClassLoader().getResource(FileConfigMonitor.configDir).getPath() + "]目录监控设置完成....");
        } catch (Exception e) {
            log.error("文件配置目录监控启动失败！");
            log.error(e.getMessage(), e);
        }
        System.out.println(" 战斗校验规则载入完毕！");
        long end = System.currentTimeMillis();
        System.out.println("【恭喜你】️初始化完成。耗时:" + (end - begin) + "毫秒！");
        System.out.println("====================================================================================================================");
        System.out.println("检查并自动修复未来[" + checkDays + "]天的数据....");
        Date now = DateUtil.now();
        boolean success = checkConfig.check(now);
        for (int i = 0; i < checkDays; i++) {
            Date date = DateUtil.addDays(now, i + 1);
            boolean b = checkConfig.check(date);
            success = success && b;
        }
        System.out.println("加载并更新版本号");
        CfgGame cfgGame = Cfg.I.getUniqueConfig(CfgGame.class);
        String data = HttpClientNewUtil.doGet(cfgGame.getVersionConfigUrl());
        int newVersion = JSON.parseObject(data).getIntValue("version");
        cfgGame.setVersion(newVersion);
        System.out.println("检查完成！结果[" + success + "]!");
        // 检查奖券是否需要重新开奖（开奖到一半重启服务器导致开奖失败）
        lotteryService.checkSendAward();
        System.out.println("完成初始化和启动检查，耗时：" + (System.currentTimeMillis() - begin));
        System.out.println("------------------------------------------------------------------------------------------------------------");
        System.out.println("---------------------------------------- godLogic start successfully ---------------------------------------");
        System.out.println("------------------------------------------------------------------------------------------------------------");
    }

}
