package com.bbw.god.rechargeactivities.wartoken;

import com.bbw.common.Rst;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.server.ServerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-11
 */
@RequestMapping("/gm")
@RestController
public class GMWarTokenController {
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private GameUserService userService;
    @Autowired
    private WarTokenLogic warTokenLogic;

    @RequestMapping("/wartoken!init")
    public Rst init(int sid,String nickname){
        Optional<Long> uidOptional = serverUserService.getUidByNickName(sid, nickname);
        if (!uidOptional.isPresent()){
            return Rst.businessFAIL(sid+"区服，不存在的玩家："+nickname);
        }
        long uid=uidOptional.get();
        UserWarToken userWarToken = warTokenLogic.getOrCreateUserWarToken(uid);
        List<UserWarTokenTask> userTasks = warTokenLogic.getUserTasks(userWarToken);
        userService.deleteItems(userWarToken.getGameUserId(), userTasks);
        userService.deleteItem(userWarToken);
        return Rst.businessOK();
    }

    @RequestMapping("/wartoken!initTask")
    public Rst initTask(int sid,String nickname){
        Optional<Long> uidOptional = serverUserService.getUidByNickName(sid, nickname);
        if (!uidOptional.isPresent()){
            return Rst.businessFAIL(sid+"区服，不存在的玩家："+nickname);
        }
        long uid=uidOptional.get();
        UserWarToken userWarToken = warTokenLogic.getOrCreateUserWarToken(uid);
        List<UserWarTokenTask> userTasks = warTokenLogic.getUserTasks(userWarToken);
        userService.deleteItems(userWarToken.getGameUserId(), userTasks);
        warTokenLogic.initTaskList(userWarToken,false);
        return Rst.businessOK();
    }
}
