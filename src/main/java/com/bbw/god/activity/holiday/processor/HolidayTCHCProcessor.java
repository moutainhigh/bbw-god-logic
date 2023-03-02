package com.bbw.god.activity.holiday.processor;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.config.special.SpecialTypeEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.UserSpecialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 特产合成
 * @date 2020-11-13 16:50
 **/
@Service
public class HolidayTCHCProcessor extends AbstractActivityProcessor {
    @Autowired
    private UserSpecialService userSpecialService;

    public HolidayTCHCProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.TCHC);
    }

    /**
     * 该活动类别有多少个可领取的
     *
     * @param gu
     * @param a
     * @return
     */
    @Override
    public int getAbleAwardedNum(GameUser gu, IActivity a) {
        List<CfgSpecialEntity> specials = SpecialTool.getSpecials(SpecialTypeEnum.SYNTHETIC);
        List<UserSpecial> ownSpecials = userSpecialService.getOwnSpecials(gu.getId());
        int num = 0;
        for (CfgSpecialEntity special : specials) {
            List<Integer> materialIds = special.getMaterialIds();
            Integer materialId1 = materialIds.get(0);
            Integer materialId2 = materialIds.get(1);
            boolean match1 = ownSpecials.stream().anyMatch(tmp -> tmp.getBaseId().equals(materialId1));
            boolean match2 = ownSpecials.stream().anyMatch(tmp -> tmp.getBaseId().equals(materialId2));
            if (match1 && match2) {
                num += 1;
            }
        }
        return num;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }
}
