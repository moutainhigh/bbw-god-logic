package com.bbw.god.gameuser.businessgang.digfortreasure;

import com.bbw.god.game.award.Award;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 挖宝信息
 *
 * @author: huanghb
 * @date: 2022/1/25 11:27
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDDigTreasureInfo extends RDCommon implements Serializable {
    private static final long serialVersionUID = 7409189386250719040L;
    private List<Award> digTreasureAward = new ArrayList<>();
    /** 我的挖宝--当前层数 */
    private Integer floor;
    /** 如果有奖励返回1，否则返回0 */
    private Integer ifAward;

    /**
     * 挖宝返回信息初始化
     *
     * @param userDigTreasure
     * @return
     */
    public static RDDigTreasureInfo getInstance(UserDigTreasure userDigTreasure) {
        List<Award> awards = getResultAwards(userDigTreasure);
        RDDigTreasureInfo rd = new RDDigTreasureInfo();
        rd.setDigTreasureAward(awards);
        rd.setFloor(userDigTreasure.getCurrentFloor());
        return rd;
    }

    /**
     * 获得最终奖励
     *
     * @param userDigTreasure
     * @return
     */
    private static List<Award> getResultAwards(UserDigTreasure userDigTreasure) {
        List<Integer> treasureTroveIds = userDigTreasure.getCurrentPosAllDugTreasureTroveIds();
        return DigTreasureTool.getCurrentPosDugTreasureTroveAwawds(treasureTroveIds, userDigTreasure.getResultAwardIds());
    }

    /**
     * 更新最终奖励
     *
     * @param userDigTreasure
     */
    protected void updateResultId(UserDigTreasure userDigTreasure) {
        this.digTreasureAward = getResultAwards(userDigTreasure);
    }

    @Data
    public static class RDAwardInfo {
        private Integer id;
        private List<Award> awards;

    }
}
