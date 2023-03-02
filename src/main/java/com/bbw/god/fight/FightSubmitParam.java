package com.bbw.god.fight;

import com.bbw.common.ListUtil;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.game.combat.data.card.BattleCard;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 战斗提交参数
 *
 * @author suhq
 * @date 2018年11月5日 上午10:30:36
 */
@Data
public class FightSubmitParam {
    private Long combatId;//战斗ID
    private int win = 0;
    /** 己方等级 */
    private int beginBlood = 0;
    /** 己方损血 */
    private int lostBlood;
    /** 使用法宝次数 */
    private int weaponUsedNum = 0;
    /** 己方被击杀的卡牌 */
    private List<SubmitCardParam> killedCards = new ArrayList<>();
    //    private boolean notLostBlood;// 是否损血
//    private boolean notLostCard;
    private boolean isFightAgain;// 是否重新战斗
    private Long opponentId;// 对手ID
    private String opponentName;// 对手昵称
    private Long monsterId;// 好友野怪ID
    private Long fightTaskId;// 战斗任务ID（村庄）
    private Integer fightType;// 战斗类型
    private Integer round = 1;// 当前回合
    private YeGuaiEnum yeGuaiType;
    /** 对手被击杀的卡牌 */
    private List<SubmitCardParam> oppKilledCards = new ArrayList<>();
    private int oppLv = 0;
    /** 对手初始血量 */
    private int oppBeginBlood = 0;
    private int oppLostBlood = 0;
    private int zcTimes = 0;
    /** 加成经验 */
    private int additionExp = 0;
    private boolean finishedTask = false;//是否完成额外的任务（野怪）
    private Integer newerGuide = null;// 新手引导进度，null的时候表示已经通过
    private boolean gainJYD = false;

    public void setOppKilledCards(List<BattleCard> oppKilledCards) {
        if (ListUtil.isEmpty(oppKilledCards)) {
            this.oppKilledCards = new ArrayList<>();
            return;
        }
        List<SubmitCardParam> oppKilleds = oppKilledCards.stream().map(tmp -> SubmitCardParam.instance(tmp)).collect(Collectors.toList());
        this.oppKilledCards = oppKilleds;
    }

    public void setKilledCards(List<BattleCard> killedCards) {
        if (ListUtil.isEmpty(killedCards)) {
            this.killedCards = new ArrayList<>();
            return;
        }
        List<SubmitCardParam> killeds = killedCards.stream().map(tmp -> SubmitCardParam.instance(tmp)).collect(Collectors.toList());
        this.killedCards = killeds;
    }

    public boolean isNotLostCard() {
        return ListUtil.isEmpty(killedCards);
    }

    public boolean isNotLostBlood() {
        return lostBlood <= 0;
    }

    /**
     * 战斗回合评分
     *
     * @return
     */
    public int gainRoundScore() {
        int round = getRound() - 1;
        int score = 15;
        if (round > 7) {
            score = score - (round - 7);
            score = score > 0 ? score : 0;
        }
        return score;
    }

    /**
     * 使用法宝评分
     *
     * @return
     */
    public int gainWeaponScore() {
        int weaponUsedNum = getWeaponUsedNum();
        int score = 5;
        if (weaponUsedNum > 0) {
            score = score - weaponUsedNum;
            score = score > 0 ? score : 0;
        }
        return score;
    }

    /**
     * 剩余血量评分
     *
     * @return
     */
    public int gainRemainBloodScore() {
        int maxScore = 15;
        int score = 15;
        if (lostBlood != 0) {
            int remainRate = 100 - (int) (lostBlood * 1.0 / beginBlood * 100);
            score = score - ((100 - remainRate) / 3 + 1);
            score = score > 0 ? score : 0;
        }
        return score > maxScore ? maxScore : score;
    }

    /**
     * 死亡神将评分
     *
     * @return
     */
    public int gainKilledScore() {
        int killedNum = getKilledCards().size();
        int score = 15;
        if (killedNum > 1) {
            score = score - (killedNum - 1) * 2;
            score = score > 0 ? score : 0;
        }
        return score;
    }

    /**
     * 击杀神将评分
     *
     * @return
     */
    public int gainOppRemainScore() {
        int oppKilledNum = getOppKilledCards().size();
        int score = oppKilledNum * 2;
        score = score > 30 ? 30 : score;
        return score;
    }

    /**
     * 扣除血量评分
     *
     * @return
     */
    public int gainOppRemainBloodScore() {
        int oppRemainRate = (int) (oppLostBlood * 1.0 / oppBeginBlood * 100);
        int score = 0;
        if (oppRemainRate > 0) {
            score = score + (oppRemainRate / 5);
            score = score < 20 ? score : 20;
        }
        return score;
    }

    @Data
    public static class SubmitCardParam {
        private int id;
        private int lv;
        private int hv;
        private int star;

        public static SubmitCardParam instance(BattleCard card) {
            SubmitCardParam param = new SubmitCardParam();
            param.setId(card.getImgId());
            param.setLv(card.getLv());
            param.setHv(card.getHv());
            param.setStar(card.getStars());
            return param;
        }
    }

}
