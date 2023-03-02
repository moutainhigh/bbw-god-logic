package com.bbw.god.server.maou.alonemaou.rd;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.game.config.CfgAloneMaou;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.server.maou.ServerMaouKind;
import com.bbw.god.server.maou.ServerMaouStatus;
import com.bbw.god.server.maou.alonemaou.AloneMaouParam;
import com.bbw.god.server.maou.alonemaou.AloneMaouTool;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaou;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouAttackSummary;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouLevelInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 进入攻打界面返回的魔王信息
 *
 * @author suhq
 * @date 2019年1月8日 下午7:07:06
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDAloneMaouAttackingInfo extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer maouKind = ServerMaouKind.ALONE_MAOU.getValue();
    private Integer maouType;//魔王属性
    private Integer remainBlood = null;// 剩余血量
    private Integer totalBlood = null;// 总血量
    private Integer remainShild = null;// 剩余护盾
    private Integer totalShild = null;// 总护盾
    private Integer maouSkill = null;//魔王技能
    private Integer remainAttackTimes = null;// 剩余攻击次数
    private Integer totalAttackTimes = null;// 总攻击次数
    private Integer boughtTimes;//已购买次数
    private Integer remainRound = null;
    private Long nextRemainTime = null;// 过多久魔王可以打
    private Integer maouStatus = null;//魔王状态
    private Integer isDoublePassAward = 0;//通关奖励是否翻倍
    private List<RDAward> passAwards;//通关奖励

    public static RDAloneMaouAttackingInfo getInstance(AloneMaouParam maouParam, boolean isDoublePassAward) {
        ServerAloneMaou maou = maouParam.getMaou();
        AloneMaouLevelInfo maouLevelInfo = maouParam.getLevelInfo();
        AloneMaouAttackSummary myAttack = maouParam.getMyAttack();
        CfgAloneMaou config = maouParam.getConfig();
        RDAloneMaouAttackingInfo obj = new RDAloneMaouAttackingInfo();
        obj.setMaouType(maou.getType());
        obj.setRemainBlood(maouLevelInfo.getRemainBlood());
        obj.setTotalBlood(maouLevelInfo.getTotalBlood());
        if (maouLevelInfo.getTotalShield() > 0) {
            obj.setRemainShild(maouLevelInfo.getRemainShield());
            obj.setTotalShild(maouLevelInfo.getTotalShield());
        }
        obj.setMaouSkill(maouLevelInfo.getMaouSkill());
        obj.setRemainAttackTimes(myAttack.getFreeAttackTimes());
        obj.setTotalAttackTimes(config.getFreeAttackTimes());
        obj.setBoughtTimes(myAttack.getBoughtTimes());
        if (maouLevelInfo.ifRoundLimit()) {
            obj.setRemainRound(maouLevelInfo.getMaxRound() - maouLevelInfo.getRound());
        }
        long nextRemainTime = myAttack.getNextAttackTime() - System.currentTimeMillis();
        obj.setNextRemainTime(nextRemainTime > 0 ? nextRemainTime : 0L);
        obj.setIsDoublePassAward(isDoublePassAward ? 1 : 0);
        List<Award> awards = AloneMaouTool.getMaouLeveAward(maou.getType(), maouLevelInfo.getMaouLevel());
        obj.setPassAwards(RDAward.getInstances(awards));
        return obj;
    }

    public static RDAloneMaouAttackingInfo getInstanceAsMaouOver() {
        RDAloneMaouAttackingInfo rd = new RDAloneMaouAttackingInfo();
        rd.setMaouStatus(ServerMaouStatus.OVER.getValue());
        return rd;
    }
}
