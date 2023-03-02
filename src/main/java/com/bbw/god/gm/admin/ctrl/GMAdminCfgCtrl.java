package com.bbw.god.gm.admin.ctrl;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.gm.admin.CRAdmin;
import com.bbw.god.gm.admin.CfgAdminType;
import com.bbw.god.gm.admin.RDConfig;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台接口-配置相关操作
 *
 * @author：lzc
 * @date: 2021/03/17 11:28
 * @version: 1.0
 */
@RequestMapping("/gm/admin")
@RestController
public class GMAdminCfgCtrl {

    /**
     * 获取配置列表
     *
     * @param key
     */
    @RequestMapping(CRAdmin.Cfg.GET_CONFIG)
    public RDConfig getConfig(String key) {
        CfgAdminType cacheEnum = CfgAdminType.fromClass(key);
        if (cacheEnum == null) {
            throw ExceptionForClientTip.fromMsg("无效的配置类型");
        }
        RDConfig rd = new RDConfig();
        rd.setCfgEntities(Cfg.I.get(cacheEnum.getEntityClass()));
        return rd;
    }
}
