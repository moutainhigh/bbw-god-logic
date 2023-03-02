package com.bbw.god.gameuser.businessgang.digfortreasure;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgInterface;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * 挖宝相关常量配置
 *
 * @author: huanghb
 * @date: 2022/1/24 15:18
 */
@Slf4j
@Data
public class CfgDigTreasure implements CfgInterface, Serializable {
    private static final long serialVersionUID = 3288156423421447551L;
    private String key;
    /** 楼层信息 */
    private List<Floor> floors;
    /** 随机奖励池 */
    private List<TreasureTrove> treasureTroves;
    /** 永久开启需要好感度 */
    private Integer permanentOpenNeedFavorability;
    /** 临时开启需要好感度 */
    private Integer tempOpenNeedFavorability;
    /** 周开始时间 */
    private Integer weekBeginHour;
    /** 对挖宝有影响的法宝id */
    private List<Integer> ownEffectTReasureIds;

    @Data
    public static class TreasureTrove {
        /** 宝藏id */
        private int id;
        /** 出现楼层 */
        private int floor;
        /** 奖励 */
        private List<Award> awards;
    }

    /**
     * 楼层
     *
     * @author: huanghb
     * @date: 2023/1/30 16:42
     */
    @Data
    public static class Floor {
        /** 楼层id */
        private Integer id;
        /** 宝藏数量 */
        private Integer treasureTroveNum;
        /** 挖宝需要铲子id */
        private Integer digTreasureNeedShovelId;
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
