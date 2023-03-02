package com.bbw.god.fight.processor;

import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.holiday.processor.holidayspcialyeguai.AbstractSpecialYeGuaiProcessor;
import com.bbw.god.activity.holiday.processor.holidayspcialyeguai.HolidaySpecialYeGuaiFactory;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.yeg.RDArriveYeG;
import com.bbw.god.city.yeg.UserYeGEliteService;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.*;
import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.event.CombatEventPublisher;
import com.bbw.god.game.combat.event.EPEliteYeGuaiFightWin;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.businessgang.BusinessGangService;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.server.ServerService;
import com.bbw.god.server.monster.MonsterService;
import com.bbw.god.server.monster.ServerMonster;
import com.bbw.god.server.monster.event.EPFriendMonsterAdd;
import com.bbw.god.server.monster.event.MonsterEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 打野战斗结算
 *
 * @author suhq
 * @date 2018年11月15日 上午9:22:40
 */
@Service
public class YeGFightProcessor extends AbstractFightProcessor {
    @Autowired
    private MonsterService monsterService;
    @Autowired
    private NewerGuideService newerGuideService;
    @Autowired
    private UserYeGEliteService userYeGEliteService;
    @Autowired
    private ServerService serverService;
    @Autowired
    private HolidaySpecialYeGuaiFactory holidaySpecialYeGuaiFactory;
    @Autowired
    private BusinessGangService businessGangService;
    @Override
    public FightTypeEnum getFightType() {
        return FightTypeEnum.YG;
    }

    @Override
    public WayEnum getWay() {
        return  WayEnum.FIGHT_YG;
    }

    @Override
    public void handleAward(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        // 免费开箱子次数
        int freeBoxTimes = getFreeBoxTimes(gu, param.isFinishedTask());
        rd.setAwardTimes(freeBoxTimes);
        int gainExp = getExp(gu, param.getOppLostBlood(), param);// 经验
        if (!this.newerGuideService.isPassNewerGuide(gu.getId())) {
            gainExp /= 3;
        } else {
            gainExp /= 4;
        }
        gainExp *= (1 + getBaseExpBuf(gu,param.getAdditionExp()));
        // 铜钱加成
        int godNum = this.godService.getCopperAddRate(gu);
        double copperAddRate = getCopperAddRate(param.getZcTimes(), godNum);
        // 处理战斗经验
        gainJinYanDan(gu.getId(), param, gainExp, rd);
        ResEventPublisher.pubExpAddEvent(gu.getId(), gainExp, getWay(), rd);
        // 落宝金钱发放道具
        this.userTreasureEffectService.effectAsLBJQ(gu, rd);
        // 重新战斗将怪物置为打胜
        if (param.isFightAgain()) {
            ServerMonster monster = this.monsterService.getLastMonster(gu.getId(), gu.getServerId());
            if (null != monster && !monster.getBeDefeated()) {
                monster.setBeDefeated(true);
                serverService.deleteServerData(monster);
            }
        }

        int oppLevel = param.getOppLv(); // 对手等级
        RDArriveYeG arriveCache = TimeLimitCacheUtil.getArriveCache(gu.getId(), RDArriveYeG.class);
        if (arriveCache.getCountry() != null && rd.getYeGType() != null
                && rd.getYeGType().equals(YeGuaiEnum.YG_ELITE)) {
            if (arriveCache.getYgAttribute() == null) {
                arriveCache.setYgAttribute(arriveCache.getCountry());
            }
            userYeGEliteService.updateYeGLevel(arriveCache.getYgAttribute(), gu.getId());
        }
        boolean specialYeGBox = businessGangService.isSpecialYeGBox(gu.getId());
        rd.setBusinessGang(specialYeGBox);
        // 缓存战斗必要信息，用于开箱子
        RDFightEndInfo rdFightEndInfo = new RDFightEndInfo(gu.gainCurCity().getType() - 100, oppLevel,
                freeBoxTimes, copperAddRate, 3, rd.getYeGType(), param.getOpponentName(), new ArrayList<>(), specialYeGBox);
        TimeLimitCacheUtil.setFightEndCache(gu.getId(), rdFightEndInfo);
        arriveCache.setHasWin(true);
        TimeLimitCacheUtil.setArriveCache(gu.getId(), arriveCache);
        if (YeGuaiEnum.YG_ELITE == rd.getYeGType()) {
            Integer type = arriveCache.getYgAttribute();
            List<RDFightsInfo.RDFightCard> cards = arriveCache.getCards();
            Integer cardLevel = cards.get(0).getLevel();
            Integer hierarchy = cards.get(0).getHierarchy();
            BaseEventParam bep = new BaseEventParam(gu.getId(), WayEnum.FIGHT_YG, rd);
            CombatEventPublisher.pubEliteYeGuaiFightWinEvent(new EPEliteYeGuaiFightWin(type, cardLevel, hierarchy, bep));
        }
        //活动特殊野怪
        AbstractSpecialYeGuaiProcessor specialYeGuaiProcessor = holidaySpecialYeGuaiFactory.getSpecialYeGuaiProcessor(gu.getId());
        if (null != specialYeGuaiProcessor){
            specialYeGuaiProcessor.removeYouHun(gu.getId(), arriveCache.getArriveCityId());
        }
    }


