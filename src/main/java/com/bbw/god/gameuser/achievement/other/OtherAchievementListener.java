package com.bbw.god.gameuser.achievement.other;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.chanjie.event.ChanjieGainHeadEvent;
import com.bbw.god.game.chanjie.event.ChanjieSpecailHonorEvent;
import com.bbw.god.game.chanjie.event.EPChanjieGainHead;
import com.bbw.god.game.chanjie.event.EPChanjieSpecailHonor;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.combat.event.EPEliteYeGuaiFightWin;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.combat.event.EliteYeGuaiFightWinEvent;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgCoc;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.zxz.enums.ZxzDifficultyEnum;
import com.bbw.god.game.zxz.event.ZxzClearanceScoreEvent;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.AchievementServiceFactory;
import com.bbw.god.gameuser.achievement.BaseAchievementService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.equipment.event.*;
import com.bbw.god.gameuser.card.event.*;
import com.bbw.god.gameuser.chamberofcommerce.CocHonorEnum;
import com.bbw.god.gameuser.chamberofcommerce.UserCocInfo;
import com.bbw.god.gameuser.chamberofcommerce.event.BuyCocBagEvent;
import com.bbw.god.gameuser.chamberofcommerce.event.CocUpLvEvent;
import com.bbw.god.gameuser.chamberofcommerce.event.EPBuyCocBag;
import com.bbw.god.gameuser.chamberofcommerce.event.EPCocUpLv;
import com.bbw.god.gameuser.level.EPGuLevelUp;
import com.bbw.god.gameuser.level.GuLevelUpEvent;
import com.bbw.god.gameuser.special.UserSpecialSaleRecord;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialDeductEvent;
import com.bbw.god.gameuser.treasure.event.EPTreasureAdd;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureAddEvent;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import com.bbw.god.server.maou.bossmaou.attackinfo.BossMaouAttackSummary;
import com.bbw.god.server.maou.bossmaou.attackinfo.BossMaouAttackSummaryService;
import com.bbw.god.server.maou.bossmaou.event.BossMaouAwardSendEvent;
import com.bbw.god.server.maou.bossmaou.event.EPBossMaou;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 特殊成就监听器
 * @date 2020/5/18 10:33
 **/
