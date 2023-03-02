package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.exception.CoderException;
import com.bbw.god.activity.holiday.lottery.*;
import com.bbw.god.activity.holiday.lottery.service.HolidayLotteryService30;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.server.ServerUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * 五气朝元管理接口
 *
 * @author: huanghb
 * @date: 2022/1/28 14:40
 */
@RestController
@RequestMapping("/gm")
public class GMWQCYCtrl {
    @Autowired
    private HolidayLotteryService30 holidayLotteryService30;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ServerUserService serverUserService;

    /**
     * 五气朝元抽取所有
     *
     * @param sId
     * @param nickname
     * @param exceptNum
     * @return
     */
    @RequestMapping("wqcy!drawAll")
    public Rst drawWangZhongWang(int sId, String nickname, Integer exceptNum) {
        Optional<Long> uid = this.serverUserService.getUidByNickName(sId, nickname);
        if (!uid.isPresent()) {
            return Rst.businessFAIL("无效的账号或者区服");
        }
        List<DrawResult> gameHolidayResult = CfgHolidayWQCY.rewardResults;
        String msg = "";
        if (exceptNum > 0 && exceptNum < gameHolidayResult.size()) {
            for (int i = 0; i < gameHolidayResult.size() - exceptNum; ) {
                HolidayLotteryParam param = new HolidayLotteryParam();
                int type = HolidayLotteryType.WQCY.getValue();
                param.setType(type);
                DrawResult drawResult = gameHolidayResult.get(i);
                param.setResult(StringUtils.join(drawResult.getResult(), ","));
                UserHolidayLottery30 userHolidayLottery30 = holidayLotteryService30.getCurUserHolidayLottery(uid.get());
                // 已经抽过了
                if (!userHolidayLottery30.getRecords().contains(drawResult)) {
                    holidayLotteryService30.draw(uid.get(), param);
                }
                List<DrawResult> holidayResults = holidayLotteryService30.getCurGameHolidayResult(uid.get());
                DrawResult holidayResult = holidayResults.stream().filter(tmp -> tmp.getResult().equals(drawResult.getResult())).findFirst().orElse(null);
                if (null == holidayResult) {
                    throw new CoderException("当前排序结果:" + drawResult.getResult().toString() + "不存在");
                }
                Integer level = holidayResults.indexOf(holidayResult);
                CfgHolidayLotteryAwards cfgHolidayLotteryAwards = HolidayLotteryTool.getByAwardLevel(type, level);
                msg += "第" + ++i + "次抽奖奖励id:" + cfgHolidayLotteryAwards.getId();
            }
            List<DrawResult> exceptDrawResult = gameHolidayResult.subList(gameHolidayResult.size() - exceptNum, gameHolidayResult.size());
            msg += "本次未使用的抽奖顺序" + exceptDrawResult.toString();
        }
        return Rst.businessOK(msg);
    }
}
