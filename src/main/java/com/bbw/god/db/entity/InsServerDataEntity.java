package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.JSONUtil;
import com.bbw.god.server.ServerData;
import lombok.Data;

import java.io.Serializable;

/**
 * 区服数据
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2019-02-27 09:44:18
 */
@Data
@TableName("ins_server_data")
public class InsServerDataEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.INPUT)
    private Long dataId; //资源ID
    private Integer sid; //区服ID
    private String dataType; //资源类型
    private String dataJson; //资源JSON

    public static InsServerDataEntity fromServerData(ServerData data) {
        InsServerDataEntity entity = new InsServerDataEntity();
        entity.setDataId(data.getId());
        entity.setDataType(data.gainDataType().getRedisKey());
        entity.setDataJson(JSONUtil.toJson(data));
        entity.setSid(data.getSid());
        return entity;
    }
}
