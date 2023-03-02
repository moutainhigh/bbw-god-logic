package com.bbw.god.gameuser.nightmarenvwam.pinchpeople;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.nvwm.RDNvWM;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.nightmarenvwam.NightmareNvWamCfgTool;
import com.bbw.god.gameuser.nightmarenvwam.RDIsActiveNightmareNWM;
import com.bbw.god.gameuser.nightmarenvwam.listener.PinchPeopleEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 捏土造人逻辑
 *
 * @author fzj
 * @date 2022/5/4 13:55
 */
@Service
@Slf4j
public class PinchPeopleLogic {
    @Autowired
    GameUserService gameUserService;
    @Autowired
    UserKneadSoilService userKneadSoilService;
    @Autowired
    AwardService awardService;
    @Autowired
    UserCityService userCityService;

    /**
     * 是否激活建筑功能
     *
     * @param uid
     */
    public RDIsActiveNightmareNWM isActiveNightmareNWM(long uid) {
        GameUser gu = gameUserService.getGameUser(uid);
        RDIsActiveNightmareNWM rd = new RDIsActiveNightmareNWM();
        if (isActive(gu)) {
            rd.setIsActive(1);
        }
        return rd;
    }

    /**
     * 是否激活建筑功能
     *
     * @param gu
     * @return
     */
    private boolean isActive(GameUser gu) {
        boolean isActive = userCityService.isOwnLowNightmareCityAsCountry(gu.getId(), TypeEnum.Wood.getValue());
        return isActive && gu.getStatus().ifNotInFsdlWorld();
    }

    /**
     * 梦魇女娲庙捐献
     *
     * @param uid
     * @param donateCopper
     * @return
     */
    public void donate(long uid, long donateCopper, RDNvWM rd) {
        long minDonateCopper = 100000;
        if (donateCopper < minDonateCopper) {
            return;
        }
        UserPinchPeopleInfo kneadSoilInFo = userKneadSoilService.getOrCreatUserKneadSoilInFo(uid);
        Date donateTime = kneadSoilInFo.getLastDonateTime();
        if (donateTime != null && !DateUtil.isToday(donateTime)) {
            //重置每日捐献进度
            kneadSoilInFo.setDayDonateTotalCopper(0L);
        }
        //计算泥人进度值
        int addClayFigurineValue = calculateProgress(kneadSoilInFo, donateCopper);
        //加泥人进度值
        kneadSoilInFo.addClayFigurineValue(addClayFigurineValue);
        //加每日铜钱
        kneadSoilInFo.addDayDonateTotalCopper(donateCopper);
        //更新捐赠时间
        kneadSoilInFo.setLastDonateTime(DateUtil.now());
        gameUserService.updateItem(kneadSoilInFo);
        rd.setAddProgressToPinchPeopleValue(addClayFigurineValue);
    }

    /**
     * 计算泥人进度值
     *
     * @param kneadSoilInFo
     * @param donateCopper
     * @return
     */
    private int calculateProgress(UserPinchPeopleInfo kneadSoilInFo, long donateCopper) {
        //增加的泥人进度值
        int addClayFigurineValue = 0;
        //获得当日已经捐献的铜钱
        long dayDonateTotalCopper = kneadSoilInFo.getDayDonateTotalCopper();
        Integer doubleCritical = NightmareNvWamCfgTool.getDoubleCritical();
        Integer valueUnit = NightmareNvWamCfgTool.getClayFigurineValueUnit();
        if (dayDonateTotalCopper >= doubleCritical) {
            return Math.toIntExact(donateCopper / valueUnit);
        }
        //每日捐赠不足300万翻倍
        long doubledValue = doubleCritical - dayDonateTotalCopper;
        //计算需要翻倍的
        long needDoubledValue = doubledValue - donateCopper;
        if (needDoubledValue >= 0) {
            return Math.toIntExact(donateCopper / valueUnit) * 2;
        }
        addClayFigurineValue += Math.toIntExact(doubledValue / valueUnit) * 2;
        donateCopper = donateCopper - doubledValue;
        addClayFigurineValue += Math.toIntExact(donateCopper / valueUnit);
        return addClayFigurineValue;
    }


    /**
     * 进入捏人总界面
     *
     * @param uid
     * @return
     */
    public RDPinchPeopleInfo enterKneadSoil(long uid) {
        GameUser gu = gameUserService.getGameUser(uid);
        if (!isActive(gu)) {
            throw new ExceptionForClientTip("activity.function.not.open");
        }
        UserPinchPeopleInfo kneadSoilInFo = userKneadSoilService.getOrCreatUserKneadSoilInFo(uid);
        //更新每日捏人分数
        Date pinchPeopleTime = kneadSoilInFo.getPinchPeopleTime();
        if (pinchPeopleTime != null && !DateUtil.isToday(pinchPeopleTime)) {
            kneadSoilInFo.setDayPinchPeopleScore(new ArrayList<>());
            gameUserService.updateItem(kneadSoilInFo);
        }
        RDPinchPeopleInfo rd = new RDPinchPeopleInfo();
        rd.setDayPinchPeopleScore(kneadSoilInFo.getDayPinchPeopleScore());
        rd.setPinchPeopleTotalScore(kneadSoilInFo.getPinchPeopleTotalScore());
        rd.setProgressToPinchPeople(kneadSoilInFo.getProgressToPinchPeople());
        return rd;
    }


