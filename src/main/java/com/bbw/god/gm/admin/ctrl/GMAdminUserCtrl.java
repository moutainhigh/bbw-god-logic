package com.bbw.god.gm.admin.ctrl;

import com.bbw.common.Rst;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.gm.UserGmService;
import com.bbw.god.gm.admin.CRAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 后台接口-玩家相关的操作
 * @author：lzc
 * @date: 2021/03/17 11:28
 * @version: 1.0
 */
@RequestMapping("/gm/admin")
@RestController
public class GMAdminUserCtrl {
    @Autowired
    private UserGmService userGmService;

    /**
     * 限制登录
     * @param sId
     * @param nickname
     * @param endDate  yyyy-MM-dd HH:mm:ss
     * @return
     */
    @RequestMapping(CRAdmin.UserGm.LIMIT_LOGIN)
    public Rst limitLogin(int sId, String nickname, String endDate){
        return userGmService.limitLogin(sId,nickname,endDate);
    }

    /**
     * 限制发言
     *
     * @param sId
     * @param nickname
     * @param endDate  yyyy-MM-dd HH:mm:ss
     * @return
     */
    @RequestMapping(CRAdmin.UserGm.LIMIT_TALKING)
    public Rst limitTalking(int sId, String nickname, String endDate) {
        return userGmService.limitTalking(sId,nickname,endDate);
    }

    /**
     * 设置性别
     *
     * @param sId
     * @param nickname
     * @param sex      1女2男
     * @return
     */
    @RequestMapping(CRAdmin.UserGm.SET_SEX)
    public Rst setSex(int sId, String nickname, int sex) {
        return userGmService.setSex(sId,nickname,sex);
    }

    /**
     * 更新玩家到特定等级
     *
     * @param sId
     * @param nickname
     * @param level
     * @return
     */
    @RequestMapping(CRAdmin.UserGm.UPDATE_TO_LEVEL)
    public Rst updateToLevel(int sId, String nickname, int level) {
        if (0 < level && level < 40){
            return userGmService.updateToLevel(sId,nickname,level);
        }
        throw ExceptionForClientTip.fromMsg("等级必须为0到40");
    }

    /**
     * 更新玩家等级到某个等级的前一个等级（exp-1）
     * @param sId
     * @param nickname
     * @param level
     * @return
     */
    @RequestMapping(CRAdmin.UserGm.UPDATE_TO_PRE_LEVEL)
    public Rst updateToPreLevel(int sId, String nickname, int level) {
        return userGmService.updateToAlmostLevel(sId,nickname,level);
    }

    /**
     * 修复用户组
     *
     * @param uids
     * @return
     */
    @RequestMapping(CRAdmin.UserGm.FIX_USER_GROUP)
    public Rst fixUserGroup(String uids) {
        return userGmService.fixUserGroup(uids);
    }

    /**
     * 设置新手引导的状态
     *
     * @param sId
     * @param nickname
     * @param guideStatus
     * @return
     */
    @RequestMapping(CRAdmin.UserGm.SET_GUIDE_STATUS)
    public Rst setGuideStatus(int sId, String nickname, int guideStatus) {
        return userGmService.setGuideStatus(sId,nickname,guideStatus);
    }

}
