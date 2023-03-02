package com.bbw.god.game.sxdh;

import com.bbw.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class SxdhRoboterServiceTest extends BaseTest {
    @Autowired
    private SxdhRoboterService sxdhRoboterService;

    @Test
    public void matchRoboter(){
        SxdhRoboterService.SxdhMatchedRoboter roboter  = sxdhRoboterService.matchRoboter(190416009900005L, 1);
        System.out.println(roboter);
    }

}