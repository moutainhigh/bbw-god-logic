package com.bbw.god.gameuser.task.timelimit.cunz;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 村庄任务枚举类
 *
 * @author fzj
 * @date 2021/8/17 16:06
 */
@Getter
@AllArgsConstructor
public enum CunZTaskEnum {

    CUNZ_SHENHAOTASK("神豪任务", 1000),
    CUNZ_LAOZHETASK("老者任务", 2000),
    CUNZ_ERMAOTASK("二毛任务", 3000),
    CUNZ_XIAOZHANGTASK("小张任务", 4000),
    CUNZ_CUNZHANGTASK("村长任务", 5000),
    CUNZ_XIAOBUTASK("小布任务", 6000),
    CUNZ_XIAOBATASK("小巴任务", 7000),
    ;
    private final String name;
    private final Integer seqGroup;

    public static CunZTaskEnum fromValue(int value) {
        for (CunZTaskEnum item : values()) {
            if (item.getSeqGroup() == value) {
                return item;
            }
        }
        return null;
    }

}
