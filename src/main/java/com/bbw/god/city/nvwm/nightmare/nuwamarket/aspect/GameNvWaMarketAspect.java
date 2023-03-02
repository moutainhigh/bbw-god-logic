package com.bbw.god.city.nvwm.nightmare.nuwamarket.aspect;

import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 梦魇女娲庙切片
 * TODO 需优化写法
 *
 * @author fzj
 * @date 2022/6/15 16:08
 */
//@Aspect
//@Component
@Slf4j
public class GameNvWaMarketAspect {
    private static final String ADD_LOG_INFO = "女娲集市数据新增";
    private static final String get_LOG_INFO = "女娲集市数据查询";
    private static final String UPDATE_LOG_INFO = "女娲集市数据更新";
    private static final GameDataType NEED_DATE = GameDataType.NV_WA_MARKET;
    private static final String CLASS_NAME = "com.bbw.god.city.nvwm.nightmare.nuwamarket.GameNvWaBooth";

    @Around("execution(* com.bbw.god.game.data.GameDataServiceImpl.getGameData(..))")
    public Object getGameNvWaBooth(ProceedingJoinPoint point) throws Throwable {
        Object args = point.proceed();
        if (!isNeedData(args)) {
            return args;
        }
        log.info(get_LOG_INFO);
        return args;
    }


    @Around("execution(* com.bbw.god.game.data.GameDataServiceImpl.addGameData(..))")
    public void addGameNvWaBooth(ProceedingJoinPoint point) throws Throwable {
        point.proceed();
        Object args = point.getArgs()[0];
        if (!isNeedData(args)) {
            return;
        }
        log.info(ADD_LOG_INFO);
    }


    @Around("execution(* com.bbw.god.game.data.GameDataServiceImpl.updateGameData(..))")
    public void updateGameNvWaBooth(ProceedingJoinPoint point) throws Throwable {
        point.proceed();
        Object args = point.getArgs()[0];
        if (!isNeedData(args)) {
            return;
        }
        log.info(UPDATE_LOG_INFO);
    }

    @Around("execution(* com.bbw.god.game.data.GameDataServiceImpl.updateGameDatas(..))")
    public void batchUpdateGameNvWaBooth(ProceedingJoinPoint point) throws Throwable {
        point.proceed();
        Object[] args = point.getArgs();
        List<GameData> gameData = (List<GameData>) args[0];
        for (GameData data : gameData) {
            GameDataType gameDataType = data.gainDataType();
            if (!gameDataType.equals(NEED_DATE)) {
                continue;
            }
            log.info(UPDATE_LOG_INFO);
        }
    }

    /**
     * 是否是需要数据
     *
     * @param point
     * @return
     */
    private boolean isNeedData(Object point) {
        String name = point.getClass().getName();
        return CLASS_NAME.equals(name);
    }
}
