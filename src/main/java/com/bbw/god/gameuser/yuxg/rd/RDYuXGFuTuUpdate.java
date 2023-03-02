package com.bbw.god.gameuser.yuxg.rd;

import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.io.Serializable;

/**
 * 符图升级结果
 *
 * @author: suhq
 * @date: 2021/10/21 11:32 上午
 */
@Data
public class RDYuXGFuTuUpdate extends RDCommon implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer level = null;
    private Long exp = null;
}
