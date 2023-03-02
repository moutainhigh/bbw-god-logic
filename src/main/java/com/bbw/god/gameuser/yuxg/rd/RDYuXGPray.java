package com.bbw.god.gameuser.yuxg.rd;

import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.util.List;

/**
 * 玉虚宫祈符
 *
 * @author: suhq
 * @date: 2021/10/19 3:48 下午
 */
@Data
public class RDYuXGPray extends RDCommon {
    private static final long serialVersionUID = -1934147186061882110L;
    /** 下次进行祈符的符坛 */
    private int nextFuTan;
    /** 许愿清单 */
    List<RdWishingDetailed.RdDetailed> wishingDetailed;
}
