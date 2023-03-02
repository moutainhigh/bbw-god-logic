package com.bbw.god.gameuser.chamberofcommerce.server;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.CfgCoc.Head;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.chamberofcommerce.CocConstant;
import com.bbw.god.gameuser.chamberofcommerce.CocPrivilegeEnum;
import com.bbw.god.gameuser.chamberofcommerce.CocTools;
import com.bbw.god.gameuser.chamberofcommerce.UserCocInfo;
import com.bbw.god.gameuser.chamberofcommerce.event.CocEventPublisher;
import com.bbw.god.gameuser.chamberofcommerce.event.EPCocUpLv;
import com.bbw.god.gameuser.redis.UserRedisKey;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 头衔相关 如商城和特权
 *
 * @author lwb
 * @version 1.0
 * @date 2019年4月16日
 */
@Service
public class UserCocInfoService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCocStoreService userCocStoreService;
    @Autowired
    private UserTreasureService userTreasureService;

    /**
     * 获取玩家加急任务概率 翻倍倍数
     *
     * @param uid
     * @return
     */
    public int getPrivilegeUrgentDouble(long uid) {
        return getAddByPrivilege(CocPrivilegeEnum.Urgent_Task, uid);
    }


    /**
     * 获取玩家的钱庄增益 百分比 如10% 返回为10 无增益为0
     *
     * @param uid
     * @return
     */
    public int getPrivilegeBankGain(long uid) {
        return getAddByPrivilege(CocPrivilegeEnum.QZ_Profit_Add, uid);
    }

    private int getAddByPrivilege(CocPrivilegeEnum type, long uid) {
        Optional<UserCocInfo> info = getUserCocInfoOp(uid);
        if (!info.isPresent()) {
            return 0;
        }
        return CocTools.getAddByPrivilege(type, info.get().getHonorLevel());
    }

    /**
     * 获得玩家进入城池 购买特产获5%折扣，卖出特产溢价5% 的获得概率 已随机 获得优惠则返回true 无则false
     *
     * @param uid
     * @return
     */
    public boolean getCityTradeProfit(long uid) {
        int probability = getAddByPrivilege(CocPrivilegeEnum.City_Profit, uid);
        if (probability == 0) {
            return false;
        }
        return PowerRandom.hitProbability(probability);
    }

    /**
     * 获取商会对象当等级不够时将抛出提示
     *
     * @param uid
     * @return
     */
    public UserCocInfo getUserCocInfo(long uid) {
        if (!opened(uid)) {
            throw new ExceptionForClientTip("coc.not.creat");
        }
        UserCocInfo info = getCocInfo(uid);
        return info;
    }

    /**
     * 获取商会对象不抛出 等级提示
     *
     * @param uid
     * @return
     */
    public Optional<UserCocInfo> getUserCocInfoOp(long uid) {
        UserCocInfo info = getCocInfo(uid);
        if (info == null) {
            return Optional.empty();
        }
        return Optional.of(info);
    }

    private UserCocInfo getCocInfo(long uid) {
        if (!opened(uid)) {
            return null;
        }
        UserCocInfo info = gameUserService.getSingleItem(uid, UserCocInfo.class);
        if (info == null) {
            info = new UserCocInfo();
            info.setId(UserRedisKey.getNewUserDataId());
            info.setGameUserId(uid);
            gameUserService.addItem(uid, info);
            return info;
        }
        int nowDate = DateUtil.getTodayInt();
        if (info.getTaskBuildDate() != nowDate) {
            userCocStoreService.initCocShopLimt(info);// 初始化每日限购
            info.setTaskBuildDate(nowDate);
        }
        return info;
    }

    /**
     * 升级商会版本=》独立出积分 金币
     *
     * @param uid
     */
    public void updateCoc(long uid) {
        UserCocInfo info = gameUserService.getSingleItem(uid, UserCocInfo.class);
        if (info == null) {
            return;
        }
        // 分离积分
        int honor = info.getHonor();
        if (honor > 0) {
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.SHJF.getValue(), honor, WayEnum.UPDATE,
                    new RDCommon());
            info.setHonor(0);
        }
        // 分离金币
        int coin = info.getGoldCoin();
        if (coin > 0) {
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.SHJB.getValue(), coin, WayEnum.UPDATE,
                    new RDCommon());
            info.setGoldCoin(0);
        }
        gameUserService.updateItem(info);
    }

    public boolean opened(long uid) {
        GameUser gu = gameUserService.getGameUser(uid);
        return gu.getLevel() >= CocConstant.OPEN_LEVEL;
    }

    public int getGoldCoin(long uid) {
        return userTreasureService.getTreasureNum(uid, TreasureEnum.SHJB.getValue());
    }

    public int getHonor(long uid) {
        return userTreasureService.getTreasureNum(uid, TreasureEnum.SHJF.getValue());
    }

    public int getTokenQuantity(long uid) {
        return userTreasureService.getTreasureNum(uid, TreasureEnum.SHLP.getValue());
    }

    public UserCocInfo updateCocLv(long uid) {
        Optional<UserCocInfo> infoOp = getUserCocInfoOp(uid);
        if (!infoOp.isPresent()) {
            return null;
        }
        int honor = getHonor(uid);
        int honorLevel = CocTools.getLvByHonor(honor);
        UserCocInfo info = infoOp.get();
        if (info.getHonorLevel() != null && info.getHonorLevel() != honorLevel) {
            info.setUnclaimed(info.getUnclaimed() + 1);
        }
        info.setHonorLevel(honorLevel);
        gameUserService.updateItem(info);
        // 发送头像
        Optional<Head> headOp = CocTools.getHeadByLv(honorLevel);
        if (headOp.isPresent()) {
            int headId = headOp.get().getBaseId();
            if (!TreasureChecker.hasTreasure(uid, headId)) {
                TreasureEventPublisher.pubTAddEvent(uid, headId, 1, WayEnum.Chamber_Of_Commerce_LV_ADD, new RDCommon());
            }
        }
        EPCocUpLv ep = EPCocUpLv.instance(new BaseEventParam(uid), honorLevel);
        CocEventPublisher.pubUplevelAddEvent(ep);
        return info;
    }
}
