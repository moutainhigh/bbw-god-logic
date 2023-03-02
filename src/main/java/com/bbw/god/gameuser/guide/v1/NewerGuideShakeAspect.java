package com.bbw.god.gameuser.guide.v1;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.guide.NewerGuideService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author suhq
 * @description: 新手引导摇骰子切片
 * @date 2019-12-26 14:41
 **/
@Slf4j
@Aspect
@Component
public class NewerGuideShakeAspect {
    @Autowired
    private NewerGuideService newerGuideService;

    @Around("execution(* com.bbw.god.gameuser.shake.ShakeService.getDiceResult(..))")
    public Object getDiceResult(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        GameUser gu = (GameUser) args[0];
        if (!this.newerGuideService.isPassNewerGuide(gu.getId())) {
            NewerGuideEnum curGuideEnum = NewerGuideEnum.fromValue(this.newerGuideService.getNewerGuide(gu.getId()));
            if (0 == curGuideEnum.getNextStepNum()) {
                throw new ExceptionForClientTip("newer.guide.error");
            }
            log.info("{}新手引导进度{}", gu.getRoleInfo().getNickname(), curGuideEnum);
            return Arrays.asList(curGuideEnum.getNextStepNum());
        }
        return point.proceed();
    }
}
