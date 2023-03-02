package com.bbw.god.gm;

import com.bbw.App;
import com.bbw.common.IpUtil;
import com.bbw.common.Rst;
import com.bbw.common.StrUtil;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.game.limit.GameBlackIpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * ip管理黑白名单
 *
 * @author suhq
 * @date 2020-11-11 12:11
 **/
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMIpCtrl extends AbstractController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private GameBlackIpService gameBlackIpService;
    @Autowired
    private App app;

    /**
     * 仅限39.108.136.120访问
     * 添加的白名单重启服务后失效
     *
     * @param ip
     * @return
     */
    @RequestMapping("white!addIp")
    public Rst reloadServer(String ip) {
        String checkResult = checkIsAbleAccess();
        if (StrUtil.isNotBlank(checkResult)) {
            return Rst.businessFAIL(checkResult);
        }
        if (ip.split("\\.").length != 4) {
            return Rst.businessFAIL("无效的ip格式");
        }
        try {
            IpUtil.ipToLong(ip);
        } catch (Exception e) {
            return Rst.businessFAIL("无效的ip格式");
        }
        CfgGame cfgGame = Cfg.I.getUniqueConfig(CfgGame.class);
        cfgGame.getGmWhiteIps().add(ip);
        return Rst.businessOK();
    }

    /**
     * 黑名单ip管理
     *
     * @param ip
     * @param ban 0 解封 1 封禁
     * @return
     */
    @RequestMapping("ip!managerBlack")
    public Rst managerBlack(String ip, int ban) {
        try {
            checkIsAbleAccess();
            if (0 == ban) {
                gameBlackIpService.removeBlackIp(ip);
            } else {
                gameBlackIpService.addBlackIp(ip);
            }
            return Rst.businessOK();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Rst.businessFAIL(e.getMessage());
        }
    }

    private String checkIsAbleAccess() {
        String ip = IpUtil.getIpAddr(request);
        CfgGame cfg = Cfg.I.getUniqueConfig(CfgGame.class);
        if (!(cfg.getGmWhiteIps().contains(ip) || app.runAsDev() || app.runAsTest())) {
            return "没有访问该接口的权限";
        }
        return "";
    }

}
