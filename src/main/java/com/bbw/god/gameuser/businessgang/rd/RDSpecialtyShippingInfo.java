package com.bbw.god.gameuser.businessgang.rd;

import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.businessgang.cfg.CfgBusinessGangShippingTaskRules;
import com.bbw.god.gameuser.businessgang.cfg.CfgTaskRules;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.RDTaskItem;
import com.bbw.god.gameuser.task.businessgang.SpecialtyShippingTaskTool;
import com.bbw.god.gameuser.task.businessgang.UserBusinessGangSpecialtyShippingTask;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 特产运送任务
 *
 * @author fzj
 * @date 2022/1/14 17:37
 */
@Data
public class RDSpecialtyShippingInfo extends RDTaskItem {
    /** 目标信息 */
    private String targetInFo;
    /** 特产明细 */
    private List<RDShippingSpecialtyDetails> specialtyDetails;
    /** 是否加急 */
    private boolean urgent;
    /** 目标城池 */
    private Integer targetCity;
    /** 目标城区 */
    private Integer targetCityArea;
    /** 目标等级 */
    private Integer targetLv;
    /** 任务等级 */
    private Integer difficulty;

    /**
     * 运送特产明细
     */
    @Data
    public static class RDShippingSpecialtyDetails {
        /** 特产 */
        private String special;
        /** 特产Id */
        private Integer specialId;
        /** 当前进度 */
        private Integer value;
        /** 总进度 */
        private Integer needValue;
    }

    public static RDSpecialtyShippingInfo getInstance(UserBusinessGangSpecialtyShippingTask ut, CfgTaskEntity taskEntity) {
        RDSpecialtyShippingInfo rdTaskItem = new RDSpecialtyShippingInfo();
        rdTaskItem.setDataId(ut.getId());
        rdTaskItem.setId(ut.getBaseId());
        rdTaskItem.setProgress((int) ut.getValue());
        rdTaskItem.setTotalProgress(ut.getNeedValue());
        rdTaskItem.setAwards(taskEntity.getAwards());
        rdTaskItem.setStatus(ut.getStatus());
        rdTaskItem.setUrgent(ut.isUrgent());
        rdTaskItem.setDifficulty(taskEntity.getDifficulty());
        CfgBusinessGangShippingTaskRules rules = SpecialtyShippingTaskTool.getRules();
        //根据任务难度获取规则
        List<RDShippingSpecialtyDetails> rdShippingSpecialtyDetails = new ArrayList<>();
        int shippingNum = rules.getTaskRules().stream().filter(t -> t.getDifficulty()
                .equals(taskEntity.getDifficulty())).map(CfgTaskRules::getShippingNum).findFirst().orElse(0);
        Map<String, Integer> targetAndProgress = ut.getTargetAndProgress();
        List<CfgSpecialEntity> specials = SpecialTool.getSpecials();
        for (Map.Entry<String, Integer> entry : targetAndProgress.entrySet()) {
            RDShippingSpecialtyDetails rd = new RDShippingSpecialtyDetails();
            rd.setSpecial(entry.getKey());
            Integer specialId = specials.stream().filter(s -> s.getName().equals(entry.getKey()))
                    .map(CfgSpecialEntity::getId).findFirst().orElse(null);
            rd.setSpecialId(specialId);
            rd.setValue(entry.getValue());
            rd.setNeedValue(shippingNum);
            rdShippingSpecialtyDetails.add(rd);
        }
        rdTaskItem.setTargetCity(ut.getTargetCity());
        rdTaskItem.setTargetCityArea(ut.getTargetCityArea());
        if (null != ut.getTargetCity()){
            CfgCityEntity city = CityTool.getCityById(ut.getTargetCity());
            rdTaskItem.setTargetCityArea(city.getCountry());
        }
        rdTaskItem.setTargetLv(ut.getTargetLv());
        rdTaskItem.setSpecialtyDetails(rdShippingSpecialtyDetails);
        return rdTaskItem;
    }
}
