package com.bbw.god.gameuser.card.equipment.rd;

import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参悟信息集合
 *
 * @author: huanghb
 * @date: 2022/9/15 10:22
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RdComprehendInfos extends RDCommon {
    private static final long serialVersionUID = -6407403326202663339L;
    /** 参悟值 */
    private List<RdComprehendInfo> comprehendValues;

    /**
     * 初始化
     *
     * @param comprehendValues
     * @return
     */
    public static RdComprehendInfos instance(List<CardEquipmentAddition> comprehendValues) {
        RdComprehendInfos rd = new RdComprehendInfos();
        List<RdComprehendInfo> infos = new ArrayList<>();
        infos.addAll(comprehendValues.stream().map(RdComprehendInfo::instance).collect(Collectors.toList()));
        rd.setComprehendValues(infos);
        return rd;

    }


}
