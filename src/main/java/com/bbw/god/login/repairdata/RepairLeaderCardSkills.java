package com.bbw.god.login.repairdata;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.bbw.god.login.repairdata.RepairDataConst.LEADER_CARD_SKILLS_MIGRATE;

/**
 * 主角卡技能数据迁移
 *
 * @author fzj
 * @date 2021/10/19 16:32
 */
@Service
public class RepairLeaderCardSkills  implements BaseRepairDataService{
    @Autowired
    private LeaderCardService leaderCardService;
    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (lastLoginDate.before(LEADER_CARD_SKILLS_MIGRATE)) {
            leaderCardService.migrateLeaderCardSkills(gu.getId());
        }
    }
}
