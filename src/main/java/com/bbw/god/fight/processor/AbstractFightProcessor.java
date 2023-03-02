package com.bbw.god.fight.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.RDArriveChengC;
import com.bbw.god.fight.*;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.event.CombatEventPublisher;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.guide.v3.NewerGuideEnum;
import com.bbw.god.gameuser.leadercard.fashion.UserLeaderFashionService;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.treasure.UserTreasureEffect;
import com.bbw.god.gameuser.treasure.UserTreasureEffectService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.god.GodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 战斗处理.继承这个类的必须为多态
 *
 * @author suhq
 * @date 2019年2月18日 下午4:02:25
 */
@Slf4j
public abstract class AbstractFightProcessor implements IFightProcessor {
    @Autowired
    protected GameUserService gameUserService;
    @Autowired
    protected PrivilegeService privilegeService;
    @Autowired
    protected GodService godService;
    @Autowired
    protected UserTreasureEffectService userTreasureEffectService;
    @Autowired
    protected UserCityService userCityService;
    @Autowired
    protected UserLeaderFashionService userLeaderFashionService;

    /**
     * 获取当前战斗类型
     *
     * @return
     */
    public abstract FightTypeEnum getFightType();

    @Override
    public boolean isMatch(FightTypeEnum fightTypeEnum) {
        return fightTypeEnum.equals(getFightType());
    }

    /**
     * 处理地图上的战斗，子类不可覆盖或重写该方法
     *
     * @param guId
     * @param param
     * @return
     */
    @Override
    public final RDFightResult submitFightResult(long guId, FightSubmitParam param) {
        GameUser gu = this.gameUserService.getGameUser(guId);
        RDFightResult rd = new RDFightResult();
        // 本次战斗是否已结算
        if (TimeLimitCacheUtil.getFightResultCache(guId) != null) {
            throw new ExceptionForClientTip("fight.already.settle");
        }
        // 将本次战斗置为已结算
        TimeLimitCacheUtil.setFightResultCache(guId, rd);
        // 初始战斗数据
        this.settleBefore(gu, param);
        rd.setWin(param.getWin());
        rd.setFightType(param.getFightType());
        if (FightTypeEnum.ATTACK.getValue() == param.getFightType() && param.getWin() == 1) {
            RDArriveChengC rdArriveChengC = TimeLimitCacheUtil.getChengCCache(gu.getId());
            rdArriveChengC.setToAttack(1);
            TimeLimitCacheUtil.setArriveCache(guId, rdArriveChengC);
        }

        rd.setYeGType(param.getYeGuaiType());
        EPFightEnd ev = EPFightEnd.instance(guId, gu.getLocation().getPosition(), getFightType(), param.getWin() == 1, param, rd);
        // 失败判定与失败处理
        if (param.getWin() == 0) {
            this.failure(gu, rd, param);
            // 失败事件
            CombatEventPublisher.pubFailEvent(ev);
            return rd;
        }

        // 胜利事件
        CombatEventPublisher.pubWinEvent(ev);
        // 战斗结算
        this.handleAward(gu, rd, param);
        return rd;
    }

    /**
     * 获得铜钱加成比
     *
     * @param zcNum
     * @param godNum
     * @return
     */
    protected final double getCopperAddRate(int zcNum, int godNum) {
        int copperAddRate = 0;
        // 招财技能
        copperAddRate += CardTool.getConfig().getZcCopperAddRate() * zcNum;
        copperAddRate += godNum;
        return copperAddRate * 10 / 1000.0;
    }

    /**
     * 基础铜钱加成
     *
     * @param gu
     * @return
     */
    public final double getBaseCopperBuf(GameUser gu) {
        double nightmareCopperBuff = userCityService.addNightmareCopperBuff(gu);
        //金风飒飒时装加成
        double fashionCopperBuff = userLeaderFashionService.getFashionCopperBuff(gu);
        double buff = nightmareCopperBuff + fashionCopperBuff;
        return buff;
    }

    /**
     * 基础经验加成
     *
     * @param gu
     * @param caiShenExpRate 10表示0.1
     * @return
     */
    protected final double getBaseExpBuf(GameUser gu, int caiShenExpRate) {
        double nightmareExpBuff = userCityService.addNightmareExpBuff(gu);
        return nightmareExpBuff + caiShenExpRate / 100.0;
    }

    /**
     * 基础经验
     *
     * @param gu
     */
    protected final int getExp(GameUser gu, long oppLostBlood, FightSubmitParam param) {
        if (NewerGuideEnum.YOU_SHANG_GUAN.getStep().equals(param.getNewerGuide())) {
            return 600;
        }
        int gainExp = FightResultUtil.getFightExp(oppLostBlood, param.getOppKilledCards());// 经验
        double rate = this.privilegeService.getExtraFightExpRate(gu.getId());
        return (int) (gainExp * (1 + rate));
    }

    @Override
    public void settleBefore(GameUser gu, FightSubmitParam param) {
        return;
    }

