package com.bbw.god.activity.holiday.config;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 劳动光荣
 *
 * @author: huanghb
 * @date: 2022/7/7 15:34
 */
@Data
public class CfgHolidayLaborGlorious implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 初始售出价格 */
    private List<InitialSellingPrice> initialSellingPrice;
    /** 事件总概率 */
    private Integer eventTotalPro;
    /** 普通野怪宝箱触发概率（百分比） */
    private Integer normalYeGuaiBoxTriggerPro;
    /** 普通野怪宝箱产出（千分比） */
    private List<Award> normalYeGuaiBoxOutput;
    /** #精英野怪宝箱触发概率（百分比） */
    private Integer eliteYeGuaiBoxTriggerPro;
    /** 精英野怪宝箱产出（千分比） */
    private List<Award> eliteYeGuaiBoxOutput;
    /** 野怪是否直接获得产出: 0 是 1否出 */
    private Integer yeGuaiBoxIsDirectOutput;
    /** 特产触发概率 */
    private Integer specialsTriggerPro;
    /** 特产触发双倍概率 */
    private Integer specialsTriggerDoublePro;
    /** #特产产出（千分比） */
    private List<Award> specialsOutput;
    /** 村庄事件触发概率 */
    private Integer cunZEventTriggerPro;
    /** 村庄事件宝箱触发概率 */
    private Integer cunZEventBoxTriggerPro;
    /** 村庄事件宝箱产出（千分比） */
    private List<Award> cunZEventBoxOutput;
    /** 村庄是否直接获得产出: 0 是 1否 */
    private Integer cunZIsDirectOutput;
    /** 客栈事件触发概率 */
    private Integer keZEventTriggerPro;
    /** 客栈事件宝箱触发概率 */
    private Integer keZEventBoxTriggerPro;
    /** 客栈事件宝箱产出（千分比） */
    private List<Award> keZEventBoxOutput;
    /** 客栈是否直接获得产出: 0 是 1否 */
    private Integer keZIsDirectOutput;
    /** 游商馆事件触发概率 */
    private Integer youSGEventTriggerPro;
    /** 游商馆事件宝箱触发概率 */
    private Integer youSGEventBoxTriggerPro;
    /** 游商馆事件宝箱产出（千分比） */
    private List<Award> youSGEventBoxOutput;
    /** 游商馆是否直接获得产出: 0 是 1否 */
    private Integer youSGIsDirectOutput;
    /** 野怪战斗概率产出 */
    private List<Award> yeGuaiFightOutput;
    /** npc名称 */
    private String npcName;
    /** npc头像 */
    private Integer headImg;
    /** 野怪战斗产出数量 */
    private Integer yeGuaiFightOutputNum;
    /** 每次上涨的价格 */
    private int pricePerIncrease;

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }


    @Data
    public static class InitialSellingPrice {
        /** 法宝id */
        private int treasureId;
        /** 价格 */
        private int price;
    }
}
