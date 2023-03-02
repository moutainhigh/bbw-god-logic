package com.bbw.god.game.zxz.service;

import com.bbw.common.ListUtil;
import com.bbw.god.game.config.card.equipment.randomrule.CardXianJueRandomRule;
import com.bbw.god.game.config.card.equipment.randomrule.CardZhiBaoRandomRule;
import com.bbw.god.game.config.card.equipment.randomrule.CfgCardEquipmentRandomRuleTool;
import com.bbw.god.game.zxz.cfg.CfgLingZhuangEntryEntity;
import com.bbw.god.game.zxz.cfg.ZxzEntryTool;
import com.bbw.god.game.zxz.constant.ZxzConstant;
import com.bbw.god.game.zxz.entity.UserEntryInfo;
import com.bbw.god.game.zxz.entity.ZxzCard;
import com.bbw.god.game.zxz.entity.ZxzFuTu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 诛仙阵解析规则
 * @author: hzf
 * @create: 2023-01-02 17:38
 **/
public class ZxzAnalysisService {

    /**
     * 解析玩家词条
     * @param userEntry
     * @return
     */
    public static List<UserEntryInfo.UserEntry> gainUserEntrys(List<String> userEntry) {
        List<UserEntryInfo.UserEntry> uEntries = new ArrayList<>();
        if (ListUtil.isEmpty(userEntry)){
            return uEntries;
        }
        for (String entry : userEntry) {
            String[] entryInfo = entry.split(ZxzConstant.SPLIT_CHAR);
            Integer entryId = Integer.valueOf(entryInfo[0]);
            Integer entryLv = Integer.valueOf(entryInfo[1]);
            Integer gear = Integer.valueOf(entryInfo[2]);
            UserEntryInfo.UserEntry uEntry = new UserEntryInfo.UserEntry();
            uEntry.setEntryId(entryId);
            uEntry.setEntryLv(entryLv);
            uEntry.setGear(gear);
            uEntries.add(uEntry);
        }
        return uEntries;
    }

    /**
     * 诛仙阵 符图 List<String> ->List<ZxzFuTu>
     * @return
     */
    public static List<ZxzFuTu> gainRunes(List<String> runes) {
        if (ListUtil.isEmpty(runes)) {
            return new ArrayList<>();
        }
        //处理旧数据带来的问题
        if (!runes.get(0).contains(ZxzConstant.SPLIT_CHAR)){
            return new ArrayList<>();
        }
        List<ZxzFuTu> zxzFuTus = new ArrayList<>();
        for (String rune : runes) {
            String[] entryInfo = rune.split(ZxzConstant.SPLIT_CHAR);
            Integer fuTuId = Integer.valueOf(entryInfo[0]);
            Integer fuTuLv = Integer.valueOf(entryInfo[1]);
            Integer pos = Integer.valueOf(entryInfo[2]);
            ZxzFuTu zxzFuTu = new ZxzFuTu();
            zxzFuTu.setFuTuId(fuTuId);
            zxzFuTu.setFuTuLv(fuTuLv);
            zxzFuTu.setPos(pos);
            zxzFuTus.add(zxzFuTu);
        }
        return zxzFuTus;
    }

    /**
     * 获取卡组
     * @return
     */
    public static List<ZxzCard> gainCards(List<String> defenderCards) {
        List<ZxzCard> zCards = new ArrayList<>();
        for (String card : defenderCards) {
            String[] cardInfo = card.split(ZxzConstant.SPLIT_CHAR);
            Integer cardId = Integer.valueOf(cardInfo[0]);
            Integer lv = Integer.valueOf(cardInfo[1]);
            Integer hv = Integer.valueOf(cardInfo[2]);
            List<Integer> skillIds = ListUtil.parseStrToInts(cardInfo[3]);
            ZxzCard zCard = new ZxzCard();
            zCard.setCardId(cardId);
            zCard.setLv(lv);
            zCard.setHv(hv);
            zCard.setSkills(skillIds);
            zCards.add(zCard);
        }
        return zCards;
    }
    /**
     * 构建卡牌仙决
     * @return
     */
    public static List<CardXianJueRandomRule> gainCardXianJues(List<String> cardXianJues){
        List<CardXianJueRandomRule> cardXianJueList = new ArrayList<>();
        /** cardId@xianJueType@level@quality@starMapProgress@addition */
        for (String cardXianJue : cardXianJues) {
            CardXianJueRandomRule cXianjue = new CardXianJueRandomRule();
            String[] xianJue = cardXianJue.split(ZxzConstant.SPLIT_CHAR);
            Integer cardId = Integer.valueOf(xianJue[0]);
            Integer xianJueType = Integer.valueOf(xianJue[1]);
            Integer level = Integer.valueOf(xianJue[2]);
            Integer quality = Integer.valueOf(xianJue[3]);
            Integer starcMapProgress = Integer.valueOf(xianJue[4]);
            Map<String,Integer> additions = CfgCardEquipmentRandomRuleTool.convertWithStream(xianJue[5]);
            cXianjue.setCardId(cardId);
            cXianjue.setXianJueType(xianJueType);
            cXianjue.setLevel(level);
            cXianjue.setQuality(quality);
            cXianjue.setStarMapProgress(starcMapProgress);
            cXianjue.setAdditions(additions);
            cardXianJueList.add(cXianjue);
        }
        return cardXianJueList;
    }
    /**
     * 获取单张卡牌的仙决信息
     * @param cardId
     * @return
     */
    public static List<CardXianJueRandomRule> gainCardXianJue(Integer cardId,List<String> cardXianJues){
        return  gainCardXianJues(cardXianJues).stream().filter(tmp -> tmp.getCardId().equals(cardId)).collect(Collectors.toList());
    }

