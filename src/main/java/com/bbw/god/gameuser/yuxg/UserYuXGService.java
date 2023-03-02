package com.bbw.god.gameuser.yuxg;

import com.bbw.cache.UserCacheService;
import com.bbw.common.ListUtil;
import com.bbw.common.lock.RedisLockUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 玉虚宫玩家数据服务类
 *
 * @author fzj
 * @date 2021/11/2 9:06
 */
@Service
public class UserYuXGService {
    @Autowired
    GameUserService gameUserService;
    @Autowired
    UserCacheService userCacheService;
    @Autowired
    private RedisLockUtil redisLockUtil;

    /**
     * 获取玩家玉虚宫数据
     *
     * @param uid
     * @return
     */
    public UserYuXG getUserYuXG(long uid) {
        return gameUserService.getSingleItem(uid, UserYuXG.class);
    }

    /**
     * 获取玩家所有符图信息
     *
     * @param uid
     * @return
     */
    public List<UserFuTu> getUserAllFuTus(long uid) {
        return userCacheService.getUserDatas(uid, UserFuTu.class);
    }

    /**
     * 添加符图
     * @param userFuTu
     */
    public void addFuTu(UserFuTu userFuTu){
        userCacheService.addUserData(userFuTu);
    }

    /**
     * 删除符图数据
     * @param userFuTu
     */
    public void doDelFuTu(UserFuTu userFuTu){
        userCacheService.delUserData(userFuTu);
    }

    /**
     * 获取玩家的指定某个符图信息
     *
     * @param uid
     * @param dataId
     * @return
     */
    public UserFuTu getUserFuTu(long uid, long dataId) {
        UserFuTu userFuTu = gameUserService.getUserData(uid, dataId, UserFuTu.class).orElse(null);
        if (null == userFuTu) {
            throw new ExceptionForClientTip("yuXG.fuTu.check");
        }
        return userFuTu;
    }

    /**
     * 获取缓存的符图数据
     *
     * @param uid
     * @param fuTuId
     * @return
     */
    public UserFuTu getUserFuTuCache(long uid, int fuTuId) {
        UserFuTu userFuTu = userCacheService.getCfgItem(uid, fuTuId, UserFuTu.class);
        return userFuTu;
    }

    /**
     * 获取符图升级设置
     *
     * @param uid
     * @return
     */
    public UserFuTuUpSetting getUserFuTuUpSetting(long uid) {
        return gameUserService.getSingleItem(uid, UserFuTuUpSetting.class);
    }

    /**
     * 获取玩家符册数据
     *
     * @param uid
     * @return
     */
    public List<UserFuCe> getUserFuCes(long uid) {
        return gameUserService.getMultiItems(uid, UserFuCe.class);
    }

    /**
     * 获取符册数据
     *
     * @param uid
     * @return
     */
    public UserFuCe getFuCe(long uid, int fuCeId) {
        UserFuCe userFuCe = getUserFuCes(uid).stream().filter(f -> f.getBaseId() == fuCeId).findFirst().orElse(null);
        if (null == userFuCe) {
            throw new ExceptionForClientTip("yuXG.fuCe.check");
        }
        return userFuCe;
    }

    /**
     * 获取当前玩家祈福设置
     *
     * @param uid 玩家id
     * @return
     */
    public UserYuXGPraySetting getCurUserSpecialSetting(long uid) {
        UserYuXGPraySetting setting = gameUserService.getSingleItem(uid, UserYuXGPraySetting.class);
        if (null == setting) {
            setting = (UserYuXGPraySetting) redisLockUtil.doSafe(String.valueOf(uid), tmp -> {
                UserYuXGPraySetting yuXGSetting = gameUserService.getSingleItem(uid, UserYuXGPraySetting.class);
                if (null == yuXGSetting) {
                    yuXGSetting = UserYuXGPraySetting.getInstance(uid);
                    gameUserService.addItem(uid, yuXGSetting);
                }
                return yuXGSetting;
            });
        }
        return setting;
    }

    /**
     * 等级解锁符册数量
     * @param uid
     * @param allFaTanLv
     */
    public void levelUnlockFuCeNum(long uid, int allFaTanLv) {
        List<Integer> faTanLvs = Arrays.asList(50,150,250);
        List<Integer> newFaTanLvs = new ArrayList<>();
        for (Integer faTanLv : faTanLvs) {
            if (allFaTanLv >= faTanLv) {
                newFaTanLvs.add(faTanLv);
            }
        }
        if (ListUtil.isEmpty(newFaTanLvs)) {
            return;
        }
        List<UserFuCe> userFuCes = getUserFuCes(uid);
        //等级解锁符册数量
        long fuCeNum = userFuCes.stream().filter(f -> f.getOpenMethod() == 1).count();
        //可添加等级解锁的符册数量
        int addNum = newFaTanLvs.size() - (int) fuCeNum;
        //等级解锁符册达到三个，可添加数量为0
        if (addNum <= 0 || fuCeNum >= 3) {
            return;
        }
        for (int i = 0; i < addNum; i++) {
            //开启新的符册
            int hasFuCeNum = userFuCes.size() + 1;
            //获取符图槽数量
            Integer fuTuSlotNum = YuXGTool.getFuTuSlotNum(allFaTanLv);
            UserFuCe userFuCe = UserFuCe.getInstance(uid, "符册" + hasFuCeNum, 1, hasFuCeNum, fuTuSlotNum);
            gameUserService.addItem(uid, userFuCe);
        }
    }
}
