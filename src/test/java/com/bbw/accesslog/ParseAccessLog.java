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

public class ParseAccessLog {
    private static List<String> URL_WITH_PARAMS = Arrays.asList(
            "/godLogic/activity!listActivities",
            "/godLogic/activity!listActivities!v2",
            "/godLogic/activity!listRankActivities",
            "/godLogic/card!gainJLCards",
            "/godLogic/cardShop!draw",
            "/godLogic/cofc!taskOption",
            "/godLogic/combat!attackCity",
            "/godLogic/fsfight!listGuInfoForFsFight",
            "/godLogic/fsfight!toMatch",
            "/godLogic/fsfight!syncSxdhCardRefresh",
            "/godLogic/fsfight!getCardInfo",
            "/godLogic/guild!optionMember",
            "/godLogic/guild!optionEightDiagramTask",
            "/godLogic/mall!listProducts",
            "/godLogic/rechargeActivities!list",
            "/godLogic/store!listProducts",
            "/godLogic/task!listTasks",
            "/godLogic/gu!gainTasks",
            "/godLogic/gu!gainTasks!v2",
            "/godLogic/maou!attack");
    private static List<String> PARAMS_TO_SKIP = Arrays.asList(
            "guId=", "uid=", "awardIndex=", "days=",
            "taskId=", "examineid=", "refresh=", "opponentId=",
            "newerGuide=", "rewardBoxCondition=", "cardId=",
            "costType=");

    public static List<String> makeUpAccessLog(List<String> accessLogs) {
        return accessLogs.stream().filter(tmp -> tmp.contains("/godLogic/") && !tmp.contains("/coder/") && !tmp.contains("/gm/") && !tmp.contains("account!") && !tmp.contains("gu!gainUserInfo") && !tmp.contains("gu!gainNewInfo"))
                .map(tmp -> {
                    String[] logParts = tmp.split(" ");
                    String url = logParts[1];
                    String[] urlParts = url.split("\\?");
                    if (logParts.length<3){
                        return "0#0";
                    }
                    String uniqUid = logParts[2].split("\\|")[1];
                    if (uniqUid.contains("&")) {
                        uniqUid = uniqUid.split("&")[0];
                    } else {
                        uniqUid = "0";
                    }
                    //处理无需参数的URL
                    if (urlParts.length == 1 || !URL_WITH_PARAMS.contains(urlParts[0])) {
                        return uniqUid + "#" + urlParts[0];
                    }
                    String[] params = urlParts[1].split("&");
                    boolean containsTK = urlParts[1].contains("tk=");
                    //需要处理的长度，截掉安全参数1
                    if (containsTK && params.length <= 2) {
                        return uniqUid + "#" + urlParts[0];
                    }
                    //需要处理的长度，截掉安全参数2
                    int lengthToHandle = params.length;
                    if (containsTK) {
                        if (url.contains("combat!attackCity") && url.contains("newerGuide")) {
                            lengthToHandle = params.length - 3;
                        } else {
                            lengthToHandle = params.length - 2;
                        }

                    }
                    String result = urlParts[0] + "?";
                    for (int i = 0; i < lengthToHandle; i++) {
                        if (urlParts[0].contains("fsfight!")) {
                            if (params[i].contains("guId=") || params[i].contains("uid=")) {
                                String[] paramParts = params[i].split("=");
                                if (paramParts.length <= 1) {
                                    uniqUid = "0";
                                } else {
                                    uniqUid = paramParts[1];
                                }
                            }
                        }
                        //跳过参数
                        int finalI = i;
                        boolean isParamSkip = PARAMS_TO_SKIP.stream().anyMatch(param -> params[finalI].contains(param));
                        if (isParamSkip || url.contains("fsfight!getCardInfo")) {
                            continue;
                        }
                        if (result.contains("=")) {
                            result += "&";
                        }
                        result += params[i];
                    }
                    return uniqUid + "#" + result;
                }).collect(Collectors.toList());
    }

