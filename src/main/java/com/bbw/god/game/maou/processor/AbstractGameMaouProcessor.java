package com.bbw.god.game.maou.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.lock.RedisLockUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.maou.*;
import com.bbw.god.game.maou.cfg.CfgGameMaou;
import com.bbw.god.game.maou.cfg.GameMaouTool;
import com.bbw.god.game.maou.cfg.GameMaouType;
import com.bbw.god.game.maou.ctrl.*;
import com.bbw.god.game.maou.event.GameMaouEventPublisher;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * 跨服魔王业务处理类
 *
 * @author: suhq
 * @date: 2021/12/17 10:42 上午
 */
public abstract class AbstractGameMaouProcessor {
    @Autowired
    private GameMaouAttackerService gameMaouAttackerService;
    @Autowired
    private GameMaouJoinerService gameMaouJoinerService;
    @Autowired
    private GameMaouInfoService gameMaouInfoService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private RedisLockUtil redisLockUtil;

    /**
     * 获取魔王信息
     *
     * @param uid
     * @return
     */
    public RDGameMaouInfo getMaou(long uid, IActivity activity) {
        GameMaouType gameMaouType = GameMaouType.fromActivity(activity.gainType());
        //魔王信息
        Integer curTurn = gameMaouInfoService.getCurTurn(activity);
        CfgGameMaou.GameMaouInfo maouConfig = GameMaouTool.getMaouConfig(gameMaouType).getMaouInfo(curTurn);
        boolean hasMaou = gameMaouInfoService.hasMaou(activity);
        //初始化魔王
        if (!hasMaou) {
            gameMaouInfoService.init(activity, maouConfig.getInitBlood());
        }

        //攻击者信息
        GameMaouAttacker attacker = gameMaouAttackerService.getOrCreateAttacker(uid, activity);
        //血量信息
        Integer totalBlood = maouConfig.getInitBlood(curTurn);
        Integer remainBlood = getRemainBloodToShow(activity);
        //魔王开始还有多少时间
        long remainTimeToBegin = gameMaouInfoService.getRemainTimeToBegin(activity);

        RDGameMaouInfo rd = RDGameMaouInfo.getInstance(totalBlood, remainBlood, attacker);
        rd.setMaouId(maouConfig.getId());
        rd.setMaouType(gameMaouType.getValue());
        rd.setRemainTimeToBegin(remainTimeToBegin);
        rd.setCurTurn(curTurn);
        return rd;
    }

    /**
     * 获取血量信息
     *
     * @param
     * @return
     */
    public RDGameMaouBloodInfo getBloodInfo(IActivity activity) {
        long remainTimeToBegin = gameMaouInfoService.getRemainTimeToBegin(activity);
        Integer remainBlood = gameMaouInfoService.getRemainBlood(activity);
        if (remainTimeToBegin > 0) {
            remainBlood = 0;
        }
        RDGameMaouBloodInfo rd = new RDGameMaouBloodInfo();
        rd.setRemainBlood(remainBlood);
        rd.setRemainTimeToBegin(remainTimeToBegin);
        return rd;
    }

    /**
     * 魔王编组
     *
     * @param uid
     * @param maouCards
     * @return
     */
    public RDSuccess setMaouCards(long uid, String maouCards, IActivity activity) {
        GameMaouType gameMaouType = GameMaouType.fromActivity(activity.gainType());

        List<Integer> cardIds = ListUtil.parseStrToInts(maouCards);
        CfgGameMaou maouConfig = GameMaouTool.getMaouConfig(gameMaouType);
        //判断卡牌的数量
        if (cardIds.size() > maouConfig.getCardLimit()) {
            throw new ExceptionForClientTip("maouboss.card.outOfLimit", maouConfig.getCardLimit());
        }

        // 判断卡牌的有效性
        List<UserCard> cards = userCardService.getUserCards(uid, cardIds);
        if (cards.size() != cardIds.size()) {
            throw new ExceptionForClientTip("maouboss.card.unvalid");
        }
        //更新数据
        GameMaouAttacker attacker = gameMaouAttackerService.getOrCreateAttacker(uid, activity);
        attacker.setCards(cardIds);
        gameMaouAttackerService.updateAttacker(attacker, activity);
        return new RDSuccess();
    }

    /**
     * 领取目标奖励
     *
     * @param uid
     * @return
     */
    public RDGameMaouAward getTargetAward(long uid, IActivity activity) {
        GameMaouAttacker attacker = gameMaouAttackerService.getOrCreateAttacker(uid, activity);
        GameMaouAttacker.Target attackerTarget = attacker.gainCurTarget();
        if (attackerTarget.getStatus() == AwardStatus.UNAWARD.getValue()) {
            throw ExceptionForClientTip.fromi18nKey("maou.game.target.not.achieved");
        }
        if (attackerTarget.getStatus() == AwardStatus.AWARDED.getValue()) {
            throw ExceptionForClientTip.fromi18nKey("maou.game.target.awarded");
        }
        //更新攻击者目标魔王奖励为已领取
        int targetId = attackerTarget.getTargetId();
        attacker.updateTargetToAwarded(targetId);
        gameMaouAttackerService.updateAttacker(attacker, activity);

        RDGameMaouAward rd = RDGameMaouAward.getInstance(attacker);
        GameMaouType gameMaouType = GameMaouType.fromActivity(activity.gainType());
        CfgGameMaou.GameMaouTarget maouTarget = GameMaouTool.getMaouConfig(gameMaouType).getMaouTarget(targetId);
        awardService.fetchAward(uid, maouTarget.getAwards(), WayEnum.MAOU_GAME, "", rd);
        return rd;
    }

