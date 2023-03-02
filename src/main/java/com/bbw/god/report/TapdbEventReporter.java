package com.bbw.god.report;

import com.bbw.common.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * tapdb https://www.tapdb.com/docs/sdk/%E6%9C%8D%E5%8A%A1%E7%AB%AF%E6%8E%A5%E5%85%A5%E6%96%87%E6%A1%A3
 *
 * @author: suhq
 * @date: 2021/8/18 10:50 上午
 */
@Slf4j
@Service
public class TapdbEventReporter {
    private static final String REPORT_URI = "https://e.tapdb.net/event";

    /**
     * 上报
     */
    public void report(TapdbEventData data) {
        String json = data.toJson();
        HttpClientUtil.doPostJson(REPORT_URI, json);
        log.info("{}报送事件{}到tapdb,数据：{}", data.getProperties().getReporter(), data.getProperties().getEvent(), json);
    }

}
