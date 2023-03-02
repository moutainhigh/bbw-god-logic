package com.bbw.god.rechargeactivities.wartoken;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 战令大奖配置
 *
 * @author fzj
 * @date 2022/2/15 10:05
 */
@Data
public class CfgWarTokenBigAwards implements CfgEntityInterface, Serializable {
    private String key;
    /** 基础大奖 */
    private List<Award> baseBigAwards;
    /** 进阶大奖 */
    private List<Award> supBigAwards;

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
