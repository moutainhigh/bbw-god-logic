package com.bbw.god.game.award.impl;

import com.alibaba.fastjson.JSON;
import com.bbw.common.PowerRandom;
import com.bbw.common.StrUtil;
import com.bbw.exception.CoderException;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardRandomService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.special.event.EVSpecialAdd;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.gameuser.task.daily.event.DailyTaskAddPoint;
import com.bbw.god.gameuser.task.daily.event.DailyTaskEventPublisher;
import com.bbw.god.gameuser.task.godtraining.event.GodTrainingTaskEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.random.service.RandomParam;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-02-21 16:28
 */
public class DefaultAwardService extends AwardService<Award> {
    @Override
    public void fetchAward(long uid, String awardJson, WayEnum way, String broadcastWayInfo, RDCommon rd) {
        List<Award> awards = parseAwardJson(awardJson,Award.class);
        fetchAward(uid, awards, way, broadcastWayInfo, rd);
    }
}
