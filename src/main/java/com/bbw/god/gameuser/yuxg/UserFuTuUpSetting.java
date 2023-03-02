package com.bbw.god.gameuser.yuxg;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 符册升级设置状态
 *
 * @author fzj
 * @date 2021/11/2 17:39
 */
@Data
public class UserFuTuUpSetting extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 设置标识 */
    private Map<String, Integer> settings = new HashMap<>();

    public static UserFuTuUpSetting getInstance(long uid) {
        UserFuTuUpSetting setting = new UserFuTuUpSetting();
        setting.setId(ID.INSTANCE.nextId());
        setting.setGameUserId(uid);
        Map<String, Integer> settings = new HashMap<>();
        for (int index = 0; index < 5; index++) {
            settings.put("FT" + index, 0);
        }
        for (int index = 0; index < 5; index++) {
            settings.put("YS" + index, 0);
        }
        setting.setSettings(settings);
        return setting;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_YUXG_FUCE_UPSETTING;
    }
}
