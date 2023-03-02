package com.bbw.god.login.repairdata;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.yaozu.UserYaoZuInfo;
import com.bbw.god.gameuser.yaozu.UserYaoZuInfoService;
import com.bbw.god.gameuser.yaozu.YaoZuGenerateProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.bbw.god.login.repairdata.RepairDataConst.YAO_ZU_DATA_REPAIR;

/**
 * 妖族数据修复
 *
 * @author fzj
 * @date 2022/1/27 9:18
 */
@Service
public class RepairYaoZuDate implements BaseRepairDataService {
    @Autowired
    YaoZuGenerateProcessor yaoZuGenerateProcessor;
    @Autowired
    UserYaoZuInfoService userYaoZuInfoService;
    @Autowired
    GameUserService gameUserService;

    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (!lastLoginDate.before(YAO_ZU_DATA_REPAIR)) {
            return;
        }
        boolean passYaoZu = yaoZuGenerateProcessor.isPassYaoZu(gu.getId());
        if (!passYaoZu) {
            return;
        }
        List<UserYaoZuInfo> userYaoZu = userYaoZuInfoService.getUserYaoZu(gu.getId());
        if (!userYaoZu.isEmpty()) {
            gameUserService.deleteItems(gu.getId(), userYaoZu);
        }
    }
}
