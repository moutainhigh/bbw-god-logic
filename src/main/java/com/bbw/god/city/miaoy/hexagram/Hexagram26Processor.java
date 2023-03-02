package com.bbw.god.city.miaoy.hexagram;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.config.special.SpecialTypeEnum;
import com.bbw.god.gameuser.businessgang.BusinessGangService;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.special.event.EVSpecialAdd;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.gameuser.task.businessgang.UserBusinessGangSpecialtyShippingTask;
import com.bbw.god.gameuser.task.businessgang.UserSpecialtyShippingTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 泽山咸卦
 * <p>
 * 获得当前商帮高级任务特产
 *
 * @author liuwenbin
 */
@Service
public class Hexagram26Processor extends AbstractHexagram {
    @Autowired
    private UserSpecialService userSpecialService;
    @Autowired
    private UserSpecialtyShippingTaskService userSpecialtyShippingTaskService;
    @Autowired
    private BusinessGangService businessGangService;

    @Override
    public int getHexagramId() {
        return 26;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_UP;
    }


    @Override
    public boolean canEffect(long uid) {
        return true;
    }

    @Override
    public void effect(long uid, RDHexagram rd) {
        List<EVSpecialAdd> addSpecials = new ArrayList<>();
        List<UserBusinessGangSpecialtyShippingTask> allTasks = userSpecialtyShippingTaskService.getAllTasks(uid);
        if (allTasks.isEmpty()) {
            CfgSpecialEntity special = SpecialTool.getRandomSpecial(SpecialTypeEnum.HIGH);
            addSpecials.add(EVSpecialAdd.given(special.getId()));
            SpecialEventPublisher.pubSpecialAddEvent(uid, addSpecials, getWay(), rd);
            return;
        }
        UserBusinessGangSpecialtyShippingTask randomTask = PowerRandom.getRandomFromList(allTasks);
        List<Integer> shippingTaskSpecials = businessGangService.getShippingTaskSpecials(randomTask);
        Integer specialId = PowerRandom.getRandomFromList(shippingTaskSpecials);
        addSpecials.add(EVSpecialAdd.given(specialId));
        if (!addSpecials.isEmpty()) {
            SpecialEventPublisher.pubSpecialAddEvent(uid, addSpecials, getWay(), rd);
        }
    }

}
