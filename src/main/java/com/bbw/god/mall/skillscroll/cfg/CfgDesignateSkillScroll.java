package com.bbw.god.mall.skillscroll.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 指定合成范围配置
 * @date 2021/2/5 10:46
 **/
@Data
public class CfgDesignateSkillScroll implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer chapter;
    /** 可指定卷轴 */
    private List<Integer> ableDesignateIds;
    /** 不可指定卷轴 */
    private List<Integer> unableDesignateIds;

    /**
     * 获取排序号
     *
     * @return
     */
    @Override
    public int getSortId() {
        return this.id;
    }
}
