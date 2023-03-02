package com.bbw.god.gameuser.nightmarenvwam.godheadwarehouse;

import com.bbw.god.game.award.RDAward;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.List;

/**
 * 封神祭坛返回客户端
 *
 * @author fzj
 * @date 2022/5/10 14:04
 */
@Data
public class RDGodHeadWarehouse extends RDSuccess {
    /** 道具 */
    private List<RDAward> awards;
}
