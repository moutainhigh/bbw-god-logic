package com.bbw.db.datasources.aspect;

import com.bbw.db.datasources.DataSourceContextHolder;
import com.bbw.db.datasources.DataSourceNames;
import com.bbw.db.datasources.annotation.DataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 多数据源，切面处理类
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-09 14:52
 */
@Aspect
@Component
public class DataSourceAspect implements Ordered {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("@annotation(com.bbw.db.datasources.annotation.DataSource)")
    public void dataSourcePointCut() {

    }

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        DataSource ds = method.getAnnotation(DataSource.class);
        try {
            if (ds == null) {
                DataSourceContextHolder.setServer(DataSourceNames.FIRST);
                logger.debug("set datasource is " + DataSourceNames.FIRST);
            } else {
                logger.debug("set datasource is " + ds.name());
            }
            return point.proceed();
        } catch (Exception e) {
            throw e;
        } finally {
            DataSourceContextHolder.clearServer();
            logger.debug("clean datasource");
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
