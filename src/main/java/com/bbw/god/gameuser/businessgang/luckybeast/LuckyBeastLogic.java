package com.bbw.god.gameuser.businessgang.luckybeast;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.award.RDAwards;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.treasure.HonorCurrencyService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDSuccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 招财兽逻辑
 *
 * @author: huanghb
 * @date: 2022/1/17 16:58
 */
@Slf4j
@Service
public class LuckyBeastLogic {
    @Autowired
    private LuckyBeastService luckyBeastService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private HonorCurrencyService honorCurrencyService;

    /**
     * 招财兽编组
     *
     * @param uid
     * @param luckyBeastCardId
     * @return
     */
    protected RDSuccess setLuckyBeastCards(long uid, Integer luckyBeastCardId) {
        //声望检测
        if (!luckyBeastService.isOpenLuckyBeast(uid)) {
            throw new ExceptionForClientTip("luckyBeast.not.open");
        }
        UserLuckyBeast userLuckyBeast = luckyBeastService.getUserLuckyBeast(uid);
        //是否是当前招财兽
        Boolean isCurrentLuckyBeast = isCurrentLuckyBeast(uid, userLuckyBeast);
        if (!isCurrentLuckyBeast) {
            throw new ExceptionForClientTip("luckyBeast.not.in");
        }
        //判断卡牌是否可以更换
        if (!userLuckyBeast.ifCanChangeCard(luckyBeastCardId)) {
            throw new ExceptionForClientTip("luckyBeast.card.not.replace");
        }
        //更新卡牌信息
        UserCard card = userCardService.getUserCard(uid, luckyBeastCardId);
        userLuckyBeast.setCardId(luckyBeastCardId);
        int cardHp = luckyBeastCardId == 0 ? 0 : card.gainHp();
        userLuckyBeast.setHp(cardHp);
        //更新技能加成
        List<Integer> skillIds = card.gainActivedSkills();
        userLuckyBeast.updateSkillBuff(skillIds);
        //更新数据
        gameUserService.updateItem(userLuckyBeast);
        return RDLuckyBeastInfo.getInstance(userLuckyBeast);
    }

    /**
     * 显示上一次攻打（招财）奖励
     *
     * @param uid
     * @return
     */
    protected RDSuccess getAwardsToShow(long uid) {
        //声望检测
        if (!luckyBeastService.isOpenLuckyBeast(uid)) {
            throw new ExceptionForClientTip("luckyBeast.not.open");
        }
        UserLuckyBeast userLuckyBeast = luckyBeastService.getUserLuckyBeast(uid);
        //是否是当前招财兽
        Boolean isCurrentLuckyBeast = isCurrentLuckyBeast(uid, userLuckyBeast);
        if (!isCurrentLuckyBeast) {
            throw new ExceptionForClientTip("luckyBeast.not.in");
        }
        List<Award> addAttactAwardRecords = userLuckyBeast.getAddAttactAwardRecords();
        return RDAwards.getInstance(addAttactAwardRecords);
    }

    /**
     * 攻打招财兽（招财）
     *
     * @param uid
     * @return
     */
    public RDSuccess attack(long uid) {
        //声望检测
        if (!luckyBeastService.isOpenLuckyBeast(uid)) {
            throw new ExceptionForClientTip("luckyBeast.not.open");
        }
        UserLuckyBeast userLuckyBeast = luckyBeastService.getUserLuckyBeast(uid);
        if (userLuckyBeast.getRemainfreeAttackTimes() <= 0) {
            throw new ExceptionForClientTip("luckyBeast.no.attack.times");
        }
        //是否是当前招财兽
        Boolean isCurrentLuckyBeast = isCurrentLuckyBeast(uid, userLuckyBeast);
        if (!isCurrentLuckyBeast) {
            throw new ExceptionForClientTip("luckyBeast.not.in");
        }
        //扣除卡牌血量
        userLuckyBeast.deductHp(userLuckyBeast.getAttackPower());
        userLuckyBeast.deductRemainfreeAttackTimes();
        //获得本次攻击奖励
        List<Award> awards = luckyBeastService.getRandomAttackAwards(uid);
        userLuckyBeast.addAttactAwardRecords(awards);
        //更新拥有项
        gameUserService.updateItem(userLuckyBeast);
        //返回
        RDLuckyBeastInfo rd = RDLuckyBeastInfo.getInstance(userLuckyBeast);
        //发布奖励
        awardService.fetchAward(uid, awards, WayEnum.LUCKY_BEAST, "在【" + WayEnum.LUCKY_BEAST.getName() + "】", rd);
        return rd;
    }

