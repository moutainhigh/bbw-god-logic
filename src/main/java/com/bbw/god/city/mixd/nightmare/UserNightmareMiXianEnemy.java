package com.bbw.god.city.mixd.nightmare;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-05-27
 */
@Data
public class UserNightmareMiXianEnemy extends UserSingleObj {

    private List<MiXianEnemy> miXianEnemies=new ArrayList<>();

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_NIGHTMARE_MIXIAN_ENEMY;
    }

    public static UserNightmareMiXianEnemy getInstance(long uid){
        UserNightmareMiXianEnemy enemy=new UserNightmareMiXianEnemy();
        enemy.setId(ID.INSTANCE.nextId());
        enemy.setGameUserId(uid);
        return enemy;
    }

    public Optional<MiXianEnemy> getEnemy(int pos,int mxdLevel,int posType){
        long aiId = NightmareMiXianTool.buildMxdAiId(mxdLevel, pos,posType);
        return miXianEnemies.stream().filter(p->p.getEnemyId()==aiId).findFirst();
    }
}
