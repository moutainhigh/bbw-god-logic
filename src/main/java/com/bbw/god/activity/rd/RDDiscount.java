package com.bbw.god.activity.rd;

import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.io.Serializable;

/**
 * 节日折扣商店限时折扣
 *
 * @author fzj
 * @date 2021/11/18 14:38
 */
@Data
public class RDDiscount extends RDCommon implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 折扣 */
    private Double discount;
}