    /**
     * 捏人
     *
     * @param uid
     * @param cursorId
     * @return
     */
    public RDPinchPeople pinchPeople(long uid, int cursorId) {
        GameUser gu = gameUserService.getGameUser(uid);
        if (!isActive(gu)) {
            throw new ExceptionForClientTip("activity.function.not.open");
        }
        UserPinchPeopleInfo kneadSoilInFo = userKneadSoilService.getOrCreatUserKneadSoilInFo(uid);
        Integer clayFigurineValue = kneadSoilInFo.getProgressToPinchPeople();
        Integer pinchPeopleNeedValue = NightmareNvWamCfgTool.getPinchPeopleNeedValue();
        //检查泥人进度值
        if (clayFigurineValue < pinchPeopleNeedValue) {
            throw new ExceptionForClientTip("nightmareNvWaM.clayFigurineValue.not");
        }
        //更新每日捏人分数
        Date pinchPeopleTime = kneadSoilInFo.getPinchPeopleTime();
        List<Integer> soilScore = kneadSoilInFo.getDayPinchPeopleScore();
        if (pinchPeopleTime != null && !DateUtil.isToday(pinchPeopleTime)) {
            soilScore = new ArrayList<>();
        }
        //扣除泥人进度值
        kneadSoilInFo.delClayFigurineValue(pinchPeopleNeedValue);
        //计算分数
        RDPinchPeople rd = new RDPinchPeople();
        //获得标识对应分数
        Integer scoreBySign = NightmareNvWamCfgTool.getPinchPeopleScoreBySign(cursorId);
        //是否是有效分数（用来计算每日奖励）
        Integer validTimes = NightmareNvWamCfgTool.getDayPinchPeopleValidTimes();
        //计入每日分数
        if (soilScore.size() < validTimes) {
            soilScore.add(scoreBySign);
            //发送每日挑战奖励
            if (soilScore.size() == validTimes) {
                sendAward(uid, soilScore, rd);
            }
        }
        kneadSoilInFo.setDayPinchPeopleScore(soilScore);
        //计入累计分数
        kneadSoilInFo.addKneadSoilTotalScore(scoreBySign);
        //更新捏人时间
        kneadSoilInFo.setPinchPeopleTime(DateUtil.now());
        gameUserService.updateItem(kneadSoilInFo);
        rd.setPinchPeopleScore(scoreBySign);
        rd.setDayScoreList(soilScore);
        PinchPeopleEventPublisher.pubPinchPeopleEvent(uid, soilScore, rd);
        return rd;
    }

    /**
     * 发送奖励
     *
     * @param uid
     * @param soilScore
     */
    private void sendAward(long uid, List<Integer> soilScore, RDPinchPeople rd) {
        //计算平均分
        double averageScore = soilScore.subList(0, 5).stream().mapToDouble(Integer::doubleValue).average().orElse(0);
        //获得对应奖励
        List<Award> challengeAwards = NightmareNvWamCfgTool.getChallengeAwards(averageScore);
        if (!challengeAwards.isEmpty()) {
            //发放奖励
            awardService.fetchAward(uid, challengeAwards, WayEnum.NIGHTMARE_NWM, "", rd);
        }
    }

    /**
     * 发放捏人累计分数奖励
     *
     * @param uid
     */
    public RDCommon sendTotalScoreAward(long uid) {
        GameUser gu = gameUserService.getGameUser(uid);
        if (!isActive(gu)) {
            throw new ExceptionForClientTip("activity.function.not.open");
        }
        UserPinchPeopleInfo kneadSoilInFo = userKneadSoilService.getOrCreatUserKneadSoilInFo(uid);
        //判断分数是否足够
        Integer soilTotalScore = kneadSoilInFo.getPinchPeopleTotalScore();
        Integer peopleAwardNeedValue = NightmareNvWamCfgTool.getPinchPeopleAwardNeedValue();
        if (soilTotalScore < peopleAwardNeedValue) {
            throw new ExceptionForClientTip("transmigration.fight.unaward");
        }
        //扣除分数
        kneadSoilInFo.delKneadSoilTotalScore(peopleAwardNeedValue);
        //获得奖励
        Integer awardType = NightmareNvWamCfgTool.getTotalScoreAwardType();
        Integer treasureId = NightmareNvWamCfgTool.getTreasureIdByType(awardType);
        gameUserService.updateItem(kneadSoilInFo);
        //发放奖励
        RDCommon rd = new RDCommon();
        TreasureEventPublisher.pubTAddEvent(uid, treasureId, 1, WayEnum.NIGHTMARE_NWM, rd);
        return rd;
    }

}
