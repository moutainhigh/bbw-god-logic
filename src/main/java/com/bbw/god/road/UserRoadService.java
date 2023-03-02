package com.bbw.god.road;

import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author suhq
 * @description: 格子服务
 * @date 2019-12-10 15:30
 **/
@Service
public class UserRoadService {
    @Autowired
    private GameUserService gameUserService;

    public UserCrossingRecord getUserCrossingRecord(long uid,int roadId){
        return this.gameUserService.getCfgItem(uid,roadId,UserCrossingRecord.class);
    }

    /**
     * 路口记录
     * @param uid
     * @param road
     * @param dir
     */
    public void recordCrossing(long uid,CfgRoadEntity road,int dir){
        if (!road.isCross()){
            return;
        }
        UserCrossingRecord ucr = this.gameUserService.getCfgItem(uid,road.getId(),UserCrossingRecord.class);
        if (ucr == null){
            ucr = UserCrossingRecord.instance(uid,road.getId(),dir);
            this.gameUserService.addItem(uid,ucr);
        }else{
            ucr.setDir(dir);
            this.gameUserService.updateItem(ucr);
        }
    }
}
