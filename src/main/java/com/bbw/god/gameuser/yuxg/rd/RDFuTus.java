package com.bbw.god.gameuser.yuxg.rd;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 符册
 *
 * @author fzj
 * @date 2021/11/12 19:53
 */
@Data
public class RDFuTus extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 998187098802155732L;
    private List<Long> fuTuId = new ArrayList<>();
}
