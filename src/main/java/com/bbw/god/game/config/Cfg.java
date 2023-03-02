package com.bbw.god.game.config;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.bbw.cache.LocalCache;
import com.bbw.common.SpringContextUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.db.entity.CfgActivityRankEntity;
import com.bbw.god.db.entity.CfgChannelEntity;
import com.bbw.god.db.entity.CfgServerEntity;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;

/**
 * 系统配置信息获取
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-09-30 11:31
 */
@Slf4j
public enum Cfg {
    I;
    private static LocalCache loaclCache = LocalCache.getInstance();

    /**
     * 载入所有配置
     */
    public void loadAllConfig() {
        for (Class<? extends CfgInterface> clazz : FileConfigDao.getDirMap().keySet()) {
            get(clazz);
        }
        prepareList();
        get(CfgActivityEntity.class);
        get(CfgActivityRankEntity.class);
        get(CfgServerEntity.class);
        get(CfgChannelEntity.class);
    }

    /**
     * 预备数据集合
     */
    public void prepareList() {
        for (Class<? extends CfgInterface> clazz : FileConfigDao.getDirMap().keySet()) {
            List<?> clazzs = Arrays.asList(clazz.getInterfaces());
            // 如果是需要预准备数据，则进行与准备
            if (clazzs.contains(CfgPrepareListInterface.class)) {
                get(clazz).forEach(tmp -> {
                    ((CfgPrepareListInterface) tmp).prepare();
                });

            }
        }
    }

    /**
     * 删除缓存
     *
     * @param clazz
     */
    public <T extends CfgInterface> void reload(Class<T> clazz) {
        String type = getTypeKey(clazz);
        loaclCache.clear(type);
        get(clazz);
    }

    public <T extends CfgInterface> void reload(Serializable id, Class<T> clazz) {
        String type = getTypeKey(clazz);
        String key = getDataKey(id, clazz);
        loaclCache.remove(type, key);
        get(clazz);
    }

    /**
     * 仅从本地缓存获取配置，如果缓存中没有，不再从其他地方获取
     *
     * @param id
     * @param clazz
     * @return
     */
    public <T extends CfgInterface> T getFromLocalCache(Serializable id, Class<T> clazz) {
        String type = getTypeKey(clazz);
        String key = getDataKey(id, clazz);
        // 从常驻内存的对象获取
        T ret = loaclCache.get(type, key);
        if (null != ret) {
            return ret;
        }
        return null;
    }

    /**
     * 获取唯一配置。如：封神台
     *
     * @param clazz
     * @return
     */
    public <T extends CfgInterface> T getUniqueConfig(Class<T> clazz) {
        if (FileConfigDao.isFileConfig(clazz)) {
            return get(CfgInterface.FILE_UNIQE_KEY, clazz);// 文件配置
        } else {
            return get(CfgInterface.DB_DEFAULT_ID, clazz);// 数据库配置
        }
    }

    /**
     * 获取配置，如果获取不到，取默认配置
     *
     * @param id
     * @param clazz
     * @param defaultId
     * @return
     */
    public <T extends CfgInterface> T get(Serializable id, Class<T> clazz, Serializable defaultId) {
        T ret = get(id, clazz);
        if (ret == null) {
            ret = get(defaultId, clazz);
        }
        return ret;
    }

    /**
     * <pre>
     * 根据ID获取配置。
     * 先从本地缓存中获取，如果没有，再从文件或者数据库获取。
     * </pre>
     *
     * @param id
     * @param clazz
     * @return
     */
    public <T extends CfgInterface> T get(Serializable id, Class<T> clazz) {
        String type = getTypeKey(clazz);
        String key = getDataKey(id, clazz);
        // 从常驻内存的对象获取
        T ret = loaclCache.get(type, key);
        if (null != ret) {
            return ret;
        }
        int count = loaclCache.getTypeCount(type);
        // 如果本地缓存中没有此类型的对象，则加载所有对象
        if (0 == count) {
            List<T> typeList = get(clazz);
            Optional<T> obj = typeList.stream().filter(value -> (value.getId().equals(id))).findFirst();
            return obj.orElse(null);
        }
        // 如果缓存中没有此对象，则调用dao去获取
        ret = getOne(id, clazz);
        if (null != ret) {
            loaclCache.put(type, key, ret);
            return ret;
        }
        return null;
    }

