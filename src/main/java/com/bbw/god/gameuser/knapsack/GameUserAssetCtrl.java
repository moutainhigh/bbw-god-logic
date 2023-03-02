package com.bbw.god.gameuser.knapsack;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lwb
 * @date 2020/4/3 11:38
 */
@RestController
public class GameUserAssetCtrl extends AbstractController {
    @Autowired
    private GameUserAssetService gameUserAssetService;

    @RequestMapping(CR.UserAsset.LIST_PACKAGE)
    public RDUserAsset getUserAssetList(int type){
        return gameUserAssetService.getUserAssetList(UserAssetEnum.fromType(type),getUserId());
    }

    @RequestMapping(CR.UserAsset.LIST_FAST_TREASURE)
    public RDUserAsset getMapFastTreasures(){
        return gameUserAssetService.getFastMapTreasureList(getUserId());
    }
}
