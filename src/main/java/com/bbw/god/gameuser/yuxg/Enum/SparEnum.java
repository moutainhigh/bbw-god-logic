package com.bbw.god.gameuser.yuxg.Enum;

import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 晶石相关枚举
 *
 * @author fzj
 * @date 2021/10/29 11:09
 */
@Getter
@AllArgsConstructor
public enum SparEnum {
    CAN_JING("残晶", 50127),
    TIAN_JING("天晶", 50128 ),
    XUE_JING("血晶", 50129),
    ;

    private final String name;
    private final int sparId;

    public static SparEnum fromValue(int sparId) {
        for (SparEnum item : values()) {
            if (item.getSparId() == sparId) {
                return item;
            }
        }
        throw CoderException.high("无效的晶石-" + sparId);
    }

    /**
     * 随机获取一个晶石的id
     *
     * @return
     */
    public static Integer randomGainSparId() {
        List<Integer> sparIdList = new ArrayList<>();
        for (SparEnum sparEnum : values()) {
            sparIdList.add(sparEnum.getSparId());
        }
        return PowerRandom.getRandomFromList(sparIdList);
    }
}
