package com.bbw.god.activity.holiday.processor.holidaymagicwitch;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.config.CfgInterface;
import com.bbw.god.game.config.CfgPrepareListInterface;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 魔法女巫配置
 *
 * @author: huanghb
 * @date: 2022/12/13 15:02
 */
@Slf4j
@Data
public class CfgHolidayMagicWitch implements CfgInterface, CfgPrepareListInterface, Serializable {
    private static final long serialVersionUID = 1341975239179802282L;
    /** 默认配置的key值 */
    private String key;
    /** 基本材料 */
    private List<Integer> baseMaterial;
    /** 结果数量 */
    private Integer resultSize;
    /** 抽奖消耗 */
    private List<DrawConsumption> drawConsumptions;
    /** 奖励结果集合 */
    public List<DrawResult> awardResults = new ArrayList<>();
    /** 获得奖池信息 */
    public List<HolidayMagicWitchAward> awardPool;

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
        //基础材料
        List<Integer> baseMaterial = HolidayMagicWitchTool.getBaseMaterial();
        //初始化存放可能结果的临时集合
        List<Integer> tempResult = new Stack<>();
        //生成全排列结果
        rondomAward(baseMaterial, tempResult, allResult);
        awardResults.addAll(allResult);
        log.info("魔法女巫奖励结果集合预备完成");
    }

    /**
     * 随机奖励排序
     *
     * @param baseMaterial
     * @param tempResult
     * @param allResult
     */
    private void rondomAward(List<Integer> baseMaterial, List<Integer> tempResult, List<DrawResult> allResult) {
        if (tempResult.size() == HolidayMagicWitchTool.getResultSize()) {
            allResult.add(DrawResult.getInstance(CloneUtil.cloneList(tempResult)));
            return;
        }
        for (int i = 0; i < baseMaterial.size(); i++) {
            if (tempResult.contains(baseMaterial.get(i))) {
                continue;
            }
            Integer value = baseMaterial.get(i);
            tempResult.add(value);
            rondomAward(baseMaterial, tempResult, allResult);
            tempResult.remove(value);
        }
    }

    @Data
    public static class DrawConsumption {
        /** 法宝id */
        private Integer treasureId;
        /** 数量 */
        private Integer num;
    }

}
