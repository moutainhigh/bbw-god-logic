package com.bbw.god.gameuser.yuxg.rd;

import com.bbw.god.game.config.treasure.TreasureType;
import com.bbw.god.gameuser.yuxg.UserFuTu;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 符图信息
 *
 * @author: suhq
 * @date: 2021/10/21 11:32 上午
 */
@Data
public class RDYuXGFuTu implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long dataId;
    private Integer fuTuId;
    private Integer level;
    private Long exp;
    /** 符图状态 0 默认；1 受保护；2入册 */
    private Integer status;
    private Integer type;

    public static RDYuXGFuTu getInstance(UserFuTu userFuTu) {
        RDYuXGFuTu rd = new RDYuXGFuTu();
        rd.setDataId(userFuTu.getId());
        rd.setFuTuId(userFuTu.getBaseId());
        rd.setLevel(userFuTu.getLv());
        rd.setExp(userFuTu.getExp());
        rd.setStatus(userFuTu.getStatus());
        rd.setType(TreasureType.FUTU.getValue());
        return rd;
    }

    public static List<RDYuXGFuTu> getInstance(List<UserFuTu> userAllFuTus) {
        List<RDYuXGFuTu> rdYuXGFuTus = new ArrayList<>();
        for (UserFuTu userFuTu : userAllFuTus) {
            RDYuXGFuTu rdYuXGFuTu = RDYuXGFuTu.getInstance(userFuTu);
            rdYuXGFuTus.add(rdYuXGFuTu);
        }
        return rdYuXGFuTus;
    }
}
