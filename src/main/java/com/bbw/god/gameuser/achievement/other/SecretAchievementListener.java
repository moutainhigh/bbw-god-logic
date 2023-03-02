package com.bbw.god.gameuser.achievement.other;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.mixd.EPOutMxd;
import com.bbw.god.city.mixd.OutMxdEvent;
import com.bbw.god.city.yed.EPYeDTrigger;
import com.bbw.god.city.yed.YeDTriggerEvent;
import com.bbw.god.city.yeg.RDYeGuaiEliteBox;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.city.yeg.event.EPOpenYeGuaiBox;
import com.bbw.god.city.yeg.event.OpenYeGuaiBoxEvent;
import com.bbw.god.db.entity.InsGamePveDetailEntity;
import com.bbw.god.db.service.InsGamePveDetailService;
import com.bbw.god.event.EventParam;
import com.bbw.god.exchange.exchangecode.event.EPExchangeCode;
import com.bbw.god.exchange.exchangecode.event.ExchangeCodeEvent;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.chanjie.event.ChanjieLDFSFourWinEvent;
import com.bbw.god.game.chanjie.event.EPChanjieLDFSFourWin;
import com.bbw.god.game.combat.event.*;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.AchievementServiceFactory;
import com.bbw.god.gameuser.achievement.BaseAchievementService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.card.equipment.event.EPCardZhiBaoAdd;
import com.bbw.god.gameuser.card.equipment.event.ZhiBaoEvent;
import com.bbw.god.gameuser.card.event.EPCardSkillReset;
import com.bbw.god.gameuser.card.event.UserCardSkillResetEvent;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.nightmarenvwam.NightmareNvWamCfgTool;
import com.bbw.god.gameuser.nightmarenvwam.listener.EPPinchPeople;
import com.bbw.god.gameuser.nightmarenvwam.listener.PinchPeopleEvent;
import com.bbw.god.gameuser.res.ResAddInfo;
import com.bbw.god.gameuser.res.copper.CopperAddEvent;
import com.bbw.god.gameuser.res.copper.CopperDeductEvent;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.res.copper.EPCopperDeduct;
import com.bbw.god.gameuser.shake.EPShake;
import com.bbw.god.gameuser.shake.ShakeEvent;
import com.bbw.god.gameuser.special.UserSpecialSaleRecord;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.special.event.*;
import com.bbw.god.gameuser.treasure.UserTreasureEffect;
import com.bbw.god.gameuser.treasure.UserTreasureEffectService;
import com.bbw.god.gameuser.treasure.UserTreasureRecord;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import com.bbw.god.gameuser.treasure.event.*;
import com.bbw.god.mall.cardshop.CardPoolEnum;
import com.bbw.god.mall.cardshop.event.DrawEndEvent;
import com.bbw.god.mall.cardshop.event.EPDraw;
import com.bbw.god.mall.snatchtreasure.UserSnatchTreasure;
import com.bbw.god.mall.snatchtreasure.event.EPSnatchTreasureDraw;
import com.bbw.god.mall.snatchtreasure.event.SnatchTreasureDrawEvent;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.flx.FlxCaiShuZiBet;
import com.bbw.god.server.flx.FlxService;
import com.bbw.god.server.flx.FlxYaYaLeBet;
import com.bbw.god.server.flx.event.CaiShuZiWinEvent;
import com.bbw.god.server.flx.event.EPCaiShuZiWin;
import com.bbw.god.server.flx.event.EPYaYaLeWin;
import com.bbw.god.server.flx.event.YaYaLeWinEvent;
import com.bbw.god.server.god.AttachNewGodEvent;
import com.bbw.god.server.god.ServerGod;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import com.bbw.god.server.maou.bossmaou.attackinfo.BossMaouAttackSummary;
import com.bbw.god.server.maou.bossmaou.attackinfo.BossMaouAttackSummaryService;
import com.bbw.god.server.maou.bossmaou.event.BossMaouAwardSendEvent;
import com.bbw.god.server.maou.bossmaou.event.BossMaouKilledEvent;
import com.bbw.god.server.maou.bossmaou.event.EPBossMaou;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 秘闻类成就监听器
 * @date 2020/5/26 16:26
 */