    public static List<String> makeSql(List<String> accessLogs, int dateInt) {
        Map<String, Integer> accessCount = new HashMap<>();
        accessLogs.forEach(tmp -> {
            Integer count = accessCount.getOrDefault(tmp, 0);
            count++;
            accessCount.put(tmp, count);
        });
        return accessCount.entrySet().stream().map(entry -> {
//            System.out.println(entry.getKey() + "#" + entry.getValue());
            String[] accessInfo = entry.getKey().split("#");
            String uniqUid = accessInfo[0];
            String url = accessInfo[1].substring(10);
            String des = makeDes(url);
            return "INSERT INTO `god_game`.`statistic_in_access_log`(`id`, `uid`, `url`, `des`, `times`, `date`) VALUES (" + ID.INSTANCE.nextId() + ", " + uniqUid + ", '" + url + "', '" + des + "', " + entry.getValue() + "," + dateInt + ");";
        }).collect(Collectors.toList());
    }

    private static String makeDes(String url) {
        String des = CREnum.fromUrl(url.split("\\?")[0]).getMemo();
        if (url.contains("undefined")) {
            return des;
        }
        if (url.contains("activity!listActivities")) {
            if (url.contains("kind")) {
                int kind = Integer.valueOf(url.split("\\?")[1].split("=")[1]);
                des += "——" + ActivityParentTypeEnum.fromValue(kind).getName();
            } else if (url.contains("type")) {
                int type = Integer.valueOf(url.split("\\?")[1].split("=")[1]);
                des += "——" + ActivityEnum.fromValue(type).getName();
            }
            return des;
        }
        if (url.contains("rechargeActivities!list")) {
            if (url.contains("parentType")) {
                int parentType = Integer.valueOf(url.split("\\?")[1].split("=")[1]);
                des += "——" + RechargeActivityEnum.fromVal(parentType).getMemo();
            } else if (url.contains("itemType")) {
                int itemType = Integer.valueOf(url.split("\\?")[1].split("=")[1]);
                des += "——" + RechargeActivityItemEnum.fromVal(itemType).getMemo();
            }
            return des;
        }
        if (url.contains("rechargeActivities!list")) {
            if (url.contains("parentType")) {
                int parentType = Integer.valueOf(url.split("\\?")[1].split("=")[1]);
                des += "——" + RechargeActivityEnum.fromVal(parentType).getMemo();
            } else if (url.contains("itemType")) {
                int itemType = Integer.valueOf(url.split("\\?")[1].split("=")[1]);
                des += "——" + RechargeActivityItemEnum.fromVal(itemType).getMemo();
            }
            return des;
        }
        if (url.contains("combat!attackCity") || url.contains("fsfight!toMatch")) {
            int type = Integer.valueOf(url.split("\\?")[1].split("=")[1]);
            des += "——" + FightTypeEnum.fromValue(type).getName();
            return des;
        }
        if (url.contains("cardShop!draw")) {
            int type = Integer.valueOf(url.split("\\?")[1].split("&")[0].split("=")[1]);
            des += "——" + CardPoolEnum.fromValue(type).getName();
            return des;
        }
        if (url.contains("task!listTasks")) {
            int type = Integer.valueOf(url.split("\\?")[1].split("&")[0].split("=")[1]);
            des += "——" + AchievementTypeEnum.fromValue(type).getName();
            return des;
        }
        if (url.contains("gu!gainTasks")) {
            int type = Integer.valueOf(url.split("\\?")[1].split("&")[0].split("=")[1]);
            des += "——" + TaskTypeEnum.fromValue(type).getName();
            return des;
        }
        if (url.contains("mall!listProducts")) {
            int type = Integer.valueOf(url.split("\\?")[1].split("&")[0].split("=")[1]);
            des += "——" + MallEnum.fromValue(type).getName();
            return des;
        }
        return des;
    }

}