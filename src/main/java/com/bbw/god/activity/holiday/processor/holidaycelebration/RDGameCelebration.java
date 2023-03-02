package com.bbw.god.activity.holiday.processor.holidaycelebration;

import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.game.award.Award;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 全服庆典
 *
 * @author: huanghb
 * @date: 2021/12/21 0:41
 */
@Data
public class RDGameCelebration extends RDActivityList implements Serializable {
    private static final long serialVersionUID = 1L;
    /*全服庆典积分个人精度*/
    private Integer personalProgress;
    private List<RDActivity> RDActivitys;

    @Data
    public static class RDActivity implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id;
        private String title;
        private Integer status;
        private Award award;

    }
}
