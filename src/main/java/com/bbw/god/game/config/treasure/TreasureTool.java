package com.bbw.god.game.config.treasure;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import com.bbw.god.game.config.Cfg;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 玩家无关的法宝方法
 *
 * @author suhq
 * @date 2018年10月22日 上午11:14:39
 */
public class TreasureTool {
    private static Map<String, CfgTreasureEntity> nameTreasureMap = new HashMap<>();

    public static void init() {
        List<CfgTreasureEntity> treasureEntities = getAllTreasures();
        for (CfgTreasureEntity treasure : treasureEntities) {
            nameTreasureMap.put(treasure.getName(), treasure);
        }
    }


    public static CfgTreasureEntity getTreasureById(int treasureId) {
        return Cfg.I.get(treasureId, CfgTreasureEntity.class);
    }

    public static List<CfgTreasureEntity> getTreasureById(List<Integer> treasureIds) {
        List<CfgTreasureEntity> treasureEntities = new ArrayList<>();
        for (Integer treasureId : treasureIds) {
            treasureEntities.add(getTreasureById(treasureId));
        }
        return treasureEntities;
    }

    public static CfgTreasureEntity getTreasureByName(String name) {
        if (nameTreasureMap.keySet().size() == 0) {
            init();
        }
        if ("水-崇侯虎-5级-金刚-金罩".equals(name)){
            name = "神·崇侯虎-5级-金刚-金罩";
        }
        CfgTreasureEntity entity = nameTreasureMap.get(name);
        if (entity == null) {
            throw CoderException.high("无效的法宝" + name);
        }
        return entity;
    }

    public static CfgTreasureEntity getRandomFightTreasure() {
        List<CfgTreasureEntity> treasures = getAllTreasures();
        List<CfgTreasureEntity> fightTreasures = treasures.stream().filter(tmp -> tmp.getType() == TreasureType.FIGHT_TREASURE.getValue()).collect(Collectors.toList());
        return PowerRandom.getRandomFromList(fightTreasures);
    }

    public static List<CfgTreasureEntity> getAllTreasures() {
        return Cfg.I.get(CfgTreasureEntity.class);
    }

    public static CfgTreasure getTreasureConfig() {
        return Cfg.I.getUniqueConfig(CfgTreasure.class);
    }


    public static List<CfgSkillScrollLimitEntity> getAllSkillScrollLimitEntity() {
        return ListUtil.copyList(Cfg.I.get(CfgSkillScrollLimitEntity.class), CfgSkillScrollLimitEntity.class);
    }

    public static CfgSkillScrollLimitEntity getSkillScrollLimitEntity(int id) {
        return Cfg.I.get(id, CfgSkillScrollLimitEntity.class);
    }

    public static CfgSkillScrollLimitEntity getSkillScrollLimitEntity(int skillId, int type, int cardId, int levelIndex) {
        if (skillId == 0) {
            return null;
        }
        List<CfgSkillScrollLimitEntity> skillScrollLimitEntities = Cfg.I.get(CfgSkillScrollLimitEntity.class);
        return skillScrollLimitEntities.stream().filter(tmp -> tmp.match(skillId, type, cardId, levelIndex)).findFirst().orElse(null);
    }

    /**
     * 获取特定类型的所有道具
     * @param type
     * @return
     */
    public static List<CfgTreasureEntity> getTreasuresByType(int type){
        return getTreasureConfig().getTreasureByType().get(type);
    }
    /**
     * 获得随机start星级法宝
     *
     * @param star 星级
     * @return
     */
    public static CfgTreasureEntity getRandomOldTreasure(int star) {
        List<CfgTreasureEntity> starTreasures = getTreasureConfig().getOldTreasureMapByStar().get(star);
        return PowerRandom.getRandomFromList(starTreasures);

    }

    /**
     * 获得指定start星级所有法宝
     *
     * @param star 星级
     * @return
     */
    public static List<CfgTreasureEntity> getTreasureByStar(int star) {
        return TreasureTool.getAllTreasures().stream().filter(t -> t.getStar() != null && t.getStar() == star).collect(Collectors.toList());
    }

    /**
     * 获得随机start星级法宝,包含灵石
     *
     * @param star 星级
     * @return
     */
    private static CfgTreasureEntity getRandomGood(int star) {
        List<CfgTreasureEntity> treasures = getTreasureConfig().getTreasureMapByStar().get(star);
        int random = PowerRandom.getRandomBySeed(treasures.size() + 1);
        if (random <= treasures.size()) {
            return treasures.get(random - 1);
        } else {
            return getTreasureById(800 + 10 * star);
        }
    }

