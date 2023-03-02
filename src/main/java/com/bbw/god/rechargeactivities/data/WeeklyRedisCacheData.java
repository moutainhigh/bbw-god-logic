package com.bbw.god.rechargeactivities.data;

import com.bbw.god.game.award.Award;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * 炼技礼包  缓存可选项
 * @author lwb
 */
@Data
public class WeeklyRedisCacheData implements Serializable {
    private static final long serialVersionUID = 1648461481664119788L;
    private List<Award> awards;
    private List<Award> awards2;
    private long id;
    private Date build=new Date();
}
