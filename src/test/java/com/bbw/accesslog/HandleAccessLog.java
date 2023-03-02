package com.bbw.accesslog;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityParentTypeEnum;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.CREnum;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.gameuser.achievement.AchievementTypeEnum;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.mall.cardshop.CardPoolEnum;
import com.bbw.god.rechargeactivities.RechargeActivityEnum;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HandleAccessLog {
    private static String SRC_ROOT_PATH = "/Users/suhq/Desktop/访问日志/src/toMakeSql/";
    private static String SQL_ROOT_PATH = "/Users/suhq/Desktop/访问日志/sql/toDB/";


    @Test
    public void handleAccessLog() throws IOException {
        long start = System.currentTimeMillis();
        Path srcRoot = Paths.get(SRC_ROOT_PATH);
        Stream<Path> walk = Files.walk(srcRoot, 1, FileVisitOption.FOLLOW_LINKS);
        List<Integer> dates = new ArrayList<>();
        walk.forEach(from -> {
            String fileName = from.getFileName().toString();
            if (!fileName.startsWith("localhost_access_log.")) {
                return;
            }
            try {
                String middlePart = fileName.split("\\.")[1];
                Path to = Paths.get(SQL_ROOT_PATH + "statistic." + middlePart + ".sql");
                if (Files.exists(to)) {
                    Files.delete(to);
                }
                Files.createFile(to);
                List<String> accessLogs = Files.readAllLines(from);
                accessLogs = ParseAccessLog.makeUpAccessLog(accessLogs);
                int dateInt = DateUtil.toDateInt(DateUtil.fromDateString(middlePart.substring(0, 10)));
                dates.add(dateInt);
                List<String> sqls = ParseAccessLog.makeSql(accessLogs, dateInt);
                Files.write(to, sqls, StandardOpenOption.APPEND);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        merge(dates);
        long end = System.currentTimeMillis() - start;
        System.out.println("执行时间(ms)：" + end);
    }

    private void merge(List<Integer> dates) throws IOException {
        int min = dates.stream().min(Integer::compareTo).get();
        int max = dates.stream().max(Integer::compareTo).get();
        Path to = Paths.get(SQL_ROOT_PATH + "final_statistic." + min + "." + max + ".sql");
        if (Files.exists(to)) {
            Files.delete(to);
        }
        Files.createFile(to);
        Path sqlRoot = Paths.get(SQL_ROOT_PATH);
        Stream<Path> walk = Files.walk(sqlRoot, 1, FileVisitOption.FOLLOW_LINKS);
        walk.forEach(from -> {
            String fileName = from.getFileName().toString();
            if (!fileName.startsWith("statistic.")) {
                return;
            }
            try {
                List<String> sqls = Files.readAllLines(from);
                Files.write(to, sqls, StandardOpenOption.APPEND);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

}