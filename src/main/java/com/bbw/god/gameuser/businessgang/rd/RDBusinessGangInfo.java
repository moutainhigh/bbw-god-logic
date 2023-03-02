package com.bbw.god.gameuser.businessgang.rd;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.List;

/**
 * 商帮信息
 *
 * @author fzj
 * @date 2022/1/14 15:27
 */
@Data
public class RDBusinessGangInfo extends RDSuccess{
    /** 商帮id */
    private Integer id;
    /** 商帮的声望 */
    private Integer prestige;
    /** 当前商帮NPC信息 */
    private List<RDBusinessGangNpcInfo> npcInfos;
}
