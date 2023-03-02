package com.bbw.god.activity;

import com.bbw.BaseTest;
import com.bbw.god.rd.RDSuccess;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ActivityLogicTest extends BaseTest {
    @Autowired
    private ActivityLogic activityLogic;

    @Test
    public void getActivities() {
        RDSuccess rd = this.activityLogic.getActivities(190422009900008L, 99, 40 + "", 20 + "");
        System.out.println(rd.toString());
    }

    @Test
    public void joinActivity() {
    }

    @Test
    public void replenish() {
    }

    @Test
    public void getAwardNum() {
    }
}