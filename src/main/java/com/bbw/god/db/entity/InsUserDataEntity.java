package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.JSONUtil;
import com.bbw.god.gameuser.UserData;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 玩家相关数据
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2019-02-27 09:44:18
 */
@Slf4j
@Data
@TableName("ins_user_data")
public class InsUserDataEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.INPUT)
    private Long dataId; // 资源ID
    private Integer sid; // 区服ID
    private Long uid; // 玩家区服ID
    private String dataType; // 资源类型
    private String dataJson; // 资源JSON

    public static InsUserDataEntity fromUserData(UserData res, CfgServerEntity server) {
        InsUserDataEntity entity = new InsUserDataEntity();
        entity.setDataId(res.getId());
        entity.setUid(res.getGameUserId());
        entity.setDataType(res.gainResType().getRedisKey());
        entity.setDataJson(JSONUtil.toJson(res));
        entity.setSid(server.getMergeSid());
        return entity;
    }
}
