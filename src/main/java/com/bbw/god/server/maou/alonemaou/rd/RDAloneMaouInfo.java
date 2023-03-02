package com.bbw.god.server.maou.alonemaou.rd;

import com.bbw.god.rd.RDSuccess;
import com.bbw.god.server.maou.ServerMaouKind;
import com.bbw.god.server.maou.ServerMaouStatus;
import com.bbw.god.server.maou.alonemaou.AloneMaouParam;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaou;
import com.bbw.god.server.maou.alonemaou.UserAloneMaouData;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouAttackSummary;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouLevelInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 独战魔王信息
 *
 * @author suhq
 * @date 2019年12月19日 下午7:07:06
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDAloneMaouInfo extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer maouKind = ServerMaouKind.ALONE_MAOU.getValue();
    private Integer maouType = null;// 魔王属性
    private Integer maouLevel = null;//魔王级别
    private Integer maouStatus = null;// 魔王状态
    private Long maouRemainTime = null;// 魔王过多久离开
    private Integer attackTimes = null;//攻击次数
    private Integer remainResetTimes = null;//可重置次数
    private Integer remainAttackTimes = null;// 剩余可攻击次数
    private List<Integer> attackMaouCards = null;// 魔王编组卡牌
    private Long bossMaouRemainTime = null;// 魔王降临剩余时间
    private Integer bossMaouStatus = null;// 魔王降临状态
    private Integer bossMaouNextOpenTime = null;// 魔王降临下轮开启时间

    public static RDAloneMaouInfo getInstance(AloneMaouParam maouParam, Long bossMaouRemainTime,
                                              Integer bossMaouStatus, Integer bossMaouNextOpenTime) {
        UserAloneMaouData uamd = maouParam.getUserMaouData();
        ServerAloneMaou maou = maouParam.getMaou();
        AloneMaouLevelInfo attackInfo = maouParam.getLevelInfo();
        AloneMaouAttackSummary myAttack = maouParam.getMyAttack();
        RDAloneMaouInfo obj = new RDAloneMaouInfo();
        obj.setMaouType(maou.getType());
        obj.setMaouLevel(attackInfo.getMaouLevel());
        obj.setMaouStatus(attackInfo.gainMaouStatus().getValue());
        long maouEndRemainTime = maou.getEndTime().getTime() - System.currentTimeMillis();
        obj.setMaouRemainTime(maouEndRemainTime);
        obj.setRemainAttackTimes(maouParam.getMyAttack().getFreeAttackTimes());
        obj.setAttackTimes(myAttack.getAttackTimes());
        obj.setRemainResetTimes(myAttack.getRemainResetTimes());
        obj.setAttackMaouCards(uamd.getAttackCards());
        obj.setBossMaouRemainTime(bossMaouRemainTime);
        obj.setBossMaouNextOpenTime(bossMaouNextOpenTime);
        obj.setBossMaouStatus(bossMaouStatus);
        return obj;
    }

    public static RDAloneMaouInfo getInstanceAsMaouOver() {
        RDAloneMaouInfo rd = new RDAloneMaouInfo();
        rd.setMaouStatus(ServerMaouStatus.OVER.getValue());
        return rd;
    }
}
