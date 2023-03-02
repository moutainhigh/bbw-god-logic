package com.bbw.god.game.sxdh;

import com.bbw.common.ListUtil;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 丹药使用信息
 *
 * @author suhq
 * @date 2020-05-19 07:11
 **/
@Service
public class SxdhMechineService {
    private final Long SECONDS_CACHE_LIMIT = 15 * 60L;
    @Autowired
    private UserTreasureService userTreasureService;

    public RDSuccess enableMechine(long uid, int roomId, Integer mechineId, boolean enable) {
        List<Integer> mechinesToUse = getMechinesToUse(uid, roomId);
        if (!enable) {//取消
            //可取消
            if (mechinesToUse.contains(mechineId)) {
                mechinesToUse.remove(mechineId);
                TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, getSuffixKey(roomId), mechinesToUse, SECONDS_CACHE_LIMIT);
            }
            return new RDSuccess();
        }
        //使用
        //已使用
        if (mechinesToUse.contains(mechineId)) {
            return new RDSuccess();
        }
        //检查数量
        TreasureChecker.checkIsEnough(mechineId, 1, uid);
        mechinesToUse.add(mechineId);
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, getSuffixKey(roomId), mechinesToUse, SECONDS_CACHE_LIMIT);

        return new RDSuccess();
    }

    public void clearMechineUseInfo(long uid, int roomId) {
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, getSuffixKey(roomId), null);
    }

    public List<Integer> getMechinesToUse(long uid, int roomId) {
    	if (uid<0) {
			return new ArrayList<Integer>();
		}
        List<Integer> mechineUses = TimeLimitCacheUtil.getFromCache(uid, getSuffixKey(roomId), List.class);
        if (ListUtil.isNotEmpty(mechineUses)) {
            return mechineUses;
        }
        List<Integer> mechinesToUse = new ArrayList<>();
        List<UserTreasure> uts = userTreasureService.getAllUserTreasures(uid);
        boolean isOwnBuSD = uts.stream().anyMatch(tmp -> tmp.getBaseId().intValue() == TreasureEnum.BuSD.getValue());
        boolean isOwnYuanQD = uts.stream().anyMatch(tmp -> tmp.getBaseId().intValue() == TreasureEnum.YuanQD.getValue());
        if (isOwnBuSD) {
            mechinesToUse.add(TreasureEnum.BuSD.getValue());
        }
        if (isOwnYuanQD) {
            mechinesToUse.add(TreasureEnum.YuanQD.getValue());
        }
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, getSuffixKey(roomId), mechinesToUse, SECONDS_CACHE_LIMIT);
        return mechinesToUse;
    }

    private String getSuffixKey(int roomId) {
        return "sxdhMechineUses" + roomId;
    }

}
