package com.bbw.god.game.config;

import com.bbw.god.game.award.Award;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 说明：
 * 仙家宝袋配置
 * @author lwb
 * date 2021-06-01
 */
@Data
public class CfgXianJia implements CfgEntityInterface, Serializable {
    private Integer id;
    private List<Award> awards;
    @Override
    public int getSortId() {
        return this.getId();
    }
}
