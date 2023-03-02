package com.bbw.god.game.dfdj;

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
 * @author suchaobin
 * @description 丹药使用信息
 * @date 2021/1/5 15:07
 **/
@Service
public class DfdjMedicineService {
    private final Long SECONDS_CACHE_LIMIT = 15 * 60L;
    @Autowired
    private UserTreasureService userTreasureService;

    public RDSuccess enableMedicine(long uid, int roomId, Integer medicineId, boolean enable) {
        List<Integer> medicinesToUse = getMedicineToUse(uid, roomId);
        if (!enable) {//取消
            //可取消
            if (medicinesToUse.contains(medicineId)) {
                medicinesToUse.remove(medicineId);
                TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, getSuffixKey(roomId), medicinesToUse, SECONDS_CACHE_LIMIT);
            }
            return new RDSuccess();
        }
        //使用
        //已使用
        if (medicinesToUse.contains(medicineId)) {
            return new RDSuccess();
        }
        //检查数量
        TreasureChecker.checkIsEnough(medicineId, 1, uid);
        medicinesToUse.add(medicineId);
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, getSuffixKey(roomId), medicinesToUse, SECONDS_CACHE_LIMIT);

        return new RDSuccess();
    }

    public void clearMedicineUseInfo(long uid, int roomId) {
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, getSuffixKey(roomId), null);
    }

    public List<Integer> getMedicineToUse(long uid, int roomId) {
        if (uid<0) {
            return new ArrayList<Integer>();
        }
        List<Integer> medicineIdUses = TimeLimitCacheUtil.getFromCache(uid, getSuffixKey(roomId), List.class);
        if (ListUtil.isNotEmpty(medicineIdUses)) {
            return medicineIdUses;
        }
        List<Integer> medicinesToUse = new ArrayList<>();
        List<UserTreasure> uts = userTreasureService.getAllUserTreasures(uid);
        boolean isOwnBuSD = uts.stream().anyMatch(tmp -> tmp.getBaseId().intValue() == TreasureEnum.BuSD.getValue());
        boolean isOwnYuanQD = uts.stream().anyMatch(tmp -> tmp.getBaseId().intValue() == TreasureEnum.YuanQD.getValue());
        if (isOwnBuSD) {
            medicinesToUse.add(TreasureEnum.BuSD.getValue());
        }
        if (isOwnYuanQD) {
            medicinesToUse.add(TreasureEnum.YuanQD.getValue());
        }
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, getSuffixKey(roomId), medicinesToUse, SECONDS_CACHE_LIMIT);
        return medicinesToUse;
    }

    private String getSuffixKey(int roomId) {
        return "dfdjMedicineUses" + roomId;
    }

}
