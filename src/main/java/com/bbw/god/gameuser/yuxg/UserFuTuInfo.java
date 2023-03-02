package com.bbw.god.gameuser.yuxg;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 在玉虚宫获得符图的信息
 *
 * @author fzj
 * @date 2021/11/16 11:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserFuTuInfo extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 在玉虚宫已获得的符图基础ID */
    private List<Integer> fuTuBaseIds = new ArrayList<>();

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_YUXG_FUTU_INFO;
    }

    public static UserFuTuInfo getInstance(long uid) {
        UserFuTuInfo instance = new UserFuTuInfo();
        instance.setId(ID.INSTANCE.nextId());
        instance.setGameUserId(uid);
        return instance;
    }

    /**
     * 在玉虚宫添加已获得的符图基础ID
     *
     * @param fuTuBaseId
     */
    public void addFuTuId(int fuTuBaseId) {
        if (fuTuBaseIds.contains(fuTuBaseId)) {
            return;
        }
        fuTuBaseIds.add(fuTuBaseId);
    }
}