    /**
     * 获得一定概率的随机法宝 如概率为20/0/0，对应的rand5=0,rand4=0,rand3=200
     *
     * @param rand5 五星概率
     * @param rand4 四星概率
     * @param rand3 三星概率
     * @return
     */
    public static CfgTreasureEntity getRandomOldTreasure(int rand5, int rand4, int rand3) {
        int star = getRandomStar(rand5, rand4, rand3);
        return getRandomOldTreasure(star);
    }

    /**
     * 获得一定概率的随机法宝， 如一级城一级炼宝炉的概率为20/0/0，对应的rand5=0,rand4=0,rand3=200
     *
     * @param rand5 五星概率
     * @param rand4 四星概率
     * @param rand3 三星概率
     * @return
     */
    public static CfgTreasureEntity getRandomGood(int rand5, int rand4, int rand3) {
        int star = getRandomStar(rand5, rand4, rand3);
        return getRandomGood(star);
    }

    /**
     * 随机星级
     *
     * @param rand5
     * @param rand4
     * @param rand3
     * @return
     */
    public static int getRandomStar(int rand5, int rand4, int rand3) {
        int random = PowerRandom.getRandomBySeed(1000);
        int star = 0;// 获得卡牌的星级
        if (random <= rand5) {// 五星
            star = 5;
        } else if (random <= rand5 + rand4) {// 四星
            star = 4;
        } else if (random <= rand5 + rand4 + rand3) {// 三星
            star = 3;
        } else {// 一二星
            random = PowerRandom.getRandomBySeed(2);
            if (random == 2) {
                star = 2;
            } else {
                star = 1;
            }

        }
        return star;
    }

    /**
     * 获取所有头像框id集
     *
     * @return
     */
    public static List<Integer> getAllIconIds() {
        List<CfgTreasureEntity> treasures = getAllTreasures();
        return treasures.stream().filter(p -> p.getType() == TreasureType.HEAD_BOX.getValue()).map(CfgTreasureEntity::getId).collect(Collectors.toList());
    }

    /**
     * 获取所有头像id集
     *
     * @return
     */
    public static List<Integer> getAllHeadIds() {
        List<CfgTreasureEntity> treasures = getAllTreasures();
        return treasures.stream().filter(p -> p.getType() == TreasureType.HEAD.getValue()).map(CfgTreasureEntity::getId).collect(Collectors.toList());
    }

    /**
     * 获取战斗法宝的ID集合
     *
     * @return
     */
    public static List<Integer> getFightTreasureIds() {
        List<CfgTreasureEntity> treasures = getAllTreasures();
        return treasures.stream().filter(p -> p.getType() == TreasureType.FIGHT_TREASURE.getValue()).map(CfgTreasureEntity::getId).collect(Collectors.toList());
    }

    /**
     * 获取地图法宝的ID集合
     *
     * @return
     */
    public static List<Integer> getMapTreasureIds() {
        List<CfgTreasureEntity> treasures = getAllTreasures();
        return treasures.stream().filter(p -> p.getType() == TreasureType.MAP_TREASURE.getValue()).map(CfgTreasureEntity::getId).collect(Collectors.toList());
    }

    /**
     * 获取技能卷轴id
     *
     * @param type            卡牌属性
     * @param skillId         技能id
     * @param targetCardLimit 卡牌ID
     * @return
     */
    public static int getSkillScrollId(int type, int skillId, int targetCardLimit) {
        List<CfgSkillScrollLimitEntity> list = getAllSkillScrollLimitEntity();
        list = list.stream().filter(p -> p.match(skillId, type, targetCardLimit)).collect(Collectors.toList());
        CfgSkillScrollLimitEntity entity = list.get(0);
        if (list.size() > 1) {
            Optional<CfgSkillScrollLimitEntity> op = list.stream()
                    .filter(p -> !p.getLimitCards().isEmpty() && p.getLimitCards().contains(targetCardLimit))
                    .findFirst();
            if (op.isPresent()) {
                entity = op.get();
            } else {
                op = list.stream()
                        .filter(p -> !p.getLimitTypes().isEmpty() && p.getLimitTypes().contains(type))
                        .findFirst();
                if (op.isPresent()) {
                    entity = op.get();
                }
            }

        }
        return entity.getId();
    }
}
