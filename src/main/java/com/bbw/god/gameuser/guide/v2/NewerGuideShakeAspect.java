package com.bbw.god.gameuser.guide.v2;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.shake.ShakeEventPublish;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author suhq
 * @description: 新手引导摇骰子切片
 * @date 2019-12-26 14:41
 **/
@Slf4j
//@Aspect
//@Component
public class NewerGuideShakeAspect {
    @Autowired
    private NewerGuideService newerGuideService;

    @Around("execution(* com.bbw.god.gameuser.shake.ShakeService.getDiceResult(..))")
    @Order(1)
    public Object getDiceResult(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        GameUser gu = (GameUser) args[0];
        List<Integer> shakeList = new ArrayList<>();
        if (!this.newerGuideService.isPassNewerGuide(gu.getId())) {
            NewerGuideEnum curGuideEnum = NewerGuideEnum.fromValue(this.newerGuideService.getNewerGuide(gu.getId()));
            log.info("{}新手引导进度{}", gu.getRoleInfo().getNickname(), curGuideEnum);
            if (curGuideEnum.equals(NewerGuideEnum.ATTACK) || curGuideEnum.equals(NewerGuideEnum.LDF_LEVEL_UP)) {
                int position = gu.getLocation().getPosition();
                if (position == NewerGuideEnum.YEGUAI.getPos().intValue()) {
                    shakeList =  Arrays.asList(NewerGuideEnum.YEGUAI.getNextStepNum());
                }
            } else {
                shakeList = Arrays.asList(curGuideEnum.getNextStepNum());
            }
            ShakeEventPublish.pubShakeEvent(shakeList, new BaseEventParam(gu.getId(), WayEnum.SHAKE_DICE));
            return shakeList;
        }
        return point.proceed();
    }
}
