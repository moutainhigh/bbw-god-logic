package com.bbw.god.gm.admin.ctrl;

import com.bbw.common.Rst;
import com.bbw.god.gm.UserGmService;
import com.bbw.god.gm.admin.CRAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 后台接口-玩家资源相关操作
 * @author：lzc
 * @date: 2021/03/17 11:28
 * @version: 1.0
 */
@RequestMapping("/gm/admin")
@RestController
public class GMAdminUserResCtrl {
    @Autowired
    private UserGmService userGmService;

    /**
     * 加铜钱
     *
     * @param sId
     * @param nickname
     * @param num
     * @return
     */
    @RequestMapping(CRAdmin.UserRes.ADD_COPPER)
    public Rst addCopper(int sId, String nickname, int num) {
        return userGmService.addCopper(sId,nickname,num);
    }

    /**
     * 加元宝
     *
     * @param sId
     * @param nickname
     * @param num
     * @return
     */
    @RequestMapping(CRAdmin.UserRes.ADD_GOLD)
    public Rst addGold(int sId, String nickname, int num) {
        return userGmService.addGold(sId,nickname,num);
    }

    /**
     * 加体力
     *
     * @param sId
     * @param nickname
     * @param num
     * @return
     */
    @RequestMapping(CRAdmin.UserRes.ADD_DICE)
    public Rst addDice(int sId, String nickname, int num) {
        return userGmService.addDice(sId,nickname,num);
    }

    /**
     * 加元素
     *
     * @param sId
     * @param nickname
     * @param type     金木水火土 10~50
     * @param num
     * @return
     */
    @RequestMapping(CRAdmin.UserRes.ADD_ELE)
    public Rst addEle(int sId, String nickname, int type, int num) {
        return userGmService.addEle(sId,nickname,type,num);
    }

    /**
     * 添加特产
     * @param sId
     * @param nickname
     * @param special
     * @param num
     * @return
     */
    @RequestMapping(CRAdmin.UserRes.ADD_SPECIAL)
    public Rst addSpecial(int sId, String nickname, String special, int num) {
        return userGmService.addSpecial(sId,nickname,special,num);
    }

    /**
     * 添加灵石
     *
     * @param sId
     * @param nickname
     * @param cards    所有;姜子牙,杨戬
     * @param num
     * @return
     */
    @RequestMapping(CRAdmin.UserRes.ADD_LING_SHI)
    public Rst addLingShi(int sId, String nickname, String cards, int num) {
        return userGmService.addLingShi(sId,nickname,cards,num);
    }
}
