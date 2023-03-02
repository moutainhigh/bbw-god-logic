package com.bbw.god.gameuser.achievement;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author suhq
 * @description: 成就切片
 * @date 2020-03-03 12:36
 **/
@Slf4j
@Aspect
@Component
public class AchievementAspect {

    /**
     * 统一处理成就监听异常，使得成就监听有异常时不影响主业务逻辑
     * @param point
     */
    @Around("execution(* com.bbw.god.gameuser.achievement.listener.*Listener.*(org.springframework.context.ApplicationEvent+))")
    public void handleExceptionAsListener(ProceedingJoinPoint point){
        try {
            point.proceed();
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage(),e);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.error(throwable.getMessage(),throwable);
        }
    }
}
