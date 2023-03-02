package com.bbw.db.redis;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author suchaobin
 * @description redis锁基础参数
 * @date 2020/7/23 15:06
 **/
@Data
@NoArgsConstructor
public class RedisBaseLockParam {
    private Long uid;
    private Integer sid;
    private Integer group;

    public RedisBaseLockParam(Long uid, Integer group) {
        this.uid = uid;
        this.group = group;
    }
}
