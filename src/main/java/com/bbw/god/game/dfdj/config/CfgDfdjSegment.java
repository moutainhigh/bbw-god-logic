package com.bbw.god.game.dfdj.config;

import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CfgDfdjSegment implements CfgInterface, Serializable {

    private static final long serialVersionUID = 1L;
    private String key;
    private List<CfgDfdjSegmentEntity> segments;// 段位信息
    private List<CfgDfdjSegmentEntity> sprintSegments;// 段位信息
    private List<CfgDfdjStageEntity> stages;//阶段信息

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }

}
