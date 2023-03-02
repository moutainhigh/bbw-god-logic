package com.bbw.god.login.repairdata;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.CardGroupWay;
import com.bbw.god.gameuser.card.UserCardGroup;
import com.bbw.god.gameuser.card.UserCardGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bbw.god.login.repairdata.RepairDataConst.REPAIR_CARD_GROUP;

/**
 * 玩家卡组数据修复
 *
 * @author fzj
 * @date 2021/12/6 16:53
 */
@Service
public class RepairUserCardGroupService implements BaseRepairDataService {
    @Autowired
    UserCardGroupService userCardGroupService;
    @Autowired
    GameUserService gameUserService;

    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (lastLoginDate.before(REPAIR_CARD_GROUP)) {
            List<UserCardGroup> userCardGroups = this.userCardGroupService.getUserCardGroups(gu.getId(), CardGroupWay.Normal_Fight);
            if (userCardGroups.size() < 7) {
                List<UserCardGroup> userCardGroupList = new ArrayList<>();
                for (int deck = 6; deck <= 7; deck++) {
                    userCardGroupList.add(UserCardGroup.instance(gu.getId(), deck));
                }
                gameUserService.addItems(userCardGroupList);
            }
        }
    }
}
