package com.bbw.god.game.zxz.service;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.zxz.cfg.award.ZxzAwardTool;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 诛仙阵奖励service
 * @author: hzf
 * @create: 2022-10-13 13:59
 **/
@Service
public class ZxzAwadService {
    @Autowired
    private AwardService awardService;
    /**
     * 下发奖励
     * @param uid
     * @param num
     * @param rd
     */
    public void open(Long uid, Integer num, RDCommon rd) {
        List<Award> awardList = ZxzAwardTool.getRandomOrigin();
        List<Award> awards = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Award award = PowerRandom.getRandomFromList(awardList);
            awards.add(award);
        }
        awardService.sendNeedMergedAwards(uid, awards, WayEnum.ZXZ_DROP_AWARD,WayEnum.ZXZ_DROP_AWARD.getName(), rd);
    }

}
