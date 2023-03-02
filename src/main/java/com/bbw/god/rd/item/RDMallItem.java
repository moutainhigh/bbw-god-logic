package com.bbw.god.rd.item;

import com.bbw.god.game.award.RDAward;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商城|兑换项通用父类
 *
 * @author suhq
 * @date 2020-11-19 11:34
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDMallItem extends RDItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String[] conditionFormats;//条件格式化值
    private List<RDNeed> needs = null;
    private List<RDAward> awards = null;
    //    private Integer remainTimes = null;//剩余次数
    private Long remainTime = null;//剩余时间
    private Integer limit = null;//购买次数限制
    private Integer boughtTimes = null;//已购次数
    private Boolean isAbleBuy = null;//是否可购
    private Integer optionalAwardStatus = null;//0未选,1已选
    private List<RDAward> optionalAwards;//可选奖励列表，有是才返回
//    private Integer waitDays = null;// 等待天数，xx天后才可以购买
}
