package com.bbw.god.rechargeactivities.wartoken;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-02
 */
@Data
public class CfgWarTokenLevelAward implements CfgEntityInterface, Serializable {
    private Integer tokenLevel;
    /**
     * 基础奖励
     */
    private List<Award> baseAwards;
    /**
     * 进阶奖励
     */
    private List<Award> supAwards;

    private Integer status;

    @Override
    public Integer getId() {
        return this.tokenLevel;
    }

    @Override
    public int getSortId() {
        return this.tokenLevel;
    }
}
