package com.bbw.god.db.entity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.god.game.data.GameDayData;
import lombok.Data;

import java.io.Serializable;

/**
 * 全服每日相关数据
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2019-03-20 09:07:08
 */
@Data
@TableName("ins_game_day_data")
public class InsGameDayDataEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.INPUT)
    private Long dataId; //数据ID
    private String dataType; //数据类型
    private Integer dateInt; //日期的数字格式
    private String dataJson; //资源JSON

    public static InsGameDayDataEntity fromGameDayData(GameDayData data) {
        InsGameDayDataEntity entity = new InsGameDayDataEntity();
        entity.setDataId(data.getId());
        entity.setDateInt(data.getDateInt());
        entity.setDataType(data.gainDataType().getRedisKey());
        entity.setDataJson(JSON.toJSONString(data));
        return entity;
    }
}
