package com.bbw.god.activity.holiday.processor.holidaymagicwitch.data.user;

import com.bbw.common.ID;
import com.bbw.god.activity.holiday.processor.holidaymagicwitch.DrawResult;
import com.bbw.god.cache.tmp.AbstractTmpData;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 魔法女巫 个人
 *
 * @author: huanghb
 * @date: 2022/12/13 9:41
 */
@Data
public class UserHolidayMagicWitch extends AbstractTmpData implements Serializable {
    private static final long serialVersionUID = -8767590270861668523L;
    private long gameUserId;
    /** 排序记录 */
    private List<DrawResult> records = new ArrayList<>();

    /**
     * 构建实例
     *
     * @param uid
     * @return
     */
    public static UserHolidayMagicWitch getInstance(long uid) {
        UserHolidayMagicWitch instance = new UserHolidayMagicWitch();
        instance.setGameUserId(uid);
        instance.setId(ID.INSTANCE.nextId());
        return instance;
    }

    /**
     * 添加记录
     *
     * @param record 本次排序
     */
    public void addRecord(List<Integer> record) {
        this.records.add(DrawResult.getInstance(record));
    }

}
