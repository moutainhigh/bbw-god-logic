package com.bbw.god.gameuser.yaozu;

import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.fight.RDFightResult;
import org.springframework.stereotype.Service;

/**
 * 触发妖族
 *
 * @author fzj
 * @date 2021/9/10 8:46
 */
@Service
public class YaoZuProcessor {

    public ArriveYaoZuCache arriveYaoZuProcessor(long uid, UserYaoZuInfo userYaoZuInfos){
        //置为未结算状态
        TimeLimitCacheUtil.removeCache(uid, RDFightResult.class);
        ArriveYaoZuCache rd = ArriveYaoZuCache.getInstance(userYaoZuInfos);
        return rd;
    }
}