    /**
     * 构建至宝
     * @return
     */
    public static List<CardZhiBaoRandomRule> gainCardZhiBaos(List<String> cardZhiBaos) {
        List<CardZhiBaoRandomRule> cardZhiBaoList = new ArrayList<>();
        /**cardId@zhiBaoId@property@addition@skillGroup */
        for (String cardZhiBao : cardZhiBaos) {
            CardZhiBaoRandomRule cZhiBao = new CardZhiBaoRandomRule();
            String[] zhiBao = cardZhiBao.split(ZxzConstant.SPLIT_CHAR);
            Integer cardId = Integer.valueOf(zhiBao[0]);
            Integer zhiBaoId = Integer.valueOf(zhiBao[1]);
            Integer property = Integer.valueOf(zhiBao[2]);
            Map<String,Integer> additions = CfgCardEquipmentRandomRuleTool.convertWithStream(zhiBao[3]);
            List<Integer> skillIds = ListUtil.parseStrToInts(zhiBao[4]);
            Integer[] skillGroup = skillIds.toArray(new Integer[0]);
            cZhiBao.setCardId(cardId);
            cZhiBao.setZhiBaoId(zhiBaoId);
            cZhiBao.setProperty(property);
            cZhiBao.setAdditions(additions);
            cZhiBao.setSkillGroup(skillGroup);
            cardZhiBaoList.add(cZhiBao);
        }
        return cardZhiBaoList;
    }
    /**
     * 获取单张卡牌的至宝信息
     * @param cardId
     * @return
     */
    public static List<CardZhiBaoRandomRule> gainCardZhiBao(Integer cardId,List<String> cardZhiBaos) {
        return  gainCardZhiBaos(cardZhiBaos).stream().filter(tmp -> tmp.getCardId().equals(cardId)).collect(Collectors.toList());
    }

    /**
     * 根据灵装词条处理卡牌仙决
     * @param lingZhuangEntryLv
     * @param defaultCardXianJues
     * @return
     */
    public static List<CardXianJueRandomRule> instanceCardXianJueByEntryLv(Integer lingZhuangEntryLv, List<CardXianJueRandomRule> defaultCardXianJues){
        CfgLingZhuangEntryEntity zxzLingZhuangEntry = ZxzEntryTool.getZxzLingZhuangEntry(lingZhuangEntryLv);
        if (null == zxzLingZhuangEntry) {
            return defaultCardXianJues;
        }
        List<CardXianJueRandomRule> cardXianJueList = new ArrayList<>();
        for (CardXianJueRandomRule xianJue : defaultCardXianJues) {
            xianJue.setLevel(zxzLingZhuangEntry.getXianJueLv());
            xianJue.setQuality(zxzLingZhuangEntry.getQuality());
            Map<String, Integer> additions = xianJue.gainAdditions(xianJue.getXianJueType(),zxzLingZhuangEntry.getXianJueLv(), zxzLingZhuangEntry.getQuality(), 0, zxzLingZhuangEntry.getComprehension());
            xianJue.setAdditions(additions);
            cardXianJueList.add(xianJue);
        }
        return cardXianJueList;
    }



    /**
     * 根据灵装词条处理卡牌至宝
     * @param lingZhuangEntrylv
     * @param defaultCardZhiBaos
     * @return
     */
    public static List<CardZhiBaoRandomRule> instanceCardZhiBaoByEntryLv(Integer lingZhuangEntrylv, List<CardZhiBaoRandomRule> defaultCardZhiBaos){
        CfgLingZhuangEntryEntity zxzLingZhuangEntry = ZxzEntryTool.getZxzLingZhuangEntry(lingZhuangEntrylv);
        if (null == zxzLingZhuangEntry) {
            return defaultCardZhiBaos;
        }
        List<CardZhiBaoRandomRule> cardZhiBaoList = new ArrayList<>();
        for (CardZhiBaoRandomRule cardZhiBao : defaultCardZhiBaos) {
            Map<String, Integer> additions = cardZhiBao.gainAdditions(cardZhiBao.getZhiBaoId(),zxzLingZhuangEntry.getAttack(),zxzLingZhuangEntry.getDefense(), zxzLingZhuangEntry.getStrength(),zxzLingZhuangEntry.getTenacity());
            cardZhiBao.setAdditions(additions);
            cardZhiBaoList.add(cardZhiBao);
        }
        return cardZhiBaoList;
    }

}
