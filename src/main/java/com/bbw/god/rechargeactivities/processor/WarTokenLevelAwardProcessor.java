package com.bbw.god.rechargeactivities.processor;

import com.bbw.common.CloneUtil;
import com.bbw.common.DateUtil;
import com.bbw.common.JSONUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.rechargeactivities.RDRechargeActivity;
import com.bbw.god.rechargeactivities.RDWarToken;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import com.bbw.god.rechargeactivities.wartoken.CfgWarTokenBigAwards;
import com.bbw.god.rechargeactivities.wartoken.CfgWarTokenLevelAward;
import com.bbw.god.rechargeactivities.wartoken.UserWarToken;
import com.bbw.god.rechargeactivities.wartoken.WarTokenTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 说明： 战令-奖励
 *
 * @author lwb
 * date 2021-06-02
 */
@Slf4j
@Service
public class WarTokenLevelAwardProcessor extends AbstractWarTokenProcessor {
    @Override
    public RechargeActivityItemEnum getCurrentEnum() {
        return RechargeActivityItemEnum.WAR_TOKEN_LEVEL_AWARD;
    }

    /**
     * 需要传递奖励内容的  在此处配置即可
     * 如1级奖励 以服务端的为准  则 SpecialAwardLevels 里面加1，否则就按客户端配置的默认奖励显示
     */
    private static final List<Integer> SpecialAwardLevels = new ArrayList<>();

    @Override
    public RDWarToken listAwards(long uid) {
        UserWarToken userWarToken = getUserWarToken(uid);
        List<CfgWarTokenLevelAward> levelAwards = WarTokenTool.getLevelAwards();
        List<RDWarToken.LevelAwardStatus> rdList = new ArrayList<>();
        for (CfgWarTokenLevelAward levelAward : levelAwards) {
            AwardStatus status = AwardStatus.UNAWARD;
            if (userWarToken.ifGainedAward(levelAward.getTokenLevel())) {
                //已领取
                status = AwardStatus.AWARDED;
            } else if (userWarToken.ifOnlyGainedBaseLevelAward(levelAward.getTokenLevel())) {
                //只领取基础奖励,解锁了进阶战令 则为可领取，否则为继续领取
                status = userWarToken.getSupToken() > 0 ? AwardStatus.ENABLE_AWARD : AwardStatus.CONTINUE_AWARD;
            } else if (levelAward.getTokenLevel() <= userWarToken.getLevel()) {
                //战令奖励解锁  可领取
                if (ListUtil.isEmpty(levelAward.getBaseAwards()) && userWarToken.getSupToken() == 0) {
                    //没有基础奖励 且 未开通进阶战令  则显示为继续领奖
                    status = AwardStatus.CONTINUE_AWARD;
                } else {
                    status = AwardStatus.ENABLE_AWARD;
                }
            }
            rdList.add(RDWarToken.LevelAwardStatus.getInstance(levelAward, status));
        }
        RDWarToken rdWarToken = RDWarToken.getInstance(userWarToken);
        rdWarToken.setLevelAwards(rdList);
        CfgWarTokenBigAwards bigAwards = WarTokenTool.getBigAwards();
        rdWarToken.setBaseBigAwards(RDAward.getInstances(bigAwards.getBaseBigAwards()));
        rdWarToken.setSupBigAwards(RDAward.getInstances(bigAwards.getSupBigAwards()));
        IActivity activity = activityService.getGameActivity(gameUserService.getActiveSid(uid), ActivityEnum.WAR_TOKEN);
        if (activity != null) {
            rdWarToken.setEndDateTime(activity.gainEnd());
        } else {
            rdWarToken.setEndDateTime(DateUtil.now());
        }
        return rdWarToken;
    }

    @Override
    public RDWarToken gainAwards(long uid, int lv) {
        UserWarToken userWarToken = getUserWarToken(uid);
        if (userWarToken.getLevel() < lv) {
            throw new ExceptionForClientTip("award.condition.not.finish");
        }
        if (userWarToken.ifGainedAward(lv)) {
            throw new ExceptionForClientTip("activity.awarded");
        }
        CfgWarTokenLevelAward award = WarTokenTool.getLevelAward(lv);
        List<Award> awards = new ArrayList<>();
        if (userWarToken.ifOnlyGainedBaseLevelAward(lv)) {
            awards.addAll(award.getSupAwards());
            userWarToken.addGainedLevelAward(lv, false);
        } else {
            awards.addAll(award.getBaseAwards());
            userWarToken.addGainedLevelAward(lv, true);
            if (userWarToken.getSupToken() > 0) {
                awards.addAll(award.getSupAwards());
                userWarToken.addGainedLevelAward(lv, false);
            }
        }
        gameUserService.updateItem(userWarToken);
        RDWarToken rd = new RDWarToken();
        if (ListUtil.isNotEmpty(awards)) {
            awardService.fetchAward(uid, awards, WayEnum.WAR_TOKEN_LEVEL_AWARD, WayEnum.WAR_TOKEN_LEVEL_AWARD.getName(), rd);
        }
        return rd;
    }

