package com.bbw.god.rd.item;

import com.bbw.god.game.award.RDAward;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 元宝充值
 *
 * @author suhq
 * @date 2020-11-19 11:34
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDRechargeGoldItem extends RDItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<RDNeed> needs = null;//
    private List<RDAward> awards = null;
    private Integer rechargeId = null;// 直冲ID
    private Integer hasFirstBoughtAward;
    private int isBought;
    private int extraNum;
}
