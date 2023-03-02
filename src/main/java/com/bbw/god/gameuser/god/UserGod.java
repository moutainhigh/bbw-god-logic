package com.bbw.god.gameuser.god;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.server.god.ServerGod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 玩家神仙附体，最新的一条为当前或最近一次附体
 *
 * @author suhq 2018年9月30日 上午10:41:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserGod extends UserCfgObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long serverGodId;
    private Date attachTime;// 附体时间
    private Integer remainStep = 0;// 剩余步数
    private Date attachEndTime;// 附体结束时间
    private WayEnum attachWay=WayEnum.NONE;// 附体途径
    /**
     * 允许使用送神符
     */
    private boolean canUseSSF=true;

    public static UserGod instance(Long guId, ServerGod serverGod) {
        UserGod userGod = new UserGod();
        userGod.setId(ID.INSTANCE.nextId());
        userGod.setGameUserId(guId);
        userGod.setBaseId(serverGod.getGodId());
        userGod.setServerGodId(serverGod.getId());
        userGod.setName(serverGod.getName());
        userGod.setAttachTime(DateUtil.now());
        GodEnum godEnum = GodEnum.fromValue(serverGod.getGodId());
        if (godEnum.getType() == 20) {
            userGod.setAttachEndTime(DateUtil.addSeconds(DateUtil.now(), godEnum.getEffect()));
        } else {
            userGod.setRemainStep(godEnum.getEffect());
            userGod.setAttachEndTime(DateUtil.addYears(DateUtil.now(), 1));// 1年后失效
        }
        return userGod;
    }

    public boolean ifEffect() {
        GodEnum godEnum = GodEnum.fromValue(getBaseId());
        if (godEnum.getType() == 10 || godEnum.getType() == 30) {
            return getRemainStep() > 0;
        }
        if (godEnum.getType() == 20) {
            return getAttachEndTime().after(DateUtil.now());
        }
        return false;
    }

    /**
     * 处理神仙步数
     *
     * @param deductNum
     */
    public void deductRemainStep(int deductNum) {
        this.remainStep -= deductNum;
        this.remainStep = Math.max(0, this.remainStep);
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.GOD;
    }
}
