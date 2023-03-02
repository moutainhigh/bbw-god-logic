package com.bbw.god.server.god;

import com.bbw.common.DateUtil;
import com.bbw.exception.ErrorLevel;
import com.bbw.god.game.config.god.GodConfig;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.mc.mail.MailAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 可能被其他模块用到的关于神仙的方法在此定义
 *
 * @author suhq
 * @date 2018年10月19日 下午3:32:21
 */
@Slf4j
@Service
public class GodService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ServerGodDayConfigService dayConfig;
    @Autowired
    private MailAction mngNotify;

    /**
     * 获得当前生效中的附体神仙。生效根据UserGod.isEffect判断
     *
     * @return
     */
    public Optional<UserGod> getAttachGod(GameUser usr) {
        Optional<Long> userGodId = usr.getStatus().getAttachingGod();
        if (userGodId.isPresent()) {
            Optional<UserGod> userGod = gameUserService.getUserData(usr.getId(), userGodId.get(), UserGod.class);
            if (userGod.isPresent() && userGod.get().ifEffect()) {
                return Optional.of(userGod.get());
            }
        }
        return Optional.empty();
    }

    /**
     * 今天仍然在位置上，还没有被玩家获得过的神仙
     *
     * @param sid
     * @param roadId
     * @param uid
     * @return
     */
    public Optional<ServerGod> getGodRemainOnRoad(int sid, int roadId, Long uid) {
        List<ServerGod> todayGods = getTodayGods(sid);
        Optional<ServerGod> positionGod = todayGods.stream()
                .filter(serverGod -> serverGod.getPosition().intValue() == roadId).findFirst();
        // 位置上没有神仙
        if (!positionGod.isPresent()) {
            return positionGod;
        }
        // 获取玩家的神仙数据
        List<UserGod> userTodayGods = getTodayAttachGod(uid);
        Optional<UserGod> todayHasAttached = userTodayGods.stream()
                .filter(userGod -> userGod.getServerGodId().longValue() == positionGod.get().getId()).findFirst();
        // 今天已经获得过
        if (todayHasAttached.isPresent()) {
            return Optional.empty();
        }
        // 返回当前位置的神仙
        return positionGod;
    }

    /**
     * 获得今日未附体的神仙
     *
     * @param user
     * @return
     */
    public List<ServerGod> getUnAttachGods(GameUser user) {
        List<ServerGod> todayGods = getTodayGods(user.getServerId());
        List<UserGod> todayAttachGod = getTodayAttachGod(user.getId());
        if (todayAttachGod.size() > 0) {
            List<Long> attachGodIds = todayAttachGod.stream().map(ug -> ug.getServerGodId())
                    .collect(Collectors.toList());
            todayGods = todayGods.stream().filter(sg -> !attachGodIds.contains(sg.getId()))
                    .collect(Collectors.toList());
        }
        return todayGods;

    }

    /**
     * 送走附在身上的神仙
     */
    public void setUnvalid(GameUser user, UserGod userGod) {
        if (userGod != null) {
            userGod.setAttachEndTime(DateUtil.now());
            userGod.setRemainStep(0);
            gameUserService.updateItem(userGod);
            user.getStatus().setAttachingGod(Optional.empty());
            user.updateStatus();
        }
    }

    /**
     * 依附神仙
     *
     * @param user
     * @param newGod
     */
    public void attachGod(GameUser user, UserGod newGod) {
        if (newGod.getBaseId() == GodEnum.BBX.getValue()) {
            gameUserService.addItem(user.getId(), newGod);
            return;
        }
        Optional<UserGod> userGod = getAttachGod(user);//
        // 目前身上有神仙，先送神
        if (userGod.isPresent()) {
            setUnvalid(user, userGod.get());
        }
        // 依附神仙
        user.getStatus().setAttachingGod(Optional.of(newGod.getId()));
        user.updateStatus();
        gameUserService.addItem(user.getId(), newGod);
    }

    /**
     * 获得玩家神仙战斗铜钱收益效果
     *
     * @param gu
     * @return
     */
    public int getCopperAddRate(GameUser gu) {
        int copperAddRate = 0;
        Optional<UserGod> userGod = getAttachGod(gu);
        if (userGod.isPresent()) {
            int attachGod = userGod.get().getBaseId();
            if (attachGod == GodEnum.XCS.getValue()) {// 50步内战斗金币收益+50%
                copperAddRate += GodConfig.bean().getXcsCopperAddRate();
            } else if (attachGod == GodEnum.DCS.getValue()) {// 50步内战斗金币收益翻倍
                copperAddRate += GodConfig.bean().getDcsCopperAddRate();
            } else if (attachGod == GodEnum.QS.getValue()) {// 50步内战斗获取金钱少20%
                copperAddRate += GodConfig.bean().getQsCopperAddRate();
            }
        }
        return copperAddRate;
    }

    /**
     * 获得玩家神仙卡牌掉率加成
     *
     * @param gu
     * @return
     */
    public int getCardDropRate(GameUser gu) {
        int cardDropRate = 0;
        Optional<UserGod> userGod = getAttachGod(gu);
        if (userGod.isPresent()) {
            int attachGodId = userGod.get().getBaseId();
            if (attachGodId == GodEnum.XFS.getValue()) {
                return GodConfig.bean().getXfsCardDropRate();
            }
            if (attachGodId == GodEnum.DFS.getValue()) {
                return GodConfig.bean().getXfsCardDropRate();
            }
            if (attachGodId == GodEnum.SS.getValue()) {
                return GodConfig.bean().getSsCardDropRate();
            }
        }
        return cardDropRate;
    }

    /**
     * 返回虚拟的神仙
     *
     * @return
     */
    public ServerGod getUnrealServerGod(int sId, int godId) {
        ServerGod serverGod = ServerGod.instanceVirtualGod(sId, godId);
        return serverGod;
    }

    /**
     * 获得今日附体的神仙
     *
     * @param guId
     * @return
     */
    private List<UserGod> getTodayAttachGod(long guId) {
        List<UserGod> attachGods = gameUserService.getMultiItems(guId, UserGod.class);
        return attachGods.stream().filter(ug -> DateUtil.isToday(ug.getAttachTime())).collect(Collectors.toList());
    }

    /**
     * 获得今日神仙
     *
     * @param sId
     * @return
     */
    private List<ServerGod> getTodayGods(int sId) {
        List<ServerGod> todayGods = dayConfig.getTodayGods(sId);
        if (todayGods == null) {
            String errorNoticeInfo = DateUtil.getTodayInt() + "的神仙数据没有生成！";
            log.error(errorNoticeInfo);
            // 邮件通知
            mngNotify.notifyCoder(ErrorLevel.HIGH, errorNoticeInfo, "");
        }
        return todayGods;
    }

}
