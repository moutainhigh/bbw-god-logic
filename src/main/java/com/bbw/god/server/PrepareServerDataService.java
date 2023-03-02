package com.bbw.god.server;

import com.bbw.common.DateUtil;
import com.bbw.god.PrepareDataService;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 提前生成区服每日需要的数据
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-21 14:59
 */
@Slf4j
@Service
public abstract class PrepareServerDataService<T extends ServerData> implements PrepareDataService {
    @Autowired
    protected ServerDataService serverData;

    public abstract boolean check(CfgServerEntity server, Date date);

    /**
     * 检查所有区服
     *
     * @param date
     * @return
     */
    @Override
    public boolean check(Date date) {
        List<CfgServerEntity> servers = ServerTool.getAvailableServers();
        boolean result = true;
        for (CfgServerEntity server : servers) {
            boolean b = check(server, date);
            result = b && result;
        }
        return result;
    }

    /**
     * 执行结束后清理临时变量
     */
    protected void clearVar() {
        return;
    }

    /**
     * 生成某一天的数据,需要保存
     *
     * @param sid
     * @param dateInt
     * @param clazz
     * @return
     */
    protected abstract void generateByDate(int sid, int dateInt);

    /**
     * 根据日期获取每天循环的key值
     *
     * @param date
     * @return
     */
    protected String getLoopKeyByDate(Date date) {
        return DateUtil.toString(date, "yyyyMMdd");
    }

    /**
     * 获取类定义的参数化对象
     *
     * @return
     */
    protected Class<T> getTClass() {
        @SuppressWarnings("unchecked")
        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return entityClass;
    }

    @Override
    public void prepareDatas(int days) {
        /// 最多不超过一个月
        int maxDays = days > 31 ? 31 : days;
        // 获取所有有效的服务器
        List<CfgServerEntity> servers = ServerTool.getAvailableServers();
        for (CfgServerEntity server : servers) {
            prepareDatasByServer(server.getId(), maxDays);
        }
        clearVar();
    }

    /**
     * 提前生成某个sid服务器days天的class类型数据
     */
    public void prepareDatasByServer(int sid, int days) {
        Date today = DateUtil.now();
        // 找出目前缺少的数据日期
        List<Integer> noDataDateInt = new ArrayList<Integer>();
        for (int i = 0; i < days; i++) {
            Date nextDay = DateUtil.addDays(today, i);
            List<T> nextDayDatas = getResultByDate(sid, DateUtil.toDateInt(nextDay));
            // 没有数据
            if (nextDayDatas.isEmpty()) {
                noDataDateInt.add(DateUtil.toDateInt(nextDay));
            }
        }
        for (Integer dataInt : noDataDateInt) {
            generateByDate(sid, dataInt);
        }
    }

    /**
     * 获取指定日期的数据
     *
     * @param sid
     * @param dateInt
     * @return
     */
    @NonNull
    public List<T> getResultByDate(int sid, int dateInt) {
        List<T> results = null;
        // 所有记录
        String loopKey = getLoopKeyByDate(DateUtil.fromDateInt(dateInt));// 每日循环的key
        results = serverData.getServerDatas(sid, getTClass(), loopKey);
        if (results.isEmpty()) {
            if (dateInt >= DateUtil.getTodayInt()) {
                String msg = "获取不到[" + dateInt + "]的[" + getTClass().getSimpleName() + "]数据！";
                log.warn(msg);
            }
        }
        return results;
    }
}
