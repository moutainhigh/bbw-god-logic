package com.bbw.god.game.transmigration.cfg;

import com.bbw.god.game.award.Award;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 轮回奖励
 *
 * @author: suhq
 * @date: 2021/10/18 11:56 上午
 */
@Data
public class CfgTransmigrationTarget implements Serializable {
    private static final long serialVersionUID = 3095926888734005517L;
    private Integer id;
    private Integer needScore;
    private List<Award> awards;
}
