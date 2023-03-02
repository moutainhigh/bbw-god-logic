package com.bbw.god.gm.admin;

import com.bbw.god.game.config.CfgEntityInterface;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.List;

/**
 * @author：lzc
 * @date: 2021/03/17 16:30
 */
@Data
public class RDConfig extends RDSuccess {
    private List<? extends CfgEntityInterface> cfgEntities;
}
