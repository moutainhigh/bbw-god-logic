package com.bbw.god.game.zxz.rd;

import com.bbw.god.game.zxz.entity.UserZxzRegionDefender;
import com.bbw.god.game.zxz.entity.UserZxzRegionInfo;
import com.bbw.god.game.zxz.entity.ZxzEntry;
import com.bbw.god.game.zxz.enums.ZxzDefenderKindEnum;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.List;

/**
 * 返回zxz区域数据
 * @author: hzf
 * @create: 2022-09-17 10:16
 **/
@Data
public class RdZxzRegion extends RDSuccess {
    /** 难度类型 */
    private Integer difficulty;
    /** 区域id */
    private Integer regionId;
    /** 进入区域 */
    private Integer intoRegion;
    /** 是否进入这个区域 */
    private Boolean ifInto;
    /** 进度 */
    private Integer progress;
    /** 精英宝箱 */
    private Integer eliteBox;
    /** 首领宝箱 */
    private Integer chiefBox;
    /** 复活次数 */
    private Integer frequency;
    /** 通关等级 */
    private Integer clearanceLv;
    /** 词条 */
    private List<ZxzEntry> entrys;
    private Integer regionLv;

    public static RdZxzRegion getInstance(UserZxzRegionInfo userRegion, Integer intoRegion){
        RdZxzRegion rd = new RdZxzRegion();
        rd.setDifficulty(userRegion.getDifficulty());
        rd.setRegionId(userRegion.getRegionId());
        rd.setProgress(userRegion.getProgress());
        //获取精英野怪信息
        UserZxzRegionDefender eliteDefender = userRegion.getRegionDefenders().stream().filter(defender -> defender.getKind().equals(ZxzDefenderKindEnum.KIND_20.getKind())).findFirst().orElse(null);
        rd.setEliteBox(eliteDefender.gainAwarded());
        //获取首领野怪信息
        UserZxzRegionDefender chiefDefender = userRegion.getRegionDefenders().stream().filter(defender -> defender.getKind().equals(ZxzDefenderKindEnum.KIND_30.getKind())).findFirst().orElse(null);
        rd.setChiefBox(chiefDefender.gainAwarded());
        rd.setFrequency(userRegion.getSurviceTimes());
        rd.setClearanceLv(userRegion.gainClearanceLv());
        rd.setEntrys(userRegion.gainEntrys());
        rd.setRegionLv(userRegion.computeRegionLv());
        //获取上一次进入的区域
        rd.setIntoRegion(intoRegion);

        rd.setIfInto(userRegion.isInto());
        return rd;
    }


}
