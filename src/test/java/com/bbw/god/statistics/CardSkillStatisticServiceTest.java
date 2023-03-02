package com.bbw.god.statistics;

import com.bbw.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TODO
 *
 * @author: suhq
 * @date: 2021/9/10 7:14 下午
 */
public class CardSkillStatisticServiceTest extends BaseTest {
    @Autowired
    private CardSkillStatisticService cardSkillStatisticService;

    @Test
    public void doStatistic() {
        cardSkillStatisticService.doStatistic(20210101);
    }
}