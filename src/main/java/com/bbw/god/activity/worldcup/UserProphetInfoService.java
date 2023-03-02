package com.bbw.god.activity.worldcup;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.worldcup.entity.UserProphetInfo;
import com.bbw.god.cache.tmp.AbstractTmpDataRedisService;
import com.bbw.god.cache.tmp.TmpDataType;
import org.springframework.stereotype.Service;

/**
 * 我是预言家 redis 临时数据
 * @author: hzf
 * @create: 2022-11-21 16:00
 **/
@Service
public class UserProphetInfoService extends AbstractTmpDataRedisService<UserProphetInfo,Long> {
    @Override
    protected Class<UserProphetInfo> getDataClazz() {
        return UserProphetInfo.class;
    }

    @Override
    protected Long getDataBelong(UserProphetInfo data) {
        return data.getGameUserId();
    }

    @Override
    protected String getDataLoop(UserProphetInfo data) {
        return "";
    }

    @Override
    protected String getRedisKey(Long belong, TmpDataType dataType, String... loop) {
        return "game:worldCup:usr:"+belong+":"+dataType.getRedisKey();
    }

    @Override
    protected Long getField(UserProphetInfo data) {
        return data.getId();
    }

    @Override
    protected long getExpiredMillis(UserProphetInfo data) {
        return DateUtil.SECOND_ONE_DAY * 30 * 1000L;
    }
}
