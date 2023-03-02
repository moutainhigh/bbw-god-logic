package com.bbw.god.game.config.special;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 特产阶级对应关系
 *
 * @author: huanghb
 * @date: 2022/7/26 16:52
 */
@Data
public class CfgSpecialHierarchyMap implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    /** 源特产id*/
    private int fromSpecialId;
    /** 进阶后特产id */
    private int toSpecialId;

    /**
     * 获取配置项到ID值
     *
     * @return
     */
    @Override
    public Serializable getId() {
        return fromSpecialId;
    }

    @Override
    public int getSortId() {
        return 1;
    }


}
