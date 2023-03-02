package com.bbw.god.login.repairdata;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.task.main.UserMainTask;
import com.bbw.god.gameuser.task.main.UserMainTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 修复主线任务中的  累计卡牌任务
 * @author：lwb
 * @date: 2021/2/25 10:00
 * @version: 1.0
 */
@Service
public class RepairMainCardsTaskService implements BaseRepairDataService{
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserMainTaskService mainTaskService;
    public static final Date repairBegin = DateUtil.fromDateTimeString("2021-02-25 10:25:00");
    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        try{
            if (lastLoginDate.before(repairBegin)){
                long uid=gu.getId();
                int size = userCardService.getUserCards(uid).size();
                UserMainTask umTask = mainTaskService.getUserMainTask(uid, 1300);
                umTask.setEnableAwardIndex(size);
                if (umTask.getAwardedIndex()>size){
                    umTask.setAwardedIndex(size);
                }
                gameUserService.updateItem(umTask);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
