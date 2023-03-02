package com.bbw.java8;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bbw.common.JSONUtil;
import com.bbw.common.StrUtil;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.gameuser.card.UserCard;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileTest {

    @Test
    public void readFile() throws URISyntaxException {
        URI url = FileTest.class.getClassLoader().getResource("config/game/nicknames.txt").toURI();
        System.out.println(url.getPath());
        Path path = Paths.get(url);
        if (!Files.exists(path)) {
            System.out.println(url + "不存在");
            return;
        }
        List<String> nicknames = new ArrayList<>();
        if (Files.exists(path)) {
            List<String> lines;
            try {
                lines = Files.readAllLines(path);
                nicknames = lines.stream().flatMap(line -> Stream.of(line.split(","))).collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        nicknames.forEach(nickname -> System.out.println(nickname));
    }

    /**
     * 读取删除日志文件，并输出sql语句
     */
    @Test
    public void readDelLog() {
        String url = "/Users/suhq/Desktop/卡牌删除数据.log";
        String godServer = "god_server_200057";
        int sid = 2257;
        String dataType = "card";
        Path path = Paths.get(url);
        if (!Files.exists(path)) {
            System.out.println(url + "不存在");
            return;
        }
        List<String> results = new ArrayList<>();
        if (Files.exists(path)) {
            List<String> lines;
            try {
                lines = Files.readAllLines(path);
                results = lines.stream().map(line -> {
                    JSONObject js = JSON.parseObject(line);
                    long uid = js.getLongValue("gameUserId");
                    long id = js.getLongValue("id");
                    String result = "INSERT INTO `" + godServer + "`.`ins_user_data`(`data_id`, `sid`, `uid`, `data_type`, `data_json`) VALUES (";
                    result += id + "," + sid + "," + uid + ",'" + dataType + "','" + line + "');";
                    return result;
                }).collect(Collectors.toList());
                List<JSONObject> jss = lines.stream().map(line -> JSON.parseObject(line)).collect(Collectors.toList());
                Map<Long, List<String>> groups = jss.stream().collect(Collectors.groupingBy(tmp -> tmp.getLongValue("gameUserId"), Collectors.mapping(tmp -> tmp.getLongValue("id") + "", Collectors.toList())));
                List<String> dels = groups.keySet().stream().map(tmp -> {
                    Long uid = tmp;
                    String ids = groups.get(tmp).stream().collect(Collectors.joining(","));
                    return "DELETE FROM `" + godServer + "`.`ins_user_data` WHERE uid=" + uid + " and data_id IN (" + ids + ");";
                }).collect(Collectors.toList());
                results.addAll(0, dels);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        results.forEach(result -> System.out.println(result));
    }

    /**
     * 从卡牌json数据解析等级和阶级信息
     */
    @Test
    public void readFromCardJson() {
        String url = "/Users/suhq/Desktop/卡牌数据.txt";
        Path path = Paths.get(url);
        if (!Files.exists(path)) {
            System.out.println(url + "不存在");
            return;
        }
        List<String> results = new ArrayList<>();
        if (Files.exists(path)) {
            List<String> lines;
            try {
                lines = Files.readAllLines(path);
                results = lines.stream().map(line -> {
                    UserCard userCard = JSONUtil.fromJson(line, UserCard.class);
                    return userCard.getBaseId() + "," + userCard.getLevel() + "," + userCard.getExperience() + "," + userCard.getHierarchy();
                }).collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        String finalResult = results.stream().collect(Collectors.joining(";"));
        System.out.println(finalResult);
    }

    @Test
    public void readLostUserFightResultData() throws IOException {
        String uidUrl = "/Users/suhq/Desktop/流失玩家ID.txt";
        String dataUrl = "/Users/suhq/Desktop/流失玩家战斗胜负.txt";
        Path uidPath = Paths.get(uidUrl);
        Path dataPath = Paths.get(dataUrl);
        if (!Files.exists(uidPath)) {
            System.err.println(uidUrl + "不存在");
            return;
        }
        if (!Files.exists(dataPath)) {
            System.err.println(dataPath + "不存在");
            return;
        }
        List<String> uids = Files.readAllLines(uidPath);
        List<String> datas = Files.readAllLines(dataPath);
        for (String uid : uids) {
            String result = "打野怪负\t%s\t打野怪胜\t%s\t打野怪胜率\t%s\t攻城负\t%s\t攻城胜\t%s\t攻城胜率\t%s\t练兵负\t%s\t练兵胜\t%s\t练兵胜率\t%s ";
            int ygWin = 0, ygFail = 0, attackWin = 0, attackFail = 0, trainingWin = 0, trainingFail = 0;
            double ygWinRate = 0.0, attackWinRate = 0.0, trainingWinRate = 0.0;
            for (String data : datas) {
                String[] dataSplit = data.split("\t");
                if (dataSplit[0].equals(uid)) {
                    switch (FightTypeEnum.fromValue(Integer.valueOf(dataSplit[1]))) {
                        case YG:
                            if (dataSplit[2].equals("胜")) {
                                ygWin = Integer.valueOf(dataSplit[3]);
                            } else {
                                ygFail = Integer.valueOf(dataSplit[3]);
                            }
                            break;
                        case ATTACK:
                            if (dataSplit[2].equals("胜")) {
                                attackWin = Integer.valueOf(dataSplit[3]);
                            } else {
                                attackFail = Integer.valueOf(dataSplit[3]);
                            }
                            break;
                        case TRAINING:
                            if (dataSplit[2].equals("胜")) {
                                trainingWin = Integer.valueOf(dataSplit[3]);
                            } else {
                                trainingFail = Integer.valueOf(dataSplit[3]);
                            }
                            break;
                    }
                    if (ygWin + ygFail > 0) {
                        ygWinRate = ygWin * 1.0 / (ygWin + ygFail);
                    }
                    if (attackWin + attackFail > 0) {
                        attackWinRate = attackWin * 1.0 / (attackWin + attackFail);
                    }
                    if (trainingWin + trainingFail > 0) {
                        trainingWinRate = trainingWin * 1.0 / (trainingWin + trainingFail);
                    }
                }

            }
            String finalResult = String.format(result, ygFail, ygWin, ygWinRate, attackFail, attackWin, attackWinRate, trainingFail, trainingWin, trainingWinRate);
            System.out.println(uid + "\t" + finalResult);
        }
    }

    @Test
    public void readLostUserCardUpdateData() throws IOException {
        String uidUrl = "/Users/suhq/Desktop/流失玩家ID.txt";
        String dataUrl = "/Users/suhq/Desktop/流失玩家升级频率.txt";
        Path uidPath = Paths.get(uidUrl);
        Path dataPath = Paths.get(dataUrl);
        if (!Files.exists(uidPath)) {
            System.err.println(uidUrl + "不存在");
            return;
        }
        if (!Files.exists(dataPath)) {
            System.err.println(dataPath + "不存在");
            return;
        }
        List<String> uids = Files.readAllLines(uidPath);
        List<String> datas = Files.readAllLines(dataPath);
        for (String uid : uids) {
            String result = "0";
            for (String data : datas) {
                String[] dataSplit = data.split("\t");
                if (dataSplit[0].equals(uid)) {
                    result = dataSplit[1];
                    break;
                }

            }
            System.out.println(uid + "\t" + result);
        }
    }

    @Test
    public void searchText() throws IOException {
        String[] texts = {"yaozu!setAttackCards", ""};
        List<String> results = read("/Users/suhq/Desktop/妖族/1", "txt", texts);
        results.addAll(read("/Users/suhq/Desktop/妖族/2", "txt", texts));

        Map<String, Long> resultGroups = results.stream().collect(Collectors.groupingBy(String::valueOf, Collectors.counting()));
        for (String exchangeInfo : resultGroups.keySet()) {
            System.out.println(exchangeInfo + "==========" + resultGroups.get(exchangeInfo));
        }
    }

    private List<String> read(String fileUrl, String fileSuffix, String... searchTexts) throws IOException {
        Path filePath = Paths.get(fileUrl);
        if (!Files.exists(filePath)) {
            System.err.println(filePath + "不存在");
            return new ArrayList<>();
        }
        List<String> results = Files.list(filePath).filter(tmp -> tmp.getFileName().toString().endsWith(fileSuffix))
                .flatMap(tmp -> {
                    try {
                        List<String> strings = Files.readAllLines(tmp);
                        return strings.stream().filter(line -> {
                            boolean isMatch = true;
                            for (String text : searchTexts) {
                                if (!line.contains(text)) {
                                    isMatch = false;
                                    break;
                                }
                            }
                            return isMatch;
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Stream.empty();
                }).collect(Collectors.toList());
        return results;
    }

    @Test
    public void paseLoginRequestData() throws IOException {
        String url = "/Users/suhq/Desktop/神秘.txt";
        Path path = Paths.get(url);
        if (!Files.exists(path)) {
            System.out.println(url + "不存在");
            return;
        }
        List<String> results = Files.readAllLines(path);
        results = results.stream().filter(tmp -> StrUtil.isNotBlank(tmp)).collect(Collectors.toList());
        Map<String, Long> resultMap = results.stream()
//                .map(tmp -> tmp.split("==========")[1])
                .collect(Collectors.groupingBy(String::toString, Collectors.counting()));
        List<String> finalReults = new ArrayList<>();
        for (String s : resultMap.keySet()) {
            if (resultMap.get(s) < 10) {
                continue;
            }
            finalReults.add(s + "\t" + resultMap.get(s));
        }
        finalReults.sort(Comparator.comparing(String::valueOf));
        finalReults.forEach(System.out::println);
    }

}
