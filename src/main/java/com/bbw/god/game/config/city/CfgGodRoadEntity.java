package com.bbw.god.game.config.city;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 特殊位置数据
 * 神仙随机出现的位置
 *
 * @author fzj
 * @date 2021/9/23 11:36
 */
@Data
public class CfgGodRoadEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    /** 位置 */
    private Integer id;
    /** 建筑名称 */
    private String name;
    @Override
    public int getSortId() {
        return this.getId();
    }
}
