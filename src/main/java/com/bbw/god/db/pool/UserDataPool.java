package com.bbw.god.db.pool;

import com.bbw.common.SpringContextUtil;
import com.bbw.common.StrUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.exception.ErrorLevel;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsUserDataEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.redis.UserRedisKey;
import com.bbw.mc.mail.MailAction;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 * 玩家数据
 * </pre>
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-13 23:15
 */
@Slf4j
@Service
public class UserDataPool extends DataPool {
    @Autowired
    private RedisValueUtil<UserData> userDataRedis;// 玩家资源数据对象，存放UserData对象
    // 业务
    private static final String BUSINESS_KEY = BASE_KEY + RedisKeyConst.SPLIT + "userdata";
    // 用户数据
    private static final String DATA_TYPE = "UserData";
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private MailAction notify;

    private ArrayList<InsUserDataEntity> getEntityList(Set<String> poolKey) {
        long begin = System.currentTimeMillis();
        // 玩家ID.资源类型.资源ID
        List<UserData> datas = userDataRedis.getBatch(poolKey);
        if (datas.isEmpty()) {
            log.error("无法获取到玩家数据。" + poolKey);
            return new ArrayList<>();
        }
        ArrayList<InsUserDataEntity> resList = new ArrayList<>(datas.size());
        for (int i = 0; i < datas.size(); i++) {
            if (null == datas.get(i)) {
                continue;
            }
            UserData data = null;
            try {
                data = datas.get(i);
            } catch (Exception e) {
                System.out.println(datas.get(i));
                continue;
            }
            try {
                CfgServerEntity server = gameUserService.getOriServer(data.getGameUserId());
                if (server == null) {
                    log.error("玩家id={}，区服不存在", data.getGameUserId());
                    server = ServerTool.getServer(gameUserService.getActiveSid(data.getGameUserId()));
                }
                InsUserDataEntity resEntity = InsUserDataEntity.fromUserData(data, server);
                resList.add(resEntity);
            } catch (Exception e) {
                log.error(e.getMessage() + "," + data.toString(), e);
            }
        }
        // for (UserData data : datas) {
        // if (null == data) {
        // continue;
        // }
        // try {
        // InsUserDataEntity resEntity = InsUserDataEntity.fromUserData(data);
        // resList.add(resEntity);
        // } catch (Exception e) {
        // log.error(e.getMessage(), e);
        // }
        // }
        long end = System.currentTimeMillis();
        if ((end - begin) > 1000) {
            log.error("构造InsUserDataEntity耗时:" + (end - begin));
        }
        return resList;
    }

    @Override
    protected DBResult<String> dbInsert(PoolKeys poolKeys) {
        // 实际需要 插入 的数据是【插入】减去【删除】
        Set<String> insertKeys = dataPoolKeySet.difference(poolKeys.getInsertPoolKey(), poolKeys.getDeletePoolKey());
        log.info("需要保存的userData key数量：" + insertKeys.size());
        if (null == insertKeys || insertKeys.isEmpty()) {
            return new DBResult<String>();
        }
        long bb = System.currentTimeMillis();
        ArrayList<InsUserDataEntity> insertList = getEntityList(insertKeys);
        long ee = System.currentTimeMillis();
        if (null == insertList || insertList.isEmpty()) {
            return new DBResult<String>();
        }
        if (ee - bb > 1000) {
            log.error("获取redis[" + insertList.size() + "]条用户数据。耗时：" + (ee - bb));
        }

        // 按照 区服ID分组
        Map<Integer, List<InsUserDataEntity>> serverEntity = insertList.stream().collect(Collectors.groupingBy(InsUserDataEntity::getSid));

        DBResult<String> result = new DBResult<>();
        // 保存
        for (Integer serverId : serverEntity.keySet()) {
            CfgServerEntity server = Cfg.I.get(serverId, CfgServerEntity.class);
            if (null == server) {
                continue;
            }
            PlayerDataDAO surop = SpringContextUtil.getBean(PlayerDataDAO.class, serverId);
            List<InsUserDataEntity> list = serverEntity.get(serverId);
            long begin = System.currentTimeMillis();
            List<InsUserDataEntity> failList = surop.dbInsertUserDataBatch(list);
            long end = System.currentTimeMillis();
            if (list.size() / 1000 > (end - begin) / 1000) {
                log.error(list.size() + "条InsUserDataEntity保存到[" + serverId + "]区服数据库，耗时太久:" + (end - begin));
            }
            if (!failList.isEmpty()) {
                for (InsUserDataEntity dataEntity : failList) {
                    result.failureAdd(UserRedisKey.getUserDataKey(dataEntity));
                }
                result.setSuccessSize(result.getSuccessSize() + list.size() - failList.size());
            } else {
                result.setSuccessSize(result.getSuccessSize() + list.size());
            }
        }
        return result;
    }

    @Override
    protected DBResult<String> dbUpdate(PoolKeys poolKeys) {
        // 实际需要 更新 的数据是【更新】减去【删除】减去【插入】
        ArrayList<String> difference = new ArrayList<String>();
        difference.add(poolKeys.getInsertPoolKey());
        difference.add(poolKeys.getDeletePoolKey());
        Set<String> updateKeys = dataPoolKeySet.difference(poolKeys.getUpdatePoolKey(), difference);
        if (null == updateKeys || updateKeys.isEmpty()) {
            return new DBResult<String>();
        }
        return dbUpdate(updateKeys);
    }

