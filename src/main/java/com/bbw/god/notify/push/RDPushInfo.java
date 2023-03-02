package com.bbw.god.notify.push;

import com.bbw.common.ListUtil;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description TODO
 * @date 2019/12/20 17:59
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDPushInfo extends RDCommon {
    private List<Integer> ablePushList = new ArrayList<>();

    public static RDPushInfo getInstance(List<Integer> ablePushList) {
        RDPushInfo rdPushInfo = new RDPushInfo();
        if (ListUtil.isNotEmpty(ablePushList)) {
            rdPushInfo.setAblePushList(ablePushList);
        }
        return rdPushInfo;
    }
}
