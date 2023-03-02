package com.bbw.god.gameuser.card.equipment.rd;

import com.bbw.god.gameuser.card.equipment.Enum.QualityEnum;
import com.bbw.god.gameuser.card.equipment.data.UserCardXianJue;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 仙诀信息
 *
 * @author: huanghb
 * @date: 2022/9/15 10:22
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RdCardXianJueInfo extends RDCommon {
    private static final long serialVersionUID = 1366511178569740530L;
    /** 仙诀数据id */
    private Long xianJueDataId;
    /** 仙诀类型 */
    private Integer xianJueType;
    /** 卡牌id */
    private Integer cardId;
    /** 强化等级 */
    private Integer level = 0;
    /** 品质 */
    private Integer quality = QualityEnum.NONE.getValue();
    /** 星图进度 */
    private Integer starMapProgress = 0;
    /** 参悟值 */
    private List<RdComprehendInfo> comprehendValue;

    public static RdCardXianJueInfo instance(UserCardXianJue xianJueInfo) {
        RdCardXianJueInfo info = new RdCardXianJueInfo();
        info.setXianJueType(xianJueInfo.getXianJueType());
        info.setLevel(xianJueInfo.getLevel());
        info.setQuality(xianJueInfo.getQuality());
        info.setStarMapProgress(xianJueInfo.getStarMapProgress());
        //获得参悟值信息
        List<RdComprehendInfo> rdCardXianJueInfos = xianJueInfo.gainAdditions()
                .stream().map(RdComprehendInfo::instance).collect(Collectors.toList());

        info.setComprehendValue(rdCardXianJueInfos);
        info.setCardId(xianJueInfo.getCardId());
        info.setXianJueDataId(xianJueInfo.getId());
        return info;

    }


}
