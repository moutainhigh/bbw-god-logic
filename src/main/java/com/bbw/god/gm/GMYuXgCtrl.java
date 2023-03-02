package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.god.city.chengc.in.FaTanService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.yuxg.UserFuCe;
import com.bbw.god.gameuser.yuxg.UserYuXGService;
import com.bbw.god.gameuser.yuxg.YuXGTool;
import com.bbw.god.server.ServerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 玉虚宫相关操作
 *
 * @author fzj
 * @date 2021/11/26 19:17
 */
@RestController
@RequestMapping("/gm/yuXg/")
public class GMYuXgCtrl {
    @Autowired
    UserYuXGService userYuXGService;
    @Autowired
    GameUserService gameUserService;
    @Autowired
    ServerUserService serverUserService;
    @Autowired
    FaTanService faTanService;

    /**
     * 增加符册
     *
     * @param sid
     * @param username
     * @return
     */
    @RequestMapping("user!addFuCe")
    public Rst resetYaoZuData(int sid, String username) {
        Long uid = serverUserService.getUidByNickName(sid, username).get();
        int allFaTanLv = faTanService.getTotalLevel(uid);
        //开启新的符册
        int hasFuCeNum = userYuXGService.getUserFuCes(uid).size() + 1;
        //获取符图槽数量
        Integer fuTuSlotNum = YuXGTool.getFuTuSlotNum(allFaTanLv);
        UserFuCe userFuCe = UserFuCe.getInstance(uid, "符册" + hasFuCeNum, 1, hasFuCeNum, fuTuSlotNum);
        gameUserService.addItem(uid, userFuCe);
        return Rst.businessOK();
    }
}
