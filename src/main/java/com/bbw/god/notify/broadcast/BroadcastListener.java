package com.bbw.god.notify.broadcast;

import com.bbw.common.LM;
import com.bbw.common.StrUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.event.EPCityAdd;
import com.bbw.god.city.event.UserCityAddEvent;
import com.bbw.god.city.yed.EPYeDTrigger;
import com.bbw.god.city.yed.YeDTriggerEvent;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.game.sxdh.event.SxdhTitleChange;
import com.bbw.god.game.sxdh.event.SxdhTitleChangeEvent;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.AchievementTool;
import com.bbw.god.gameuser.achievement.AchievementTypeEnum;
import com.bbw.god.gameuser.achievement.CfgAchievementEntity;
import com.bbw.god.gameuser.achievement.event.AchievementFinishEvent;
import com.bbw.god.gameuser.achievement.event.EPAchievementFinish;
import com.bbw.god.gameuser.biyoupalace.event.EPBiyouGainAward;
import com.bbw.god.gameuser.biyoupalace.event.EPBiyouGainAwardEvent;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.*;
import com.bbw.god.gameuser.chamberofcommerce.CocConstant;
import com.bbw.god.gameuser.chamberofcommerce.event.CocTaskFinishedEvent;
import com.bbw.god.gameuser.chamberofcommerce.event.EPTaskFinished;
import com.bbw.god.gameuser.treasure.event.EPCardDeify;
import com.bbw.god.gameuser.treasure.event.TreasureUseDeifyTokenEvent;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import com.bbw.god.server.ServerUserService;
import com.bbw.god.server.fst.event.EVFstWin;
import com.bbw.god.server.fst.event.FstWinEvent;
import com.bbw.god.server.fst.server.FstServerService;
import com.bbw.god.server.guild.event.EPGuildCreate;
import com.bbw.god.server.guild.event.GuildCreateEvent;
import com.bbw.god.server.maou.alonemaou.event.AloneMaouKilledEvent;
import com.bbw.god.server.maou.alonemaou.event.EPAloneMaou;
import com.bbw.god.statistics.ServerStatistic;
import com.bbw.god.statistics.StatisticKeyEnum;
import com.bbw.god.statistics.serverstatistic.GodServerStatisticService;
import com.bbw.mc.broadcast.BroadcastAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BroadcastListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private GodServerStatisticService godServerStatisticService;
    @Autowired
    private BroadcastAction broadcastAction;
    @Autowired
    private FstServerService fstService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private UserCardService userCardService;

    /**
     * 卡牌升阶
     */
    @Async
    @EventListener
    @Order(1000)
    public void hierarchyUp(UserCardHierarchyUpEvent event) {
        EPCardHierarchyUp ep = event.getEP();
        int cardId = ep.getCardId();
        GameUser gu = gameUserService.getGameUser(ep.getGuId());
        UserCard userCard = userCardService.getUserCard(gu.getId(), cardId);
        // 广播
        String broadCastInfo = LM.I.getMsgByUid(ep.getGuId(), "broadcast.card.hie.update", gu.getRoleInfo().getNickname(), userCard.gainCard().getName(), userCard.getHierarchy());
        broadcastAction.broadcast(gu.getServerId(), broadCastInfo);
    }

    /**
     * 卡牌升级
     *
     * @param event
     */
    @Async
    @EventListener
    @Order(1000)
    public void levelUp(UserCardLevelUpEvent event) {
        EPCardLevelUp ep = event.getEP();
        int cardId = ep.getCardId();
        int newLevel = ep.getNewLevel();
        if (newLevel >= 10) {
            // 广播
            GameUser gu = gameUserService.getGameUser(ep.getGuId());
            CfgCardEntity card = CardTool.getCardById(cardId);
            String broadCastInfo = LM.I.getMsgByUid(ep.getGuId(), "broadcast.card.lv.update", gu.getRoleInfo().getNickname(), card.getName(), newLevel);
            broadcastAction.broadcast(gu.getServerId(), broadCastInfo);
        }

    }

    /**
     * 获得卡牌
     *
     * @param event
     */
    @Async
    @EventListener
    public void addCard(UserCardAddEvent event) {
        EPCardAdd ep = event.getEP();
        GameUser gu = null;
        for (EPCardAdd.CardAddInfo addCard : ep.getAddCards()) {
            CfgCardEntity card = CardTool.getCardById(addCard.getCardId());
            if (card.getStar() > 3 && StrUtil.isNotBlank(ep.getBroadcastWayInfo())) {
                if (null == gu) {
                    gu = gameUserService.getGameUser(ep.getGuId());
                }
                String broadCastInfo = LM.I.getMsgByUid(ep.getGuId(), "broadcast.card.add", gu.getRoleInfo().getNickname(), ep.getBroadcastWayInfo(), card.getStar(), card.getName());
                broadcastAction.broadcast(gu.getServerId(), broadCastInfo);
            }
        }
    }

    /**
     * 攻下城池广播
     *
     * @param event
     */
    @Async
    @EventListener
    public void addUserCity(UserCityAddEvent event) {
        EventParam<EPCityAdd> ep = (EventParam<EPCityAdd>) event.getSource();
        if (ep.getValue().isNightmare()) {
            return;
        }
        GameUser gu = gameUserService.getGameUser(ep.getGuId());
        CfgCityEntity city = CityTool.getCityById(ep.getValue().getCityId());
        // 广播
        if (city.getLevel() == 5) {
            String broadCastInfo = LM.I.getMsgByUid(ep.getGuId(), "broadcast.city5.add.simple", gu.getRoleInfo().getNickname(), city.getName());
            broadcastAction.broadcast(gu.getServerId(), broadCastInfo);
        }
        String broadCastInfo = "";
        boolean isFirst = false;
        switch (city.getLevel()) {
            case 3:
                broadCastInfo = "broadcast.city3.add";
                break;
            case 4:
                broadCastInfo = "broadcast.city4.add";
                ServerStatistic stat = godServerStatisticService.getStatistic(gu.getServerId(), StatisticKeyEnum.FIRST_CC4);
                if (stat != null && stat.getUid().equals(ep.getGuId())) {
                    isFirst = true;
                }
                break;
            case 5:
                broadCastInfo = "broadcast.city5.add";
                ServerStatistic stat2 = godServerStatisticService.getStatistic(gu.getServerId(), StatisticKeyEnum.FIRST_CC5);
                if (stat2 != null && stat2.getUid().equals(ep.getGuId())) {
                    isFirst = true;
                }
                break;
            default:
                return;
        }
        List<UserCity> userCities = userCityService.getUserOwnCities(ep.getGuId());
        int num = (int) userCities.stream().filter(p -> p.gainCity().getLevel() == city.getLevel()).count();
        if (num == 1 && isFirst) {
            if (city.getLevel() == 4) {
                broadCastInfo = "broadcast.city4.add.first";
            } else {
                broadCastInfo = "broadcast.city5.add.first";
            }
            broadCastInfo = LM.I.getMsgByUid(ep.getGuId(), broadCastInfo, gu.getRoleInfo().getNickname());
        } else {
            broadCastInfo = LM.I.getMsgByUid(ep.getGuId(), broadCastInfo, gu.getRoleInfo().getNickname(), num);
        }
        broadcastAction.broadcast(gu.getServerId(), broadCastInfo);
    }

    /**
     * 攻下梦魇城池城池广播
     *
     * @param event
     */
    @Async
    @EventListener
    public void addUserNightmareCity(UserCityAddEvent event) {
        EventParam<EPCityAdd> ep = (EventParam<EPCityAdd>) event.getSource();
        if (!ep.getValue().isNightmare()) {
            return;
        }
        GameUser gu = gameUserService.getGameUser(ep.getGuId());
        CfgCityEntity city = CityTool.getCityById(ep.getValue().getCityId());
        // 广播
        if (city.getLevel() == 5) {
            String broadCastInfo = LM.I.getMsgByUid(ep.getGuId(), "broadcast.city5.nightmare.add.simple", gu.getRoleInfo().getNickname(), city.getName());
            broadcastAction.broadcast(gu.getServerId(), broadCastInfo);
        }
        String broadCastInfo = "";
        boolean isFirst = false;
        switch (city.getLevel()) {
            case 3:
                broadCastInfo = "broadcast.city3.nightmare.add";
                break;
            case 4:
                broadCastInfo = "broadcast.city4.nightmare.add";
                ServerStatistic stat = godServerStatisticService.getStatistic(gu.getServerId(), StatisticKeyEnum.FIRST_CC4);
                if (stat != null && stat.getUid().equals(ep.getGuId())) {
                    isFirst = true;
                }
                break;
            case 5:
                broadCastInfo = "broadcast.city5.nightmare.add";
                ServerStatistic stat2 = godServerStatisticService.getStatistic(gu.getServerId(), StatisticKeyEnum.FIRST_CC5);
                if (stat2 != null && stat2.getUid().equals(ep.getGuId())) {
                    isFirst = true;
                }
                break;
            default:
                return;
        }
        List<UserNightmareCity> userCities = userCityService.getUserOwnNightmareCities(ep.getGuId());
        int num = (int) userCities.stream().filter(p -> p.gainCity().getLevel() == city.getLevel()).count();
        if (num == 1 && isFirst) {
            if (city.getLevel() == 4) {
                broadCastInfo = "broadcast.city4.nightmare.add.first";
            } else {
                broadCastInfo = "broadcast.city5.nightmare.add.first";
            }
            broadCastInfo = LM.I.getMsgByUid(ep.getGuId(), broadCastInfo, gu.getRoleInfo().getNickname());
        } else {
            broadCastInfo = LM.I.getMsgByUid(ep.getGuId(), broadCastInfo, gu.getRoleInfo().getNickname(), num);
        }
        broadcastAction.broadcast(gu.getServerId(), broadCastInfo);
    }

    /**
     * 封神台广播
     *
     * @param event
     */
    @Async
    @EventListener
    public void fstBroadcast(FstWinEvent event) {
        EventParam<EVFstWin> ep = (EventParam<EVFstWin>) event.getSource();
        EVFstWin evFst = ep.getValue();
        int sId = gameUserService.getActiveSid(ep.getGuId());
        String chanllengerName = serverUserService.getNickNameByUid(sId, ep.getGuId());
        String oppName = fstService.getRankingUserNickName(evFst.getOppId());
        String formatInfo = "";
        String broadcastInfo = "";
        if (evFst.getOldOppRank() == 1) {
            formatInfo = "broadcast.fst.no1";
            broadcastInfo = LM.I.getMsgByUid(ep.getGuId(), formatInfo, chanllengerName, oppName);
            broadcastAction.broadcast(sId, broadcastInfo);
        }
        if (evFst.getMyWinStreak() >= 5) {
            formatInfo = "broadcast.fst.winStreak";
            broadcastInfo = LM.I.getMsgByUid(ep.getGuId(), formatInfo, chanllengerName, oppName, evFst.getMyWinStreak());
            broadcastAction.broadcast(sId, broadcastInfo);
        }
        if (evFst.getOldOppWinStreak() >= 5) {
            formatInfo = "broadcast.fst.end.opp.winStreak";
            broadcastInfo = LM.I.getMsgByUid(ep.getGuId(), formatInfo, chanllengerName, oppName, evFst.getOldOppWinStreak());
            broadcastAction.broadcast(sId, broadcastInfo);
        }
    }

    /**
     * 野地事件广播
     *
     * @param event
     */
    @Async
    @EventListener
    public void ydBroadcast(YeDTriggerEvent event) {
        EventParam<EPYeDTrigger> ep = (EventParam<EPYeDTrigger>) event.getSource();
        EPYeDTrigger evYd = ep.getValue();
        int sId = gameUserService.getActiveSid(ep.getGuId());
        String nickname = serverUserService.getNickNameByUid(sId, ep.getGuId());
        String broadcastInfo = "";
        if (evYd.getEvent() == YdEventEnum.XIAO_BAI && evYd.getIncome() / 10000 >= 20) {
            broadcastInfo = LM.I.getMsgByUid(ep.getGuId(), "broadcast.yd.xiaobai", nickname, evYd.getIncome() / 10000);
        } else if (evYd.getEvent() == YdEventEnum.DA_MA && evYd.getGoodsIds().size() >= 10) {
            broadcastInfo = LM.I.getMsgByUid(ep.getGuId(), "broadcast.yd.dama", nickname, evYd.getGoodsIds().size());
        }
        if (StrUtil.isNotBlank(broadcastInfo)) {
            broadcastAction.broadcast(sId, broadcastInfo);
        }

    }

    /**
     * 获取领取技能卷轴
     *
     * @param event
     */
    @Async
    @EventListener
    public void biYouBroadcast(EPBiyouGainAwardEvent event) {
        EPBiyouGainAward gain = event.getEP();
        if (gain.getChapter() < 4) {
            return;
        }
        List<Award> awards = gain.getAwards();
        if (awards.isEmpty()) {
            return;
        }
        CfgTreasureEntity cfgTreasureEntity = TreasureTool.getTreasureById(awards.get(0).gainAwardId());
        if (cfgTreasureEntity.getType() != 56) {
            // 56为卷轴类型
            return;
        }
        int sId = gameUserService.getActiveSid(gain.getGuId());
        String nickname = serverUserService.getNickNameByUid(sId, gain.getGuId());
        String broadcastInfo = LM.I.getMsgByUid(gain.getGuId(), "broadcast.bypalace.skillScroll.add", nickname, cfgTreasureEntity.getName());
        broadcastAction.broadcast(sId, broadcastInfo);
    }

    /**
     * 首次攻打独占魔王成功广播
     *
     * @param event
     */
    @Async
    @EventListener
    public void firstkilledAloneMaouBroadcast(AloneMaouKilledEvent event) {
        EPAloneMaou ep = event.getEP();
        int level = ep.getMaouLevelInfo().getMaouLevel();
        if (!(level == 3 || level == 5 || level >= 7)) {
            return;
        }
        String[] typeNames = {"金", "木", "水", "火", "土"};
        String typeName = typeNames[ep.getAloneMaou().getType() / 10 - 1];
        int sId = gameUserService.getActiveSid(ep.getGuId());
        String nickname = serverUserService.getNickNameByUid(sId, ep.getGuId());
        String broadcastInfo = LM.I.getMsgByUid(ep.getGuId(), "broadcast.alonemaou.first.kill", nickname, typeName, level);
        broadcastAction.broadcast(sId, broadcastInfo);
    }

    /**
     * 攻打独占魔王6层以上 广播
     *
     * @param event
     */
    @Async
    @EventListener
    public void killedAloneMaouLevel6Broadcast(AloneMaouKilledEvent event) {
        EPAloneMaou ep = event.getEP();
        int level = ep.getMaouLevelInfo().getMaouLevel();
        if (level < 6) {
            return;
        }
        String[] typeNames = {"金", "木", "水", "火", "土"};
        String typeName = typeNames[ep.getAloneMaou().getType() / 10 - 1];
        int sId = gameUserService.getActiveSid(ep.getGuId());
        String nickname = serverUserService.getNickNameByUid(sId, ep.getGuId());
        String broadcastInfo = LM.I.getMsgByUid(ep.getGuId(), "broadcast.alonemaou.high.level.kill", nickname, level, typeName);
        broadcastAction.broadcast(sId, broadcastInfo);
    }

    @Async
    @EventListener
    public void sxdhTitleChangeBroadcast(SxdhTitleChangeEvent event) {
        SxdhTitleChange sxdh = event.getEP();//【区服名称角色名称】战胜众神，成为【天尊】！
        if (sxdh.getTitle() != 22) {
            //天尊为22
            return;
        }
        GameUser gu = gameUserService.getGameUser(sxdh.getGuId());
        CfgServerEntity serverEntity = ServerTool.getServer(gu.getServerId());
        String broadcastInfo = LM.I.getMsgByUid(sxdh.getGuId(), "broadcast.sxdh.title.change", serverEntity.getName(), gu.getRoleInfo().getNickname());
        for (Integer sid : sxdh.getSids()) {
            broadcastAction.broadcast(sid, broadcastInfo);
        }
    }

    @Async
    @EventListener
    public void cardSKillChangeBroadcast(UserCardSkillChangeEvent event) {
        EPCardSkillChange ep = event.getEP();
        int sId = gameUserService.getActiveSid(ep.getGuId());
        String nickname = serverUserService.getNickNameByUid(sId, ep.getGuId());
        String broadcastInfo = LM.I.getMsgByUid(ep.getGuId(), "broadcast.card.new.skill", nickname, ep.getCardName(), ep.getSkillScrollName());
        broadcastAction.broadcast(sId, broadcastInfo);
    }

    @Async
    @EventListener
    public void createGuildBroadcast(GuildCreateEvent event) {
        EPGuildCreate ep = event.getEP();
        int sId = gameUserService.getActiveSid(ep.getGuId());
        String nickname = serverUserService.getNickNameByUid(sId, ep.getGuId());
        String msgContent = LM.I.getMsgByUid(ep.getGuId(), "broadcast.guild.create", nickname, ep.getName());
        broadcastAction.broadcast(sId, msgContent);
    }

    @Async
    @EventListener
    public void finishAchievement(AchievementFinishEvent event) {
        EPAchievementFinish ep = event.getEP();
        Integer achievementId = ep.getAchievementId();
        CfgAchievementEntity achievement = AchievementTool.getAchievement(achievementId);
        if (achievement.getIsBroadCast()) {
            int sId = gameUserService.getActiveSid(ep.getGuId());
            String nickname = serverUserService.getNickNameByUid(sId, ep.getGuId());
            String typeName = AchievementTypeEnum.fromValue(achievement.getType()).getName();
            String achievementName = achievement.getName();
            String msgContent = LM.I.getMsgByUid(ep.getGuId(), "broadcast.achievement.achieved", nickname, typeName, achievementName);
            broadcastAction.broadcast(sId, msgContent);
        }
    }

    @Async
    @EventListener
    public void finishedCocTask(CocTaskFinishedEvent event) {
        EPTaskFinished ep = event.getEP();
        if (ep.getLevel() == CocConstant.LEVEL_LOW) {
            return;
        }
        int sId = gameUserService.getActiveSid(ep.getGuId());
        broadcastAction.broadcast(sId, ep.getBroadcast());
    }

    @Async
    @EventListener
    public void deifyCardEvent(TreasureUseDeifyTokenEvent event) {
        EPCardDeify ep = event.getEP();
        CfgCardEntity cardEntity = CardTool.getCardById(ep.getCardId());
        GameUser gu = gameUserService.getGameUser(ep.getGuId());
        String msg = LM.I.getMsgByUid(ep.getGuId(), "broadcast.card.to.deify", gu.getRoleInfo().getNickname(), cardEntity.getStar(), cardEntity.getName());
        broadcastAction.broadcast(gu.getServerId(), msg);
    }
}
