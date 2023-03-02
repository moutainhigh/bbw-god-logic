package com.bbw.common;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-13 18:05
 */
public class StrUtilTest {

    @Test
    public void test() {
        String ss = "4星灵石";
        System.out.println(StrUtil.isDigit(ss));
        String serverName = "1区朝歌";
        System.out.println(serverName.split(" ")[0]);
        String data = "%7B%22userDbId%22%3A190515210800045%2C%22uid%22%3A190515210800045%2C%22nickname%22%3A%22%E8%8F%9C%E8%83%8C%E7%AF%BC%22%2C%22headImg%22%3A3030%2C%22lv%22%3A40%2C%22cards%22%3A%5B%7B%22baseId%22%3A413%2C%22lv%22%3A23%2C%22hv%22%3A10%2C%22skill0%22%3A0%2C%22skill5%22%3A3124%2C%22skill10%22%3A3112%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%2C%7B%22baseId%22%3A136%2C%22lv%22%3A14%2C%22hv%22%3A6%2C%22skill0%22%3A0%2C%22skill5%22%3A3201%2C%22skill10%22%3A101%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%2C%7B%22baseId%22%3A324%2C%22lv%22%3A10%2C%22hv%22%3A8%2C%22skill0%22%3A1004%2C%22skill5%22%3A0%2C%22skill10%22%3A3113%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%2C%7B%22baseId%22%3A340%2C%22lv%22%3A0%2C%22hv%22%3A0%2C%22skill0%22%3A0%2C%22skill5%22%3A0%2C%22skill10%22%3A0%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%2C%7B%22baseId%22%3A210%2C%22lv%22%3A19%2C%22hv%22%3A10%2C%22skill0%22%3A3102%2C%22skill5%22%3A0%2C%22skill10%22%3A3113%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%2C%7B%22baseId%22%3A238%2C%22lv%22%3A23%2C%22hv%22%3A9%2C%22skill0%22%3A0%2C%22skill5%22%3A4301%2C%22skill10%22%3A4113%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%2C%7B%22baseId%22%3A222%2C%22lv%22%3A11%2C%22hv%22%3A9%2C%22skill0%22%3A3101%2C%22skill5%22%3A0%2C%22skill10%22%3A0%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%2C%7B%22baseId%22%3A135%2C%22lv%22%3A10%2C%22hv%22%3A2%2C%22skill0%22%3A0%2C%22skill5%22%3A4104%2C%22skill10%22%3A0%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%2C%7B%22baseId%22%3A333%2C%22lv%22%3A0%2C%22hv%22%3A0%2C%22skill0%22%3A0%2C%22skill5%22%3A0%2C%22skill10%22%3A0%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%2C%7B%22baseId%22%3A519%2C%22lv%22%3A15%2C%22hv%22%3A10%2C%22skill0%22%3A0%2C%22skill5%22%3A3201%2C%22skill10%22%3A0%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%2C%7B%22baseId%22%3A223%2C%22lv%22%3A12%2C%22hv%22%3A10%2C%22skill0%22%3A0%2C%22skill5%22%3A0%2C%22skill10%22%3A4302%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%2C%7B%22baseId%22%3A112%2C%22lv%22%3A22%2C%22hv%22%3A10%2C%22skill0%22%3A3201%2C%22skill5%22%3A1202%2C%22skill10%22%3A3121%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%2C%7B%22baseId%22%3A106%2C%22lv%22%3A13%2C%22hv%22%3A3%2C%22skill0%22%3A4112%2C%22skill5%22%3A4113%2C%22skill10%22%3A0%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%2C%7B%22baseId%22%3A318%2C%22lv%22%3A19%2C%22hv%22%3A10%2C%22skill0%22%3A2201%2C%22skill5%22%3A0%2C%22skill10%22%3A0%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%2C%7B%22baseId%22%3A310%2C%22lv%22%3A23%2C%22hv%22%3A10%2C%22skill0%22%3A3103%2C%22skill5%22%3A0%2C%22skill10%22%3A1002%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%2C%7B%22baseId%22%3A322%2C%22lv%22%3A16%2C%22hv%22%3A9%2C%22skill0%22%3A0%2C%22skill5%22%3A3103%2C%22skill10%22%3A0%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%2C%7B%22baseId%22%3A527%2C%22lv%22%3A0%2C%22hv%22%3A0%2C%22skill0%22%3A0%2C%22skill5%22%3A0%2C%22skill10%22%3A0%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%2C%7B%22baseId%22%3A532%2C%22lv%22%3A0%2C%22hv%22%3A0%2C%22skill0%22%3A0%2C%22skill5%22%3A0%2C%22skill10%22%3A0%2C%22attackSymbol%22%3A0%2C%22defenceSymbol%22%3A0%2C%22isUseSkillScroll%22%3A0%7D%5D%7D";
        try {
            String decodeData = URLDecoder.decode(data, "utf-8");
            System.out.println(decodeData);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
