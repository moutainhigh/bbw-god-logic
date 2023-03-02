package com.bbw.god.game.config.city;

import com.bbw.god.game.config.CfgBuff;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.util.List;

/**
 * 梦魇城池相关配置
 *
 * @author suchaobin
 * @date 2020-08-06 09:17:39
 */
@Data
public class CfgNightmareChengC implements CfgInterface {
    private String key;
    /** 梦魇城池配置 */
    private List<ChengC> chengCs;
    /** 梦魇奖励 */
    private List<CfgNightmareAward> nightmareAwards;
    /** buff加成 */
    private List<CfgBuff> buffAdds;

    @Override
    public String getId() {
        return this.key;
    }

    @Override
    public int getSortId() {
        return 1;
    }

    @Data
    public static class CfgNightmareAward {
        private int minOwnNum;
        private int maxOwnNum;
        private List<Award> awards;
    }
}
