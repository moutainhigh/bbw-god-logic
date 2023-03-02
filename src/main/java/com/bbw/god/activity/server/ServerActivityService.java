package com.bbw.god.activity.server;

import com.bbw.cache.ServerCacheService;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityParentTypeEnum;
import com.bbw.god.activity.config.ActivityScopeEnum;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.server.ServerDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ServerActivityService {
    @Autowired
    private ServerCacheService serverCacheService;
    @Autowired
    private ServerDataService serverDataService;

    /**
     * 获得区服活动实例集合
     *
     * @param sId
     * @param pType
     * @return
     */
    public List<ServerActivity> getServerActivities(int sId, ActivityParentTypeEnum pType) {
        List<ServerActivity> sas = getServerActivities(sId);
        List<ServerActivity> validSas = sas.stream().filter(sa -> sa.getParentType() == pType.getValue() && sa.ifTimeValid()).collect(Collectors.toList());
        return validSas;
    }

    /**
     * 获得区服活动实例
     *
     * @param sId
     * @param ca
     * @return
     */
    public ServerActivity getSa(int sId, CfgActivityEntity ca) {
        if (ca.getScope() != ActivityScopeEnum.SERVER.getValue()) {
            return null;
        }
        return getServerActivities(sId).stream().filter(sa -> sa.getType().intValue() == ca.getType() && sa.ifTimeValid()).findFirst().orElse(null);
    }

    /**
     * 获得所有活动，包括历史活动
     *
     * @param sId
     * @param ca
     * @return
     */
    public List<IActivity> getSasIncludeHistory(int sId, CfgActivityEntity ca) {
        if (ca.getScope() != ActivityScopeEnum.SERVER.getValue()) {
            return null;
        }
        return getServerActivities(sId).stream().filter(sa -> sa.getType().intValue() == ca.getType()).collect(Collectors.toList());
    }

    /**
     * 获得区服所有活动(基于本地缓存)
     *
     * @param sId
     * @return
     */
    public List<ServerActivity> getServerActivities(int sId) {
        return serverCacheService.getServerDatas(sId, ServerActivity.class);
    }


    /**
     * 添加活动
     *
     * @param sas
     */
    public void addServerActivities(List<ServerActivity> sas) {
        if (ListUtil.isEmpty(sas)) {
            return;
        }
        serverDataService.addServerData(sas);
    }

    /**
     * 删除活动
     *
     * @param sas
     */
    public void delServerActivities(List<ServerActivity> sas) {
        serverDataService.deleteServerDatas(sas, ServerActivity.class);
    }


    public void updateServerActivities(List<ServerActivity> sas) {
        if (ListUtil.isEmpty(sas)) {
            return;
        }
        serverDataService.updateServerData(sas);
    }
}
