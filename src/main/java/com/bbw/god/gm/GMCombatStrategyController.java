package com.bbw.god.gm;

import com.bbw.common.DateUtil;
import com.bbw.common.Rst;
import com.bbw.god.game.combat.attackstrategy.StrategySourceEnum;
import com.bbw.god.game.combat.attackstrategy.service.StrategyRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-05-08
 */
@RequestMapping("/gm/")
@RestController
public class GMCombatStrategyController {
    @Autowired
    private StrategyRedisService strategyRedisService;
    /**
     * 清理指定时间以前的旧数据
     * @param gid 平台
     * @param isNightmare 是否是梦魇
     * @param endDate 截止时间
     * @return
     */
    @RequestMapping("combatStrategy!clear")
    public Rst clear(int gid,Integer isNightmare,String endDatetime) {
        Date date = DateUtil.fromDateTimeString(endDatetime);
        boolean b = isNightmare != null && isNightmare == 1;
        StrategySourceEnum strategySource = b ? StrategySourceEnum.NIGHTMARE_ATTACK_CITY : StrategySourceEnum.FSDL_ATTACK_CITY;
        strategyRedisService.clearOldData(gid, date, strategySource);
        return Rst.businessOK("清理成功！");
    }
}
