package com.bbw.god.activity.worldcup;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.worldcup.entity.UserDroiyan8Info;
import com.bbw.god.cache.tmp.AbstractTmpDataRedisService;
import com.bbw.god.cache.tmp.TmpDataType;
import org.springframework.stereotype.Service;

/**
 * 玩家决战8强 redis 临时数据
 * @author: hzf
 * @create: 2022-11-12 02:25
 **/
@Service
public class UserDroiyan8InfoService extends AbstractTmpDataRedisService<UserDroiyan8Info,Long> {

    @Override
    protected Class<UserDroiyan8Info> getDataClazz() {
        return UserDroiyan8Info.class;
    }

    @Override
    protected Long getDataBelong(UserDroiyan8Info data) {
        return data.getGameUserId();
    }

    @Override
    protected String getDataLoop(UserDroiyan8Info data) {
        return "";
    }

    @Override
    protected String getRedisKey(Long belong, TmpDataType dataType, String... loop) {
        return "game:worldCup:usr:"+belong+":"+dataType.getRedisKey();
    }

    @Override
    protected Long getField(UserDroiyan8Info data) {
        return data.getId();
    }

    @Override
    protected long getExpiredMillis(UserDroiyan8Info data) {
        return DateUtil.SECOND_ONE_DAY * 30 * 1000L;
    }
}
