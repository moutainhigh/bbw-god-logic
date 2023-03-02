package com.bbw.god.gameuser.businessgang.rd;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 返回客户端商帮信息
 *
 * @author fzj
 * @date 2022/1/14 14:49
 */
@Data
public class RDEnterBusinessGang extends RDSuccess {
    /** 可加入的商帮 初始只开启正财 */
    private List<Integer> openedBusinessGangs;
    /** 不可加入的 */
    private List<Integer> unopenedBusinessGangs;
    /** 当前加入商帮 */
    private Integer currentBusinessGang;
    /** 商帮信息 */
    private RDBusinessGangInfo businessGangInfo;
}
