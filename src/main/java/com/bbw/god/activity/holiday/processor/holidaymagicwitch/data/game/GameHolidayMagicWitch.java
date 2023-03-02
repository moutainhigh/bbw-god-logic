package com.bbw.god.activity.holiday.processor.holidaymagicwitch.data.game;

import com.bbw.common.ID;
import com.bbw.god.activity.holiday.processor.holidaymagicwitch.DrawResult;
import com.bbw.god.cache.tmp.AbstractTmpData;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 魔法女巫 全服数据
 *
 * @author: huanghb
 * @date: 2022/12/13 9:41
 */
@Data
public class GameHolidayMagicWitch extends AbstractTmpData implements Serializable {
    private static final long serialVersionUID = -8767590270861668523L;
    /** 排序记录 */
    private List<DrawResult> records = new ArrayList<>();

    /**
     * 构建实例
     *
     * @param records
     * @return
     */
    public static GameHolidayMagicWitch getInstance(List<DrawResult> records) {
        GameHolidayMagicWitch instance = new GameHolidayMagicWitch();
        instance.setId(ID.INSTANCE.nextId());
        instance.setRecords(records);
        return instance;
    }
}
