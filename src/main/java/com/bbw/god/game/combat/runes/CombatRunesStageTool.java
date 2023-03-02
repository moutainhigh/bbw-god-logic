package com.bbw.god.game.combat.runes;

import com.bbw.common.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author：lwb
 * @date: 2020/12/8 14:53
 * @version: 1.0
 */
public class CombatRunesStageTool {
    private static List<CombatStage> stages = new ArrayList<>(0);

    private static CombatStage findCombatStage(String key) {
        if (stages.size() == 0 || StrUtil.isBlank(key)) {
            return null;
        }
        Optional<CombatStage> optional = stages.stream().filter(p ->null != p && key.equals(p.getKey())).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    /**
     * 战斗参数构建阶段
     *
     * @return
     */
    public static CombatStage getCombatParamInitRunes() {
        CombatStage combatRunes = findCombatStage("getCombatParamInitRunes");
        if (combatRunes == null) {
            int[] runes = {131580, 131590, 131600, 231302, 131200, 231401, 331409, 332001,333001};
            combatRunes = new CombatStage("getCombatParamInitRunes", runes);
            stages.add(combatRunes);
        }
        return combatRunes;
    }

    /**
     * 初始话战斗需要执行的符文
     *
     * @return
     */
    public static CombatStage getInitCombatRunes() {
        CombatStage combatRunes = findCombatStage("getInitCombatRunes");
        if (combatRunes == null) {
            /**
             * 泰体符,飞仙符,禁法符,蛊惑符,得道符,健体符,升仙符,缓速符,迟缓符,玩家攻击/防御/血量符图
             */
            int[] runes = {
                    900001,331209,
                    134001, 134002, 134003, 134004, 134005,
                    131010, 131120, 131100, 131150, 131180, 131490, 131520, 131530, 131550, 131570,
                    232001, 232002, 232003, 232004, 232005, 232101, 232102, 232103, 232104, 232105, 232201, 232202, 232203, 232204, 232205, 232301, 232302, 232303, 232304, 232305, 232401, 232402, 232403, 232404, 232405,
                    233001, 233002, 233003, 233004, 233005, 233101, 233102, 233103, 233104, 233105, 233201, 233202, 233203, 233204, 233205, 233301, 233302, 233303, 233304, 233305, 233401, 233402, 233403, 233404, 233405,
                    234001, 234002, 234003, 234004, 234005,
                    231001, 231002, 231003, 231005, 231006, 231007, 231008, 231004, 231009, 231010, 231011,
                    231101, 231102, 231107, 231108, 231109,
                    231203, 231205, 231206,
                    231301, 231303, 231306,
                    231404,
                    331101,
                    331409,
                    332002
            };
            combatRunes = new CombatStage("getInitCombatRunes", runes);
            stages.add(combatRunes);
        }
        return combatRunes;
    }

    /**
     * 获取己方全体获得XX 技能的符文
     * @return
     */
    public static CombatStage getAddSkillRunes(){
        CombatStage combatRunes = findCombatStage("getAddSkillRunes");
        if (combatRunes==null){
            int[] runes={131360,131070,131240,131250,131370,131110,131060,131390,131410,131440,131450,131460,131080,131020,131140};
            combatRunes=new CombatStage("getAddSkillRunes",runes);
            stages.add(combatRunes);
        }
        return combatRunes;
    }

    /**
     * 获取每回合开始都会执行的符文
     *
     * @return
     */
    public static CombatStage getRoundBeginRunes() {
        CombatStage combatRunes = findCombatStage("getRoundBeginRunes");
        if (combatRunes == null) {
            int[] runes = {
                    131050, 131130, 131170, 131210, 131220, 131280, 131320, 131340, 131030, 131420, 131480, 131500, 131510, 131660, 131690, 131610,
                    231201, 231105, 231104, 231103, 231207, 231208, 231111, 231304, 231307, 231405,
                    331103, 331104,
                    331201, 331202, 331203, 331204, 331205, 331206,
                    331304, 331305, 331306, 331307, 331308,
                    331406,
                    332011,
                    333106,
                    333202, 333204, 333205,
                    333302
            };
            combatRunes = new CombatStage("getRoundBeginRunes", runes);
            stages.add(combatRunes);
        }
        return combatRunes;
    }

    /**
     * 死亡技能执行前触发
     *
     * @return
     */
    public static CombatStage getBeforeDieSectionRunes() {
        CombatStage combatRunes = findCombatStage("getBeforeDieSectionRunes");
        if (combatRunes == null) {
            int[] runes = {333206};
            combatRunes = new CombatStage("getBeforeDieSectionRunes", runes);
            stages.add(combatRunes);
        }
        return combatRunes;
    }

    /**
     * 进入坟场时执行的符文
     * @return
     */
    public static CombatStage getIntoDiscardRunes(){
        CombatStage combatRunes = findCombatStage("getIntoDiscardRunes");
        if (combatRunes == null) {
            //陷仙符、刚烈符、招魂符
            int[] runes = {131290, 131350, 131040, 231402,
                    331403, 331404,
                    333110,
                    333301
            };
            combatRunes = new CombatStage("getIntoDiscardRunes", runes);
            stages.add(combatRunes);
        }
        return combatRunes;
    }

    /**
     * 离开战场触发
     *
     * @return
     */
    public static CombatStage getLeaveBattleRunes() {
        CombatStage combatRunes = findCombatStage("getLeaveBattleRunes");
        if (combatRunes == null) {
            int[] runes = {331405};
            combatRunes = new CombatStage("getLeaveBattleRunes", runes);
            stages.add(combatRunes);
        }
        return combatRunes;
    }

    /**
     * 召唤师血量变动时发动的符文
     *
     * @return
     */
    public static CombatStage getAttackPlayerRunes() {
        CombatStage combatRunes = findCombatStage("getAttackPlayerRunes");
        if (combatRunes == null) {
            int[] runes = {};
            combatRunes = new CombatStage("getAttackPlayerRunes", runes);
            stages.add(combatRunes);
        }
        return combatRunes;
    }

    /**
     * 召唤师血量变动时发动的符文
     * @return
     */
    public static CombatStage getPlayerHpChangeRunes(){
        CombatStage combatRunes = findCombatStage("getPlayerHpChangeRunes");
        if (combatRunes==null){
            //戮仙符、永生符、巫术符、亡命符、宝鉴符
            int[] runes = {131330, 131400, 131470, 131540, 131300,
                    231403,
                    331102,
                    332003,
                    333105,
                    333208,
                    900002
            };
            combatRunes = new CombatStage("getPlayerHpChangeRunes", runes);
            stages.add(combatRunes);
        }
        return combatRunes;
    }

    /**
     * 物理攻击前发动的符文
     *
     * @return
     */
    public static CombatStage getBeforeNormalAttackRunes() {
        CombatStage combatRunes = findCombatStage("getBeforeNormalAttackRunes");
        if (combatRunes == null) {
            //戮仙符、巫术符、亡命符、宝鉴符
            int[] runes = {131090, 131230, 131204,
                    231106, 231202, 231204,
                    331105,
                    332009,
                    333209
            };
            combatRunes = new CombatStage("getBeforeNormalAttackRunes", runes);
            stages.add(combatRunes);
        }
        return combatRunes;
    }

    /**
     * 物理攻击buff后发动的符文
     *
     * @return
     */
    public static CombatStage getAfterAttackBuffRunes() {
        CombatStage combatRunes = findCombatStage("getAfterAttackBuffRunes");
        if (combatRunes == null) {
            int[] runes = {231110,
                    331107, 331108,
                    331207,
                    332010,
                    333107
            };
            combatRunes = new CombatStage("getAfterAttackBuffRunes", runes);
            stages.add(combatRunes);
        }
        return combatRunes;
    }

    /**
     * 物理防御前
     *
     * @return
     */
    public static CombatStage getBeforeNormalNormalDefenceRunes() {
        CombatStage combatRunes = findCombatStage("getBeforeNormalNormalDefenceRunes");
        if (combatRunes == null) {
            int[] runes = {
                    331301, 331302,
                    331402
            };
            combatRunes = new CombatStage("getBeforeNormalNormalDefenceRunes", runes);
            stages.add(combatRunes);
        }
        return combatRunes;
    }

    /**
     * 接受普通攻击后
     *
     * @return
     */
    public static CombatStage getAfterAcceptAttackRunes() {
        CombatStage combatRunes = findCombatStage("getAfterAcceptAttackRunes");
        if (combatRunes == null) {
            int[] runes = {
                    331208, 331303,
                    333109,
                    333210
            };
            combatRunes = new CombatStage("getAfterAcceptAttackRunes", runes);
            stages.add(combatRunes);
        }
        return combatRunes;
    }

    /**
     * 回合结束初始化前执行的=》天劫
     *
     * @return
     */
    public static CombatStage getRoundEndRunes() {
        CombatStage combatRunes = findCombatStage("getRoundEndRunes");
        if (combatRunes == null) {
            int[] runes = {131430, 231405, 331407, 331408, 333208};
            combatRunes = new CombatStage("getRoundEndRunes", runes);
            stages.add(combatRunes);
        }
        return combatRunes;
    }

    /**
     * 回合初始化时执行的=》重置手牌后执行
     * @return
     */
    public static CombatStage getInitRoundRunes(){
        CombatStage combatRunes = findCombatStage("getInitRoundRunes");
        if (combatRunes==null){
            //群嘲符、缓速符
            int[] runes = { 131260,131560};
            combatRunes=new CombatStage("getInitRoundRunes",runes);
            stages.add(combatRunes);
        }
        return combatRunes;
    }

    /**
     * 释放技能时候执行的
     * @return
     */
    public static CombatStage getPerformSkillRunes(){
        CombatStage combatRunes = findCombatStage("getPerformSkillRunes");
        if (combatRunes == null) {
            //克火符、避雷符、自毙符
            int[] runes = {
                    131190, 131380, 131270, 131620, 131630, 131640, 131650, 131670, 131680,
                    231001, 231002, 231003, 231005, 231006, 231007, 231008, 231004,
                    231102, 231203, 231301,
                    331001, 331002, 331003, 331004, 331005, 331006, 331007, 331008, 331009,
                    331106,
                    331401,
                    332004, 332005, 332006, 332007, 332008,
                    333101, 333102, 333103, 333104, 333108, 333109,
                    333201, 333203, 333207, 333208, 333211, 333212, 333213, 333214, 333215,
                    333303, 333304, 333305
            };
            combatRunes = new CombatStage("getPerformSkillRunes", runes);
            stages.add(combatRunes);
        }
        return combatRunes;
    }

    /**
     * 法术防御前
     *
     * @return
     */
    public static CombatStage getBeforeSkillDefenceRunes() {
        CombatStage combatRunes = findCombatStage("getBeforeSkillDefenceRunes");
        if (combatRunes == null) {
            int[] runes = {331402};
            combatRunes = new CombatStage("getBeforeSkillDefenceRunes", runes);
            stages.add(combatRunes);
        }
        return combatRunes;
    }

}