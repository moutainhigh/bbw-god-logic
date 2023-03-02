package com.bbw.god.activity.cfg;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 萌虎集市配置
 *
 * @author fzj
 * @date 2022/3/7 13:37
 */
@Data
public class CfgCuteTigerEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 触发野怪宝箱概率 */
    private int yeGuaiTriggerPro;
    /** 野怪宝箱概率产出 */
    private List<Award> yeGuaiBoxOutPut;
    /** 村庄触发概率 */
    private int cunZTriggerPro;
    /** 村庄产出 */
    private List<Award> cunZOutPut;
    /** 特产触发概率 */
    private int specialsTriggerPro;
    /** 特产产出 */
    private List<Award> specialsOutPut;
    /** 最大触发野地事件次数 */
    private int maxTriggerspecialYeDiTimes;
    /** 活动特殊野地事件概率 */
    private int specialYeDiEventPro;
    /** 糕点初始售出价格(单位：小虎币) */
    private List<InitialSellingPrice> initialSellingPrice;
    /** 邮件通知等级 */
    private int levelToMailNotice;
    /** 活动特殊野地事件 */
    private List<SpecialYeDiEvent> specialYeDiEvent;

    @Data
    public static class InitialSellingPrice {
        /** 法宝id */
        private int treasureId;
        /** 价格 */
        private int price;
    }

    @Data
    public static class SpecialYeDiEvent {
        private int id;
        /** 法宝id */
        private int treasureId;
        /** 效果 */
        private double effect;
        /** 概率 */
        private int pro;
        /** 描述 */
        private String memo;
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