    /**
     * 购买攻击(招财)次数
     *
     * @param uid
     * @param buyNum
     * @return
     */
    protected RDSuccess buyAttackTimes(long uid, Integer buyNum) {
        //声望检测
        if (!luckyBeastService.isOpenLuckyBeast(uid)) {
            throw new ExceptionForClientTip("luckyBeast.not.open");
        }
        if (buyNum == 0) {
            throw new ExceptionForClientTip("luckyBeast.invalid，buy.times");
        }
        //道具检测
        int needHonorCopperCoinNum = LuckyBeastTool.getBuyAttackTimesNeedHonorCopperCoinNum() * buyNum;
        TreasureChecker.checkIsEnough(TreasureEnum.HONOR_COPPER_COIN.getValue(), needHonorCopperCoinNum, uid);
        UserLuckyBeast userLuckyBeast = luckyBeastService.getUserLuckyBeast(uid);
        //是否是当前招财兽
        Boolean isCurrentLuckyBeast = isCurrentLuckyBeast(uid, userLuckyBeast);
        if (!isCurrentLuckyBeast) {
            throw new ExceptionForClientTip("luckyBeast.not.in");
        }
        if (buyNum > userLuckyBeast.getRemainBuyAttacksTimes()) {
            throw new ExceptionForClientTip("luckyBeast.attack.times.out");
        }
        //增加攻击次数
        userLuckyBeast.buyAttackTimes(buyNum);
        RDLuckyBeastInfo rd = RDLuckyBeastInfo.getInstance(userLuckyBeast);
        //扣除消耗道具
//        int needHonorCopperCoinNum = LuckyBeastTool.getBuyAttackTimesNeedHonorCopperCoinNum() * buyNum;
//        honorCurrencyService.honorCurrencyDeductConvert(uid, TreasureEnum.HONOR_COPPER_COIN.getValue(), needHonorCopperCoinNum, rd);
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.HONOR_COPPER_COIN.getValue(), needHonorCopperCoinNum, WayEnum.LUCKY_BEAST, rd);
        gameUserService.updateItem(userLuckyBeast);
        return rd;
    }
    /**
     * 刷新招财兽
     *
     * @param uid
     * @return
     */
    protected RDRefreshLuckyBeastPos refreshMyLuckyBeast(long uid) {
        //声望检测
        if (!luckyBeastService.isOpenLuckyBeast(uid)) {
            throw new ExceptionForClientTip("luckyBeast.not.open");
        }

        UserLuckyBeast userLuckyBeast = luckyBeastService.getUserLuckyBeast(uid);
        //招财兽攻击次数检测
        if (userLuckyBeast.getRemainfreeAttackTimes() > 0) {
            throw new ExceptionForClientTip("luckyBeast.attack.times.not.used");
        }
        //招财兽重置次数检测
        Integer ableRestTimes = userLuckyBeast.getRemainResetTimes();
        if (ableRestTimes <= 0) {
            throw new ExceptionForClientTip("luckyBeast.reset.times.out");
        }
        //道具数量检测
        int needBellNum = LuckyBeastTool.getResetNeedBellNum();
        TreasureChecker.checkIsEnough(TreasureEnum.CALL_BEAST_BELL.getValue(), needBellNum, uid);
        //初始化招财兽
        CfgLuckyBeast.LuckyBeastInfo luckyBeastInfo = LuckyBeastTool.getLuckyBeastInfo(luckyBeastService.getPrestige(uid));
        Integer position = luckyBeastService.generateLuckyBeastRandomPosition(uid);
        userLuckyBeast.resetLuckyBeast(luckyBeastInfo, position);
        //更新拥有项
        gameUserService.updateItem(userLuckyBeast);
        //返回
        RDRefreshLuckyBeastPos rd = new RDRefreshLuckyBeastPos(userLuckyBeast);
        //扣除唤兽铃铛
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.CALL_BEAST_BELL.getValue(), 1, WayEnum.YEAR_BEAST, rd);
        return rd;
    }

    /**
     * 是否是当前招财兽
     *
     * @param uid
     * @return
     */
    protected boolean isCurrentLuckyBeast(long uid, UserLuckyBeast userLuckyBeast) {
        //玩家位置
        int userPos = gameUserService.getGameUser(uid).getLocation().getPosition();
        int luckyPos = userLuckyBeast.getPosition();
        if (userPos == luckyPos) {
            return true;
        }
        return false;
    }
}
