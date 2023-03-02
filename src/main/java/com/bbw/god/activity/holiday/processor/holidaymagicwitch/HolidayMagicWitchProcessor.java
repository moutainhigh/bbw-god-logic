package com.bbw.god.activity.holiday.processor.holidaymagicwitch;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.lottery.rd.RDHolidayLotteryAward;
import com.bbw.god.activity.holiday.lottery.rd.RDHolidayLotteryAwards;
import com.bbw.god.activity.holiday.processor.holidaymagicwitch.data.game.HolidayMagicWitchGameService;
import com.bbw.god.activity.holiday.processor.holidaymagicwitch.data.user.HolidayMagicWitchUserService;
import com.bbw.god.activity.holiday.processor.holidaymagicwitch.data.user.UserHolidayMagicWitch;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 魔法女巫
 *
 * @author: huanghb
 * @date: 2022/12/13 9:10
 */
@Service
public class HolidayMagicWitchProcessor extends AbstractActivityProcessor {
    @Autowired
    private HolidayMagicWitchUserService holidayMagicWitchUserService;
    @Autowired
    private HolidayMagicWitchGameService holidayMagicWitchGameService;

    @Autowired
    private AwardService awardService;

    public HolidayMagicWitchProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.MAGIC_WITCH_51);
    }

    /**
     * 获取炼制信息
     *
     * @param uid 玩家id
     * @return
     */
    public RDHolidayMagicWitch getHolidayRefinInfo(long uid) {
        List<RDHolidayLotteryAwards> myRecords = new ArrayList<>();
        UserHolidayMagicWitch userHolidayMagicWitch = holidayMagicWitchUserService.getCurUserHolidayWagicWoman(uid);
        List<DrawResult> records = userHolidayMagicWitch.getRecords();
        for (DrawResult record : records) {
            List<RDHolidayLotteryAward> awards = getAwards(uid, record).stream().map(RDHolidayLotteryAward::getInstance).collect(Collectors.toList());
            myRecords.add(new RDHolidayLotteryAwards(awards, record.getResult()));
        }
        return RDHolidayMagicWitch.getInstance(myRecords);
    }


    /**
     * 获取奖励
     *
     * @param uid
     * @param result
     * @return
     */
    public List<Award> getAwards(long uid, DrawResult result) {
        List<DrawResult> drawResults = holidayMagicWitchGameService.getCurGameHolidayMagicWomanResult(uid);
        DrawResult drawResult = drawResults.stream().filter(tmp -> tmp.getResult().equals(result.getResult())).findFirst().orElse(null);
        if (null == drawResult) {
            throw new ExceptionForClientTip("当前排序结果:" + result.toString() + "不存在");
        }
        Integer level = drawResults.indexOf(drawResult);
        HolidayMagicWitchAward holidayMagicWitchAward = HolidayMagicWitchTool.getByAwardLevel(level);
        return holidayMagicWitchAward.getAwards();
    }


    /**
     * 炼制
     *
     * @param uid           玩家id
     * @param magicMaterial 魔法材料
     * @return
     */
    public RDCommon refin(long uid, String magicMaterial) {
        RDCommon rd = new RDCommon();
        List<Integer> result = ListUtil.parseStrToInts(magicMaterial).stream().distinct().collect(Collectors.toList());
        // 检查
        checkForDraw(uid, result);
        // 扣除法宝道具资源
        deductTreasure(uid, result, rd);
        // 发放奖励
        List<Award> awards = getAwards(uid, DrawResult.getInstance(result));
        awardService.fetchAward(uid, awards, WayEnum.MAGIC_WITCH, "", rd);
        //缓存玩家抽奖信息到redis
        UserHolidayMagicWitch curUserHolidayWagicWoman = holidayMagicWitchUserService.getCurUserHolidayWagicWoman(uid);
        // 已经抽过了
        if (curUserHolidayWagicWoman.getRecords().contains(DrawResult.getInstance(result))) {
            return rd;
        }
        // 添加记录
        curUserHolidayWagicWoman.addRecord(result);
        holidayMagicWitchUserService.updateData(curUserHolidayWagicWoman);
        return rd;
    }

    /**
     * 抽奖检查
     *
     * @param uid    玩家id
     * @param result 魔法材料
     */
    public void checkForDraw(long uid, List<Integer> result) {
        //检查需要消耗的法宝道具是否足够
        checkTreasure(uid, result);
        if (HolidayMagicWitchTool.getResultSize() != result.size()) {
            throw new ExceptionForClientTip("activity.magicWitch.error");
        }
    }

    /**
     * 检查消耗法宝道具是否足够
     *
     * @param uid
     * @param result 魔法材料
     */
    private void checkTreasure(long uid, List<Integer> result) {
        if (ListUtil.isEmpty(result)) {
            return;
        }
        //每次抽奖需要消耗的法宝信息集合
        List<CfgHolidayMagicWitch.DrawConsumption> currentRoundDarwConsumptions = HolidayMagicWitchTool.getCurrentRoundDarwConsumption();
        for (CfgHolidayMagicWitch.DrawConsumption drawConsumption : currentRoundDarwConsumptions) {
            if (!result.contains(drawConsumption.getTreasureId())) {
                continue;
            }
            //检查消耗法宝道具是否足够
            TreasureChecker.checkIsEnough(drawConsumption.getTreasureId(), drawConsumption.getNum(), uid);
        }
    }

    /**
     * 扣除法宝
     *
     * @param uid
     */
    private void deductTreasure(long uid, List<Integer> result, RDCommon rd) {
        //每次抽奖需要消耗的法宝信息集合
        List<CfgHolidayMagicWitch.DrawConsumption> currentRoundDarwConsumptions = HolidayMagicWitchTool.getCurrentRoundDarwConsumption();
        for (CfgHolidayMagicWitch.DrawConsumption drawConsumption : currentRoundDarwConsumptions) {
            if (!result.contains(drawConsumption.getTreasureId())) {
                continue;
            }
            //扣除法宝道具
            TreasureEventPublisher.pubTDeductEvent(uid, drawConsumption.getTreasureId(), drawConsumption.getNum(), WayEnum.MAGIC_WITCH, rd);
        }
    }
}