    public DBResult<String> dbUpdate(Set<String> updateKeys) {
        ArrayList<InsUserDataEntity> updateList = getEntityList(updateKeys);
        Map<Integer, List<InsUserDataEntity>> serverEntity = updateList.stream().collect(Collectors.groupingBy(InsUserDataEntity::getSid));

        DBResult<String> result = new DBResult<>();
        // 保存
        for (Integer serverId : serverEntity.keySet()) {
            CfgServerEntity server = Cfg.I.get(serverId, CfgServerEntity.class);
            if (null == server) {
                continue;
            }
            PlayerDataDAO surop = SpringContextUtil.getBean(PlayerDataDAO.class, serverId);
            List<InsUserDataEntity> list = serverEntity.get(serverId);
            List<InsUserDataEntity> failList = surop.dbUpdateUserDataBatch(list);
            if (!failList.isEmpty()) {
                for (InsUserDataEntity dataEntity : failList) {
                    result.failureAdd(UserRedisKey.getUserDataKey(dataEntity));
                }
                result.setSuccessSize(result.getSuccessSize() + list.size() - failList.size());
            } else {
                result.setSuccessSize(result.getSuccessSize() + list.size());
            }
        }

        return result;
    }

    @Override
    protected DBResult<String> dbDelete(String deletePoolKey) {
        Set<String> deleteKeys = dataPoolKeySet.members(deletePoolKey);
        if (null != deleteKeys && deleteKeys.size() > 100000) {
            notify.notifyCoder(ErrorLevel.HIGH, "delete缓冲池[" + deletePoolKey + "]待同步删除的数据条数达[" + deleteKeys.size() + "]", "");
        }
        return doDbDelete(deleteKeys);
    }

    public DBResult<String> doDbDelete(Set<String> deleteKeys) {
        if (null == deleteKeys || deleteKeys.isEmpty()) {
            return new DBResult<>();
        }
        // 分拣到各个区服
        HashMap<Integer, ArrayList<Long>> serverSplit = new HashMap<>();
        HashMap<Integer, ArrayList<String>> serverSplitDeleteKeys = new HashMap<>();
        for (String deleteKey : deleteKeys) {
            UserIdEntity idEntity = this.getIdEntity(deleteKey);
            if (!serverSplit.containsKey(idEntity.getServerId())) {
                ArrayList<Long> resList = new ArrayList<>();
                serverSplit.put(idEntity.getServerId(), resList);
                //
                ArrayList<String> resListDeleteKeys = new ArrayList<>();
                serverSplitDeleteKeys.put(idEntity.getServerId(), resListDeleteKeys);
            }
            ArrayList<Long> resList = serverSplit.get(idEntity.getServerId());
            resList.add(idEntity.getResId());
        }
        DBResult<String> result = new DBResult<>();
        // 按照区服删除
        for (Integer serverKey : serverSplit.keySet()) {
            CfgServerEntity server = Cfg.I.get(serverKey, CfgServerEntity.class);
            if (null == server) {
                continue;
            }
            ArrayList<Long> resKeyList = serverSplit.get(serverKey);
            PlayerDataDAO surop = SpringContextUtil.getBean(PlayerDataDAO.class, serverKey);
            if (!surop.dbDeleteUserDataBatch(resKeyList)) {
                result.failureAdd(serverSplitDeleteKeys.get(serverKey));
            } else {
                result.setSuccessSize(resKeyList.size());
            }
        }
        return result;
    }

    /**
     * <pre>
     * 根据rediskey获取信息。
     * 解析以下类型的key:
     * 类型1格式如  前缀:玩家ID:数据类型
     * 类型2格式如  前缀:玩家ID:数据类型:资源ID
     * 实现方法依赖getNextPlayerId方法的实现。
     * </pre>
     *
     * @param redisKey
     * @return
     */
    private UserIdEntity getIdEntity(String redisKey) {
        UserIdEntity id = new UserIdEntity();
        String[] keyParts = redisKey.split(UserRedisKey.SPLIT);
        id.setPlayerId(keyParts[1]);
        id.setServerId(gameUserService.getActiveSid(Long.parseLong(keyParts[1])));
        id.setResType(UserDataType.fromRedisKey(keyParts[2]));
        // 如果
        if (keyParts.length > 3) {
            // 如果第四部分长度大于ID值的最小长度，并且是数字，判定为资源ID
            if (StrUtil.isDigit(keyParts[3])) {
                id.setResId(Long.valueOf(keyParts[3]));
            }
        }
        return id;
    }

    @Data
    private class UserIdEntity {
        private Long playerId;// 区服玩家ID
        private UserDataType resType;// 资源类型
        private Long resId;// 资源ID
        private Integer serverId;// 服务器ID
        // private Integer regSeq;// 区服注册排序号
        // private Integer regTime;// 8位时间(yyMMddHH)

        @SuppressWarnings("unused")
        private void setPlayerId(Long l) {

        }

        /**
         * 依赖GameUserRedisUtil.getNewPlayerId方法 6位日期(yyMMdd)+4位原始区服ID+5位区服玩家计数器
         *
         * @param sPlayerId
         */
        public void setPlayerId(String sPlayerId) {
            playerId = Long.parseLong(sPlayerId);
        }
    }

    @Override
    protected String getBaseKey() {
        return BUSINESS_KEY;
    }

    @Override
    protected String getDataType() {
        return DATA_TYPE;
    }
}