@Component
@Async
@Slf4j
public class OtherAchievementListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private AchievementServiceFactory achievementServiceFactory;
    @Autowired
    private BossMaouAttackSummaryService bossMaouAttackSummaryService;
    @Autowired
    private UserCardService userCardService;

    @EventListener
    @Order(1000)
    public void fightWin(CombatFightWinEvent event) {
        try {
            EPFightEnd ep = (EPFightEnd) event.getSource();
            long uid = ep.getGuId();
            if (ep.getFightType() == FightTypeEnum.YG) {
                FightSubmitParam param = ep.getFightSubmit();
                // 对手等级
                int oppLevel = param.getOppLv();
                UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
                if (param.isNotLostCard() && oppLevel >= 15) {
                    BaseAchievementService service650 = achievementServiceFactory.getById(650);
                    service650.achieve(uid, 1, info, ep.getRd());
                }
                if (param.isNotLostBlood() && oppLevel >= 15) {
                    BaseAchievementService service660 = achievementServiceFactory.getById(660);
                    service660.achieve(uid, 1, info, ep.getRd());
                }
            }
        } catch (NumberFormatException e) {
            log.error(e.getMessage(), e);
        }
    }

    @EventListener
    @Order(1000)
    public void sendMaouAward(BossMaouAwardSendEvent event) {
        EPBossMaou ep = event.getEP();
        ServerBossMaou bossMaou = ep.getBossMaou();
        List<BossMaouAttackSummary> ranker = bossMaouAttackSummaryService.getAttackInfoSorted(bossMaou);
        if (ListUtil.isNotEmpty(ranker)) {
            BossMaouAttackSummary summary = ranker.get(0);
            Integer beatedBlood = summary.getBeatedBlood();
            Integer totalBlood = bossMaou.getTotalBlood();
            if (getPercent(beatedBlood, totalBlood) >= 0.7) {
                long uid = summary.getGuId();
                BaseAchievementService service900 = achievementServiceFactory.getById(900);
                UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
                service900.achieve(uid, 1, info, ep.getRd());
            }
        }
    }

    private Float getPercent(int num, int total) {
        return (float) num / (float) total;
    }

    @EventListener
    @Order(1000)
    public void eliteYeGuaiFightWin(EliteYeGuaiFightWinEvent event) {
        try {
            EPEliteYeGuaiFightWin ep = event.getEP();
            Long uid = ep.getGuId();
            Integer type = ep.getType();
            Integer cardLevel = ep.getCardLevel();
            Integer cardHierarchy = ep.getCardHierarchy();
            if (40 == cardLevel && 10 == cardHierarchy) {
                UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
                BaseAchievementService baseAchievementService = achievementServiceFactory.getById(14500 + type);
                baseAchievementService.achieve(uid, 1, info, ep.getRd());
                int count = 0;
                for (int i = 14510; i <= 14550; i = i + 10) {
                    BaseAchievementService service = achievementServiceFactory.getById(i);
                    if (service.isAccomplished(info, i)) {
                        count++;
                    }
                }
                BaseAchievementService service14560 = achievementServiceFactory.getById(14560);
                service14560.achieve(uid, count, info, ep.getRd());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @EventListener
    @Order(1000)
    public void buyCocBag(BuyCocBagEvent event) {
        try {
            EPBuyCocBag ep = event.getEP();
            Long uid = ep.getGuId();
            int value = 0;
            UserCocInfo cocInfo = this.gameUserService.getSingleItem(uid, UserCocInfo.class);
            if (null != cocInfo && null != cocInfo.getCocShopLimt()) {
                List<Integer> buyLogs = cocInfo.getCocShopLimt().getHonorGiftsBuyLogs();
                value = buyLogs == null ? 0 : buyLogs.size();
            }
            BaseAchievementService service13480 = achievementServiceFactory.getById(13480);
            UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
            service13480.achieve(uid, value, info, ep.getRd());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @EventListener
    @Order(1000)
    public void cocHonorLevelUp(CocUpLvEvent event) {
        try {
            EPCocUpLv ep = event.getEP();
            Long uid = ep.getGuId();
            Integer level = ep.getLevel();
            CfgCoc cfgCoc = Cfg.I.getUniqueConfig(CfgCoc.class);
            List<CfgCoc.CfgCocHonorItem> honors = cfgCoc.getHonorList();
            CfgCoc.CfgCocHonorItem item =
                    honors.stream().filter(h -> h.getLevel().equals(level)).findFirst().orElse(null);
            if (item != null && level.equals(CocHonorEnum.FSSF.getLevel())) {
                BaseAchievementService service13490 = achievementServiceFactory.getById(13490);
                UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
                service13490.achieve(ep.getGuId(), 1, info, ep.getRd());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Order(1000)
    @EventListener
    public void addCard(UserCardAddEvent event) {
        try {
            EPCardAdd ep = event.getEP();
            long uid = ep.getGuId();
            //获得添加卡牌信息
            List<EPCardAdd.CardAddInfo> cardAddInfos = ep.getAddCards();
            //获得新卡牌信息
            List<EPCardAdd.CardAddInfo> newCardAddInfos = cardAddInfos.stream().filter(tmp -> tmp.isNew()).collect(Collectors.toList());
            //没有新卡牌，直接返回
            if (ListUtil.isEmpty(newCardAddInfos)) {
                return;
            }
            List<UserCard> userCards = userCardService.getUserCards(uid);
            UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
            // 殷洪+四天君
            long count136 = userCards.stream().filter(uc ->
                    CardTool.getYinHongCards().contains(CardTool.getNormalCardId(uc.getBaseId()))).count();
            BaseAchievementService service136 = achievementServiceFactory.getById(136);
            service136.achieve(uid, count136, info, ep.getRd());
            // 五行交融
            long count13300 = userCards.stream().filter(uc ->
                    Arrays.asList(102, 202, 301, 402, 501, 10102).contains(CardTool.getNormalCardId(uc.getBaseId()))).count();
            BaseAchievementService service13300 = achievementServiceFactory.getById(13300);
            service13300.achieve(uid, count13300, info, ep.getRd());
            // 古兽来袭
            long count14730 = userCards.stream().filter(uc ->
                    Arrays.asList(542, 441, 440, 346, 245, 141).contains(CardTool.getNormalCardId(uc.getBaseId()))).count();
            BaseAchievementService service14730 = achievementServiceFactory.getById(14730);
            service14730.achieve(uid, count14730, info, ep.getRd());
            //群星册-封神传奇
            long count17310 = userCards.stream().filter(uc ->
                    CardTool.getFlockStarBookCards().contains(CardTool.getNormalCardId(uc.getBaseId()))).count();
            BaseAchievementService service17310 = achievementServiceFactory.getById(17310);
            service17310.achieve(uid, count17310, info, ep.getRd());
            //群星册-众建贤才
            long count17320 = userCards.stream().filter(uc ->
                    CardTool.getFlockStarBookCards().contains(CardTool.getNormalCardId(uc.getBaseId()))).count();
            BaseAchievementService service17320 = achievementServiceFactory.getById(17320);
            service17320.achieve(uid, count17320, info, ep.getRd());
            //群星册-群星璀璨
            long count17330 = userCards.stream().filter(uc ->
                    CardTool.getFlockStarBookCards().contains(CardTool.getNormalCardId(uc.getBaseId()))).count();
            BaseAchievementService service17330 = achievementServiceFactory.getById(17330);
            service17330.achieve(uid, count17330, info, ep.getRd());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Order(1000)
    @EventListener
    public void cardLevelUp(UserCardLevelUpEvent event) {
        try {
            EPCardLevelUp ep = event.getEP();
            long uid = ep.getGuId();
            List<UserCard> userCards = userCardService.getUserCards(uid);
            UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
            // 羽化登仙
            long count820 = userCards.stream().filter(uc ->
                    uc.getLevel() >= 10 && CardTool.getCardById(uc.getBaseId()).getStar() >= 4).count();
            BaseAchievementService service820 = achievementServiceFactory.getById(820);
            service820.achieve(uid, count820, info, ep.getRd());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Order(1000)
    @EventListener
    public void changeCardSkill(UserCardSkillChangeEvent event) {
        try {
            EPCardSkillChange ep = event.getEP();
            Long uid = ep.getGuId();
            List<UserCard> userCards = userCardService.getUserCards(uid);
            Optional<UserCard> optional =
                    userCards.stream().filter(uc -> uc.getBaseId().equals(ep.getCardId())).findFirst();
            if (optional.isPresent()) {
                UserCard userCard = optional.get();
                log.info("玩家的强化技能信息：{}",userCard.getStrengthenInfo());
                log.info("玩家的练技次数：{}",userCard.getStrengthenInfo().gainUseSkillScrollTimes());
                Integer times = userCard.getStrengthenInfo().gainUseSkillScrollTimes();
                BaseAchievementService service13360 = achievementServiceFactory.getById(13360);
                UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
                service13360.achieve(uid, times, info, ep.getRd());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Order(1000)
    @EventListener
    public void hierarchyCard(UserCardHierarchyUpEvent event) {
        try {
            EPCardHierarchyUp ep = event.getEP();
            long uid = ep.getGuId();
            List<UserCard> userCards = userCardService.getUserCards(uid);
            // 师之尊者
            long count135 = userCards.stream().filter(uc ->
                    Arrays.asList(106, 333, 432).contains(CardTool.getNormalCardId(uc.getBaseId())) && uc.getHierarchy() >= 3).count();
            BaseAchievementService service135 = achievementServiceFactory.getById(135);
            UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
            service135.achieve(uid, count135, info, ep.getRd());
            //五行荟聚
            long count17300 = userCards.stream().filter(uc ->
                    CardTool.getFiveElementsGathering().contains(CardTool.getNormalCardId(uc.getBaseId())) && uc.getHierarchy() >= AchievementService17300.TARGET_HIERARCHY).count();
            BaseAchievementService service17300 = achievementServiceFactory.getById(17300);
            service17300.achieve(uid, count17300, info, ep.getRd());
            //星月皎洁
            long count17410 = userCards.stream().filter(uc ->
                    CardTool.getStarAndMoonBrightAndClean().contains(CardTool.getNormalCardId(uc.getBaseId())) && uc.getHierarchy() >= AchievementService17410.TARGET_HIERARCHY).count();
            BaseAchievementService service17410 = achievementServiceFactory.getById(17410);
            service17410.achieve(uid, count17410, info, ep.getRd());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @EventListener
    @Order(2)
    public void deductSpecial(SpecialDeductEvent event) {
        try {
            EPSpecialDeduct ep = event.getEP();
            WayEnum way = ep.getWay();
            if (way != WayEnum.TRADE) {
                return;
            }
            long uid = ep.getGuId();
            List<EPSpecialDeduct.SpecialInfo> specialInfoList = ep.getSpecialInfoList();
            List<Integer> specialIds = specialInfoList.stream()
                    .map(EPSpecialDeduct.SpecialInfo::getBaseSpecialIds).collect(Collectors.toList());

            GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
            int position = gu.getLocation().getPosition();
            UserSpecialSaleRecord record = this.gameUserService.getSingleItem(ep.getGuId(),
                    UserSpecialSaleRecord.class);
            if (record == null) {
                record = UserSpecialSaleRecord.instanceSaleRecord(ep.getGuId(), specialIds,
                        CityTool.getCityId(position));
                this.gameUserService.addItem(ep.getGuId(), record);
            }
            for (EPSpecialDeduct.SpecialInfo info : specialInfoList) {
                Integer specialId = info.getBaseSpecialIds();
                if (record.isNewSpecial(specialId)) {
                    record.addSpecial(specialId);
                }
            }
            record.updateSaleRecord(CityTool.getCityId(position), specialIds);
            this.gameUserService.updateItem(record);
            BaseAchievementService service13820 = achievementServiceFactory.getById(13820);
            UserAchievementInfo achievementInfo = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
            service13820.achieve(uid, record.getSaledSpecialList().size(), achievementInfo, ep.getRd());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @EventListener
    @Order(1000)
    public void levelUp(GuLevelUpEvent event) {
        try {
            EPGuLevelUp ep = event.getEP();
            Long uid = ep.getGuId();
            int newLevel = ep.getNewLevel();
            BaseAchievementService service10 = achievementServiceFactory.getById(10);
            BaseAchievementService service20 = achievementServiceFactory.getById(20);
            BaseAchievementService service30 = achievementServiceFactory.getById(30);
            BaseAchievementService service40 = achievementServiceFactory.getById(40);
            BaseAchievementService service50 = achievementServiceFactory.getById(50);
            BaseAchievementService service55 = achievementServiceFactory.getById(55);
            BaseAchievementService service60 = achievementServiceFactory.getById(60);
            List<BaseAchievementService> list = Arrays.asList(service10, service20, service30, service40, service50,
                    service55, service60);
            UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
            list.forEach(s -> {
                s.achieve(uid, newLevel, info, ep.getRd());
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @EventListener
    @Order(1000)
    public void updateSymbol(TreasureAddEvent event) {
        try {
            EPTreasureAdd ep = event.getEP();
            Long uid = ep.getGuId();
            WayEnum way = ep.getWay();
            if (way == WayEnum.UPDATE_SYMBOL) {
                EVTreasure evTreasure = ep.getAddTreasures().get(0);
                BaseAchievementService service = null;
                if (evTreasure.getId() == 20160) {
                    // 天力符
                    service = achievementServiceFactory.getById(13240);
                } else if (evTreasure.getId() == 20260) {
                    // 元阳符
                    service = achievementServiceFactory.getById(13250);
                }
                if (null != service) {
                    UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
                    service.achieve(uid, 1, info, ep.getRd());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Order(1000)
    @EventListener
    public void specialHonor(ChanjieSpecailHonorEvent event) {
        try {
            EPChanjieSpecailHonor ep = event.getEP();
            Long ddst = ep.getDdst();
            Long rbkd = ep.getRbkd();
            Long txzr = ep.getTxzr();
            Long yryy = ep.getYryy();
            if (ddst.equals(rbkd) && ddst.equals(txzr) && ddst.equals(yryy) && ddst > 0) {
                long uid = ddst;
                BaseAchievementService service11110 = achievementServiceFactory.getById(11110);
                UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
                service11110.achieve(uid, 1, info, ep.getRd());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @EventListener
    @Order(1000)
    public void gainDailyHeadLv(ChanjieGainHeadEvent event) {
        try {
            EPChanjieGainHead ep = event.getEP();
            int headlv = ep.getHeadlv();
            List<Long> uids = ep.getUids();
            if (DateUtil.isWeekDay(6)) {
                BaseAchievementService service;
                switch (headlv) {
                    case 8:
                        service = achievementServiceFactory.getById(13030);
                        break;
                    case 7:
                        service = achievementServiceFactory.getById(13040);
                        break;
                    case 6:
                        service = achievementServiceFactory.getById(13050);
                        break;
                    case 5:
                        service = achievementServiceFactory.getById(13060);
                        break;
                    case 4:
                        service = achievementServiceFactory.getById(13070);
                        break;
                    case 3:
                        service = achievementServiceFactory.getById(13080);
                        break;
                    case 2:
                        service = achievementServiceFactory.getById(13090);
                        break;
                    default:
                        return;
                }
                // 周六结算的时候，第一名是掌教师尊，所以从护教法王中移除
                for (int i = 0; i < uids.size(); i++) {
                    if (7 == headlv && 0 == i) {
                        continue;
                    }
                    UserAchievementInfo info = gameUserService.getSingleItem(uids.get(i), UserAchievementInfo.class);
                    service.achieve(uids.get(i), 1, info, ep.getRd());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 仙诀激活
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void xianJueActive(XianJueActiveEvent event) {
        try {
            EPXianJueActive ep = event.getEP();
            long uid = ep.getGuId();
            UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);

            // 金之仙诀
            BaseAchievementService service17550 = achievementServiceFactory.getById(17550);
            service17550.achieve(uid, service17550.getMyProgress(uid, info), info, ep.getRd());

            // 木之仙诀
            BaseAchievementService service17560 = achievementServiceFactory.getById(17560);
            service17560.achieve(uid, service17560.getMyProgress(uid, info), info, ep.getRd());

            // 水之仙诀
            BaseAchievementService service17570 = achievementServiceFactory.getById(17570);
            service17570.achieve(uid, service17570.getMyProgress(uid, info), info, ep.getRd());

            // 火之仙诀
            BaseAchievementService service17580 = achievementServiceFactory.getById(17580);
            service17580.achieve(uid, service17580.getMyProgress(uid, info), info, ep.getRd());

            // 土之仙诀
            BaseAchievementService service17590 = achievementServiceFactory.getById(17590);
            service17590.achieve(uid, service17590.getMyProgress(uid, info), info, ep.getRd());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 仙诀满研习
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void xianJueFullSutdy(XianJueStudyEvent event) {
        try {
            EPXianJueStudy ep = event.getEP();
            long uid = ep.getGuId();
            if (0 == ep.getFullStudyNum()) {
                return;
            }
            UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
            // 御器有成I
            BaseAchievementService service17600 = achievementServiceFactory.getById(17600);
            service17600.achieve(uid, service17600.getMyProgress(uid, info), info, ep.getRd());

            // 御器有成II
            BaseAchievementService service17610 = achievementServiceFactory.getById(17610);
            service17610.achieve(uid, service17610.getMyProgress(uid, info), info, ep.getRd());

            // 御器有成III
            BaseAchievementService service17620 = achievementServiceFactory.getById(17620);
            service17620.achieve(uid, service17620.getMyProgress(uid, info), info, ep.getRd());

            // 控宝有成I
            BaseAchievementService service17630 = achievementServiceFactory.getById(17630);
            service17630.achieve(uid, service17630.getMyProgress(uid, info), info, ep.getRd());

            // 控宝有成II
            BaseAchievementService service17640 = achievementServiceFactory.getById(17640);
            service17640.achieve(uid, service17640.getMyProgress(uid, info), info, ep.getRd());

            // 控宝有成III
            BaseAchievementService service17650 = achievementServiceFactory.getById(17650);
            service17650.achieve(uid, service17650.getMyProgress(uid, info), info, ep.getRd());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 仙诀满淬星
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void xianJueFullUpdateStar(XianJueUpdataStarEvent event) {
        try {
            EPXianJueUpdataStar ep = event.getEP();
            long uid = ep.getGuId();
            UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
            // 御器大成I
            BaseAchievementService service17660 = achievementServiceFactory.getById(17660);
            service17660.achieve(uid, service17660.getMyProgress(uid, info), info, ep.getRd());

            // 御器大成II
            BaseAchievementService service17670 = achievementServiceFactory.getById(17670);
            service17670.achieve(uid, service17670.getMyProgress(uid, info), info, ep.getRd());

            // 御器大成III
            BaseAchievementService service17680 = achievementServiceFactory.getById(17680);
            service17680.achieve(uid, service17680.getMyProgress(uid, info), info, ep.getRd());

            // 御器大成IV
            BaseAchievementService service17690 = achievementServiceFactory.getById(17690);
            service17690.achieve(uid, service17690.getMyProgress(uid, info), info, ep.getRd());

            // 控宝大成I
            BaseAchievementService service17700 = achievementServiceFactory.getById(17700);
            service17700.achieve(uid, service17700.getMyProgress(uid, info), info, ep.getRd());

            // 控宝大成II
            BaseAchievementService service17710 = achievementServiceFactory.getById(17710);
            service17710.achieve(uid, service17710.getMyProgress(uid, info), info, ep.getRd());

            // 控宝大成III
            BaseAchievementService service17720 = achievementServiceFactory.getById(17720);
            service17720.achieve(uid, service17720.getMyProgress(uid, info), info, ep.getRd());

            // 控宝大成IV
            BaseAchievementService service17730 = achievementServiceFactory.getById(17730);
            service17730.achieve(uid, service17730.getMyProgress(uid, info), info, ep.getRd());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 仙诀满参悟
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void xianJueFullComprehend(XianJueComprehendEvent event) {
        try {
            EPXianJueComprehend ep = event.getEP();
            long uid = ep.getGuId();
            if (0 == ep.getFullComprehendNum()) {
                return;
            }
            UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
            // 众醉独醒I
            BaseAchievementService service17740 = achievementServiceFactory.getById(17740);
            service17740.achieve(uid, service17740.getMyProgress(uid, info), info, ep.getRd());

            // 众醉独醒II
            BaseAchievementService service17750 = achievementServiceFactory.getById(17750);
            service17750.achieve(uid, service17750.getMyProgress(uid, info), info, ep.getRd());

            // 众醉独醒III
            BaseAchievementService service17760 = achievementServiceFactory.getById(17760);
            service17760.achieve(uid, service17760.getMyProgress(uid, info), info, ep.getRd());

            // 众醉独醒IV
            BaseAchievementService service17770 = achievementServiceFactory.getById(17770);
            service17770.achieve(uid, service17770.getMyProgress(uid, info), info, ep.getRd());

            // 低唱浅斟I
            BaseAchievementService service17780 = achievementServiceFactory.getById(17780);
            service17780.achieve(uid, service17780.getMyProgress(uid, info), info, ep.getRd());

            // 低唱浅斟II
            BaseAchievementService service17790 = achievementServiceFactory.getById(17790);
            service17790.achieve(uid, service17790.getMyProgress(uid, info), info, ep.getRd());

            // 低唱浅斟III
            BaseAchievementService service17800 = achievementServiceFactory.getById(17800);
            service17800.achieve(uid, service17800.getMyProgress(uid, info), info, ep.getRd());

            // 低唱浅斟IV
            BaseAchievementService service17810 = achievementServiceFactory.getById(17810);
            service17810.achieve(uid, service17810.getMyProgress(uid, info), info, ep.getRd());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 诛仙阵扫荡分
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void ZxzClearanceScore(ZxzClearanceScoreEvent event) {
        try {
            EPZxzClearanceScore ep = event.getEP();
            long uid = ep.getGuId();
            if (0 == ep.getClearanceScore()) {
                return;
            }
            int clearanceScore = ep.getClearanceScore();

            UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
            // 大破诛仙
            if (ZxzDifficultyEnum.DIFFICULTY_10.getDifficulty() == ep.getDifficulty()) {
                BaseAchievementService service17820 = achievementServiceFactory.getById(17820);
                service17820.achieve(uid, clearanceScore, info, ep.getRd());
            }


            // 大破戮仙
            if (ZxzDifficultyEnum.DIFFICULTY_20.getDifficulty() == ep.getDifficulty()) {
                BaseAchievementService service17830 = achievementServiceFactory.getById(17830);
                service17830.achieve(uid, clearanceScore, info, ep.getRd());
            }


            // 大破陷仙
            if (ZxzDifficultyEnum.DIFFICULTY_30.getDifficulty() == ep.getDifficulty()) {
                BaseAchievementService service17840 = achievementServiceFactory.getById(17840);
                service17840.achieve(uid, clearanceScore, info, ep.getRd());
            }


            // 大破绝仙
            if (ZxzDifficultyEnum.DIFFICULTY_40.getDifficulty() == ep.getDifficulty()) {
                BaseAchievementService service17850 = achievementServiceFactory.getById(17850);
                service17850.achieve(uid, clearanceScore, info, ep.getRd());
            }


            // 诛仙阵破
            if (ZxzDifficultyEnum.DIFFICULTY_50.getDifficulty() == ep.getDifficulty()) {
                BaseAchievementService service17860 = achievementServiceFactory.getById(17860);
                service17860.achieve(uid, clearanceScore, info, ep.getRd());
            }


        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


}
