package com.bbw.god.activity.worldcup;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.worldcup.entity.UserSuper16Info;
import com.bbw.god.cache.tmp.AbstractTmpDataRedisService;
import com.bbw.god.cache.tmp.TmpDataType;
import org.springframework.stereotype.Service;

/**
 * 超级16强 redis 临时数据类
 * @author: hzf
 * @create: 2022-11-21 15:46
 **/
@Service
public class UserSuper16InfoService extends AbstractTmpDataRedisService<UserSuper16Info,Long> {

    @Override
    protected Class<UserSuper16Info> getDataClazz() {
        return UserSuper16Info.class;
    }

    @Override
    protected Long getDataBelong(UserSuper16Info data) {
        return data.getGameUserId();
    }

    @Override
    protected String getDataLoop(UserSuper16Info data) {
        return "";
    }

    @Override
    protected String getRedisKey(Long belong, TmpDataType dataType, String... loop) {
        return "game:worldCup:usr:"+belong+":"+dataType.getRedisKey();
    }

    @Override
    protected Long getField(UserSuper16Info data) {
        return data.getId();
    }

    @Override
    protected long getExpiredMillis(UserSuper16Info data) {
        return DateUtil.SECOND_ONE_DAY * 30 * 1000L;
    }
}
