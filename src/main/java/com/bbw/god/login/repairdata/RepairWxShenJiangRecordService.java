package com.bbw.god.login.repairdata;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.UserTreasureRecord;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bbw.god.login.repairdata.RepairDataConst.RESET_WX_SHENG_RECORD;

/**
 * 修复五行神将礼包保底次数
 *
 * @author fzj
 * @date 2022/5/19 9:56
 */
@Service
public class RepairWxShenJiangRecordService implements BaseRepairDataService {
    @Autowired
    UserTreasureRecordService userTreasureRecordService;
    @Autowired
    GameUserService gameUserService;

    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (!lastLoginDate.before(RESET_WX_SHENG_RECORD)) {
            return;
        }
        //获取战令三星记录
        long uid = gu.getId();
        List<UserTreasureRecord> userTreasureRecords = new ArrayList<>();
        UserTreasureRecord utr = userTreasureRecordService.getUserTreasureRecord(uid, TreasureEnum.WAR_TOKEN_TS_BOX.getValue());
        if (utr == null || utr.getUseTimes() == 0) {
            return;
        }
        Integer useTimes = utr.getUseTimes();
        UserTreasureRecord treasureRecord = userTreasureRecordService.getOrCreateRecord(uid, TreasureEnum.WX_SHEN_JIANG.getValue(), useTimes);
        if (useTimes >= 200) {
            utr.setUseTimes(100);
            userTreasureRecords.add(utr);
        }
        userTreasureRecords.add(treasureRecord);
        gameUserService.updateItems(userTreasureRecords);
    }
}
