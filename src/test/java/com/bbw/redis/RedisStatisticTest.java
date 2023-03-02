package com.bbw.redis;

import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.server.ServerDataType;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class RedisStatisticTest {

    @Test
    public void makeSql() {
        for (UserDataType userDataType : UserDataType.values()) {
//            System.out.println(userDataType.getRedisKey());
            System.out.println("SELECT COUNT(1),AVG(size_in_bytes) as " + userDataType.getRedisKey() + ", COUNT(1)*AVG(size_in_bytes)/1024/1024 FROM logic_redis_memory WHERE `key` LIKE '%:" + userDataType.getRedisKey() + ":%' and `key` LIKE '%usr:%';");
        }
        System.out.println();
        System.out.println();
        for (ServerDataType serverDataType : ServerDataType.values()) {
//            System.out.println(serverDataType.getRedisKey());
            System.out.println("SELECT COUNT(1),AVG(size_in_bytes) as " + serverDataType.getRedisKey() + ",COUNT(1)*AVG(size_in_bytes)/1024/1024 FROM logic_redis_memory WHERE `key` LIKE '%:" + serverDataType.getRedisKey() + ":%' and `key` LIKE '%server:%';");
        }
        System.out.println();
        System.out.println();
        for (UserDataType userDataType : UserDataType.values()) {
            System.out.println(userDataType.getRedisKey());
        }
        System.out.println();
        System.out.println();
        for (ServerDataType serverDataType : ServerDataType.values()) {
            System.out.println(serverDataType.getRedisKey());
        }
    }


    @Test
    public void statistic() throws IOException {
        //        doStatisticForServer();
        //        doStatisticForUser();
        doStatisticForGame();
    }

    /**
     * 玩家数据key统计
     *
     * @throws IOException
     */
    public void doStatisticForUser() throws IOException {
        long begin = System.currentTimeMillis();
        List<String> lines = read("/Users/suhq/Downloads/redis-key-2023-02-20.txt");
        System.out.println("集合读取耗时(ms)：" + (System.currentTimeMillis() - begin));
        Set<String> uids = new HashSet<>();
        Map<String, Integer> groupResults = new TreeMap<>();
        Map<String, Long> groupUsedMemory = new TreeMap<>();
        for (String line : lines) {
            String[] lineParts = line.split(",");
            String key = lineParts[2];
            String usedMemory = lineParts[3];
            if (!key.startsWith("usr")) {
                continue;
            }
            String[] keyParts = key.split(":");
            if (keyParts.length != 4) {
                continue;
            }
            //数量统计
            int num = groupResults.getOrDefault(keyParts[2], 0);
            num++;
            groupResults.put(keyParts[2], num);

            //内存统计
            Long totalUsedMemory = groupUsedMemory.getOrDefault(keyParts[2], 0L);
            totalUsedMemory += Long.valueOf(usedMemory);
            groupUsedMemory.put(keyParts[2], totalUsedMemory);

            uids.add(keyParts[1]);
        }
        System.out.println("数据分组耗时(ms)：" + (System.currentTimeMillis() - begin));
        System.out.println("key数量：" + lines.size());
        System.out.println("玩家数：" + uids.size());
        List<Map.Entry<String, Integer>> results = groupResults.entrySet().stream()
                                                               .sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
                                                               .collect(Collectors.toList());
        for (Map.Entry<String, Integer> result : results) {
            System.out.println(result.getKey() + " 数量:" + result.getValue() + " 内存占用(kb):" + groupUsedMemory.get(result.getKey()) / 1024);

        }
    }

    /**
     * 区服数据key统计
     *
     * @throws IOException
     */
    public void doStatisticForServer() throws IOException {
        long begin = System.currentTimeMillis();
        List<String> lines = read("/Users/suhq/Downloads/redis-key-2023-02-20.txt");
        System.out.println("集合读取：" + (System.currentTimeMillis() - begin));
        Map<String, Integer> groupResults = new TreeMap<>();
        Map<String, Long> groupUsedMemory = new TreeMap<>();
        for (String line : lines) {
            String[] lineParts = line.split(",");
            String key = lineParts[2];
            String usedMemory = lineParts[3];
            if (!key.startsWith("server")) {
                continue;
            }
            String[] keyParts = key.split(":");
            if (keyParts.length < 4 || keyParts.length > 5) {
                continue;
            }
            String keyPart = "";
            if (keyParts.length == 4) {
                keyPart = keyParts[2];
            } else if (line.contains("maou")) {
                keyPart = keyParts[4].substring(0, keyParts[4].length() - 1);
            } else {
                continue;
            }
            //数量统计
            int num = groupResults.getOrDefault(keyPart, 0);
            num++;
            groupResults.put(keyPart, num);

            //内存统计
            Long totalUsedMemory = groupUsedMemory.getOrDefault(keyPart, 0L);
            totalUsedMemory += Long.valueOf(usedMemory);
            groupUsedMemory.put(keyPart, totalUsedMemory);
        }
        System.out.println("数据分组：" + (System.currentTimeMillis() - begin));
        List<Map.Entry<String, Integer>> results = groupResults.entrySet().stream()
                                                               .sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
                                                               .collect(Collectors.toList());
        for (Map.Entry<String, Integer> result : results) {
            System.out.println(result.getKey() + " 数量:" + result.getValue() + " 内存占用(kb):" + groupUsedMemory.get(result.getKey()) / 1024);
        }
    }

    /**
     * 区服数据key统计
     *
     * @throws IOException
     */
    public void doStatisticForGame() throws IOException {
        long begin = System.currentTimeMillis();
        List<String> lines = read("/Users/suhq/Downloads/redis-key-2023-02-20.txt");
        System.out.println("集合读取：" + (System.currentTimeMillis() - begin));
        long totalUsedMemory = 0L;
        long totalKeyNum = 0L;
        Map<String, Long> groupUsedMemory = new TreeMap<>();
        for (String line : lines) {
            String[] lineParts = line.split(",");
            String key = lineParts[2];
            String usedMemory = lineParts[3];
            if (!key.startsWith("game")) {
                continue;
            }
            String[] keyParts = key.split(":");
            String keyPart = "";
            if (keyParts.length == 4) {
                keyPart = keyParts[2];
            } else if (keyParts.length == 3) {
                keyPart = keyParts[1];
            } else {
                keyPart = keyParts[1];
            }
            // 数量统计
            totalKeyNum += 1;
            // 内存统计
            totalUsedMemory += Long.valueOf(usedMemory);
            Long dataUsedMemory = groupUsedMemory.getOrDefault(keyPart, 0L);
            dataUsedMemory += Long.valueOf(usedMemory);
            groupUsedMemory.put(keyPart, dataUsedMemory);
        }
        System.out.println("数据分组：" + (System.currentTimeMillis() - begin));
        System.out.println("跨服数据 数量:" + totalKeyNum + " 内存占用(kb):" + totalUsedMemory / 1024);
        List<Map.Entry<String, Long>> results = groupUsedMemory.entrySet().stream()
                                                               .sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
                                                               .collect(Collectors.toList());
        for (Map.Entry<String, Long> result : results) {
            System.out.println(result.getKey() + " 内存占用(kb):" + result.getValue() / 1024);
        }

    }

    private List<String> read(String fileUrl, String prefix) throws IOException {
        Path filePath = Paths.get(fileUrl);
        if (!Files.exists(filePath)) {
            System.err.println(filePath + "不存在");
            return new ArrayList<>();
        }
        return Files.readAllLines(filePath).stream().filter(tmp -> tmp.startsWith(prefix)).collect(Collectors.toList());
    }

    private List<String> read(String fileUrl) throws IOException {
        Path filePath = Paths.get(fileUrl);
        if (!Files.exists(filePath)) {
            System.err.println(filePath + "不存在");
            return new ArrayList<>();
        }
        return Files.readAllLines(filePath);
    }
}