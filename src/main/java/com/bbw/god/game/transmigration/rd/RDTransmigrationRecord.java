package com.bbw.god.game.transmigration.rd;

import com.bbw.common.ListUtil;
import com.bbw.god.game.transmigration.entity.UserTransmigrationRecord;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 轮回挑战记录(每座城最好挑战记录)
 *
 * @author: suhq
 * @date: 2021/9/15 10:34 上午
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDTransmigrationRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 城池 */
    private Integer cityId;
    /** 基础评分 */
    private Integer score;
    /** 额外评分 */
    private Integer extraScore;
    /** 是否是新纪录 */
    private Integer isNewRecord = 0;
    /** 是否第一 */
    private Integer isCityNo1 = 0;
    /** 战斗回合评分,使用法宝评分,剩余血量评分,死亡神将评分,扣除血量评分,击杀神将评分 */
    private List<Integer> scoreCompositions;
    /** 轮回战斗奖励领取状态 */
    private int[] awardStatus;

    public static RDTransmigrationRecord getInstance(UserTransmigrationRecord record) {
        RDTransmigrationRecord rdRecord = new RDTransmigrationRecord();
        rdRecord.setCityId(record.getCityId());
        rdRecord.setIsNewRecord(record.isNewRecord() ? 1 : 0);
        rdRecord.setScore(ListUtil.sumInt(record.getScoreCompositions()));
        rdRecord.setScoreCompositions(record.getScoreCompositions());
        return rdRecord;
    }
}
