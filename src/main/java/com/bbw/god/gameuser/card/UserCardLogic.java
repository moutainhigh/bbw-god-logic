package com.bbw.god.gameuser.card;

import com.bbw.cache.UserCacheService;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.processor.cardboost.CardBoostProcessor;
import com.bbw.god.detail.async.UserSpecialCardAsyncHandler;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.*;
import com.bbw.god.game.config.treasure.CfgSkillScrollLimitEntity;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.biyoupalace.cfg.BYPalaceTool;
import com.bbw.god.gameuser.biyoupalace.cfg.CfgBYPalaceSymbolEntity;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.card.event.EPCardHierarchyUp;
import com.bbw.god.gameuser.card.event.EPCardSkillChange;
import com.bbw.god.gameuser.card.event.EPCardSkillReset;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.ele.EVEle;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasureRecordService;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 卡牌游戏逻辑
 *
 * @author suhq
 * @date 2018年11月24日 下午8:16:59
 */
@Slf4j
@Service
public class UserCardLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserTreasureRecordService userTreasureRecordService;
    @Autowired
    private CardBoostProcessor cardBoostProcessor;
    @Autowired
    private UserSpecialCardAsyncHandler userSpecialCardAsyncHandler;
    @Autowired
    private UserCacheService userCacheService;

    /**
     * 升级卡牌
     *
     * @param guId
     * @param param
     * @return
     */
    public RDCommon updateCard(Long guId, CPCardUpdate param) {

        RDCommon rd = new RDCommon();
        GameUser gu = this.gameUserService.getGameUser(guId);

        // 元素是否足够
        ResChecker.checkEle(gu, TypeEnum.Gold.getValue(), param.getGoldEle());
        ResChecker.checkEle(gu, TypeEnum.Wood.getValue(), param.getWoodEle());
        ResChecker.checkEle(gu, TypeEnum.Water.getValue(), param.getWaterEle());
        ResChecker.checkEle(gu, TypeEnum.Fire.getValue(), param.getFireEle());
        ResChecker.checkEle(gu, TypeEnum.Earth.getValue(), param.getEarthEle());
        UserCard userCard = getUserCard(gu.getId(), param.getDataId(), param.getCardId());
        // 卡牌是否有效
        CardChecker.checkIsOwn(userCard);
        // 卡牌是否升满
        CardChecker.checkIsFullUpdate(userCard);
        // 灵石是否足够
        if (userCard.getLingshi() < param.getCardSoul()) {
            throw new ExceptionForClientTip("card.update.not.enough.lingshi");
        }
        int totalUseEle = param.getGoldEle() + param.getWoodEle() + param.getWaterEle() + param.getFireEle() + param.getEarthEle() + param.getShenSha();
        long needCopper = totalUseEle * CardTool.getCardUpdateData().getCopperPerEle();
        // 铜钱是否足够
        ResChecker.checkCopper(gu, needCopper);

        // 卡牌经验
        CfgCardEntity card = userCard.gainCard();
        int addedExperience = getExpAsUpdateParam(card, param);
        //助力经验翻倍
        if (cardBoostProcessor.isDoubleExp(guId)) {
            addedExperience *= 2;
        }
        long maxExp = CardExpTool.getMaxExp(card);
        if (userCard.getExperience() + addedExperience > maxExp) {
            throw new ExceptionForClientTip("card.update.to.exceedTopLevel");
        }
        // 灵石
        if (param.getCardSoul() > 0) {
            userCard.deductLingshi(param.getCardSoul());
            this.gameUserService.updateItem(userCard);
        }
        // 铜钱
        ResEventPublisher.pubCopperDeductEvent(guId, needCopper, WayEnum.CARD_UPDATE, rd);
        // 扣除元素
        List<EVEle> eles = new ArrayList<>();
        eles.add(new EVEle(TypeEnum.Gold.getValue(), param.getGoldEle()));
        eles.add(new EVEle(TypeEnum.Wood.getValue(), param.getWoodEle()));
        eles.add(new EVEle(TypeEnum.Water.getValue(), param.getWaterEle()));
        eles.add(new EVEle(TypeEnum.Fire.getValue(), param.getFireEle()));
        eles.add(new EVEle(TypeEnum.Earth.getValue(), param.getEarthEle()));
        if (param.getShenSha() > 0) {
            // 如果需要神砂则检查神砂数量是否够
            TreasureChecker.checkIsEnough(TreasureEnum.SS.getValue(), param.getShenSha(), guId);
            // 将神砂自动先兑换成元素（因为此操作需要记录到元素消耗榜）
            TreasureEventPublisher.pubTDeductEvent(guId, TreasureEnum.SS.getValue(), param.getShenSha(),
                    WayEnum.EXCHANGE_XJBK, new RDCommon());
            ResEventPublisher.pubEleAddEvent(guId, card.getType(), param.getShenSha(), WayEnum.EXCHANGE_XJBK,
                    new RDCommon());
            eles.add(new EVEle(card.getType(), param.getShenSha()));
        }
        ResEventPublisher.pubEleDeductEvent(guId, eles, WayEnum.CARD_UPDATE, rd);
        // 经验
        BaseEventParam bp = new BaseEventParam(guId, WayEnum.CARD_UPDATE, rd);
        CardEventPublisher.pubCardExpAddEvent(bp, card.getId(), addedExperience);
        return rd;
    }

    /**
     * 卡牌进阶
     *
     * @param guId
     * @param cardId             卡牌ID
     * @param isUseUniversalSoul 是否使用万能灵石
     * @return
     */
    public RDCommon updateHierarchy(long guId, Long dataId, int cardId, int isUseUniversalSoul) {
        RDCommon rd = new RDCommon();
        UserCard userCard = getUserCard(guId, dataId, cardId);
        CardChecker.checkIsOwn(userCard);
        CardChecker.checkIsFullHierarchy(userCard);
        CfgCardEntity card = userCard.gainCard();
        int needLingshi = CardHvTool.getNeededLingshiForUpdate(userCard.getHierarchy());
        int ownLingshi = userCard.getLingshi();
        int ownUniversalSoul = 0;
        int deductUniversalSoul = 0;
        int universalSoulId = card.getStar() * 10 + 800;
        // 是否使用万能灵石
        if (isUseUniversalSoul == 1) {
            ownUniversalSoul = userTreasureService.getTreasureNum(guId, universalSoulId);
        }
        if (ownLingshi + ownUniversalSoul < needLingshi) {
            throw new ExceptionForClientTip("card.hierarchy.not.enough.lingshi");
        }
        // 处理混沌仙石
        int needHYXS = CardHvTool.getNeededHYXSForUpdate(userCard.getHierarchy(), card.getStar());
        if (needHYXS > 0) {
            TreasureChecker.checkIsEnough(TreasureEnum.HDXS.getValue(), needHYXS, guId);
            // 扣除混沌仙石
            TreasureEventPublisher.pubTDeductEvent(guId, TreasureEnum.HDXS.getValue(), needHYXS, WayEnum.CARD_HIERARCHY, rd);
        }
        // 扣除万能灵石
        if (isUseUniversalSoul == 1 && ownLingshi < needLingshi) {
            deductUniversalSoul = needLingshi - ownLingshi;
            TreasureEventPublisher.pubTDeductEvent(guId, universalSoulId, deductUniversalSoul, WayEnum.CARD_HIERARCHY, rd);
            needLingshi = ownLingshi;
        }
        // 扣除灵石
        userCard.deductLingshi(needLingshi);
        this.gameUserService.updateItem(userCard);
        // 进阶
        BaseEventParam bp = new BaseEventParam(guId, WayEnum.CARD_HIERARCHY, rd);
        EPCardHierarchyUp ep = new EPCardHierarchyUp(bp);
        ep.setCardId(cardId);
        ep.setDeductCardLingshi(needLingshi);
        ep.setUniversalSoulId(universalSoulId);
        ep.setDeductUniversalSoulId(deductUniversalSoul);
        ep.setDeductneedHYXS(needHYXS);
        CardEventPublisher.pubCardHierarchyUpEvent(ep);

        return rd;
    }

    /**
     * 使用技能卷轴（炼技）
     *
     * @param param
     * @return
     */
    public RDCardStrengthen useSkillScroll(long uid, CPCardUseSkillScroll param) {
        int cardId = param.getCardId();
        int skillLevel = param.getSkillLevel();
        int skillScroll = param.getSkillScroll();
        // 是否拥有该技能卷轴
        TreasureChecker.checkHasTreasure(uid, skillScroll);

        CfgCardEntity cc = CardTool.getCardById(cardId);
        // 卷轴的有效性
        CfgTreasureEntity treasureEntity = TreasureTool.getTreasureById(skillScroll);
        if (treasureEntity == null || !treasureEntity.ifSkillScroll()) {
            throw new ExceptionForClientTip("card.useSkillScroll.noValid");
        }
        // 属性判定
        CfgSkillScrollLimitEntity ssle = TreasureTool.getSkillScrollLimitEntity(skillScroll);
        if (ListUtil.isNotEmpty(ssle.getLimitTypes()) && !ssle.getLimitTypes().contains(cc.getType())) {
            throw new ExceptionForClientTip("card.useSkillScroll.noValid.type");
        }
        // 卡牌判定
        if (ListUtil.isNotEmpty(ssle.getLimitCards()) && !ssle.getLimitCards().contains(cc.getId())) {
            throw new ExceptionForClientTip("card.useSkillScroll.noValid.card");
        }

        // 技能判定
        Optional<CfgCardSkill> optional = CardSkillTool.getCardSkillOpById(ssle.getSkillId());
        if (!optional.isPresent()) {
            throw new ExceptionForClientTip("card.useSkillScroll.noValid");
        }

        UserCard uc = getUserCard(uid, param.getDataId(), cardId);
        if (uc.ifOwnCompoundSkills(ssle.getSkillId())) {
            throw new ExceptionForClientTip("card.useSkillScroll.noValid.card");
        }
        if (uc.getLevel() < 10) {
            throw new ExceptionForClientTip("card.useSkillScroll.noValid.level");
        }
        int replacedSkill = uc.gainSkill(CardSkillPosEnum.fromLevel(skillLevel));
        if (ListUtil.isNotEmpty(ssle.getLimitSkills()) && !ssle.getLimitSkills().contains(replacedSkill)) {
            throw new ExceptionForClientTip("card.useSkillScroll.noValid.skill");
        }
        // 技能等级判定
        if (ListUtil.isNotEmpty(ssle.getLimitLevels()) && !ssle.getLimitLevels().contains(skillLevel)) {
            throw new ExceptionForClientTip("card.useSkillScroll.noValid.level");
        }

        // 卷轴使用次数限制
        if (!uc.ifAbleUseSkillScroll()) {
            throw new ExceptionForClientTip("card.useSkillScroll.outOfLimit");
        }
        // 不能有重复的技能
        if (uc.ifUsed(ssle.getSkillId())) {
            throw new ExceptionForClientTip("card.useSkillScroll.no.repeat");
        }

        // 元宝检查
        CfgCard cfgCard = CardTool.getConfig();
        int needGold = cfgCard.getSkillChangePrice().get(cc.getStar() - 1);
        GameUser gu = this.gameUserService.getGameUser(uid);
        ResChecker.checkGold(gu, needGold);
        RDCardStrengthen rd = new RDCardStrengthen();

        // 清除限制卷轴使用记录
        this.userTreasureRecordService.deductSkillScrollRecord(uc, skillLevel);
        //扣除需要的资源
        TreasureEventPublisher.pubTDeductEvent(uid, skillScroll, 1, WayEnum.CARD_USE_SKILL_SCROLL, rd);
        ResEventPublisher.pubGoldDeductEvent(uid, needGold, WayEnum.CARD_USE_SKILL_SCROLL, rd);
        // 更新数据
        Integer oldSkill = uc.getStrengthenInfo().gainCurrentSkill(CardSkillPosEnum.fromLevel(skillLevel));
        uc.getStrengthenInfo().updateLastSkillMap(skillLevel, oldSkill);
        uc.updateNewSkill(skillLevel, ssle.getSkillId());
        uc.updateUsedSkillScroll(skillLevel, ssle.getId());
        this.gameUserService.updateItem(uc);

        if (null != oldSkill && 0 != oldSkill) {
            int type = CardTool.getCardById(uc.getBaseId()).getType();
            int skillScrollId = TreasureTool.getSkillScrollId(type, oldSkill, uc.getBaseId());
            TreasureEventPublisher.pubTRecordDelEvent(gu.getId(), skillScrollId, WayEnum.RESET_CARD_SKILL);
        }

        rd.setStrengthenInfo(uc);

        //发布卡牌练技事件
        EPCardSkillChange ep = new EPCardSkillChange(new BaseEventParam(uid), cardId, cc.getName(), oldSkill, treasureEntity.getName());
        CardEventPublisher.pubCardSkillChangeEvent(ep);
        //更新炼技信息到数据库  与正常业务无关
        int gid = gameUserService.getActiveGid(uid);
        userSpecialCardAsyncHandler.log(uc, gid, gu.getLevel(), gu.getServerId(), gu.getRoleInfo());
//        log.info("[" + uid + "]为卡牌[" + cc.getName() + "]成功穿戴技能卷轴[" + treasureEntity.getName() + "][" + skillScroll + "]");
        return rd;
    }

    /**
     * 重置卷轴
     *
     * @param uid
     * @param pos 为空时重置所有，非空时0,5,10 重置对应的技能
     */
    public RDCardStrengthen clearSkillScroll(long uid, Long dataId, int cardId, Integer pos) {
        UserCard uc = getUserCard(uid, dataId, cardId);
        GameUser gu = this.gameUserService.getGameUser(uid);
        int needGold = 500;//重置单个技能要500;
        check(pos, uc);
        if (pos == null) {
            //重置所有
            needGold = 200;//全部重置200元宝
        }
        UserCard.UserCardStrengthenInfo info = uc.getStrengthenInfo();
        int useSkillScrollTimes = info.gainUseSkillScrollTimes();
        List<Integer> oldSkill = Arrays.asList(info.gainSkill0(), info.gainSkill5(), info.gainSkill10());
        Integer usedSkillScroll = uc.gainUsedSkillScrollId(pos);
        uc.resetSkill(pos);
        List<Integer> newSkill = Arrays.asList(info.gainSkill0(), info.gainSkill5(), info.gainSkill10());
        ResChecker.checkGold(gu, needGold);
        RDCardStrengthen rd = new RDCardStrengthen();
        rd.setIsUseSkillScroll(uc.ifUseSkillScroll() ? 1 : 0);
        rd.setSkill0(uc.gainSkill0());
        rd.setSkill5(uc.gainSkill5());
        rd.setSkill10(uc.gainSkill10());
        ResEventPublisher.pubGoldDeductEvent(uid, needGold, WayEnum.RESET_CARD_SKILL, rd);
        gameUserService.updateItem(uc);
        BaseEventParam bep = new BaseEventParam(gu.getId(), WayEnum.RESET_CARD_SKILL, rd);
        CardEventPublisher.pubCardSkillResetEvent(new EPCardSkillReset(oldSkill, newSkill, useSkillScrollTimes, bep));
        TreasureEventPublisher.pubTRecordDelEvent(gu.getId(), usedSkillScroll, WayEnum.RESET_CARD_SKILL);
        //更新炼技信息到数据库  与正常业务无关
        int gid = gameUserService.getActiveGid(uid);
        userSpecialCardAsyncHandler.log(uc, gid, gu.getLevel(), gu.getServerId(), gu.getRoleInfo());
        return rd;
    }

    /**
     * 取回技能卷轴
     *
     * @param uid    玩家id
     * @param cardId 卡牌id
     * @param pos    0,5,10 重置对应的技能
     */
    public RDCardStrengthen takeOutSkillScroll(long uid, Long dataId, int cardId, int pos) {
        UserCard uc = getUserCard(uid, dataId, cardId);
        GameUser gu = this.gameUserService.getGameUser(uid);
        int treasureNum = userTreasureService.getTreasureNum(uid, TreasureEnum.LJBHF.getValue());
        if (treasureNum < 0) {
            throw new ExceptionForClientTip("treasure.not.enough", TreasureEnum.LJBHF.getName());
        }
        check(pos, uc);
        RDCardStrengthen rd = new RDCardStrengthen();
        UserCard.UserCardStrengthenInfo info = uc.getStrengthenInfo();
        int useSkillScrollTimes = info.gainUseSkillScrollTimes();
        List<Integer> oldSkill = Arrays.asList(info.gainSkill0(), info.gainSkill5(), info.gainSkill10());
        Integer usedSkillScroll = uc.gainUsedSkillScrollId(pos);
        uc.resetSkill(pos);
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.LJBHF.getValue(), 1, WayEnum.TAKE_OUT_SKILL_SCROLL, rd);
        List<Integer> newSkill = Arrays.asList(info.gainSkill0(), info.gainSkill5(), info.gainSkill10());
        rd.setIsUseSkillScroll(uc.ifUseSkillScroll() ? 1 : 0);
        rd.setSkill0(uc.gainSkill0());
        rd.setSkill5(uc.gainSkill5());
        rd.setSkill10(uc.gainSkill10());
        gameUserService.updateItem(uc);
        BaseEventParam bep = new BaseEventParam(gu.getId(), WayEnum.TAKE_OUT_SKILL_SCROLL, rd);
        CardEventPublisher.pubCardSkillResetEvent(new EPCardSkillReset(oldSkill, newSkill, useSkillScrollTimes, bep));
        TreasureEventPublisher.pubTAddEvent(uid, usedSkillScroll, 1, WayEnum.TAKE_OUT_SKILL_SCROLL, rd);
        TreasureEventPublisher.pubTRecordDelEvent(gu.getId(), usedSkillScroll, WayEnum.RESET_CARD_SKILL);
        //更新炼技信息到数据库  与正常业务无关
        int gid = gameUserService.getActiveGid(uid);
        userSpecialCardAsyncHandler.log(uc, gid, gu.getLevel(), gu.getServerId(), gu.getRoleInfo());
        return rd;
    }

    /**
     * 重置技能前的校验
     *
     * @param pos
     * @param uc
     */
    private void check(int pos, UserCard uc) {
        if (uc.getStrengthenInfo() == null) {
            throw new ExceptionForClientTip("card.no.useSkillScroll");
        }
        UserCard.UserCardStrengthenInfo ucs = uc.getStrengthenInfo();
        ucs.updateUseSkillScrollTimes();
        if (ucs.gainUseSkillScrollTimes() == 0) {
            //没有需要重置的技能
            throw new ExceptionForClientTip("card.no.useSkillScroll");
        }
        if (!uc.ifSkillChanged(pos)) {
            //没有需要重置的技能
            throw new ExceptionForClientTip("card.no.useSkillScroll");
        }
        List<Integer> skills = uc.gainSkills();
        skills.remove(pos / 5);
        List<Integer> putedOnSkills = ucs.gainPutedOnSkills(pos);
        int lastPosSkill = 0;
        if (ListUtil.isEmpty(putedOnSkills) || putedOnSkills.size() == 1) {
            lastPosSkill = uc.gainCard().getSkill(pos);
        } else {
            lastPosSkill = putedOnSkills.get(putedOnSkills.size() - 2);
        }
        if (lastPosSkill > 0 && skills.contains(lastPosSkill)) {
            throw ExceptionForClientTip.fromi18nKey("card.useSkillScroll.skill.will.repeat");
        }

    }

    /**
     * 穿上符箓（修体）
     *
     * @param param
     * @return
     */
    public RDCardStrengthen putOnSymbol(Long uid, CPSymbol param) {
        int cardId = param.getCardId();
        int symbol = param.getSymbol();
        // 是否拥有该符箓
        TreasureChecker.checkHasTreasure(uid, symbol);

        CfgBYPalaceSymbolEntity symbolEntity = BYPalaceTool.getSymbolEntity(symbol);
        UserCard uc = getUserCard(uid, param.getDataId(), cardId);
        // 卸下使用中的符箓
        int usedSymbol = uc.gainSymbol(symbolEntity);
        if (usedSymbol > 0) {
            TreasureEventPublisher.pubTAddEvent(uid, usedSymbol, 1, WayEnum.CARD_PUT_ON_SYMBOL, new RDCommon());
        }
        RDCardStrengthen rd = new RDCardStrengthen();
        // 转备新的符箓
        uc.updateSymbol(symbolEntity);
        this.gameUserService.updateItem(uc);
        TreasureEventPublisher.pubTDeductEvent(uid, symbol, 1, WayEnum.CARD_PUT_ON_SYMBOL, rd);
        rd.setStrengthenInfo(uc);
        return rd;
    }

    /**
     * 卸下符箓
     *
     * @param param
     * @return
     */
    public RDCardStrengthen unloadSymbol(long uid, CPSymbol param) {
        int cardId = param.getCardId();
        int symbol = param.getSymbol();
        RDCardStrengthen rd = new RDCardStrengthen();
        CfgBYPalaceSymbolEntity symbolEntity = BYPalaceTool.getSymbolEntity(symbol);
        if (null == symbolEntity) {
            return new RDCardStrengthen();
        }
        UserCard uc = getUserCard(uid, param.getDataId(), cardId);
        // 卸下使用中的符箓
        int usedSymbol = uc.gainSymbol(symbolEntity);
        if (usedSymbol > 0) {
            TreasureEventPublisher.pubTAddEvent(uid, usedSymbol, 1, WayEnum.CARD_UNLOAD_SYMBOL, rd);
        }
        // 卸下符箓
        uc.removeSymbol(symbolEntity);
        this.gameUserService.updateItem(uc);
        rd.setStrengthenInfo(uc);
        return rd;
    }

    /**
     * 升级卡牌时可获得经验
     *
     * @param card
     * @param param
     * @return
     */
    private int getExpAsUpdateParam(CfgCardEntity card, CPCardUpdate param) {
        int addedExperience = 0;
        int expPerEle = CardTool.getCardUpdateData().getExpPerEle();
        switch (card.getType()) {
            case 10:
                addedExperience = expPerEle / 2 * (param.getWoodEle() + param.getWaterEle() + param.getFireEle() + param.getEarthEle()) + expPerEle * param.getGoldEle();
                break;
            case 20:
                addedExperience = expPerEle / 2 * (param.getGoldEle() + param.getWaterEle() + param.getFireEle() + param.getEarthEle()) + expPerEle * param.getWoodEle();
                break;
            case 30:
                addedExperience = expPerEle / 2 * (param.getGoldEle() + param.getWoodEle() + param.getFireEle() + param.getEarthEle()) + expPerEle * param.getWaterEle();
                break;
            case 40:
                addedExperience = expPerEle / 2 * (param.getGoldEle() + param.getWoodEle() + param.getWaterEle() + param.getEarthEle()) + expPerEle * param.getFireEle();
                break;
            case 50:
                addedExperience = expPerEle / 2 * (param.getGoldEle() + param.getWoodEle() + param.getWaterEle() + param.getFireEle()) + expPerEle * param.getEarthEle();
                break;
        }
        // 当使用神砂时默认为卡牌属性相同元素
        addedExperience += param.getShenSha() * expPerEle;
        switch (card.getStar()) {
            case 1:
                addedExperience += 300 * param.getCardSoul();
                break;
            case 2:
                addedExperience += 600 * param.getCardSoul();
                break;
            case 3:
                addedExperience += 2000 * param.getCardSoul();
                break;
            case 4:
                addedExperience += 6000 * param.getCardSoul();
                break;
            case 5:
                addedExperience += 15000 * param.getCardSoul();
                break;
        }
        return addedExperience;
    }

    /**
     * 添加技能组
     *
     * @param cardId 卡牌id
     * @return
     */
    public RDCommon activationSkillGroup(Long uid, Long dataId, int cardId) {
        //获取卡牌基本信息并校验
        CardTool.getCardById(cardId);
        //获取用户卡牌
        UserCard uc = getUserCard(uid, dataId, cardId);
        //校验用户是否拥有该卡牌
        CardChecker.checkIsOwn(uc);
        //获取卡牌配置
        CfgCard cardConfig = CardTool.getConfig();
        //获取元宝
        int needGold = cardConfig.getSkillGroupActiveGold();
        //获取用户角色拥有的元宝
        GameUser gu = this.gameUserService.getGameUser(uid);
        //比较元宝
        ResChecker.checkGold(gu, needGold);

        if (null == uc.getStrengthenInfo()) {
            uc.setStrengthenInfo(new UserCard.UserCardStrengthenInfo());
            uc.getStrengthenInfo().setSkillGroups(uc.getStrengthenInfo().initSkillGroup(CardConstant.SKILL_GROUP_1));
        }
        RDCommon rd = new RDCommon();
        //实例化技能组信息
        uc.getStrengthenInfo().setSkillGroups(uc.getStrengthenInfo().initSkillGroup(CardConstant.SKILL_GROUP_2));
        //扣除元宝
        ResEventPublisher.pubGoldDeductEvent(uid, needGold, WayEnum.CARD_USE_SKILL_SCROLL, rd);
        gameUserService.updateItem(uc);
        return rd;
    }

    /**
     * 切换技能组
     *
     * @param cardId         卡牌id
     * @param skillGroupName 技能组名称
     * @return
     */
    public RDChangeSkillGroups changeSkillGroup(long uid, Long dataId, int cardId, String skillGroupName) {
        //获取卡牌基本信息并校验
        CardTool.getCardById(cardId);
        //获取用户卡牌
        UserCard uc = getUserCard(uid, dataId, cardId);
        //校验用户是否拥有该卡牌
        CardChecker.checkIsOwn(uc);

        if (null == uc.getStrengthenInfo()) {
            throw new ExceptionForClientTip("card.skill.group.not.actived");
        }

        if (!CardConstant.SKILL_GROUP_1.equals(skillGroupName) && !CardConstant.SKILL_GROUP_2.equals(skillGroupName)) {
            throw new ExceptionForClientTip("card.skill.group.not.exist");
        }
        if (!uc.getStrengthenInfo().ifActivationSkillGroup(skillGroupName)) {
            throw new ExceptionForClientTip("card.skill.group.not.actived");
        }

        uc.getStrengthenInfo().setCurrentSkillGroup(skillGroupName);
        gameUserService.updateItem(uc);
        return RDChangeSkillGroups.getInstance(uc);
    }

    /**
     * 获取技能组
     *
     * @param cardId 卡牌id
     * @return
     */
    public RDSkillGroups getSkillGroup(Long uid, Long dataId, int cardId) {
        //获取卡牌基本信息并校验
        CfgCardEntity cfgCard = CardTool.getCardById(cardId);
        //获取用户卡牌
        UserCard uc = getUserCard(uid, dataId, cardId);
        //校验用户是否拥有该卡牌
        CardChecker.checkIsOwn(uc);

        if (null == uc.getStrengthenInfo()) {
            uc.setStrengthenInfo(new UserCard.UserCardStrengthenInfo());
            uc.getStrengthenInfo().initSkillGroup(CardConstant.SKILL_GROUP_1);
//            rd.setStrengthenInfo(uc);
            gameUserService.updateItem(uc);
        }
        Map<String, UserCard.SkillGroup> skillGroups = uc.getStrengthenInfo().getSkillGroups();
        List<RDSkillGroups.RDSkillGroup> rdSkillGroups = new ArrayList<>();
        for (String skillGroupKey : skillGroups.keySet()) {
            UserCard.SkillGroup skillGroup = skillGroups.get(skillGroupKey);
            if (skillGroup != null) {
                RDSkillGroups.RDSkillGroup rdSkillGroup = RDSkillGroups.RDSkillGroup.setRDSkillGroup(skillGroup, skillGroupKey, cfgCard);
                rdSkillGroups.add(rdSkillGroup);
            }
        }
        return RDSkillGroups.getInstance(rdSkillGroups, uc.getStrengthenInfo().getCurrentSkillGroup());
    }

    /**
     * 获取卡牌数据
     *
     * @param uid
     * @param dataId
     * @param cardId
     * @return
     */
    private UserCard getUserCard(long uid, Long dataId, int cardId) {
        UserCard userCard = null;
        if (null != dataId) {
            userCard = gameUserService.getUserData(uid, dataId, UserCard.class).orElse(null);
            //直接从Redis读数据，需将缓存置为无效，避免因为缓存读取不到最新更改的数据
            userCacheService.removeCache(uid, DateUtil.now(), UserCard.class);
            return userCard;
        }
        return userCardService.getUserCard(uid, cardId);
    }
}
