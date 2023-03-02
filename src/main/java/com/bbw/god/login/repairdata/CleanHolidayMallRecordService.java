package com.bbw.god.login.repairdata;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.UserMallRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.bbw.god.login.repairdata.RepairDataConst.CLEAN_HOLIDAY_MALL_RECORD_TIME;

/**
 * @author suchaobin
 * @description 清除活动兑换商品记录service
 * @date 2020/9/13 0:21
 **/
@Service
public class CleanHolidayMallRecordService implements BaseRepairDataService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private MallService mallService;

    /**
     * 修复数据
     *
     * @param gu            玩家对象
     * @param lastLoginDate 上次登录时间
     */
    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (lastLoginDate.before(CLEAN_HOLIDAY_MALL_RECORD_TIME)) {
            Long uid = gu.getId();
            List<UserMallRecord> records = mallService.getRecords(uid).stream()
                    .filter(tmp -> Arrays.asList(140010, 140020).contains(tmp.getBaseId())).collect(Collectors.toList());
            mallService.delRecords(records);
        }
    }
}
