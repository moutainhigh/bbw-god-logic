package com.bbw.god.city.yed;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author suchaobin
 * @description 玩家奇遇
 * @date 2020/6/2 10:15
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserAdventure extends UserCfgObj implements Serializable {
    private static final long serialVersionUID = -2792811744028461989L;
    private Integer mallId;
    private Integer type;
    private Integer ableMaxExp;
    private Date generateTime;

    public static UserAdventure instanceShouYe(long uid, Integer type, Integer baseId, int ableMaxExp) {
        UserAdventure userAdventure = new UserAdventure();
        userAdventure.setId(ID.INSTANCE.nextId());
        userAdventure.setGameUserId(uid);
        userAdventure.setBaseId(baseId);
        userAdventure.setType(type);
        userAdventure.setAbleMaxExp(ableMaxExp);
        userAdventure.setGenerateTime(DateUtil.now());
        return userAdventure;
    }

    public static UserAdventure instanceBusiness(long uid, Integer type, Integer mallId, Integer baseId) {
        UserAdventure userAdventure = new UserAdventure();
        userAdventure.setId(ID.INSTANCE.nextId());
        userAdventure.setGameUserId(uid);
        userAdventure.setMallId(mallId);
        userAdventure.setBaseId(baseId);
        userAdventure.setType(type);
        userAdventure.setGenerateTime(DateUtil.now());
        return userAdventure;
    }

    public boolean isValid(int type) {
        if (YdEventEnum.YYSR.getValue() == type) {
            Date generateTime = this.getGenerateTime();
            Date date = DateUtil.addMinutes(generateTime, 60);
            return date.getTime() - System.currentTimeMillis() > 0;
        }
        //仙人授业超过30天不会显示
        if (AdventureType.XRSY.getValue() == type) {
            Date generateTime = this.getGenerateTime();
            Date date = DateUtil.addDays(generateTime, 30);
            return date.getTime() - System.currentTimeMillis() > 0;
        }
        return true;
    }

    /**
     * 玩家资源类型
     *
     * @return
     */
    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_ADVENTURE;
    }
}
