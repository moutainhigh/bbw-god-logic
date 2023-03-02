package com.bbw.god.db.pool;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.bbw.god.db.entity.InsUserDataEntity;
import com.bbw.god.db.entity.InsUserEntity;
import com.bbw.god.db.entity.InsUserStatistic;
import com.bbw.god.db.service.InsUserDataService;
import com.bbw.god.db.service.InsUserService;
import com.bbw.god.db.service.InsUserStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * <pre>
 * 区服操作类
 * 使用一下语句获取bean
 *   PlayerDataDAO pdd = SpringContextUtil.getBean(PlayerDataDAO.class, serverId);
 * </pre>
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-21 22:05
 */
@Slf4j
@Service
@Scope("prototype")
@Lazy
public class PlayerDataDAO {
    @Value("${bbw-god.db-batchSize:1000}")
    private int batchSize = 1000;
    private int serverId;
    @Autowired
    private InsUserDataService userDataService;
    @Autowired
    private InsUserService userService;
    @Autowired
    private InsUserStatisticService insUserStatisticService;
    @Autowired
    private JdbcTemplate jdbc;

    @SuppressWarnings("unused")
    private PlayerDataDAO() {

    }

    public PlayerDataDAO(int serverId) {
        this.serverId = serverId;
    }

    public int getServerId() {
        return serverId;
    }

