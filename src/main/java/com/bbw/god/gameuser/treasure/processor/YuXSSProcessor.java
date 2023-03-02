package com.bbw.god.gameuser.treasure.processor;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.yuxg.UserYuXG;
import com.bbw.god.gameuser.yuxg.YuXGService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 玉虚神水
 *
 * @author longwh
 * @date 2022/9/27 8:55
 */
@Service
public class YuXSSProcessor extends TreasureUseProcessor{

    @Autowired
    YuXGService yuXGService;
    @Autowired
    GameUserService gameUserService;

    public YuXSSProcessor() {
        this.treasureEnum = TreasureEnum.YUXU_SHENSHUI;
        this.isAutoBuy = false;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        UserYuXG userYuXG = yuXGService.getOrCreateYuXGData(gu.getId());
        //检查玉虚神水数量
        TreasureChecker.checkIsEnough(param.getProId(), 1, gu.getId());
        //更新玉虚神水使用状态
        int useShenShui = 1;
        userYuXG.setActiveShenShui(useShenShui);
        gameUserService.updateItem(userYuXG);
    }
}