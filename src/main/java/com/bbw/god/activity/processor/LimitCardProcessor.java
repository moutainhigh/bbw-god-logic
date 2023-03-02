package com.bbw.god.activity.processor;

import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author suhq
 * @description: 限定卡牌
 * @date 2019-11-07 09:20
 **/
@Service
public class LimitCardProcessor extends AbstractActivityProcessor {
    @Autowired
    private BoxService boxService;

    public LimitCardProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.LIMIT_CARD);
    }


    @Override
    protected void deliver(long uid, WayEnum way, String caName, List<Award> awards, RDCommon rd) {
        int boxId = awards.get(0).getAwardId();
        this.boxService.open(uid, boxId, way, rd);
    }
}
