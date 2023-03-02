package com.bbw.god.gameuser.businessgang.digfortreasure;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 挖宝相关入口
 *
 * @author: huanghb
 * @date: 2022/1/17 16:58
 */
@RestController
public class DigTreasureCtrl extends AbstractController {
    @Autowired
    private DigTreasureLogic digTreasureLogic;

    /**
     * 挖宝
     *
     * @return
     */
    @GetMapping(CR.DigTreasure.DIG_TREASURE_DIG)
    public RDSuccess digTreasure() {
        return digTreasureLogic.digTreasure(getUserId());
    }

    /**
     * 获得所有挖宝信息
     *
     * @return
     */
    @GetMapping(CR.DigTreasure.GET_ALL_DIG_TREASURE_INFO)
    public RdDigTreasureInfos getAlldigTreasureIfo() {
        return digTreasureLogic.getAlldigTreasureIfo(getUserId());
    }
}