    /**
     * 获取对象集合，如所有城池、所有卡牌、所有法宝、所有特产等
     *
     * @param clazz
     * @return
     */
    public <T extends CfgInterface> List<T> get(Class<T> clazz) {
        String type = getTypeKey(clazz);
        List<T> list = loaclCache.getTypeList(type);
        if (list.size() > 0) {
            // 获得数据按ID排序
            list.sort(Comparator.comparing(T::getSortId));
            return list;
        }
        if (FileConfigDao.isFileConfig(clazz)) {// 文件配置
            list = FileConfigDao.getByType(clazz);
        } else {
            list = dbGetByType(clazz);// 数据库配置
        }
        if (list == null || list.isEmpty()) {
            throw CoderException.normal(clazz.getName() + "没有任何持久化数据！");
        }

        Map<String, T> values = new HashMap<>();
        for (T t : list) {
            String dataKey = getDataKey(t.getId(), clazz);
            values.put(dataKey, t);
        }
        loaclCache.put(type, values);
        // 获得数据按ID排序
        list.sort(Comparator.comparing(T::getSortId));
        return list;
    }

    /**
     * 重新获取数据，覆盖原有数据
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends CfgInterface> void reloadWithoutClear(Class<T> clazz) {
        List<T> list = null;
        if (FileConfigDao.isFileConfig(clazz)) {// 文件配置
            System.out.println("reload from yml");
            list = FileConfigDao.getByType(clazz);
        } else {
            System.out.println("to load from db " + clazz.getSimpleName());
            list = dbGetByType(clazz);// 数据库配置
        }
        if (list == null || list.isEmpty()) {
            throw CoderException.normal(clazz.getName() + "没有任何持久化数据！");
        }
        Map<String, T> values = new HashMap<>();
        for (T t : list) {
            String dataKey = getDataKey(t.getId(), clazz);
            values.put(dataKey, t);
        }
        String type = getTypeKey(clazz);
        loaclCache.putNew(type, values);
    }

    private <T extends CfgInterface> T getOne(Serializable id, Class<T> clazz) {
        // 是否是文件配置
        if (FileConfigDao.isFileConfig(clazz)) {
            List<T> list = FileConfigDao.getByType(clazz);
            return list.stream().filter(tmp -> tmp.getId().equals(id)).findAny().orElse(null);
        }
        // 从数据库获取
        BaseMapper<T> dao = getDao(clazz);
        System.out.println("getOne " + clazz.getSimpleName() + " id:" + id);
        return dao.selectById(id);
    }

    private <T> List<T> dbGetByType(Class<T> clazz) {
        BaseMapper<T> dao = getDao(clazz);
        List<T> all = dao.selectList(new EntityWrapper<T>());
        return all;
    }

    private <T> String getDataKey(Serializable id, Class<T> clazz) {
        String key = getTypeKey(clazz) + "." + String.valueOf(id);
        return key;
    }

    private <T> String getTypeKey(Class<T> clazz) {
        String key = "cfg." + clazz.getSimpleName();
        return key;
    }

    private <T> BaseMapper<T> getDao(Class entityClazz) {
        try {
            String daoClassName = entityClazz.getName().replace("Entity", "Dao");
            daoClassName = daoClassName.replace("entity", "dao");
            Class clazz = Class.forName(daoClassName);
            @SuppressWarnings("unchecked")
            BaseMapper<T> dao = (BaseMapper<T>) SpringContextUtil.getBean(clazz);
            return dao;
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
