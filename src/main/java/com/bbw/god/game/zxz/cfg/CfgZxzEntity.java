package com.bbw.god.game.zxz.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 诛仙阵的基本配置
 * @author: hzf
 * @create: 2022-09-14 15:47
 **/
@Data
public class CfgZxzEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    private String key;
    /** 玩家解锁诛仙阵需要的等级 */
    private Integer unlockNeedUserLevel;
    /** 玩家解锁诛仙阵需要拥有至少应该5级城池 */
    private Integer unlockNeedCityLevel5;
    /** 榜单人数上限 */
    private Integer rankLimit;
    /** 保留几周时间 */
    private Integer retainWeekNum;
    /** 开始维护时间 */
    private String beginMaintainDate;
    /** 结束维护时间 */
    private String endMaintainDate;
    /** 区域信息 */
    private List<CfgRegionConfig> regionConfig;

    /**默认配置*/
    private List<CfgZxzLevel> levels;
    /** 敌方配置规则 */
    private List<CfgZxzDefenderCardRule> defenderCardRules;
    /** 区域攻防比例 */
    private List<CfgRegionDefenseProport> regionDefenseProports;
    /** 自动刷新时间 */
    private List<CfgAutoRefreshRule> autoRefreshRules;
    /**复阵图*/
    private List<CfgManualRefreshLevel> manualRefreshLevels;

    /** 复活草 */
    private List<CfgRevival> revivals;
    /** 诛仙阵要过滤对的符图id */
    private List<Integer> filterFutuIds;

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }
}
