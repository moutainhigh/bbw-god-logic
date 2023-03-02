package com.bbw.god.gameuser.yuxg;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 玩家符册数据
 *
 * @author fzj
 * @date 2021/10/29 15:50
 */
@Data
public class UserFuCe extends UserCfgObj implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 符册名称 */
    private String name;
    /** 开启方式  0为默认开启 1为累计等级开启 2为元宝开启 */
    private Integer openMethod;
    /** 装备的符图 */
    private List<FuTu> fuTus;

    public static UserFuCe getInstance(long uid, String fuCeName, int openMethod, int fuCeId, Integer fuTuSlotNum) {
        UserFuCe userFuCe = new UserFuCe();
        userFuCe.setId(ID.INSTANCE.nextId());
        userFuCe.setBaseId(fuCeId);
        userFuCe.setGameUserId(uid);
        userFuCe.setName(fuCeName);
        userFuCe.setOpenMethod(openMethod);
        List<FuTu> runes = new ArrayList<>();
        for (int pos = 0; pos < fuTuSlotNum; pos++) {
            FuTu fuTu = new FuTu();
            fuTu.setDataId((long) 0);
            fuTu.setPos(pos);
            runes.add(fuTu);
        }
        userFuCe.setFuTus(runes);
        return userFuCe;
    }

    /**
     * 入册的符图
     */
    @Data
    public static class FuTu implements Serializable {
        private static final long serialVersionUID = -4216527940689976757L;
        private Long dataId;
        private Integer pos;

        public static FuTu getInstance(int pos) {
            FuTu fuTu = new FuTu();
            fuTu.setDataId(0L);
            fuTu.setPos(pos);
            return fuTu;
        }
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_YUXG_FUCE;
    }
}