    @Override
    public int getGodCopperRate(GameUser gu) {
        return godService.getCopperAddRate(gu);
    }

    /**
     * 对手信息转换
     *
     * @param fightsInfo
     * @return
     */
    protected CombatPVEParam toCombatPVEParam(RDFightsInfo fightsInfo) {
        CombatPVEParam param = new CombatPVEParam();
        CPlayerInitParam aiPlayer = CPlayerInitParam.initParam(fightsInfo.getLevel(), fightsInfo.getNickname(), fightsInfo.getHead(), fightsInfo.getHeadIcon());
        List<CCardParam> cardParams = new ArrayList<>();
        for (RDFightsInfo.RDFightCard infoCard : fightsInfo.getCards()) {
            cardParams.add(CCardParam.init(infoCard.getBaseId(), infoCard.getLevel(), infoCard.getHierarchy(), infoCard.getStrengthenInfo()));
        }
        aiPlayer.setCards(cardParams);
        if (null != fightsInfo.getCityBuff()) {
            aiPlayer.addBuff(fightsInfo.getCityBuff());
        }
        aiPlayer.setHp(fightsInfo.getBlood());
        param.setAiPlayer(aiPlayer);
        return param;
    }

    /**
     * 最大不超过20级
     * 解析卡牌字符串
     *
     * @param soliderString #守军*,* -> 卡牌ID,等级 eg: 101,0 -> 姜子牙0级  #守军*,*,* -> 属性,星级,等级 eg: 20,2,0 -> 木属性二星0级卡牌
     * @return
     */
    public List<CCardParam> getCardsBySoliderString(String soliderString, int difficulty, int addHv, List<Integer> extraExcludes) {
        List<CCardParam> cards = new ArrayList<>();
        List<Integer> cardIds = new ArrayList<>(); // 仅用来避免重复卡牌
        if (ListUtil.isNotEmpty(extraExcludes)) {
            cardIds.addAll(extraExcludes);
        }
        String[] cardStrArray = soliderString.split(";");
        for (String cardSrc : cardStrArray) {
            String[] cardParts = cardSrc.split(",");
            int cardId = 0;
            int lv = 0;
            List<Integer> skills = new ArrayList<>();
            switch (cardParts.length) {
                case 1:
                    //具体卡
                    cardId = Integer.valueOf(cardParts[0]);
                    if (cardIds.contains(cardId)) {
                        continue;
                    }
                    break;
                case 2:
                    //具体卡 + 等级
                    cardId = Integer.valueOf(cardParts[0]);
                    lv = Integer.valueOf(cardParts[1]);
                    if (cardIds.contains(cardId)) {
                        continue;
                    }
                    break;
                case 3:
                    //随机卡
                    int cardType = Integer.valueOf(cardParts[0]);
                    int cardStar = Integer.valueOf(cardParts[1]);
                    lv = Integer.valueOf(cardParts[2]);
                    CfgCardEntity randomCard = CardTool.getRandomCardByTypeStar(TypeEnum.fromValue(cardType), cardStar, cardIds);
                    cardId = randomCard.getId();
                    break;
                case 4:
                    //具体卡+卡技能
                    cardId = Integer.valueOf(cardParts[0]);
                    if (cardIds.contains(cardId)) {
                        continue;
                    }
                    skills.add(Integer.valueOf(cardParts[1]));
                    skills.add(Integer.valueOf(cardParts[2]));
                    skills.add(Integer.valueOf(cardParts[3]));
                    break;
                default:
                    continue;
            }
            cardIds.add(cardId);
            CCardParam cardParam = CCardParam.init(cardId, Math.min(lv + difficulty, 20), addHv);
            if (ListUtil.isNotEmpty(skills)) {
                cardParam.setSkills(skills);
                cardParam.setIsUseSkillScroll(1);
            }
            cards.add(cardParam);
        }
        return cards;
    }

    /**
     * 获得经验丹
     *
     * @param uid
     * @param param
     * @param exp
     * @param rd
     */
    public void gainJinYanDan(long uid, FightSubmitParam param, int exp, RDCommon rd) {
        if (!param.isGainJYD()) {
            return;
        }
        //判断是否双倍经验丹是否生效
        UserTreasureEffect utEffect = userTreasureEffectService.getEffect(uid, TreasureEnum.DOUBLE_EXPERIENCE_MEDICINE.getValue());
        if (utEffect != null) {
            //计算已经使用的秒数
            long usedSecond = DateUtil.getSecondsBetween(utEffect.getEffectTime(), DateUtil.now());
            if (usedSecond <= utEffect.getRemainEffect()) {
                exp *= 2;
            }
        }
        int num = exp / 4000;
        if (num > 0) {
            List<Award> extraAward = userLeaderFashionService.getExtraAward(uid, TreasureEnum.FASHION_ShiJNH);
            if (ListUtil.isNotEmpty(extraAward)) {
                num += extraAward.get(0).getNum();
            }
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.FEN_SHEN_JYD.getValue(), num, getWay(), rd);
        }
    }
}
