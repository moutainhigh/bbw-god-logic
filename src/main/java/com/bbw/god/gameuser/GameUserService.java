package com.bbw.god.gameuser;

import com.bbw.god.db.entity.CfgServerEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 玩家属性管理
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-29 21:39
 */
public interface GameUserService {
    /**
     * 仅获取user数据
     *
     * @param uid
     * @return
     */
    GameUser getGameUser(long uid);

    /**
     * 获取user数据，如果未加载玩家其他数据，则一同加载
     *
     * @param uid
     * @return
     */
    GameUser getGameUserWithUserData(long uid);

    // 是否在Redis中
    boolean isInRedis(long uid);

    /**
     * 获取玩家的区服ID
     *
     * @param uid
     * @return
     */
    int getActiveSid(long uid);

    /**
     * 获取玩家的区服所在平台ID
     *
     * @param uid
     * @return
     */
    int getActiveGid(long uid);

    /**
     * 获取角色账号
     *
     * @param uid
     * @return
     */
    String getAccount(long uid);

    CfgServerEntity getOriServer(long uid);

    /**
     * 登录时设置
     *
     * @param uid
     * @param sid
     * @return
     */
    void setActiveSid(Long uid, Integer sid);

    /**
     * 添加用户实例数据
     */
    void addItem(long uid, UserData data);

    <T extends UserData> void addItems(List<T> datas);

    /**
     * 更新拥有项
     */
    void updateItem(UserData item);

    /**
     * 批量更新数据
     *
     * @param items
     */
    <T extends UserData> void updateItems(List<T> items);

    /**
     * 删除拥有项
     */
    void deleteItem(UserData item);

    <T extends UserData> void deleteItems(long uid, List<Long> ids, Class<T> objClass);

    <T extends UserData> void deleteItems(long uid, List<T> objs);

    /**
     * 获取用户数据
     *
     * @param uid
     * @param dataId
     * @param objClass
     * @return
     */
    @NonNull
    <T extends UserData> Optional<T> getUserData(long uid, long dataId, Class<T> objClass);


    /**
     * 获取用户数据
     *
     * @param uid
     * @param dataIds
     * @param objClass
     * @return
     */
    @NonNull
    <T extends UserData> List<T> getUserDatas(long uid, Collection<Long> dataIds, Class<T> objClass);

    /**
     * 获取数据数量
     *
     * @param uid
     * @param objClass
     * @return
     */
    <T extends UserData> Long getDataCount(Long uid, Class<T> objClass);

    /**
     * 玩家拥有集。按照资源ID排序。即，按照获得的时间顺序正序排序。<br/>
     * 没有记录返回大小为0的集合
     *
     * @param uid
     * @param objClass
     * @return 没有记录返回大小为0的集合
     */
    @NonNull
    <T extends UserData> List<T> getMultiItems(long uid, Class<T> objClass);

    // 获取单个对象
    @Nullable
    <T extends UserSingleObj> T getSingleItem(long uid, Class<T> objClass);

    <T extends UserCfgObj> T getCfgItem(long uid, int baseId, Class<T> objClass);

    /**
     * 根据基础ID集获得玩家数据对象列表
     *
     * @param uid
     * @param baseIds
     * @param objClass
     * @return
     */
    @NonNull
    <T extends UserCfgObj> List<T> getCfgItems(long uid, List<Integer> baseIds, Class<T> objClass);

    <T extends UserCfgObj> boolean isExistCfgItem(long uid, int baseId, Class<T> objClass);
}
