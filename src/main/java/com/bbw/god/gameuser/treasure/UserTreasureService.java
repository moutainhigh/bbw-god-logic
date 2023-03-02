package com.bbw.god.gameuser.treasure;

import com.bbw.cache.UserCacheService;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.gameuser.yuxg.UserFuTu;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserTreasureService {
    @Autowired
    private UserCacheService userCacheService;

    /**
     * 获得单个道具
     *
     * @param uid
     * @param treasureId
     * @return
     */
    public UserTreasure getUserTreasure(long uid, int treasureId) {
        UserTreasure userTreasure = userCacheService.getCfgItem(uid, treasureId, UserTreasure.class);
        return userTreasure;
    }

    /**
     * 获取单个道具，如果没有该道具则抛出异常
     *
     * @param uid
     * @param treasureId
     * @return
     */
    public UserTreasure getUserTreasureWithCheck(long uid, int treasureId, int needNum) {
        UserTreasure userTreasure = getUserTreasure(uid, treasureId);
        if (userTreasure == null || userTreasure.gainTotalNum() <= needNum) {
            throw new ExceptionForClientTip("treasure.not.enough", userTreasure.getName());
        }
        return userTreasure;
    }

    /**
     * 获取玩家所有法宝  即拥有数量大于0的
     *
     * @param uid
     * @return
     */
    public List<UserTreasure> getAllUserTreasures(long uid) {
        List<UserTreasure> userTreasures = userCacheService.getUserDatas(uid, UserTreasure.class);
        return userTreasures.stream().filter(p -> p.gainTotalNum() > 0).collect(Collectors.toList());
    }

    /**
     * 获得多个道具
     *
     * @param uid
     * @param treasureIds
     * @return
     */
    public List<UserTreasure> getUserTreasures(long uid, List<Integer> treasureIds) {
        List<UserTreasure> uts = userCacheService.getCfgItems(uid, treasureIds, UserTreasure.class);
        return uts;
    }

    /**
     * 获取玩家的战斗法宝
     *
     * @param uid
     * @return
     */
    public List<UserTreasure> getFightTreasures(long uid) {
        List<UserTreasure> treasures = getAllUserTreasures(uid);
        List<Integer> fightTreasureIds = TreasureTool.getFightTreasureIds();
        return treasures.stream().filter(p -> fightTreasureIds.contains(p.getBaseId())).collect(Collectors.toList());
    }

    /**
     * 获得道具数量
     *
     * @param uid
     * @param treasureId
     * @return
     */
    public int getTreasureNum(long uid, int treasureId) {
        int num = 0;
        UserTreasure ut = getUserTreasure(uid, treasureId);
        if (ut != null) {
            num = ut.gainTotalNum();
        }
        return num;
    }

    /**
     * 扣除法宝，法宝足够并扣除返回true,反之返回false
     *
     * @param uid
     * @param treasureId
     * @param num
     * @return
     */
    public boolean delTreasure(long uid, int treasureId, int num, WayEnum way) {
        UserTreasure uTreasure = getUserTreasure(uid, treasureId);
        if (uTreasure != null && uTreasure.gainTotalNum() >= num) {
            TreasureEventPublisher.pubTDeductEvent(uid, treasureId, num, way, new RDCommon());
            return true;
        }
        return false;
    }

    /**
     * 添加法宝数据
     * @param ut
     */
    public void doAddTreasure(UserTreasure ut){
        userCacheService.addUserData(ut);
    }

    /**
     * 删除法宝数据
     * @param ut
     */
    public void doDelTreasure(UserTreasure ut){
        userCacheService.delUserData(ut);
    }

}
