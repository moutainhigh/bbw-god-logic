package com.bbw.accesslog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bbw.common.DateUtil;
import com.bbw.god.fight.FightTypeEnum;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReadAccessLog {

    @Test
    public void searchText() throws IOException {
        String[] andText = {"yaozu!setAttackCards"};
        String[] orText = {"200307225500073","201117206900014","200321225700006","210128237000081","200328225800004","200222225300016","190903223601271","200620226700015","201025227700187","201205228000010","190516031505370","200521235300230","191116224300004","191019224100013","190716233000035","190516031502322","200320225601365","190516000312892","190921223900208","201205228000071","191214224500007","190515210600090","190515210800431","201107227800025","210221228600040","200428226200445","200502226300008","200412226000359","200425226200029","210102228200069","200530235400014","200718226900002","190510210100888","191214224500148","190726223500071","190516106601749","200912227300038","210509238000955","201024236400497","190516106605132","190516000319928","190515209300074","210418237600011","200725235800037","201007227500588","190516207803197","210130228400082","190516106503409","200716226801448","190515212300029","210710229600070","190515209800787","210507238000009","201013227600236","210203228400918","200125224900032","210125228301214","210724229700029","210221228600007","190516106300983"};
        List<String> results = read("/Users/suhq/Desktop/妖族/1", "txt", andText,orText);
        results.addAll(read("/Users/suhq/Desktop/妖族/2", "txt", andText,orText));
        System.out.println("查询结果的条数："+results.size());
        results = results.stream().map(tmp->tmp.split("/godLogic/")[1]).collect(Collectors.toList());
        results.forEach(System.out::println);
    }

    /**
     * 读取一个目录下的以某个文件为末尾的文件的所有行
     * @param fileUrl
     * @param fileSuffix
     * @return
     * @throws IOException
     */
    private List<String> read(String fileUrl, String fileSuffix,String[] andText,String[] orText) throws IOException {
        Path filePath = Paths.get(fileUrl);
        if (!Files.exists(filePath)) {
            System.err.println(filePath + "不存在");
            return new ArrayList<>();
        }
        List<String> results = Files.list(filePath).filter(tmp -> tmp.getFileName().toString().endsWith(fileSuffix))
                .flatMap(tmp -> {
                    try {
                        return Files.readAllLines(tmp).stream().filter(line -> {
                            boolean isAndMatch = true;
                            for (String text : andText) {
                                if (!line.contains(text)) {
                                    isAndMatch = false;
                                    break;
                                }
                            }
                            if (!isAndMatch){
                                return false;
                            }
                            boolean isOrMath = false;
                            for (String text : orText) {
                                if (line.contains(text)) {
                                    isOrMath = true;
                                    break;
                                }
                            }
                            return isOrMath;
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Stream.empty();
                }).collect(Collectors.toList());
        return results;
    }
}
