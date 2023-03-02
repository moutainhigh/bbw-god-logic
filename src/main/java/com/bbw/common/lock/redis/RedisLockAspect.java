package com.bbw.common.lock.redis;

import com.bbw.common.StrUtil;
import com.bbw.common.lock.redis.annotation.RedisLock;
import com.bbw.common.lock.redis.annotation.RedisLockParam;
import com.bbw.god.game.data.redis.RedisKeyConst;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 以切片的方式对业务进行分布式锁处理
 *
 * @author: suhq
 * @date: 2022/1/4 11:16 上午
 */
@Slf4j
@Component
@Aspect
public class RedisLockAspect {
    /** spel解析器 */
    private final ExpressionParser spelParser = new SpelExpressionParser();

    @Autowired
    private RedisLockService redisLockService;

    @Around(value = "@annotation(com.bbw.common.lock.redis.annotation.RedisLock)")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        // 获取注解配置的锁key值
        Method method = getMethod(jp);
        String lockKey = getLockKey(jp, method);
//        System.out.println("==========" + lockKey);

        RedisLock redisLock = method.getAnnotation(RedisLock.class);
        String error = redisLock.error();
        //加锁
        String locker = Thread.currentThread().getName();
        boolean success = redisLockService.lock(lockKey, locker, redisLock);
        if (!success) {
            throw new RedisLockException(error);
        }

        try {
            return jp.proceed();
        } finally {
            redisLockService.unlock(lockKey, locker);
        }
    }

    /**
     * 获取切片的方法
     *
     * @param jp
     * @return
     * @throws NoSuchMethodException
     */
    private Method getMethod(ProceedingJoinPoint jp) throws NoSuchMethodException {
        Signature signature = jp.getSignature();
        MethodSignature ms = (MethodSignature) signature;
        Method method = jp.getTarget().getClass().getMethod(ms.getName(), ms.getParameterTypes());
        return method;
    }

    /**
     * 获取锁的key值
     * 获取 RedisLockRequest 对应的 HttpServletRequest 请求参数的值
     * 获取 RedisLockParam 对应参数的值
     *
     * @param jp
     * @param method
     * @return
     * @throws Throwable
     */
    private String getLockKey(ProceedingJoinPoint jp, Method method) {
        //获取 RedisLockParam 对应参数的值
        Parameter[] parameters = method.getParameters();
        StringBuilder paramKey = new StringBuilder();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object arg = jp.getArgs()[i];

            RedisLockParam redisLockParam = parameter.getAnnotation(RedisLockParam.class);
            if (null == redisLockParam) {
                continue;
            }

            paramKey.append(RedisKeyConst.SPLIT);

            String spel = redisLockParam.spel();
            //spel为空,返回参数本身的toString()作为锁的key值
            if (StringUtils.isEmpty(spel)) {
                if (arg instanceof RedisLockable) {
                    RedisLockable lockable = (RedisLockable) arg;
                    paramKey.append(lockable.key());
                } else {
                    paramKey.append(arg.toString());
                }
            } else {
                //spel不为空,返回根据SpEl表达式解析参数对象
                paramKey.append(parseSpel(arg, spel));
            }
        }

        RedisLock redisLock = method.getAnnotation(RedisLock.class);
        //构建完整的lockKey
        StringBuilder lockKey = new StringBuilder();
        lockKey.append(redisLock.key());
        if (StrUtil.isNotBlank(paramKey.toString())) {
            lockKey.append(paramKey);
        }
        return lockKey.toString();
    }

    /**
     * 根据spel解析对象
     *
     * @param root
     * @param spel
     * @return
     */
    private String parseSpel(Object root, String spel) {
        try {
            StandardEvaluationContext context = new StandardEvaluationContext(root);
            context.addPropertyAccessor(new MapAccessor());
            Expression expression = spelParser.parseExpression("#{" + spel + "}", ParserContext.TEMPLATE_EXPRESSION);
            Object cal = expression.getValue(context);
            return cal.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RedisLockException("Unsupported redis lock param value: " + spel + ".");
        }
    }
}
