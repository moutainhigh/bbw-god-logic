package com.bbw.god.server.maou.bossmaou.rd;

import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.maou.ServerMaouKind;
import com.bbw.god.server.maou.ServerMaouStatus;
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
public class RDBossMaouAttackingInfo extends RDCommon implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer maouKind = ServerMaouKind.BOSS_MAOU.getValue();
    private Integer maouType;//魔王属性
    private Integer remainBlood = null;// 剩余血量
    private Integer totalBlood = null;// 总血量
    private Integer maouStatus = ServerMaouStatus.ATTACKING.getValue();// 魔王状态
    private Long maouRemainTime = null;// 魔王过多久离开
    private Long roundRemainTime = null;// 回合剩余时间
    private Integer myFreeTimesCurRound = null;//当前回合剩余免费次数
    private Integer beatedBlood = null;// 我当次打掉的血量
    private Integer myBeatedTotalBlood = null;// 我打掉的总血量
    private Integer myRank = null;// 我的排行
    private List<RDBossMaouRanker> rankers;//排行信息

    public static RDBossMaouAttackingInfo getInstanceAsMaouOver() {
        RDBossMaouAttackingInfo rd = new RDBossMaouAttackingInfo();
        rd.setMaouStatus(ServerMaouStatus.OVER.getValue());
        return rd;
    }
}
