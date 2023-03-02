package com.bbw.god.activity.worldcup;

import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.worldcup.entity.UserDroiyan8Info;
import com.bbw.god.activity.worldcup.entity.UserProphetInfo;
import com.bbw.god.activity.worldcup.entity.UserQuizKingInfo;
import com.bbw.god.activity.worldcup.entity.UserSuper16Info;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 世界杯service
 * @author: hzf
 * @create: 2022-11-12 03:45
 **/
@Service
public class WorldCupService {

    @Autowired
    private UserDroiyan8InfoService userDroiyan8InfoService;
    @Autowired
    private UserSuper16InfoService userSuper16InfoService;

    @Autowired
    private UserProphetInfoService userProphetInfoService;

    @Autowired
    private UserQuizKingInfoService userQuizKingInfoService;




    /**
     * 获取用户超级16强
     *
     * @param uid
     * @return
     */
    public UserSuper16Info getUserSuper16(long uid) {
        return userSuper16InfoService.getSingleData(uid);
    }

    /**
     * 获取决战8强
     *
     * @param uid
     * @return
     */
    public UserDroiyan8Info getUserDroiyan8(long uid) {
        return userDroiyan8InfoService.getSingleData(uid);
    }

    /**
     * 获取我是预言家
     *
     * @param uid
     * @return
     */
    public UserProphetInfo getUserProphet(long uid) {
        return userProphetInfoService.getSingleData(uid);
    }

    /**
     * 获取我是竞猜王
     *
     * @param uid
     * @return
     */
    public UserQuizKingInfo getUserQuizKing(long uid) {
        return userQuizKingInfoService.getSingleData(uid);
    }

    /**
     * 扣除道具
     * @param uid
     * @param needTreasureId
     * @param needNum
     */
    public RDCommon deductTreasure(long uid, Integer needTreasureId, Integer needNum){
        //检查是否有足够道具
        TreasureChecker.checkIsEnough(needTreasureId, needNum, uid);
        RDCommon rd = new  RDCommon();
        TreasureEventPublisher.pubTDeductEvent(uid, needTreasureId, needNum, WayEnum.LANTERN_BET, rd);
        return rd;
    }

}