package com.bbw.god.activity.worldcup;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.worldcup.entity.UserQuizKingInfo;
import com.bbw.god.cache.tmp.AbstractTmpDataRedisService;
import com.bbw.god.cache.tmp.TmpDataType;
import org.springframework.stereotype.Service;

/**
 * 我是竞猜王 redis 临时数据
 * @author: hzf
 * @create: 2022-11-21 16:09
 **/
@Service
public class UserQuizKingInfoService extends AbstractTmpDataRedisService<UserQuizKingInfo,Long> {
    @Override
    protected Class<UserQuizKingInfo> getDataClazz() {
        return UserQuizKingInfo.class;
    }

    @Override
    protected Long getDataBelong(UserQuizKingInfo data) {
        return data.getGameUserId();
    }

    @Override
    protected String getDataLoop(UserQuizKingInfo data) {
        return "";
    }

    @Override
    protected String getRedisKey(Long belong, TmpDataType dataType, String... loop) {
        return "game:worldCup:usr:"+belong+":"+dataType.getRedisKey();
    }

    @Override
    protected Long getField(UserQuizKingInfo data) {
        return data.getId();
    }

    @Override
    protected long getExpiredMillis(UserQuizKingInfo data) {
        return DateUtil.SECOND_ONE_DAY * 30 * 1000L;
    }
}
