package com.bbw.god.gameuser.yuxg.rd;

import com.bbw.god.gameuser.yuxg.UserFuCe;
import com.bbw.god.gameuser.yuxg.UserFuTu;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 符册列表
 *
 * @author: suhq
 * @date: 2021/10/19 3:48 下午
 */
@Data
public class RDYuXGFuCes extends RDSuccess {
    /** 符册列表 */
    private List<RDFuCes> fuCes;
    private Integer faTanLv;
    private Integer meltValue;

    /**
     * 符册
     *
     * @author: suhq
     * @date: 2021/10/21 2:31 下午
     */
    @Data
    public static class RDFuCes extends RDSuccess implements Serializable {
        private static final long serialVersionUID = 998187098802155732L;
        private long dataId;
        private Integer fuCeId;
        private String name;
        /** 开启方式  0为默认开启 1为累计等级开启 2为元宝开启 */
        private Integer openMethod;
        private Integer allQuality;
        private List<RDFuTu> fuTus = new ArrayList<>();
    }

    /**
     * 入册的符图
     *
     * @author: suhq
     * @date: 2021/10/21 2:35 下午
     */
    @Data
    public static class RDFuTu implements Serializable {
        private static final long serialVersionUID = -4216527940689976757L;
        private Long dataId;
        private Integer fuTuId;
        private Integer fuTuLv;
        private Integer pos;
        private long fuTuExp;


        public static RDFuTu getInstance(UserFuCe.FuTu fuTu, UserFuTu userFuTu) {
            RDFuTu rdFuTu = new RDFuTu();
            rdFuTu.setDataId(fuTu.getDataId());
            rdFuTu.setPos(fuTu.getPos());
            rdFuTu.setFuTuId(userFuTu.getBaseId());
            rdFuTu.setFuTuLv(userFuTu.getLv());
            rdFuTu.setFuTuExp(userFuTu.getExp());
            return rdFuTu;
        }

        public static RDFuTu getInstance(UserFuCe.FuTu fuTu) {
            RDFuTu rdFuTu = new RDFuTu();
            rdFuTu.setDataId(0L);
            rdFuTu.setPos(fuTu.getPos());
            rdFuTu.setFuTuId(0);
            rdFuTu.setFuTuLv(0);
            rdFuTu.setFuTuExp(0);
            return rdFuTu;
        }
    }

    public static RDFuCes getInstance(UserFuCe userFuCe, int fuCeQuality, List<RDFuTu> rdFuTus) {
        RDFuCes rdFuCes = new RDFuCes();
        rdFuCes.setDataId(userFuCe.getId());
        rdFuCes.setFuCeId(userFuCe.getBaseId());
        rdFuCes.setName(userFuCe.getName());
        rdFuCes.setFuTus(rdFuTus);
        rdFuCes.setAllQuality(fuCeQuality);
        rdFuCes.setOpenMethod(userFuCe.getOpenMethod());
        return rdFuCes;
    }


}
