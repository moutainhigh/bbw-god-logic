package com.bbw.god.gameuser;

import com.bbw.BaseTest;
import com.bbw.god.rd.RDAdvance;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SimulateShakingTest extends BaseTest {
    @Autowired
    private GameUserShakeLogic gameUserShakeLogic;

    @Test
    public void shakeDice() {
        SimulateResult result = new SimulateResult();
        for (int i = 0; i <= 100; i++) {
            long begin = System.currentTimeMillis();
            RDAdvance rdAdvance = this.gameUserShakeLogic.shakeDice(UID, 1);
            System.out.println("--------" + (System.currentTimeMillis() - begin));
        }
    }
}