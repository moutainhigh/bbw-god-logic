package com.bbw.god.gameuser.kunls.rd;

import com.bbw.god.rd.RDCommon;
import lombok.Data;

/**
 * 昆仑山炼制室炼制结果
 *
 * @author: huanghb
 * @date: 2022/9/28 14:58
 */
@Data
public class RdMakingResult extends RDCommon {
    private static final long serialVersionUID = -1L;
    /** 是否炼制成功 */
    private Integer isMakingSuccess = 0;

    /**
     * 炼制失败
     */
    public void makingFailure() {
        isMakingSuccess = 1;
    }
}
