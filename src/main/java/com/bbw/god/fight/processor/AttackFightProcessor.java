package com.bbw.god.fight.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.difficulty.UserAttackDifficulty;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.city.event.EPCityAdd;
import com.bbw.god.city.event.EPPassCityLevel;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardDeifyCardParam;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCard;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.ChengC;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.ResWayType;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 攻城练兵结算
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AttackFightProcessor extends AbstractCityFightProcessor {

    private static final int[] cities = {4325, 1024, 2608, 2539, 2725};
    private static final int[] treasures = {
            TreasureEnum.SHEN_WU_BAI_HU.getValue(), TreasureEnum.SHEN_WU_QING_LONG.getValue(),
            TreasureEnum.SHEN_WU_ZHU_QUE.getValue(), TreasureEnum.SHEN_WU_XUAN_WU.getValue(),
            TreasureEnum.SHEN_WU_QI_LIN.getValue()};
    @Autowired
    private UserCardService userCardService;

    @Override
    public WayEnum getWay() {
        return WayEnum.FIGHT_ATTACK;
    }


    @Override
    public FightTypeEnum getFightType() {
        return FightTypeEnum.ATTACK;
    }

    @Override
    public void checkAbleFight(GameUser gu,ChengChiInfoCache cache) {
        if (!gu.getStatus().intoNightmareWord() && cache.isAttack()){
            throw new ExceptionForClientTip("city.not.attack.times");
        }
    }

    @Override
    public CombatPVEParam getOpponentInfo(Long uid, Long oppId, boolean fightAgain) {
        return super.getOpponentInfo(uid, oppId, fightAgain);
    }

    @Override
    public void handleAward(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        ChengChiInfoCache chengChiInfoCache = TimeLimitCacheUtil.getChengChiInfoCache(gu.getId());
        //结算经验
        int gainExp = getExp(gu, param.getOppLostBlood(), param) * 8 / 10;
        int baseCopper = gainExp;
        baseCopper *= (1 + getBaseCopperBuf(gu));
        gainExp *= (1 + getBaseExpBuf(gu,param.getAdditionExp()));
        gainJinYanDan(gu.getId(), param, gainExp, rd);
        ResEventPublisher.pubExpAddEvent(gu.getId(), gainExp, getWay(), rd);
        //结算铜钱
        int godCopperRate = getGodCopperRate(gu);
        double copperAddRate = getCopperAddRate(param.getZcTimes(), godCopperRate);
        int extraCopper = (int) (baseCopper * copperAddRate);
        EPCopperAdd copperAdd = new EPCopperAdd(new BaseEventParam(gu.getId(), getWay(), rd), baseCopper, baseCopper);
        copperAdd.addCopper(ResWayType.Extra, extraCopper);
        ResEventPublisher.pubCopperAddEvent(copperAdd);
        // 落宝金钱发放道具
        boolean nightmareWord = gu.getStatus().intoNightmareWord();
        this.userTreasureEffectService.effectAsLBJQ(gu, rd);
        chengChiInfoCache.setAttackTimes(-1);
        if (chengChiInfoCache.ifAttackCity(nightmareWord)) {
            settleAttackCity(gu, chengChiInfoCache, rd, param, nightmareWord);
        } else {
            settleAttackLevel(gu, chengChiInfoCache, rd, nightmareWord);
        }
        TimeLimitCacheUtil.setChengChiInfoCache(gu.getId(), chengChiInfoCache);
    }

    /**
     * 攻城结算
     * @param gu
     * @param
     * @param rd
     * @param param
     */
    public void settleAttackCity(GameUser gu,ChengChiInfoCache cache, RDFightResult rd, FightSubmitParam param ,boolean nightmareWord){
        CfgCityEntity cityEntity = CityTool.getCityById(cache.getCityId());
        // 封地处理
        CityEventPublisher.pubUserCityAddEvent(gu.getId(), new EPCityAdd(cityEntity.getId(), gu.getStatus().intoNightmareWord()), rd);
        // 奖励 掉落卡牌
        CfgCardEntity card = this.userCardService.getAttackCardAwardForCity(gu.getId(),cache.getCityId());
        String type=nightmareWord?"唤醒":"攻下";
        CardEventPublisher.pubCardAddEvent(gu.getId(), card.getId(), getWay(), type+"了【" + cityEntity.getName() + "】，并获得", rd);
        // 已攻下
        rd.setOwnCity(1);
        rd.setManor(cityEntity.getId());
        rd.setInfoForBuriedPoint(type+"第" + userCityService.getOwnCityNumAsLevel(gu.getId(), cityEntity.getLevel()) + "座" + cityEntity.getLevel() + "级城");
        // 更新城内缓存
        cache.setOwnCity(true);
        UserCity userCity=userCityService.getUserCity(gu.getId(),cache.getCityId());
        cache.updateByUserCity(userCity);
        cache.setAttack(true);
        if (!nightmareWord){
            for (int i=0;i<cities.length;i++) {
                int cityId = cities[i];
                if (cityId==cityEntity.getId()){
                    TreasureEventPublisher.pubTAddEvent(gu.getId(), treasures[i],1,WayEnum.FIGHT_ATTACK,rd);
                    break;
                }
            }
        }
    }

    /**
     * 闯关结算
     * @param gu
     * @param chengChiInfoCache
     * @param rd
     * @param nightmareWord
     */
    public void settleAttackLevel(GameUser gu,ChengChiInfoCache chengChiInfoCache, RDFightResult rd, boolean nightmareWord){
        int cityId=chengChiInfoCache.getCityId();
        int passLevel= chengChiInfoCache.checkNextAttackLevel();
        int status=1;
        boolean nightmare=false;
        if (nightmareWord){
            status=3;
            nightmare=true;
            chengChiInfoCache.setAttackTimes(0);
        }
        chengChiInfoCache.getLevelProgress()[passLevel-1]=status;
        CityEventPublisher.pubUserPassCityLevelEvent(EPPassCityLevel.instance(new BaseEventParam(gu.getId(),getWay(),rd),cityId,passLevel,nightmare));
        TimeLimitCacheUtil.setChengChiInfoCache(gu.getId(),chengChiInfoCache);
        rd.setOwnCity(0);
        // 更新城内缓存
        chengChiInfoCache.setAttack(true);
    }

    @Override
    public void failure(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        // 未攻下
        rd.setOwnCity(0);
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(gu.getId());
        cache.addAttackTimes();
        this.userTreasureEffectService.effectAsLBJQ(gu, rd);
        TimeLimitCacheUtil.setChengChiInfoCache(gu.getId(),cache);
    }

    @Override
    public CPlayerInitParam getNormalOpponentParam(GameUser gu, ChengChiInfoCache cache, CombatPVEParam pveParam) {
        UserAttackDifficulty difficulty = attackDifficultyLogic.getAttackDifficulty(gu.getId());
        ChengC chengc = null;
        int nextLevel=cache.checkNextAttackLevel();
        int index=nextLevel-1;
        CPlayerInitParam param=new CPlayerInitParam();
        List<CCardParam> cardParams = null;
        int cardAddLv = difficulty.settleCardDifficultyByCityLevel(index);
        if (nextLevel==cache.getCityLv()){
            //攻打主城
            chengc = CityTool.getChengc(cache.getCityId());
            index=nextLevel-1;
            param.setNickname("禁卫军");
            cardParams = getCardsBySoliderString(chengc.getSoliders(), cardAddLv, 0, CfgCard.AI_CARDS_NOT_TO_FSDL_1);
        }else {
            //攻打关卡
            chengc = CityTool.getRandomCityByLeveType(nextLevel, TypeEnum.fromValue(cache.getCityProperty()));
            param.setNickname("护卫军");
            cardParams = getCardsBySoliderString(chengc.getSoliders(), cardAddLv, 0, CfgCard.AI_CARDS_NOT_TO_FSDL_1);
        }
        //AI 等级 ：配置等级+同级别攻下的城池数
        int playerLv=chengc.getSoliderLevel()+difficulty.getOwnCityNum()[index];
        CCardParam cardParam=cardParams.get(0);
        //第一张卡作为AI召唤师的名字
        param.setHeadImg(cardParam.getId());
        param.setLv(playerLv);
        param.setCards(cardParams);
        pveParam.setAiPlayer(param);
        return param;
    }

    public CPlayerInitParam getNormalOpponentParam(GameUser gu,int cityId,int cityLv, CombatPVEParam pveParam) {
        UserAttackDifficulty difficulty = attackDifficultyLogic.getAttackDifficulty(gu.getId());
        ChengC chengc = CityTool.getChengc(cityId);
        int index=cityLv-1;
        CPlayerInitParam param=new CPlayerInitParam();
        param.setNickname("禁卫军");
        int cardAddLv = difficulty.settleCardDifficultyByCityLevel(index);
        List<CCardParam> cardParams = getCardsBySoliderString(chengc.getSoliders(), cardAddLv, 0, CfgCard.AI_CARDS_NOT_TO_FSDL_1);
        //AI 等级 ：配置等级+同级别攻下的城池数
        int playerLv=chengc.getSoliderLevel()+difficulty.getOwnCityNum()[index];
        CCardParam cardParam=cardParams.get(0);
        //第一张卡作为AI召唤师的名字
        param.setHeadImg(cardParam.getId());
        param.setLv(playerLv);
        param.setCards(cardParams);
        pveParam.setAiPlayer(param);
        return param;
    }

    @Override
    public CPlayerInitParam getNightmareOpponentParam(GameUser gu, ChengChiInfoCache cache, CombatPVEParam pveParam) {
        ChengC chengC = CityTool.getNightmareChengC(cache.getCityId());
        List<CCardParam> cardParams = null;
        if (cache.ifAttackCity(true) && chengC.getSoliders2()!=null){
            //禁卫军
            cardParams = getCardsBySoliderString(chengC.getSoliders2(),0,0,null);
        }else {
            //护卫军
            cardParams = getCardsBySoliderString(chengC.getSoliders(),0,0,null);
        }
        for (CCardParam cardParam : cardParams) {
            if (cardParam.getId()>10000 && cardParam.getIsUseSkillScroll()!=1){
                CardDeifyCardParam param = CardTool.getPerfectDeifyCardSkills(cardParam.getId());
                //后续可能会涉及到集合操作，避免使用Arrays.as()方法
                if (param.isChange()){
                    List<Integer> newSkills=new ArrayList<>();
                    for (int skill : param.getSkills()) {
                        newSkills.add(skill);
                    }
                    cardParam.setSkills(newSkills);
                    cardParam.setIsUseSkillScroll(1);
                }
            }
        }
        //AI 等级 ：配置等级+同级别攻下的城池数
        CCardParam cardParam=cardParams.get(0);
        //第一张卡作为AI召唤师的名字
        CPlayerInitParam param=new CPlayerInitParam();
        param.setHeadImg(cardParam.getId());
        param.setLv(0);
        param.setCards(cardParams);
        param.setNickname("护卫军");
        if (cache.ifAttackCity(true)){
            param.setNickname("禁卫军");
//            pveParam.setCardDisparityAtk(-0.2);
//            pveParam.setCardDisparityHp(-0.2);
        }
        param.addBuff(cache.getCityBuff());
        updateNightmareAiInfo(gu.getId(), cache, param);
        pveParam.setAiPlayer(param);
        return param;
    }

    private void updateNightmareAiInfo(long uid,ChengChiInfoCache cache, CPlayerInitParam param){
        int lv=0;
        int cardHv=6;
        int cardLv=0;
        UserAttackDifficulty difficulty = attackDifficultyLogic.getAttackDifficulty(uid);
        int cityLevel=cache.getCityLv();
        int ownNum=difficulty.getOwnNightMareCityNum()[cityLevel-1];
        switch (cityLevel){
            case 1:
                lv=72+ownNum*2;
                cardHv+=ownNum/5;
                cardLv=16+ownNum;
                break;
            case 2:
                lv=82+ownNum*2;
                cardHv+=ownNum/4;
                cardLv=21+ownNum;
                break;
            case 3:
                lv=92+ownNum*2;
                cardHv+=ownNum/4;
                cardLv=21+ownNum;
                break;
            case 4:
                lv=102+ownNum*2;
                cardHv+=ownNum/2;
                cardLv=26+ownNum;
                break;
            case 5:
                lv=135;
                cardHv+=4;
                cardLv=40;
                break;
            default:
        }
        param.setLv(lv);
        for (CCardParam card : param.getCards()) {
            card.setLv(cardLv);
            card.setHv(cardHv);
        }
    }
}
