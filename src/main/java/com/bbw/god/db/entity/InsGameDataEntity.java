package com.bbw.god.db.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.god.game.data.GameData;
import lombok.Data;

import java.io.Serializable;

/**
 * 全服相关数据
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2019-03-20 09:07:08
 */
@Data
@TableName("ins_game_data")
public class InsGameDataEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.INPUT)
    private Long dataId; // 数据ID
    private String dataType; // 数据类型
    // private Integer dateInt; //日期的数字格式
    private String dataJson; // 资源JSON

    public static InsGameDataEntity fromGameData(GameData data) {
        InsGameDataEntity entity = new InsGameDataEntity();
        entity.setDataId(data.getId());
        entity.setDataType(data.gainDataType().getRedisKey());
        entity.setDataJson(JSON.toJSONString(data, SerializerFeature.WriteDateUseDateFormat));
        return entity;
    }
}
