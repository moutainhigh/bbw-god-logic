package com.bbw.god.mall.skillscroll;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.game.config.treasure.TreasureType;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.biyoupalace.cfg.BYPalaceTool;
import com.bbw.god.gameuser.biyoupalace.cfg.CfgBYPalaceSkillEntity;
import com.bbw.god.gameuser.biyoupalace.cfg.Chapter;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.skillscroll.cfg.CfgDesignateSkillScroll;
import com.bbw.god.mall.skillscroll.cfg.CfgSkillScroll;
import com.bbw.god.mall.skillscroll.cfg.SkillScrollTool;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 技能卷轴service
 * @date 2021/2/2 16:44
 **/
@Service
public class SkillScrollService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserTreasureService userTreasureService;

    /**
     * 合成技能卷轴
     *
     * @param uid         玩家id
     * @param treasureIds 合成消耗的法宝id
     * @param type        合成类型
     * @param targetId    指定合成的法宝id
     * @return
     */
    public RDCommon synthesis(long uid, List<Integer> treasureIds, int type, Integer targetId) {
        // 检查
        check(uid, treasureIds, type, targetId);
        RDCommon rd = new RDCommon();
        // 获得篇章信息
        CfgTreasureEntity treasure = TreasureTool.getTreasureById(treasureIds.get(0));
        Integer chapter = getChapter(treasure);
        // 扣除元宝
        ResEventPublisher.pubGoldDeductEvent(uid, getNeedGold(chapter), WayEnum.SYNTHESIS_SKILL_SCROLL, rd);
        // 扣除法宝
        for (Integer treasureId : treasureIds) {
            TreasureEventPublisher.pubTDeductEvent(uid, treasureId, 1, WayEnum.SYNTHESIS_SKILL_SCROLL, rd);
        }
        // 候选的技能卷轴id集合
        List<Integer> candidateSkillScrolls = new ArrayList<>();
        SynthesisType synthesisType = SynthesisType.fromValue(type);
        int num = 1;
        switch (synthesisType) {
            case TYPE_1:
            case TYPE_JINSHEN_RANDOM:
                addCandidateSkillScrolls(chapter, candidateSkillScrolls);
                break;
            case TYPE_3:
                CfgSkillScroll cfgSkillScroll = SkillScrollTool.getSkillScroll(chapter);
                Integer probability = cfgSkillScroll.getProbability();
                // 合成一本随机高篇技能卷轴
                if (PowerRandom.hitProbability(probability)) {
                    chapter = chapter < Chapter.Five.getValue() ? chapter + 1 : Chapter.SB1.getValue();
                    addCandidateSkillScrolls(chapter, candidateSkillScrolls);
                    break;
                }
                // 合成两本随机本篇技能卷轴
                num++;
                addCandidateSkillScrolls(chapter, candidateSkillScrolls);
                break;
            case TYPE_2:
            case TYPE_4:
            case TYPE_JINSHEN_DESIGNATE:
                candidateSkillScrolls.add(targetId);
                break;
            default:
                break;
        }
        // 过滤自身和限定
        candidateSkillScrolls.removeAll(treasureIds);
        candidateSkillScrolls.removeAll(BYPalaceTool.getUniqueSkillScrollIds());
        // 从候选集合中随机挑num个，发放奖励
        List<Integer> randoms = PowerRandom.getRandomsFromList(num, candidateSkillScrolls);
        List<EVTreasure> evTreasures = randoms.stream().map(tmp -> new EVTreasure(tmp, 1)).collect(Collectors.toList());
        TreasureEventPublisher.pubTAddEvent(uid, evTreasures, WayEnum.SYNTHESIS_SKILL_SCROLL, rd);
        return rd;
    }

    /**
     * 添加候选的技能卷轴id
     *
     * @param chapter
     * @param candidateSkillScrolls
     */
    private void addCandidateSkillScrolls(Integer chapter, List<Integer> candidateSkillScrolls) {
        CfgDesignateSkillScroll designateSkillScroll = SkillScrollTool.getDesignateSkillScroll(chapter);
        candidateSkillScrolls.addAll(designateSkillScroll.getAbleDesignateIds());
        candidateSkillScrolls.addAll(designateSkillScroll.getUnableDesignateIds());
    }

    /**
     * 检查
     *
     * @param uid
     * @param treasureIds
     * @param type
     * @param targetId
     */
    private void check(long uid, List<Integer> treasureIds, int type, Integer targetId) {
        // 检查技能卷轴
        checkTreasure(uid, treasureIds, type, targetId);
        // 检查元宝
        CfgTreasureEntity treasure = TreasureTool.getTreasureById(treasureIds.get(0));
        int chapter = getChapter(treasure);
        checkGold(uid, chapter);
        // 检查合成类型
        checkSynthesisType(treasureIds, type, chapter);
    }

    /**
     * 检查合成类型
     *
     * @param treasureIds
     * @param type
     * @param chapter
     */
    private void checkSynthesisType(List<Integer> treasureIds, int type, int chapter) {
        SynthesisType synthesisType = SynthesisType.fromValue(type);
        if (null == synthesisType) {
            throw new ExceptionForClientTip("skill.scroll.synthesis.type.error");
        }
        switch (synthesisType) {
            case TYPE_1:
                if (chapter > 5 && treasureIds.size() < 3) {
                    throw new ExceptionForClientTip("skill.scroll.synthesis.error");
                }
                if (chapter <= 5 && 2 != treasureIds.size()) {
                    throw new ExceptionForClientTip("skill.scroll.synthesis.error");
                }
                break;
            case TYPE_JINSHEN_RANDOM:
            case TYPE_JINSHEN_DESIGNATE:
                break;
            default:
                if (3 != treasureIds.size()) {
                    throw new ExceptionForClientTip("skill.scroll.synthesis.error");
                }
                break;
        }
        // 该篇章的卷轴不支持该合成方式
        CfgSkillScroll cfgSkillScroll = SkillScrollTool.getSkillScroll(chapter);
        List<Integer> supportType = cfgSkillScroll.getSupportSynthesisType();
        if (!supportType.contains(type)) {
            throw new ExceptionForClientTip("skill.scroll.synthesis.type.error");
        }
    }

    /**
     * 检查元宝
     *
     * @param uid
     * @param chapter
     */
    private void checkGold(long uid, int chapter) {
        GameUser gu = gameUserService.getGameUser(uid);
        ResChecker.checkGold(gu, getNeedGold(chapter));
    }

    /**
     * 获取合成卷轴需要花费的元宝
     *
     * @param chapter
     * @return
     */
    private int getNeedGold(int chapter) {
        switch (chapter) {
            case 1:
                return 10;
            case 2:
                return 30;
            case 3:
                return 50;
            case 4:
                return 100;
            case 5:
                return 200;
            default:
                return 500;
        }
    }

    /**
     * 检查用来合成的卷轴
     *
     * @param treasureIds
     * @param targetId
     */
    private void checkTreasure(long uid, List<Integer> treasureIds, int type, Integer targetId) {
        // 消耗的技能卷轴不合法
        if (ListUtil.isEmpty(treasureIds) || treasureIds.size() < 2 || treasureIds.size() > 3) {
            throw new ExceptionForClientTip("skill.scroll.synthesis.error");
        }
        // 指定卷轴不能和消耗的一样
        if (targetId > 0 && treasureIds.contains(targetId)) {
            throw new ExceptionForClientTip("skill.scroll.designate.error");
        }
        CfgTreasureEntity treasure1 = TreasureTool.getTreasureById(treasureIds.get(0));
        CfgTreasureEntity treasure2 = TreasureTool.getTreasureById(treasureIds.get(1));
        CfgTreasureEntity treasure3 = treasureIds.size() == 3 ? TreasureTool.getTreasureById(treasureIds.get(2)) : null;
        // 有错误的法宝id
        if (null == treasure1 || null == treasure2 || (null == treasure3 && 3 == treasureIds.size())) {
            throw new ExceptionForClientTip("skill.scroll.synthesis.error");
        }
        // 有错误的法宝类型
        if (treasure1.getType() != TreasureType.SKILL_SCROLL.getValue() ||
                treasure2.getType() != TreasureType.SKILL_SCROLL.getValue() ||
                (null != treasure3 && treasure3.getType() != TreasureType.SKILL_SCROLL.getValue())) {
            throw new ExceptionForClientTip("skill.scroll.synthesis.error");
        }
        // 检查道具数量
        Map<Integer, List<Integer>> listMap = treasureIds.stream().collect(Collectors.groupingBy(tmp -> tmp));
        Set<Map.Entry<Integer, List<Integer>>> entries = listMap.entrySet();
        for (Map.Entry<Integer, List<Integer>> entry : entries) {
            Integer id = entry.getKey();
            int num = userTreasureService.getTreasureNum(uid, id);
            if (num < entry.getValue().size()) {
                throw new ExceptionForClientTip("treasure.not.enough", TreasureTool.getTreasureById(id).getName());
            }
        }
        if (treasure1.getId() == TreasureEnum.JIN_SHEN_SKILL_SCROLL.getValue()) {
            checkForJinS(treasure2, type, targetId);
        }
        // 判断是否都是同一篇的技能卷轴
        CfgBYPalaceSkillEntity skillEntity1 = BYPalaceTool.getBYPSkillEntity(treasure1.getName());
        CfgBYPalaceSkillEntity skillEntity2 = BYPalaceTool.getBYPSkillEntity(treasure2.getName());
        CfgBYPalaceSkillEntity skillEntity3 = null == treasure3 ? null : BYPalaceTool.getBYPSkillEntity(treasure3.getName());
        //金身碧游宫没有产出
        Integer chapter1 = getChapter(treasure1);
        Integer chapter2 = getChapter(treasure2); //skillEntity2.getChapter();
        Integer chapter3 = getChapter(treasure3); //treasure3 == null ? 0 : skillEntity3.getChapter();
        // 不是同一篇的技能卷轴
        if (!treasure1.getName().contains("金身") && !(chapter1.equals(chapter2) && (null == treasure3 || chapter1.equals(chapter3)))) {
            throw new ExceptionForClientTip("skill.scroll.need.some.chapter");
        }
        // 指定卷轴不在可选范围内
        if (targetId > 0 && !SkillScrollTool.getDesignateSkillScroll(chapter1).getAbleDesignateIds().contains(targetId)) {
            throw new ExceptionForClientTip("skill.scroll.can.not.designate");
        }
        // 指定本篇同属性卷轴 必须要消耗3个同属性同篇章的卷轴
        if (SynthesisType.TYPE_2.getValue() == type) {
            String type1 = skillEntity1.getType();
            String type2 = skillEntity2.getType();
            String type3 = null == treasure3 ? "" : skillEntity3.getType();
            // 属性不相同
            if (!(type1.equals(type2) && type2.equals(type3))) {
                throw new ExceptionForClientTip("skill.scroll.need.some.type");
            }
            // 未指定
            if (targetId <= 0) {
                throw new ExceptionForClientTip("skill.scroll.not.designate");
            }
            // 指定的和消耗的不是同属性
            char c = TreasureTool.getTreasureById(targetId).getName().charAt(0);
            if (!type1.equals(String.valueOf(c))) {
                throw new ExceptionForClientTip("skill.scroll.designate.need.some.type");
            }
        }
        // 随机高篇卷轴 必须要3个一样的卷轴
        if (SynthesisType.TYPE_3.getValue() == type) {
            String name1 = treasure1.getName();
            String name2 = treasure2.getName();
            String name3 = null == treasure3 ? "" : treasure3.getName();
            // 不是同一个卷轴
            if (!(name1.equals(name2) && name2.equals(name3))) {
                throw new ExceptionForClientTip("skill.scroll.need.some.skill");
            }
        }
        // 未指定
        if (SynthesisType.TYPE_4.getValue() == type && targetId <= 0) {
            throw new ExceptionForClientTip("skill.scroll.not.designate");
        }
    }

    private void checkForJinS(CfgTreasureEntity treasure2, int type, int targetId) {
        // 判断是否都是同一篇的技能卷轴
        CfgBYPalaceSkillEntity skillEntity2 = BYPalaceTool.getBYPSkillEntity(treasure2.getName());
//        CfgBYPalaceSkillEntity skillEntity3 = BYPalaceTool.getBYPSkillEntity(treasure3.getName());
        Integer chapter2 = skillEntity2.getChapter();
//        Integer chapter3 = skillEntity3.getChapter();
        //1本金身卷轴+1本五篇卷轴
        if (type == SynthesisType.TYPE_JINSHEN_RANDOM.getValue()) {
            if (chapter2 != Chapter.Five.getValue()) {
                throw new ExceptionForClientTip("skill.scroll.synthesis.unvalid");
            }
        }
        // 1本金身卷轴+1本秘传卷轴
        if (type == SynthesisType.TYPE_JINSHEN_DESIGNATE.getValue()) {
            if (chapter2 < Chapter.SB1.getValue()) {
                throw new ExceptionForClientTip("skill.scroll.synthesis.unvalid");
            }
            // 未指定
            if (targetId <= 0) {
                throw new ExceptionForClientTip("skill.scroll.not.designate");
            }
            // 指定卷轴不在可选范围内
            if (!SkillScrollTool.getDesignateSkillScroll(Chapter.SB1.getValue()).getAbleDesignateIds().contains(targetId)) {
                throw new ExceptionForClientTip("skill.scroll.can.not.designate");
            }
        }

    }

    private int getChapter(CfgTreasureEntity treasure) {
        int chapter = 0;
        if (null == treasure) {
            return chapter;
        }
        if (treasure.getId() == TreasureEnum.JIN_SHEN_SKILL_SCROLL.getValue()) {
            //金身卷轴不在秘传产出
            chapter = Chapter.SB1.getValue();
        } else {
            // 合成表配置的不可指定卷轴，新增 可以参与合成其他卷轴
            List<CfgDesignateSkillScroll> designateSkillScrolls = SkillScrollTool.getDesignateSkillScrolls();
            for (CfgDesignateSkillScroll skillScroll : designateSkillScrolls) {
                // 当前合成消耗卷轴是否为 合成表配置的不可指定卷轴
                if (skillScroll.getUnableDesignateIds().contains(treasure.getId())){
                    return skillScroll.getChapter();
                }
            }

            chapter = BYPalaceTool.getBYPSkillEntity(treasure.getName()).getChapter();
        }
        return chapter;
    }
}