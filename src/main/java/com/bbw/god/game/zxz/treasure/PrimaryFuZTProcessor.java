package com.bbw.god.game.zxz.treasure;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.zxz.service.ZxzRefreshService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.processor.TreasureUseProcessor;
import com.bbw.god.game.zxz.cfg.CfgManualRefreshLevel;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 初级复阵图
 * @author: hzf
 * @create: 2022-09-23 11:44
 *
 * IntermediateComplexArrayDiagramProcessor
 **/
@Service
public class PrimaryFuZTProcessor extends TreasureUseProcessor {

    @Autowired
    private ZxzRefreshService zxzRefreshService;

    public PrimaryFuZTProcessor() {
        this.treasureEnum = TreasureEnum.ZXZ_PRIMARY_FUZT;
        this.isAutoBuy = false;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        //判断难度
        ZxzTool.ifDifficulty(param.getDifficulty());
        //判断该难度是否消耗该复阵图
        CfgManualRefreshLevel byDifficultyManualRefreshLevel = ZxzTool.getManualRefreshLevel(param.getDifficulty(), param.getProId());
        if (byDifficultyManualRefreshLevel == null) {
            throw new ExceptionForClientTip("zxz.difficulty.needTreasure.error");
        }
        zxzRefreshService.manualRefreshDifficulty(gu.getId(),param.getDifficulty());
    }
}
