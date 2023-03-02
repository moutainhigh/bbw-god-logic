package com.bbw.god.gameuser.special;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * 特产
 *
 * @author suhq
 * @date 2018年10月23日 上午8:55:54
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserSpecial extends UserCfgObj {

    private Integer discount;
    private Date ownTime;

    public static UserSpecial fromCfgSpecial(long guId, CfgSpecialEntity cfgSpecial, int discount) {
        UserSpecial uSpecial = new UserSpecial();
        uSpecial.setId(ID.INSTANCE.nextId());
        uSpecial.setGameUserId(guId);
        uSpecial.setBaseId(cfgSpecial.getId());
        uSpecial.setName(cfgSpecial.getName());
        uSpecial.setDiscount(discount);
        uSpecial.setOwnTime(DateUtil.now());
        return uSpecial;
    }

    public int getDiscount() {
        if (this.discount == null) {
            return 100;
        }
        return this.discount;
    }

    public CfgSpecialEntity gainSpecial(int specialId) {
        return SpecialTool.getSpecialById(specialId);
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.SPECIAL;
    }

}
