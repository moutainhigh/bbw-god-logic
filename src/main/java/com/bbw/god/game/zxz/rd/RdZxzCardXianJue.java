package com.bbw.god.game.zxz.rd;

import com.bbw.common.ListUtil;
import com.bbw.god.game.config.card.equipment.randomrule.CardXianJueRandomRule;
import com.bbw.god.game.zxz.entity.UserZxzCardXianJue;
import com.bbw.god.gameuser.card.equipment.Enum.QualityEnum;
import com.bbw.god.gameuser.card.equipment.rd.RdCardXianJueInfo;
import com.bbw.god.gameuser.card.equipment.rd.RdComprehendInfo;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 诛仙阵返回仙决信息
 * @author: hzf
 * @create: 2022-10-11 08:47
 **/
@Data
public class RdZxzCardXianJue  extends RDSuccess {
    private static final long serialVersionUID = 1366511178569740530L;

    /** 仙诀类型 */
    private Integer xianJueType;
    /** 强化等级 */
    private Integer level = 0;
    /** 品质 */
    private Integer quality = QualityEnum.NONE.getValue();
    /** 参悟值 */
    private List<RdComprehendInfo> comprehendValue;

    public static List<RdZxzCardXianJue> instance(List<UserZxzCardXianJue> xianJues) {
        List<RdZxzCardXianJue> xianJueInfos = new ArrayList<>();
        if (ListUtil.isEmpty(xianJues)) {
             return xianJueInfos;
        }
        for (UserZxzCardXianJue xianJue : xianJues) {
            RdZxzCardXianJue rd = new RdZxzCardXianJue();
            rd.setXianJueType(xianJue.getXianJueType());
            rd.setLevel(xianJue.getLevel());
            rd.setQuality(xianJue.getQuality());
            //获得参悟值信息
            List<RdComprehendInfo> rdCardXianJueInfos = xianJue.gainAdditions()
                    .stream().map(RdComprehendInfo::instance).collect(Collectors.toList());

            rd.setComprehendValue(rdCardXianJueInfos);
            xianJueInfos.add(rd);
        }

        return xianJueInfos;

    }
    public static List<RdZxzCardXianJue> instanceEnemy(List<CardXianJueRandomRule> xianJues) {
        List<RdZxzCardXianJue> xianJueInfos = new ArrayList<>();
        if (ListUtil.isEmpty(xianJues)) {
            return xianJueInfos;
        }
        for (CardXianJueRandomRule xianJue : xianJues) {
            RdZxzCardXianJue rd = new RdZxzCardXianJue();
            rd.setXianJueType(xianJue.getXianJueType());
            rd.setLevel(xianJue.getLevel());
            rd.setQuality(xianJue.getQuality());
            //获得参悟值信息
            List<RdComprehendInfo> rdCardXianJueInfos = xianJue.gainAdditions()
                    .stream().map(RdComprehendInfo::instance).collect(Collectors.toList());

            rd.setComprehendValue(rdCardXianJueInfos);
            xianJueInfos.add(rd);
        }

        return xianJueInfos;

    }
    /**
     * 返回仙决信息
     * @return
     */
    public static List<RdCardXianJueInfo> gainRdCardXianJues(List<UserZxzCardXianJue> xianJues){
        List<RdCardXianJueInfo> rdCardXianJueInfos = new ArrayList<>();
        List<RdZxzCardXianJue> rdZxzCardXianJues = RdZxzCardXianJue.instance(xianJues);
        for (RdZxzCardXianJue zxzCardXianJue : rdZxzCardXianJues) {
            RdCardXianJueInfo rd = new RdCardXianJueInfo();
            rd.setXianJueType(zxzCardXianJue.getXianJueType());
            rd.setLevel(zxzCardXianJue.getLevel());
            rd.setQuality(zxzCardXianJue.getQuality());
            rd.setComprehendValue(zxzCardXianJue.getComprehendValue());
            rdCardXianJueInfos.add(rd);
        }
        return rdCardXianJueInfos;

    }
    public static List<RdCardXianJueInfo> gainCardXianJues(List<RdZxzCardXianJue> rdZxzCardXianJues){
        List<RdCardXianJueInfo> rdCardXianJueInfos = new ArrayList<>();
        for (RdZxzCardXianJue zxzCardXianJue : rdZxzCardXianJues) {
            RdCardXianJueInfo rd = new RdCardXianJueInfo();
            rd.setXianJueType(zxzCardXianJue.getXianJueType());
            rd.setLevel(zxzCardXianJue.getLevel());
            rd.setQuality(zxzCardXianJue.getQuality());
            rd.setComprehendValue(zxzCardXianJue.getComprehendValue());
            rdCardXianJueInfos.add(rd);
        }
        return rdCardXianJueInfos;

    }

}
