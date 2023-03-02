package com.bbw.god.gameuser.res;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 资源添加信息
 *
 * @author suhq
 * @date 2019-09-20 16:18:55
 */
@Data
@AllArgsConstructor
public class ResAddInfo {
    private ResWayType wayType;
    private long value;
}
