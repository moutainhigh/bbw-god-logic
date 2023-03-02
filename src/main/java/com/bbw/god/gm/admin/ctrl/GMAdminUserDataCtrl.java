package com.bbw.god.gm.admin.ctrl;

import com.bbw.common.Rst;
import com.bbw.god.gm.UserGmService;
import com.bbw.god.gm.admin.CRAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 后台接口-玩家数据相关操作
 * @author：lzc
 * @date: 2021/03/17 11:28
 * @version: 1.0
 */
@RequestMapping("/gm/admin")
@RestController
public class GMAdminUserDataCtrl {
    @Autowired
    private UserGmService userGmService;

    /**
     * 添加法宝
     *
     * @param sId
     * @param nickname
     * @param treasures 所有;七香车,定风珠
     * @param num
     * @return
     */
    @RequestMapping(CRAdmin.UserData.ADD_TREASURES)
    public Rst addTreasures(int sId, String nickname, String treasures, int num) {
        return userGmService.addTreasures(sId,nickname,treasures,num);
    }

    /**
     * 根据法宝名模糊添加法宝
     * @param sId
     * @param nickname
     * @param like
     * @param num
     * @return
     */
    @RequestMapping(CRAdmin.UserData.ADD_LIKE_TREASURES)
    public Rst addLikeTreasures(int sId, String nickname, String like, int num) {
        return userGmService.addLikeTreasures(sId,nickname,like,num);
    }

    /**
     * 以邮件形式添加法宝
     * @param sId
     * @param nickname
     * @param treasures
     * @param num
     * @param like （true or false 是否根据treasures模糊添加）
     * @return
     */
    @RequestMapping(CRAdmin.UserData.ADD_TREASURES_BY_MAIL)
    public Rst addTreasuresByMail(int sId, String nickname, String treasures, int num,boolean like) {
        return userGmService.addTreasuresByMail(sId,nickname,treasures,num,like);
    }

    /**
     * 加封地
     *
     * @param sId
     * @param nickname
     * @param cityNames
     * @return
     */
    @RequestMapping(CRAdmin.UserData.ADD_CITIES)
    public Rst addCities(int sId, String nickname, String cityNames) {
        return userGmService.addCities(sId,nickname,cityNames,true);
    }

    /**
     * 添加月卡的天数
     *
     * @param sId
     * @param nickname
     * @param days
     * @return
     */
    @RequestMapping(CRAdmin.UserData.ADD_YK_END_TIME)
    public Rst addYKEndTime(int sId, String nickname, int days) {
        return userGmService.addYKEndTime(sId,nickname,days);
    }

    /**
     * 添加季卡的天数
     *
     * @param sId
     * @param nickname
     * @param days
     * @return
     */
    @RequestMapping(CRAdmin.UserData.ADD_JK_END_TIME)
    public Rst addJKEndTime(int sId, String nickname, int days) {
        return userGmService.addJKEndTime(sId,nickname,days);
    }

    /**
     * 添加速战卡
     *
     * @param sId
     * @param nickname
     * @return
     */
    @RequestMapping(CRAdmin.UserData.ADD_SZK)
    public Rst addSZK(int sId, String nickname) {
        return userGmService.addSZK(sId,nickname);
    }
}
