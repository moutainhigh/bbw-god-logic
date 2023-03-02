package com.bbw.god.gameuser.treasure;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * <pre>
 * 道具效果记录
 * 漫步鞋剩余步数 -1路口直接选择方向；0不可选择方向；>0剩余步数
 * </pre>
 *
 * @author suhq 2018年9月30日 上午10:43:39
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserTreasureEffect extends UserCfgObj implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer remainEffect;// 剩余步数、次数、时间
    private Date effectTime;// 生效时间/最近生效时间

    public static UserTreasureEffect instance(long guId, int treasureId, int addEffect) {
        UserTreasureEffect utEffect = new UserTreasureEffect();
        utEffect.setId(ID.INSTANCE.nextId());
        utEffect.setBaseId(treasureId);
        utEffect.setGameUserId(guId);
        utEffect.setRemainEffect(addEffect);
        utEffect.setEffectTime(DateUtil.now());
        return utEffect;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.TREASURE_EFFECT;
    }

    public void addEffect(int num) {
        this.remainEffect += num;
        this.effectTime = DateUtil.now();
    }

    public void deductEffect(int num) {
        this.remainEffect -= num;
        if (this.remainEffect < 0) {
            this.remainEffect = 0;
        }
    }

    public void setEffect(int remainTime) {
        this.remainEffect = remainTime;
        this.effectTime = DateUtil.now();
    }

}
