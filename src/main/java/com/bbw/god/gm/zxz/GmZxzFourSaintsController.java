package com.bbw.god.gm.zxz;

import com.bbw.god.game.zxz.entity.foursaints.ZxzFourSaintsInfo;
import com.bbw.god.game.zxz.service.foursaints.InitZxzFourSaintsService;
import com.bbw.god.game.zxz.service.foursaints.ZxzFourSaintsLogic;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * gm 四圣挑战controller
 * @author: hzf
 * @create: 2023-01-02 09:22
 **/
@RestController
public class GmZxzFourSaintsController {

    @Autowired
    private InitZxzFourSaintsService initZxzFourSaintsService;
    @Autowired
    private ZxzFourSaintsLogic zxzFourSaintsLogic;

    /**
     * 生成敌方野怪
     * @return
     */
    @GetMapping("gm/initZxzFourSaints")
    public ZxzFourSaintsInfo initZxzFourSaints(){
        ZxzFourSaintsInfo zxzFourSaintsInfo = initZxzFourSaintsService.initZxzFourSaints();
        return zxzFourSaintsInfo;
    }
    /**
     * 删除敌方配置
     * @param beginDate
     * @return
     */
    @GetMapping("/gm/delZxzFourSaints")
    public RDSuccess delZxzFourSaints(String beginDate){
        return initZxzFourSaintsService.delZxzFourSaints(beginDate);
    }

    /**
     * 解锁四圣难度
     * @param uid
     * @param clearanceScore
     * @param difficulty
     * @return
     */
    @GetMapping("gm/zxz!unlockFourSaints")
    public RDSuccess unlockFourSaints(long uid, Integer clearanceScore,Integer difficulty){
        return zxzFourSaintsLogic.unlockFourSaints(uid, clearanceScore, difficulty);
    }
}