    /**
     * 返回失败的集合
     *
     * @param resList
     * @return
     */
    public List<InsUserDataEntity> dbInsertUserDataBatch(List<InsUserDataEntity> resList) {
        try {
            long begin = System.currentTimeMillis();
            // userDataService.insertBatch(resList, batchSize);
            userDataService.insertOrUpdateBatch(resList, batchSize);
            long end = System.currentTimeMillis();
            if (end - begin > 1000) {
                log.error("sql保存userData业务耗时:" + (end - begin));
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("-------------ins_user_data  Insert失败，逐条保存！-------------");
            // 保存失败则逐条保存
            List<InsUserDataEntity> failList = new ArrayList<>();
            for (InsUserDataEntity entity : resList) {
                try {
                    boolean b = userDataService.insertOrUpdate(entity);
                    if (!b) {
                        log.error(entity.toString());
                        failList.add(entity);
                    }
                } catch (Exception ee) {
                    if (!ee.getMessage().contains("Duplicate entry")) {
                        failList.add(entity);
                    }
                    log.error(entity.toString());
                    log.error(ee.getMessage(), ee);
                }
            }
            return failList;
        }
    }

    public List<InsUserDataEntity> dbUpdateUserDataBatch(List<InsUserDataEntity> resList) {
        try {
            userDataService.insertOrUpdateBatch(resList);
            return new ArrayList<>();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("-------------ins_user_data Update失败，逐条保存！-------------");
            // 保存失败则逐条保存
            List<InsUserDataEntity> failList = new ArrayList<>();
            for (InsUserDataEntity entity : resList) {
                try {
                    boolean b = userDataService.insertOrUpdate(entity);
                    if (!b) {
                        log.error(entity.toString());
                        failList.add(entity);
                    }
                } catch (Exception ee) {
                    failList.add(entity);
                    log.error(entity.toString());
                    log.error(ee.getMessage(), ee);
                }
            }
            return failList;
        }
    }

    public boolean dbDeleteUserDataBatch(Collection<Long> resKeyList) {
//        if (resKeyList == null || resKeyList.size() == 0) {
//            return true;
//        }
//        String ids = resKeyList.stream().map(value -> value.toString()).collect(Collectors.joining(","));
//        log.error(ids);
        try {
//            return userDataService.delete(new EntityWrapper<InsUserDataEntity>().where("data_id in (" + ids + ")"));
            return userDataService.deleteBatchIds(resKeyList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 删除玩家类型数据
     *
     * @param uid
     * @param types eg:"'treasure' ,'special'"
     * @return
     */
    public boolean dbDeleteUserDataByTypes(long uid, String types) {
        try {
            return userDataService.delete(new EntityWrapper<InsUserDataEntity>().where("uid=" + uid + " and data_type " +
                    "in (" + types + ")"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public List<InsUserDataEntity> dbSelectUserData(Long uid) {
        EntityWrapper<InsUserDataEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("uid", uid);
        return userDataService.selectList(wrapper);
    }

    public List<InsUserDataEntity> dbSelectUserDataByType(Long uid, String dataType) {
        EntityWrapper<InsUserDataEntity> wrapper = new EntityWrapper<>();
        wrapper.where("uid = {0} AND data_type={1}", uid, dataType);
        return userDataService.selectList(wrapper);
    }

    public List<InsUserDataEntity> dbSelectUserDataByType(String dataType) {
        EntityWrapper<InsUserDataEntity> wrapper = new EntityWrapper<>();
        wrapper.where("data_type={0}", dataType);
        return userDataService.selectList(wrapper);
    }

    public List<InsUserDataEntity> dbSelectUserDatas(String dataType, String uids) {
        EntityWrapper<InsUserDataEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("data_type", dataType).in("uid", uids);
        return userDataService.selectList(wrapper);
    }

    public List<InsUserEntity> dbUpdateUserBatch(List<InsUserEntity> resList) {
        try {
            userService.updateBatchById(resList, batchSize);
            return new ArrayList<>();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("------------- ins_user Update 失败，逐条保存！--------------------");
            // 保存失败则逐条保存
            List<InsUserEntity> failList = new ArrayList<>();
            for (InsUserEntity entity : resList) {
                try {
                    boolean b = userService.updateById(entity);
                    if (!b) {
                        log.error(entity.toString());
                        failList.add(entity);
                    }
                } catch (Exception ee) {
                    failList.add(entity);
                    log.error(entity.toString());
                    log.error(ee.getMessage(), ee);
                }
            }
            return failList;
        }
    }

    public boolean dbInsertInsUserEntity(InsUserEntity entity) {
        return userService.insert(entity);
    }

    public InsUserEntity dbSelectInsUserEntity(Long uid) {
        return userService.selectById(uid);
    }

    public List<InsUserEntity> dbSelectUsers() {
        return userService.selectList(null);
    }

    /**
     * 根据昵称关键字模糊查询,随机limit结果集
     *
     * @param sid
     * @param limit
     * @param keyword
     * @param exclude
     * @return
     */
    public List<InsUserEntity> dbGetRandResultNicknameLike(int sid, int limit, String keyword, Set<Long> exclude) {
        return userService.getRandResultNicknameLike(sid, limit, keyword, exclude);
    }

    /**
     * 从服务器上获取limit个对象，排除exclude，随机limit结果集
     *
     * @param sid
     * @param limit
     * @param exclude
     * @return
     */
    public List<InsUserEntity> dbGetRandResultfromServer(int sid, int limit, Set<Long> exclude) {
        return userService.getRandResultfromServer(sid, limit, exclude);
    }

    /**
     * 获取指定ID的对象
     *
     * @param include
     * @return
     */
    public List<InsUserEntity> dbGetByIds(Set<Long> include) {
        return userService.selectBatchIds(include);
    }

    public void dbDelPlayers() {
        String[] tables = {"ins_receipt", "ins_user", "ins_user_data", "ins_user_detail"};
        for (String table : tables) {
            jdbc.execute("DELETE  FROM " + table + " WHERE sid=" + serverId);
        }
        for (int i = 0; i < 10; i++) {
            jdbc.execute("DELETE  FROM ins_user_detail_" + i + " WHERE sid=" + serverId);
        }
    }

    /**
     * 返回失败的集合
     *
     * @param resList
     * @return
     */
    public List<InsUserStatistic> dbInsertUserStatisticBatch(List<InsUserStatistic> resList) {
        try {
            long begin = System.currentTimeMillis();
            // userDataService.insertBatch(resList, batchSize);
            insUserStatisticService.insertOrUpdateBatch(resList, batchSize);
            long end = System.currentTimeMillis();
            if (end - begin > 1000) {
                log.error("sql保存userData业务耗时:" + (end - begin));
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("-------------ins_user_Statistic  Insert失败，逐条保存！-------------");
            // 保存失败则逐条保存
            List<InsUserStatistic> failList = new ArrayList<>();
            for (InsUserStatistic entity : resList) {
                try {
                    boolean b = insUserStatisticService.insertOrUpdate(entity);
                    if (!b) {
                        log.error(entity.toString());
                        failList.add(entity);
                    }
                } catch (Exception ee) {
                    if (!ee.getMessage().contains("Duplicate entry")) {
                        failList.add(entity);
                    }
                    log.error(entity.toString());
                    log.error(ee.getMessage(), ee);
                }
            }
            return failList;
        }
    }

    public InsUserStatistic dbGetUserStatisticById(String id) {
        return insUserStatisticService.selectById(id);
    }
}
