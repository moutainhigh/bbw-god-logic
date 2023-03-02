package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.gameuser.businessgang.digfortreasure.DigTreasureService;
import com.bbw.god.gameuser.businessgang.digfortreasure.UserDigTreasure;
import com.bbw.god.server.ServerService;
import com.bbw.god.server.ServerUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 挖宝处理相关接口
 *
 * @author: huanghb
 * @date: 2023/2/9 14:34
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMDigTreasureCtrl extends AbstractController {
    @Autowired
    ServerService serverService;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private DigTreasureService digTreasureService;

    /**
     * 删除
     *
     * @param sId
     * @param nickname
     * @return
     */
    @RequestMapping("digTreasure!delete")
    public Rst deleteDigTresureInfo(int sId, String nickname) {
        Optional<Long> uidOp = this.serverUserService.getUidByNickName(sId, nickname);
        if (!uidOp.isPresent()) {
            return Rst.businessFAIL("该账号不存在");
        }
        long uid = uidOp.get();
        Rst rst = Rst.businessOK();
        //获得玩家挖宝信息
        int userPos = gameUserService.getGameUser(uid).getLocation().getPosition();
        UserDigTreasure userDigTreasure = digTreasureService.getUserCurrentDigTreasureByPos(uid, userPos);
        if (null == userDigTreasure) {
            return rst.put("" + userPos, "该位置没有挖宝数据");
        }
        gameUserService.deleteItem(userDigTreasure);
        return rst.put("" + userPos, "该位置挖宝数据删除成功");
    }
}