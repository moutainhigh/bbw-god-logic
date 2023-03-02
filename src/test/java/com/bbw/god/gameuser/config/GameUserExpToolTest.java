package com.bbw.god.gameuser.config;

import com.bbw.BaseTest;
import org.junit.Test;

public class GameUserExpToolTest extends BaseTest {

    @Test
    public void getExpByLevel() {
        System.out.println(GameUserExpTool.getExpByLevel(5));
        System.out.println(GameUserExpTool.getExpByLevel(10));
        System.out.println(GameUserExpTool.getExpByLevel(120));
        System.out.println(GameUserExpTool.getExpByLevel(130));
        System.out.println(GameUserExpTool.getExpByLevel(140));
        System.out.println(GameUserExpTool.getExpByLevel(150));
        System.out.println(GameUserExpTool.getExpByLevel(160));
        System.out.println(GameUserExpTool.getExpByLevel(170));
        System.out.println(GameUserExpTool.getExpByLevel(180));
        System.out.println(GameUserExpTool.getExpByLevel(200));
    }
}