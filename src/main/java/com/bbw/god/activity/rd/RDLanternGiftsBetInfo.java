package com.bbw.god.activity.rd;

import com.bbw.god.rd.RDCommon;
import lombok.Data;

/**
 * 锦礼投注信息
 *
 * @author fzj
 * @date 2022/2/12 16:05
 */
@Data
public class RDLanternGiftsBetInfo extends RDCommon {
    private static final long serialVersionUID = -5004342992968923705L;
    /** 奖券号码 */
    private String ticketNum;
}
