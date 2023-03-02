package com.bbw.god.rd.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 直冲商品
 *
 * @author suhq
 * @date 2020-11-19 11:34
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDRechargePackageItem extends RDMallItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer rechargeId = null;// 直冲ID
}
