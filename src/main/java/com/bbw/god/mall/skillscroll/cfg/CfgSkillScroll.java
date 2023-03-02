package com.bbw.god.mall.skillscroll.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 卷轴合成配置
 * @date 2021/2/3 14:46
 **/
@Data
public class CfgSkillScroll implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    // 被合成的卷轴的篇章
    private Integer chapter;
    // 支持的合成类型
    private List<Integer> supportSynthesisType;
    // 3本相同卷轴合成随机高篇卷轴的成功概率
    private Integer probability;

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
