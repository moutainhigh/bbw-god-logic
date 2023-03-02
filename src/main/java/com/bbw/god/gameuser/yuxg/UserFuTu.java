package com.bbw.god.gameuser.yuxg;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

import java.io.Serializable;

/**
 * 玩家符图数据
 *
 * @author fzj
 * @date 2021/10/29 11:26
 */
@Data
public class UserFuTu extends UserCfgObj implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 符图等级 */
    private Integer lv;
    /** 当前经验 */
    private Long exp;
    /** 符图状态 0 默认；1 受保护；2入册 */
    private Integer status;

    public static UserFuTu getInstance(long uid, int fuTuId) {
        UserFuTu userFuTu = new UserFuTu();
        userFuTu.setId(ID.INSTANCE.nextId());
        userFuTu.setGameUserId(uid);
        userFuTu.setBaseId(fuTuId);
        userFuTu.setLv(0);
        userFuTu.setExp(0L);
        userFuTu.setStatus(0);
        return userFuTu;
    }
    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_YUXG_FUTU;
    }
}
