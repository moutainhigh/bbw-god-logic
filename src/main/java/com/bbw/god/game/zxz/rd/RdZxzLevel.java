package com.bbw.god.game.zxz.rd;

import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.entity.UserZxzDifficulty;
import com.bbw.god.game.zxz.entity.UserZxzRegionInfo;
import com.bbw.god.game.zxz.enums.ZxzStatusEnum;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回Zxz难度数据
 * @author: hzf
 * @create: 2022-09-17 10:05
 **/
@Data
public class RdZxzLevel extends RDSuccess {
    /** 难度数据 */
    List<RdUserZxzLevel> levels = new ArrayList<>();
    /** 进入的难度 */
    private Integer intoDifficulty;

    public RdZxzLevel getInstance(List<UserZxzDifficulty> levelInfo, List<UserZxzRegionInfo> userZxzRegionInfos, Integer intoDifficulty) {
        List<RdUserZxzLevel> levels = new ArrayList<>();
        for (UserZxzDifficulty userZxzDifficulty : levelInfo) {

            RdUserZxzLevel rdLevel = new RdUserZxzLevel();

            rdLevel.setDifficulty(userZxzDifficulty.getDifficulty());
            rdLevel.setClearanceScore(userZxzDifficulty.gainClearanceScore());
            rdLevel.setStatus(userZxzDifficulty.gainStatus());
            rdLevel.setRefreshTime(ZxzTool.getRemainRefreshSeconds(userZxzDifficulty.getDifficulty()));

            List<RdUserZxzLevelRegion> regions = new ArrayList<>();
            for (UserZxzRegionInfo regionInfo : userZxzRegionInfos) {
                if (userZxzDifficulty.getDifficulty().equals(regionInfo.getDifficulty())) {
                    RdUserZxzLevelRegion rdRegion = new RdUserZxzLevelRegion();
                    rdRegion.setRegionId(regionInfo.getRegionId());
                    rdRegion.setStatus(regionInfo.getStatus());
                    regions.add(rdRegion);
                }
            }
            //开启了才添加区域数据
            if (!userZxzDifficulty.getStatus().equals(ZxzStatusEnum.NOT_OPEN.getStatus())) {
                rdLevel.setLevelRegions(regions);
            } else {
                rdLevel.setLevelRegions(new ArrayList<>());
            }


            levels.add(rdLevel);
        }

        RdZxzLevel rd = new RdZxzLevel();
        rd.setIntoDifficulty(intoDifficulty);
        rd.setLevels(levels);
        return rd;
    }

    @Data
    public static class RdUserZxzLevel{
        /** 难度类型 */
        private Integer difficulty;
        /** 通关评分 */
        private Integer clearanceScore;
        /** 诛仙阵：状态信息：参考 ZxzStatusEnum 枚举类 */
        private Integer status;
        /** 刷新时间 */
        private long refreshTime;
        /** 上次进入的区域*/
        private Integer intoRegion;
        /** 区域状态 */
        private List<RdUserZxzLevelRegion> levelRegions;


    }
    @Data
    public static class RdUserZxzLevelRegion{
        private Integer regionId;
        private Integer status;
    }
}
