package com.bbw.god.gameuser.yuxg.rd;

import com.bbw.god.gameuser.yuxg.UserFuCe;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 单个符册信息
 *
 * @author fzj
 * @date 2021/11/16 17:34
 */
@Data
public class RDFuCe extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 998187098802155732L;
    private long dataId;
    private Integer fuCeId;
    private String name;
    /** 开启方式  0为默认开启 1为累计等级开启 2为元宝开启 */
    private Integer openMethod;
    private Integer allQuality;
    private List<RDYuXGFuCes.RDFuTu> fuTus = new ArrayList<>();

    public static RDFuCe getInstance(UserFuCe userFuCe, int fuCeQuality, List<RDYuXGFuCes.RDFuTu> rdFuTus){
        RDFuCe rd = new RDFuCe();
        rd.setDataId(userFuCe.getId());
        rd.setFuCeId(userFuCe.getBaseId());
        rd.setName(userFuCe.getName());
        rd.setFuTus(rdFuTus);
        rd.setAllQuality(fuCeQuality);
        rd.setOpenMethod(userFuCe.getOpenMethod());
        return rd;
    }
}
