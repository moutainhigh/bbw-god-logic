package com.bbw.god.activity.config;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 仙人祝福奖励配置
 * @date 2020/10/19 10:39
 **/
@Data
public class CfgGodBlessAward implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer type;
    private Integer index;
    private List<Award> awards;

    /**
     * 获取排序号
     *
     * @return
     */
    @Override
    public int getSortId() {
        return this.getId();
    }
}