    /**
     * 攻打魔王
     *
     * @param uid
     * @return
     */
    public RDGameMaouAttack attack(long uid, CPGameMaouAttack attackParam, IActivity activity) {
        //检查魔王开始攻打的时间
        checkRemainTimeToBegin(activity);
        int beatedBlood = getBeatedBlood(uid, attackParam, activity);
        return handleBlood(uid, beatedBlood, activity);
    }

    /**
     * 处理魔王血量
     *
     * @param uid
     * @param beatedBlood
     * @param activity
     * @return
     */
    private RDGameMaouAttack handleBlood(long uid, int beatedBlood, IActivity activity) {
        //攻击者信息
        GameMaouAttacker attacker = gameMaouAttackerService.getOrCreateAttacker(uid, activity);
        //预备下一轮的数据
        //当前轮次
        Integer maouTurn = gameMaouInfoService.getCurTurn(activity);
        GameMaouType gameMaouType = GameMaouType.fromActivity(activity.gainType());
        CfgGameMaou maouConfig = GameMaouTool.getMaouConfig(gameMaouType);
        Integer nextTurn = maouTurn + 1;
        Integer initBlood = maouConfig.getMaouInfo(nextTurn).getInitBlood(nextTurn);
        Date nextBeginTime = DateUtil.addSeconds(DateUtil.now(), maouConfig.getIntervalSeconds());
        long nextBeginTimeLong = DateUtil.toDateTimeLong(nextBeginTime);
        // 并发情况下，代码从检查状态完毕执行到这里，魔王状态可能已经被改变，需要在同步锁内再判定一下
        // ----同步锁开始----
        String key = GameMaouRedisKeys.getMaouLockKey(activity);
        boolean isKilled = (boolean) redisLockUtil.doSafe(key, obj -> {
//            long begin = System.currentTimeMillis();
            //检查开始时间
            checkRemainTimeToBegin(activity);
            Integer remainBlood = gameMaouInfoService.getRemainBlood(activity);
            int lostBlood = Math.min(beatedBlood, remainBlood);
            // 魔王失血
            remainBlood = remainBlood - lostBlood;
            gameMaouInfoService.updateBlood(activity, remainBlood);
            // 更新魔王状态
            if (remainBlood > 0) {
                return false;
            }
            gameMaouInfoService.toNextTurn(activity, initBlood, nextBeginTimeLong);
//            System.out.println(System.currentTimeMillis() + "-----------time：" + (System.currentTimeMillis() - begin));
            return true;
        });
        // ----同步锁结束----
        // 保存攻打信息
        attacker.updateAttackInfo(beatedBlood);
        gameMaouAttackerService.updateAttacker(attacker, activity);
        //加入本轮魔王参与者集合
        gameMaouJoinerService.join(uid, activity, maouTurn);

        int remainBlood = getRemainBloodToShow(activity);
        RDGameMaouAttack rd = RDGameMaouAttack.getInstance(beatedBlood, remainBlood, attacker);
        long remainTimeToBegin = gameMaouInfoService.getRemainTimeToBegin(activity);
        rd.setRemainTimeToBegin(remainTimeToBegin);
        //发布魔王击杀事件
        if (isKilled) {
            GameMaouEventPublisher.pubKilledEvent(uid, activity, maouTurn);
        }
        GameMaouEventPublisher.pubAttackMaouEvent(uid, beatedBlood);
        return rd;
    }

    /**
     * 获取剩余血量用于展示
     *
     * @return
     */
    private int getRemainBloodToShow(IActivity activity) {
        Integer remainBlood = gameMaouInfoService.getRemainBlood(activity);
        if (gameMaouInfoService.getRemainTimeToBegin(activity) > 0) {
            remainBlood = 0;
        }
        return remainBlood;
    }

    /**
     * 检查魔王开始攻打的时间
     *
     * @param activity
     */
    private void checkRemainTimeToBegin(IActivity activity) {
        long remainTimeToBegin = gameMaouInfoService.getRemainTimeToBegin(activity);
        if (remainTimeToBegin > 0) {
            throw new ExceptionForClientTip("maou.game.turn.not.start", remainTimeToBegin / 1000);
        }
    }

    /**
     * 获取本次攻击的血量
     *
     * @return
     */
    public abstract int getBeatedBlood(long uid, CPGameMaouAttack attackParam, IActivity activity);

    /**
     * 发放击杀奖励邮件
     *
     * @param killer
     * @param activity
     * @param turn
     */
    public abstract void sendKilledAward(long killer, IActivity activity, int turn);

    public abstract boolean isMatch(GameMaouType gameMaouType);
}
