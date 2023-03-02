package com.bbw.god.game.wanxianzhen.service;

import com.bbw.exception.ErrorLevel;
import com.bbw.god.game.wanxianzhen.RDWanXianJob;
import com.bbw.god.game.wanxianzhen.WanXianTool;
import com.bbw.mc.mail.MailAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 万仙阵结算服务
 *
 * @author suhq
 * @date 2023/2/17 10:32 下午
 */
@Slf4j
@Component
public class WanXianJobService {
    @Autowired
    private WanXianRaceFactory factory;
    @Autowired
    private WanXianLogic wanXianLogic;
    @Autowired
    private MailAction mailAction;
    /** 万仙阵赛制类型：常规赛、特色赛 */
    private static final int[] types = {WanXianLogic.TYPE_REGULAR_RACE, WanXianLogic.TYPE_SPECIAL_RACE};

    /**
     * 结束报名
     */
    public void endSignUp() {
        String content = "今日万仙阵报名截止执行定时器：\n";
        List<Integer> serverGroups = WanXianTool.getOpenedServerGroups();
        long time1 = System.currentTimeMillis();
        boolean error = false;
        for (int type : types) {
            for (Integer servereGroup : serverGroups) {
                try {
                    RDWanXianJob rd = RDWanXianJob.instance(servereGroup, type);
                    wanXianLogic.updateWanXianCardInfo(servereGroup, type, rd);
                    log.info(rd.getMsg());
                    content += rd.getMsg() + "\n";
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    content += "赛制" + type + "--区服组" + servereGroup + "-----异常执行\n";
                    error = true;
                }
            }
            log.info("----------------");
            content += "-----------------\n";
        }
        String timeStr = "执行赛事总耗时：" + (System.currentTimeMillis() - time1);
        content += "-----------------\n";
        content += timeStr;
        log.info(timeStr);
        if (error) {
            mailAction.notifyCoder(ErrorLevel.HIGH, "万仙阵报名截止定时器执行结果：", content);
        } else {
            mailAction.notifyCoder("万仙阵报名截止定时器执行结果：", content);
        }

    }

    /**
     * 战斗执行
     *
     * @param weekday
     * @param isBefore12Hour
     */
    public void fightJob(int weekday, boolean isBefore12Hour) {
        //星期一12点前不执行
        if (weekday == 1 && isBefore12Hour) {
            return;
        }
        String content = "万仙阵战斗定时器执行结果：\n";
        List<Integer> groupIds = WanXianTool.getOpenedServerGroups();
        long time1 = System.currentTimeMillis();
        boolean error = false;
        for (int type : types) {
            for (Integer group : groupIds) {
                try {
                    factory.matchByWeekDay(weekday).beginTodayAllRace(weekday, group, type);
                    log.info(type + "--" + group + "--执行正常");
                    content += type + "--" + group + "--执行正常" + "\n";
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    content += type + "--" + group + "-----异常执行\n";
                    error = true;
                }
            }
            log.info("----------------");
            content += "-----------------\n";
        }
        String timeStr = "执行赛事总耗时：" + (System.currentTimeMillis() - time1);
        content += "-----------------\n";
        content += timeStr;
        log.info(timeStr);
        if (error) {
            mailAction.notifyCoder(ErrorLevel.HIGH, "万仙阵战斗定时器执行结果：", content);
        } else {
            mailAction.notifyCoder("万仙阵战斗定时器执行结果：", content);
        }
        System.err.println(content);
    }

    /**
     * 淘汰邮件定时器
     *
     * @param weekday
     * @param val
     */
    public void sendEliminationMailJob(int weekday, String val) {
        int order = Integer.parseInt(val);
        String content = "万仙阵淘汰邮件执行定时器：\n";
        List<Integer> groupIds = WanXianTool.getOpenedServerGroups();
        long time1 = System.currentTimeMillis();
        boolean error = false;
        for (int type : types) {
            for (Integer group : groupIds) {
                try {
                    factory.matchByWeekDay(weekday).sendEliminateMail(group, type, order);
                    log.info(type + "--" + group + "--执行正常");
                    content += type + "--" + group + "--执行正常" + "\n";
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    content += type + "--" + group + "-----异常执行\n";
                    error = true;
                }
            }
            log.info("----------------");
            content += "-----------------\n";
        }
        String timeStr = "执行赛事总耗时：" + (System.currentTimeMillis() - time1);
        content += "-----------------\n";
        content += timeStr;
        log.info(timeStr);
        if (error) {
            mailAction.notifyCoder(ErrorLevel.HIGH, "万仙阵淘汰定时器执行结果：", content);
        } else {
            mailAction.notifyCoder("万仙阵淘汰定时器执行结果：", content);
        }
    }

    /**
     * 邮件定时器
     */
    public void sendMailJob(int weekday) {
        String content = "万仙阵战报邮件执行定时器：\n";
        List<Integer> groupIds = WanXianTool.getOpenedServerGroups();
        long time1 = System.currentTimeMillis();
        boolean error = false;
        for (int type : types) {
            for (Integer group : groupIds) {
                try {
                    factory.matchByWeekDay(weekday).sendMail(group, type);
                    log.info(type + "--" + group + "--执行正常");
                    content += type + "--" + group + "--执行正常" + "\n";
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    content += type + "--" + group + "-----异常执行\n";
                    error = true;
                }
            }
            log.info("----------------");
            content += "-----------------\n";
        }
        String timeStr = "执行赛事总耗时：" + (System.currentTimeMillis() - time1);
        content += "-----------------\n";
        content += timeStr;
        log.info(timeStr);
        if (error) {
            mailAction.notifyCoder(ErrorLevel.HIGH, "万仙阵战报定时器执行结果：", content);
        } else {
            mailAction.notifyCoder("万仙阵战报定时器执行结果：", content);
        }
    }
}
