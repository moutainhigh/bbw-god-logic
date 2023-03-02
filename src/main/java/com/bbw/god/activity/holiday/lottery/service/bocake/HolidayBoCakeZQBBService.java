package com.bbw.god.activity.holiday.lottery.service.bocake;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.holiday.lottery.CfgHolidayLotteryAwards;
import com.bbw.god.activity.holiday.lottery.HolidayLotteryTool;
import com.bbw.god.activity.holiday.lottery.HolidayLotteryType;
import com.bbw.god.activity.holiday.lottery.UserHolidayBoCake;
import com.bbw.god.activity.holiday.lottery.rd.RDHolidayLotteryAward;
import com.bbw.god.activity.holiday.lottery.rd.RDHolidayLotteryAwards;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description id=20的节日抽奖service
 * 中秋博饼
 * @author: huanghb
 * @date: 2022/1/10 15:22
 */
@Service
@Slf4j
public class HolidayBoCakeZQBBService extends HolidayBoCakeService {
    private static final Date RESULT_SHOW_TIME = DateUtil.fromDateTimeString("2022-09-19 08:00:00");
    /** 保底冠军结果 */
    private static List<Integer> GUARANTEED_CHAMPION = Arrays.asList(4, 4, 4, 4);

    /**
     * 获取当前service对应的id
     *
     * @return
     */
    @Override
    public int getMyId() {
        return HolidayLotteryType.ZQBB.getValue();
    }

    /**
     * 是否结果展示时间
     *
     * @return
     */
    @Override
    public boolean isResultShowTime() {
        return DateUtil.now().after(RESULT_SHOW_TIME);
    }

    /**
     * 获得抽奖道具类型
     *
     * @return
     */
    @Override
    public TreasureEnum getLotteryPropsType() {
        return TreasureEnum.BO_CAKE_TICKET;
    }

    /**
     * 获得抽奖结果
     *
     * @param drawResultList
     * @return
     */
    @Override
    public int getResultLevel(List<Integer> drawResultList) {
        return Result.getResultLevel(drawResultList);
    }

    /**
     * 获得保底状元的结果
     *
     * @return
     */
    @Override
    public List<Integer> getGuaranteedChampionResult() {
        return GUARANTEED_CHAMPION;
    }

    private static class Result {
        /**
         * 是否状元
         *
         * @param num1
         * @param num2
         * @param num3
         * @param num4
         * @param num5
         * @param num6
         * @return
         */
        private static boolean isZhuangYuan(int num1, int num2, int num3, int num4, int num5, int num6) {
            // 状元插金花
            if (4 == num4 && 2 == num1) {
                return true;
            }
            // 满堂红 或者 六博黑
            if (6 == num2 || 6 == num3 || 6 == num4 || 6 == num5 || 6 == num6) {
                return true;
            }
            // 五子
            if (5 == num1 || 5 == num2 || 5 == num3 || 5 == num4 || 5 == num5 || 5 == num6) {
                return true;
            }
            // 状元
            return 4 == num4;
        }

        /**
         * 是否榜眼
         *
         * @param num1
         * @param num2
         * @param num3
         * @param num4
         * @param num5
         * @param num6
         * @return
         */
        private static boolean isBangYan(int num1, int num2, int num3, int num4, int num5, int num6) {
            return 1 == num1 && 1 == num2 && 1 == num3 && 1 == num4 && 1 == num5 && 1 == num6;
        }

        /**
         * 是否探花
         *
         * @param num4
         * @return
         */
        private static boolean isTanHua(int num4) {
            return 3 == num4;
        }

        /**
         * 是否进士
         *
         * @param num1
         * @param num2
         * @param num3
         * @param num4
         * @param num5
         * @param num6
         * @return
         */
        private static boolean isJinShi(int num1, int num2, int num3, int num4, int num5, int num6) {
            return 4 == num1 || 4 == num2 || 4 == num3 || 4 == num4 || 4 == num5 || 4 == num6;
        }

        /**
         * 是否举人
         *
         * @param num4
         * @return
         */
        private static boolean isJuRen(int num4) {
            return 2 == num4;
        }

        /**
         * 是否秀才
         *
         * @param num4
         * @return
         */
        private static boolean isXiuCai(int num4) {
            return 1 == num4;
        }

        /**
         * 从字符串获取字符数
         *
         * @param str
         * @param searchChar
         * @return
         */
        private static int getCharNumsFromStr(String str, String searchChar) {
            int oldLength = str.length();
            str = str.replace(searchChar, "");
            return oldLength - str.length();
        }

        /**
         * 获得抽奖结果（结果级别）
         *
         * @param drawResult
         * @return
         */
        private static int getResultLevel(List<Integer> drawResult) {
            String str = drawResult.toString().replace(",", "");
            str = str.substring(1, str.length() - 1).replace(" ", "");
            int num1 = getCharNumsFromStr(str, "1");
            int num2 = getCharNumsFromStr(str, "2");
            int num3 = getCharNumsFromStr(str, "3");
            int num4 = getCharNumsFromStr(str, "4");
            int num5 = getCharNumsFromStr(str, "5");
            int num6 = getCharNumsFromStr(str, "6");
            if (isZhuangYuan(num1, num2, num3, num4, num5, num6)) {
                return ResultLevelEnum.ZY.getValue();
            }
            if (isBangYan(num1, num2, num3, num4, num5, num6)) {
                return ResultLevelEnum.BY.getValue();
            }
            if (isTanHua(num4)) {
                return ResultLevelEnum.TH.getValue();
            }
            if (isJinShi(num1, num2, num3, num4, num5, num6)) {
                return ResultLevelEnum.JS.getValue();
            }
            if (isJuRen(num4)) {
                return ResultLevelEnum.JR.getValue();
            }
            if (isXiuCai(num4)) {
                return ResultLevelEnum.XC.getValue();
            }
            return ResultLevelEnum.PARTICIPATE.getValue();
        }
    }

    /**
     * 获取我的纪录
     *
     * @param cfgIds 已领取的配置id集合
     * @return
     */
    @Override
    protected List<RDHolidayLotteryAwards> getMyRecords(List<Integer> cfgIds) {
        List<RDHolidayLotteryAwards> myRecords = new ArrayList<>();
        for (Integer cfgId : cfgIds) {
            CfgHolidayLotteryAwards cfg = HolidayLotteryTool.getById(cfgId);
            List<RDHolidayLotteryAward> awardList = cfg.getAwards().stream().map(tmp ->
                    RDHolidayLotteryAward.getInstance(tmp, cfg.getLevel())).collect(Collectors.toList());
            myRecords.add(new RDHolidayLotteryAwards(awardList));
        }
        return myRecords;
    }

    /**
     * 获得第一次状元额外奖励
     *
     * @param holidayBoCake
     * @return
     */
    @Override
    protected Award getFirstZhuangYuanAward(UserHolidayBoCake holidayBoCake) {
        Award firstZhuangYuanAward = null;
        List<String> zhuangYuanNOList = holidayBoCake.getZhuangYuanNOList();
        if (ListUtil.isEmpty(zhuangYuanNOList)) {
            firstZhuangYuanAward = new Award(559, AwardEnum.KP, 1);
        }
        return firstZhuangYuanAward;
    }
}
