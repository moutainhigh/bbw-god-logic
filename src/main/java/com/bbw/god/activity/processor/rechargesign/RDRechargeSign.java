package com.bbw.god.activity.processor.rechargesign;

import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.game.award.RDAward;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 活动
 *
 * @author suhq
 * @date 2019年3月3日 下午11:24:29
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDRechargeSign extends RDActivityList implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer rechargeId = 0;
    private List<RDAward> awardeds;

}
