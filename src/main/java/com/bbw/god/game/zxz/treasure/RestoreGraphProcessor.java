package com.bbw.god.game.zxz.treasure;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.zxz.entity.UserZxzRegionInfo;
import com.bbw.god.game.zxz.enums.ZxzStatusEnum;
import com.bbw.god.game.zxz.service.ZxzRefreshService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.processor.TreasureUseProcessor;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.service.ZxzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 还原图
 * @author: hzf
 * @create: 2022-09-23 11:40
 **/
@Service
public class RestoreGraphProcessor extends TreasureUseProcessor {

    @Autowired
    private ZxzService zxzService;
    @Autowired
    private ZxzRefreshService zxzRefreshService;

    public RestoreGraphProcessor() {
        this.treasureEnum = TreasureEnum.ZXZ_RESTORE_GRAPH;
        this.isAutoBuy = false;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        Integer difficulty = ZxzTool.getDifficulty(param.getRegionId());
        UserZxzRegionInfo userZxzRegion = zxzService.getUserZxzRegion(gu.getId(), param.getRegionId());
        if (userZxzRegion.getStatus().equals(ZxzStatusEnum.NOT_OPEN.getStatus())) {
            throw new ExceptionForClientTip("zxz.region.no.exist");
        }
        zxzRefreshService.manualRefreshDifficultyRegion(gu.getId(),difficulty,param.getRegionId());
    }
}
