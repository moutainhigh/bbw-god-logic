package com.bbw.god.server.maou.alonemaou;

import com.bbw.god.game.config.CfgAloneMaou;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouAttackSummary;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouLevelInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author suhq
 * @description: 独战魔王临时变量
 * @date 2020-01-03 10:52
 **/
@Data
@AllArgsConstructor
public class AloneMaouParam {
    private UserAloneMaouData userMaouData;
    private ServerAloneMaou maou;
    private AloneMaouLevelInfo levelInfo;
    private AloneMaouAttackSummary myAttack;
    private CfgAloneMaou config;

    AloneMaouParam() {
    }
}
