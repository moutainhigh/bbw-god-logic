package com.bbw.god.city.yeg;

import com.bbw.BaseTest;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.city.YgTool;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class YeGBoxServiceTest extends BaseTest {
    @Autowired
    private YeGBoxService yeGBoxService;

    @Test
    public void getAwards() {
        List<String> results = new ArrayList<>();
        int num = 10000;
        List<CfgYeGuai.YeGBoxConfig> yeGBoxs = YgTool.getYgConfig().getYeGBoxs();
        for (CfgYeGuai.YeGBoxConfig yeGBox : yeGBoxs) {
            int awardCardTimes = 0;
            for (int i = 0; i < num; i++) {
                List<Award> awards = yeGBoxService.getAward(UID, yeGBox.getBoxKey());
                if (awards.stream().anyMatch(tmp -> tmp.getItem() == AwardEnum.KP.getValue())) {
                    awardCardTimes++;
                }
            }
            results.add(yeGBox.getBoxKey() + " " + num + "次里开出卡牌次数：" + awardCardTimes);
        }
        results.forEach(System.out::println);
    }
}