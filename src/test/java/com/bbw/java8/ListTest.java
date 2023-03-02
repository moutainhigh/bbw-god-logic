package com.bbw.java8;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.WayTool;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月28日 上午2:59:40
 */
public class ListTest {

    @Test
    public void testList() {
        List<String> keys = new ArrayList<>();
        long uid = 210506237901515L;
        for (int i = 0; i < 1000; i++) {
            keys.add("usr:" + uid + ":0statistic:resource:70:0");
            keys.add("usr:" + uid + ":0statistic:resource:70:0");
            uid++;
        }
        WayTool.init();
        long begin = System.currentTimeMillis();
        Map<Long, List<String>> uidKeysMap = new HashMap<>();

        for (String key : keys) {
            Long uid2 = Long.valueOf(key.split(SPLIT)[1]);
            if (!uidKeysMap.containsKey(uid2)) {
                uidKeysMap.put(uid2, new ArrayList<>());
            }
            uidKeysMap.get(uid2).add(key);
        }
        System.out.println(System.currentTimeMillis() - begin);
        for (Long uid2 : uidKeysMap.keySet()) {
            keys.stream().filter(key -> key.contains(uid2.toString())).collect(Collectors.toList());
        }
        System.out.println(System.currentTimeMillis() - begin);
        for (int i = 0; i < 1000; i++) {
            WayEnum wayEnum = WayEnum.fromName("法外分身升级");
        }
        System.out.println(System.currentTimeMillis() - begin);

    }

    @Test
    public void test() {
        LinkedList<String> list = new LinkedList();
        list.addFirst("h");
        list.addLast("e");
        list.addLast("e");
        list.addLast("h");
        System.out.println(list);
        System.out.println(list.get(0));
        System.out.println(list.indexOf("e"));
        System.out.println(list.lastIndexOf("e"));
        System.out.println(list.offer("e"));
        System.out.println(list.peek());
        System.out.println(list.peek());
        System.out.println(list.poll());
        System.out.println(list);
        System.out.println(list.pop());
        list.push("eee");
        System.out.println(list);
    }

}
