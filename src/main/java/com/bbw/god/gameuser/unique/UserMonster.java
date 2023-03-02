package com.bbw.god.gameuser.unique;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgMonster;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 帮好友打怪相关信息
 *
 * @author suhq 2018年9月30日 上午10:50:11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserMonster extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    // private Integer findTimes;
    private Integer helpWinTimes;
    private Date nextBeatTime;

    public static UserMonster instance(long guId) {
        UserMonster userMonsterHelp = new UserMonster();
        userMonsterHelp.setId(ID.INSTANCE.nextId());
        userMonsterHelp.setGameUserId(guId);
        int monsterColdTime = Cfg.I.getUniqueConfig(CfgMonster.class).getMonsterColdTime();
        userMonsterHelp.setNextBeatTime(DateUtil.addSeconds(DateUtil.now(), monsterColdTime));
        return userMonsterHelp;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.MONSTER_HELP;
    }
}
