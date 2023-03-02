package com.bbw.god.rechargeactivities.wartoken;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-02
 */
@Data
public class CfgWarTokenTask implements CfgEntityInterface, Serializable {
    private Integer id;
    private Integer type;
    private Integer exp;
    private boolean addWeekExp=false;
    private Integer need;


    @Override
    public int getSortId() {
        return this.id;
    }
}