@Component
@Slf4j
@Async
public class SecretAchievementListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserSpecialService userSpecialService;
    @Autowired
    private BossMaouAttackSummaryService bossMaouAttackSummaryService;
    @Autowired
    private FlxService flxService;
    @Autowired
    private AchievementServiceFactory achievementServiceFactory;
    @Autowired
    private InsGamePveDetailService insGamePveDetailService;
    @Autowired
    private UserTreasureEffectService userTreasureEffectService;
    @Autowired
    private UserTreasureRecordService userTreasureRecordService;

    private static final List<Integer> statusTreasureList = Arrays.asList(TreasureEnum.QL.getValue(),
            TreasureEnum.CSZ.getValue(), TreasureEnum.LBJQ.getValue(), TreasureEnum.SBX.getValue());
    private static final List<Integer> effectTreasures = Arrays.asList(
            TreasureEnum.MBX.getValue(),
            TreasureEnum.CSZ.getValue(),
            TreasureEnum.LBJQ.getValue(),
            TreasureEnum.SBX.getValue(),
            TreasureEnum.QL.getValue(),
            TreasureEnum.SSF.getValue());
    @EventListener
    @Order(1000)
    @SuppressWarnings("unchecked")
    //附体新的神仙
    public void attachGod(AttachNewGodEvent event) {
        EventParam<ServerGod> ep = (EventParam<ServerGod>) event.getSource();
        List<UserTreasureEffect> treasureEffects = userTreasureEffectService.getAllEffects(ep.getGuId());
        long count = treasureEffects.stream().filter(t ->
                statusTreasureList.contains(t.getBaseId()) && t.getRemainEffect() > 0).count();
        if (count >= 3 && 1 == gameUserService.getGameUser(ep.getGuId()).getSetting().getActiveMbx()) {
            BaseAchievementService service14640 = achievementServiceFactory.getById(14640);
            UserAchievementInfo info = gameUserService.getSingleItem(ep.getGuId(), UserAchievementInfo.class);
            service14640.achieve(ep.getGuId(), 1, info, ep.getRd());
        }
    }

    //出迷仙洞事件
    @EventListener
    @Order(1000)
    public void outMxd(OutMxdEvent event) {
        EPOutMxd ep = event.getEP();
        List<Integer> guAwardPos = ep.getGuAwardPos();
        long uid = ep.getGuId();
        if (ListUtil.isEmpty(guAwardPos)) {
            BaseAchievementService service13840 = achievementServiceFactory.getById(13840);
            UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
            service13840.achieve(uid, 1, info, ep.getRd());
        }
    }

    //卡牌技能重置事件
    @EventListener
    @Order(1000)
    public void resetCardSkill(UserCardSkillResetEvent event) {
        EPCardSkillReset ep = event.getEP();
        Long uid = ep.getGuId();
        WayEnum way = ep.getWay();
        if (way != WayEnum.RESET_CARD_SKILL) {
            return;
        }
        Integer useSkillScrollTimes = ep.getUseSkillScrollTimes();
        if (useSkillScrollTimes >= 2) {
            BaseAchievementService service13850 = achievementServiceFactory.getById(13850);
            UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
            service13850.achieve(uid, 1, info, ep.getRd());
        }
    }

    //铜钱扣除事件
    @EventListener
    @Order(1000)
    public void deductCopper(CopperDeductEvent event) {
        EPCopperDeduct ep = event.getEP();
        WayEnum way = ep.getWay();
        long deductCopper = ep.getDeductCopper();
        if (WayEnum.LT == way) {
            if (deductCopper >= 10000 * 10000) {
                BaseAchievementService service13860 = achievementServiceFactory.getById(13860);
                UserAchievementInfo info = gameUserService.getSingleItem(ep.getGuId(), UserAchievementInfo.class);
                service13860.achieve(ep.getGuId(), 1, info, ep.getRd());
            }
        }
    }

    //战斗失败事件
    @EventListener
    @Order(1000)
    public void fightFailEvent(CombatFailEvent event) {
        EPFightEnd ep = (EPFightEnd) event.getSource();
        Long uid = ep.getGuId();
        int sid = gameUserService.getActiveSid(uid);
        List<UserTreasureEffect> effects = userTreasureEffectService.getAllEffects(uid);
        UserTreasureEffect treasureEffect = effects.stream().filter(e ->
                e.getBaseId().equals(TreasureEnum.LBJQ.getValue())).findFirst().orElse(null);
        if (null == treasureEffect) {
            return;
        }
        // 延迟3s，保证读取数据的时候，数据已经先正常存储到数据库中了
        long start = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - start >= 3000) {
                break;
            }
        }
        Date effectTime = treasureEffect.getEffectTime();
        Integer remainEffect = treasureEffect.getRemainEffect();
        List<InsGamePveDetailEntity> entityList = insGamePveDetailService.getDataForAchievement13880(uid, sid,
                effectTime);
        int loseTimes = 0;
        for (InsGamePveDetailEntity entity : entityList) {
            if (1 == entity.getIsWin()) {
                break;
            }
            loseTimes++;
        }
        if (3 == loseTimes) {
            BaseAchievementService service13880 = achievementServiceFactory.getById(13880);
            UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
            service13880.achieve(uid, loseTimes, info, ep.getRd());
        }
        if (remainEffect == 0) {
            this.gameUserService.deleteItem(treasureEffect);
        }
    }

    //法宝扣除事件
    @EventListener
    @Order(1000)
    public void treasureDeduct(TreasureDeductEvent event) {
        EPTreasureDeduct ep = event.getEP();
        EVTreasure deductTreasure = ep.getDeductTreasure();
        if (!effectTreasures.contains(deductTreasure.getId())){
            return;
        }
        Long uid = ep.getGuId();
        Optional<UserGod> optional = this.gameUserService.getMultiItems(uid, UserGod.class).
                stream().max(Comparator.comparing(UserGod::getAttachTime));
        if (deductTreasure.getId() == TreasureEnum.SSF.getValue()) {
            if (optional.isPresent()) {
                UserGod userGod = optional.get();
                GodEnum godEnum = GodEnum.fromValue(userGod.getBaseId());
                if (null != godEnum && godEnum.isBuffGod()) {
                    BaseAchievementService service13900 = achievementServiceFactory.getById(13900);
                    UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
                    service13900.achieve(uid, 1, info, ep.getRd());
                }
            }
        }
        List<UserTreasureEffect> treasureEffects = userTreasureEffectService.getAllEffects(uid);
        long count = treasureEffects.stream().filter(t ->
                statusTreasureList.contains(t.getBaseId()) && t.getRemainEffect() > 0).count();
        if (count >= 3 && optional.isPresent() && 1 == gameUserService.getGameUser(uid).getSetting().getActiveMbx()) {
            UserGod userGod = optional.get();
            if (userGod.ifEffect()) {
                BaseAchievementService service14640 = achievementServiceFactory.getById(14640);
                UserAchievementInfo info = gameUserService.getSingleItem(ep.getGuId(), UserAchievementInfo.class);
                service14640.achieve(ep.getGuId(), 1, info, ep.getRd());
            }
        }
    }

    //特产添加事件
    @EventListener
    @Order(1000)
    public void addSpecial(SpecialAddEvent event) {
        EPSpecialAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        List<EVSpecialAdd> addSpecials = ep.getAddSpecials();
        long uid = ep.getGuId();
        List<Integer> specialIds = addSpecials.stream().map(EVSpecialAdd::getSpecialId).collect(Collectors.toList());
        int position = this.gameUserService.getGameUser(uid).getLocation().getPosition();
        UserSpecialSaleRecord record = this.gameUserService.getSingleItem(uid, UserSpecialSaleRecord.class);
        if (record == null) {
            record = UserSpecialSaleRecord.instanceBoughtRecord(uid, specialIds,
                    CityTool.getCityId(position));
            this.gameUserService.addItem(uid, record);
        }
        if (way == WayEnum.TRADE || way == WayEnum.AUTO_BUY) {
            long lastBuyTime = record.getLastBuyTime();
            if (CityTool.getCityId(position).equals(record.getLastBuyCityId())) {
                if (DateUtil.getMinutesBetween(DateUtil.fromDateLong(lastBuyTime), DateUtil.now()) < 5) {
                    BaseAchievementService service13910 = achievementServiceFactory.getById(13910);
                    UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
                    int value = record.getLastBuySpecialList().size() + addSpecials.size();
                    service13910.achieve(uid, value, info, ep.getRd());
                }
            }
            // 特产交易记录可能别地方会用到，所以暂时不考虑成就完成时删除
            record.updateBuyRecord(CityTool.getCityId(position), specialIds);
            this.gameUserService.updateItem(record);
        }
    }

    //野地触发事件
    @EventListener
    @Order(1000)
    public void yeDTriggerEvent(YeDTriggerEvent event) {
        EventParam<EPYeDTrigger> ep = (EventParam<EPYeDTrigger>) event.getSource();
        Long uid = ep.getGuId();
        GameUser gu = this.gameUserService.getGameUser(uid);
        Integer specialLimit = this.userSpecialService.getSpecialLimit(gu);
        RDCommon rd = ep.getRd();
        EPYeDTrigger yeDTrigger = ep.getValue();
        List<Integer> goodsIds = yeDTrigger.getGoodsIds();
        YdEventEnum ydEventEnum = yeDTrigger.getEvent();
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        switch (ydEventEnum) {
            case TU_HAO:
                if (specialLimit.equals(goodsIds.size())) {
                    BaseAchievementService service13920 = achievementServiceFactory.getById(13920);
                    service13920.achieve(uid, 1, info, ep.getRd());
                }
                break;
            case XIAO_BAI:
                if (specialLimit.equals(goodsIds.size())) {
                    BaseAchievementService service13930 = achievementServiceFactory.getById(13930);
                    service13930.achieve(uid, 1, info, ep.getRd());
                }
                break;
            default:
                break;
        }
    }

    //抽卡事件，抽完卡触发
    @EventListener
    @Order(1000)
    @SuppressWarnings("unchecked")
    public void cardDraw(DrawEndEvent event) {
        EventParam<EPDraw> ep = (EventParam<EPDraw>) event.getSource();
        EPDraw epDraw = ep.getValue();
        long uid = ep.getGuId();
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        // 十连抽抽到4星以上卡牌超过3张
        List<Integer> addCardIds = epDraw.getAddCardIds();
        List<Integer> fourStarCards = addCardIds.stream().filter(ac ->
                CardTool.getCardById(ac).getStar() >= 4).collect(Collectors.toList());
        if (fourStarCards.size() >= 3) {
            BaseAchievementService service14180 = achievementServiceFactory.getById(14180);
            service14180.achieve(uid, 1, info, ep.getRd());
        }
        // 属性卡池判断
        Integer cardPoolType = epDraw.getCardPoolType();
        if (cardPoolType == CardPoolEnum.WANWU_CP.getValue()) {
            return;
        }
        // 更新成就进度
        Integer wishValue = epDraw.getWishValue();
        Integer wishCard = epDraw.getWishCard();
        BaseAchievementService service = null;
        if (wishValue <= 10 && addCardIds.contains(wishCard)) {
            service = achievementServiceFactory.getById(13970 + cardPoolType);
        } else if (wishValue >= 400 && addCardIds.contains(wishCard)) {
            service = achievementServiceFactory.getById(14020 + cardPoolType);
        }
        if (null != service) {
            service.achieve(uid, 1, info, ep.getRd());
        }
    }

    //押押乐头奖事件
    @EventListener
    @Order(1000)
    public void yaYaLe(YaYaLeWinEvent event) {
        EPYaYaLeWin ep = event.getEP();
        Long uid = ep.getGuId();
        GameUser gu = this.gameUserService.getGameUser(uid);
        Date yesterday = DateUtil.addDays(DateUtil.now(), -1);
        List<FlxYaYaLeBet> betList = flxService.getYaYaLeBetResult(uid, gu.getServerId(),
                DateUtil.toDateInt(yesterday));
        if (1 == betList.size()) {
            BaseAchievementService service14100 = achievementServiceFactory.getById(14100);
            UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
            service14100.achieve(uid, 1, info, ep.getRd());
        }
    }

    //猜数字事件
    @EventListener
    @Order(1000)
    public void caiShuZi(CaiShuZiWinEvent event) {
        EPCaiShuZiWin ep = event.getEP();
        Long uid = ep.getGuId();
        GameUser gu = this.gameUserService.getGameUser(uid);
        Date yesterday = DateUtil.addDays(DateUtil.now(), -1);
        List<FlxCaiShuZiBet> betList = flxService.getCaiShuZiBetResult(uid, gu.getServerId(),
                DateUtil.toDateInt(yesterday));
        if (1 == betList.size()) {
            BaseAchievementService service14090 = achievementServiceFactory.getById(14090);
            UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
            service14090.achieve(uid, 1, info, ep.getRd());
        }
    }

    //特产扣除事件
    @EventListener
    @Order(1000)
    public void deductSpecial(SpecialDeductEvent event) {
        EPSpecialDeduct ep = event.getEP();
        WayEnum way = ep.getWay();
        if (way != WayEnum.TRADE) {
            return;
        }
        List<EPSpecialDeduct.SpecialInfo> specialInfoList = ep.getSpecialInfoList();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        int position = ep.getPos();
        CfgCityEntity cfgCityEntity = CityTool.getCities().stream().filter(c ->
                c.getAddress1() == position || c.getAddress2() == position).findFirst().orElse(null);
        if (cfgCityEntity != null && cfgCityEntity.isCC()) {
            String specials = cfgCityEntity.getSpecials();
            String[] specialArray = specials.split(",");
            List<Integer> specialIds =
                    specialInfoList.stream().map(EPSpecialDeduct.SpecialInfo::getBaseSpecialIds).collect(Collectors.toList());
            List<Integer> collect = Arrays.stream(specialArray).map(Integer::parseInt).collect(Collectors.toList());
            // 判断卖出的特产在该城池是否可以产出
            if (!Collections.disjoint(collect, specialIds)) {
                BaseAchievementService service14120 = achievementServiceFactory.getById(14120);
                UserAchievementInfo info = gameUserService.getSingleItem(gu.getId(), UserAchievementInfo.class);
                service14120.achieve(gu.getId(), 1, info, ep.getRd());
            }
        }
    }

    //丢骰子事件
    @EventListener
    @Order(1000)
    public void shakeDice(ShakeEvent event) {
        EPShake ep = event.getEP();
        List<Integer> shakeList = ep.getShakeList();
        int shakeSum = ListUtil.sumInt(shakeList);
        if (shakeSum == 18) {
            BaseAchievementService service14130 = achievementServiceFactory.getById(14130);
            UserAchievementInfo info = gameUserService.getSingleItem(ep.getGuId(), UserAchievementInfo.class);
            service14130.achieve(ep.getGuId(), 1, info, ep.getRd());
        }
    }

    //战斗胜利事件
    @EventListener
    @Order(1000)
    public void fightWin(CombatFightWinEvent event) {
        EPFightEnd ep = (EPFightEnd) event.getSource();
        FightTypeEnum fightType = ep.getFightType();
        FightSubmitParam fightSubmit = ep.getFightSubmit();
        Integer round = fightSubmit.getRound();
        long uid = ep.getGuId();
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        if (null != round && round >= 20) {
            BaseAchievementService service14170 = achievementServiceFactory.getById(14170);
            service14170.achieve(uid, 1, info, ep.getRd());
        }
        if (fightType == FightTypeEnum.ATTACK || fightType == FightTypeEnum.TRAINING) {
            UserTreasureRecord record = userTreasureRecordService.getUserTreasureRecord(uid, TreasureEnum.XJZ.getValue());
            if (null != record && record.getUseTimes() >= 3) {
                BaseAchievementService service14160 = achievementServiceFactory.getById(14160);
                service14160.achieve(uid, 1, info, ep.getRd());
            }
        }
    }

    //战斗失败事件
    @EventListener
    @Order(1000)
    public void fightFail(CombatFailEvent event) {
        EPFightEnd ep = (EPFightEnd) event.getSource();
        FightSubmitParam fightSubmit = ep.getFightSubmit();
        Integer round = fightSubmit.getRound();
        long uid = ep.getGuId();
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        if (null != round && round >= 20) {
            BaseAchievementService service14170 = achievementServiceFactory.getById(14170);
            service14170.achieve(uid, 1, info, ep.getRd());
        }
    }

    //铜钱添加事件
    @EventListener
    @Order(1000)
    public void addCopper(CopperAddEvent event) {
        EPCopperAdd ep = event.getEP();
        Long uid = ep.getGuId();
        GameUser gu = this.gameUserService.getGameUser(uid);
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        BaseAchievementService service14190 = achievementServiceFactory.getById(14190);
        service14190.achieve(uid, gu.getCopper(), info, ep.getRd());
        List<ResAddInfo> addCoppers = ep.getAddCoppers();
        long addCopperSum = addCoppers.stream().mapToLong(ResAddInfo::getValue).sum();
        switch (ep.getWay()) {
            case FIGHT_YG:
            case FIGHT_HELP_YG:
            case FIGHT_ATTACK:
            case FIGHT_TRAINING:
            case FIGHT_PROMOTE:
            case FIGHT_FST:
            case FIGHT_ZXZ:
            case FIGHT_MAOU:
            case FS_FIGHT:
            case SXDH_FIGHT:
            case CHANJIE_FIGHT:
                BaseAchievementService service14230 = achievementServiceFactory.getById(14230);
                service14230.achieve(uid, addCopperSum, info, ep.getRd());
                break;
            default:
                break;
        }
    }

    //魔王发送奖励事件
    @EventListener
    @Order(1000)
    public void maouAwardSend(BossMaouAwardSendEvent event) {
        EPBossMaou ep = event.getEP();
        ServerBossMaou bossMaou = ep.getBossMaou();
        List<BossMaouAttackSummary> ranker = bossMaouAttackSummaryService.getAttackInfoSorted(bossMaou);
        if (ranker.size() >= 2) {
            BossMaouAttackSummary first = ranker.get(0);
            BossMaouAttackSummary second = ranker.get(1);
            int blood = first.getBeatedBlood() - second.getBeatedBlood();
            if (blood == 1) {
                BaseAchievementService service14220 = achievementServiceFactory.getById(14220);
                UserAchievementInfo info = gameUserService.getSingleItem(second.getGuId(), UserAchievementInfo.class);
                service14220.achieve(second.getGuId(), 1, info, ep.getRd());
            }
            if (blood <= 10) {
                BaseAchievementService service14210 = achievementServiceFactory.getById(14210);
                UserAchievementInfo info = gameUserService.getSingleItem(first.getGuId(), UserAchievementInfo.class);
                service14210.achieve(first.getGuId(), 1, info, ep.getRd());
            }
        }

    }

    //魔王被击杀事件
    @EventListener
    @Order(1000)
    public void killMaou(BossMaouKilledEvent event) {
        EPBossMaou ep = event.getEP();
        ServerBossMaou bossMaou = ep.getBossMaou();
        long uid = ep.getGuId();
        List<BossMaouAttackSummary> ranker = bossMaouAttackSummaryService.getAttackInfoSorted(bossMaou);
        BossMaouAttackSummary killSummary = ranker.stream().filter(r ->
                r.getGuId().equals(ep.getGuId())).findFirst().orElse(null);
        if (killSummary != null && killSummary.getAttackTimes() == 1) {
            BaseAchievementService service14200 = achievementServiceFactory.getById(14200);
            UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
            service14200.achieve(uid, 1, info, ep.getRd());
        }
    }

    //阐截斗法四胜事件
    @EventListener
    @Order(1000)
    public void chanJie(ChanjieLDFSFourWinEvent event) {
        EPChanjieLDFSFourWin ep = event.getEP();
        if (!ep.isFirstPlayer()) {
            BaseAchievementService service14140 = achievementServiceFactory.getById(14140);
            UserAchievementInfo info = gameUserService.getSingleItem(ep.getGuId(), UserAchievementInfo.class);
            service14140.achieve(ep.getGuId(), 1, info, ep.getRd());
        }
    }

    //开启野怪宝箱事件
    @EventListener
    @Order(1000)
    public void openYeGuaiBox(OpenYeGuaiBoxEvent event) {
        EPOpenYeGuaiBox ep = event.getEP();
        YeGuaiEnum yeGuaiEnum = ep.getYeGuaiEnum();
        if (YeGuaiEnum.YG_ELITE == yeGuaiEnum) {
            RDYeGuaiEliteBox boxCache = TimeLimitCacheUtil.getYeGuaiBoxCache(ep.getGuId());
            List<Award> boxAwards = boxCache.getBoxAwards();
            if (boxAwards.size() >= 3) {
                UserAchievementInfo info = gameUserService.getSingleItem(ep.getGuId(), UserAchievementInfo.class);
                List<Award> awards = boxAwards.subList(boxAwards.size() - 3, boxAwards.size());
                if (awards.get(0).getItem() == awards.get(1).getItem() && awards.get(0).getItem() == awards.get(2).getItem()) {
                    if (awards.get(0).getItem() == AwardEnum.TQ.getValue() || awards.get(0).getItem() == AwardEnum.TL.getValue()) {
                        BaseAchievementService service14630 = achievementServiceFactory.getById(14630);
                        service14630.achieve(ep.getGuId(), 1, info, ep.getRd());
                    }
                } else if (awards.get(0).getAwardId().equals(awards.get(1).getAwardId()) && awards.get(0).getAwardId().equals(awards.get(2).getAwardId())) {
                    BaseAchievementService service14630 = achievementServiceFactory.getById(14630);
                    service14630.achieve(ep.getGuId(), 1, info, ep.getRd());
                }
            }
        }
    }

    //战斗相关的成就
    @EventListener
    @Order(1000)
    public void combatAchievement(CombatAchievementEvent event) {
        EPCombatAchievement ep = event.getEP();
        Long uid = ep.getGuId();
        List<Integer> achievementIds = ep.getFinished();
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        for (Integer id : achievementIds) {
            BaseAchievementService service = achievementServiceFactory.getById(id);
            service.achieve(uid, 1, info, ep.getRd());
        }
    }

    //法宝过期事件
    @EventListener
    @Order(1000)
    public void treasureExpired(TreasureExpiredEvent event) {
        EPTreasureExpired ep = event.getEP();
        Integer treasureId = ep.getTreasureId();
        long expiredNum = ep.getExpiredNum();
        if (TreasureEnum.XIAN_DOU.getValue() == treasureId && expiredNum >= 10000) {
            BaseAchievementService service14580 = achievementServiceFactory.getById(14580);
            UserAchievementInfo info = gameUserService.getSingleItem(ep.getGuId(), UserAchievementInfo.class);
            service14580.achieve(ep.getGuId(), 1, info, ep.getRd());
        }
    }

    /**
     * 邮件标题在ExchangeCodeServiceImpl中定义，兑换礼包事件
     *
     * @param event
     * @see com.bbw.god.uac.service.impl.ExchangeCodeServiceImpl
     */
    @EventListener
    @Order(1000)
    public void payAttentionWeChatPublic(ExchangeCodeEvent event) {
        EPExchangeCode ep = event.getEP();
        long uid = ep.getGuId();
        BaseAchievementService service13290 = achievementServiceFactory.getById(13290);
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        service13290.achieve(uid, 1, info, ep.getRd());
    }

    //夺宝抽奖事件
    @EventListener
    @Order(1000)
    public void drawSnatchTreasure(SnatchTreasureDrawEvent event) {
        EPSnatchTreasureDraw ep = event.getEP();
        Long uid = ep.getGuId();
        UserSnatchTreasure userSnatchTreasure = gameUserService.getSingleItem(uid, UserSnatchTreasure.class);
        if (null == userSnatchTreasure) {
            return;
        }
        Integer lastWishValue = userSnatchTreasure.getLastWishValue();
        if (600 == lastWishValue) {
            BaseAchievementService service14950 = achievementServiceFactory.getById(14950);
            UserAchievementInfo info = gameUserService.getSingleItem(ep.getGuId(), UserAchievementInfo.class);
            service14950.achieve(ep.getGuId(), 1, info, ep.getRd());
        }
    }

    /**
     * 捏人事件监听
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void pinchPeopleEventEvent(PinchPeopleEvent event) {
        EPPinchPeople ep = event.getEP();
        long uid = ep.getGuId();
        List<Integer> soilScore = ep.getSoilScore();
        //是否用完每日有效捏人次数
        if (soilScore.size() < NightmareNvWamCfgTool.getDayPinchPeopleValidTimes()) {
            return;
        }
        //计算泥人平均分
        Double averageScore = soilScore.subList(soilScore.size() - NightmareNvWamCfgTool.getDayPinchPeopleValidTimes(), soilScore.size())
                .stream().mapToDouble(Integer::doubleValue).average().orElse(0);
        //获得用户成就信息
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        //心灵手巧成就
        BaseAchievementService service17390 = achievementServiceFactory.getById(17390);
        //成就需要的平均分
        Double achievementNeedAverageScore17390 = Double.valueOf(service17390.getMyNeedValue());
        if (achievementNeedAverageScore17390.equals(averageScore)) {
            service17390.achieve(uid, service17390.getMyNeedValue(), info, ep.getRd());
        }
        //笨手笨脚成就
        BaseAchievementService service17400 = achievementServiceFactory.getById(17400);
        //成就需要的平均分
        Double achievementNeedAverageScore17400 = Double.valueOf(service17400.getMyNeedValue());
        if (achievementNeedAverageScore17400.equals(averageScore)) {
            service17400.achieve(uid, service17390.getMyNeedValue(), info, ep.getRd());
        }
    }

    /**
     * 诛仙阵扫荡分
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void zhiBaoAdd(ZhiBaoEvent event) {
        try {
            EPCardZhiBaoAdd ep = event.getEP();
            long uid = ep.getGuId();
            UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
            // 顶级法器
            if (0 != ep.getFullAttackNum()) {
                BaseAchievementService service17870 = achievementServiceFactory.getById(17870);
                service17870.achieve(uid, ep.getFullAttackNum(), info, ep.getRd());
            }

            // 顶级灵宝
            if (0 != ep.getFullDefenseNum()) {
                BaseAchievementService service17880 = achievementServiceFactory.getById(17880);
                service17880.achieve(uid, service17880.getMyProgress(uid, info), info, ep.getRd());
            }


        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
