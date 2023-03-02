package com.bbw.god.activity.rd;

import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.io.Serializable;

/**
 * 锦鲤祈愿信息
 *
 * @author: huanghb
 * @date: 2022/9/29 16:07
 */
@Data
public class RDKoiPrayInfo extends RDCommon implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 祈愿倍数 */
    private Double multiplea;
}
