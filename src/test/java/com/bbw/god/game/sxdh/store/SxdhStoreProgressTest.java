package com.bbw.god.game.sxdh.store;

import com.bbw.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SxdhStoreProgressTest extends BaseTest {
    @Autowired
    private SxdhStoreProgress sxdhStoreProgress;

    @Test
    public void getGoodsList() {
        sxdhStoreProgress.getGoodsList(UID);
    }

}