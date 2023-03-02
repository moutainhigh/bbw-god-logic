package com.bbw.god.db.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.bbw.db.datasources.DataSourceContextHolder;
import com.bbw.db.datasources.DynamicDataSource;
import com.bbw.db.datasources.godserver.GodServerDataSource;
import com.bbw.exception.CoderException;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.service.LogicInsReceiptService;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.pay.DispatchProduct;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 区服数据库切换
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-14 22:19
 */
@Aspect
@Component
public class DataDAOAspect {

    @Around("execution(* com.bbw.god.db.service.LogicInsReceiptService.db*(..))")
    public Object logicInsReceiptService(ProceedingJoinPoint point) throws Throwable {
        // 获取目标区服
        LogicInsReceiptService so = (LogicInsReceiptService) point.getThis();
        int serverId = so.getServerId();
        return switchDataSource(point, serverId);
    }

    @Around("execution(* com.bbw.god.pay.DispatchProduct.db*(..))")
    public Object dispatchProductAround(ProceedingJoinPoint point) throws Throwable {
        // 获取目标区服
        DispatchProduct so = (DispatchProduct) point.getThis();
        int serverId = so.getServerId();
        return switchDataSource(point, serverId);
    }

    @Around("execution(* com.bbw.god.db.pool.PlayerDataDAO.db*(..))")
    public Object playerDataDAOAround(ProceedingJoinPoint point) throws Throwable {
        // 获取目标区服
        PlayerDataDAO so = (PlayerDataDAO) point.getThis();
        int serverId = so.getServerId();
        return switchDataSource(point, serverId);
    }

    @Around("execution(* com.bbw.god.db.pool.ServerDataDAO.db*(..))")
    public Object serverDataDAOAround(ProceedingJoinPoint point) throws Throwable {
        // 获取目标区服
        ServerDataDAO so = (ServerDataDAO) point.getThis();
        int serverId = so.getServerId();
        return switchDataSource(point, serverId);
    }

    @Around("execution(* com.bbw.god.db.pool.DetailDataDAO.db*(..))")
    public Object detailDataDAOAround(ProceedingJoinPoint point) throws Throwable {
        // 获取目标区服
        DetailDataDAO so = (DetailDataDAO) point.getThis();
        int serverId = so.getSid();
        return switchDataSource(point, serverId);
    }

    // @Around("execution(*
    // com.bbw.god.gm.ClearTestDataService.clearUserData*(..))")
    // public Object clearTestDataService(ProceedingJoinPoint point) throws
    // Throwable {
    // // 获取目标区服
    // Object[] args = point.getArgs();
    // int serverId = (Integer) args[0];
    // return switchDataSource(point, serverId);
    // }

    private Object switchDataSource(ProceedingJoinPoint point, int serverId) throws Throwable {
        CfgServerEntity destServer = Cfg.I.get(serverId, CfgServerEntity.class);
        if (null == destServer) {
            throw CoderException.high("不存在id=[" + serverId + "]的服务器！");
        }

        String dsKeyName = String.valueOf(serverId);
        DynamicDataSource ds = DynamicDataSource.getInstance();
        //
        if (!ds.contains(dsKeyName)) {
            //log.error("创建[" + destServer.getId() + "]数据库连接!");
            DruidDataSource serverDs = GodServerDataSource.I.getDataSource(destServer.getConnString());
            ds.addDataSource(dsKeyName, serverDs);
        }
        //
        try {
            DataSourceContextHolder.setServer(dsKeyName);
            return point.proceed();
        } catch (Exception e) {
            throw e;
        } finally {
            DataSourceContextHolder.clearServer();
        }
    }

}
