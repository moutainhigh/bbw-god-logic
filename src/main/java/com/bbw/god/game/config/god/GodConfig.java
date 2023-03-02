package com.bbw.god.game.config.god;

import com.bbw.common.SpringContextUtil;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 神仙相关配置
 *
 * @author suhq
 * @date 2018年11月14日 下午3:08:39
 */
@Getter
@Configuration
@Component
public class GodConfig {
    // #小财神50步内战斗金币收益+50%
    private final Integer xcsCopperAddRate = 50;
    // 大财神50步内战斗金币收益翻倍
    private final Integer dcsCopperAddRate = 100;
    // 穷神50步内战斗获取金钱-20%
    private final Integer qsCopperAddRate = -20;
    // 小福神20次行动内高兴卡牌掉率+25%
    private final Integer xfsCardDropRate = 25;
    // 大福神20次行动内高兴卡牌掉率+50%
    private final Integer dfsCardDropRate = 50;
    // 衰神20次行动内高兴卡牌掉率-25%
    private final Integer ssCardDropRate = -25;

    private final Integer virtualDate = 20000101;

    public static GodConfig bean() {
        return SpringContextUtil.getBean(GodConfig.class);
    }

}
