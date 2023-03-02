package com.bbw.god.activity.processor;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.login.DynamicMenuEnum;
import com.bbw.god.login.RDNoticeInfo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description: 七日之约
 * @date 2019-11-07 09:20
 **/
@Service
public class SevenLoginProcessor extends AbstractActivityProcessor {

    private static final DynamicMenuEnum[] types={DynamicMenuEnum.NEW_USER_SEVEN_LOGIN_1,DynamicMenuEnum.NEW_USER_SEVEN_LOGIN_2,DynamicMenuEnum.NEW_USER_SEVEN_LOGIN_3,DynamicMenuEnum.NEW_USER_SEVEN_LOGIN_4
            ,DynamicMenuEnum.NEW_USER_SEVEN_LOGIN_5, DynamicMenuEnum.NEW_USER_SEVEN_LOGIN_6,DynamicMenuEnum.NEW_USER_SEVEN_LOGIN_7};
    public SevenLoginProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.SEVEN_LOGIN);
    }

    public RDNoticeInfo.ActivityShow showMenuIcon(GameUser gu){
        IActivity iActivity = activityService.getActivity(gu.getServerId(), ActivityEnum.SEVEN_LOGIN);
        if (null == iActivity) {
            return null;
        }
        ActivityEnum activityEnum = ActivityEnum.fromValue(iActivity.gainType());
        List<UserActivity> uas = this.activityService.getUserActivities(gu.getId(), iActivity.gainId(), activityEnum);
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(activityEnum);
        cas=cas.stream().sorted(Comparator.comparing(CfgActivityEntity::getId)).collect(Collectors.toList());
        for (int i = 0; i < cas.size(); i++) {
            CfgActivityEntity ca=cas.get(i);
            UserActivity ua = uas.stream().filter(uaTmp -> uaTmp.getBaseId().intValue() == ca.getId()).findFirst().orElse(null);
            AwardStatus status = this.getUAStatus(gu, iActivity, ua, ca);
            if (status == AwardStatus.ENABLE_AWARD) {
                //可领取
                return RDNoticeInfo.ActivityShow.instance(types[i].getVal(),1,0L);
            }else if (status.getValue() < AwardStatus.ENABLE_AWARD.getValue()){
                //不可领取
                long interval = DateUtil.millisecondsInterval(DateUtil.getDateEnd(new Date()),new Date());
                return RDNoticeInfo.ActivityShow.instance(types[i].getVal(),0,interval);
            }
        }
        return null;
    }
}
