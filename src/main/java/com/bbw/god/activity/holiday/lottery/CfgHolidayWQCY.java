package com.bbw.god.activity.holiday.lottery;

import com.bbw.god.game.config.CfgInterface;
import com.bbw.god.game.config.CfgPrepareListInterface;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 五气朝元抽奖配置
 *
 * @author: huanghb
 * @date: 2022/5/12 14:11
 */
@Slf4j
@Data
public class CfgHolidayWQCY implements CfgInterface, CfgPrepareListInterface, Serializable {
    private static final long serialVersionUID = 1341975239179802282L;
    /** 默认配置的key值 */
    private String key;
    private List<Integer> firstRewardResult;
    /** 特产产出总概率 */
    private Integer specialsOutPutTotalProb;
    /** 特产产出一个圣元珠概率 */
    private Integer specialsOutPutOneSYZProb;
    /** 特产产出两个圣元珠概率 */
    private Integer specialsOutPutTwoSYZProb;
    /** 抽奖消耗 */
    private List<DrawConsumption> drawConsumptions;
    /** 抽奖轮次信息 */
    private List<DrawRoundInfo> drawRoundInfos;
    /** 奖励结果集合 */
    public static List<DrawResult> rewardResults = new ArrayList<>();

    /**
     * 获取配置项到ID值
     *
     * @return
     */
    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }

    @Override
    public void prepare() {
        List<DrawResult> allResult = new ArrayList<>();
        //第一个抽奖结果
        List<Integer> firstRewardResult = HolidayWQCYTool.getFirstRewardResult();
        //初始化存放元珠可能结果的临时栈
        Stack<Integer> stack = new Stack<>();
        //生成珠子的全排列结果
        permutations(stack, firstRewardResult, allResult);
        // 保证被gc回收
        stack = null;
        rewardResults.addAll(allResult);
        log.info("五气朝元奖励结果集合预备完成");
    }

    /**
     * 排列组合，把所有可能的结果添加到list中
     *
     * @param firstRewardResult 第一个奖励结果
     * @param allResult         要操作的结果集合
     */
    private static void permutations(Stack<Integer> stack, List<Integer> firstRewardResult, List<DrawResult> allResult) {
        //获得临时结果的数据长度
        int tmpResultSize = stack.size();
        //获得结果的目标长度
        int resultTargetSize = firstRewardResult.size();
        //是否完成生成一种新的可能排列方式
        boolean isNewResult = tmpResultSize == resultTargetSize;
        if (isNewResult) {
            //添加新的排列到结果集合
            DrawResult drawResult = DrawResult.getInstance(new ArrayList<>(stack));
            allResult.add(drawResult);
            return;
        }
        //生成珠子的全排列
        for (int i = 0; i < firstRewardResult.size(); i++) {
            if (!stack.contains(firstRewardResult.get(i))) {
                stack.add(firstRewardResult.get(i));
                permutations(stack, firstRewardResult, allResult);
                stack.pop();
            }
        }
    }

    @Data
    public static class DrawConsumption {
        /** 法宝id */
        private Integer treasureId;
        /** 数量 */
        private Integer num;
    }

    @Data
    public static class DrawRoundInfo {
        /** 轮次id */
        private Integer roundId;
        /** 最小轮次 */
        private Integer minRound;
        /** 最大轮次 */
        private Integer maxRound;
        /** 每轮抽奖次数限制 */
        private Integer perRoundDrawTimesLimit;
    }
}
