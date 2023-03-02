package com.bbw.god.gameuser.businessgang.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 商帮运送任务规则
 *
 * @author fzj
 * @date 2022/1/17 10:13
 */
@Data
public class CfgBusinessGangShippingTaskRules implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 运送特产规则 */
    private List<CfgSpecialsRules> specialsRules;
    /** 任务规则 */
    private List<CfgTaskRules> taskRules;
    /** 加急概率 */
    private Map<Integer, Integer> taskUrgent;
    /** 加急概率翻倍需要的声望 */
    private Integer urgentDoubleNeedPrestige;
    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