    @Override
    public void failure(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        int oppLostBlood = param.getOppLostBlood(); // 对手损失血量
        int oppLevel = param.getOppLv();
        int oppBlood = FightResultUtil.getBloodByLevel(oppLevel);
        if (param.isFightAgain()) {
            // 更新怪物血量
            int remainBlood = oppBlood - oppLostBlood;
            ServerMonster monster = this.monsterService.getLastMonster(gu.getId(), gu.getServerId());
            if (monster != null) {
                monster.setBlood(remainBlood);
                this.monsterService.updateMonster(monster);
            }

        } else {
            RDArriveYeG cache = TimeLimitCacheUtil.getArriveCache(gu.getId(), RDArriveYeG.class);
            int property = cache.getYgAttribute();
            int remainBlood = oppBlood - oppLostBlood;
            ServerMonster sMonster = ServerMonster.fromGu(gu,build(cache), remainBlood, property);
            sMonster.setYeGuaiEnum(rd.getYeGType());
            sMonster.setJoinYouGuai(false);
            this.monsterService.addMonsterToList(gu.getServerId(), sMonster);
        }
        this.userTreasureEffectService.effectAsLBJQ(gu, rd);
    }

    @Override
    public CombatPVEParam getOpponentInfo(Long uid, Long oppId, boolean fightAgain) {
        RDArriveYeG cache = TimeLimitCacheUtil.getArriveCache(uid, RDArriveYeG.class);
        if (cache.getHasWin()) {
            throw new ExceptionForClientTip("fight.cant.repeat");
        }
        List<CCardParam> cardParams = new ArrayList<>();
        for (RDFightsInfo.RDFightCard card : cache.getCards()) {
            cardParams.add(CCardParam.init(card.getBaseId(), card.getLevel(), card.getHierarchy(), card.getStrengthenInfo()));
        }
        CPlayerInitParam ai = new CPlayerInitParam();
        ai.setHeadImg(cache.getHead());
        ai.setHeadIcon(cache.getHeadIcon());
        if (StrUtil.isNotBlank(cache.getNickname())) {
            ai.setNickname(cache.getNickname());
        } else {
            ai.setNickname(CardTool.getCardById(cardParams.get(0).getId()).getName());
        }
        ai.setLv(cache.getLevel());
        ai.setCards(cardParams);
        if (null != cache.getBuff()) {
            ai.addBuff(cache.getBuff());
        }
        CombatPVEParam pveParam = new CombatPVEParam();
        pveParam.setCityBaseId(cache.getArriveCityId());
        pveParam.setYgType(cache.getYeGuaiType());
        pveParam.setAwardkey(cache.getRandomGoal());
        pveParam.setAiPlayer(ai);
        return pveParam;
    }

    private RDFightsInfo build(RDArriveYeG arriveCache){
        RDFightsInfo info = new RDFightsInfo(arriveCache.getLevel(), arriveCache.getCards(), arriveCache.getRandomGoal(), arriveCache.getYeGuaiType());
        if (arriveCache.getNickname() != null) {
            info.setNickname(arriveCache.getNickname());
        }
        info.setCityBaseId(arriveCache.getArriveCityId());
        info.setHeadIcon(arriveCache.getHeadIcon());
        info.setHead(arriveCache.getHead());
        info.setCityBuff(arriveCache.getBuff());
        return info;
    }

    /**
     * 将玩家不想战斗 即选择逃跑不战的野怪加入到友怪之中
     */
    public void takeYeGtoYouG(long uid) {
        RDArriveYeG arriveCache = TimeLimitCacheUtil.getArriveCache(uid, RDArriveYeG.class);
        GameUser gu = this.gameUserService.getGameUser(uid);
        ServerMonster monster =null;
        if (TimeLimitCacheUtil.getFightResultCache(uid) == null){
            //没有发生战斗 直接选择 求助好友的
            RDFightsInfo info=build(arriveCache);
            int remainBlood= CombatInitService.getPlayerInitHp(info.getLevel());
            int type=CityTool.getCityById(arriveCache.getArriveCityId()).getType()-100;
            monster = ServerMonster.fromGu(gu, info, remainBlood, type);
            monster.setYeGuaiEnum(YeGuaiEnum.fromValue(info.getYgType()));
            monster.setJoinYouGuai(true);
            monsterService.addMonsterToList(gu.getServerId(),monster);
        }else {
            monster = this.monsterService.getLastMonster(gu.getId(), gu.getServerId());
        }
        if (monster != null) {
            if (monster.getYeGuaiEnum() == null) {
                monster.setYeGuaiEnum(YeGuaiEnum.YG_FRIEND);
            } else if (monster.getYeGuaiEnum().equals(YeGuaiEnum.YG_NORMAL)) {
                monster.setYeGuaiEnum(YeGuaiEnum.YG_FRIEND);
            } else {
                monster.setYeGuaiEnum(YeGuaiEnum.YG_ELITE_FRIEND);
            }
            monster.setJoinYouGuai(true);
            this.monsterService.updateMonster(monster);
            MonsterEventPublisher.pubFriendMonsterAddEvent(EPFriendMonsterAdd.instance(uid, WayEnum.FIGHT_YEGUAI_RUN,
                    monster));
        }
        //活动特殊野怪
        AbstractSpecialYeGuaiProcessor specialYeGuaiProcessor = holidaySpecialYeGuaiFactory.getSpecialYeGuaiProcessor(gu.getId());
        if (null != specialYeGuaiProcessor){
            specialYeGuaiProcessor.removeYouHun(gu.getId(), arriveCache.getArriveCityId());
        }

    }

    /**
     * 获得免费箱子的个数
     *
     * @param isFinishGoal
     * @return
     */
    private int getFreeBoxTimes(GameUser gu, boolean isFinishGoal) {
        int freeTime = 1;
        if (isFinishGoal) {
            freeTime++;
        }
        freeTime += this.privilegeService.getExtraFightBoxFreeTimes(gu);
        return freeTime;
    }
}
