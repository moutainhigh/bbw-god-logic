package com.bbw.god.game.config.city;

import com.bbw.god.game.config.CfgEntityInterface;
import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 法坛基础数据
 *
 * @author fzj
 * @date 2021/11/11 9:22
 */
@Data
public class CfgFaTanEntity implements CfgEntityInterface, Serializable{
    private static final long serialVersionUID = 1L;
    private String key;
    /** 解锁法坛需要的铜钱 */
    private Integer unlockFaTanNeedCopper;
    /** 解锁法坛需要的法宝 */
    private Integer unlockFaTanNeedTreasureId;
    /** 解锁法坛需要的法宝数量 */
    private Integer unlockFaTanNeedTreasureNum;
    /** 升级法坛 */
    private List<CfgUpgradeFaTan> upgradeFaTan;

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
