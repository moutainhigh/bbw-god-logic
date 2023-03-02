package com.bbw.god.report.business;

import com.bbw.god.ConsumeType;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.mall.cardshop.CardPoolEnum;
import com.bbw.god.mall.cardshop.event.EPDraw;
import com.bbw.god.notify.rednotice.ModuleEnum;
import com.bbw.god.report.ReportEvent;
import com.bbw.god.report.ReportEventType;
import com.bbw.god.report.Reporter;
import lombok.Data;

import java.io.Serializable;

/**
 * 业务结束上报事件
 *
 * @author: suhq
 * @date: 2021/8/17 5:57 下午
 */
@Data
public class BusinessFinishReportEvent extends ReportEvent implements Serializable {
    private static final long serialVersionUID = 3133606249120040894L;
    /** 操作 */
    private String handleName;
    /** 操作数量 */
    private int handleNum;
    /** 消耗 */
    private String consume;
    /** 消耗数量 */
    private int consumeNum;
    /** 获得 */
    private String award;
    /** 获得数量 */
    private int awardNum;

    /**
     * 抽卡上报实体构建
     *
     * @param gu
     * @param ep
     * @return
     */
    public static BusinessFinishReportEvent instanceAsCardDraw(GameUser gu, EPDraw ep) {
        CardPoolEnum cardPool = CardPoolEnum.fromValue(ep.getCardPoolType());
        int consumeNum = ep.getDrawTimes() == 10 ? 9 : ep.getDrawTimes();
        String handleName = ep.getDrawTimes() == 10 ? "十连抽" : "单抽";
        Reporter reporter = Reporter.instance(gu);
        BusinessFinishReportEvent instance = instance(reporter, ModuleEnum.CARD_POOL, cardPool.getName());
        instance.setHandleName(handleName);
        instance.setHandleNum(1);
        instance.setConsume(ep.getCostType().getName());
        instance.setConsumeNum(consumeNum);
        instance.setAward(cardPool.getName() + "*" + ep.getDrawTimes());
        instance.setAwardNum(1);
        return instance;
    }

    /**
     * 商城购买上报实体构建
     *
     * @param gu
     * @param mall
     * @param num
     * @return
     */
    public static BusinessFinishReportEvent instanceAsMallBuy(GameUser gu, CfgMallEntity mall, int num) {
        Reporter reporter = Reporter.instance(gu);
        String businessName = MallEnum.fromValue(mall.getType()).getName();
        BusinessFinishReportEvent instance = instance(reporter, ModuleEnum.MALL, businessName);
        instance.setHandleName(mall.getName());
        instance.setHandleNum(num);
        instance.setConsume(ConsumeType.fromValue(mall.getUnit()).getName());
        instance.setConsumeNum(mall.getPrice() * num);
        instance.setAward(mall.getName());
        instance.setAwardNum(num);
        return instance;
    }

    /**
     * 任务上报实体构建
     *
     * @param gu
     * @param task
     * @return
     */
    public static BusinessFinishReportEvent instanceAsTask(GameUser gu, CfgTaskEntity task) {
        Reporter reporter = Reporter.instance(gu);
        String taskType = TaskTypeEnum.fromValue(task.getType()).getName();
        return instance(reporter, ModuleEnum.TASK, taskType);
    }

    /**
     * 构建上报实体
     *
     * @param reporter
     * @param businessChildType
     * @return
     */
    private static BusinessFinishReportEvent instance(Reporter reporter, ModuleEnum businessType, String businessChildType) {
        BusinessFinishReportEvent reportEvent = new BusinessFinishReportEvent();
        reportEvent.setReporter(reporter);
        reportEvent.setEvent(ReportEventType.BUSINESS_FINISH);
        reportEvent.setBusinessType(businessType);
        reportEvent.setBusinessChildType(businessChildType);
        return reportEvent;
    }

}
