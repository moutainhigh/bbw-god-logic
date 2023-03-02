package com.bbw.god.gameuser.yuxg;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 玩家玉虚宫自动祈福设置
 *
 * @author: huanghb
 * @date: 2022/8/17 9:39
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserYuXGPraySetting extends UserSingleObj {
    /** 玩家玉虚宫自动祈福设置 */
    private int userYuXGPraySetting = 0;

    public static UserYuXGPraySetting getInstance(long uid) {
        UserYuXGPraySetting setting = new UserYuXGPraySetting();
        setting.setGameUserId(uid);
        setting.setId(ID.INSTANCE.nextId());
        return setting;
    }

    /**
     * 更新玩家自动设置
     *
     * @param yuXGPraySeting
     */
    protected void updateUserYuXGPraySetting(int yuXGPraySeting) {
        //直到所选材料消耗完
        this.userYuXGPraySetting = yuXGPraySeting;
    }

    /**
     * 玩家资源类型
     *
     * @return
     */
    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_YUXG_PRAY_SETTING;
    }
}
