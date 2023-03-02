package com.bbw.god.activityrank;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 榜单生成规则
 *
 * @author suhq
 * @date 2020年02月11日
 */
@Data
public class CfgActivityRankGenerateRule implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id; //
    private List<Integer> loop; //循环
    private Integer beginDay; //起始天（比对开服时间）
    private Integer beginWeek; //开始周数
    private Integer endWeek; //结束周数
    private Integer duration;//持续天数
    private String endHms;//结束时对应的时分秒

    public boolean isEnableWeek(int weekToGenerate) {
        if (weekToGenerate >= this.beginWeek && weekToGenerate <= this.endWeek) {
            return true;
        }
        return false;
    }

    public int getFirstType() {
        return this.loop.get(0);
    }

    public int getNextType(int curType) {
        if (this.loop.size() == 1) {
            return this.loop.get(0);
        }
        int index = this.loop.indexOf(curType);
        if (index < this.loop.size() - 1) {
            index++;
        } else {
            index = 0;
        }
        return this.loop.get(index);
    }

    @Override
    public int getSortId() {
        return this.getId();
    }
}
