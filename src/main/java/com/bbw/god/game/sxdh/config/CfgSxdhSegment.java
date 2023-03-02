package com.bbw.god.game.sxdh.config;

import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CfgSxdhSegment implements CfgInterface, Serializable {

    private static final long serialVersionUID = 1L;
    private String key;
    private List<CfgSxdhSegmentEntity> segments;// 段位信息
    private List<CfgSxdhSegmentEntity> sprintSegments;// 段位信息
    private List<CfgSxdhStageEntity> stages;//阶段信息

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }

}
