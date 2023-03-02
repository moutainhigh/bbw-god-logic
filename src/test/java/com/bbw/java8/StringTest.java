package com.bbw.java8;

import com.bbw.common.FileUtil;
import com.bbw.common.HexByteConveter;
import com.bbw.common.ListUtil;
import com.bbw.god.security.param.SecurityParamKey;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月28日 上午2:59:40
 */
public class StringTest {


    @Test
    public void testStr() throws UnsupportedEncodingException {
        byte[] bytes = HexByteConveter.hex2Byte("e2eb7bec0f92b7b498be6ec384733da79d237dceafdc689b8829cd939eb261e2");
        System.out.println(HexByteConveter.byte2Hex(bytes));
        System.out.println(Integer.valueOf("0099"));
        String src = ";;;10,30;";
        String[] splitArray = src.split(";");
        for (int i = 0; i < splitArray.length; i++) {
            System.out.println(i + ":" + splitArray[i]);
            List<Integer> cardIds = ListUtil.parseStrToInts(splitArray[i]);
            System.out.println("cardIds:" + cardIds);
        }

        System.out.println(URLEncoder.encode("[{\"uid\":190416009900005,\"bt\":1,\"si\":\"c9122a7d-38b7-4d9e-9109-b30dce763eac\",\"account\":\"sdsa@qq.com\"},{\"uid\":200227009900007,\"bt\":0,\"si\":\"f0a6ccaa-0808-41bc-a9cb-0f68b1b0582f\",\"account\":\"sdsa001@qq.com\"}]", "utf-8"));

    }

    @Test
    public void test() {
        long timestamp = System.currentTimeMillis();
        System.out.println(timestamp);
        System.out.println(SecurityParamKey.idToParamKey(timestamp));
        String src = "";
        String[] splitArray = src.split(",");
        for (int i = 0; i < splitArray.length; i++) {
            System.out.println(i + ":" + Integer.valueOf(splitArray[i]));
//            List<Integer> cardIds = ListUtil.parseStrToInts(splitArray[i]);
//            System.out.println("cardIds:" + cardIds);
        }

    }

    //	@Test
    public void writeStringToFile() {
        long beginTime = System.currentTimeMillis();
        List<String> srcWords = FileUtil.readFileLines("srcWordFilter.txt");
        srcWords.forEach(tmp -> {
            String replacedStr = tmp.replace("，", ",");
            String[] strs = replacedStr.split(",");
            for (String str : strs) {
                FileUtil.appendFileLines("newWordFilter.txt", str);
            }
        });
        System.out.println("使用时间：" + (System.currentTimeMillis() - beginTime));
    }

//    @Test
    public void appendNewWordToWfc() {
        long beginTime = System.currentTimeMillis();
        List<String> words = FileUtil.readFileLines("META-INF/dic/wfc.dic");
        List<String> newWords = FileUtil.readFileLines("META-INF/dic/wfc20221213.dic");
        List<String> wordsToAppend = new ArrayList<>();
        newWords.forEach(tmp -> {
            if (tmp != "" && !words.contains(tmp)) {
                wordsToAppend.add(tmp);
            }
        });
        FileUtil.appendFileLines("E:\\ideaProject\\BambooWind\\codeup\\bbw-god-game-develop\\src\\main\\resources\\META-INF\\dic\\wfc.dic", wordsToAppend);
        System.out.println("使用时间：" + (System.currentTimeMillis() - beginTime));
    }

}
