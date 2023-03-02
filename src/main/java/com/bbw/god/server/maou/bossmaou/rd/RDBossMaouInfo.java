package com.bbw.god.server.maou.bossmaou.rd;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgBossMaou;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.server.maou.ServerMaouKind;
import com.bbw.god.server.maou.ServerMaouStatusInfo;
import com.bbw.god.server.maou.bossmaou.BossMaouTool;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import com.bbw.god.server.maou.bossmaou.attackinfo.BossMaouRoundDetail;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 魔王信息
 *
 * @author suhq
 * @date 2019年12月23日 下午7:07:06
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDBossMaouInfo extends RDSuccess implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer maouKind = ServerMaouKind.BOSS_MAOU.getValue();
    private Integer baseMaouId = null;//魔王类型
    private Integer maouType = null;// 魔王属性
    private Integer maouStatus = null;// 魔王状态
    private Long maouRemainTime = null;// 魔王过多久
    private List<Award> firstAward = null;// 第一名的奖励
    private Integer treasure = null;// 最后一家法宝
    private Boolean isMySelected = false;// 是否是我选择的魔王

    public static RDBossMaouInfo getInstance(Boolean isMySelected, ServerBossMaou sbm, BossMaouRoundDetail roundDetail,
                                             ServerMaouStatusInfo maouStatus, List<Award> awards) {
        RDBossMaouInfo rd = new RDBossMaouInfo();
        CfgBossMaou.BossMaou bossMaouConfig = BossMaouTool.getBossMaouConfig(sbm.getBaseMaouId());
        Integer maouLevel = bossMaouConfig.getMaouLevel();
        rd.setBaseMaouId(maouLevel);
        // 设置魔王属性
        int maouType = bossMaouConfig.getMaouLevel() == 10 ? TypeEnum.Null.getValue() : sbm.getType();
        if (TypeEnum.Null.getValue() != maouType && null != roundDetail) {
            maouType = roundDetail.getType();
        }
        rd.setMaouType(maouType);
        rd.setMaouStatus(maouStatus.getStatus());
        rd.setMaouRemainTime(maouStatus.getRemainTime());
        rd.setFirstAward(awards);
        rd.setTreasure(sbm.getKillAward());
        rd.setIsMySelected(isMySelected);
        return rd;
    }
}
