package com.bbw.god.cache.tmp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 临时数据服务工厂类
 *
 * @author: suhq
 * @date: 2022/11/23 4:00 下午
 */
@Service
public class TmpDataRedisServiceFactory {
    @Autowired
    private List<AbstractTmpDataRedisService> tmpDataRedisServices;


    /**
     * 获取临时数据服务
     *
     * @param tmpDataType
     * @return
     */
    public Optional<AbstractTmpDataRedisService> getService(TmpDataType tmpDataType) {
        Optional<AbstractTmpDataRedisService> optional = tmpDataRedisServices.stream().filter(tmp -> tmp.isMatch(tmpDataType)).findFirst();
        return optional;
    }
}
