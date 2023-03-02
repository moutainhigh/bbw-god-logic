package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.god.statistics.CardSkillStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设置本地技能缓存
 *
 * @author fzj
 * @date 2022/4/5 11:00
 */
@RestController
@RequestMapping("/gm/cardSkill/")
public class GMSetCardSkillCacheCtrl {
    @Autowired
    CardSkillStatisticService cardSkillStatisticService;

    @RequestMapping("game!setCardSkill")
    public Rst setCardSkillCache() {
        cardSkillStatisticService.setCardSkillCache();
        return Rst.businessOK();
    }
}
