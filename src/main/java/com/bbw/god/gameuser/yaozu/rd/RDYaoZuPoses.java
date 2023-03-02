package com.bbw.god.gameuser.yaozu.rd;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author fzj
 * @date 2021/9/17 11:56
 */
@Slf4j
@Data
public class RDYaoZuPoses extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    List<RDYaoZuPos> yaoZuPos = null;
}
