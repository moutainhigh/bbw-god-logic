package com.bbw.god.login.repairdata;

import com.bbw.god.city.chengc.UserCitySetting;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.UserAchievementLogic;
import com.bbw.god.gameuser.card.UserCardGroupService;
import com.bbw.god.gameuser.task.UserTaskService;
import com.bbw.god.mall.cardshop.CardShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

import static com.bbw.god.login.repairdata.RepairDataConst.REPAIR_INIT_DATA;


/**
 * 修复初始化数据
 *
 * @author: suhq
 * @date: 2022/11/9 2:17 下午
 */
@Service
public class RepairInitDataService implements BaseRepairDataService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserTaskService userTaskService;
    @Autowired
    private UserCardGroupService userCardGroupService;
    @Autowired
    private CardShopService cardShopService;
    @Autowired
    private UserAchievementLogic userAchievementLogic;

    /**
     * 修复数据
     *
     * @param gu            玩家对象
     * @param lastLoginDate 上次登录时间
     */
    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        // 源晶转化
        if (null != lastLoginDate && !lastLoginDate.before(REPAIR_INIT_DATA)) {
            return;
        }
        long uid = gu.getId();

        // 初始化任务
        this.userTaskService.initTasks(gu);
        // 初始化城池设置
        UserCitySetting ucSetting = gameUserService.getSingleItem(uid, UserCitySetting.class);
        if (ucSetting == null) {
            ucSetting = UserCitySetting.instance(uid);
            gameUserService.addItem(uid, ucSetting);
        }
        // 初始化卡池
        cardShopService.initUserCardPool(uid);
        // 初始化卡组
        userCardGroupService.initCardGroup(gu, new ArrayList<>());
        //成就初始化
        UserAchievementInfo info = gameUserService.getSingleItem(gu.getId(), UserAchievementInfo.class);
        if (null == info) {
            info = userAchievementLogic.initUserAchievementInfo(gu.getId());
            gameUserService.addItem(gu.getId(), info);
        }
    }
}