    @Override
    public RDRechargeActivity gainAllAvailableAwards(long uid) {
        UserWarToken userWarToken = getUserWarToken(uid);
        List<CfgWarTokenLevelAward> levelAwards = WarTokenTool.getLevelAwards();
        List<Award> awards = new ArrayList<>();
        log.info(uid + "一键领取战令奖励前：已领取基础：" + JSONUtil.toJson(userWarToken.getGainedBaseLevelAwards()) + ";已完整领取的：" + JSONUtil.toJson(userWarToken.getGainedAllAwards()));
        for (CfgWarTokenLevelAward levelAward : levelAwards) {
            if (levelAward.getTokenLevel() > userWarToken.getLevel()) {
                //未解锁
                continue;
            }
            if (userWarToken.ifGainedAward(levelAward.getTokenLevel())) {
                //已领取所有
                continue;
            }
            if (!userWarToken.ifOnlyGainedBaseLevelAward(levelAward.getTokenLevel())) {
                //基础奖励 可领取
                awards.addAll(levelAward.getBaseAwards());
            }
            //解锁了进阶战令 则为可领取进阶奖励
            if (userWarToken.getSupToken() > 0) {
                awards.addAll(levelAward.getSupAwards());
                userWarToken.addGainedLevelAward(levelAward.getTokenLevel(), false);
            } else {
                //仅领取普通奖励
                userWarToken.addGainedLevelAward(levelAward.getTokenLevel(), true);
            }
        }
        RDRechargeActivity rd = new RDRechargeActivity();
        if (ListUtil.isNotEmpty(awards)) {
            List<Award> res = new ArrayList<>();
            for (Award award : awards) {
                Optional<Award> optional = res.stream().filter(p -> p.ifEqual(award)).findFirst();
                if (optional.isPresent()) {
                    Award first = optional.get();
                    first.setNum(first.getNum() + award.getNum());
                } else {
                    res.add(CloneUtil.clone(award));
                }
            }
            gameUserService.updateItem(userWarToken);
            awardService.fetchAward(uid, res, WayEnum.WAR_TOKEN_LEVEL_AWARD, WayEnum.WAR_TOKEN_LEVEL_AWARD.getName(), rd);
        } else {
            throw new ExceptionForClientTip("wartoken.level.not.awards");
        }
        return rd;
    }

    /**
     * 购买战令等级
     * 每级需要花费300元宝。最高可购买至180级。
     *
     * @param uid
     * @param addLevel
     * @return
     */
    @Override
    public RDWarToken buyAwards(long uid, int addLevel) {
        UserWarToken userWarToken = getUserWarToken(uid);
        int toFullLevelNeedExpNum = WarTokenTool.getToFullLevelNeedExpNum(userWarToken.getExp());
        int maxAddLevel = toFullLevelNeedExpNum / WarTokenTool.getUpLevelNeedExp();
        if (toFullLevelNeedExpNum % WarTokenTool.getUpLevelNeedExp() > 0) {
            maxAddLevel += 1;
        }
        addLevel = Math.min(addLevel, maxAddLevel);
        int needGold = addLevel * 300;
        ResChecker.checkGold(gameUserService.getGameUser(uid), needGold);
        userWarToken.addExp(addLevel * WarTokenTool.getUpLevelNeedExp());
        RDWarToken rd = RDWarToken.getInstance(userWarToken);
        ResEventPublisher.pubGoldDeductEvent(uid, needGold, WayEnum.WAR_TOKEN_BUY_LEVEL, rd);
        gameUserService.updateItem(userWarToken);
        return rd;
    }

    @Override
    public int getCanGainAwardNum(long uid) {
        int num = 0;
        UserWarToken userWarToken = getUserWarToken(uid);
        List<CfgWarTokenLevelAward> levelAwards = WarTokenTool.getLevelAwards();
        for (CfgWarTokenLevelAward levelAward : levelAwards) {
            if (levelAward.getTokenLevel() > userWarToken.getLevel()) {
                //未解锁
                return num;
            }
            if (userWarToken.ifGainedAward(levelAward.getTokenLevel())) {
                //已领取所有
                continue;
            }
            if (!userWarToken.ifOnlyGainedBaseLevelAward(levelAward.getTokenLevel())) {
                //基础奖励 可领取
                num++;
            }
        }
        return num;
    }
}
