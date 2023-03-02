package com.bbw.god.gameuser.task.businessgang;

import com.bbw.god.game.config.Cfg;
import com.bbw.god.gameuser.businessgang.cfg.CfgBusinessGangShippingTaskRules;

/**
 * 运送任务工具类
 *
 * @author fzj
 * @date 2022/1/18 16:45
 */
public class SpecialtyShippingTaskTool {

    /**
     * 获取运送任务规则
     *
     * @return
     */
    public static CfgBusinessGangShippingTaskRules getRules() {
        return Cfg.I.getUniqueConfig(CfgBusinessGangShippingTaskRules.class);
    }
}
